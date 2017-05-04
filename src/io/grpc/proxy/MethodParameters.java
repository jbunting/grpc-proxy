package io.grpc.proxy;

import java.util.List;

/**
 * TODO: Document this class
 */
public class MethodParameters {
    private final List<Object> params;


    public MethodParameters(List<Object> params) {
        this.params = params;
    }

    public List<Object> getParams() {
        return params;
    }

    public int getLength() {
        return params.size();
    }
}
