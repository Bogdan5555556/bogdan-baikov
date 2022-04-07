package com.apitest.config;

import com.apitest.spring.MapPropertiesInject;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Data
public class EndpointProperties {
    @Value("${endpoints.baseUrl}")
    private String baseUrl;

    @MapPropertiesInject("endpoints.pets")
    private Map<String, String> petsEndpoints;

    @MapPropertiesInject("endpoints.store")
    private Map<String, String> storeEndpoints;

    @MapPropertiesInject("endpoints.user")
    private Map<String, String> userEndpoints;

    public String getPetEndpoint(String key){
        return petsEndpoints.get(key);
    }

    public String getStoreEndpoint(String key){
        return storeEndpoints.get(key);
    }

    public String getUserEndpoint(String key){
        return userEndpoints.get(key);
    }
}
