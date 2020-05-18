package frc.taurus.messages;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.Optional;
import java.util.function.Function;

import com.google.flatbuffers.FlatBufferBuilder;

// import org.apache.commons.collections4.queue.CircularFifoQueue;
// see:
// https://commons.apache.org/proper/commons-collections/javadocs/api-4.4/org/apache/commons/collections4/queue/CircularFifoQueue.html
// https://www.baeldung.com/commons-circular-fifo-queue


public class MessageQueue<T> {
    CircularFifoQueue<ByteBuffer> mQueue;
    Function<ByteBuffer,T> mGetRootAs;

    static int initialSize = 512;

    public MessageQueue(Function<ByteBuffer,T> getRootAs, 
                        int queueDepth) {
        mGetRootAs = getRootAs;
        mQueue = new CircularFifoQueue<ByteBuffer>(queueDepth);
    }

    public MessageQueue(Function<ByteBuffer,T> getRootAs) {
        this(getRootAs, initialSize);
    }

    // the following methods should be synchronized so that
    // multiple threads can access the same queue safely (no collisions)

    public synchronized void reset() {
        mQueue.clear();
    }

    // TODO: I'd prefer to pass (builder, offset) and have this function finalize the buffer
    // with finishJoystickStatusBuffer, just to make sure we don't miss that step
    // need to figure out Supplier<> with two arguments, or create a class with those 2 arguments

    public synchronized void writeMessage(FlatBufferBuilder builder) {
        ByteBuffer bb = builder.dataBuffer();
        mQueue.add(bb);
    }

    public synchronized Optional<T> readNextMessage() {
        if (mQueue.isEmpty()) {
            return Optional.empty();
        }
        ByteBuffer bb = mQueue.get(0);      // get first element in queue        
        T element = mGetRootAs.apply(bb);   // convert ByteBuffer to desired message
        return Optional.of(element);        // place desired message in an Optional
    }

    public synchronized Optional<T> readLastMessage() {
        if (mQueue.isEmpty()) {
            return Optional.empty();
        }
        int endIdx = mQueue.size()-1;
        ByteBuffer bb = mQueue.get(endIdx); // get last element in queue 
        T element = mGetRootAs.apply(bb);   // convert ByteBuffer to desired message
        return Optional.of(element);        // place desired message in an Optional
    }

    public synchronized boolean isEmpty() {
        return mQueue.isEmpty();
    }
    public synchronized int size() {
        return mQueue.size();
    }

    public synchronized int maxSize() {
        return mQueue.maxSize();
    }

    public QueueReader makeReader() {
        return new QueueReader(this);
    }




    class QueueReader {
        MessageQueue<T> mParent;
        int iNext = 0;

        private QueueReader(MessageQueue<T> parent) {
            mParent = parent;
            iNext = parent.mQueue.start();
        }

        public synchronized Optional<T> readNextMessage() {
            if (iNext >= mQueue.end()) {
                iNext = mQueue.end();
                return Optional.empty();
            }
            if (iNext < mQueue.start()) {
                iNext = mQueue.start();
            }
            var current = iNext;
            iNext++;
            ByteBuffer bb = mQueue.get(current); 
            T element = mGetRootAs.apply(bb);   
            return Optional.of(element);        
        }       

        public synchronized Optional<T> readLastMessage() {
            iNext = mQueue.end();
            ByteBuffer bb = mQueue.get(iNext); 
            T element = mGetRootAs.apply(bb);   
            return Optional.of(element);        
        }

        
        public boolean isEmpty() {
            return mParent.isEmpty();
        }

        public int size() {
            int size = 0;
    
            int start = iNext;
            int end = mQueue.end();
            int maxElements = mQueue.maxSize();
            boolean full = mQueue.isFull();

            if (end < start) {
                size = maxElements - start + end;
            } else if (end == start) {
                size = full ? maxElements : 0;
            } else {
                size = end - start;
            }
    
            return size;
        }
    

        public int maxSize() {
            return mQueue.maxSize();
        }
    }
}