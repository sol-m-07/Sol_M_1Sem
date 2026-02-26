package com.example.todolist.config;

import com.example.todolist.repository.TaskRepository;
import com.example.todolist.service.TaskService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * BeanPostProcessor, логирующий создание и инициализацию бинов TaskService и TaskRepository.
 */
@Component
public class TaskLifecycleProcessor implements BeanPostProcessor {

    private static final Logger log = LoggerFactory.getLogger(TaskLifecycleProcessor.class);

    @Override
    public Object postProcessBeforeInitialization(@NonNull Object bean, @NonNull String beanName)
            throws BeansException {
        if (bean instanceof TaskService || bean instanceof TaskRepository) {
            log.info("[Lifecycle] Before initialization: bean={}, class={}",
                    beanName, bean.getClass().getSimpleName());
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName)
            throws BeansException {
        if (bean instanceof TaskService || bean instanceof TaskRepository) {
            log.info("[Lifecycle] After initialization: bean={}, class={}",
                    beanName, bean.getClass().getSimpleName());
        }
        return bean;
    }
}
