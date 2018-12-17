package nl.ing.test

import java.io.{ByteArrayInputStream, DataInputStream}

import org.apache.flink.core.memory.DataInputView

final class TestFlinkInputView(val data: Array[Byte]) extends DataInputStream(new ByteArrayInputStream(data)) with DataInputView {
  override def skipBytesToRead(numBytes1: Int): Unit = {
    var numBytes = numBytes1
    while (numBytes > 0) {
      val skipped = skipBytes(numBytes)
      numBytes -= skipped
    }
  }
}
