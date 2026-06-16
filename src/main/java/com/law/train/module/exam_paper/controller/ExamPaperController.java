package com.law.train.module.exam_paper.controller;

import com.law.train.common.Result;
import com.law.train.module.exam_paper.entity.ExamPaper;
import com.law.train.module.exam_paper.service.ExamPaperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import com.law.train.vo.SelectVO;

/**
 * @author wuming
 * @description: 试卷Controller
 */
@RestController
@RequestMapping("/exam_paper")
public class ExamPaperController {

    @Autowired
    private ExamPaperService examPaperService;

    /**
     * 分页查询试卷列表
     */
    @PostMapping("/page")
    public Result page(@RequestParam(defaultValue = "1") Integer current,
                       @RequestParam(defaultValue = "10") Integer size,
                       @RequestBody(required = false) ExamPaper entity) {
        if (entity == null) {
            entity = new ExamPaper();
        }
        return Result.success(examPaperService.pageQuery(current, size, entity));
    }

    /**
     * 查询所有试卷列表
     */
    @PostMapping("/list")
    public Result list(@RequestBody(required = false) ExamPaper entity) {
        if (entity == null) {
            entity = new ExamPaper();
        }
        List<ExamPaper> list = examPaperService.listQuery(entity);
        return Result.success(list);
    }

    /**
     * 根据ID查询试卷
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        ExamPaper examPaper = examPaperService.getById(id);
        if (examPaper == null) {
            return Result.error("试卷不存在");
        }
        return Result.success(examPaper);
    }

    /**
     * 新增试卷
     */
    @PostMapping
    public Result save(@RequestBody ExamPaper examPaper) {
        examPaper.setCreateTime(LocalDateTime.now());
        boolean success = examPaperService.save(examPaper);
        if (success) {
            return Result.success("新增成功", examPaper);
        }
        return Result.error("新增失败");
    }

    /**
     * 更新试卷
     */
    @PutMapping
    public Result update(@RequestBody ExamPaper examPaper) {
        boolean success = examPaperService.updateById(examPaper);
        if (success) {
            return Result.success("更新成功", examPaper);
        }
        return Result.error("更新失败");
    }

    /**
     * 根据ID删除试卷
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        boolean success = examPaperService.removeById(id);
        if (success) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }

    /**
     * 批量删除试卷
     */
    @DeleteMapping("/batch")
    public Result deleteBatch(@RequestBody List<Long> ids) {
        boolean success = examPaperService.removeByIds(ids);
        if (success) {
            return Result.success("批量删除成功");
        }
        return Result.error("批量删除失败");
    }

    /**
     * 下拉数据接口：exam_course
     */
    @GetMapping("/select/listExamCourse")
    public Result listExamCourse() {
        List<SelectVO> list = examPaperService.listExamCourse();
        return Result.success(list);
    }

    /**
     * 启用试卷
     */
    @PutMapping("/enable/{id}")
    public Result enable(@PathVariable Long id) {
        ExamPaper examPaper = examPaperService.getById(id);
        if (examPaper == null) {
            return Result.error("试卷不存在");
        }
        
        // 重新计算试卷总分，确保数据是最新的
        Result calculateResult = examPaperService.calculatePaperInfo(id);
        if (calculateResult.getCode() != 200) {
            return Result.error("计算试卷总分失败：" + calculateResult.getMsg());
        }
        
        // 重新获取试卷信息（包含最新计算的总分）
        examPaper = examPaperService.getById(id);
        
        // 检查试卷总分是否为100分
        if (examPaper.getTotalScore() == null) {
            return Result.error("试卷总分不能为空，请先添加题目");
        }
        
        // 比较总分是否为100分（使用BigDecimal比较，避免浮点数精度问题）
        java.math.BigDecimal totalScore = examPaper.getTotalScore();
        java.math.BigDecimal requiredScore = new java.math.BigDecimal("100.00");
        
        if (totalScore.compareTo(requiredScore) != 0) {
            return Result.error("试卷总分必须为100分，当前总分为：" + totalScore + "分，请调整题目分值");
        }
        
        examPaper.setStatus(1); // 启用
        boolean success = examPaperService.updateById(examPaper);
        if (success) {
            return Result.success("启用成功");
        }
        return Result.error("启用失败");
    }

    /**
     * 停用试卷
     */
    @PutMapping("/disable/{id}")
    public Result disable(@PathVariable Long id) {
        ExamPaper examPaper = examPaperService.getById(id);
        if (examPaper == null) {
            return Result.error("试卷不存在");
        }
        examPaper.setStatus(0); // 停用
        boolean success = examPaperService.updateById(examPaper);
        if (success) {
            return Result.success("停用成功");
        }
        return Result.error("停用失败");
    }

    /**
     * 批量添加题目到试卷
     */
    @PostMapping("/{paperId}/addQuestions")
    public Result addQuestions(@PathVariable Long paperId, @RequestBody List<Long> questionIds) {
        return examPaperService.addQuestions(paperId, questionIds);
    }

    /**
     * 计算并更新试卷总分和题目数量
     */
    @PutMapping("/{paperId}/calculate")
    public Result calculatePaperInfo(@PathVariable Long paperId) {
        return examPaperService.calculatePaperInfo(paperId);
    }

    /**
     * AI组卷
     */
    @PostMapping("/{paperId}/ai-generate")
    public Result aiGeneratePaper(@PathVariable Long paperId) {
        return examPaperService.aiGeneratePaper(paperId);
    }

}
