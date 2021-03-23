package org.nozomi.jikkenkichi.demo.provider;

import org.nozomi.jikkenkichi.machikouba.fliter.EnableNozomiFliter;
import org.nozomi.jikkenkichi.machikouba.zk.EnableNozomiZk;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableNozomiZk
@EnableNozomiFliter
@SpringBootApplication
public class DemoProviderApp {
    public static void main(String[] args) {
        SpringApplication.run(DemoProviderApp.class, args);
    }

}
