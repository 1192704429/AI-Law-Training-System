package com.law.train.module.exam_question_practice_record.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.law.train.module.exam_question_practice_record.entity.ExamQuestionPracticeRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.law.train.vo.SelectVO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @author wuming
 * @description: 题目作答记录Mapper接口
 */
@Mapper
public interface ExamQuestionPracticeRecordMapper extends BaseMapper<ExamQuestionPracticeRecord> {

    /**
     * 分页查询
     */
    List<ExamQuestionPracticeRecord> pageQuery(@Param("entity") ExamQuestionPracticeRecord entity,
                             @Param("offset") Long offset,
                             @Param("limit") Long limit);

    /**
     * 查询总数
     */
    Long countQuery(@Param("entity") ExamQuestionPracticeRecord entity);

    /**
     * 列表查询（不分页）
     */
    List<ExamQuestionPracticeRecord> listQuery(@Param("entity") ExamQuestionPracticeRecord entity);

    /**
     * 根据ID查询
     */
    ExamQuestionPracticeRecord getById(@Param("id") Long id);

    /**
     * 下拉选项查询
     */
    /**
     * 下拉数据：exam_course
     */
    @Select("select course_name as label, id as value from exam_course")
    List<SelectVO> listExamCourse();

    /**
     * 下拉数据：exam_student
     */
    @Select("select real_name as label, id as value from exam_student")
    List<SelectVO> listExamStudent();

    /**
     * 下拉数据：exam_review_plan
     */
    @Select("select plan_name as label, id as value from exam_review_plan")
    List<SelectVO> listExamReviewPlan();

    /**
     * 下拉数据：exam_paper
     */
    @Select("select paper_name as label, id as value from exam_paper")
    List<SelectVO> listExamPaper();

    /**
     * 下拉数据：exam_question
     */
    @Select("select question_title as label, id as value from exam_question")
    List<SelectVO> listExamQuestion();

    /**
     * 学生端统计：按日刷题量（练习类型 1、3）
     */
    List<Map<String, Object>> selectStudentDailyPracticeCount(@Param("studentId") Long studentId,
                                                               @Param("startTime") LocalDateTime startTime);

    /**
     * 学生端统计：各课程正确率（按作答记录聚合）
     */
    List<Map<String, Object>> selectStudentCourseAccuracy(@Param("studentId") Long studentId);

}
