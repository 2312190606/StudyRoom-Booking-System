package com.example.studyroom.controller;

import com.example.studyroom.common.Result;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * AI 客服控制器（接入阿里云百炼 API）
 */
@RestController
@RequestMapping("/api/ai")
public class AIController {

    private static final String API_KEY = "sk-987de2f40e704872b567950739efa3b5";
    private static final String API_URL = "https://api.deepseek.com/v1/chat/completions";

    @Autowired
    private RestTemplate restTemplate;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // 静学自习室知识库
    private static final String SYSTEM_PROMPT = "你是一个智能客服助手，专门为\"静学自习室\"用户提供服务。请根据以下信息回答用户问题：\n\n" +
            "【系统信息】\n" +
            "- 名称：静学自习室座位预约系统\n" +
            "- 功能：自习室座位预约、签到、信誉分管理\n" +
            "- 开放时间：每天 7:00-22:30\n" +
            "- 最长预约时长：4小时\n" +
            "- 取消预约时限：提前30分钟\n\n" +
            "【信誉分规则】\n" +
            "- 初始信誉分：100分\n" +
            "- 签到成功：+5分\n" +
            "- 每日签到额外奖励：+5分\n" +
            "- 违约一次：-10分\n" +
            "- 信誉分低于60分：禁用预约功能\n\n" +
            "【座位类型】\n" +
            "- 普通座位\n" +
            "- 靠窗座位\n" +
            "- 带电源座位\n\n" +
            "【违约行为】\n" +
            "- 预约后未签到\n" +
            "- 签到后提前离开超过30分钟\n" +
            "- 频繁取消预约\n\n" +
            "请用友好、简洁的方式回答用户问题。如果不确定答案，请告知用户联系管理员。";

    /**
     * AI 问答
     */
    @PostMapping("/chat")
    public Result<String> chat(@RequestBody Map<String, String> request) {
        String question = request.get("question");

        if (question == null || question.trim().isEmpty()) {
            return Result.success("请输入您的问题");
        }

        try {
            String answer = callBailianAPI(question);
            return Result.success(answer);
        } catch (Exception e) {
            // API 调用失败时返回友好提示
            return Result.success("抱歉，AI 服务暂时无法响应，请稍后再试或联系管理员。");
        }
    }

    /**
     * 调用百炼 API
     */
    private String callBailianAPI(String question) throws Exception {
        // 构建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + API_KEY);

        // 构建消息列表
        List<Map<String, String>> messages = new ArrayList<>();

        // 系统提示
        Map<String, String> systemMsg = new HashMap<>();
        systemMsg.put("role", "system");
        systemMsg.put("content", SYSTEM_PROMPT);
        messages.add(systemMsg);

        // 用户问题
        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", question);
        messages.add(userMsg);

        // 构建请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "deepseek-chat");
        requestBody.put("messages", messages);
        requestBody.put("max_tokens", 500);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        // 发送请求
        ResponseEntity<String> response = restTemplate.exchange(
                API_URL,
                HttpMethod.POST,
                entity,
                String.class
        );

        // 解析响应
        JsonNode rootNode = objectMapper.readTree(response.getBody());
        JsonNode choices = rootNode.get("choices");

        if (choices != null && choices.isArray() && choices.size() > 0) {
            JsonNode message = choices.get(0).get("message");
            return message.get("content").asText();
        }

        return "抱歉，我暂时无法回答这个问题，请稍后再试。";
    }
}
