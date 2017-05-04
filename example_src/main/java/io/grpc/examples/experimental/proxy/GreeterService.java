package io.grpc.examples.experimental.proxy;

import io.grpc.proxy.annotation.GrpcMethod;
import io.grpc.proxy.annotation.GrpcService;

@GrpcService
public interface GreeterService {

	@GrpcMethod
	public HelloResponse hello(HelloRequest request1, HelloRequest request2);

	@GrpcMethod
	public String helloMore(String request1, String request2);

	@GrpcMethod
	public MultiResponse helloList(String request1, String request2);

	@GrpcMethod
	public MapResponse helloMap(String request1, String request2);

}
