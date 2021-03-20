package com.gigi.crumbs.context;

import com.gigi.crumbs.annotation.CrumbsApplication;

import java.util.Arrays;

public class CrumbsApp {
    public static CrumbsContext run(Class<?> clazz) {
        if(Arrays.stream(clazz.getAnnotations())
                .noneMatch(annotation -> annotation.annotationType().equals(CrumbsApplication.class))) {
            throw new RuntimeException("Class is not @CrumbsApplication annotated");
        }
        CrumbsContext context = new CrumbsContext();
        context.initialize(clazz);
        return context;
    }
}
