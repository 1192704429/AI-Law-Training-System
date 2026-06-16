package com.law.train.module.exam_review_plan.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.law.train.module.exam_review_plan.entity.ExamReviewPlan;
import com.law.train.module.exam_review_plan.mapper.ExamReviewPlanMapper;
import com.law.train.module.exam_review_plan.service.ExamReviewPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.law.train.vo.SelectVO;

import java.util.List;

/**
 * @author wuming
 * @description: 学生复习计划Service实现类
 */
@Service
public class ExamReviewPlanServiceImpl extends ServiceImpl<ExamReviewPlanMapper, ExamReviewPlan> implements ExamReviewPlanService {

    @Autowired
    private ExamReviewPlanMapper examReviewPlanMapper;

    @Override
    public IPage<ExamReviewPlan> pageQuery(Integer current, Integer size, ExamReviewPlan entity) {
        Long offset = (long) (current - 1) * size;
        Long limit = (long) size;
        List<ExamReviewPlan> records = examReviewPlanMapper.pageQuery(entity, offset, limit);
        Long total = examReviewPlanMapper.countQuery(entity);

        Page<ExamReviewPlan> page = new Page<>(current, size);
        page.setRecords(records);
        page.setTotal(total);
        return page;
    }

    @Override
    public List<ExamReviewPlan> listQuery(ExamReviewPlan entity) {
        return examReviewPlanMapper.listQuery(entity);
    }

    @Override
    public ExamReviewPlan getById(Long id) {
        return examReviewPlanMapper.getById(id);
    }

    /**
     * 下拉数据：exam_student
     */
    @Override
    public List<SelectVO> listExamStudent() {
        return examReviewPlanMapper.listExamStudent();
    }

}
