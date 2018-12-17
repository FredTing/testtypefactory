package nl.ing.test.mvalue

import java.lang.reflect
import java.util.Objects

import org.apache.flink.api.common.typeutils.{SimpleTypeSerializerSnapshot, TypeSerializer, TypeSerializerSnapshot}
import org.apache.flink.core.memory.{DataInputView, DataOutputView}

class MValueSerializer(tpe: reflect.Type) extends TypeSerializer[MValue] {
  private val TypeLong: Int = 0xF4
  private val TypeDouble: Int = 0xF5

  override def isImmutableType: Boolean = true
  override def duplicate(): TypeSerializer[MValue] = this
  override def createInstance(): MValue = tpe match {
    case t if tpe == classOf[MLong]   => MLong(0L)
    case t if tpe == classOf[MDouble] => MDouble(0.0)
    case t                            => throw new Exception(s"${tpe.getTypeName} is not supported.")
  }

  override def copy(from: MValue): MValue = from match {
    case v: MLong   => v.copy()
    case v: MDouble => v.copy()
  }

  override def copy(from: MValue, reuse: MValue): MValue = copy(from)
  override def getLength: Int = -1
  override def serialize(mValue: MValue, target: DataOutputView): Unit =
    mValue match {
      case v: MLong => target.writeByte(TypeLong); target.writeLong(v.value)
      case v: MDouble =>
        target.writeByte(TypeDouble); target.writeDouble(v.value)
      case v =>
        throw new Exception(s"Serialization of value [$v] as type ${tpe.getTypeName} is not supported.")
    }
  override def deserialize(source: DataInputView): MValue =
    source.readByte match {
      case TypeLong   => MLong(source.readLong())
      case TypeDouble => MDouble(source.readDouble())
    }
  override def deserialize(reuse: MValue, source: DataInputView): MValue = deserialize(source)
  override def copy(source: DataInputView, target: DataOutputView): Unit = serialize(deserialize(source), target)
  override def canEqual(obj: Any): Boolean = obj.isInstanceOf[MValueSerializer]

  override def snapshotConfiguration: TypeSerializerSnapshot[MValue] = new MValueSerializerSnapshot

  // ------------------------------------------------------------------------

  /**
    * Serializer configuration snapshot for compatibility and format evolution.
    */
  final class MValueSerializerSnapshot() extends SimpleTypeSerializerSnapshot[MValue](classOf[MValueSerializer]) {}

  override def equals(obj: Any): Boolean = canEqual(obj)
  override def hashCode(): Int = 31 * Objects.hash(tpe) + 1
}
