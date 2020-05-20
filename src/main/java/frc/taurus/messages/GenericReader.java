package frc.taurus.messages;

import java.util.Optional;



public class GenericReader<T> {
    GenericQueue<T> mQueue;
    int nextReadIndex;

    public GenericReader(GenericQueue<T> queue) {
        mQueue = queue;
    }

    /**
     * Get current size of queue
     * @return numer of elements not yet read out of queue
     */
    public int size() {
        return (mQueue.back() - nextReadIndex);
    }

    /**
     * Check if queue is empty
     * @return true if all elements have been read out of queue
     */
    public boolean isEmpty() {
        return (mQueue.back() == nextReadIndex);
    }

    /**
     * Read next element out of queue
     * @return next element
     */
    public Optional<T> read() {
        return mQueue.read(this);
        // readIndex will be adjusted in this function
    }       

    /**
     * Read last element placed in queue
     * @return last element
     */
    public Optional<T> readLast() {
        nextReadIndex = mQueue.back();
        return mQueue.readLast();
    }
}