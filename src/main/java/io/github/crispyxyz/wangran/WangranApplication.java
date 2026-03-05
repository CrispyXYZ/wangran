package io.github.crispyxyz.wangran;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@SpringBootApplication
public class WangranApplication {

    public static void main(String[] args) {
        SpringApplication.run(WangranApplication.class, args);
    }

}
