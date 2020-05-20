package frc.taurus.messages;

import java.nio.ByteBuffer;
import java.util.Optional;

import com.google.flatbuffers.FlatBufferBuilder;



public abstract class MessageQueue2<T> extends GenericQueue<ByteBuffer> {

    public MessageQueue2(final int size) {
        super(size);
    }

    public abstract void writeMessage(FlatBufferBuilder builder, int offset);
    
    public abstract MessageQueue2<T>.MessageReader2 makeMessageReader();



    public abstract class MessageReader2 extends GenericQueue<ByteBuffer>.QueueReader {

        // MessageQueue2<T> mQueue;

        protected MessageReader2(MessageQueue2<T> queue) {
            super(queue);
            // mQueue = queue;
        }
        
        public abstract Optional<T> readNextMessage();
        public abstract Optional<T> readLastMessage();
    }    
}