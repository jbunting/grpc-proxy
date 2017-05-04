package io.grpc.examples.experimental.proxy;

import java.util.Map;

/**
 * TODO: Document this class
 */
public class MapResponse {
    private final Map<String, String> map;

    public MapResponse(Map<String, String> map) {
        this.map = map;
    }

    public Map<String, String> getMap() {
        return map;
    }
}
