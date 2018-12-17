package nl.ing.test.mvalue

import java.lang.reflect
import java.util

import org.apache.flink.api.common.typeinfo.{TypeInfoFactory, TypeInformation}

class MValueTypeInfoFactory extends TypeInfoFactory[MValue] {
  override def createTypeInfo(t: reflect.Type, genericParameters: util.Map[String, TypeInformation[_]]): TypeInformation[MValue] = new MValueTypeInfo(t)
}
