package com.lugeek.extclassmapper.api.template;

import java.util.Map;

public interface IExtClassMapperLoader {
    void loadInto(Map<String, Class<?>> targets);
}
