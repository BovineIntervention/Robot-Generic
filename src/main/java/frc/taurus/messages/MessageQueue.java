package frc.taurus.messages;

import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.function.Function;

import com.google.flatbuffers.FlatBufferBuilder;



public class MessageQueue<T> extends GenericQueue<ByteBuffer> {

    Function<ByteBuffer,T> mGetRootAs;

    public MessageQueue(final int size, Function<ByteBuffer,T> getRootAs) {
        super(size);
        mGetRootAs = getRootAs;
    }


    public void writeMessage(FlatBufferBuilder builder, int offset) {
        builder.finish(offset);
        ByteBuffer bb = builder.dataBuffer();
        this.write(bb);
    }

    public MessageQueue<T>.MessageReader makeMessageReader() {
        return new MessageReader(this);
    }



    public class MessageReader extends GenericQueue<ByteBuffer>.QueueReader {

        protected MessageReader(MessageQueue<T> queue) {
            super(queue);
        }
        
        public Optional<T> readNextMessage() {
            Optional<ByteBuffer> obb = read();
            if (obb.isEmpty()) {
                return Optional.empty();
            }
            ByteBuffer bb = obb.get();
            var out = mGetRootAs.apply(bb);
            return Optional.of(out);
        }
    
        public Optional<T> readLastMessage() {
            Optional<ByteBuffer> obb = readLast();
            if (obb.isEmpty()) {
                return Optional.empty();
            }
            ByteBuffer bb = obb.get();
            var out = mGetRootAs.apply(bb);
            return Optional.of(out);
        }            
    }    
}