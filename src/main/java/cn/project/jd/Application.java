package cn.project.jd;

import cn.project.jd.task.txtTask;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;

@SpringBootApplication
//使用定时任务，需要先开启定时任务
@EnableScheduling
public class Application {
    public static void main(String[] args) throws IOException {
        SpringApplication.run(Application.class,args);

    }
}
