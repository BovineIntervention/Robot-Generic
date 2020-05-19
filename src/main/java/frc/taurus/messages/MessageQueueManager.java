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

    public MessageQueue<ByteBuffer> driveJoystickGoalQueue;
    public MessageQueue<ByteBuffer> driveJoystickStatusQueue;

    public MessageQueue<ByteBuffer> operatorJoystickGoalQueue;
    public MessageQueue<ByteBuffer> operatorJoystickStatusQueue;    

    private MessageQueueManager() {
        driveJoystickGoalQueue = new MessageQueue<ByteBuffer>();
        driveJoystickStatusQueue = new MessageQueue<ByteBuffer>();

        operatorJoystickGoalQueue = new MessageQueue<ByteBuffer>();
        operatorJoystickStatusQueue = new MessageQueue<ByteBuffer>();


    }




}