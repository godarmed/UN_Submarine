package com.eseasky.submarine.microservice.defaultdemo.controller;


import com.eseasky.submarine.microservice.defaultdemo.util.SpringUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@Log4j2
@RestController
public class TestController {

    /*@Autowired
    TestController testController;*/

    @RequestMapping("/asyncTest")
    public void contextLoads() {
        TestController testController = SpringUtils.getBean(TestController.class);
        //开始任务
        log.info("开始任务");
        testController.asyncTask();
        //结束任务
        log.info("结束任务");
    }

    @Async
    public void asyncTask() {
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("执行任务");
    }

}
