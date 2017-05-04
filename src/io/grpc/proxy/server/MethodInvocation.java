package io.grpc.proxy.server;

import io.grpc.proxy.MethodParameters;
import io.grpc.stub.ServerCalls.UnaryMethod;
import io.grpc.stub.StreamObserver;

import java.lang.reflect.Method;


public class MethodInvocation implements UnaryMethod<MethodParameters,Object> {
	private final Object serviceToInvoke;
	private final Method method;
	
	public MethodInvocation(Object serviceToInvoke, Method method) {
		this.serviceToInvoke = serviceToInvoke;
		this.method = method;
	}

	@Override
	public  void invoke(MethodParameters parameters, StreamObserver<Object> responseObserver) {
		if (method.getParameterTypes().length != parameters.getLength()) {
			return;
		}
		try {
			Object[] requestParams  = parameters.getParams().toArray(new Object[parameters.getLength()]);
			Object returnObj = method.invoke(serviceToInvoke, requestParams);
			responseObserver.onNext(returnObj);
		} catch(Exception ex) {
			responseObserver.onError(ex);
		} finally {
			responseObserver.onCompleted();
		}
	}

	

}
