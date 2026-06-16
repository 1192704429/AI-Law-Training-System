package com.law.train.module.exam_question_practice_record.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.law.train.module.exam_question_practice_record.entity.ExamQuestionPracticeRecord;
import com.law.train.module.exam_question_practice_record.mapper.ExamQuestionPracticeRecordMapper;
import com.law.train.module.exam_question_practice_record.service.ExamQuestionPracticeRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.law.train.vo.SelectVO;

import java.util.List;

/**
 * @author wuming
 * @description: 题目作答记录Service实现类
 */
@Service
public class ExamQuestionPracticeRecordServiceImpl extends ServiceImpl<ExamQuestionPracticeRecordMapper, ExamQuestionPracticeRecord> implements ExamQuestionPracticeRecordService {

    @Autowired
    private ExamQuestionPracticeRecordMapper examQuestionPracticeRecordMapper;

    @Override
    public IPage<ExamQuestionPracticeRecord> pageQuery(Integer current, Integer size, ExamQuestionPracticeRecord entity) {
        Long offset = (long) (current - 1) * size;
        Long limit = (long) size;
        List<ExamQuestionPracticeRecord> records = examQuestionPracticeRecordMapper.pageQuery(entity, offset, limit);
        Long total = examQuestionPracticeRecordMapper.countQuery(entity);

        Page<ExamQuestionPracticeRecord> page = new Page<>(current, size);
        page.setRecords(records);
        page.setTotal(total);
        return page;
    }

    @Override
    public List<ExamQuestionPracticeRecord> listQuery(ExamQuestionPracticeRecord entity) {
        return examQuestionPracticeRecordMapper.listQuery(entity);
    }

    @Override
    public ExamQuestionPracticeRecord getById(Long id) {
        return examQuestionPracticeRecordMapper.getById(id);
    }

    /**
     * 下拉数据：exam_course
     */
    @Override
    public List<SelectVO> listExamCourse() {
        return examQuestionPracticeRecordMapper.listExamCourse();
    }

    /**
     * 下拉数据：exam_student
     */
    @Override
    public List<SelectVO> listExamStudent() {
        return examQuestionPracticeRecordMapper.listExamStudent();
    }

    /**
     * 下拉数据：exam_review_plan
     */
    @Override
    public List<SelectVO> listExamReviewPlan() {
        return examQuestionPracticeRecordMapper.listExamReviewPlan();
    }

    /**
     * 下拉数据：exam_paper
     */
    @Override
    public List<SelectVO> listExamPaper() {
        return examQuestionPracticeRecordMapper.listExamPaper();
    }

    /**
     * 下拉数据：exam_question
     */
    @Override
    public List<SelectVO> listExamQuestion() {
        return examQuestionPracticeRecordMapper.listExamQuestion();
    }

}
