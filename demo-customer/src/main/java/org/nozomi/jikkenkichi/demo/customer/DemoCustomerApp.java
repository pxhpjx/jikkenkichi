package org.nozomi.jikkenkichi.demo.customer;

import org.nozomi.jikkenkichi.machikouba.aop.AspectFunc;
import org.nozomi.jikkenkichi.machikouba.fliter.EnableNozomiFliter;
import org.nozomi.jikkenkichi.machikouba.zk.EnableNozomiZk;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableNozomiZk
@EnableNozomiFliter
@EnableScheduling
@Import({AspectFunc.class})
@SpringBootApplication
public class DemoCustomerApp {
    public static void main(String[] args) {
        SpringApplication.run(DemoCustomerApp.class, args);
    }


}
