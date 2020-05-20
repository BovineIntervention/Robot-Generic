package frc.taurus.messages;

import java.nio.ByteBuffer;

import com.google.flatbuffers.FlatBufferBuilder;



public abstract class MessageQueue<T> {

    GenericQueue<ByteBuffer> mQueue;
    
    public MessageQueue(final int size) {
        mQueue = new GenericQueue<ByteBuffer>(size);
    }

    abstract void write(FlatBufferBuilder builder, int offset);
}