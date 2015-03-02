package com.flipboard.goldengate.sample;

import com.flipboard.goldengate.Bridge;
import com.flipboard.goldengate.Callback;
import com.flipboard.goldengate.Debug;
import com.flipboard.goldengate.JavascriptCallback;
import com.flipboard.goldengate.Method;
import com.flipboard.goldengate.Property;

@Debug
@Bridge
public interface Sample {

    void alert(String message);

    @Property("window.innerWidth")
    void getWindowWidth(Callback<Float> width);

    @Property("navigator")
    void getNavigator(Callback<Navigator> navigator);

    @Method("[1,2,3,4,5,6,7,8,9,10].forEach")
    void looptyLoop(@JavascriptCallback Callback<Integer> val);

}
