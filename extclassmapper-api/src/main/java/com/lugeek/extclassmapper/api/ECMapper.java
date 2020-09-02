package com.lugeek.extclassmapper.api;

import android.app.Application;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.lugeek.extclassmapper.annotations.Consts;
import com.lugeek.extclassmapper.api.template.IExtClassMapperLoader;
import com.lugeek.extclassmapper.api.utils.ClassUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ECMapper {

    private static final String TAG = "ExtClassMapper";

    public static Map<String, Class<?>> targetsIndex = new HashMap<>();

    private Handler mainHandler = new Handler(Looper.getMainLooper());

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
        for (Map.Entry<String, Class<?>> stringClassEntry : targetsIndex.entrySet()) {
            Log.d(TAG, "Root映射表[ " + stringClassEntry.getKey() + " : " + stringClassEntry.getValue() + "]");
        }
    }

    public Class<?> getClz(String target) {
        return targetsIndex.get(target);
    }

}
