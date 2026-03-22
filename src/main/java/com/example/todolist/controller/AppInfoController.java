package com.example.todolist.controller;

import com.example.todolist.config.PrototypeScopedBean;
import com.example.todolist.config.RequestScopedBean;
import com.example.todolist.service.AppInfoService;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Контроллер для демонстрации @Value (app info) и scope бинов (request, prototype).
 */
@RestController
@RequestMapping("/api")
public class AppInfoController {

    private final AppInfoService appInfoService;
    private final RequestScopedBean requestScopedBean;
    private final ObjectFactory<PrototypeScopedBean> prototypeScopedBeanFactory;

    public AppInfoController(AppInfoService appInfoService,
                             RequestScopedBean requestScopedBean,
                             ObjectFactory<PrototypeScopedBean> prototypeScopedBeanFactory) {
        this.appInfoService = appInfoService;
        this.requestScopedBean = requestScopedBean;
        this.prototypeScopedBeanFactory = prototypeScopedBeanFactory;
    }

    @GetMapping("/info")
    public Map<String, Object> getInfo() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("app", appInfoService.getAppInfo());
        result.put("requestId", requestScopedBean.getRequestId());
        result.put("requestStartTime", requestScopedBean.getRequestStartTime().toString());
        result.put("newTaskIdFromPrototype", prototypeScopedBeanFactory.getObject().getTaskId());
        return result;
    }
}
