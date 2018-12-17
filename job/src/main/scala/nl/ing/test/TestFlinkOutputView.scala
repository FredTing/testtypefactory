package nl.ing.test

import java.io.{ByteArrayOutputStream, DataOutputStream}

import org.apache.flink.core.memory.{DataInputView, DataOutputView}

final class TestFlinkOutputView() extends DataOutputStream(new ByteArrayOutputStream(4096)) with DataOutputView {
  def getBufferAsHex: String = getByteArray.map(b => f" $b%02X").mkString

  def getBufferAsChar: String =
    getByteArray
      .map(b => s" ${if (b > 32 && b <= 255) b.toChar else "."} ")
      .mkString

  private def getByteArray: Array[Byte] =
    out.asInstanceOf[ByteArrayOutputStream].toByteArray

  def getInputView: TestFlinkInputView = {
    val baos = out.asInstanceOf[ByteArrayOutputStream]
    new TestFlinkInputView(baos.toByteArray)
  }

  override def skipBytesToWrite(numBytes: Int): Unit = {
    var i = 0
    while (i < numBytes) {
      write(0)
      i = i - 1
    }
  }
  override def write(source: DataInputView, numBytes: Int): Unit = {
    val buffer = new Array[Byte](numBytes)
    source.readFully(buffer)
    write(buffer)
  }
}
