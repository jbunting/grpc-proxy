package io.grpc.proxy;

import io.grpc.MethodDescriptor;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.apache.commons.lang.Validate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MethodParametersMarshaller implements MethodDescriptor.Marshaller<MethodParameters> {

    private final Schema<Object>[] schemas;

    public MethodParametersMarshaller(Class<?>[] paramTypes) {

        this.schemas = new Schema[paramTypes.length];
        for (int i = 0; i < paramTypes.length; i++) {
            this.schemas[i] = RuntimeSchema.getSchema((Class<Object>) paramTypes[i]);
        }
    }

    @Override
    public InputStream stream(MethodParameters value) {
        System.err.printf("Doin a marshall! %d parameters%n", value.getLength());
        if (value.getLength() != schemas.length) {
            throw new IllegalArgumentException("Got " + value.getLength() + " parameter values, supposed to get " + schemas.length);
        }
        ByteArrayOutputStream outputstream = new ByteArrayOutputStream();
        LinkedBuffer writeBuffer1 = LinkedBuffer.allocate(1000000);
        try {
            for (int i = 0; i < schemas.length; i++) {
                System.err.printf("  marshall param %d%n", i);
                ProtobufIOUtil.writeDelimitedTo(outputstream, value.getParams().get(i), schemas[i], writeBuffer1);
                writeBuffer1.clear();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        byte[] arr = outputstream.toByteArray();
        InputStream messageIs = new ByteArrayInputStream(arr);
        System.err.printf("  Done with the marshall%n");
        return messageIs;

    }

    @Override
    public MethodParameters parse(InputStream stream) {
        System.err.printf("Doin a parse!%n");
        List<Object> parameters = new ArrayList<>();

        for (int i = 0; i < schemas.length; i++) {
            System.err.printf("  parse param %d%n", i);
            Schema<Object> schema = schemas[i];
            Object param = schema.newMessage();
            try {
                ProtobufIOUtil.mergeDelimitedFrom(stream, param, schema);
            } catch (IOException e) {
                throw new RuntimeException("Failed to parse param " + i, e);
            }
            parameters.add(param);
        }

        System.err.printf("  Done wit the pars%n");
        return new MethodParameters(parameters);
    }
}
