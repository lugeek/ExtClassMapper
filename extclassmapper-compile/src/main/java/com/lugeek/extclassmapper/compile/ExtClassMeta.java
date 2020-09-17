package com.lugeek.extclassmapper.compile;

import com.squareup.javapoet.ClassName;

public class ExtClassMeta {

    private String target;

    private String group;

    private ClassName className;

    public ExtClassMeta(String target, String group, ClassName className) {
        this.target = target;
        this.group = group;
        this.className = className;
    }


    public String getTarget() {
        return target;
    }

    public String getGroup() {
        return group;
    }

    public ClassName getClassName() {
        return className;
    }

}