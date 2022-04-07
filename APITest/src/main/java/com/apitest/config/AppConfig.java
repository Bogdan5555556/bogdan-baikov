package com.apitest.config;

import com.apitest.spring.MapValueBeanPostProcessor;
import com.apitest.spring.RestTemplateRequestHandlerInterceptor;
import com.apitest.spring.RestTemplateResponseErrorHandler;
import com.apitest.spring.YamlPropertySourceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ComponentScan(basePackages = { "com.apitest.** " })
@PropertySource(value = "classpath:endpoints.yml", factory = YamlPropertySourceFactory.class)
@EnableAspectJAutoProxy
public class AppConfig {

    @Autowired
    Environment env;

    @Bean
    public MapValueBeanPostProcessor mapValueBeanPostProcessor(){
        return new MapValueBeanPostProcessor(env);
    }

    @Bean
    public RestTemplate restTemplate(){
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(responseErrorHandler());
        restTemplate.setRequestFactory(createRequestFactory());
        List<ClientHttpRequestInterceptor> interceptors
                = restTemplate.getInterceptors();
        if (CollectionUtils.isEmpty(interceptors)) {
            interceptors = new ArrayList<>();
        }
        interceptors.add(createInterceptor());
        restTemplate.setInterceptors(interceptors);
        return restTemplate;
    }

    @Bean
    public ResponseErrorHandler responseErrorHandler(){
        return new RestTemplateResponseErrorHandler();
    }

    @Bean
    public SimpleClientHttpRequestFactory createRequestFactory(){
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setOutputStreaming(false);
        return requestFactory;
    }

    @Bean
    public RestTemplateRequestHandlerInterceptor createInterceptor(){
        return new RestTemplateRequestHandlerInterceptor();
    }
}
