package com.law.train.module.exam_paper.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.law.train.module.exam_paper.entity.ExamPaper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.law.train.vo.SelectVO;

import java.util.List;

/**
 * @author wuming
 * @description: 试卷Mapper接口
 */
@Mapper
public interface ExamPaperMapper extends BaseMapper<ExamPaper> {

    /**
     * 分页查询
     */
    List<ExamPaper> pageQuery(@Param("entity") ExamPaper entity,
                             @Param("offset") Long offset,
                             @Param("limit") Long limit);

    /**
     * 查询总数
     */
    Long countQuery(@Param("entity") ExamPaper entity);

    /**
     * 列表查询（不分页）
     */
    List<ExamPaper> listQuery(@Param("entity") ExamPaper entity);

    /**
     * 根据ID查询
     */
    ExamPaper getById(@Param("id") Long id);

    /**
     * 下拉选项查询
     */
    /**
     * 下拉数据：exam_course
     */
    @Select("select course_name as label, id as value from exam_course")
    List<SelectVO> listExamCourse();

}
