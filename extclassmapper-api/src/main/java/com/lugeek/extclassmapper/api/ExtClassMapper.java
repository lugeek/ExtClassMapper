package com.lugeek.extclassmapper.api;

import android.app.Application;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.lugeek.extclassmapper.api.template.IExtClassMapperLoader;
import com.lugeek.extclassmapper.api.utils.ClassUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.lugeek.extclassmapper.annotations.Consts.NAME_OF_ROOT;
import static com.lugeek.extclassmapper.annotations.Consts.PACKAGE_OF_GENERATE_FILE;


public class ExtClassMapper {

    private static final String TAG = "ExtRouter";

    public static Map<String, Class<?>> targetsIndex = new HashMap<>();

    private Handler mainHandler = new Handler(Looper.getMainLooper());

    private static ExtClassMapper sInstance;

    public static ExtClassMapper getsInstance() {
        synchronized (ExtClassMapper.class) {
            if (sInstance == null) {
                sInstance = new ExtClassMapper();
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
        Set<String> routerMap = ClassUtils.getFileNameByPackageName(context, PACKAGE_OF_GENERATE_FILE);
        for (String className : routerMap) {
            if (className.startsWith(PACKAGE_OF_GENERATE_FILE + "." + NAME_OF_ROOT)) {
                ((IExtClassMapperLoader) Class.forName(className).getConstructor().newInstance()).loadInto(targetsIndex);
            }
        }
        for (Map.Entry<String, Class<?>> stringClassEntry : targetsIndex.entrySet()) {
            Log.d(TAG, "Root映射表[ " + stringClassEntry.getKey() + " : " + stringClassEntry.getValue() + "]");
        }
    }

    private Object findArgByClassType(Class<?> clz, Object[] args) {
        if (args == null || args.length <= 0) return null;
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg == null) continue;
            if (clz == arg.getClass() || clz.isAssignableFrom(arg.getClass())) {
                return arg;
            }
        }
        return null;
    }

    private Object[] findArgsByClassType(Class<?>[] classes, Object[] args) {
        if (args == null || args.length <= 0) return new Object[0];
        if (classes == null || classes.length <= 0) return new Object[0];
        Object[] newArgs = new Object[classes.length];
        for (int i = 0; i < classes.length; i++) {
            Class<?> type = classes[i];
            Object arg = findArgByClassType(type, args);
            if (arg == null) {
                throw new IllegalArgumentException("can not find matched parameter");
            }
            newArgs[i] = arg;
        }
        return newArgs;
    }

    public Class<?> getClz(String target) {
        return targetsIndex.get(target);
    }

}
