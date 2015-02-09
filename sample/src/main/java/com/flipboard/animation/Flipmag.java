package com.flipboard.animation;

import com.flipboard.Bridge;
import com.flipboard.Callback;
import com.flipboard.Debug;
import com.flipboard.Property;

@Debug
@Bridge
public interface Flipmag {

    void alert(String message);

    @Property("window.innerWidth")
    void getWindowWidth(Callback<Float> innerWidth);

}
