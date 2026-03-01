package com.decisiontool.service;

import com.decisiontool.dto.*;
import com.decisiontool.model.ChatMessage;
import com.decisiontool.model.ChatSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DenodoChatService {

    private static final Logger log = LoggerFactory.getLogger(DenodoChatService.class);

    // Timeout realista para Denodo AI SDK
    private static final Duration DENODO_TIMEOUT = Duration.ofSeconds(90);

    private final DenodoApiClient denodoApiClient;
    private final DatasetContextBuilder contextBuilder;

    private final Map<String, ChatSession> sessions = new ConcurrentHashMap<>();

    public DenodoChatService(
            DenodoApiClient denodoApiClient,
            DatasetContextBuilder contextBuilder
    ) {
        this.denodoApiClient = denodoApiClient;
        this.contextBuilder = contextBuilder;
    }

    public ChatResponse process(ChatRequest request) {

        String question = request.getQuestion();

        log.info("Procesando pregunta: '{}'", question);

        ChatSession session = resolveSession(request.getSessionId());

        // ✔ Validaciones seguras
        if (question == null || question.isBlank()) {
            return error(session,"Pregunta vacía");
        }

        String vdpDatabaseName = request.getDatasetId();

        if (vdpDatabaseName == null || vdpDatabaseName.isBlank()) {
            return error(session,"datasetId obligatorio");
        }

        log.info("Database usada: {}", vdpDatabaseName);

        try {

            // =========================
            // 1. METADATA
            // =========================

            log.info("→ Metadata start");

            DenodoMetadataRequest metadataReq =
                    DenodoMetadataRequest.builder()
                            .question(question)
                            .context("")
                            .build();

            DenodoAnswerResponse metadata =
                    denodoApiClient
                            .answerMetadataQuestion(vdpDatabaseName, metadataReq)
                            .timeout(DENODO_TIMEOUT)
                            .block();

            log.info("→ Metadata OK");

            if (metadata == null) {
                return error(session,"Metadata null");
            }

            // =========================
            // 2. CONTEXTO LIGERO
            // =========================

            String context = contextBuilder.buildContext(metadata,null,question);

            if(context.length()>3000){
                context=context.substring(0,3000);
            }

            // =========================
            // 3. DATA
            // =========================

            log.info("→ Data start");

            DenodoDataRequest dataReq =
                    DenodoDataRequest.builder()
                            .question(question)
                            .context(context)
                            .build();

            DenodoAnswerResponse data=null;

            try {

                data =
                        denodoApiClient
                                .answerDataQuestion(vdpDatabaseName,dataReq)
                                .timeout(DENODO_TIMEOUT)
                                .block();

                log.info("→ Data OK");

            }
            catch(Exception ex){

                log.warn("Data falló → fallback metadata");

            }

            // =========================
            // 4. RESPUESTA FINAL
            // =========================

            String answer=null;

            if(data!=null && data.getAnswer()!=null){
                answer=data.getAnswer();
            }
            else{
                answer=metadata.getAnswer();
            }

            if(answer==null){
                answer="No se obtuvo respuesta";
            }

            // =========================
            // 5. TABLE DATA
            // =========================

            List<Map<String,Object>> tableData=extractTableData(data);

            // =========================
            // 6. METRICS DEBUG
            // =========================

            Map<String,Object> metrics=new HashMap<>();

            metrics.put("tablesUsed",metadata.getTablesUsed());
            metrics.put("contextSize",context.length());
            metrics.put("metadataOk",metadata!=null);
            metrics.put("dataOk",data!=null);

            // =========================
            // 7. HISTORIAL
            // =========================

            addToHistory(session,ChatMessage.Role.USER,question);
            addToHistory(session,ChatMessage.Role.ASSISTANT,answer);

            return ChatResponse.builder()
                    .sessionId(session.getId())
                    .answer(answer)
                    .tableData(tableData)
                    .metrics(metrics)
                    .error(false)
                    .build();

        }
        catch(Exception ex){

            log.error("Error Denodo",ex);

            return error(session,"Denodo error: "+ex.getMessage());
        }
    }

    // =========================
    // HELPERS
    // =========================

    private ChatResponse error(ChatSession s,String msg){

        return ChatResponse.builder()
                .sessionId(s.getId())
                .error(true)
                .errorMessage(msg)
                .build();
    }

    private List<Map<String,Object>> extractTableData(DenodoAnswerResponse data){

        if(data==null) return null;

        if(data.getExecutionResult()==null) return null;

        Object rows=data.getExecutionResult().get("rows");

        if(rows instanceof List<?> list){

            List<Map<String,Object>> out=new ArrayList<>();

            for(Object o:list){

                if(o instanceof Map<?,?> m){

                    out.add((Map<String,Object>)m);

                }

            }

            return out;
        }

        return null;
    }

    private ChatSession resolveSession(String sessionId){

        if(sessionId!=null && sessions.containsKey(sessionId)){
            return sessions.get(sessionId);
        }

        String id=sessionId!=null?sessionId:UUID.randomUUID().toString();

        ChatSession s=ChatSession.builder()
                .id(id)
                .createdAt(LocalDateTime.now())
                .lastActivity(LocalDateTime.now())
                .build();

        sessions.put(id,s);

        return s;
    }

    private void addToHistory(ChatSession s,ChatMessage.Role role,String content){

        ChatMessage msg=ChatMessage.builder()
                .id(UUID.randomUUID().toString())
                .role(role)
                .content(content)
                .timestamp(LocalDateTime.now())
                .build();

        s.addMessage(msg);
    }

    public Optional<ChatSession> getSession(String sessionId) {
    return Optional.ofNullable(sessions.get(sessionId));
}

}