package frc.taurus.joystick;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import com.google.flatbuffers.FlatBufferBuilder;

import org.junit.Test;

import frc.taurus.config.Config;
import frc.taurus.messages.MessageQueue;

public class JoystickStatusTest {

    double eps = 1e-6;  // using floats for joystick axes


    @Test 
    public void writeSingleMessageTest() {

        @SuppressWarnings("unchecked")
        MessageQueue<JoystickStatus> statusQueue = (MessageQueue<JoystickStatus>) Config.JOYSTICK_STATUS.getQueue();
        var statusReader = statusQueue.makeMessageReader();

        final int bufferSizeBytes = 128;   // slightly larger than required
        FlatBufferBuilder builder = new FlatBufferBuilder(bufferSizeBytes);        

        int offset = JoystickStatus.createJoystickStatus(builder,
            12345678.0, //timestamp,
            0.0f,       //axis0,
            0.1f,       //axis1
            0.2f,       //axis2
            0.3f,       //axis3,
            0.4f,       //axis4
            0.5f,       //axis5
            true,       //button1
            false,      //button2
            true,       //button3
            false,      //button4
            true,       //button5
            false,      //button6
            true,       //button7
            false,      //button8
            true,       //button9
            false,      //button10
            true,       //button11
            false,      //button12
            true,       //button13
            false,      //button14
            true,       //button15
            false,      //button16
            45);        //pov
            
        statusQueue.writeMessage(builder, offset);

        // check that we are pre-allocating enough space for the flatbuffer
        assertEquals(bufferSizeBytes, builder.dataBuffer().capacity());    // increase bufferSizeBytes if this fails

        assertFalse(statusReader.isEmpty());
        assertEquals(1, statusReader.size());
        Optional<JoystickStatus> optStatus = statusReader.readLastMessage();
        assertTrue(statusReader.isEmpty());
        assertTrue(optStatus.isPresent());

        if (optStatus.isPresent())
        {
            JoystickStatus status = optStatus.get();
            assertEquals(0.0, status.axis0(), eps);        
            assertEquals(0.1, status.axis1(), eps);        
            assertEquals(0.2, status.axis2(), eps);        
            assertEquals(0.3, status.axis3(), eps);        
            assertEquals(0.4, status.axis4(), eps);        
            assertEquals(0.5, status.axis5(), eps);    
            assertTrue( status.button1());    
            assertFalse(status.button2());    
            assertTrue( status.button3());    
            assertFalse(status.button4());    
            assertTrue( status.button5());    
            assertFalse(status.button6());    
            assertTrue( status.button7());    
            assertFalse(status.button8());    
            assertTrue( status.button9());    
            assertFalse(status.button10());    
            assertTrue( status.button11());    
            assertFalse(status.button12());    
            assertTrue( status.button13());    
            assertFalse(status.button14());    
            assertTrue( status.button15());    
            assertFalse(status.button16());    
            assertEquals(45, status.pov(), eps);
        }
    }    


}