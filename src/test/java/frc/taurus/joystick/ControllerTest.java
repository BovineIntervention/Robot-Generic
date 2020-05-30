package frc.taurus.joystick;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import edu.wpi.first.wpilibj.Joystick;
import frc.taurus.joystick.Controller.AxisButton;
import frc.taurus.joystick.Controller.Button;
import frc.taurus.joystick.Controller.PovButton;

public class ControllerTest {

    double eps = 1e-6;  // using floats for joystick axes

    @Test
    public void addButtonTest() {
        Joystick mockJoystick = mock(Joystick.class);
        Controller controller = new Controller(mockJoystick);
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
        Controller controller = new Controller(mockJoystick);
        
        Button button0 = controller.addButton(0);
        Button button1 = controller.addButton(1);

        // initially not pressed
        when(mockJoystick.getRawButton(0)).thenReturn(false);
        when(mockJoystick.getRawButton(1)).thenReturn(false);  
        controller.update();     
        assertFalse( button0.getButton() );
        assertFalse( button0.getButtonPressed() );
        assertFalse( button0.getButtonReleased() );
        assertFalse( button1.getButton() );

        when(mockJoystick.getRawButton(0)).thenReturn(false);
        controller.update();   
        assertFalse( button0.getButton() );
        assertFalse( button0.getButtonReleased() );
        assertFalse( button0.getButtonReleased() );
        assertFalse( button1.getButton() );
        
        // press button 0
        when(mockJoystick.getRawButton(0)).thenReturn(true);
        controller.update();   
        assertTrue( button0.getButton() );
        assertTrue( button0.getButtonPressed() );
        assertFalse( button0.getButtonReleased() );
        assertFalse( button1.getButton() );
        
        when(mockJoystick.getRawButton(0)).thenReturn(true);
        controller.update();   
        assertTrue( button0.getButton() );
        assertFalse( button0.getButtonPressed() );
        assertFalse( button0.getButtonReleased() );
        assertFalse( button1.getButton() );
        
        // release
        when(mockJoystick.getRawButton(0)).thenReturn(false);
        controller.update();   
        assertFalse( button0.getButton() );
        assertFalse( button0.getButtonPressed() );
        assertTrue( button0.getButtonReleased() );
        assertFalse( button1.getButton() );
        
        when(mockJoystick.getRawButton(0)).thenReturn(false);
        controller.update();   
        assertFalse( button0.getButton() );
        assertFalse( button0.getButtonPressed() );
        assertFalse( button0.getButtonReleased() );
        assertFalse( button1.getButton() );
        
        // press button 1
        when(mockJoystick.getRawButton(1)).thenReturn(true);     
        controller.update();   
        assertFalse( button0.getButton() );
        assertTrue( button1.getButton() );        
    }   
    
    @Test 
    public void axisButtonTest() {

        Joystick mockJoystick = mock(Joystick.class);
        Controller controller = new Controller(mockJoystick);
        int id = 0;
        AxisButton axisButton = controller.addAxisButton(id, 0.5);

        // use the when().thenReturn() functions to have the mock controller
        // return fake axis values
        when(mockJoystick.getRawAxis(id)).thenReturn(0.1);
        controller.update();
        assertFalse( axisButton.getButton() );
        assertFalse( axisButton.getButtonPressed() );
        assertFalse( axisButton.getButtonReleased() );

        when(mockJoystick.getRawAxis(id)).thenReturn(0.4);
        controller.update();
        assertFalse( axisButton.getButton() );
        assertFalse( axisButton.getButtonReleased() );
        assertFalse( axisButton.getButtonReleased() );

        // press
        when(mockJoystick.getRawAxis(id)).thenReturn(0.6);
        controller.update();
        assertTrue( axisButton.getButton() );
        assertTrue( axisButton.getButtonPressed() );
        assertFalse( axisButton.getButtonReleased() );

        when(mockJoystick.getRawAxis(id)).thenReturn(1.0);
        controller.update();        
        assertTrue( axisButton.getButton() );
        assertFalse( axisButton.getButtonPressed() );
        assertFalse( axisButton.getButtonReleased() );

        // release
        when(mockJoystick.getRawAxis(id)).thenReturn(0.3);
        controller.update();
        assertFalse( axisButton.getButton() );
        assertFalse( axisButton.getButtonPressed() );
        assertTrue( axisButton.getButtonReleased() );

        when(mockJoystick.getRawAxis(id)).thenReturn(0.3);
        controller.update();
        assertFalse( axisButton.getButton() );
        assertFalse( axisButton.getButtonPressed() );
        assertFalse( axisButton.getButtonReleased() );             
    }    

    @Test 
    public void povButtonTest() {

        Joystick mockJoystick = mock(Joystick.class);
        Controller controller = new Controller(mockJoystick);
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
        assertFalse( povButtonNorth.getButton() );
        assertFalse( povButtonNorth.getButtonPressed() );
        assertFalse( povButtonNorth.getButtonReleased() );
        assertFalse( povButtonEast.getButton() );
        assertFalse( povButtonEast.getButtonPressed() );
        assertFalse( povButtonEast.getButtonReleased() );
        assertFalse( povButtonSouth.getButton() );
        assertFalse( povButtonSouth.getButtonPressed() );
        assertFalse( povButtonSouth.getButtonReleased() );
        assertFalse( povButtonWest.getButton() );
        assertFalse( povButtonWest.getButtonPressed() );
        assertFalse( povButtonWest.getButtonReleased() );

        // North
        when(mockJoystick.getPOV(id)).thenReturn(0);
        controller.update();
        assertTrue( povButtonNorth.getButton() );
        assertTrue( povButtonNorth.getButtonPressed() );
        assertFalse( povButtonNorth.getButtonReleased() );
        assertFalse( povButtonEast.getButton() );
        assertFalse( povButtonEast.getButtonPressed() );
        assertFalse( povButtonEast.getButtonReleased() );
        assertFalse( povButtonSouth.getButton() );
        assertFalse( povButtonSouth.getButtonPressed() );
        assertFalse( povButtonSouth.getButtonReleased() );
        assertFalse( povButtonWest.getButton() );
        assertFalse( povButtonWest.getButtonPressed() );
        assertFalse( povButtonWest.getButtonReleased() );

        // Northeast
        when(mockJoystick.getPOV(id)).thenReturn(45);
        controller.update();
        assertTrue( povButtonNorth.getButton() );
        assertFalse( povButtonNorth.getButtonPressed() );
        assertFalse( povButtonNorth.getButtonReleased() );
        assertFalse( povButtonEast.getButton() );
        assertFalse( povButtonEast.getButtonPressed() );
        assertFalse( povButtonEast.getButtonReleased() );
        assertFalse( povButtonSouth.getButton() );
        assertFalse( povButtonSouth.getButtonPressed() );
        assertFalse( povButtonSouth.getButtonReleased() );
        assertFalse( povButtonWest.getButton() );
        assertFalse( povButtonWest.getButtonPressed() );
        assertFalse( povButtonWest.getButtonReleased() );

        // East
        when(mockJoystick.getPOV(id)).thenReturn(90);
        controller.update();
        assertFalse( povButtonNorth.getButton() );
        assertFalse( povButtonNorth.getButtonPressed() );
        assertTrue( povButtonNorth.getButtonReleased() );
        assertTrue( povButtonEast.getButton() );
        assertTrue( povButtonEast.getButtonPressed() );
        assertFalse( povButtonEast.getButtonReleased() );
        assertFalse( povButtonSouth.getButton() );
        assertFalse( povButtonSouth.getButtonPressed() );
        assertFalse( povButtonSouth.getButtonReleased() );
        assertFalse( povButtonWest.getButton() );
        assertFalse( povButtonWest.getButtonPressed() );
        assertFalse( povButtonWest.getButtonReleased() );        

        // Southeast
        when(mockJoystick.getPOV(id)).thenReturn(135);
        controller.update();
        assertFalse( povButtonNorth.getButton() );
        assertFalse( povButtonNorth.getButtonPressed() );
        assertFalse( povButtonNorth.getButtonReleased() );
        assertFalse( povButtonEast.getButton() );
        assertFalse( povButtonEast.getButtonPressed() );
        assertTrue( povButtonEast.getButtonReleased() );
        assertTrue( povButtonSouth.getButton() );
        assertTrue( povButtonSouth.getButtonPressed() );
        assertFalse( povButtonSouth.getButtonReleased() );
        assertFalse( povButtonWest.getButton() );
        assertFalse( povButtonWest.getButtonPressed() );
        assertFalse( povButtonWest.getButtonReleased() );             

        // South
        when(mockJoystick.getPOV(id)).thenReturn(180);
        controller.update();
        assertFalse( povButtonNorth.getButton() );
        assertFalse( povButtonNorth.getButtonPressed() );
        assertFalse( povButtonNorth.getButtonReleased() );
        assertFalse( povButtonEast.getButton() );
        assertFalse( povButtonEast.getButtonPressed() );
        assertFalse( povButtonEast.getButtonReleased() );
        assertTrue( povButtonSouth.getButton() );
        assertFalse( povButtonSouth.getButtonPressed() );
        assertFalse( povButtonSouth.getButtonReleased() );
        assertFalse( povButtonWest.getButton() );
        assertFalse( povButtonWest.getButtonPressed() );
        assertFalse( povButtonWest.getButtonReleased() );             
        
        // Southwest
        when(mockJoystick.getPOV(id)).thenReturn(225);
        controller.update();
        assertFalse( povButtonNorth.getButton() );
        assertFalse( povButtonNorth.getButtonPressed() );
        assertFalse( povButtonNorth.getButtonReleased() );
        assertFalse( povButtonEast.getButton() );
        assertFalse( povButtonEast.getButtonPressed() );
        assertFalse( povButtonEast.getButtonReleased() );
        assertTrue( povButtonSouth.getButton() );
        assertFalse( povButtonSouth.getButtonPressed() );
        assertFalse( povButtonSouth.getButtonReleased() );
        assertFalse( povButtonWest.getButton() );
        assertFalse( povButtonWest.getButtonPressed() );
        assertFalse( povButtonWest.getButtonReleased() );             

        // West
        when(mockJoystick.getPOV(id)).thenReturn(270);
        controller.update();
        assertFalse( povButtonNorth.getButton() );
        assertFalse( povButtonNorth.getButtonPressed() );
        assertFalse( povButtonNorth.getButtonReleased() );
        assertFalse( povButtonEast.getButton() );
        assertFalse( povButtonEast.getButtonPressed() );
        assertFalse( povButtonEast.getButtonReleased() );
        assertFalse( povButtonSouth.getButton() );
        assertFalse( povButtonSouth.getButtonPressed() );
        assertTrue( povButtonSouth.getButtonReleased() );
        assertTrue( povButtonWest.getButton() );
        assertTrue( povButtonWest.getButtonPressed() );
        assertFalse( povButtonWest.getButtonReleased() );   
        
        // Northwest
        when(mockJoystick.getPOV(id)).thenReturn(315);
        controller.update();
        assertTrue( povButtonNorth.getButton() );
        assertTrue( povButtonNorth.getButtonPressed() );
        assertFalse( povButtonNorth.getButtonReleased() );
        assertFalse( povButtonEast.getButton() );
        assertFalse( povButtonEast.getButtonPressed() );
        assertFalse( povButtonEast.getButtonReleased() );
        assertFalse( povButtonSouth.getButton() );
        assertFalse( povButtonSouth.getButtonPressed() );
        assertFalse( povButtonSouth.getButtonReleased() );
        assertFalse( povButtonWest.getButton() );
        assertFalse( povButtonWest.getButtonPressed() );
        assertTrue( povButtonWest.getButtonReleased() );      
        
        // Unpressed
        when(mockJoystick.getPOV(id)).thenReturn(-1);
        controller.update();
        assertFalse( povButtonNorth.getButton() );
        assertFalse( povButtonNorth.getButtonPressed() );
        assertTrue( povButtonNorth.getButtonReleased() );
        assertFalse( povButtonEast.getButton() );
        assertFalse( povButtonEast.getButtonPressed() );
        assertFalse( povButtonEast.getButtonReleased() );
        assertFalse( povButtonSouth.getButton() );
        assertFalse( povButtonSouth.getButtonPressed() );
        assertFalse( povButtonSouth.getButtonReleased() );
        assertFalse( povButtonWest.getButton() );
        assertFalse( povButtonWest.getButtonPressed() );
        assertFalse( povButtonWest.getButtonReleased() );         
    }    
}