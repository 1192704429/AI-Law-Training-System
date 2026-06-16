package com.law.train.module.exam_paper_question.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.law.train.module.exam_paper_question.entity.ExamPaperQuestion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.law.train.vo.SelectVO;

import java.util.List;

/**
 * @author wuming
 * @description: 试卷题目关联Mapper接口
 */
@Mapper
public interface ExamPaperQuestionMapper extends BaseMapper<ExamPaperQuestion> {

    /**
     * 分页查询
     */
    List<ExamPaperQuestion> pageQuery(@Param("entity") ExamPaperQuestion entity,
                             @Param("offset") Long offset,
                             @Param("limit") Long limit);

    /**
     * 查询总数
     */
    Long countQuery(@Param("entity") ExamPaperQuestion entity);

    /**
     * 列表查询（不分页）
     */
    List<ExamPaperQuestion> listQuery(@Param("entity") ExamPaperQuestion entity);

    /**
     * 根据ID查询
     */
    ExamPaperQuestion getById(@Param("id") Long id);

    /**
     * 下拉选项查询
     */
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

}
