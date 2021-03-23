package org.nozomi.jikkenkichi.seiran;

import org.nozomi.jikkenkichi.machikouba.pojo.LocalConfig;
import org.nozomi.jikkenkichi.machikouba.util.ClassUtils;
import org.nozomi.jikkenkichi.machikouba.zk.ZkOnline;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static org.nozomi.jikkenkichi.seiran.SeiranStatusManager.INVOKE_INFO;
import static org.nozomi.jikkenkichi.seiran.SeiranStatusManager.SERVER_LIST;

/**
 * for loading @Seiran
 * can be modified to be a ClassUtils if necessary
 */
@Component
public class SeiranScanner {
    @Autowired
    LocalConfig localConfig;
    @Autowired
    ZkOnline zkOnline;

    public SeiranScanner() throws Exception {
        new Thread(() -> {
            if (localConfig == null || zkOnline == null) {
                try {
                    Thread.sleep(555);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {
                pickSeiran(localConfig.getSeiranClientPath());
            } catch (Exception e) {
                e.printStackTrace();
            }
            for (String key : SERVER_LIST.toArray(new String[SERVER_LIST.size()])) {
                zkOnline.initWatch(key);
            }
        }).start();
    }

    void pickSeiran(String packageName) throws Exception {
        Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(packageName.replace(".", "/"));
        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            if (url == null) {
                continue;
            }
            String protocol = url.getProtocol();
            if ("file".equals(protocol)) {
                String packagePath = url.getPath().replace("%20", "");
                addClass(packagePath, packageName);
                continue;
            }
            if ("jar".equals(protocol)) {
                JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
                if (jarURLConnection == null) {
                    continue;
                }
                JarFile jarFile = jarURLConnection.getJarFile();
                if (jarFile == null) {
                    continue;
                }
                Enumeration<JarEntry> jarEntries = jarFile.entries();
                while (jarEntries.hasMoreElements()) {
                    JarEntry jarEntry = jarEntries.nextElement();
                    String jarEntryName = jarEntry.getName();
                    if (jarEntryName.endsWith(".class")) {
                        String className = jarEntryName.substring(0, jarEntryName.lastIndexOf("."))
                                .replaceAll("/", ".");
                        doAddClass(className);
                    }
                }
            }
        }
    }

    void addClass(String packagePath, String packageName) throws Exception {
        File[] files = new File(packagePath).listFiles((file) ->
                (file.isFile() && file.getName().endsWith(".class")) || file.isDirectory());
        for (File file : files) {
            String fileName = file.getName();
            if (file.isFile()) {
                String className = fileName.substring(0, fileName.lastIndexOf("."));
                if (StringUtils.hasText(packageName)) {
                    className = packageName + "." + className;
                }
                doAddClass(className);
                continue;
            }
            String subPackagePath = fileName;
            if (StringUtils.hasText(packageName)) {
                subPackagePath = packagePath + "/" + subPackagePath;
            }
            String subPackageName = fileName;
            if (StringUtils.hasText(packageName)) {
                subPackageName = packageName + "." + subPackageName;
            }
            addClass(subPackagePath, subPackageName);
        }
    }

    void doAddClass(String className) throws Exception {
        Class<?> cls = Class.forName(className, false, Thread.currentThread().getContextClassLoader());
        Annotation[] classAnnoList = cls.getAnnotations();
        if (classAnnoList == null || classAnnoList.length == 0) {
            return;
        }
        for (Annotation classAnno : classAnnoList) {
            if (!classAnno.annotationType().equals(SeiranClient.class)) {
                continue;
            }
            String serverName = ClassUtils.getAnnotationValue(classAnno, "value").toString();
            String basePath = ClassUtils.getAnnotationValue(classAnno, "basePath").toString();
            SERVER_LIST.add(serverName);
            for (Method method : cls.getMethods()) {
                Annotation[] methodAnnoList = method.getAnnotations();
                if (methodAnnoList == null || methodAnnoList.length == 0) {
                    continue;
                }
                for (Annotation ann : methodAnnoList) {
                    if (!ann.annotationType().equals(SeiranRequest.class)) {
                        continue;
                    }
                    String path = basePath + ClassUtils.getAnnotationValue(ann, "value").toString();
                    INVOKE_INFO.put(method.toGenericString(), new InvokeInfo(serverName, path, method.getReturnType()));
                }
            }

        }
    }

}
