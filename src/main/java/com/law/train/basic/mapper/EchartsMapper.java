package com.law.train.basic.mapper;

import com.law.train.vo.EchartsDataVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EchartsMapper {

    /**
     * 学生性别分布统计
     */
    List<EchartsDataVO> countStudentByGender(@Param("status") Integer status);

    /**
     * 课程难度分布统计
     */
    List<EchartsDataVO> countCourseByDifficulty(@Param("status") Integer status);

    /**
     * 题目类型分布统计
     */
    List<EchartsDataVO> countQuestionByType(@Param("status") Integer status);

    /**
     * 练习类型统计
     */
    List<EchartsDataVO> countPracticeByType(@Param("startTime") String startTime, @Param("endTime") String endTime);

    /**
     * 复习计划状态统计
     */
    List<EchartsDataVO> countReviewPlanByStatus();

    /**
     * 试卷难度分布统计
     */
    List<EchartsDataVO> countPaperByDifficulty(@Param("paperType") Integer paperType, @Param("status") Integer status);

}
