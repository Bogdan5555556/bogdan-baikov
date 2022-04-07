package com.apitest;

import com.apitest.config.AppConfig;
import com.apitest.dataModel.Order;
import com.apitest.dataModel.StoreResponse;
import io.qameta.allure.Issue;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AppConfig.class)
public class CRUDOrderTest extends BasePetStoreTest{

    @Test
    @DisplayName("Create one order")
    public void createOneOrder() {
        Order newOrder = prepareBasicOrder();
        ResponseEntity<Order> response = restService.postForObject(endpoints.getBaseUrl(), headers, newOrder, Order.class, endpoints.getStoreEndpoint("order"));
        assertThat("There is wrong response status! ", response.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat("Response doesn't contain created Order", response.getBody(), equalTo(newOrder));
    }

    @Test
    @DisplayName("Create one order with incorrect status")
    public void createOneOrderWithIncorrectStatus() {
        Order newOrder = prepareBasicOrder();
        newOrder.setStatus(Order.OrderStatuses.UNDEFINED.getStatus());
        ResponseEntity<String> response = restService.postForObject(endpoints.getBaseUrl(), headers, newOrder, String.class, endpoints.getStoreEndpoint("order"));
        assertThat("There is wrong response status! ", response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
        assertThat("Response contains incorrect message", response.getBody(), equalTo("Unsupported order status!"));
    }

    @Test
    @DisplayName("Create one order without body content")
    public void createOneOrderWithBodyContent() {
        ResponseEntity<String> response = restService.postForObject(endpoints.getBaseUrl(), headers, String.class, endpoints.getStoreEndpoint("order"));
        assertThat("There is wrong response status! ", response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
        assertThat("Response contains incorrect message", response.getBody(), equalTo("No Order provided. Try again?"));
    }

    @Test
    @DisplayName("Find Order by id")
    public void findOrderById() {
        Order newOrder = prepareBasicOrder();
        restService.postForObject(endpoints.getBaseUrl(), headers, newOrder, String.class, endpoints.getStoreEndpoint("order"));
        Map<String,Long> urlParams = new HashMap<>();
        urlParams.put("orderId", newOrder.getId());
        ResponseEntity<Order> response = restService.getForObject(endpoints.getBaseUrl(), headers, urlParams, Order.class, endpoints.getStoreEndpoint("orderById"));
        assertThat("There is wrong response status! ", response.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat("Response doesn't contain created Order", response.getBody(), equalTo(newOrder));
    }

    @Test
    @DisplayName("Try to find not exist Order by id")
    public void notExistOrderById() {
        Order newOrder = prepareBasicOrder();
        Map<String,Long> urlParams = new HashMap<>();
        urlParams.put("orderId", newOrder.getId());
        ResponseEntity<String> response = restService.getForObject(endpoints.getBaseUrl(), headers, urlParams, String.class, endpoints.getStoreEndpoint("orderById"));
        assertThat("There is wrong response status! ", response.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
        assertThat("Response message is not as expected", response.getBody(), equalTo("Order not found"));
    }

    @Test
    @DisplayName("Try to find Order by invalid id")
    public void findOrderByInvalidId() {
        Map<String,Object> urlParams = new HashMap<>();
        urlParams.put("orderId", RandomStringUtils.randomAlphanumeric(10));
        ResponseEntity<StoreResponse> response = restService.getForObject(endpoints.getBaseUrl(), headers, urlParams, StoreResponse.class, endpoints.getStoreEndpoint("orderById"));
        assertThat("There is wrong response status! ", response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
        assertThat("There is wrong response status! ", response.getBody().getCode(), equalTo(400));
        assertThat("Response message is not as expected", response.getBody().getMessage(), equalTo("Invalid ID supplied"));
    }

    @Test
    @DisplayName("Delete Order by id")
    public void deleteOrderById() {
        Order newOrder = prepareBasicOrder();
        restService.postForObject(endpoints.getBaseUrl(), headers, newOrder, String.class, endpoints.getStoreEndpoint("order"));
        Map<String,Long> urlParams = new HashMap<>();
        urlParams.put("orderId", newOrder.getId());
        ResponseEntity<Order> response = restService.deleteForObject(endpoints.getBaseUrl(), headers, urlParams, Order.class, endpoints.getStoreEndpoint("orderById"));
        assertThat("There is wrong response status! ", response.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat("Response body is not empty", response.getBody(), nullValue());
    }

    @Test
    @DisplayName("Delete not existing Order by id")
    public void deleteNotExistOrderById() {
        Order newOrder = prepareBasicOrder();
        Map<String,Long> urlParams = new HashMap<>();
        urlParams.put("orderId", newOrder.getId());
        ResponseEntity<String> response = restService.getForObject(endpoints.getBaseUrl(), headers, urlParams, String.class, endpoints.getStoreEndpoint("orderById"));
        assertThat("There is wrong response status! ", response.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
        assertThat("Response message is not as expected", response.getBody(), equalTo("Order not found"));
    }

    @Test
    @DisplayName("Delete Order by invalid id")
    public void deleteOrderByInvalidId() {
        Map<String,Object> urlParams = new HashMap<>();
        urlParams.put("orderId", RandomStringUtils.randomAlphanumeric(10));
        ResponseEntity<StoreResponse> response = restService.getForObject(endpoints.getBaseUrl(), headers, urlParams, StoreResponse.class, endpoints.getStoreEndpoint("orderById"));
        assertThat("There is wrong response status! ", response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
        assertThat("There is wrong response status! ", response.getBody().getCode(), equalTo(400));
        assertThat("Response message is not as expected", response.getBody().getMessage(), equalTo("Invalid ID supplied"));
    }

    @Test
    @DisplayName("Get Inventory")
    @Issue("Cleaning of created orders is not implemented")
    public void getInventory() {
        Order.OrderStatuses.getOrderStatusesList().forEach(status ->
        restService.postForObject(endpoints.getBaseUrl(), headers, prepareBasicOrder(status), Order.class, endpoints.getStoreEndpoint("order")));
        ResponseEntity<Map<String, String>> response = restService.getForObject(endpoints.getBaseUrl(), headers, Map.class, endpoints.getStoreEndpoint("inventory"));
        assertThat("There is wrong response status! ", response.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat("Inventory has incorrect approved count", response.getBody(), hasEntry("approved","1"));
        assertThat("Inventory has incorrect placed count", response.getBody(), hasEntry("placed","1"));
        assertThat("Inventory has incorrect delivered count", response.getBody(), hasEntry("delivered","1"));
    }

    private Order prepareBasicOrder(String status){
        Random rnd = new Random();
        return Order.builder().id(rnd.nextLong())
                .complete(true)
                .petId(rnd.nextLong())
                .quantity(rnd.nextInt())
                .status(status)
                .shipDate(new Date())
                .build();
    }

    private Order prepareBasicOrder(){
        return prepareBasicOrder(Order.OrderStatuses.APPROVED.getStatus());
    }
}
