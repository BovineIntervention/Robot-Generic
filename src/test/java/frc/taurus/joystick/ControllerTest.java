package frc.taurus.joystick;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.ByteBuffer;

import org.junit.Test;

import edu.wpi.first.wpilibj.Joystick;
import frc.taurus.joystick.Controller.AxisButton;
import frc.taurus.joystick.Controller.Button;
import frc.taurus.joystick.Controller.PovButton;
import frc.taurus.messages.MessageQueue;

public class ControllerTest {

    double eps = 1e-6;  // using floats for joystick axes

    @Test
    public void addButtonTest() {
        Joystick mockJoystick = mock(Joystick.class);
        var dummyStatusQueue = new MessageQueue<ByteBuffer>();
        var dummyGoalQueue = new MessageQueue<ByteBuffer>();
        Controller controller = new Controller(mockJoystick, dummyStatusQueue, dummyGoalQueue);
        Button button = controller.addButton(2);
        button.update();
        assertEquals(1, Controller.buttons.size());
    }

    @Test 
    public void handleDeadbandTest() {

        assertEquals( 0.5, Controller.applyDeadband( 0.5,  0.05), eps);
        assertEquals(-0.5, Controller.applyDeadband(-0.5,  0.05), eps);
        assertEquals( 0.0, Controller.applyDeadband( 0.02, 0.05), eps);
        assertEquals( 0.0, Controller.applyDeadband(-0.03, 0.05), eps);
        assertEquals( 0.0, Controller.applyDeadband( 0.0,  0.05), eps);

        assertEquals( 0.5,  Controller.applyDeadband( 0.5,  0.025), eps);
        assertEquals(-0.5,  Controller.applyDeadband(-0.5,  0.025), eps);
        assertEquals( 0.0,  Controller.applyDeadband( 0.02, 0.025), eps);
        assertEquals(-0.03, Controller.applyDeadband(-0.03, 0.025), eps);
        assertEquals( 0.0,  Controller.applyDeadband( 0.0,  0.025), eps);        
    }

    @Test 
    public void buttonTest() {

        Joystick mockJoystick = mock(Joystick.class);
        var dummyStatusQueue = new MessageQueue<ByteBuffer>();
        var dummyGoalQueue = new MessageQueue<ByteBuffer>();
        Controller controller = new Controller(mockJoystick, dummyStatusQueue, dummyGoalQueue);
        
        Button button0 = controller.addButton(0);
        Button button1 = controller.addButton(1);

        // initially not pressed
        when(mockJoystick.getRawButton(0)).thenReturn(false);
        when(mockJoystick.getRawButton(1)).thenReturn(false);  
        controller.update();     
        assertFalse( button0.isPressed() );
        assertFalse( button0.posEdge() );
        assertFalse( button0.negEdge() );
        assertFalse( button1.isPressed() );

        when(mockJoystick.getRawButton(0)).thenReturn(false);
        controller.update();   
        assertFalse( button0.isPressed() );
        assertFalse( button0.negEdge() );
        assertFalse( button0.negEdge() );
        assertFalse( button1.isPressed() );
        
        // press button 0
        when(mockJoystick.getRawButton(0)).thenReturn(true);
        controller.update();   
        assertTrue( button0.isPressed() );
        assertTrue( button0.posEdge() );
        assertFalse( button0.negEdge() );
        assertFalse( button1.isPressed() );
        
        when(mockJoystick.getRawButton(0)).thenReturn(true);
        controller.update();   
        assertTrue( button0.isPressed() );
        assertFalse( button0.posEdge() );
        assertFalse( button0.negEdge() );
        assertFalse( button1.isPressed() );
        
        // release
        when(mockJoystick.getRawButton(0)).thenReturn(false);
        controller.update();   
        assertFalse( button0.isPressed() );
        assertFalse( button0.posEdge() );
        assertTrue( button0.negEdge() );
        assertFalse( button1.isPressed() );
        
        when(mockJoystick.getRawButton(0)).thenReturn(false);
        controller.update();   
        assertFalse( button0.isPressed() );
        assertFalse( button0.posEdge() );
        assertFalse( button0.negEdge() );
        assertFalse( button1.isPressed() );
        
        // press button 1
        when(mockJoystick.getRawButton(1)).thenReturn(true);     
        controller.update();   
        assertFalse( button0.isPressed() );
        assertTrue( button1.isPressed() );        
    }   
    
    @Test 
    public void axisButtonTest() {

        Joystick mockJoystick = mock(Joystick.class);
        var dummyStatusQueue = new MessageQueue<ByteBuffer>();
        var dummyGoalQueue = new MessageQueue<ByteBuffer>();
        Controller controller = new Controller(mockJoystick, dummyStatusQueue, dummyGoalQueue);
        int id = 0;
        AxisButton axisButton = controller.addAxisButton(id, 0.5);

        // use the when().thenReturn() functions to have the mock controller
        // return fake axis values
        when(mockJoystick.getRawAxis(id)).thenReturn(0.1);
        controller.update();
        assertFalse( axisButton.isPressed() );
        assertFalse( axisButton.posEdge() );
        assertFalse( axisButton.negEdge() );

        when(mockJoystick.getRawAxis(id)).thenReturn(0.4);
        controller.update();
        assertFalse( axisButton.isPressed() );
        assertFalse( axisButton.negEdge() );
        assertFalse( axisButton.negEdge() );

        // press
        when(mockJoystick.getRawAxis(id)).thenReturn(0.6);
        controller.update();
        assertTrue( axisButton.isPressed() );
        assertTrue( axisButton.posEdge() );
        assertFalse( axisButton.negEdge() );

        when(mockJoystick.getRawAxis(id)).thenReturn(1.0);
        controller.update();        
        assertTrue( axisButton.isPressed() );
        assertFalse( axisButton.posEdge() );
        assertFalse( axisButton.negEdge() );

        // release
        when(mockJoystick.getRawAxis(id)).thenReturn(0.3);
        controller.update();
        assertFalse( axisButton.isPressed() );
        assertFalse( axisButton.posEdge() );
        assertTrue( axisButton.negEdge() );

        when(mockJoystick.getRawAxis(id)).thenReturn(0.3);
        controller.update();
        assertFalse( axisButton.isPressed() );
        assertFalse( axisButton.posEdge() );
        assertFalse( axisButton.negEdge() );             
    }    

    @Test 
    public void povButtonTest() {

        Joystick mockJoystick = mock(Joystick.class);
        var dummyStatusQueue = new MessageQueue<ByteBuffer>();
        var dummyGoalQueue = new MessageQueue<ByteBuffer>();
        Controller controller = new Controller(mockJoystick, dummyStatusQueue, dummyGoalQueue);

        int id = 0;
        PovButton povButtonNorth = controller.addPovButton(id, -45, 45);
        PovButton povButtonEast  = controller.addPovButton(id, 90, 90);
        PovButton povButtonSouth = controller.addPovButton(id, 135, 225);
        PovButton povButtonWest  = controller.addPovButton(id, 270, 270);

        // use the when().thenReturn() functions to have the mock controller
        // return fake POV values

        // not pressed
        when(mockJoystick.getPOV(id)).thenReturn(-1);
        controller.update();
        assertFalse( povButtonNorth.isPressed() );
        assertFalse( povButtonNorth.posEdge() );
        assertFalse( povButtonNorth.negEdge() );
        assertFalse( povButtonEast.isPressed() );
        assertFalse( povButtonEast.posEdge() );
        assertFalse( povButtonEast.negEdge() );
        assertFalse( povButtonSouth.isPressed() );
        assertFalse( povButtonSouth.posEdge() );
        assertFalse( povButtonSouth.negEdge() );
        assertFalse( povButtonWest.isPressed() );
        assertFalse( povButtonWest.posEdge() );
        assertFalse( povButtonWest.negEdge() );

        // North
        when(mockJoystick.getPOV(id)).thenReturn(0);
        controller.update();
        assertTrue( povButtonNorth.isPressed() );
        assertTrue( povButtonNorth.posEdge() );
        assertFalse( povButtonNorth.negEdge() );
        assertFalse( povButtonEast.isPressed() );
        assertFalse( povButtonEast.posEdge() );
        assertFalse( povButtonEast.negEdge() );
        assertFalse( povButtonSouth.isPressed() );
        assertFalse( povButtonSouth.posEdge() );
        assertFalse( povButtonSouth.negEdge() );
        assertFalse( povButtonWest.isPressed() );
        assertFalse( povButtonWest.posEdge() );
        assertFalse( povButtonWest.negEdge() );

        // Northeast
        when(mockJoystick.getPOV(id)).thenReturn(45);
        controller.update();
        assertTrue( povButtonNorth.isPressed() );
        assertFalse( povButtonNorth.posEdge() );
        assertFalse( povButtonNorth.negEdge() );
        assertFalse( povButtonEast.isPressed() );
        assertFalse( povButtonEast.posEdge() );
        assertFalse( povButtonEast.negEdge() );
        assertFalse( povButtonSouth.isPressed() );
        assertFalse( povButtonSouth.posEdge() );
        assertFalse( povButtonSouth.negEdge() );
        assertFalse( povButtonWest.isPressed() );
        assertFalse( povButtonWest.posEdge() );
        assertFalse( povButtonWest.negEdge() );

        // East
        when(mockJoystick.getPOV(id)).thenReturn(90);
        controller.update();
        assertFalse( povButtonNorth.isPressed() );
        assertFalse( povButtonNorth.posEdge() );
        assertTrue( povButtonNorth.negEdge() );
        assertTrue( povButtonEast.isPressed() );
        assertTrue( povButtonEast.posEdge() );
        assertFalse( povButtonEast.negEdge() );
        assertFalse( povButtonSouth.isPressed() );
        assertFalse( povButtonSouth.posEdge() );
        assertFalse( povButtonSouth.negEdge() );
        assertFalse( povButtonWest.isPressed() );
        assertFalse( povButtonWest.posEdge() );
        assertFalse( povButtonWest.negEdge() );        

        // Southeast
        when(mockJoystick.getPOV(id)).thenReturn(135);
        controller.update();
        assertFalse( povButtonNorth.isPressed() );
        assertFalse( povButtonNorth.posEdge() );
        assertFalse( povButtonNorth.negEdge() );
        assertFalse( povButtonEast.isPressed() );
        assertFalse( povButtonEast.posEdge() );
        assertTrue( povButtonEast.negEdge() );
        assertTrue( povButtonSouth.isPressed() );
        assertTrue( povButtonSouth.posEdge() );
        assertFalse( povButtonSouth.negEdge() );
        assertFalse( povButtonWest.isPressed() );
        assertFalse( povButtonWest.posEdge() );
        assertFalse( povButtonWest.negEdge() );             

        // South
        when(mockJoystick.getPOV(id)).thenReturn(180);
        controller.update();
        assertFalse( povButtonNorth.isPressed() );
        assertFalse( povButtonNorth.posEdge() );
        assertFalse( povButtonNorth.negEdge() );
        assertFalse( povButtonEast.isPressed() );
        assertFalse( povButtonEast.posEdge() );
        assertFalse( povButtonEast.negEdge() );
        assertTrue( povButtonSouth.isPressed() );
        assertFalse( povButtonSouth.posEdge() );
        assertFalse( povButtonSouth.negEdge() );
        assertFalse( povButtonWest.isPressed() );
        assertFalse( povButtonWest.posEdge() );
        assertFalse( povButtonWest.negEdge() );             
        
        // Southwest
        when(mockJoystick.getPOV(id)).thenReturn(225);
        controller.update();
        assertFalse( povButtonNorth.isPressed() );
        assertFalse( povButtonNorth.posEdge() );
        assertFalse( povButtonNorth.negEdge() );
        assertFalse( povButtonEast.isPressed() );
        assertFalse( povButtonEast.posEdge() );
        assertFalse( povButtonEast.negEdge() );
        assertTrue( povButtonSouth.isPressed() );
        assertFalse( povButtonSouth.posEdge() );
        assertFalse( povButtonSouth.negEdge() );
        assertFalse( povButtonWest.isPressed() );
        assertFalse( povButtonWest.posEdge() );
        assertFalse( povButtonWest.negEdge() );             

        // West
        when(mockJoystick.getPOV(id)).thenReturn(270);
        controller.update();
        assertFalse( povButtonNorth.isPressed() );
        assertFalse( povButtonNorth.posEdge() );
        assertFalse( povButtonNorth.negEdge() );
        assertFalse( povButtonEast.isPressed() );
        assertFalse( povButtonEast.posEdge() );
        assertFalse( povButtonEast.negEdge() );
        assertFalse( povButtonSouth.isPressed() );
        assertFalse( povButtonSouth.posEdge() );
        assertTrue( povButtonSouth.negEdge() );
        assertTrue( povButtonWest.isPressed() );
        assertTrue( povButtonWest.posEdge() );
        assertFalse( povButtonWest.negEdge() );   
        
        // Northwest
        when(mockJoystick.getPOV(id)).thenReturn(315);
        controller.update();
        assertTrue( povButtonNorth.isPressed() );
        assertTrue( povButtonNorth.posEdge() );
        assertFalse( povButtonNorth.negEdge() );
        assertFalse( povButtonEast.isPressed() );
        assertFalse( povButtonEast.posEdge() );
        assertFalse( povButtonEast.negEdge() );
        assertFalse( povButtonSouth.isPressed() );
        assertFalse( povButtonSouth.posEdge() );
        assertFalse( povButtonSouth.negEdge() );
        assertFalse( povButtonWest.isPressed() );
        assertFalse( povButtonWest.posEdge() );
        assertTrue( povButtonWest.negEdge() );      
        
        // Unpressed
        when(mockJoystick.getPOV(id)).thenReturn(-1);
        controller.update();
        assertFalse( povButtonNorth.isPressed() );
        assertFalse( povButtonNorth.posEdge() );
        assertTrue( povButtonNorth.negEdge() );
        assertFalse( povButtonEast.isPressed() );
        assertFalse( povButtonEast.posEdge() );
        assertFalse( povButtonEast.negEdge() );
        assertFalse( povButtonSouth.isPressed() );
        assertFalse( povButtonSouth.posEdge() );
        assertFalse( povButtonSouth.negEdge() );
        assertFalse( povButtonWest.isPressed() );
        assertFalse( povButtonWest.posEdge() );
        assertFalse( povButtonWest.negEdge() );         
    }    
}