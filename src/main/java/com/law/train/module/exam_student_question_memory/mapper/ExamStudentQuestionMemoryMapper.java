package com.law.train.module.exam_student_question_memory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.law.train.module.exam_student_question_memory.entity.ExamStudentQuestionMemory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.law.train.vo.SelectVO;

import java.util.List;

/**
 * @author wuming
 * @description: 学生题目记忆状态Mapper接口
 */
@Mapper
public interface ExamStudentQuestionMemoryMapper extends BaseMapper<ExamStudentQuestionMemory> {

    /**
     * 分页查询
     */
    List<ExamStudentQuestionMemory> pageQuery(@Param("entity") ExamStudentQuestionMemory entity,
                             @Param("offset") Long offset,
                             @Param("limit") Long limit);

    /**
     * 查询总数
     */
    Long countQuery(@Param("entity") ExamStudentQuestionMemory entity);

    /**
     * 列表查询（不分页）
     */
    List<ExamStudentQuestionMemory> listQuery(@Param("entity") ExamStudentQuestionMemory entity);

    /**
     * 根据ID查询
     */
    ExamStudentQuestionMemory getById(@Param("id") Long id);

    /**
     * 推荐题目列表：next_review_time <= now, order by recommend_priority desc, next_review_time asc
     * @param courseId 可选，指定则只返回该课程的推荐题
     */
    List<ExamStudentQuestionMemory> listRecommend(@Param("studentId") Long studentId,
                                                  @Param("now") java.time.LocalDateTime now,
                                                  @Param("courseId") Long courseId,
                                                  @Param("limit") Integer limit);

    /**
     * 下拉选项查询
     */
    /**
     * 下拉数据：exam_course
     */
    @Select("select course_name as label, id as value from exam_course")
    List<SelectVO> listExamCourse();

    /**
     * 下拉数据：exam_student
     */
    @Select("select real_name as label, id as value from exam_student")
    List<SelectVO> listExamStudent();

    /**
     * 下拉数据：exam_question
     */
    @Select("select question_title as label, id as value from exam_question")
    List<SelectVO> listExamQuestion();

}
