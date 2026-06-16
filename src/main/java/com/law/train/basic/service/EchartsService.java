package com.law.train.basic.service;

import com.law.train.vo.EchartsDataVO;

import java.util.List;

public interface EchartsService {

    /**
     * 学生性别分布统计
     */
    List<EchartsDataVO> countStudentByGender(Integer status);

    /**
     * 课程难度分布统计
     */
    List<EchartsDataVO> countCourseByDifficulty(Integer status);

    /**
     * 题目类型分布统计
     */
    List<EchartsDataVO> countQuestionByType(Integer status);

    /**
     * 练习类型统计
     */
    List<EchartsDataVO> countPracticeByType(String startTime, String endTime);

    /**
     * 复习计划状态统计
     */
    List<EchartsDataVO> countReviewPlanByStatus();

    /**
     * 试卷难度分布统计
     */
    List<EchartsDataVO> countPaperByDifficulty(Integer paperType, Integer status);

}
