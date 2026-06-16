package com.law.train.aspect;

import com.law.train.module.exam_question_practice_record.entity.ExamQuestionPracticeRecord;
import com.law.train.service.ForgettingCurveService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 所有「做题」产生的练习记录在 save 成功后统一触发遗忘曲线、错题本、计划明细。
 * 覆盖：考试提交、练习提交、错题重刷、后台新增等任何调用 practiceRecordService.save(record) 的入口。
 */
@Aspect
@Component
public class ForgettingCurvePracticeRecordAspect {

    private static final ThreadLocal<Boolean> SKIP_ASPECT = ThreadLocal.withInitial(() -> Boolean.FALSE);

    @Autowired
    private ForgettingCurveService forgettingCurveService;

    /** 练习提交接口内已显式调用遗忘曲线，通过此方法跳过切面避免重复执行 */
    public static void setSkipAspect(boolean skip) {
        SKIP_ASPECT.set(skip);
    }

    @AfterReturning(
            pointcut = "execution(boolean com.law.train.module.exam_question_practice_record.service.ExamQuestionPracticeRecordService.save(com.law.train.module.exam_question_practice_record.entity.ExamQuestionPracticeRecord)) && args(record)",
            returning = "result"
    )
    public void afterPracticeRecordSaved(JoinPoint joinPoint, ExamQuestionPracticeRecord record, boolean result) {
        if (Boolean.TRUE.equals(SKIP_ASPECT.get())) return;
        if (!result || record == null) return;
        forgettingCurveService.afterPracticeRecordSaved(record);
    }
}
