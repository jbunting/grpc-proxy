package io.grpc.examples.experimental.proxy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Document this class
 */
public class MultiResponse implements Serializable {
    private final List<HelloRequest> list;

    public MultiResponse(List<HelloRequest> list) {
        this.list = new ArrayList<>(list);
    }

    public List<HelloRequest> getList() {
        return list;
    }
}
