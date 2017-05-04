package io.grpc.proxy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import io.grpc.MethodDescriptor.Marshaller;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

public class ResponseMarshaller<T> implements Marshaller<T> {

	private final Class<T> type;
	private final Schema<T> respSchema;

	public ResponseMarshaller(final Class<T> type) {
		this.type = type;
		this.respSchema = RuntimeSchema.getSchema(type);
	}

	@Override
	public InputStream stream(T value) {
		ByteArrayOutputStream outputstream = new ByteArrayOutputStream();
		LinkedBuffer writeBuffer1 = LinkedBuffer.allocate(1000000);
		try {
			ProtobufIOUtil.writeTo(outputstream, value, respSchema, writeBuffer1);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		byte[] arr = outputstream.toByteArray();
		InputStream messageIs = new ByteArrayInputStream(arr);
		return messageIs;
	}

	@Override
	public T parse(InputStream stream) {
		T response = respSchema.newMessage();
		try {
			ProtobufIOUtil.mergeFrom(stream, response, respSchema);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return response;
	}
}
