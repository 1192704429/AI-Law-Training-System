package com.law.train.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.law.train.module.exam_question_practice_record.entity.ExamQuestionPracticeRecord;
import com.law.train.module.exam_review_plan_detail.entity.ExamReviewPlanDetail;
import com.law.train.module.exam_review_plan_detail.service.ExamReviewPlanDetailService;
import com.law.train.module.exam_student_question_memory.entity.ExamStudentQuestionMemory;
import com.law.train.module.exam_student_question_memory.service.ExamStudentQuestionMemoryService;
import com.law.train.module.exam_wrong_question_book.entity.ExamWrongQuestionBook;
import com.law.train.module.exam_wrong_question_book.service.ExamWrongQuestionBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * 遗忘曲线算法服务（分钟级演示）
 * 规则：首次作答初始化、已有记忆更新、主观题部分掌握、错题本联动、计划明细完成标记
 */
@Service
public class ForgettingCurveService {

    @Autowired
    private ExamStudentQuestionMemoryService memoryService;
    @Autowired
    private ExamWrongQuestionBookService wrongBookService;
    @Autowired
    private ExamReviewPlanDetailService planDetailService;

    private static final BigDecimal BD_02 = BigDecimal.valueOf(0.20);
    private static final BigDecimal BD_05 = BigDecimal.valueOf(0.50);
    private static final BigDecimal BD_08 = BigDecimal.valueOf(0.80);
    private static final BigDecimal BD_10 = BigDecimal.valueOf(0.10);
    private static final BigDecimal BD_15 = BigDecimal.valueOf(0.15);
    private static final BigDecimal BD_03 = BigDecimal.valueOf(0.03);
    private static final BigDecimal BD_85 = BigDecimal.valueOf(0.85);
    private static final BigDecimal BD_60 = BigDecimal.valueOf(0.60);
    private static final BigDecimal ONE = BigDecimal.ONE;

    /**
     * 每次插入 practice_record 后调用：更新记忆表、错题本、计划明细
     */
    @Transactional(rollbackFor = Exception.class)
    public void afterPracticeRecordSaved(ExamQuestionPracticeRecord record) {
        if (record == null || record.getStudentId() == null || record.getQuestionId() == null) return;
        Long studentId = record.getStudentId();
        Long courseId = record.getCourseId();
        Long questionId = record.getQuestionId();
        LocalDateTime answerTime = record.getAnswerTime() != null ? record.getAnswerTime() : LocalDateTime.now();
        BigDecimal scoreRate = record.getScoreRate() != null ? record.getScoreRate() : BigDecimal.ZERO;
        boolean isCorrect = record.getIsCorrect() != null && record.getIsCorrect() == 1;

        // 主观题：按 score_rate 判定
        Boolean effectiveCorrect = null; // true=答对, false=答错, 需要部分掌握时单独处理
        if (record.getJudgeType() != null && record.getJudgeType() == 2) {
            if (scoreRate.compareTo(BD_85) >= 0) effectiveCorrect = true;
            else if (scoreRate.compareTo(BD_60) >= 0) effectiveCorrect = null; // 部分掌握
            else effectiveCorrect = false;
        } else {
            effectiveCorrect = isCorrect;
        }

        ExamStudentQuestionMemory memory = findByStudentAndQuestion(studentId, questionId);

        if (memory == null) {
            // 首次作答
            if (effectiveCorrect == null) {
                // 部分掌握（仅主观题 60~85）
                initFirstPartial(studentId, courseId, questionId, answerTime, scoreRate);
            } else if (effectiveCorrect) {
                initFirstCorrect(studentId, courseId, questionId, answerTime, scoreRate);
            } else {
                initFirstWrong(studentId, courseId, questionId, answerTime, scoreRate);
            }
        } else {
            // 已有记忆
            if (effectiveCorrect == null) {
                updatePartialMastery(memory, answerTime, scoreRate);
            } else if (effectiveCorrect) {
                updateCorrect(memory, answerTime, scoreRate);
            } else {
                updateWrong(memory, answerTime, scoreRate);
            }
        }

        // 错题本联动
        syncWrongBook(studentId, courseId, questionId, answerTime, record.getId(), effectiveCorrect, memory);

        // 计划明细：来源于计划则标记完成
        if (record.getPlanId() != null) {
            updatePlanDetailComplete(record.getPlanId(), studentId, courseId, answerTime, record.getId());
        }
    }

    private ExamStudentQuestionMemory findByStudentAndQuestion(Long studentId, Long questionId) {
        ExamStudentQuestionMemory q = new ExamStudentQuestionMemory();
        q.setStudentId(studentId);
        q.setQuestionId(questionId);
        List<ExamStudentQuestionMemory> list = memoryService.listQuery(q);
        return list.isEmpty() ? null : list.get(0);
    }

    private int calcMasteryLevel(BigDecimal memoryStrength) {
        if (memoryStrength == null) return 1;
        if (memoryStrength.compareTo(BigDecimal.valueOf(0.80)) >= 0) return 4;
        if (memoryStrength.compareTo(BigDecimal.valueOf(0.50)) >= 0) return 3;
        if (memoryStrength.compareTo(BigDecimal.valueOf(0.30)) >= 0) return 2;
        return 1;
    }

    /** 首次答错 */
    private void initFirstWrong(Long studentId, Long courseId, Long questionId, LocalDateTime answerTime, BigDecimal scoreRate) {
        ExamStudentQuestionMemory m = new ExamStudentQuestionMemory();
        m.setStudentId(studentId);
        m.setCourseId(courseId);
        m.setQuestionId(questionId);
        m.setMemoryStrength(BD_02);
        m.setForgettingIndex(BD_08);
        m.setMasteryLevel(1);
        m.setCorrectCount(0);
        m.setWrongCount(1);
        m.setContinuousCorrectCount(0);
        m.setContinuousWrongCount(1);
        m.setReviewCount(1);
        m.setReviewIntervalMinutes(2);
        m.setLastScoreRate(scoreRate);
        m.setAvgScoreRate(scoreRate);
        m.setLastAnswerTime(answerTime);
        m.setLastCorrectTime(null);
        m.setNextReviewTime(answerTime.plusMinutes(2));
        m.setOverdueMinutes(0);
        m.setStatus(1);
        m.setCreateTime(LocalDateTime.now());
        m.setUpdateTime(LocalDateTime.now());
        memoryService.save(m);
    }

    /** 首次答对 */
    private void initFirstCorrect(Long studentId, Long courseId, Long questionId, LocalDateTime answerTime, BigDecimal scoreRate) {
        ExamStudentQuestionMemory m = new ExamStudentQuestionMemory();
        m.setStudentId(studentId);
        m.setCourseId(courseId);
        m.setQuestionId(questionId);
        m.setMemoryStrength(BD_05);
        m.setForgettingIndex(BD_05);
        m.setMasteryLevel(3);
        m.setCorrectCount(1);
        m.setWrongCount(0);
        m.setContinuousCorrectCount(1);
        m.setContinuousWrongCount(0);
        m.setReviewCount(1);
        m.setReviewIntervalMinutes(4);
        m.setLastScoreRate(scoreRate);
        m.setAvgScoreRate(scoreRate);
        m.setLastAnswerTime(answerTime);
        m.setLastCorrectTime(answerTime);
        m.setNextReviewTime(answerTime.plusMinutes(4));
        m.setOverdueMinutes(0);
        m.setStatus(1);
        m.setCreateTime(LocalDateTime.now());
        m.setUpdateTime(LocalDateTime.now());
        memoryService.save(m);
    }

    /** 首次部分掌握（主观题 60~85）按一次“弱对”处理，给一个中间状态 */
    private void initFirstPartial(Long studentId, Long courseId, Long questionId, LocalDateTime answerTime, BigDecimal scoreRate) {
        ExamStudentQuestionMemory m = new ExamStudentQuestionMemory();
        m.setStudentId(studentId);
        m.setCourseId(courseId);
        m.setQuestionId(questionId);
        m.setMemoryStrength(BigDecimal.valueOf(0.35));
        m.setForgettingIndex(BigDecimal.valueOf(0.65));
        m.setMasteryLevel(2);
        m.setCorrectCount(0);
        m.setWrongCount(0);
        m.setContinuousCorrectCount(0);
        m.setContinuousWrongCount(0);
        m.setReviewCount(1);
        m.setReviewIntervalMinutes(3);
        m.setLastScoreRate(scoreRate);
        m.setAvgScoreRate(scoreRate);
        m.setLastAnswerTime(answerTime);
        m.setLastCorrectTime(null);
        m.setNextReviewTime(answerTime.plusMinutes(3));
        m.setOverdueMinutes(0);
        m.setStatus(1);
        m.setCreateTime(LocalDateTime.now());
        m.setUpdateTime(LocalDateTime.now());
        memoryService.save(m);
    }

    /** 已有记忆-本次答对 */
    private void updateCorrect(ExamStudentQuestionMemory old, LocalDateTime answerTime, BigDecimal scoreRate) {
        int newReviewCount = (old.getReviewCount() == null ? 0 : old.getReviewCount()) + 1;
        int newContinuousCorrect = (old.getContinuousCorrectCount() == null ? 0 : old.getContinuousCorrectCount()) + 1;
        BigDecimal oldAvg = old.getAvgScoreRate() != null ? old.getAvgScoreRate() : BigDecimal.ZERO;
        int oldReview = old.getReviewCount() == null ? 0 : old.getReviewCount();
        BigDecimal newAvg = oldAvg.multiply(BigDecimal.valueOf(oldReview)).add(scoreRate).divide(BigDecimal.valueOf(newReviewCount), 4, RoundingMode.HALF_UP);
        BigDecimal oldStrength = old.getMemoryStrength() != null ? old.getMemoryStrength() : BD_10;
        BigDecimal newStrength = oldStrength.add(BD_15).add(BigDecimal.valueOf(newContinuousCorrect).multiply(BD_03));
        if (newStrength.compareTo(ONE) > 0) newStrength = ONE;
        BigDecimal newForgetting = ONE.subtract(newStrength);
        int intervalMinutes = 4;
        if (newContinuousCorrect >= 5) intervalMinutes = 60;
        else if (newContinuousCorrect == 4) intervalMinutes = 30;
        else if (newContinuousCorrect == 3) intervalMinutes = 15;
        else if (newContinuousCorrect == 2) intervalMinutes = 8;
        else intervalMinutes = 4;

        old.setCorrectCount((old.getCorrectCount() == null ? 0 : old.getCorrectCount()) + 1);
        old.setContinuousCorrectCount(newContinuousCorrect);
        old.setContinuousWrongCount(0);
        old.setReviewCount(newReviewCount);
        old.setLastAnswerTime(answerTime);
        old.setLastCorrectTime(answerTime);
        old.setLastScoreRate(scoreRate);
        old.setAvgScoreRate(newAvg);
        old.setMemoryStrength(newStrength);
        old.setForgettingIndex(newForgetting);
        old.setReviewIntervalMinutes(intervalMinutes);
        old.setNextReviewTime(answerTime.plusMinutes(intervalMinutes));
        old.setMasteryLevel(calcMasteryLevel(newStrength));
        old.setUpdateTime(LocalDateTime.now());
        memoryService.updateById(old);
    }

    /** 已有记忆-本次答错 */
    private void updateWrong(ExamStudentQuestionMemory old, LocalDateTime answerTime, BigDecimal scoreRate) {
        int newReviewCount = (old.getReviewCount() == null ? 0 : old.getReviewCount()) + 1;
        BigDecimal oldAvg = old.getAvgScoreRate() != null ? old.getAvgScoreRate() : BigDecimal.ZERO;
        int oldReview = old.getReviewCount() == null ? 0 : old.getReviewCount();
        BigDecimal newAvg = oldAvg.multiply(BigDecimal.valueOf(oldReview)).add(scoreRate).divide(BigDecimal.valueOf(newReviewCount), 4, RoundingMode.HALF_UP);
        BigDecimal oldStrength = old.getMemoryStrength() != null ? old.getMemoryStrength() : BD_10;
        BigDecimal newStrength = oldStrength.subtract(BD_02);
        if (newStrength.compareTo(BD_10) < 0) newStrength = BD_10;
        BigDecimal newForgetting = ONE.subtract(newStrength).add(BigDecimal.valueOf(0.10));
        if (newForgetting.compareTo(ONE) > 0) newForgetting = ONE;

        old.setWrongCount((old.getWrongCount() == null ? 0 : old.getWrongCount()) + 1);
        old.setContinuousWrongCount((old.getContinuousWrongCount() == null ? 0 : old.getContinuousWrongCount()) + 1);
        old.setContinuousCorrectCount(0);
        old.setReviewCount(newReviewCount);
        old.setLastAnswerTime(answerTime);
        old.setLastScoreRate(scoreRate);
        old.setAvgScoreRate(newAvg);
        old.setMemoryStrength(newStrength);
        old.setForgettingIndex(newForgetting);
        old.setReviewIntervalMinutes(2);
        old.setNextReviewTime(answerTime.plusMinutes(2));
        old.setMasteryLevel(calcMasteryLevel(newStrength));
        old.setUpdateTime(LocalDateTime.now());
        memoryService.updateById(old);
    }

    /** 已有记忆-本次部分掌握（主观题 60~85） */
    private void updatePartialMastery(ExamStudentQuestionMemory old, LocalDateTime answerTime, BigDecimal scoreRate) {
        BigDecimal oldStrength = old.getMemoryStrength() != null ? old.getMemoryStrength() : BD_10;
        BigDecimal newStrength = oldStrength.add(BigDecimal.valueOf(0.05));
        if (newStrength.compareTo(ONE) > 0) newStrength = ONE;
        BigDecimal newForgetting = ONE.subtract(newStrength);
        int newReviewCount = (old.getReviewCount() == null ? 0 : old.getReviewCount()) + 1;
        BigDecimal oldAvg = old.getAvgScoreRate() != null ? old.getAvgScoreRate() : BigDecimal.ZERO;
        int oldReview = old.getReviewCount() == null ? 0 : old.getReviewCount();
        BigDecimal newAvg = oldAvg.multiply(BigDecimal.valueOf(oldReview)).add(scoreRate).divide(BigDecimal.valueOf(newReviewCount), 4, RoundingMode.HALF_UP);

        old.setReviewCount(newReviewCount);
        old.setLastAnswerTime(answerTime);
        old.setLastScoreRate(scoreRate);
        old.setAvgScoreRate(newAvg);
        old.setMemoryStrength(newStrength);
        old.setForgettingIndex(newForgetting);
        old.setReviewIntervalMinutes(3);
        old.setNextReviewTime(answerTime.plusMinutes(3));
        old.setMasteryLevel(calcMasteryLevel(newStrength));
        old.setUpdateTime(LocalDateTime.now());
        memoryService.updateById(old);
    }

    /** 错题本：答错插入/更新；答对且 memory_strength>=0.80 或 continuous_correct_count>=2 则 master_status=2 已掌握 */
    private void syncWrongBook(Long studentId, Long courseId, Long questionId, LocalDateTime answerTime, Long practiceRecordId, Boolean effectiveCorrect, ExamStudentQuestionMemory memoryAfter) {
        LambdaQueryWrapper<ExamWrongQuestionBook> q = new LambdaQueryWrapper<>();
        q.eq(ExamWrongQuestionBook::getStudentId, studentId).eq(ExamWrongQuestionBook::getQuestionId, questionId);
        ExamWrongQuestionBook book = wrongBookService.getOne(q);

        if (Boolean.FALSE.equals(effectiveCorrect)) {
            if (book == null) {
                book = new ExamWrongQuestionBook();
                book.setStudentId(studentId);
                book.setCourseId(courseId);
                book.setQuestionId(questionId);
                book.setFirstWrongTime(answerTime);
                book.setLastWrongTime(answerTime);
                book.setWrongCount(1);
                book.setLastPracticeRecordId(practiceRecordId);
                book.setStatus(1);
                book.setCreateTime(LocalDateTime.now());
                wrongBookService.save(book);
            } else {
                book.setLastWrongTime(answerTime);
                book.setWrongCount((book.getWrongCount() == null ? 0 : book.getWrongCount()) + 1);
                book.setLastPracticeRecordId(practiceRecordId);
                book.setStatus(1);
                wrongBookService.updateById(book);
            }
        } else if (Boolean.TRUE.equals(effectiveCorrect) && memoryAfter != null) {
            if (book != null) {
                BigDecimal strength = memoryAfter.getMemoryStrength();
                int continuous = memoryAfter.getContinuousCorrectCount() == null ? 0 : memoryAfter.getContinuousCorrectCount();
                if (strength != null && (strength.compareTo(BD_08) >= 0 || continuous >= 2)) {
                    book.setMasterStatus(2); // 已掌握
                    wrongBookService.updateById(book);
                }
            }
        }
    }

    /** 计划明细：本次练习来源于计划则更新对应明细完成状态（同一天同课程的那条） */
    private void updatePlanDetailComplete(Long planId, Long studentId, Long courseId, LocalDateTime answerTime, Long practiceRecordId) {
        ExamReviewPlanDetail q = new ExamReviewPlanDetail();
        q.setPlanId(planId);
        q.setStudentId(studentId);
        q.setCourseId(courseId);
        q.setCompleteStatus(0);
        List<ExamReviewPlanDetail> list = planDetailService.listQuery(q);
        for (ExamReviewPlanDetail d : list) {
            if (d.getPlanDate() != null && d.getPlanDate().toLocalDate().equals(answerTime.toLocalDate())) {
                d.setCompleteStatus(1);
                d.setPracticeRecordId(practiceRecordId);
                d.setCompleteTime(LocalDateTime.now());
                planDetailService.updateById(d);
                break;
            }
        }
    }

    public List<ExamStudentQuestionMemory> listRecommend(Long studentId, Long courseId, int limit) {
        return memoryService.listRecommend(studentId, courseId, limit);
    }

    /**
     * 定时任务调用：更新 overdue_minutes 与 recommend_priority
     * recommend_priority = forgetting_index*0.5 + min(overdue/10,1)*0.2 + 错误率*0.2 + 掌握薄弱系数*0.1
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateOverdueAndRecommendPriority() {
        ExamStudentQuestionMemory q = new ExamStudentQuestionMemory();
        q.setStatus(1);
        List<ExamStudentQuestionMemory> list = memoryService.listQuery(q);
        LocalDateTime now = LocalDateTime.now();
        for (ExamStudentQuestionMemory m : list) {
            LocalDateTime next = m.getNextReviewTime();
            int overdue = (next != null && now.isAfter(next))
                    ? (int) ChronoUnit.MINUTES.between(next, now) : 0;
            int reviewCount = m.getReviewCount() == null ? 0 : m.getReviewCount();
            double wrongRate = reviewCount > 0 && m.getWrongCount() != null
                    ? m.getWrongCount().doubleValue() / reviewCount : 0.0;
            double strength = m.getMemoryStrength() != null ? m.getMemoryStrength().doubleValue() : 0.0;
            double weakCoef = 1.0 - strength;
            double forget = m.getForgettingIndex() != null ? m.getForgettingIndex().doubleValue() : 0.5;
            double overduePart = Math.min(overdue / 10.0, 1.0);
            double priority = forget * 0.5 + overduePart * 0.2 + wrongRate * 0.2 + weakCoef * 0.1;
            m.setOverdueMinutes(overdue);
            m.setRecommendPriority(BigDecimal.valueOf(priority).setScale(4, RoundingMode.HALF_UP));
            m.setUpdateTime(now);
            memoryService.updateById(m);
        }
    }
}
