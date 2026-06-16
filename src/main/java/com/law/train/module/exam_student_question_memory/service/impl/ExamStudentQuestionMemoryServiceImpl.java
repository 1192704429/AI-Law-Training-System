package com.law.train.module.exam_student_question_memory.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.law.train.module.exam_student_question_memory.entity.ExamStudentQuestionMemory;
import com.law.train.module.exam_student_question_memory.mapper.ExamStudentQuestionMemoryMapper;
import com.law.train.module.exam_student_question_memory.service.ExamStudentQuestionMemoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.law.train.vo.SelectVO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wuming
 * @description: 学生题目记忆状态Service实现类
 */
@Service
public class ExamStudentQuestionMemoryServiceImpl extends ServiceImpl<ExamStudentQuestionMemoryMapper, ExamStudentQuestionMemory> implements ExamStudentQuestionMemoryService {

    @Autowired
    private ExamStudentQuestionMemoryMapper examStudentQuestionMemoryMapper;

    @Override
    public IPage<ExamStudentQuestionMemory> pageQuery(Integer current, Integer size, ExamStudentQuestionMemory entity) {
        Long offset = (long) (current - 1) * size;
        Long limit = (long) size;
        List<ExamStudentQuestionMemory> records = examStudentQuestionMemoryMapper.pageQuery(entity, offset, limit);
        Long total = examStudentQuestionMemoryMapper.countQuery(entity);

        Page<ExamStudentQuestionMemory> page = new Page<>(current, size);
        page.setRecords(records);
        page.setTotal(total);
        return page;
    }

    @Override
    public List<ExamStudentQuestionMemory> listQuery(ExamStudentQuestionMemory entity) {
        return examStudentQuestionMemoryMapper.listQuery(entity);
    }

    @Override
    public ExamStudentQuestionMemory getById(Long id) {
        return examStudentQuestionMemoryMapper.getById(id);
    }

    /**
     * 下拉数据：exam_course
     */
    @Override
    public List<SelectVO> listExamCourse() {
        return examStudentQuestionMemoryMapper.listExamCourse();
    }

    /**
     * 下拉数据：exam_student
     */
    @Override
    public List<SelectVO> listExamStudent() {
        return examStudentQuestionMemoryMapper.listExamStudent();
    }

    /**
     * 下拉数据：exam_question
     */
    @Override
    public List<SelectVO> listExamQuestion() {
        return examStudentQuestionMemoryMapper.listExamQuestion();
    }

    @Override
    public List<ExamStudentQuestionMemory> listRecommend(Long studentId, Long courseId, Integer limit) {
        if (studentId == null || limit == null || limit <= 0) return new java.util.ArrayList<>();
        return examStudentQuestionMemoryMapper.listRecommend(studentId, LocalDateTime.now(), courseId, limit);
    }

}
