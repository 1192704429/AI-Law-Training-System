package com.law.train.module.exam_review_plan.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.law.train.module.exam_review_plan.entity.ExamReviewPlan;
import com.law.train.vo.SelectVO;

import java.util.List;

/**
 * @author wuming
 * @description: 学生复习计划Service接口
 */
public interface ExamReviewPlanService extends IService<ExamReviewPlan> {

    /**
     * 分页查询
     */
    IPage<ExamReviewPlan> pageQuery(Integer current, Integer size, ExamReviewPlan entity);

    /**
     * 列表查询（不分页）
     */
    List<ExamReviewPlan> listQuery(ExamReviewPlan entity);

    /**
     * 根据ID查询
     */
    ExamReviewPlan getById(Long id);

    /**
     * 下拉数据：exam_student
     */
    List<SelectVO> listExamStudent();

}
