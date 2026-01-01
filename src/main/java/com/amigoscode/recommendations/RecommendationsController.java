package com.amigoscode.recommendations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
public class RecommendationsController {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String inferenceBaseUrl;

    public RecommendationsController(@Value("${INFERENCE_BASE_URL}") String inferenceBaseUrl) {
        this.inferenceBaseUrl = inferenceBaseUrl;
    }

    @GetMapping(value = "/recommendations/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> recommendations(@PathVariable long id) {
        Map<String, Object> payload = Map.of("user_id", id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        ResponseEntity<Object> resp = restTemplate.exchange(
                inferenceBaseUrl + "/predict",
                HttpMethod.POST,
                entity,
                Object.class
        );

        return ResponseEntity.status(resp.getStatusCode()).body(resp.getBody());
    }

}
