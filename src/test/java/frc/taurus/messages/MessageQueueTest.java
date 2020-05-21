package frc.taurus.messages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import com.google.flatbuffers.FlatBufferBuilder;

import org.junit.Test;

public class MessageQueueTest {
    
    // verify queue can be reset/cleared
    @Test
    public void resetTest() {
        MessageQueue<TestMessage> queue = new MessageQueue<TestMessage>(10, TestMessage::getRootAsTestMessage);
        MessageQueue<TestMessage>.MessageReader reader = queue.makeMessageReader();
        
        FlatBufferBuilder builder = new FlatBufferBuilder(64);
        int offset = TestMessage.createTestMessage(builder, 686);
        queue.writeMessage(builder, offset);      
        
        assertEquals(1, reader.size());         // verify queue has 1 element
        assertFalse(reader.isEmpty());
        
        queue.clear();                          // clear queue
        
        assertEquals(0, reader.size());         // verify queue is empty
        assertTrue(reader.isEmpty());
    }


    // verify we can send one value
    @Test
    public void sendSingleValueTest() {
        MessageQueue<TestMessage> queue = new MessageQueue<TestMessage>(10, TestMessage::getRootAsTestMessage);
        MessageQueue<TestMessage>.MessageReader reader = queue.makeMessageReader();
        
        FlatBufferBuilder builder = new FlatBufferBuilder(64);
        int offset = TestMessage.createTestMessage(builder, 686);
        queue.writeMessage(builder, offset);  
        
        assertEquals(1, reader.size());         // verify queue has 1 element
        Optional<TestMessage> opt = reader.readNextMessage();  // read from queue
        assertTrue(opt.isPresent());            // verify that the value is present (not empty)
        
        TestMessage msg = opt.get();            // get the element
        assertEquals(686, msg.value());         // verify contents are 686

        assertEquals(0, reader.size());         // verify queue is now empty
        assertTrue(reader.isEmpty());           // verify queue is now empty
    }
    

    // verify GenericQueue.readLastMessage() returns the last element, always
    @Test
    public void readLastMessageTest() {
        MessageQueue<TestMessage> queue = new MessageQueue<TestMessage>(10, TestMessage::getRootAsTestMessage);
        MessageQueue<TestMessage>.MessageReader reader = queue.makeMessageReader();

        FlatBufferBuilder builder = new FlatBufferBuilder(64);
        int offset = TestMessage.createTestMessage(builder, 254);
        queue.writeMessage(builder, offset);       

        builder = new FlatBufferBuilder(64);
        offset = TestMessage.createTestMessage(builder, 971);
        queue.writeMessage(builder, offset);        

        builder = new FlatBufferBuilder(64);
        offset = TestMessage.createTestMessage(builder, 686);
        queue.writeMessage(builder, offset);  
    
        // the last element should always be 686 when using the queue's readLastMessage()
        // even if queried several times
        for (int k=1; k<=5; k++)
        {
            Optional<TestMessage> opt = reader.readLastMessage();    // read the last element
            assertTrue(opt.isPresent());                 // verify that the value is present (not empty)

            TestMessage msg = opt.get();                   // get the value
            assertEquals(686, msg.value());           // verify contents are 686
        }
    }

    // check that queue reader reads several values correctly
    @Test
    public void readerReadNextMessageTest() {
        MessageQueue<TestMessage> queue = new MessageQueue<TestMessage>(10, TestMessage::getRootAsTestMessage);
        MessageQueue<TestMessage>.MessageReader reader = queue.makeMessageReader();

        FlatBufferBuilder builder = new FlatBufferBuilder(64);
        int offset = TestMessage.createTestMessage(builder, 254);
        queue.writeMessage(builder, offset);   

        builder = new FlatBufferBuilder(64);
        offset = TestMessage.createTestMessage(builder, 971);
        queue.writeMessage(builder, offset);        
        
        builder = new FlatBufferBuilder(64);
        offset = TestMessage.createTestMessage(builder, 686);
        queue.writeMessage(builder, offset);  
        
        assertEquals(3, reader.size());
        Optional<TestMessage> opt = reader.readNextMessage();   // read the next value, removing it
        assertTrue(opt.isPresent());             // verify that the value is present (not empty)
        TestMessage msg = opt.get();               // get the value
        assertEquals(254, msg.value());       // verify contents are 254
        
        assertEquals(2, reader.size());
        opt = reader.readNextMessage();                     // read the next value    
        assertTrue(opt.isPresent());                                
        msg = opt.get();                                      
        assertEquals(971, msg.value());       // verify contents are 971                    
        
        assertEquals(1, reader.size());
        opt = reader.readNextMessage();                     // read the next value    
        assertTrue(opt.isPresent());                                
        msg = opt.get();                              
        assertEquals(686, msg.value());       // verify contents are 686                     
        
        assertEquals(0, reader.size());
        assertTrue(reader.isEmpty());           // verify all elements have been read out
        opt = reader.readNextMessage();                    // attempt to read one more element
        assertTrue(opt.isEmpty());              // check that it is empty
        assertFalse(opt.isPresent());           // check that it is empty
    }  
    
    
    
    // check that queue reader reads the last value correctly
    @Test
    public void readerReadLastMessageTest() {
        MessageQueue<TestMessage> queue = new MessageQueue<TestMessage>(10, TestMessage::getRootAsTestMessage);
        MessageQueue<TestMessage>.MessageReader reader = queue.makeMessageReader();
        
        FlatBufferBuilder builder = new FlatBufferBuilder(64);
        int offset = TestMessage.createTestMessage(builder, 254);
        queue.writeMessage(builder, offset);       
 
        builder = new FlatBufferBuilder(64);
        offset = TestMessage.createTestMessage(builder, 971);
        queue.writeMessage(builder, offset);        
 
        builder = new FlatBufferBuilder(64);
        offset = TestMessage.createTestMessage(builder, 686);
        queue.writeMessage(builder, offset);  
    
        assertEquals(3, reader.size());
        assertFalse(reader.isEmpty());

        Optional<TestMessage> opt = reader.readLastMessage();  // read the last value, remove all values
        assertTrue(opt.isPresent());                // verify that the value is present (not empty)
        TestMessage msg = opt.get();                  // get the value
        assertEquals(686, msg.value());          // verify contents are 686

        assertEquals(0, reader.size());
        assertTrue(reader.isEmpty());

        opt = reader.readNextMessage();                        // read the last value
        assertTrue(opt.isEmpty());                  // element should be empty
    }    



    // check that queue delivers multiple values correctly, and in sequence
    @Test
    public void deliverManyValuesTest() {
        MessageQueue<TestMessage> queue = new MessageQueue<TestMessage>(10, TestMessage::getRootAsTestMessage);
        MessageQueue<TestMessage>.MessageReader reader = queue.makeMessageReader();
        
        for (int k=0; k<10; k++) {
            FlatBufferBuilder builder = new FlatBufferBuilder(64);
            int offset = TestMessage.createTestMessage(builder, k);
            queue.writeMessage(builder, offset);       
        }                            

        int cnt = 0;            
        Optional<TestMessage> opt = reader.readNextMessage();
        while (opt.isPresent()) {
            assertEquals(cnt++, opt.get().value());  // check we are reading counting pattern
            opt = reader.readNextMessage();
        }

        assertEquals(10, cnt);  // check that all 10 values were read out
    }   
    
    
    // ensure that correctness is maintained even when the circular buffer wraps
    // and we start to overwrite the beginning of the buffer
    @Test
    public void wraparoundTest() {
        MessageQueue<TestMessage> queue = new MessageQueue<TestMessage>(10, TestMessage::getRootAsTestMessage);
        MessageQueue<TestMessage>.MessageReader reader = queue.makeMessageReader();
        
        queue.clear();

        // fill queue with counting pattern
        for (int k=0; k<10; k++) {
            FlatBufferBuilder builder = new FlatBufferBuilder(64);
            int offset = TestMessage.createTestMessage(builder, k);
            queue.writeMessage(builder, offset);       
        }

        int cnt = 0;
        Optional<TestMessage> opt = reader.readNextMessage();
        while (opt.isPresent()) {
            assertEquals(cnt++, opt.get().value());
            opt = reader.readNextMessage();            
        }

        // continue filling queue with counting pattern
        for (int k=10; k<15; k++) {
            FlatBufferBuilder builder = new FlatBufferBuilder(64);
            int offset = TestMessage.createTestMessage(builder, k);
            queue.writeMessage(builder, offset);       
        }

        opt = reader.readNextMessage();
        while (opt.isPresent()) {
            assertEquals(cnt++, opt.get().value());
            opt = reader.readNextMessage();            
        }

        assertTrue(reader.isEmpty());
    }


    // ensure that the queue works with multiple readers
    @Test
    public void multipleReaderTest() {
        MessageQueue<TestMessage> queue = new MessageQueue<TestMessage>(10, TestMessage::getRootAsTestMessage);
        
        var reader1 = queue.makeMessageReader();
        var reader2 = queue.makeMessageReader();
        var reader3 = queue.makeMessageReader();
        var reader4 = queue.makeMessageReader();

        // fill queue with counting pattern
        for (int k=0; k<9; k++) {
            FlatBufferBuilder builder = new FlatBufferBuilder(64);
            int offset = TestMessage.createTestMessage(builder, k);
            queue.writeMessage(builder, offset);       
        }

        assertFalse(reader1.isEmpty());
        int cnt = 0;
        var opt = reader1.readNextMessage();
        assertTrue(opt.isPresent());
        while (opt.isPresent()) {
            assertEquals(cnt++, opt.get().value());
            opt = reader1.readNextMessage();            
        }
        assertTrue(reader1.isEmpty());
        
        assertFalse(reader2.isEmpty());
        cnt = 0;
        opt = reader2.readNextMessage();
        assertTrue(opt.isPresent());
        while (opt.isPresent()) {
            assertEquals(cnt++, opt.get().value());
            opt = reader2.readNextMessage();            
        }   
        assertTrue(reader2.isEmpty());
        
        assertFalse(reader3.isEmpty());
        assertFalse(reader4.isEmpty());
        for (int k=0; k<9; k++) {
            opt = reader3.readNextMessage();
            assertTrue(opt.isPresent());
            assertEquals(k, opt.get().value());
            
            opt = reader4.readNextMessage();
            assertTrue(opt.isPresent());
            assertEquals(k, opt.get().value());
        }
        assertTrue(reader3.isEmpty());
        assertTrue(reader4.isEmpty());
    }     


    // Speed test for profiling
    @Test
    public void speedTest() {
        final int numValues = 1000000;
        MessageQueue<TestMessage> queue = new MessageQueue<TestMessage>(numValues, TestMessage::getRootAsTestMessage);
        MessageQueue<TestMessage>.MessageReader reader = queue.makeMessageReader();
        
        // fill queue with counting pattern
        for (int k=0; k<numValues; k++) {
            FlatBufferBuilder builder = new FlatBufferBuilder(64);
            int offset = TestMessage.createTestMessage(builder, k);
            queue.writeMessage(builder, offset); 
        }

        int cnt = 0;
        while (!reader.isEmpty()) {
            var opt = reader.readNextMessage();
            if (opt.isPresent()) {
                assertEquals(cnt++, opt.get().value());
            }
        }
        assertEquals(1000000, cnt);        
    }



    // ensure that queues maintain correctness with a single writer thread 
    // and several reader threads
    @Test
    public void multipleReaderThreadTest() {
        final int kNumValues = 10000;
        final int numThreads = 5;
        final long timeout = 1000;  // milliseconds
        MessageQueue<TestMessage> queue = new MessageQueue<TestMessage>(kNumValues, TestMessage::getRootAsTestMessage);

        // start reader threads
        Thread[] threads = new Thread[numThreads];
        for (int k=0; k<threads.length; k++) {
            threads[k] = new Thread(new Runnable() {
                public void run() {
                    // System.out.println(Thread.currentThreadNextMessage().getName() 
                    //          + " started");
                    MessageQueue<TestMessage>.MessageReader reader = queue.makeMessageReader();
                    int cnt = 0;
                    long start = System.currentTimeMillis();
                    long end = start + timeout;
                    while (cnt < kNumValues && System.currentTimeMillis() < end)
                    {
                        while (!reader.isEmpty()) {
                            var opt = reader.readNextMessage();
                            if (opt.isPresent()) {
                                assertEquals(cnt++, opt.get().value());
                            }
                        }
                    }
                    assertEquals(kNumValues, cnt);  
                    // System.out.println(Thread.currentThread().getName() 
                    //          + " read " + cnt + " elements");                     
                }             
            });
            threads[k].start();
        }

        // start writing
        for (int k=0; k<kNumValues; k++) {
            FlatBufferBuilder builder = new FlatBufferBuilder(64);
            int offset = TestMessage.createTestMessage(builder, k);
            queue.writeMessage(builder, offset); 
        }
        // System.out.println("Wrote " + kNumValues + " elements");   

        // wait for all threads to complete before stopping this test function
        for (int k=0; k<threads.length; k++) {
            try {
                threads[k].join(timeout);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    // ensure that all the data written is read by readers
    // with multiple writer threads and multiple reader threads

    // testing multiple threads using technique documented here:
    // (the first method, not the ConcurrentUnit Waiter method)
    // https://jodah.net/testing-multi-threaded-code

    @Test
    public void multipleWriterReaderThreadTest() {
        final int kNumValuesPerThread = 10000;
        final int kNumThreads = 5;
        final long timeout = 1000;  // milliseconds
        MessageQueue<TestMessage> queue = new MessageQueue<TestMessage>(kNumValuesPerThread*kNumThreads, TestMessage::getRootAsTestMessage);

        CountDownLatch latch = new CountDownLatch(kNumThreads);
        AtomicReference<AssertionError> failure = new AtomicReference<>();

        // start reader threads
        Thread[] readerThreads = new Thread[kNumThreads];
        for (int k=0; k<readerThreads.length; k++) {
            readerThreads[k] = new Thread(new Runnable() {
                public void run() {
                    // System.out.println(Thread.currentThread().getName() 
                    //          + " started");
                    MessageQueue<TestMessage>.MessageReader reader = queue.makeMessageReader();
                    int cnt = 0;
                    long start = System.currentTimeMillis();
                    long end = start + timeout;
                    while (cnt < kNumValuesPerThread * kNumThreads && System.currentTimeMillis() < end)
                    {
                        while (!reader.isEmpty()) {
                            var opt = reader.readNextMessage();
                            if (opt.isPresent()) {
                                // not looking at contents, which will be jumbled
                                cnt++;
                            }
                        }
                    }
                    try {
                        assertEquals(kNumValuesPerThread * kNumThreads, cnt);
                    } catch (AssertionError e) {
                        failure.set(e);
                    }
                    latch.countDown();  // each time a thread succeeds, count down one
                    // System.out.println(Thread.currentThread().getName() 
                    //          + " read " + cnt + " elements");                     
                }             
            });
        }

        // start writer threads
        Thread[] writerThreads = new Thread[kNumThreads];
        for (int k=0; k<writerThreads.length; k++) {
            writerThreads[k] = new Thread(new Runnable() {
                public void run() {
                    for (int k=0; k<kNumValuesPerThread; k++) {
                        FlatBufferBuilder builder = new FlatBufferBuilder(64);
                        int offset = TestMessage.createTestMessage(builder, k);
                        queue.writeMessage(builder, offset); 
                    }
                    // System.out.println(Thread.currentThread().getName() 
                    //          + " wrote " + kNumValuesPerThread + " elements"); 
                }             
            });
        }

        // start all reader threads
        for (int k=0; k<readerThreads.length; k++) {
            readerThreads[k].start();
        }

        // start all writer threads
        for (int k=0; k<writerThreads.length; k++) {
            writerThreads[k].start();
        }

        // wait to see if all reader threads complete successfully within timeout period
        try {
            latch.await(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // throw any failures found in reader threads
        assertNull(failure.get());
        if (failure.get() != null)
            throw failure.get();


        // wait for all threads to complete before stopping this test function
        for (int k=0; k<readerThreads.length; k++) {
            try {
                readerThreads[k].join(timeout);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for (int k=0; k<writerThreads.length; k++) {
            try {
                writerThreads[k].join(timeout);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }        
    } 
    
    
    // check that reader index moves up when reader gets too far behind
    @Test
    public void readerIndexTest() {
        MessageQueue<TestMessage> queue = new MessageQueue<TestMessage>(10, TestMessage::getRootAsTestMessage);
        MessageQueue<TestMessage>.MessageReader reader = queue.makeMessageReader();

        assertEquals(0, reader.nextReadIndex);

        FlatBufferBuilder builder = new FlatBufferBuilder(64);
        int offset = TestMessage.createTestMessage(builder, 686);
        queue.writeMessage(builder, offset); 
        assertEquals(0, reader.nextReadIndex);
        
        var opt = reader.readNextMessage();
        assertEquals(1, reader.nextReadIndex);

        // write 11 numbers
        for (int k=0; k<11; k++) {
            builder = new FlatBufferBuilder(64);
            offset = TestMessage.createTestMessage(builder, k);
            queue.writeMessage(builder, offset); 
        }

        // because the queue only uses a buffer with length 10, the
        // following numbers were overwritten:
        // 686-->9
        //   0-->10
        // the oldest number in the buffer (that wasn't overwritten)
        // is now the number 1 at index 2

        // verify that we skip the number 0 (at index 1)
        // verify we read the number 1 (at index 2)
        // verify the nextReadIndex is 3
        assertEquals(2, queue.front());
        assertEquals(12, queue.back());
        opt = reader.readNextMessage();
        assertTrue(opt.isPresent());
        assertEquals(1, opt.get().value());         
        assertEquals(3, reader.nextReadIndex);  
    }
}