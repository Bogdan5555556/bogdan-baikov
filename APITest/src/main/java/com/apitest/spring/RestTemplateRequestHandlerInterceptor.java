package com.apitest.spring;

import com.apitest.services.AttachmentsProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class RestTemplateRequestHandlerInterceptor
        implements ClientHttpRequestInterceptor {

    @Autowired
    AttachmentsProvider attachmentsProvider;

    @Override
    public ClientHttpResponse intercept(
            HttpRequest request,
            byte[] body,
            ClientHttpRequestExecution execution) throws IOException {
        attachmentsProvider.attachRequest(request);
        attachmentsProvider.attachRequest(body);
        ClientHttpResponse response = execution.execute(request, body);
        return response;
    }
}