package com.law.train.module.exam_student_paper.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.law.train.module.exam_student_paper.entity.ExamStudentPaper;
import com.law.train.module.exam_student_paper.mapper.ExamStudentPaperMapper;
import com.law.train.module.exam_student_paper.service.ExamStudentPaperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.law.train.vo.SelectVO;

import java.util.List;

/**
 * @author wuming
 * @description: 学生试卷作答Service实现类
 */
@Service
public class ExamStudentPaperServiceImpl extends ServiceImpl<ExamStudentPaperMapper, ExamStudentPaper> implements ExamStudentPaperService {

    @Autowired
    private ExamStudentPaperMapper examStudentPaperMapper;

    @Override
    public IPage<ExamStudentPaper> pageQuery(Integer current, Integer size, ExamStudentPaper entity) {
        Long offset = (long) (current - 1) * size;
        Long limit = (long) size;
        List<ExamStudentPaper> records = examStudentPaperMapper.pageQuery(entity, offset, limit);
        Long total = examStudentPaperMapper.countQuery(entity);

        Page<ExamStudentPaper> page = new Page<>(current, size);
        page.setRecords(records);
        page.setTotal(total);
        return page;
    }

    @Override
    public List<ExamStudentPaper> listQuery(ExamStudentPaper entity) {
        return examStudentPaperMapper.listQuery(entity);
    }

    @Override
    public ExamStudentPaper getById(Long id) {
        return examStudentPaperMapper.getById(id);
    }

    /**
     * 下拉数据：exam_student
     */
    @Override
    public List<SelectVO> listExamStudent() {
        return examStudentPaperMapper.listExamStudent();
    }

    /**
     * 下拉数据：exam_paper
     */
    @Override
    public List<SelectVO> listExamPaper() {
        return examStudentPaperMapper.listExamPaper();
    }

}
