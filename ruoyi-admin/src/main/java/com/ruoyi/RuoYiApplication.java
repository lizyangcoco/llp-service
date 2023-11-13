package com.ruoyi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import java.util.logging.Logger;

/**
 * 启动程序
 *
 * @author ruoyi
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class RuoYiApplication {
    private static final Logger logger = Logger.getLogger(String.valueOf(RuoYiApplication.class));

    public static void main(String[] args) {
        // System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(RuoYiApplication.class, args);
        logger.info("=============================启动成功=============================");
        logger.info("本地文档地址：http://127.0.0.1:10100/doc.html#/");
        // http://doc.llp.at-tianshuo.com/doc.html
        // http://api.llp.at-tianshuo.com/

    }
}
