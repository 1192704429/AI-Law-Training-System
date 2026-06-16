package com.law.train.schedule;

import com.law.train.service.ForgettingCurveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 遗忘曲线定时任务：每1分钟更新 overdue_minutes 与 recommend_priority
 */
@Component
public class ForgettingCurveScheduleTask {

    @Autowired
    private ForgettingCurveService forgettingCurveService;

    @Scheduled(cron = "0 * * * * ?")
    public void updateOverdueAndRecommendPriority() {
        forgettingCurveService.updateOverdueAndRecommendPriority();
    }
}
