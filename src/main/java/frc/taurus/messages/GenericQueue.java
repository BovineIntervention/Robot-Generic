package frc.taurus.messages;

import java.util.Optional;



public class GenericQueue<T> {

    private T[] buffer;             // storage array (circular)
    private int capacity;           // capacity (size of storage array)
    private int back = 0;           // index of last (youngest) element in array

    /**
     * Constructor that creates a queue with the default size of 32.
     */
    public GenericQueue() {
        this(32);
    }

    /**
     * Constructor that creates a queue with the specified size
     * @param size the size of the queue (cannot be changed)
     */
    @SuppressWarnings("unchecked")
    public GenericQueue(final int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("The size must be greater than 0");
        }
        buffer = (T[]) new Object[size];
        capacity = buffer.length;
    }

    /**
     * Returns the maximum size of the queue
     * @return this queue's capacity
     */
    public int capacity() {
        return capacity;
    }

    /**
     * Returns the number of elements stored in the queue.
     * @return this queue's size
     */
    public int front() {
        return Math.max(back, capacity) - capacity;
    }

    // the following methods should be synchronized so that
    // multiple threads can access the same queue safely (no collisions)

    public synchronized void clear() {
        back = 0;
    }

    public synchronized void writeMessage(final T element) {
        buffer[back % capacity] = null;     // dereference for garbage collection.  Not sure this is necessary
        buffer[back % capacity] = element;
        back++;
    }

    public synchronized Optional<T> readMessage(QueueReader reader) {
        // make sure idx is in the bounds of valid data
        if (reader.idx >= back) {
            // idx has moved too far forward.  Message has not yet been written.
            reader.idx = back;
            return Optional.empty();
        }

        if (reader.idx < front()) {
            // idx is too far back.  This data has already been overwritten.
            reader.idx = front();
        }

        T element = buffer[reader.idx % capacity];
        reader.idx++;

        return Optional.of(element);
    }

    public synchronized Optional<T> readLastMessage() {
        if (back == 0) {
            // nothing written yet
            return Optional.empty();
        }
        T element = buffer[(back-1) % capacity];
        return Optional.of(element);
    }

    public QueueReader makeReader() {
        return new QueueReader(this);
    }




    class QueueReader {
        GenericQueue<T> mParent;
        int idx;

        private QueueReader(GenericQueue<T> parent) {
            mParent = parent;
        }

        public Optional<T> readNextMessage() {
            return mParent.readMessage(this);
            // idx will be adjusted in this function
        }       

        public Optional<T> readLastMessage() {
            idx = mParent.back;
            return mParent.readLastMessage();
        }
    }
}