package frc.taurus.messages;

import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Queue;

import com.google.flatbuffers.FlatBufferBuilder;


public class MessageQueue {
    // double ended queue
    Queue<ByteBuffer> joystickStatusQueue;

    static int defaultQueueDepth = 512;

    public MessageQueue(int queueDepth) {
        joystickStatusQueue = new ArrayDeque<ByteBuffer>(queueDepth);
    }

    public MessageQueue() {
        this(defaultQueueDepth);
    }    
    
    // the add() and remove() methods should be synchronized so that
    // multiple threads can access the same queue safely (no collisions)
    public synchronized void add(final FlatBufferBuilder builder, final int offset) {
        JoystickStatus.finishJoystickStatusBuffer(builder, offset);
        ByteBuffer bb = builder.dataBuffer();
        joystickStatusQueue.add(bb);
    }

    public synchronized JoystickStatus remove() {
        ByteBuffer bb = joystickStatusQueue.remove();
        return JoystickStatus.getRootAsJoystickStatus(bb);
    }


}