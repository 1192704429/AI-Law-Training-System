package com.law.train.module.exam_review_plan_detail.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.law.train.module.exam_review_plan_detail.entity.ExamReviewPlanDetail;
import com.law.train.module.exam_review_plan_detail.mapper.ExamReviewPlanDetailMapper;
import com.law.train.module.exam_review_plan_detail.service.ExamReviewPlanDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.law.train.vo.SelectVO;

import java.util.List;

/**
 * @author wuming
 * @description: 复习计划明细Service实现类
 */
@Service
public class ExamReviewPlanDetailServiceImpl extends ServiceImpl<ExamReviewPlanDetailMapper, ExamReviewPlanDetail> implements ExamReviewPlanDetailService {

    @Autowired
    private ExamReviewPlanDetailMapper examReviewPlanDetailMapper;

    @Override
    public IPage<ExamReviewPlanDetail> pageQuery(Integer current, Integer size, ExamReviewPlanDetail entity) {
        Long offset = (long) (current - 1) * size;
        Long limit = (long) size;
        List<ExamReviewPlanDetail> records = examReviewPlanDetailMapper.pageQuery(entity, offset, limit);
        Long total = examReviewPlanDetailMapper.countQuery(entity);

        Page<ExamReviewPlanDetail> page = new Page<>(current, size);
        page.setRecords(records);
        page.setTotal(total);
        return page;
    }

    @Override
    public List<ExamReviewPlanDetail> listQuery(ExamReviewPlanDetail entity) {
        return examReviewPlanDetailMapper.listQuery(entity);
    }

    @Override
    public ExamReviewPlanDetail getById(Long id) {
        return examReviewPlanDetailMapper.getById(id);
    }

    /**
     * 下拉数据：exam_course
     */
    @Override
    public List<SelectVO> listExamCourse() {
        return examReviewPlanDetailMapper.listExamCourse();
    }

    /**
     * 下拉数据：exam_question_practice_record
     */
    @Override
    public List<SelectVO> listExamQuestionPracticeRecord() {
        return examReviewPlanDetailMapper.listExamQuestionPracticeRecord();
    }

    /**
     * 下拉数据：exam_review_plan
     */
    @Override
    public List<SelectVO> listExamReviewPlan() {
        return examReviewPlanDetailMapper.listExamReviewPlan();
    }

    /**
     * 下拉数据：exam_student
     */
    @Override
    public List<SelectVO> listExamStudent() {
        return examReviewPlanDetailMapper.listExamStudent();
    }

    /**
     * 下拉数据：exam_question
     */
    @Override
    public List<SelectVO> listExamQuestion() {
        return examReviewPlanDetailMapper.listExamQuestion();
    }

}
