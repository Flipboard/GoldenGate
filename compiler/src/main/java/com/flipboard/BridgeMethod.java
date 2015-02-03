package com.flipboard;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;

import java.util.ArrayList;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;

public class BridgeMethod {

    public final String javaName;
    public final String name;
    public final ArrayList<BridgeParameter> parameters = new ArrayList<>();
    public final BridgeCallback callback;

    public BridgeMethod(ExecutableElement e) {
        this.javaName = e.getSimpleName().toString();
        this.name = e.getAnnotation(Method.class) != null ? e.getAnnotation(Method.class).value() : javaName;

        for (int i = 0; i < e.getParameters().size() - 1; i++) {
            VariableElement param = e.getParameters().get(i);
            parameters.add(new BridgeParameter(param));
        }

        if (e.getParameters().size() > 0) {
            VariableElement lastParam = e.getParameters().get(e.getParameters().size() - 1);
            if (Util.isCallback(lastParam)) {
                callback = new BridgeCallback(lastParam);
            } else {
                callback = null;
                parameters.add(new BridgeParameter(lastParam));
            }
        } else {
            callback = null;
        }
    }

    public MethodSpec toMethodSpec(String bridgeName) {
        MethodSpec.Builder methodSpec = MethodSpec.methodBuilder(javaName).addModifiers(Modifier.PUBLIC);
        for (int i = 0; i < parameters.size(); i++) {
            methodSpec.addParameter(TypeName.get(parameters.get(i).type), parameters.get(i).name, Modifier.FINAL);
        }

        if (callback != null) {
            methodSpec.addParameter(TypeName.get(callback.type), callback.name, Modifier.FINAL);
            methodSpec.addCode(CodeBlock.builder()
                    .addStatement("$T uuid = randomUUID()", String.class)
                    .add("this.resultBridge.registerCallback(uuid, new Callback<String>() {\n")
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
        }

        if (parameters.size() > 0) {
            String parameterList = "";
            for (int i = 0; i < parameters.size(); i++) {
                BridgeParameter parameter = parameters.get(i);
                if (i < parameters.size()) {
                    parameterList += "toJson(" + parameter.name + ")";
                }
                if (i < parameters.size()-1) {
                    parameterList += " + \", \" + ";
                }
            }

            if (callback == null) {
                methodSpec.addCode(CodeBlock.builder()
                        .addStatement("this.webView.loadUrl(\"javascript:$L(\"+$L+\");\")", name, parameterList)
                        .build());
            } else {
                methodSpec.addCode(CodeBlock.builder()
                        .addStatement("this.webView.loadUrl(\"javascript:$L.onResult(JSON.stringify({receiver:\"+uuid+\", result:$L(\"+$L+\")}));\")", bridgeName, name, parameterList)
                        .build());
            }
        } else {
            if (callback == null) {
                methodSpec.addCode(CodeBlock.builder()
                        .addStatement("this.webView.loadUrl(\"javascript:$L();\")", name)
                        .build());
            } else {
                methodSpec.addCode(CodeBlock.builder()
                        .addStatement("this.webView.loadUrl(\"javascript:$L.onResult(JSON.stringify({receiver:\"+uuid+\", result:$L()});\")", bridgeName, name)
                        .build());
            }
        }

        return methodSpec.build();
    }

}
