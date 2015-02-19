package com.flipboard.goldengate;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;

public class BridgeProperty {

    public final String javaName;
    public final String name;
    public final BridgeParameter parameter;
    public final BridgeCallback callback;

    public BridgeProperty(ExecutableElement e) {
        this.javaName = e.getSimpleName().toString();

        String name = e.getAnnotation(Property.class).value();
        VariableElement param = e.getParameters().get(0);
        this.name = "".equals(name) ? param.getSimpleName().toString() : name;

        if (Util.isCallback(param)) {
            parameter = null;
            callback = new BridgeCallback(param);
        } else {
            parameter = new BridgeParameter(param);
            callback = null;
        }
    }

    public MethodSpec toMethodSpec(BridgeInterface bridge) {
        MethodSpec.Builder methodSpec = MethodSpec.methodBuilder(javaName).addModifiers(Modifier.PUBLIC);

        if (callback != null) {
            methodSpec.addParameter(TypeName.get(callback.type), callback.name, Modifier.FINAL);
            methodSpec.addCode(CodeBlock.builder()
                    .addStatement("$T id = receiverIds.incrementAndGet()", long.class)
                    .add("this.resultBridge.registerCallback(id, new Callback<$T>() {\n", String.class)
                    .indent()
                    .add("@$T\n", Override.class)
                    .add("public void onResult(String result) {\n")
                    .indent()
                    .addStatement("$N.onResult(fromJson(result, $T.class))", callback.name, callback.genericType)
                    .unindent()
                    .add("}\n")
                    .unindent()
                    .addStatement("})")
                    .build());

            CodeBlock.Builder codeBlock = CodeBlock.builder();
            codeBlock.addStatement("$T javascript = \"$L.onResult(JSON.stringify({receiver:\"+id+\", result:JSON.stringify($L)}));\"", String.class, bridge.name, name);
            if (bridge.isDebug) {
                codeBlock.addStatement("android.util.Log.d($S, javascript)", bridge.name);
            }
            codeBlock.addStatement("this.webView.loadUrl(\"javascript:\" + javascript)");
            methodSpec.addCode(codeBlock.build());
        } else {
            methodSpec.addParameter(TypeName.get(parameter.type), parameter.name, Modifier.FINAL);
            CodeBlock.Builder codeBlock = CodeBlock.builder();
            codeBlock.addStatement("$T javascript = \"$L = \"+toJson($L)+\";\"", String.class, name, parameter.name);
            if (bridge.isDebug) {
                codeBlock.addStatement("android.util.Log.d($S, javascript)", bridge.name);
            }
            codeBlock.addStatement("this.webView.loadUrl(\"javascript:\" + javascript)");
            methodSpec.addCode(codeBlock.build());
        }

        return methodSpec.build();
    }

}
