package io.grpc.proxy.client;

import io.grpc.CallOptions;
import io.grpc.ClientCall;
import io.grpc.ManagedChannel;
import io.grpc.MethodDescriptor;
import io.grpc.MethodDescriptor.MethodType;
import io.grpc.proxy.MethodParameters;
import io.grpc.proxy.MethodParametersMarshaller;
import io.grpc.proxy.ResponseMarshaller;
import io.grpc.stub.ClientCalls;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;

public class MethodCallProxyHandler implements InvocationHandler {

	private final ManagedChannel channel;
	private final String interfaceName;

	public MethodCallProxyHandler(ManagedChannel channel, String interfaceName) {
		this.channel = channel;
		this.interfaceName = interfaceName;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		  String fullMethodName = interfaceName + "/" + method.getName();
		
	      MethodDescriptor<MethodParameters, Object> methodDescriptor = MethodDescriptor.create(MethodType.UNARY, fullMethodName,
	    				new MethodParametersMarshaller(method.getParameterTypes()), new ResponseMarshaller(method.getReturnType()));
	      ClientCall<MethodParameters, Object> newCall = channel.newCall(methodDescriptor, CallOptions.DEFAULT);
	      
	      Object response = ClientCalls.blockingUnaryCall(newCall, new MethodParameters(args == null || args.length == 0 ? Collections.emptyList() : Arrays.asList(args)));
	      return response;
	}

	

}
