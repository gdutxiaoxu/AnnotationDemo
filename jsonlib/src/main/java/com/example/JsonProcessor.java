package com.example;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;

/**
 * @author meitu.xujun  on 2017/4/14 18:07
 * @version 0.1
 */

@SupportedAnnotationTypes({"com.example.Seriable"})
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class JsonProcessor extends AbstractProcessor {

    private Elements mElementUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        //  工具辅助类
        mElementUtils = processingEnv.getElementUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        //   第一步，根据我们自定义的注解拿到 elememts set 集合
        Set<? extends Element> elememts = roundEnv.getElementsAnnotatedWith(Seriable.class);
        TypeElement typeElement;
        VariableElement variableElement;
        Map<String, List<VariableElement>> map = new HashMap<>();
        List<VariableElement> fileds = null;
        //  第二步： 根据 element 的类型做相应的处理，并存进 map 集合
        for (Element element : elememts) {
            ElementKind kind = element.getKind();
            // 判断该元素是否为类
            if (kind == ElementKind.CLASS) {
                typeElement = (TypeElement) element;
                //  这里以类的全限定类名作为 key，确保唯一
                String qualifiedName = typeElement.getQualifiedName().toString();
                map.put(qualifiedName, fileds = new ArrayList<VariableElement>());
                // 判断该元素是否为成员变量
            } else if (kind == ElementKind.FIELD) {
                variableElement = (VariableElement) element;
                //                获取该元素的封装类型
                typeElement = (TypeElement) variableElement.getEnclosingElement();
                String qualifiedName = typeElement.getQualifiedName().toString();
                fileds = map.get(qualifiedName);
                if (fileds == null) {
                    map.put(qualifiedName, fileds = new ArrayList<VariableElement>());
                }
                fileds.add(variableElement);
            }
        }

        Set<String> set = map.keySet();

        for (String key : set) {
            if (map.get(key).size() == 0) {
                typeElement = mElementUtils.getTypeElement(key);
                List<? extends Element> allMembers = mElementUtils.getAllMembers(typeElement);
                if (allMembers.size() > 0) {
                    map.get(key).addAll(ElementFilter.fieldsIn(allMembers));
                }
            }
        }
        // 第三步：根据 map 集合数据生成代码
        generateCodes(map);

        return true;
    }

    // 生成我们的代码文件
    private void generateCodes(Map<String, List<VariableElement>> maps) {
        File dir = new File("f://Animation");
        if (!dir.exists())
            dir.mkdirs();
        // 遍历map
        for (String key : maps.keySet()) {

            // 创建文件
            File file = new File(dir, key.replaceAll("\\.", "_") + ".txt");
            try {
                /**
                 * 编写json文件内容
                 */
                FileWriter fw = new FileWriter(file);
                fw.append("{").append("class:").append("\"" + key + "\"")
                        .append(",\n ");
                fw.append("fields:\n {\n");
                List<VariableElement> fields = maps.get(key);

                for (int i = 0; i < fields.size(); i++) {
                    VariableElement field = fields.get(i);
                    fw.append("  ").append(field.getSimpleName()).append(":")
                            .append("\"" + field.asType().toString() + "\"");
                    if (i < fields.size() - 1) {
                        fw.append(",");
                        fw.append("\n");
                    }
                }
                fw.append("\n }\n");
                fw.append("}");
                fw.flush();
                fw.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
