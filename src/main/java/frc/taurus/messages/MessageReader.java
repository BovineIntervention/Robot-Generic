package frc.taurus.messages;

import java.nio.ByteBuffer;
import java.util.Optional;

public abstract class MessageReader<T> {

    GenericQueue<ByteBuffer> mQueue;
    GenericReader<ByteBuffer> mReader;

    public MessageReader(GenericQueue<ByteBuffer> queue) {
        mQueue = queue;
        mReader = new GenericReader<ByteBuffer>(mQueue);
    }
    
    abstract Optional<T> read();
    abstract Optional<T> readLast();


    /**
     * Get current size of queue
     * @return numer of elements not yet read out of queue
     */
    public int size() {
        return mReader.size();
    }

    /**
     * Check if queue is empty
     * @return true if all elements have been read out of queue
     */
    public boolean isEmpty() {
        return mReader.isEmpty();
    }

}