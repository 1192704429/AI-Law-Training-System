package com.law.train.module.exam_student_question_memory.entity;

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
 * @description: 学生题目记忆状态实体类
 */
@Data
@TableName("exam_student_question_memory")
public class ExamStudentQuestionMemory implements Serializable {

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
    /** 记忆强度 */
    private BigDecimal memoryStrength;
    /** 遗忘指数 */
    private BigDecimal forgettingIndex;
    /** 掌握等级 */
    private Integer masteryLevel;
    /** 累计正确次数 */
    private Integer correctCount;
    /** 累计错误次数 */
    private Integer wrongCount;
    /** 连续答对次数 */
    private Integer continuousCorrectCount;
    /** 连续答错次数 */
    private Integer continuousWrongCount;
    /** 累计复习次数 */
    private Integer reviewCount;
    /** 当前复习间隔分钟数 */
    private Integer reviewIntervalMinutes;
    /** 最近得分率 */
    private BigDecimal lastScoreRate;
    /** 平均得分率 */
    private BigDecimal avgScoreRate;
    /** 最近作答时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastAnswerTime;
    /** 最近答对时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastCorrectTime;
    /** 下次复习时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime nextReviewTime;
    /** 逾期分钟数 */
    private Integer overdueMinutes;
    /** 推荐优先级 */
    private BigDecimal recommendPriority;
    /** 错题本状态 */
    private Integer wrongBookStatus;
    /** 状态 */
    private Integer status;
    /** 备注 */
    private String remark;
    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    /** 姓名 */
    @TableField(exist = false)
    private String realName;
    /** 课程名称 */
    @TableField(exist = false)
    private String courseName;
    /** 题目标题 */
    @TableField(exist = false)
    private String questionTitle;
    /** 下次复习时间-上限（查询用，<= 该时间） */
    @TableField(exist = false)
    private java.time.LocalDateTime nextReviewTimeEnd;

}
