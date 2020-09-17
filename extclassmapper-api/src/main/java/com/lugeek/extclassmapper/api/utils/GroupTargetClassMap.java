package com.lugeek.extclassmapper.api.utils;

import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * Merge target with same group and check duplicated target in same group when put value to Map<Group, Map<Target, Class>>.
 */
public class GroupTargetClassMap extends HashMap<String, Map<String, Class<?>>> {

    @Nullable
    @Override
    public Map<String, Class<?>> put(String key, Map<String, Class<?>> value) {
        Map<String, Class<?>> oldValue = super.put(key, value);
        if (oldValue != null) {
            for(Map.Entry<String, Class<?>> entry : oldValue.entrySet()) {
                Class<?> duplicatedTargetClass = value.put(entry.getKey(), entry.getValue());
                if (duplicatedTargetClass != null) {
                    throw new IllegalArgumentException("ExtClassMapper annotate duplicated target '"
                            + entry.getKey() + "' for group '" + key +  "' in class '" + entry.getValue().getName()
                            + "' and '" + duplicatedTargetClass.getName() + "'.");
                }
            }
        }
        return oldValue;
    }
}
