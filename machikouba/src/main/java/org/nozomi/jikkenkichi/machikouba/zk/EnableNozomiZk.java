package org.nozomi.jikkenkichi.machikouba.zk;

import org.nozomi.jikkenkichi.machikouba.pojo.LocalConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@EnableConfigurationProperties({LocalConfig.class})
@Import({ZkCommon.class, ZkConfig.class, ZkLock.class, ZkOnline.class})
public @interface EnableNozomiZk {
}
