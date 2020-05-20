package frc.taurus.messages;

import java.nio.ByteBuffer;
import java.util.Optional;

import com.google.flatbuffers.FlatBufferBuilder;

public class JoystickStatusQueue2 extends MessageQueue2<JoystickStatus> {

    static final int size = 32;

    public JoystickStatusQueue2() {
        super(size);
    }

    public void writeMessage(FlatBufferBuilder builder, int offset) {
        JoystickStatus.finishJoystickStatusBuffer(builder, offset);
        ByteBuffer bb = builder.dataBuffer();
        this.write(bb);//.write(bb);
    }

    public JoystickStatusReader2 makeMessageReader() {
        return new JoystickStatusReader2(this);
    }



    public class JoystickStatusReader2 extends MessageQueue2<JoystickStatus>.MessageReader2 {

        private JoystickStatusReader2(JoystickStatusQueue2 queue) {
            super(queue);
        }
    
        public Optional<JoystickStatus> readNextMessage() {
            Optional<ByteBuffer> obb = read();
            if (obb.isEmpty()) {
                return Optional.empty();
            }
            var out = JoystickStatus.getRootAsJoystickStatus(obb.get());
            return Optional.of(out);
        }
    
        public Optional<JoystickStatus> readLastMessage() {
            Optional<ByteBuffer> obb = readLast();
            if (obb.isEmpty()) {
                return Optional.empty();
            }
            var out = JoystickStatus.getRootAsJoystickStatus(obb.get());
            return Optional.of(out);
        }    
    }    
}