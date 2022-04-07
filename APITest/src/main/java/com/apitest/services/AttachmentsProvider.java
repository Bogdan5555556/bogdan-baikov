package com.apitest.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.qameta.allure.Attachment;
import lombok.SneakyThrows;
import org.apache.commons.text.TextStringBuilder;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class AttachmentsProvider {

    @Attachment(value = "Get method response",type = "text/json")
    public String attachGetResponse(ResponseEntity responseEntity){
        return attachResponse(responseEntity);
    }

    @Attachment(value = "Post method response",type = "text/json")
    public String attachPostResponse(ResponseEntity responseEntity){
        return attachResponse(responseEntity);
    }

    @Attachment(value = "Put method response",type = "text/json")
    public String attachPutResponse(ResponseEntity responseEntity){
        return attachResponse(responseEntity);
    }

    @Attachment(value = "Delete method response",type = "text/json")
    public String attachDeleteResponse(ResponseEntity responseEntity){
        return attachResponse(responseEntity);
    }

    @Attachment(value = "Request",type = "text/html")
    public String attachRequest(HttpRequest request){
        return formatRequest(request);
    }

    @Attachment(value = "Request body",type = "text/json")
    public String attachRequest(byte[] body){
        return new String(body);
    }

    @SneakyThrows
    private String attachResponse(ResponseEntity responseEntity){
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(responseEntity);
    }

    @SneakyThrows
    private String formatRequest(HttpRequest request){
        ObjectMapper mapper = new ObjectMapper();
        TextStringBuilder builder = new TextStringBuilder().append("Method: ")
                .append(request.getMethodValue())
                .appendNewLine()
                .append("URL: ")
                .append(request.getURI())
                .appendNewLine()
                .append("Headers: ")
                .append(request.getHeaders());
        return builder.toString();
    }
}
