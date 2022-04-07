package com.apitest.spring;

import lombok.SneakyThrows;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class MapValueBeanPostProcessor implements BeanPostProcessor {
    private final Environment env;

    public MapValueBeanPostProcessor(Environment env) {
        this.env = env;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Arrays.stream(bean.getClass().getDeclaredFields())
                .filter(field -> field.getDeclaredAnnotation(MapPropertiesInject.class) != null ).forEach(
                        field -> {
                            field.setAccessible(true);
                            String propertyGroup = field.getDeclaredAnnotation(MapPropertiesInject.class).value();
                            try {
                                field.set(bean,resolveEnvSource(propertyGroup));
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
        );
        return bean;
    }

    private Map<String, String> resolveEnvSource(String propertyGroup){
        List<MapPropertySource> listPropertySource= new ArrayList<>();
                ((AbstractEnvironment) env).getPropertySources().stream().filter(i-> i instanceof PropertiesPropertySource).forEach(source -> {
            listPropertySource.add((MapPropertySource) source);
        });
        Map<String,String> resultMap = new HashMap<>();
        listPropertySource.stream().flatMap(source -> Arrays.stream(source.getPropertyNames()))
                .filter(name -> name.startsWith(propertyGroup))
                .forEach(name->{
                    resultMap.put(name.substring(propertyGroup.length() + 1),env.getProperty(name));
                });
                return resultMap;
    }
}
