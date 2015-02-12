package com.flipboard.goldengate.sample;

import com.flipboard.goldengate.Bridge;
import com.flipboard.goldengate.Callback;
import com.flipboard.goldengate.Method;
import com.flipboard.goldengate.Property;

@Bridge
public interface Test {

    void getter(Callback<String> callback);

    void getterWithParam(int param, Callback<String> callback);

    void multiParamSetter(int param1, int param2);

    @Property
    void propertySetter(int param);

    @Property
    void propertyGetter(Callback<String> callback);

    @Property("otherName")
    void propertyGetterWithChangedName(Callback<String> callback);

    @Method("otherName")
    void methodWithChangedName(int param);

    @Method("otherName")
    void noParamMethodWithChangedName(Callback<String> callback);

}