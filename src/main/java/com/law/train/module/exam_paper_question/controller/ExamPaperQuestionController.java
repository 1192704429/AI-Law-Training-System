package com.law.train.module.exam_paper_question.controller;

import com.law.train.common.Result;
import com.law.train.module.exam_paper_question.entity.ExamPaperQuestion;
import com.law.train.module.exam_paper_question.service.ExamPaperQuestionService;
import com.law.train.module.exam_paper.service.ExamPaperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.law.train.vo.SelectVO;

/**
 * @author wuming
 * @description: 试卷题目关联Controller
 */
@RestController
@RequestMapping("/exam_paper_question")
public class ExamPaperQuestionController {

    @Autowired
    private ExamPaperQuestionService examPaperQuestionService;

    @Autowired
    private ExamPaperService examPaperService;

    /**
     * 分页查询试卷题目关联列表
     */
    @PostMapping("/page")
    public Result page(@RequestParam(defaultValue = "1") Integer current,
                       @RequestParam(defaultValue = "10") Integer size,
                       @RequestBody(required = false) ExamPaperQuestion entity) {
        if (entity == null) {
            entity = new ExamPaperQuestion();
        }
        return Result.success(examPaperQuestionService.pageQuery(current, size, entity));
    }

    /**
     * 查询所有试卷题目关联列表
     */
    @PostMapping("/list")
    public Result list(@RequestBody(required = false) ExamPaperQuestion entity) {
        if (entity == null) {
            entity = new ExamPaperQuestion();
        }
        List<ExamPaperQuestion> list = examPaperQuestionService.listQuery(entity);
        return Result.success(list);
    }

    /**
     * 根据ID查询试卷题目关联
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        ExamPaperQuestion examPaperQuestion = examPaperQuestionService.getById(id);
        if (examPaperQuestion == null) {
            return Result.error("试卷题目关联不存在");
        }
        return Result.success(examPaperQuestion);
    }

    /**
     * 新增试卷题目关联
     */
    @PostMapping
    public Result save(@RequestBody ExamPaperQuestion examPaperQuestion) {
        boolean success = examPaperQuestionService.save(examPaperQuestion);
        if (success) {
            return Result.success("新增成功", examPaperQuestion);
        }
        return Result.error("新增失败");
    }

    /**
     * 更新试卷题目关联
     */
    @PutMapping
    public Result update(@RequestBody ExamPaperQuestion examPaperQuestion) {
        boolean success = examPaperQuestionService.updateById(examPaperQuestion);
        if (success) {
            return Result.success("更新成功", examPaperQuestion);
        }
        return Result.error("更新失败");
    }

    /**
     * 根据ID删除试卷题目关联
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        // 先查询获取paperId
        ExamPaperQuestion paperQuestion = examPaperQuestionService.getById(id);
        if (paperQuestion == null) {
            return Result.error("试卷题目关联不存在");
        }
        Long paperId = paperQuestion.getPaperId();
        
        boolean success = examPaperQuestionService.removeById(id);
        if (success) {
            // 重新计算试卷信息
            examPaperService.calculatePaperInfo(paperId);
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }

    /**
     * 批量删除试卷题目关联
     */
    @DeleteMapping("/batch")
    public Result deleteBatch(@RequestBody List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Result.error("ID列表不能为空");
        }
        
        // 先查询获取所有paperId
        List<ExamPaperQuestion> paperQuestions = (List<ExamPaperQuestion>) examPaperQuestionService.listByIds(ids);
        if (paperQuestions.isEmpty()) {
            return Result.error("未找到要删除的数据");
        }
        
        // 获取所有不重复的paperId
        java.util.Set<Long> paperIds = paperQuestions.stream()
                .map(ExamPaperQuestion::getPaperId)
                .collect(java.util.stream.Collectors.toSet());
        
        boolean success = examPaperQuestionService.removeByIds(ids);
        if (success) {
            // 重新计算所有相关试卷信息
            for (Long paperId : paperIds) {
                examPaperService.calculatePaperInfo(paperId);
            }
            return Result.success("批量删除成功");
        }
        return Result.error("批量删除失败");
    }

    /**
     * 下拉数据接口：exam_paper
     */
    @GetMapping("/select/listExamPaper")
    public Result listExamPaper() {
        List<SelectVO> list = examPaperQuestionService.listExamPaper();
        return Result.success(list);
    }

    /**
     * 下拉数据接口：exam_question
     */
    @GetMapping("/select/listExamQuestion")
    public Result listExamQuestion() {
        List<SelectVO> list = examPaperQuestionService.listExamQuestion();
        return Result.success(list);
    }

}
