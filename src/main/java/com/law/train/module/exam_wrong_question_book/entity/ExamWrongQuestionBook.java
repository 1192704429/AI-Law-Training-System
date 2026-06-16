package com.law.train.module.exam_wrong_question_book.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author wuming
 * @description: 学生错题本实体类
 */
@Data
@TableName("exam_wrong_question_book")
public class ExamWrongQuestionBook implements Serializable {

    private static final long serialVersionUID=1L;

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 学生 */
    private Long studentId;
    /** 课程 */
    private Long courseId;
    /** 题目 */
    private Long questionId;
    /** 首次做错时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime firstWrongTime;
    /** 最近做错时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastWrongTime;
    /** 做错次数 */
    private Integer wrongCount;
    /** 最近作答记录 */
    private Long lastPracticeRecordId;
    /** 掌握状态 */
    private Integer masterStatus;
    /** 状态 */
    private Integer status;
    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    /** 姓名 */
    @TableField(exist = false)
    private String realName;
    /** 课程名称 */
    @TableField(exist = false)
    private String courseName;
    /** 题目标题 */
    @TableField(exist = false)
    private String questionTitle;
    /** 题目类型 */
    @TableField(exist = false)
    private Integer questionType;
    /** 最近一次做错的答案（来自 last_practice_record_id 对应记录的 student_answer） */
    @TableField(exist = false)
    private String lastWrongAnswer;
    /** 主键ID */
    @TableId(type = IdType.AUTO)
    @TableField(exist = false)
    private Long id2;


}
