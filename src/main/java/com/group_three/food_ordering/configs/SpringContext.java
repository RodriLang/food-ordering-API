package com.group_three.food_ordering.configs;

import lombok.NonNull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringContext implements ApplicationContextAware {

    private static final Object LOCK = new Object();
    private static ApplicationContext context;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        synchronized (LOCK) {
            if (SpringContext.context == null) {
                SpringContext.context = applicationContext;
            }
        }
    }

    public static <T> T getBean(Class<T> beanClass) {
        synchronized (LOCK) {
            return context.getBean(beanClass);
        }
    }
}

