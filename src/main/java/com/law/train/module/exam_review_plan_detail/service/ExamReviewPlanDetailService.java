package com.law.train.module.exam_review_plan_detail.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.law.train.module.exam_review_plan_detail.entity.ExamReviewPlanDetail;
import com.law.train.vo.SelectVO;

import java.util.List;

/**
 * @author wuming
 * @description: 复习计划明细Service接口
 */
public interface ExamReviewPlanDetailService extends IService<ExamReviewPlanDetail> {

    /**
     * 分页查询
     */
    IPage<ExamReviewPlanDetail> pageQuery(Integer current, Integer size, ExamReviewPlanDetail entity);

    /**
     * 列表查询（不分页）
     */
    List<ExamReviewPlanDetail> listQuery(ExamReviewPlanDetail entity);

    /**
     * 根据ID查询
     */
    ExamReviewPlanDetail getById(Long id);

    /**
     * 下拉数据：exam_course
     */
    List<SelectVO> listExamCourse();

    /**
     * 下拉数据：exam_question_practice_record
     */
    List<SelectVO> listExamQuestionPracticeRecord();

    /**
     * 下拉数据：exam_review_plan
     */
    List<SelectVO> listExamReviewPlan();

    /**
     * 下拉数据：exam_student
     */
    List<SelectVO> listExamStudent();

    /**
     * 下拉数据：exam_question
     */
    List<SelectVO> listExamQuestion();

}
