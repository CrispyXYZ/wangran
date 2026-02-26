package io.github.crispyxyz.wangran;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("io.github.crispyxyz.wangran.mapper")
public class WangranApplication {

    public static void main(String[] args) {
        SpringApplication.run(WangranApplication.class, args);
    }

}
