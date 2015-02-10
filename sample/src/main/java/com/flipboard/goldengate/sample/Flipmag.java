package com.flipboard.goldengate.sample;

import com.flipboard.goldengate.Bridge;
import com.flipboard.goldengate.Callback;
import com.flipboard.goldengate.Debug;
import com.flipboard.goldengate.Property;

@Debug
@Bridge
public interface Flipmag {

    void alert(String message);

    @Property("window.innerWidth")
    void getWindowWidth(Callback<Float> innerWidth);

}
