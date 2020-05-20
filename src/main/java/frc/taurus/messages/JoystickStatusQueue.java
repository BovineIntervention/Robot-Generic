package frc.taurus.messages;

import java.nio.ByteBuffer;

import com.google.flatbuffers.FlatBufferBuilder;

public class JoystickStatusQueue extends MessageQueue<JoystickStatus> {

    static final int size = 32;

    public JoystickStatusQueue() {
        super(size);
    }

    public void write(FlatBufferBuilder builder, int offset) {
        JoystickStatus.finishJoystickStatusBuffer(builder, offset);
        ByteBuffer bb = builder.dataBuffer();
        mQueue.write(bb);
    }
}