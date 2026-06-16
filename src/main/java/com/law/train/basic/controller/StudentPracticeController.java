package com.law.train.basic.controller;

import cn.hutool.core.util.StrUtil;
import com.law.train.common.Result;
import com.law.train.basic.controller.ExamStudentLoginController.StudentLoginUser;
import com.law.train.module.exam_question.entity.ExamQuestion;
import com.law.train.module.exam_question_option.entity.ExamQuestionOption;
import com.law.train.module.exam_question_option.service.ExamQuestionOptionService;
import com.law.train.module.exam_question.service.ExamQuestionService;
import com.law.train.module.exam_question_practice_record.entity.ExamQuestionPracticeRecord;
import com.law.train.module.exam_question_practice_record.service.ExamQuestionPracticeRecordService;
import com.law.train.aspect.ForgettingCurvePracticeRecordAspect;
import com.law.train.service.ForgettingCurveService;
import com.law.train.util.AiChatClient;
import com.law.train.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 学生端刷题练习：按顺序/随机/按题型获取题目，主观题 AI 打分
 */
@RestController
@RequestMapping("/student")
public class StudentPracticeController {

    @Autowired
    private ExamQuestionService examQuestionService;
    @Autowired
    private ExamQuestionOptionService examQuestionOptionService;
    @Autowired
    private ExamQuestionPracticeRecordService practiceRecordService;
    @Autowired
    private ForgettingCurveService forgettingCurveService;
    @Autowired
    private JwtUtil<StudentLoginUser> jwtUtil;
    @Autowired
    private AiChatClient aiChatClient;

    private Long getStudentId(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (StrUtil.isBlank(authorization)) return null;
        if (ExamStudentLoginController.STUDENT_TOKEN_CACHE_MAP.get(authorization) == null) return null;
        if (!jwtUtil.verifyToken(authorization)) return null;
        StudentLoginUser user = jwtUtil.parseToken(authorization, StudentLoginUser.class);
        return user != null ? user.getId() : null;
    }

    /**
     * 获取一道练习题目
     * @param courseId 课程ID
     * @param mode sequential=顺序练习, random=随机练习, byType=按题型练习
     * @param questionTypes 题型，逗号分隔，如 1,2（仅 byType 时有效）
     * @param index 当前题序号，从 0 开始（仅 sequential 时有效）
     */
    @GetMapping("/practice/question")
    public Result getPracticeQuestion(
            @RequestParam Long courseId,
            @RequestParam String mode,
            @RequestParam(required = false) String questionTypes,
            @RequestParam(defaultValue = "0") Integer index) {

        ExamQuestion entity = new ExamQuestion();
        entity.setCourseId(courseId);
        entity.setStatus(1);

        if ("byType".equals(mode) && StringUtils.hasText(questionTypes)) {
            List<Integer> types = Arrays.stream(questionTypes.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            if (!types.isEmpty()) {
                entity.setQuestionTypeList(types);
            }
        }

        ExamQuestion question = null;
        int totalCount = 0;
        int currentIndex = index;

        if ("sequential".equals(mode)) {
            List<ExamQuestion> list = examQuestionService.listQuery(entity);
            list.sort(Comparator.comparing(ExamQuestion::getId));
            totalCount = list.size();
            if (totalCount == 0) {
                return Result.error("该课程下暂无题目");
            }
            if (index < 0 || index >= totalCount) {
                currentIndex = Math.max(0, Math.min(index, totalCount - 1));
            }
            Long questionId = list.get(currentIndex).getId();
            question = examQuestionService.getById(questionId);
        } else {
            question = examQuestionService.getRandomOne(entity);
            if (question == null) {
                return Result.error("该条件下暂无题目");
            }
            totalCount = 1;
            currentIndex = 0;
        }

        List<ExamQuestionOption> options = new ArrayList<>();
        if (question.getQuestionType() != null && (question.getQuestionType() == 1 || question.getQuestionType() == 2)) {
            ExamQuestionOption optQuery = new ExamQuestionOption();
            optQuery.setQuestionId(question.getId());
            options = examQuestionOptionService.listQuery(optQuery);
        }
        List<Map<String, Object>> optionItems = new ArrayList<>();
        for (ExamQuestionOption opt : options) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", opt.getId());
            item.put("optionKey", opt.getOptionKey());
            item.put("optionContent", opt.getOptionContent());
            item.put("sortNo", opt.getSortNo());
            optionItems.add(item);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("question", question);
        data.put("options", optionItems);
        data.put("totalCount", totalCount);
        data.put("currentIndex", currentIndex);
        return Result.success(data);
    }

    /**
     * 主观题 AI 打分
     */
    @PostMapping("/practice/subjective/score")
    public Result subjectiveScore(@RequestBody SubjectiveScoreRequest request) {
        if (request.getUserAnswer() == null || request.getUserAnswer().trim().isEmpty()) {
            return Result.error("请先作答");
        }
        String systemPrompt = "你是一名法考阅卷老师，请根据题目、标准答案对考生答案进行打分和简短点评。"
                + "打分满分 100，只考虑要点是否答到、表述是否合理。"
                + "请严格按以下 JSON 格式返回，不要包含其他内容：{\"score\": 分数(0-100的整数), \"comment\": \"简短点评文字\"}";
        String userPrompt = String.format(
                "题目：%s\n\n标准答案：%s\n\n考生答案：%s\n\n请给出 score 和 comment。",
                request.getQuestionTitle() != null ? request.getQuestionTitle() : "",
                request.getStandardAnswer() != null ? request.getStandardAnswer() : "",
                request.getUserAnswer().trim()
        );
        String aiResponse = aiChatClient.chat(systemPrompt, userPrompt);
        if (aiResponse == null || aiResponse.trim().isEmpty()) {
            return Result.error("AI 打分服务暂不可用，请稍后再试");
        }
        try {
            String cleaned = aiResponse.trim();
            if (cleaned.startsWith("```json")) cleaned = cleaned.substring(7);
            if (cleaned.startsWith("```")) cleaned = cleaned.substring(3);
            if (cleaned.endsWith("```")) cleaned = cleaned.substring(0, cleaned.length() - 3);
            cleaned = cleaned.trim();
            com.alibaba.fastjson.JSONObject obj = com.alibaba.fastjson.JSON.parseObject(cleaned);
            int score = obj.getIntValue("score");
            if (score < 0) score = 0;
            if (score > 100) score = 100;
            String comment = obj.getString("comment");
            if (comment == null) comment = "";
            Map<String, Object> data = new HashMap<>();
            data.put("score", score);
            data.put("comment", comment);
            return Result.success(data);
        } catch (Exception e) {
            Map<String, Object> data = new HashMap<>();
            data.put("score", 0);
            data.put("comment", "解析失败，请以标准答案为准自行对照。");
            return Result.success(data);
        }
    }

    /**
     * 学生端：提交单题练习记录（刷题作答后调用，触发遗忘曲线、错题本、计划明细）
     */
    @PostMapping("/practice/record")
    public Result submitPracticeRecord(HttpServletRequest request, @RequestBody SubmitPracticeRecordRequest body) {
        Long studentId = getStudentId(request);
        if (studentId == null) return Result.error(401, "请先登录");
        if (body.getCourseId() == null || body.getQuestionId() == null) return Result.error("缺少课程或题目");
        LocalDateTime now = LocalDateTime.now();
        ExamQuestionPracticeRecord record = new ExamQuestionPracticeRecord();
        record.setStudentId(studentId);
        record.setCourseId(body.getCourseId());
        record.setQuestionId(body.getQuestionId());
        record.setPracticeType(body.getPracticeType() != null ? body.getPracticeType() : 1);
        record.setSourceType(body.getSourceType() != null ? body.getSourceType() : 1);
        record.setPlanId(body.getPlanId());
        record.setStudentAnswer(body.getStudentAnswer());
        record.setStandardAnswer(body.getStandardAnswer());
        record.setIsCorrect(body.getIsCorrect() != null ? body.getIsCorrect() : 0);
        record.setScore(body.getScore() != null ? body.getScore() : BigDecimal.ZERO);
        record.setScoreRate(body.getScoreRate() != null ? body.getScoreRate() : BigDecimal.ZERO);
        record.setUseTimeSec(body.getUseTimeSec() != null ? body.getUseTimeSec() : 0);
        record.setJudgeType(body.getJudgeType() != null ? body.getJudgeType() : 1);
        record.setJudgeResult(body.getJudgeResult());
        record.setDifficultyLevel(body.getDifficultyLevel());
        record.setAnswerTime(now);
        record.setCreateTime(now);
        try {
            ForgettingCurvePracticeRecordAspect.setSkipAspect(true);
            practiceRecordService.save(record);
            forgettingCurveService.afterPracticeRecordSaved(record);
        } finally {
            ForgettingCurvePracticeRecordAspect.setSkipAspect(false);
        }
        return Result.success(record);
    }

    @Data
    public static class SubmitPracticeRecordRequest {
        private Long courseId;
        private Long questionId;
        private Integer practiceType;
        private Integer sourceType;
        private Long planId;
        private String studentAnswer;
        private String standardAnswer;
        private Integer isCorrect;
        private BigDecimal score;
        private BigDecimal scoreRate;
        private Integer useTimeSec;
        private Integer judgeType;
        private String judgeResult;
        private Integer difficultyLevel;
    }

    @Data
    public static class SubjectiveScoreRequest {
        private Long questionId;
        private String questionTitle;
        private String userAnswer;
        private String standardAnswer;
    }
}
