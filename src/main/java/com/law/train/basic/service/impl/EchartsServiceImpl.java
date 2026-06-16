package com.law.train.basic.service.impl;

import com.law.train.basic.mapper.EchartsMapper;
import com.law.train.basic.service.EchartsService;
import com.law.train.vo.EchartsDataVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EchartsServiceImpl implements EchartsService {

    @Autowired
    private EchartsMapper echartsMapper;

    /**
     * 学生性别分布统计
     */
    @Override
    public List<EchartsDataVO> countStudentByGender(Integer status) {
        return echartsMapper.countStudentByGender(status);
    }

    /**
     * 课程难度分布统计
     */
    @Override
    public List<EchartsDataVO> countCourseByDifficulty(Integer status) {
        return echartsMapper.countCourseByDifficulty(status);
    }

    /**
     * 题目类型分布统计
     */
    @Override
    public List<EchartsDataVO> countQuestionByType(Integer status) {
        return echartsMapper.countQuestionByType(status);
    }

    /**
     * 练习类型统计
     */
    @Override
    public List<EchartsDataVO> countPracticeByType(String startTime, String endTime) {
        return echartsMapper.countPracticeByType(startTime, endTime);
    }

    /**
     * 复习计划状态统计
     */
    @Override
    public List<EchartsDataVO> countReviewPlanByStatus() {
        return echartsMapper.countReviewPlanByStatus();
    }

    /**
     * 试卷难度分布统计
     */
    @Override
    public List<EchartsDataVO> countPaperByDifficulty(Integer paperType, Integer status) {
        return echartsMapper.countPaperByDifficulty(paperType, status);
    }

}
