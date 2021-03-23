package org.nozomi.jikkenkichi.machikouba.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FileUtils {
    public static List<String> fileSeeking(String baseDir,
                                           Set<String> extensions, List<String> fileNameFilter,
                                           List<String> fileContentFilter, String charsetName) {
        List<String> result = new ArrayList<>();
        findFileList(new File(baseDir), extensions, fileNameFilter, fileContentFilter, charsetName, result);
        return result;
    }

    public static void textReplace(List<String> sourceFiles, String targetDir,
                                   Map<String, String> replaceRule, String sourceCharsetName, String targetCharsetName) {
        for (String sourceFilePath : sourceFiles) {
            File sourceFile = new File(sourceFilePath);
            String text = readFileContent(sourceFile, sourceCharsetName);
            for (Map.Entry<String, String> e : replaceRule.entrySet()) {
                text = text.replace(e.getKey(), e.getValue());
            }
            writeFile(new File(targetDir, sourceFile.getName()), text, targetCharsetName);
        }
    }

    public static void findFileList(File dir,
                                    Set<String> extensions, List<String> fileNameFilter,
                                    List<String> fileContentFilter, String charsetName,
                                    List<String> resultList) {
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        for (String child : dir.list()) {
            File file = new File(dir, child);
            if (file.isDirectory()) {
                findFileList(file, extensions, fileNameFilter, fileContentFilter, charsetName, resultList);
                continue;
            }
            if (extensions != null) {
                String ext;
                int idx = child.lastIndexOf(".");
                if (idx < 0) {
                    continue;
                }
                ext = child.substring(idx + 1);
                if (!extensions.contains(ext)) {
                    continue;
                }
            }
            if (fileNameFilter != null) {
                boolean go = false;
                for (String f : fileNameFilter) {
                    if (child.contains(f)) {
                        go = true;
                        break;
                    }
                }
                if (!go) {
                    continue;
                }
            }
            if (fileContentFilter != null) {
                String ft = readFileContent(file, charsetName);
                boolean go = false;
                for (String f : fileContentFilter) {
                    if (ft.contains(f)) {
                        go = true;
                        break;
                    }
                }
                if (!go) {
                    continue;
                }
            }
            resultList.add(dir + "\\" + file.getName());
        }
    }

    public static String readFileContent(String fileName, String charsetName) {
        return readFileContent(new File(fileName), charsetName);
    }

    public static String readFileContent(File file, String charsetName) {
        if (charsetName == null) {
            charsetName = "UTF-8";
        }

        StringBuilder sb = new StringBuilder();
        try (InputStreamReader read = new InputStreamReader(new FileInputStream(file), charsetName); BufferedReader reader = new BufferedReader(read)) {
            String tempStr;
            while ((tempStr = reader.readLine()) != null) {
                sb.append(tempStr).append("\r\n");
            }
            return sb.toString();
        } catch (IOException e) {
            DebugTool.recordAndThrow(e);
        }
        return sb.toString();
    }

    public static void writeFile(File file, String text, String charsetName) {
        if (charsetName == null) {
            charsetName = "UTF-8";
        }

        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), charsetName)) {
            writer.write(text);
        } catch (IOException e) {
            DebugTool.recordAndThrow(e);
        }
    }

}
