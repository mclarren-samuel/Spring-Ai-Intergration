package com.example.Intergration.Service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DocRecService {
    private final ChatClient chatClient;
    private static final int MAX_ITERATIONS = 3;

    public DocRecService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public RefinementResult refine(String topic, String roughContent) {
        String draft = roughContent;
        List<String> feedbackHistory = new ArrayList<>();
        int iteration = 0;
        boolean approved = false;

        while (iteration < MAX_ITERATIONS && !approved) {
            iteration++;

            // WRITER AGENT: Improve based on feedback (or create initial draft)
            String writerPrompt = iteration == 1
                    ? buildInitialWriterPrompt(topic, roughContent)
                    : buildRevisionPrompt(topic, draft, feedbackHistory.get(feedbackHistory.size() - 1));

            draft = chatClient.prompt()
                    .user(writerPrompt)
                    .call()
                    .content();

            // EVALUATOR AGENT: Critique the draft
            String evaluatorPrompt = buildEvaluatorPrompt(topic, draft);
            String evaluation = chatClient.prompt()
                    .user(evaluatorPrompt)
                    .call()
                    .content();

            feedbackHistory.add(evaluation);

            // Check if approved (look for APPROVED marker in response)
            if (evaluation.contains("APPROVED") || evaluation.contains("approved")) {
                approved = true;
            }
        }

        return new RefinementResult(draft, iteration, approved, feedbackHistory);
    }

    private String buildInitialWriterPrompt(String topic, String roughContent) {
        return """
            You are a technical documentation writer. Create a clear, well-structured 
            README or documentation section for the following topic.
            
            Topic: %s
            Rough content to refine: %s
            
            Requirements:
            - Use clear headings and code examples where appropriate
            - Be concise but complete
            - Target audience: developers
            
            Return ONLY the refined documentation, no explanations.
            """.formatted(topic, roughContent);
    }

    private String buildRevisionPrompt(String topic, String currentDraft, String feedback) {
        return """
            You are a technical documentation writer. Revise the following documentation 
            based on the editor's feedback.
            
            Topic: %s
            Current draft: %s
            Editor feedback: %s
            
            Address ALL feedback points. Return ONLY the revised documentation.
            """.formatted(topic, currentDraft, feedback);
    }

    private String buildEvaluatorPrompt(String topic, String draft) {
        return """
            You are a senior technical editor. Evaluate the following documentation 
            for topic: %s
            
            Draft:
            %s
            
            Evaluate based on:
            1. Clarity and readability
            2. Technical accuracy
            3. Completeness
            4. Structure and formatting
            
            If the document is acceptable, respond with "APPROVED" and a brief summary.
            Otherwise, provide specific, actionable feedback for improvement.
            Be critical but constructive.
            """.formatted(topic, draft);
    }

    // Record for structured response
    public record RefinementResult(
            String refinedContent,
            int iterations,
            boolean approved,
            List<String> feedbackHistory
    ) {}
}

