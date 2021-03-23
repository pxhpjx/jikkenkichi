package org.nozomi.jikkenkichi.machikouba;

import org.nozomi.jikkenkichi.machikouba.pojo.LocalConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@ServletComponentScan
@EnableConfigurationProperties({LocalConfig.class})
@SpringBootApplication
public class MachikoubaApplication {
    public static void main(String[] args) {
        SpringApplication.run(MachikoubaApplication.class, args);
    }
}
