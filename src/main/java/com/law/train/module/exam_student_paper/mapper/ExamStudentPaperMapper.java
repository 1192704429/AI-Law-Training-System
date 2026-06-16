package com.law.train.module.exam_student_paper.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.law.train.module.exam_student_paper.entity.ExamStudentPaper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.law.train.vo.SelectVO;

import java.util.List;

/**
 * @author wuming
 * @description: 学生试卷作答Mapper接口
 */
@Mapper
public interface ExamStudentPaperMapper extends BaseMapper<ExamStudentPaper> {

    /**
     * 分页查询
     */
    List<ExamStudentPaper> pageQuery(@Param("entity") ExamStudentPaper entity,
                             @Param("offset") Long offset,
                             @Param("limit") Long limit);

    /**
     * 查询总数
     */
    Long countQuery(@Param("entity") ExamStudentPaper entity);

    /**
     * 列表查询（不分页）
     */
    List<ExamStudentPaper> listQuery(@Param("entity") ExamStudentPaper entity);

    /**
     * 根据ID查询
     */
    ExamStudentPaper getById(@Param("id") Long id);

    /**
     * 下拉选项查询
     */
    /**
     * 下拉数据：exam_student
     */
    @Select("select real_name as label, id as value from exam_student")
    List<SelectVO> listExamStudent();

    /**
     * 下拉数据：exam_paper
     */
    @Select("select paper_name as label, id as value from exam_paper")
    List<SelectVO> listExamPaper();

}
