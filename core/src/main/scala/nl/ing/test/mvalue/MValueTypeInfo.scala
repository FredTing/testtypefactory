package nl.ing.test.mvalue

import java.lang.reflect
import java.util.Objects

import org.apache.flink.api.common.ExecutionConfig
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.common.typeutils.TypeSerializer

class MValueTypeInfo(tpe: reflect.Type) extends TypeInformation[MValue]  {
  override def isBasicType: Boolean = true
  override def isTupleType: Boolean = false
  override def getArity: Int = 1
  override def getTotalFields: Int = 1
  override def getTypeClass: Class[MValue] = classOf[MValue]
  override def isKeyType: Boolean = true
  override def createSerializer(config: ExecutionConfig): TypeSerializer[MValue] = new MValueSerializer(tpe)

  override def toString: String = classOf[MValue].getSimpleName

  override def canEqual(obj: Any): Boolean = obj match {
    case other: MValueTypeInfo => true
    case _                     => false
  }
  override def equals(obj: Any): Boolean = canEqual(obj)
  override def hashCode(): Int = 31 * Objects.hash(tpe) + 1
}
