package com.gigi.test;

import com.gigi.crumbs.annotation.CrumbsApplication;
import com.gigi.crumbs.context.CrumbsApp;
import com.gigi.crumbs.context.CrumbsContext;
import org.junit.Test;

@CrumbsApplication
public class TestApp {

    @Test
    public void shouldNotThrowNullPointer() {
        CrumbsContext context = CrumbsApp.run(TestApp.class);
        Crumb2 crumb2 = (Crumb2) context.getCrumb(Crumb2.class);
        Crumb1 crumb1 = (Crumb1) context.getCrumb(Crumb1.class);
        crumb2.callCrumb1();
        crumb1.callCrumb2();
    }
}
