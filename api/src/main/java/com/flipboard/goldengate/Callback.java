package com.flipboard.goldengate;

/**
 * Callback used to get results from a javascript call
 *
 * @param <T> The return type of the javascript call
 */
public interface Callback<T> {
    void onResult(T result);
}
