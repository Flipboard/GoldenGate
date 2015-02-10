package com.flipboard.goldengate;

import android.webkit.WebView;

import com.google.gson.reflect.TypeToken;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;

public class BridgeInterface {

    public final String name;
    public final boolean isDebug;
    private TypeMirror type;
    private ArrayList<BridgeMethod> bridgeMethods = new ArrayList<>();
    private ArrayList<BridgeProperty> bridgeProperties = new ArrayList<>();

    public BridgeInterface(Element element) {
        this.name = element.getSimpleName().toString();
        this.isDebug = element.getAnnotation(Debug.class) != null;
        this.type = element.asType();

        for (Element method : element.getEnclosedElements()) {
            if (method.getAnnotation(Property.class) != null) {
                bridgeProperties.add(new BridgeProperty((ExecutableElement) method));
            } else {
                bridgeMethods.add(new BridgeMethod((ExecutableElement) method));
            }
        }
    }

    public void writeToFiler(Filer filer) throws IOException {
        // Build Bridge class
        TypeSpec.Builder bridge = TypeSpec.classBuilder(name + "Bridge")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(TypeName.get(type))
                .superclass(JavaScriptBridge.class)
                .addField(ClassName.get("com.flipboard.goldengate.bridge", name + "Bridge", "ResultBridge"), "resultBridge", Modifier.PRIVATE);

        Type callbacksMapType = new TypeToken<Map<String, Callback<String>>>(){}.getType();
        Type callbackType = new TypeToken<Callback<String>>(){}.getType();

        // Generate the result bridge
        bridge.addType(TypeSpec.classBuilder("ResultBridge")
                .addField(FieldSpec.builder(callbacksMapType, "callbacks")
                        .initializer("new $T<>()", HashMap.class)
                        .build())
                .addMethod(MethodSpec.methodBuilder("registerCallback")
                        .addParameter(String.class, "receiver")
                        .addParameter(callbackType, "cb")
                        .addCode(CodeBlock.builder().addStatement("callbacks.put($N, $N)", "receiver", "cb").build())
                        .build())
                .addMethod(MethodSpec.methodBuilder("onResult")
                        .addAnnotation(ClassName.get("android.webkit", "JavascriptInterface"))
                        .addParameter(String.class, "result")
                        .addCode(CodeBlock.builder()
                                .beginControlFlow("try")
                                    .addStatement("$T $N = new $T($N)", ClassName.get("org.json", "JSONObject"), "json", ClassName.get("org.json", "JSONObject"), "result")
                                    .addStatement("$T $N = $N.getString($S)", String.class, "receiver", "json", "\"receiver\"")
                                    .addStatement("$T $N = $N.get($S).toString()", String.class, "realResult", "json", "\"result\"")
                                    .addStatement("$N.remove($N).onResult($N)", "callbacks", "receiver", "realResult")
                                .nextControlFlow("catch (org.json.JSONException e)")
                                    .addStatement("$N.printStackTrace()", "e")
                                .endControlFlow()
                                .build())
                        .build())
                .build());

        // Add Bridge constructor
        bridge.addMethod(
                MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(WebView.class, "webView")
                        .addStatement("super($N)", "webView")
                        .addStatement("this.$N = new ResultBridge()", "resultBridge")
                        .addStatement("this.$N.addJavascriptInterface($N, $L)", "webView", "resultBridge", "\"" + name + "\"")
                        .build()
        );

        // Add Bridge methods
        for (BridgeMethod method : bridgeMethods) {
            bridge.addMethod(method.toMethodSpec(this));
        }

        // Add Bridge property methods
        for (BridgeProperty property : bridgeProperties) {
            bridge.addMethod(property.toMethodSpec(this));
        }

        // Write source
        JavaFile javaFile = JavaFile.builder("com.flipboard.goldengate.bridge", bridge.build()).build();
        javaFile.writeTo(filer);
    }

}
