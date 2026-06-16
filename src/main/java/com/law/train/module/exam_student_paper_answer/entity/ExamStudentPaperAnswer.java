package com.law.train.module.exam_student_paper_answer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * @author wuming
 * @description: 学生试卷答案实体类
 */
@Data
@TableName("exam_student_paper_answer")
public class ExamStudentPaperAnswer implements Serializable {

    private static final long serialVersionUID=1L;

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 学生答卷（exam_student_paper.id） */
    private Long studentPaperId;
    /** 学生 */
    private Long studentId;
    /** 试卷 */
    private Long paperId;
    /** 题目 */
    private Long questionId;
    /** 学生答案 */
    private String studentAnswer;
    /** 是否正确 */
    private Integer isCorrect;
    /** 得分 */
    private BigDecimal score;
    /** 得分率 */
    private BigDecimal scoreRate;
    /** 判题方式 */
    private Integer judgeType;
    /** 判题结果说明 */
    private String judgeResult;
    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    /** 姓名 */
    @TableField(exist = false)
    private String realName;
    /** 试卷名称 */
    @TableField(exist = false)
    private String paperName;
    /** 题目标题 */
    @TableField(exist = false)
    private String questionTitle;
    /** 标准答案（关联题目，详情展示用） */
    @TableField(exist = false)
    private String standardAnswer;


}
