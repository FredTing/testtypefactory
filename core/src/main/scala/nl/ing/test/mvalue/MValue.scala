package nl.ing.test.mvalue

import java.io.Serializable

import org.apache.flink.api.common.typeinfo.TypeInfo

@TypeInfo(classOf[MValueTypeInfoFactory])
abstract class MValue extends Product with Comparable[MValue] with Serializable {
  override def compareTo(o: MValue): Int = (this, o) match {
    case (me: MLong, other: MLong)     => me.value.compareTo(other.value)
    case (me: MDouble, other: MDouble) => me.value.compareTo(other.value)
    case _ =>
      throw new Exception(s"Can't compare ${this.getClass.getSimpleName} with a ${o.getClass.getSimpleName}")
  }
}

case class MLong(value: Long) extends MValue
case class MDouble(value: Double) extends MValue
