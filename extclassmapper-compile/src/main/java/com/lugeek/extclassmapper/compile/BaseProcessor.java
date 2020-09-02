package com.lugeek.extclassmapper.compile;

import java.util.Map;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import static com.lugeek.extclassmapper.annotations.Consts.MODULE_ARGUMENT;


public abstract class BaseProcessor extends AbstractProcessor {

    protected Types mTypeUtils;
    protected Elements mElementUtils;
    protected Filer mFiler;
    protected Messager mMessager;

    protected String moduleName;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        mTypeUtils = processingEnv.getTypeUtils();
        mElementUtils = processingEnv.getElementUtils();
        mFiler = processingEnv.getFiler();
        mMessager = processingEnv.getMessager();

        /**
         * 参数是模块名 为了防止多模块/组件化开发的时候 生成相同的 xx_ROOT_文件
         * defaultConfig{
         *     javaCompileOptions {
         *          annotationProcessorOptions {
         *               arguments = [moduleName: project.getName()]
         *          }
         *     }
         * }
         */
        Map<String, String> options = processingEnvironment.getOptions();
        if (options != null && !options.isEmpty()) {
            moduleName = options.get(MODULE_ARGUMENT);
        }
        if (moduleName == null || moduleName.isEmpty()) {
            throw new RuntimeException("Not set processor '" + MODULE_ARGUMENT + "' option !");
        } else {
            moduleName = moduleName.replaceAll("[^0-9a-zA-Z_]+", "");
        }
    }

    protected void info(String msg) {
        mMessager.printMessage(Diagnostic.Kind.OTHER, "✅ [ExtClassMapper-AnnotationProcess] " + msg);
    }

    protected void error(String msg) {
        mMessager.printMessage(Diagnostic.Kind.ERROR, "❎ [ExtClassMapper-AnnotationProcess] " + msg);
    }
}
