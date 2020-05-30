package frc.taurus.messages;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.Optional;

import com.google.flatbuffers.FlatBufferBuilder;


public abstract class MessageQueue<T> extends GenericQueue<ByteBuffer> {

    static int defaultQueueSize = 200;
    Type type;

    public MessageQueue() {
        this(defaultQueueSize);
    }

    public MessageQueue(final int size) {
        super(size);

        type = ((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public void writeMessage(FlatBufferBuilder builder, int offset) {
        builder.finish(offset);
        ByteBuffer bb = builder.dataBuffer();
        this.write(bb);
    }

    public MessageQueue<T>.MessageReader makeMessageReader() {
        return new MessageReader(this);
    }

    public Type type() { return type; }


    public class MessageReader extends GenericQueue<ByteBuffer>.QueueReader {

        // needed to cal getRootAs* method
        Class<?> cls;
        Method getRootAsMethod;

        protected MessageReader(MessageQueue<T> queue) {
            super(queue);

            // construct the name of the FlatBuffers getRootAs* method
            String qualifiedName = type.toString();
            int idx = qualifiedName.lastIndexOf(".")+1;
            String simpleName = qualifiedName.substring(idx);        
            String methodName = "getRootAs" + simpleName;
            
            try {
                cls = (Class<?>) type;
                getRootAsMethod = cls.getDeclaredMethod(methodName, ByteBuffer.class);
            } catch (NoSuchMethodException e) {
                System.out.println("Method: " + type.getTypeName() + "." + getRootAsMethod + "() not found in MessageReader constructor.");
                e.printStackTrace();
                System.exit(-1);
            }            
        }
        
        public Optional<T> readNextMessage() {
            Optional<ByteBuffer> obb = read();
            return convertToMessage(obb);
        }

        public Optional<T> readLastMessage() {
            Optional<ByteBuffer> obb = readLast();
            return convertToMessage(obb);
        }

        // common conversion process for readNextMessage(), readLastMessage()
        private Optional<T> convertToMessage(Optional<ByteBuffer> obb) {
            if (obb.isEmpty()) {
                return Optional.empty();
            }
            ByteBuffer bb = obb.get();

            // generic way to call T.getRootAsT(bb) where T is the type of this MessageQueue
            // For example, if T is JoystickStatus, call JoystickStatus.getRootAsJoystickStatus(bb)
            try {
                @SuppressWarnings("unchecked")
                var out = (T) getRootAsMethod.invoke(cls, bb);
                return Optional.of(out);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                e.printStackTrace();
            } 

            return Optional.empty();
        }
    
        public Type type() { return type; }
    }    
}