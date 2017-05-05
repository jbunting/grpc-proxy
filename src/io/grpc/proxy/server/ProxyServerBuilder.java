package io.grpc.proxy.server;

import io.grpc.MethodDescriptor;
import io.grpc.MethodDescriptor.MethodType;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerServiceDefinition;
import io.grpc.netty.NettyServerBuilder;
import io.grpc.proxy.Communication;
import io.grpc.proxy.MethodParameters;
import io.grpc.proxy.MethodParametersMarshaller;
import io.grpc.proxy.ResponseMarshaller;
import io.grpc.proxy.annotation.GrpcMethod;
import io.grpc.proxy.annotation.GrpcService;
import io.grpc.stub.ServerCalls;
import io.netty.channel.epoll.EpollDomainSocketChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.unix.DomainSocketAddress;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ProxyServerBuilder {
	private final List<Object> serviceList;
	private final Server server;
	@Nonnull
	private final Communication communication;

	public static class Builder {
		private final List<Object> serviceList;
		@Nonnull
		private final Communication communication;


		public Builder(@Nonnull Communication communication) {
			this.communication = communication;
			this.serviceList = new ArrayList<Object>();
		}

		public Builder addService(Object service) {
			List<Class> effectiveClassAnnotations = ReflectionHelper.getEffectiveClassAnnotations(service.getClass(), GrpcService.class);
			if( effectiveClassAnnotations.size() != 1 ) {
				String msg = effectiveClassAnnotations.isEmpty() ? "No Interfaces implementing GrpcService annotation" :
					"More than one interface implementing GRPC annotation";
				throw new IllegalArgumentException(msg);
			}
			this.serviceList.add(service);
			return this;
		}
		
		public ProxyServerBuilder build() throws Exception {
			ProxyServerBuilder proxyManager = new ProxyServerBuilder(this);
			return proxyManager;
		}
		
	}
	
	private ProxyServerBuilder(Builder builder) throws Exception {
		this.serviceList = builder.serviceList;
		this.communication = builder.communication;
		this.server = register();
	}
	
	private  Server register() throws Exception {
		ServerBuilder<?> serverBuilder = communication.createServerBuilder();

		for(Object serviceToInvoke : serviceList) {
			List<Class> effectiveClassAnnotations = ReflectionHelper.getEffectiveClassAnnotations(serviceToInvoke.getClass(), GrpcService.class);
			String  serviceInterfaceName = effectiveClassAnnotations.get(0).getName();
			io.grpc.ServerServiceDefinition.Builder serviceDefBuilder = ServerServiceDefinition
					.builder(serviceInterfaceName);
			
			Collection<Method> findAnnotatedMethods = ReflectionHelper.findAnnotatedMethods(serviceToInvoke.getClass(), GrpcMethod.class);
			for(Method exposedMethod : findAnnotatedMethods) {
				final String methodName = serviceInterfaceName+ "/" + exposedMethod.getName();
				MethodParametersMarshaller reqMarshaller = new MethodParametersMarshaller(exposedMethod.getParameterTypes());

				MethodDescriptor<MethodParameters, Object> methodDescriptor = MethodDescriptor
						.create(MethodType.UNARY, methodName,
								reqMarshaller,
								new ResponseMarshaller(exposedMethod.getReturnType()));
				
				MethodInvocation methodInvokation = new MethodInvocation(serviceToInvoke, exposedMethod);
				serviceDefBuilder.addMethod(methodDescriptor, ServerCalls.asyncUnaryCall(methodInvokation));
			}
			serverBuilder.addService(serviceDefBuilder.build());
		}
		return serverBuilder.build();
	}
	
	public Server startServer() throws IOException {
		return server.start();
	}
	
}
