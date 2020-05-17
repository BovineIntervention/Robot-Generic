package frc.taurus.messages;

import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.function.Function;

import com.google.flatbuffers.FlatBufferBuilder;


public class MessageQueue<T> {
    // double ended queue
    Queue<ByteBuffer> mQueue;
    Function<ByteBuffer,T> mGetRootAs;

    static int defaultQueueDepth = 512;

    public MessageQueue(Function<ByteBuffer,T> getRootAs, 
                        int queueDepth) {
        mGetRootAs = getRootAs;
        mQueue = new ArrayDeque<ByteBuffer>(queueDepth);
    }

    public MessageQueue(Function<ByteBuffer,T> getRootAs) {
        this(getRootAs, defaultQueueDepth);
    }

    // the add() and remove() methods should be synchronized so that
    // multiple threads can access the same queue safely (no collisions)

    // TODO: I'd prefer to pass (builder, offset) and have this function finalize the buffer
    // with finishJoystickStatusBuffer, just to make sure we don't miss that step
    // need to figure out Supplier<> with two arguments, or create a class with those 2 arguments

    public synchronized void add(FlatBufferBuilder builder) {
        ByteBuffer bb = builder.dataBuffer();
        mQueue.add(bb);
    }

    public synchronized T remove() {
        ByteBuffer bb = mQueue.remove();
        return mGetRootAs.apply(bb);
    }


}