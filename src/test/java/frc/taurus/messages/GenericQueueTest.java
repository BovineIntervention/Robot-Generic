package frc.taurus.messages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.Optional;

import org.junit.Test;


public class GenericQueueTest {
    

    @Test
    public void sendSingleMessage() {
        GenericQueue<Integer> queue = new GenericQueue<Integer>(10);
        GenericQueue<Integer>.QueueReader reader = queue.makeReader();

        queue.writeMessage(686);                            // write message
        
        Optional<Integer> opt = reader.readNextMessage();   // read that message
        assertTrue(opt.isPresent());                        // verify that the message is present (not empty)
        
        Integer value = opt.get();                          // get the message
        assertEquals((Integer)686, value);                  // verify contents are 686
    }
    
    @Test
    public void queueReadLastMessage() {
        GenericQueue<Integer> queue = new GenericQueue<Integer>(10);
        
        queue.writeMessage(254);                            
        queue.writeMessage(971);                            
        queue.writeMessage(686);                           
    
        for (int k=1; k<=5; k++)
        {
            Optional<Integer> opt = queue.readLastMessage();    // read the last message
            assertTrue(opt.isPresent());                            // verify that the message is present (not empty)

            Integer value = opt.get();                              // get the message
            assertEquals((Integer)686, value);                      // verify contents are 686
        }
    }

    // check that queue reader reads several messages correctly
    @Test
    public void readerReadNextMessage() {
        GenericQueue<Integer> queue = new GenericQueue<Integer>(10);
        
        queue.writeMessage(254);                            
        queue.writeMessage(971);                            
        queue.writeMessage(686);                            
    
        GenericQueue<Integer>.QueueReader reader = queue.makeReader();

        Optional<Integer> opt = reader.readNextMessage();   // read the next message, removing it
        assertTrue(opt.isPresent());                            // verify that the message is present (not empty)
        Integer value = opt.get();                              // get the message
        assertEquals((Integer)254, value);                      // verify contents are 254

        opt = reader.readNextMessage();                         
        assertTrue(opt.isPresent());                                
        value = opt.get();                                      
        assertEquals((Integer)971, value);                      // verify contents are 971                    

        opt = reader.readNextMessage();                         
        assertTrue(opt.isPresent());                                
        value = opt.get();                              
        assertEquals((Integer)686, value);                      // verify contents are 686                     

        opt = reader.readNextMessage();                         
        assertFalse(opt.isPresent());                               
    }  



    // check that queue reader reads the last message correctly
    @Test
    public void readerReadLastMessage() {
        GenericQueue<Integer> queue = new GenericQueue<Integer>(10);
        
        queue.writeMessage(254);                            
        queue.writeMessage(971);                            
        queue.writeMessage(686);                            
    
        GenericQueue<Integer>.QueueReader reader = queue.makeReader();

        Optional<Integer> opt = reader.readLastMessage();   // read the last message, remove all messages
        assertTrue(opt.isPresent());                            // verify that the message is present (not empty)
        Integer value = opt.get();                              // get the message
        assertEquals((Integer)686, value);                      // verify contents are 686

        opt = reader.readNextMessage();                     // read the last message
        assertTrue(opt.isEmpty());                           // element should be empty
    }    



    // check that queue delivers multiple messages correctly, and in sequence
    @Test
    public void deliverManyMessages() {
        GenericQueue<Integer> queue = new GenericQueue<Integer>(10);
        GenericQueue<Integer>.QueueReader reader = queue.makeReader();    
        
        for (int k=0; k<10; k++) {
            queue.writeMessage(k);      // write a counting pattern
        }                            

        int cnt = 0;            
        Optional<Integer> opt = reader.readNextMessage();
        while (opt.isPresent()) {
            assertEquals((Integer)cnt++, opt.get());  // check we are reading counting pattern
            opt = reader.readNextMessage();
        }

        assertEquals(10, cnt);  // check that all 10 messages were read out
    }   
    
    
    // ensure that correctness is maintained even when the circular buffer wraps
    // and we start to overwrite the beginning of the buffer
    @Test
    public void wraparoundTest() {
        GenericQueue<Integer> queue = new GenericQueue<Integer>(10);
        GenericQueue<Integer>.QueueReader reader = queue.makeReader();    
        
        queue.clear();

        // fill queue with counting pattern
        for (int k=0; k<10; k++) {
            queue.writeMessage(k);
        }

        int cnt = 0;
        Optional<Integer> opt = reader.readNextMessage();
        while (opt.isPresent()) {
            assertEquals((Integer)cnt++, opt.get());
            opt = reader.readNextMessage();            
        }

        // continue filling queue with counting pattern
        for (int k=10; k<15; k++) {
            queue.writeMessage(k);
        }

        opt = reader.readNextMessage();
        while (opt.isPresent()) {
            assertEquals((Integer)cnt++, opt.get());
            opt = reader.readNextMessage();            
        }
    }


    // ensure that the queue works with multiple readers
    @Test
    public void multipleReaderTest() {
        GenericQueue<Integer> queue = new GenericQueue<Integer>(10);
        
        var reader1 = queue.makeReader();
        var reader2 = queue.makeReader();
        var reader3 = queue.makeReader();
        var reader4 = queue.makeReader();

        // fill queue with counting pattern
        for (int k=0; k<9; k++) {
            queue.writeMessage(k);
        }

        int cnt = 0;
        var opt = reader1.readNextMessage();
        assertTrue(opt.isPresent());
        while (opt.isPresent()) {
            assertEquals((Integer)cnt++, opt.get());
            opt = reader1.readNextMessage();            
        }

        cnt = 0;
        opt = reader2.readNextMessage();
        assertTrue(opt.isPresent());
        while (opt.isPresent()) {
            assertEquals((Integer)cnt++, opt.get());
            opt = reader2.readNextMessage();            
        }   

        for (int k=0; k<9; k++) {
            opt = reader3.readNextMessage();
            assertTrue(opt.isPresent());
            assertEquals((Integer)k, opt.get());

            opt = reader4.readNextMessage();
            assertTrue(opt.isPresent());
            assertEquals((Integer)k, opt.get());
        }
    }     


    // Speed test for profiling
    @Test
    public void speedTest() {
        final int numMessages = 1000000;
        GenericQueue<Integer> queue = new GenericQueue<Integer>(numMessages);
        GenericQueue<Integer>.QueueReader reader = queue.makeReader();    
        
        // fill queue with counting pattern
        for (int k=0; k<numMessages; k++) {
            queue.writeMessage(k);
        }

        int cnt = 0;
        var opt = reader.readNextMessage();
        while (opt.isPresent()) {
            assertEquals((Integer)cnt++, opt.get());
            opt = reader.readNextMessage();            
        }        
    }



    // ensure that queues maintain correctness with a single writer thread 
    // and several reader threads
    @Test
    public void multithreadingTest() {
        final int kNumMessages = 100;
        GenericQueue<Integer> queue = new GenericQueue<Integer>(kNumMessages);

        // start reader threads
        for (int k=0; k<5; k++) {
            new Thread(new Runnable() {
                public void run() {
                    System.out.println(Thread.currentThread().getName() + ", executing run() method!"); 
                    GenericQueue<Integer>.QueueReader reader = queue.makeReader();                    
                    int cnt = 0;
                    long start = System.currentTimeMillis();
                    long end = start + 1000;
                    while (System.currentTimeMillis() < end)
                    {
                        long current = System.currentTimeMillis();
                        Optional<Integer> opt = reader.readNextMessage();
                        while (opt.isPresent()) {
                            assertEquals((Integer)cnt++, opt.get());
                            System.out.println(cnt-1);
                            opt = reader.readNextMessage();
                        }
                        // try {
                        //     Thread.sleep(10);
                        // } catch (InterruptedException e) {
                        //     e.printStackTrace();
                        // }
                    }
                    assertEquals(kNumMessages, cnt);  
                    System.out.println(Thread.currentThread().getName() 
                             + ", done!");                     
                }             
            }).start();
        }

        // start writing
        for (int k=0; k<kNumMessages; k++) {
            queue.writeMessage(k);
        }
    }
}