package com.flipboard;

import android.webkit.WebView;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;

public class BridgeInterface {

    private String name;
    private TypeMirror type;
    private ArrayList<BridgeMethod> bridgeMethods = new ArrayList<>();
    private ArrayList<BridgeProperty> bridgeProperties = new ArrayList<>();

    public BridgeInterface(Element element) {
        this.name = element.getSimpleName().toString();
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
                .superclass(JavaScriptBridge.class);

        // Add Bridge constructor
        bridge.addMethod(
                MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(WebView.class, "webView")
                        .addStatement("super($N)", "webView")
                        .addStatement("this.webView.addJavascriptInterface($N, $L)", "resultBridge", "\"" + name + "\"")
                        .build()
        );

        // Add Bridge methods
        for (BridgeMethod method : bridgeMethods) {
            bridge.addMethod(method.toMethodSpec(name));
        }

        // Add Bridge property methods
        for (BridgeProperty property : bridgeProperties) {
            bridge.addMethod(property.toMethodSpec(name));
        }

        // Write source
        JavaFile javaFile = JavaFile.builder("com.flipboard.bridge", bridge.build()).build();
        javaFile.writeTo(filer);
    }

}
