package com.lugeek.extclassmapper.compile;

import com.google.auto.service.AutoService;
import com.lugeek.extclassmapper.annotations.ExtClassMapper;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import static com.lugeek.extclassmapper.annotations.Consts.LOADER_INTERFACE_PATH;
import static com.lugeek.extclassmapper.annotations.Consts.NAME_OF_ROOT;
import static com.lugeek.extclassmapper.annotations.Consts.PACKAGE_OF_GENERATE_FILE;


@AutoService(Processor.class)
@SupportedAnnotationTypes({"com.lugeek.extclassmapper.annotations.ExtClassMapper"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class ExtClassMapperProcessor extends BaseProcessor {

    // group: ExtClassMapper_Group_GroupName
    private Map<String, ClassName> rootMap = new HashMap<>();
    // group: [{target,group,class},{target,group,class}]
    private Map<String, List<ExtClassMeta>> groupMap = new HashMap<>();
    private Map<String, ClassName> targetMap = new HashMap<>();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations != null && !annotations.isEmpty()) {
            Set<? extends Element> extClassMapperElements = roundEnv.getElementsAnnotatedWith(ExtClassMapper.class);
            try {
                info("Process ExtClassMapper start ...");
                this.parseExtClassMapper(extClassMapperElements);
            } catch (Exception e) {
                error(e.getMessage());
            }
            return true;
        }
        return false;
    }

    private void parseExtClassMapper(Set<? extends Element> classElements) throws IOException {
        if (classElements == null || classElements.isEmpty()) return;
        info("Found " + classElements.size() + " ExtClassMapper");

        for (Element element : classElements) {
            if (element.getKind() != ElementKind.CLASS) {
                error(element.asType().toString() + " @ExtClassMapper only works for class");
            }
            TypeMirror tm = element.asType();
            info(tm.toString());
            ClassName className = ClassName.get((TypeElement)element);
            ExtClassMapper extClassMapper = element.getAnnotation(ExtClassMapper.class);

            String[] targets = extClassMapper.value();
            if (targets == null || targets.length <= 0) {
                error("ExtClassMappers'value is empty for class: " + className);
            }
            String group = extClassMapper.group();
            if (group == null || group.length() <= 0) {
                error("ExtClassMapper'group is empty for class: " + className);
            }

            for (String target : targets) {
                List<ExtClassMeta> metas = groupMap.get(group);
                if (metas == null) {
                    metas = new ArrayList<>();
                    metas.add(new ExtClassMeta(target, group, className));
                    groupMap.put(group, metas);
                } else {
                    boolean noneDuplicat = metas.stream().noneMatch(m -> m.getTarget().equals(target));
                    if (!noneDuplicat) {
                        ExtClassMeta duplicatedMete = metas.stream().peek(m -> m.getTarget().equals(target)).findFirst().get();
                        error("has duplicated target '" + target + "' for group '" + group + "' in class '"
                                + className.simpleName() + "' and '"
                                + duplicatedMete.getClassName().simpleName() + "'.");
                    }
                    metas.add(new ExtClassMeta(target, group, className));
                }
                targetMap.put(target, className);
            }

        }

        TypeElement iTargetRoot = mElementUtils.getTypeElement(LOADER_INTERFACE_PATH);
        generatedRoot(iTargetRoot);
    }

    /**
     * example:
     * public class ExtClassMapper_Root_app implements IExtClassMapperLoader {
     *   @Override
     *   public void loadInto(Map<String, Class<?>> targets) {
     *     targets.put("test2", Test.class);
     *     targets.put("test", Test.class);
     *     targets.put("main", MainActivity.class);
     *   }
     * }
     *
     */
    private void generatedRoot(TypeElement iTargetRoot) {
        info("generate root file start ...");

        //参数类型 Map<String,Map<String,Class<?>>>
        ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ParameterizedTypeName.get(
                        ClassName.get(Map.class),
                        ClassName.get(String.class),
                        ParameterizedTypeName.get(
                                ClassName.get(Class.class),
                                WildcardTypeName.subtypeOf(ClassName.get(Object.class))
                        )
                )
        );
        //参数 (Map<String,Map<String,Class<?>>> groups)
        ParameterSpec parameter = ParameterSpec.builder(parameterizedTypeName, "groups").build();
        //函数 @Override public void loadInto(Map<String,Map<String,Class<?>>> groups) {}
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("loadInto")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(parameter);

        /**
         * 函数体
         * Map<String,Class<?>> groupMap = null;
         * groupMap = new HashMap<>();
         * groupMap.put("main", MainActivity.class);
         * groups.put("default", groupMap);
         * groupMap = new HashMap<>();
         * groupMap.put("test", Test.class);
         * groupMap.put("test2", Test.class);
         * groups.put("testGroup", groupMap);
         */
        methodBuilder.addStatement("Map<String,Class<?>> groupMap = null");
        for (String groupName : groupMap.keySet()) {
            methodBuilder.addStatement("groupMap = new $T<>()", ClassName.get(HashMap.class));
            for (ExtClassMeta meta : groupMap.get(groupName)) {
                methodBuilder.addStatement("groupMap.put($S, $T.class)", meta.getTarget(), meta.getClassName());
            }
            methodBuilder.addStatement("groups.put($S, groupMap)", groupName);
        }

        //生成$Root$类: public class ExtClassMapper_Root_appxx implements IExtClassMapperLoader {}
        String className = NAME_OF_ROOT + moduleName;
        TypeSpec typeSpec = TypeSpec.classBuilder(className)
                .addSuperinterface(ClassName.get(iTargetRoot))
                .addModifiers(Modifier.PUBLIC)
                .addMethod(methodBuilder.build())
                .build();
        try {
            JavaFile.builder(PACKAGE_OF_GENERATE_FILE, typeSpec)
                    .addFileComment("Generated code from ExtClassMapper. Do not modify!")
                    .build().writeTo(mFiler);
            info(className + ".class generated!");
        } catch (IOException e) {
            error(className + ".class writeToFile fail: " + e.getMessage());
        }
        info("generate root file end ...");
    }

}
