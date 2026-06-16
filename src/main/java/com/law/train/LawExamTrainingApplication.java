package com.law.train;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author wuming
 * @description: 基于遗忘曲线理论的智能化法考训练系统服务端启动入口
 */
@SpringBootApplication
@Slf4j
@MapperScan("com.law.train.**.mapper")
@EnableScheduling
public class LawExamTrainingApplication {

    public static void main(String[] args) {
        SpringApplication.run(LawExamTrainingApplication.class, args);
        log.info("JAVA后端服务启动成功！！！！！！");
    }

}
