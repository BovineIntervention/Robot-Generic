package frc.taurus.messages;

import java.nio.ByteBuffer;

public class MessageQueueManager {
    // singleton pattern
    private static MessageQueueManager mInstance = null;
    public static MessageQueueManager getInstance() {
        if (mInstance == null) {
             mInstance = new MessageQueueManager();
        }
        return mInstance;
    }   

    public GenericQueue<ByteBuffer> driveJoystickGoalQueue;
    public JoystickStatusQueue driveJoystickStatusQueue;

    public GenericQueue<ByteBuffer> operatorJoystickGoalQueue;
    public JoystickStatusQueue operatorJoystickStatusQueue;    

    private MessageQueueManager() {
        driveJoystickGoalQueue = new GenericQueue<ByteBuffer>();
        driveJoystickStatusQueue = new JoystickStatusQueue();

        operatorJoystickGoalQueue = new GenericQueue<ByteBuffer>();
        operatorJoystickStatusQueue = new JoystickStatusQueue();


    }




}