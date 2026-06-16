package com.law.train.module.exam_paper.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.law.train.module.exam_paper.entity.ExamPaper;
import com.law.train.vo.SelectVO;

import java.util.List;

/**
 * @author wuming
 * @description: 试卷Service接口
 */
public interface ExamPaperService extends IService<ExamPaper> {

    /**
     * 分页查询
     */
    IPage<ExamPaper> pageQuery(Integer current, Integer size, ExamPaper entity);

    /**
     * 列表查询（不分页）
     */
    List<ExamPaper> listQuery(ExamPaper entity);

    /**
     * 根据ID查询
     */
    ExamPaper getById(Long id);

    /**
     * 下拉数据：exam_course
     */
    List<SelectVO> listExamCourse();

    /**
     * 批量添加题目到试卷
     */
    com.law.train.common.Result addQuestions(Long paperId, List<Long> questionIds);

    /**
     * 计算并更新试卷总分和题目数量
     */
    com.law.train.common.Result calculatePaperInfo(Long paperId);

    /**
     * AI组卷
     */
    com.law.train.common.Result aiGeneratePaper(Long paperId);

}
