package frc.taurus.messages;

import java.nio.ByteBuffer;
import java.util.Optional;

public class JoystickStatusReader extends MessageReader<JoystickStatus> {

    public JoystickStatusReader(GenericQueue<ByteBuffer> queue) {
        super(queue);
    }

    public Optional<JoystickStatus> read() {
        Optional<ByteBuffer> obb = mReader.read();
        if (obb.isEmpty()) {
            return Optional.empty();
        }
        var out = JoystickStatus.getRootAsJoystickStatus(obb.get());
        return Optional.of(out);
    }

    public Optional<JoystickStatus> readLast() {
        Optional<ByteBuffer> obb = mReader.readLast();
        if (obb.isEmpty()) {
            return Optional.empty();
        }
        var out = JoystickStatus.getRootAsJoystickStatus(obb.get());
        return Optional.of(out);
    }    
}