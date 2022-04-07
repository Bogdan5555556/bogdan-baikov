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

//    @BeforeEach
//    public void deleteAllTriangles() {
//        ResponseEntity<TriangleResponseModel[]> allTriangles = restService.getForObject(baseUrl, headers, TriangleResponseModel[].class, "/triangle/all");
//        Arrays.stream(Objects.requireNonNull(allTriangles.getBody())).forEach(triangle -> {
//            restService.deleteForObject(baseUrl, headers, TriangleResponseModel[].class, "/triangle", triangle.getId());
//        });
//    }

//    @Step("Create triangle with input {input} and separator {separator}.")
//    public ResponseEntity<TriangleResponseModel> createTriangle(String input, String separator) {
//        TriangleRequestModel triangle = TriangleRequestModel.builder().input(input).separator(separator).build();
//        return restService.postForObject(baseUrl, "/triangle", headers, triangle, TriangleResponseModel.class);
//    }
}
