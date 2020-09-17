package com.lugeek.extclassmapper.api;

import android.app.Application;
import android.content.pm.PackageManager;
import android.util.Log;

import com.lugeek.extclassmapper.annotations.Consts;
import com.lugeek.extclassmapper.api.template.IExtClassMapperLoader;
import com.lugeek.extclassmapper.api.utils.ClassUtils;
import com.lugeek.extclassmapper.api.utils.GroupTargetClassMap;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;

public class ECMapper {

    private static final String TAG = "ExtClassMapper";

    private static GroupTargetClassMap targetsIndex = new GroupTargetClassMap();

    private static ECMapper sInstance;

    public static ECMapper getsInstance() {
        synchronized (ECMapper.class) {
            if (sInstance == null) {
                sInstance = new ECMapper();
            }
        }
        return sInstance;
    }

    public static void init(Application application) {
        try {
            loadInfo(application);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "初始化失败!", e);
        }
    }

    private static void loadInfo(Application context) throws PackageManager.NameNotFoundException, InterruptedException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, IOException {
        Set<String> routerMap = ClassUtils.getFileNameByPackageName(context, Consts.PACKAGE_OF_GENERATE_FILE);
        for (String className : routerMap) {
            if (className.startsWith(Consts.PACKAGE_OF_GENERATE_FILE + "." + Consts.NAME_OF_ROOT)) {
                ((IExtClassMapperLoader) Class.forName(className).getConstructor().newInstance()).loadInto(targetsIndex);
            }
        }
        for (Map.Entry<String, Map<String, Class<?>>> stringClassEntry : targetsIndex.entrySet()) {
            Map<String, Class<?>> groupMap = stringClassEntry.getValue();
            for (Map.Entry<String, Class<?>> targetEntry : groupMap.entrySet()) {
                Log.d(TAG, "Root映射表[ " + stringClassEntry.getKey() + " : " + targetEntry.getKey() + " : " + targetEntry.getValue() + "]");
            }
        }
    }

    public Set<String> groupSet() {
        return targetsIndex.keySet();
    }

    public Map<String, Class<?>> getGroup(String group) {
        return targetsIndex.get(group);
    }

    public Class<?> getClz(String target) {
        return getClz("default", target);
    }

    public Class<?> getClz(String group, String target) {
        Map<String, Class<?>> groupMap = getGroup(group);
        if (groupMap == null) {
            throw new IllegalArgumentException("[ExtClassMapper] group of '" + group + "' is not existed.");
        }
        return groupMap.get(target);
    }

}
