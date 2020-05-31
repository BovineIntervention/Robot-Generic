package frc.taurus.joystick;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.ByteBuffer;

import org.junit.Test;

import edu.wpi.first.wpilibj.Joystick;
import frc.taurus.messages.MessageQueue;

public class ControllerManagerTest {

  // check that getController() returns the correct classes
  @Test
  public void getControllerTest() {
    double deadband = 0.05;
    var dummyStatusQueue = new MessageQueue<ByteBuffer>();
    var dummyGoalQueue = new MessageQueue<ByteBuffer>();
    ControllerManager manager = new ControllerManager();
    Controller xbox = manager.getController(ControllerType.XBOX, new Joystick(0), deadband, 
                                            dummyStatusQueue, dummyGoalQueue);

    Controller tm = manager.getController(ControllerType.THRUSTMASTER, new Joystick(1), deadband, 
                                          dummyStatusQueue, dummyGoalQueue);

    Controller bb = manager.getController(ControllerType.BUTTON_BOARD, new Joystick(2), deadband, 
                                          dummyStatusQueue, dummyGoalQueue);

    assertEquals(XboxController.class, xbox.getClass());
    assertEquals(ThrustmasterController.class, tm.getClass());
    assertEquals(ButtonBoardController.class, bb.getClass());
  }

  // check for exception if same port is used twice
  @Test (expected = RuntimeException.class)
  public void duplicatePortTest() {
    double deadband = 0.05;
    var dummyStatusQueue = new MessageQueue<ByteBuffer>();
    var dummyGoalQueue = new MessageQueue<ByteBuffer>();
    ControllerManager manager = new ControllerManager();
    Controller xbox1 = manager.getController(ControllerType.XBOX, new Joystick(0), deadband, 
                                              dummyStatusQueue, dummyGoalQueue);
    Controller xbox2 = manager.getController(ControllerType.XBOX, new Joystick(0), deadband, 
                                              dummyStatusQueue, dummyGoalQueue);

    // we shouldn't hit this line because of the RuntimeException                                              
    assert(false);                                              
  }

  // check that the same controller type can be used on different ports
  @Test
  public void sameJoystickTest() {
    double deadband = 0.05;
    var dummyStatusQueue = new MessageQueue<ByteBuffer>();
    var dummyGoalQueue = new MessageQueue<ByteBuffer>();
    ControllerManager manager = new ControllerManager();
    Controller xbox1 = manager.getController(ControllerType.XBOX, new Joystick(0), deadband, 
                                              dummyStatusQueue, dummyGoalQueue);
    Controller xbox2 = manager.getController(ControllerType.XBOX, new Joystick(1), deadband, 
                                              dummyStatusQueue, dummyGoalQueue);
    Controller xbox3 = manager.getController(ControllerType.XBOX, new Joystick(2), deadband, 
                                              dummyStatusQueue, dummyGoalQueue);
    Controller xbox4 = manager.getController(ControllerType.XBOX, new Joystick(3), deadband, 
                                              dummyStatusQueue, dummyGoalQueue);
    assert(true); // we made it this far if getController didn't throw an exception
  }

  // check that controllers are updated (mock the joysticks)
  public void updateTest() {
    double deadband = 0.05;
    var dummyStatusQueue = new MessageQueue<ByteBuffer>();
    var dummyGoalQueue = new MessageQueue<ByteBuffer>();
    ControllerManager manager = new ControllerManager();

    Joystick mockJoystick1 = mock(Joystick.class);
    Joystick mockJoystick2 = mock(Joystick.class);
    Joystick mockJoystick3 = mock(Joystick.class);

    // have the mock joysticks report different ports
    when(mockJoystick1.getPort()).thenReturn(1);
    when(mockJoystick2.getPort()).thenReturn(2);
    when(mockJoystick3.getPort()).thenReturn(3);

    // create 3 controllers
    Controller ctrlr1 = manager.getController(ControllerType.XBOX, mockJoystick1, deadband, 
                                            dummyStatusQueue, dummyGoalQueue);

    Controller ctrlr2 = manager.getController(ControllerType.THRUSTMASTER, mockJoystick2, deadband, 
                                          dummyStatusQueue, dummyGoalQueue);

    Controller ctrlr3 = manager.getController(ControllerType.BUTTON_BOARD, mockJoystick3, deadband, 
                                          dummyStatusQueue, dummyGoalQueue);

    int id = 13;   // the button we are testing

    // press on 1st controller
    when(mockJoystick1.getRawButton(id)).thenReturn(true);
    when(mockJoystick2.getRawButton(id)).thenReturn(false);
    when(mockJoystick3.getRawButton(id)).thenReturn(false);

    manager.update(); // this is what we are testing

    assertTrue(ctrlr1.getButton(id));
    assertFalse(ctrlr2.getButton(id));
    assertFalse(ctrlr3.getButton(id));

    // press on 2nd controller
    when(mockJoystick1.getRawButton(id)).thenReturn(false);
    when(mockJoystick2.getRawButton(id)).thenReturn(true);
    when(mockJoystick3.getRawButton(id)).thenReturn(false);

    manager.update(); // this is what we are testing

    assertFalse(ctrlr1.getButton(id));
    assertTrue(ctrlr2.getButton(id));
    assertFalse(ctrlr3.getButton(id));

    // press on 3rd controller
    when(mockJoystick1.getRawButton(id)).thenReturn(false);
    when(mockJoystick2.getRawButton(id)).thenReturn(false);
    when(mockJoystick3.getRawButton(id)).thenReturn(true);

    manager.update(); // this is what we are testing

    assertFalse(ctrlr1.getButton(id));
    assertFalse(ctrlr2.getButton(id));
    assertTrue(ctrlr3.getButton(id));

  }

}