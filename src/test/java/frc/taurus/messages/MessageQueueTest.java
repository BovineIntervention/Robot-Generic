package frc.taurus.messages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.Optional;

import com.google.flatbuffers.FlatBufferBuilder;

import org.junit.Test;

public class MessageQueueTest {

    MessageQueue<TestMessage> testQueue = new MessageQueue<TestMessage>(TestMessage::getRootAsTestMessage);
    final int bufferSizeBytes = 128;   // slightly larger than required
    FlatBufferBuilder builder = new FlatBufferBuilder(bufferSizeBytes);        

    @Test
    public void sendSingleMessage() {
        int offset = TestMessage.createTestMessage(builder, 686);   // create a message with a value of 686
        TestMessage.finishTestMessageBuffer(builder, offset);       // finalize the message
        testQueue.writeMessage(builder);                            // convert it to binary and store
    
        assertEquals(1, testQueue.size());                       // queue should have only 1 message
        Optional<TestMessage> opt = testQueue.readNextMessage();    // read that message
        assertTrue(opt.isPresent());                                // verify that the message is present (not empty)

        TestMessage message = opt.get();                            // get the message
        assertEquals(686, message.value());                         // verify contents are 686
    }

    @Test
    public void queueReadLastMessage() {
        int offset = TestMessage.createTestMessage(builder, 254);   // create a message with a value of 686
        TestMessage.finishTestMessageBuffer(builder, offset);       // finalize the message
        testQueue.writeMessage(builder);                            // convert it to binary and store
        offset = TestMessage.createTestMessage(builder, 971);       // create a message with a value of 686
        TestMessage.finishTestMessageBuffer(builder, offset);       // finalize the message
        testQueue.writeMessage(builder);                            // convert it to binary and store
        offset = TestMessage.createTestMessage(builder, 686);       // create a message with a value of 686
        TestMessage.finishTestMessageBuffer(builder, offset);       // finalize the message
        testQueue.writeMessage(builder);                            // convert it to binary and store
    
        for (int k=1; k<=5; k++)
        {
            assertEquals(3, testQueue.size());                       // queue should have 3 messages
            Optional<TestMessage> opt = testQueue.readLastMessage();    // read the last message
            assertTrue(opt.isPresent());                                // verify that the message is present (not empty)

            TestMessage message = opt.get();                            // get the message
            assertEquals(686, message.value());                         // verify contents are 686
        }
    }

    @Test
    public void readerReadNextMessage() {
        int offset = TestMessage.createTestMessage(builder, 254);   // create a message with a value of 686
        TestMessage.finishTestMessageBuffer(builder, offset);       // finalize the message
        testQueue.writeMessage(builder);                            // convert it to binary and store
        offset = TestMessage.createTestMessage(builder, 971);       // create a message with a value of 686
        TestMessage.finishTestMessageBuffer(builder, offset);       // finalize the message
        testQueue.writeMessage(builder);                            // convert it to binary and store
        offset = TestMessage.createTestMessage(builder, 686);       // create a message with a value of 686
        TestMessage.finishTestMessageBuffer(builder, offset);       // finalize the message
        testQueue.writeMessage(builder);                            // convert it to binary and store
    
        MessageQueue<TestMessage>.QueueReader testReader = testQueue.makeReader();

        assertEquals(3, testReader.size());                      // queue should have 3 messages
        Optional<TestMessage> opt = testReader.readNextMessage();   // read the next message, removing it
        assertTrue(opt.isPresent());                                // verify that the message is present (not empty)
        TestMessage message = opt.get();                            // get the message
        assertEquals(686, message.value());                         // verify contents are 686

        assertEquals(2, testReader.size());                       
        opt = testReader.readNextMessage();                         
        assertTrue(opt.isPresent());                                
        message = opt.get();                                        
        assertEquals(971, message.value());                         

        assertEquals(1, testReader.size());                       
        opt = testReader.readNextMessage();                         
        assertTrue(opt.isPresent());                                
        message = opt.get();                                        
        assertEquals(254, message.value());                         

        assertTrue(testReader.isEmpty());                            
        assertEquals(0, testReader.size());                       
        opt = testReader.readNextMessage();                         
        assertFalse(opt.isPresent());                               
    }  


    @Test
    public void readerReadLastMessage() {
        int offset = TestMessage.createTestMessage(builder, 254);   // create a message with a value of 686
        TestMessage.finishTestMessageBuffer(builder, offset);       // finalize the message
        testQueue.writeMessage(builder);                            // convert it to binary and store
        offset = TestMessage.createTestMessage(builder, 971);       // create a message with a value of 686
        TestMessage.finishTestMessageBuffer(builder, offset);       // finalize the message
        testQueue.writeMessage(builder);                            // convert it to binary and store
        offset = TestMessage.createTestMessage(builder, 686);       // create a message with a value of 686
        TestMessage.finishTestMessageBuffer(builder, offset);       // finalize the message
        testQueue.writeMessage(builder);                            // convert it to binary and store
    
        MessageQueue<TestMessage>.QueueReader testReader = testQueue.makeReader();

        assertEquals(3, testReader.size());                       // queue should have 3 messages
        Optional<TestMessage> opt = testReader.readLastMessage();   // read the last message, remove all messages
        assertTrue(opt.isPresent());                                // verify that the message is present (not empty)
        TestMessage message = opt.get();                            // get the message
        assertEquals(686, message.value());                         // verify contents are 686

        assertTrue(testReader.isEmpty());                           // verify that the message is present (not empty)
        assertEquals(0, testReader.size());                      // queue should be empty
        opt = testReader.readLastMessage();                         // read the last message
        assertFalse(opt.isPresent());                               // verify that the message is present (not empty)
    }    
}