// automatically generated by the FlatBuffers compiler, do not modify

package frc.taurus.messages.generated;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class TestMessage2 extends Table {
  public static void ValidateVersion() { Constants.FLATBUFFERS_1_12_0(); }
  public static TestMessage2 getRootAsTestMessage2(ByteBuffer _bb) { return getRootAsTestMessage2(_bb, new TestMessage2()); }
  public static TestMessage2 getRootAsTestMessage2(ByteBuffer _bb, TestMessage2 obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public static boolean TestMessage2BufferHasIdentifier(ByteBuffer _bb) { return __has_identifier(_bb, "TST2"); }
  public void __init(int _i, ByteBuffer _bb) { __reset(_i, _bb); }
  public TestMessage2 __assign(int _i, ByteBuffer _bb) { __init(_i, _bb); return this; }

  public double dblValue() { int o = __offset(4); return o != 0 ? bb.getDouble(o + bb_pos) : 0.0; }

  public static int createTestMessage2(FlatBufferBuilder builder,
      double dbl_value) {
    builder.startTable(1);
    TestMessage2.addDblValue(builder, dbl_value);
    return TestMessage2.endTestMessage2(builder);
  }

  public static void startTestMessage2(FlatBufferBuilder builder) { builder.startTable(1); }
  public static void addDblValue(FlatBufferBuilder builder, double dblValue) { builder.addDouble(0, dblValue, 0.0); }
  public static int endTestMessage2(FlatBufferBuilder builder) {
    int o = builder.endTable();
    return o;
  }
  public static void finishTestMessage2Buffer(FlatBufferBuilder builder, int offset) { builder.finish(offset, "TST2"); }
  public static void finishSizePrefixedTestMessage2Buffer(FlatBufferBuilder builder, int offset) { builder.finishSizePrefixed(offset, "TST2"); }

  public static final class Vector extends BaseVector {
    public Vector __assign(int _vector, int _element_size, ByteBuffer _bb) { __reset(_vector, _element_size, _bb); return this; }

    public TestMessage2 get(int j) { return get(new TestMessage2(), j); }
    public TestMessage2 get(TestMessage2 obj, int j) {  return obj.__assign(__indirect(__element(j), bb), bb); }
  }
}

