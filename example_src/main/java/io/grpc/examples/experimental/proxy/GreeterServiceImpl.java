package io.grpc.examples.experimental.proxy;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GreeterServiceImpl implements GreeterService {

	@Override
	public HelloResponse hello(HelloRequest request1, HelloRequest request2) {
		System.out.println("request1 :" + request1.getName() + " request2:" + request2.getName());
		
		HelloResponse resultResponse = new HelloResponse();
		resultResponse.setMessage("Hello " + request1.getName() + " and " + request2.getName());
		return resultResponse;
	}

	@Override
	public String helloMore(String request1, String request2) {
		System.out.println("request1 :" + request1 + " request2:" + request2);

		return "Hello " + request1 + " and " + request2;
	}

	@Override
	public MultiResponse helloList(String request1, String request2) {
		System.out.println("request1 :" + request1 + " request2:" + request2);

		return new MultiResponse(Arrays.asList(new HelloRequest("Hello: " + request1), new HelloRequest("Hello: " + request2)));
	}

	@Override
	public MapResponse helloMap(String request1, String request2) {
		System.out.println("request1 :" + request1 + " request2:" + request2);

		final Map<String, String> map = new HashMap<>();

		map.put(request1, "Hello: " + request1);
		map.put(request2, "Hello: " + request2);

		return new MapResponse(map);
	}
}
