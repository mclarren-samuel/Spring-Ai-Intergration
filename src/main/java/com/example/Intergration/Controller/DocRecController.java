package com.example.Intergration.Controller;

import com.example.Intergration.Service.DocRecService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/refine")
public class DocRecController {

        private final DocRecService docRefinerService;

        public DocRecController(DocRecService service) {
            this.docRefinerService = service;
        }

        @PostMapping
        public RefinementResponse refine(@RequestBody RefinementRequest request) {
            var result = docRefinerService.refine(
                    request.topic(),
                    request.roughContent()
            );

            return new RefinementResponse(
                    request.topic(),
                    result.refinedContent(),
                    result.iterations(),
                    result.approved(),
                    result.feedbackHistory(),
                    "completed"
            );
        }

        public record RefinementRequest(String topic, String roughContent) {}
        public record RefinementResponse(
                String topic,
                String refinedContent,
                int iterations,
                boolean approved,
                List<String> feedback,
                String status
        ) {}
}