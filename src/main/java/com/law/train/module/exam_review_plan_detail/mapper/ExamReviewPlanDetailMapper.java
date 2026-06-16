package com.law.train.module.exam_review_plan_detail.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.law.train.module.exam_review_plan_detail.entity.ExamReviewPlanDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.law.train.vo.SelectVO;

import java.util.List;

/**
 * @author wuming
 * @description: 复习计划明细Mapper接口
 */
@Mapper
public interface ExamReviewPlanDetailMapper extends BaseMapper<ExamReviewPlanDetail> {

    /**
     * 分页查询
     */
    List<ExamReviewPlanDetail> pageQuery(@Param("entity") ExamReviewPlanDetail entity,
                             @Param("offset") Long offset,
                             @Param("limit") Long limit);

    /**
     * 查询总数
     */
    Long countQuery(@Param("entity") ExamReviewPlanDetail entity);

    /**
     * 列表查询（不分页）
     */
    List<ExamReviewPlanDetail> listQuery(@Param("entity") ExamReviewPlanDetail entity);

    /**
     * 根据ID查询
     */
    ExamReviewPlanDetail getById(@Param("id") Long id);

    /**
     * 下拉选项查询
     */
    /**
     * 下拉数据：exam_course
     */
    @Select("select course_name as label, id as value from exam_course")
    List<SelectVO> listExamCourse();

    /**
     * 下拉数据：exam_question_practice_record
     */
    @Select("select id as label, id as value from exam_question_practice_record")
    List<SelectVO> listExamQuestionPracticeRecord();

    /**
     * 下拉数据：exam_review_plan
     */
    @Select("select plan_name as label, id as value from exam_review_plan")
    List<SelectVO> listExamReviewPlan();

    /**
     * 下拉数据：exam_student
     */
    @Select("select real_name as label, id as value from exam_student")
    List<SelectVO> listExamStudent();

    /**
     * 下拉数据：exam_question
     */
    @Select("select question_title as label, id as value from exam_question")
    List<SelectVO> listExamQuestion();

}
