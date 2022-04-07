package com.apitest.services;

import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.net.URI;
import java.util.Collections;
import java.util.Map;

@Service
public class RestService<T> {

    @Autowired
    RestTemplate restTemplate;

    public ResponseEntity<T> getForObject(String baseUrl, HttpHeaders headers, Class clazz, String endpoint) {
        URI uri = buildUrl(baseUrl, endpoint);
        return getForObject(uri, headers, clazz);
    }

    public ResponseEntity<T> getForObject(String baseUrl, HttpHeaders headers, Class clazz, String... endpoints) {
        URI uri = buildUrl(baseUrl, endpoints);
        return getForObject(uri, headers, clazz);
    }

    public ResponseEntity<T> getForObject(String baseUrl, HttpHeaders headers, MultiValueMap<String, String> queryParams, Class clazz, String... endpoints) {
        URI uri = buildUrl(baseUrl, queryParams, endpoints);
        return getForObject(uri, headers, clazz);
    }

    public ResponseEntity<T> getForObject(String baseUrl, HttpHeaders headers, Map<String, Object> urlParam, Class clazz, String... endpoints) {
        URI uri = buildUrl(baseUrl, urlParam, null, endpoints);
        return getForObject(uri, headers, clazz);
    }

    public ResponseEntity<T> deleteForObject(String baseUrl, HttpHeaders headers, Map<String, Object> urlParam, Class clazz, String... endpoints) {
        URI uri = buildUrl(baseUrl, urlParam, null, endpoints);
        return sendRequest(uri, headers, clazz, HttpMethod.DELETE);
    }

    public ResponseEntity<T> getForObject(URI uri, HttpHeaders headers, Class clazz) {
        return sendRequest(uri, headers, clazz, HttpMethod.GET);
    }

    public ResponseEntity<T> postForObject(String baseUrl, HttpHeaders headers, Object dataObject, Class clazz, String... endpoints) {
        return sendRequest(baseUrl, headers, dataObject, clazz, HttpMethod.POST, endpoints);
    }

    public ResponseEntity<T> postForObject(String baseUrl, HttpHeaders headers,  Class clazz, String... endpoints) {
        return postForObject(baseUrl, headers, null , clazz, endpoints);
    }

    public ResponseEntity<T> postForObject(String baseUrl, HttpHeaders headers, Map<String, Object> urlParams, MultiValueMap<String, String> queryParams, Class clazz, String... endpoints) {
        URI uri = buildUrl(baseUrl, urlParams, queryParams, endpoints);
        return sendRequest(uri, headers, null, clazz, HttpMethod.POST);
    }

    public ResponseEntity<T> postForObject(String baseUrl, HttpHeaders headers, Object dataObject, Map<String, Object> urlParams, MultiValueMap<String, String> queryParams, Class clazz, String... endpoints) {
        URI uri = buildUrl(baseUrl, urlParams, queryParams, endpoints);
        return sendRequest(uri, headers, dataObject, clazz, HttpMethod.POST);
    }

    public ResponseEntity<T> postImageForObject(String baseUrl, File file, Map<String, Object> urlParams, Class clazz, String... endpoints) {
        URI uri = buildUrl(baseUrl, urlParams, null, endpoints);
        return sendRequestWithImage(uri, file, clazz, HttpMethod.POST);
    }

    public ResponseEntity<T> putForObject(String baseUrl, HttpHeaders headers, Map<String, Object> urlParams, Class clazz, String... endpoints) {
        return putForObject(baseUrl, headers, null, urlParams, clazz, endpoints);
    }

    public ResponseEntity<T> putForObject(String baseUrl, HttpHeaders headers, Object dataObject, Class clazz, String... endpoints) {
        return sendRequest(baseUrl, headers, dataObject, clazz, HttpMethod.PUT, endpoints);
    }

    public ResponseEntity<T> putForObject(String baseUrl, HttpHeaders headers, Object dataObject, Map<String, Object> urlParams, Class clazz, String... endpoints) {
        URI uri = buildUrl(baseUrl, urlParams, null, endpoints);
        return sendRequest(uri, headers, dataObject, clazz, HttpMethod.PUT);
    }

    public ResponseEntity<T> deleteForObject(String baseUrl, HttpHeaders headers, Class clazz, String... endpoints) {
        return sendRequest(baseUrl, headers, clazz, HttpMethod.PUT, endpoints);
    }

    private ResponseEntity<T> sendRequest(String baseUrl, HttpHeaders headers, Class clazz, HttpMethod method, String... endpoints) {
        return sendRequest(baseUrl, headers, null, clazz, HttpMethod.PUT, endpoints);
    }

    private ResponseEntity<T> sendRequest(URI uri, HttpHeaders headers, Class clazz, HttpMethod method) {
        return sendRequest(uri, headers, null, clazz, method);
    }

    private ResponseEntity<T> sendRequest(String baseUrl, HttpHeaders headers, Object dataObject, Class clazz, HttpMethod method, String... endpoints) {
        URI uri = buildUrl(baseUrl, endpoints);
        return sendRequest(uri, headers, dataObject, clazz, method);
    }

    private ResponseEntity<T> sendRequest(URI uri, HttpHeaders headers, Object dataObject, Class clazz, HttpMethod method) {
        HttpEntity entity = new HttpEntity(dataObject, headers);
        return restTemplate.exchange(
                uri, method, entity, clazz);
    }

    @SneakyThrows
    private ResponseEntity<T> sendRequestWithImage(URI uri, File file, Class clazz, HttpMethod method) {
        restTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        HttpEntity entity = new HttpEntity(FileUtils.readFileToByteArray(file), httpHeaders);
        return restTemplate.exchange(
                uri, method, entity, clazz);
    }

    private UriComponentsBuilder createBasicUriBuilder(String baseUrl, String... pathSegments) {
        return UriComponentsBuilder
                .fromHttpUrl(baseUrl)
                .pathSegment(pathSegments);
    }

    private URI buildUrl(String baseUrl, String... pathSegments) {
        return createBasicUriBuilder(baseUrl, pathSegments).build()
                .toUri();
    }

    private URI buildUrl(String baseUrl, @Nullable MultiValueMap<String, String> queryParams, String... pathSegments) {
        return buildUrl(baseUrl, Collections.emptyMap(), queryParams, pathSegments);
    }

    private URI buildUrl(String baseUrl, Map<String, Object> urlParams, @Nullable MultiValueMap<String, String> queryParams, String... pathSegments) {
        return createBasicUriBuilder(baseUrl, pathSegments)
                .uriVariables(urlParams)
                .queryParams(queryParams)
                .build()
                .toUri();
    }
}
