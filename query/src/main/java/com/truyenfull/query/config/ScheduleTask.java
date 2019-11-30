package com.truyenfull.query.config;

import com.truyenfull.query.service.QueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class ScheduleTask {

    @Autowired
    QueryService queryService;

    //Dat lich 2h sang moi ngay them top 50 hot comic vao redis
    @Scheduled(cron = "0 0 2 * * ?")
    public void scheduleAddTopHotComic(){
        queryService.addTopHotComic(50);
    }

}
