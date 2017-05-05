package io.grpc.proxy;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * TODO: Document this class
 */
public interface Serialization {

    Serialization DEFAULT = new ProtostuffSerialization();

    <T> int serialize(final OutputStream out, final T message, final Schema<T> schema, final LinkedBuffer buffer)
            throws IOException;

    <T> int deserialize(InputStream in, T message, Schema<T> schema)
            throws IOException;

    class ProtostuffSerialization implements Serialization {
        @Override
        public <T> int serialize(OutputStream out, T message, Schema<T> schema, LinkedBuffer buffer) throws IOException {
            return ProtostuffIOUtil.writeDelimitedTo(out, message, schema, buffer);
        }

        @Override
        public <T> int deserialize(InputStream in, T message, Schema<T> schema) throws IOException {
            return ProtostuffIOUtil.mergeDelimitedFrom(in, message, schema);
        }
    }

    class ProtobufSerialization implements Serialization {
        @Override
        public <T> int serialize(OutputStream out, T message, Schema<T> schema, LinkedBuffer buffer) throws IOException {
            return ProtobufIOUtil.writeDelimitedTo(out, message, schema, buffer);
        }

        @Override
        public <T> int deserialize(InputStream in, T message, Schema<T> schema) throws IOException {
            return ProtobufIOUtil.mergeDelimitedFrom(in, message, schema);
        }
    }
}
