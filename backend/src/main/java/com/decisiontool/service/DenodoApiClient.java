package com.decisiontool.service;

import com.decisiontool.config.DenodoConfig;
import com.decisiontool.dto.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
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

    public Mono<ResponseEntity<String>> getMetadataRaw(String dbsCsv) {
        return denodoWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/getMetadata")
                        .queryParam("vdp_database_names", dbsCsv)
                        .queryParam("incremental", true)
                        .build()
                )
                .retrieve()
                .toEntity(String.class);
    }

    /** POST /answerQuestion */
    public Mono<String> answerQuestion(String vdpDatabaseName, Object requestBody) {
        if (vdpDatabaseName == null || vdpDatabaseName.isBlank()) {
            return Mono.error(new IllegalArgumentException("vdpDatabaseName es obligatorio"));
        }

        return denodoWebClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/answerQuestion")
                        .queryParam("vdp_database_names", vdpDatabaseName)
                        .build()
                )
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(HttpStatusCode::isError, r ->
                        r.bodyToMono(String.class)
                                .defaultIfEmpty("Sin body")
                                .flatMap(body -> Mono.error(
                                        new RuntimeException("Denodo /answerQuestion -> " + r.statusCode() + " | " + body)
                                ))
                )
                .bodyToMono(String.class)
                .retryWhen(defaultRetry());
    }

    /**
     * GET /answerDataQuestion (según Swagger)
     * Fuerza disclaimer=false y check_ambiguity=false.
     *
     * NOTA: sólo usamos request.getQuestion() porque tus DTOs no tienen más campos.
     */
    public Mono<DenodoDataResponse> answerDataQuestion(String vdpDatabaseName, DenodoDataRequest request) {
        if (request == null || !StringUtils.hasText(request.getQuestion())) {
            return Mono.error(new IllegalArgumentException("DenodoDataRequest.question es obligatorio"));
        }
        if (!StringUtils.hasText(vdpDatabaseName)) {
            return Mono.error(new IllegalArgumentException("vdpDatabaseName es obligatorio"));
        }

        return denodoWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/answerDataQuestion")
                        .queryParam("question", request.getQuestion())
                        .queryParam("vdp_database_names", vdpDatabaseName) // ✅ AQUI
                        .queryParam("disclaimer", false)
                        .queryParam("check_ambiguity", false)
                        .build()
                )
                .retrieve()
                .onStatus(HttpStatusCode::isError, r -> r.bodyToMono(String.class)
                        .defaultIfEmpty("Denodo /answerDataQuestion error")
                        .flatMap(msg -> Mono.error(new RuntimeException(msg))))
                .bodyToMono(DenodoDataResponse.class)
                .retryWhen(defaultRetry());
    }

    public Mono<DenodoMetadataResponse> answerMetadataQuestion(String vdpDatabaseName, DenodoMetadataRequest request) {
        if (request == null || !StringUtils.hasText(request.getQuestion())) {
            return Mono.error(new IllegalArgumentException("DenodoMetadataRequest.question es obligatorio"));
        }
        if (!StringUtils.hasText(vdpDatabaseName)) {
            return Mono.error(new IllegalArgumentException("vdpDatabaseName es obligatorio"));
        }

        return denodoWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/answerMetadataQuestion")
                        .queryParam("question", request.getQuestion())
                        .queryParam("vdp_database_names", vdpDatabaseName) // ✅ AQUI
                        .queryParam("disclaimer", false)
                        .queryParam("check_ambiguity", false)
                        .build()
                )
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