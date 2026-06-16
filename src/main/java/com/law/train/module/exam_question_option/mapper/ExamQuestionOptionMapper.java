package com.law.train.module.exam_question_option.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.law.train.module.exam_question_option.entity.ExamQuestionOption;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.law.train.vo.SelectVO;

import java.util.List;

/**
 * @author wuming
 * @description: 题目选项Mapper接口
 */
@Mapper
public interface ExamQuestionOptionMapper extends BaseMapper<ExamQuestionOption> {

    /**
     * 分页查询
     */
    List<ExamQuestionOption> pageQuery(@Param("entity") ExamQuestionOption entity,
                             @Param("offset") Long offset,
                             @Param("limit") Long limit);

    /**
     * 查询总数
     */
    Long countQuery(@Param("entity") ExamQuestionOption entity);

    /**
     * 列表查询（不分页）
     */
    List<ExamQuestionOption> listQuery(@Param("entity") ExamQuestionOption entity);

    /**
     * 根据ID查询
     */
    ExamQuestionOption getById(@Param("id") Long id);

    /**
     * 下拉选项查询
     */
    /**
     * 下拉数据：exam_question
     */
    @Select("select question_title as label, id as value from exam_question")
    List<SelectVO> listExamQuestion();

}
