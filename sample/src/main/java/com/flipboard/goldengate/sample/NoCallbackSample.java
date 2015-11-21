package com.flipboard.goldengate.sample;

import com.flipboard.goldengate.Bridge;
import com.flipboard.goldengate.Debug;

/**
 * No callbacks necessary for this interface, so no javascript interface logic will be generated
 */
@Debug
@Bridge
public interface NoCallbackSample {

    void alert(String message);

}
