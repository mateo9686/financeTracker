package com.kedzierski.financetracker.components;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SessionComponent implements Serializable {

    private static final long serialVersionUID = 1L;

    private int pageViews;

    public void setPageViews(int pageViews) {
        this.pageViews = pageViews;
    }

    public int getPageViews() {
        return this.pageViews;
    }
}
