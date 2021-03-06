package com.apitest;

import com.apitest.config.EndpointProperties;
import com.apitest.services.RestService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;

public class BasePetStoreTest {

    HttpHeaders headers;

    @Autowired
    RestService restService;

    @Autowired
    ResourceLoader resourceLoader;

    @Autowired
    EndpointProperties endpoints;

    @BeforeEach
    public void prepareDefaultHeaders() {
        headers = new HttpHeaders();
    }
}
