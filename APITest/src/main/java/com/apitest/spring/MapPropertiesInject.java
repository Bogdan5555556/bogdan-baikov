package com.apitest.spring;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MapPropertiesInject {
    String value();
}
