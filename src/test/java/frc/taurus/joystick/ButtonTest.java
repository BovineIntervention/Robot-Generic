package frc.taurus.joystick;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import org.junit.Test;

import edu.wpi.first.wpilibj.Joystick;

public class ButtonTest {
    @Test 
    public void pressTest() {

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
}