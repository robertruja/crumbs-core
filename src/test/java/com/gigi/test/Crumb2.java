package com.gigi.test;

import com.gigi.crumbs.annotation.Crumb;
import com.gigi.crumbs.annotation.CrumbRef;

@Crumb
public class Crumb2 {

    @CrumbRef
    private Crumb1 crumb1;

    public void testCall() {
        System.out.println("Called Crumb 2 test method");
    }

    public void callCrumb1() {
        crumb1.testCall();
    }
}
