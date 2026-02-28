package com.decisiontool.service;

import com.decisiontool.config.DenodoConfig;
import com.decisiontool.dto.DenodoDataRequest;
import com.decisiontool.dto.DenodoDataResponse;
import com.decisiontool.dto.DenodoMetadataRequest;
import com.decisiontool.dto.DenodoMetadataResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Component
public class DenodoApiClient {

    private final WebClient denodoWebClient;
    private final DenodoConfig props;

    public DenodoApiClient(@Qualifier("denodoWebClient") WebClient denodoWebClient,
                           DenodoConfig props) {
        this.denodoWebClient = denodoWebClient;
        this.props = props;
    }

    public Mono<String> health() {
        return denodoWebClient.get()
                .uri("/health")
                .retrieve()
                .onStatus(HttpStatusCode::isError, r -> r.bodyToMono(String.class)
                        .defaultIfEmpty("Denodo health error")
                        .flatMap(msg -> Mono.error(new RuntimeException(msg))))
                .bodyToMono(String.class)
                .retryWhen(defaultRetry());
    }

    /** POST /answerQuestion */
    public Mono<String> answerQuestion(Object requestBody) {
        return denodoWebClient.post()
                .uri("/answerQuestion")
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(HttpStatusCode::isError, r -> r.bodyToMono(String.class)
                        .defaultIfEmpty("Denodo /answerQuestion error")
                        .flatMap(msg -> Mono.error(new RuntimeException(msg))))
                .bodyToMono(String.class)
                .retryWhen(defaultRetry());
    }

    /** POST /answerDataQuestion */
    public Mono<DenodoDataResponse> answerDataQuestion(DenodoDataRequest request) {
        return denodoWebClient.post()
                .uri("/answerDataQuestion")
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, r -> r.bodyToMono(String.class)
                        .defaultIfEmpty("Denodo /answerDataQuestion error")
                        .flatMap(msg -> Mono.error(new RuntimeException(msg))))
                .bodyToMono(DenodoDataResponse.class)
                .retryWhen(defaultRetry());
    }

    /** POST /answerMetadataQuestion */
    public Mono<DenodoMetadataResponse> answerMetadataQuestion(DenodoMetadataRequest request) {
        return denodoWebClient.post()
                .uri("/answerMetadataQuestion")
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, r -> r.bodyToMono(String.class)
                        .defaultIfEmpty("Denodo /answerMetadataQuestion error")
                        .flatMap(msg -> Mono.error(new RuntimeException(msg))))
                .bodyToMono(DenodoMetadataResponse.class)
                .retryWhen(defaultRetry());
    }

    private Retry defaultRetry() {
        int retries = Math.max(props.getMaxRetries(), 0);
        if (retries == 0) return Retry.max(0);

        return Retry.backoff(retries, Duration.ofMillis(250))
                .maxBackoff(Duration.ofSeconds(2))
                .filter(ex -> true);
    }
}