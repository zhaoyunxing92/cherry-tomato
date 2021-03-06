/**
 * Copyright(C) 2019 Hangzhou zhaoyunxing Technology Co., Ltd. All rights reserved.
 */
package io.github.sunny.cherry.tomato.account;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author zhaoyunxing
 * @date: 2019-10-12 17:56
 */
@SpringBootApplication
@MapperScan("io.github.sunny.cherry.tomato.account.mapper")
public class AccountServer {
    public static void main(String[] args) {
        SpringApplication.run(AccountServer.class, args);
    }
}
