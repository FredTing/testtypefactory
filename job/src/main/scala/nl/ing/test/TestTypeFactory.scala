package nl.ing.test

import nl.ing.test.mvalue._
import org.apache.flink.api.common.state.{MapState, MapStateDescriptor}
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.KeyedProcessFunction
import org.apache.flink.streaming.api.scala.{createTypeInformation, DataStream, StreamExecutionEnvironment}
import org.apache.flink.util.Collector

object TestTypeFactory {

  import scala.language.implicitConversions

  implicit def long2MLong(v: Long): MLong = MLong(v)

  implicit def double2MDouble(v: Double): MDouble = MDouble(v)

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment

//    val inMessageTypeFactory = createTypeInformation[InMessage]
//    println(s"createTypeInformation[InMessage]: $inMessageTypeFactory")
//
//    val inMessageTypeInfo = inMessageTypeFactory
//    val inMessageSerializer = inMessageTypeInfo.createSerializer(env.getConfig)
//    val target = new TestFlinkOutputView
//    inMessageSerializer.serialize(InMessage(Seq(1L, 2L, 3.3, 4L, 7.7)), target)
//    println(s"${target.getBufferAsHex}")
//    println(s"${target.getBufferAsChar}")

    env.setParallelism(1)

    val streamIn: DataStream[InMessage] = env.fromCollection(
      Seq(InMessage(Seq(1L, 2L, 3L, 4L)),
        InMessage(Seq(2L, 3L, 4L, 5L)),
        InMessage(Seq(3L, 4L, 6L)),
        InMessage(Seq(4L, 7L)),
        InMessage(Seq(5L, 8L)),
        InMessage(Seq(6L, 9L)))
    )

    val counts = streamIn
      .keyBy(_.values.head)
      .process(new MyProcFunc)

    counts.print

    val _ = env.execute("Scala SocketTextStreamWordCount Example")
  }
}

class MyProcFunc extends KeyedProcessFunction[MValue, InMessage, OutMessage] {
  var receivedIntermediateEventKeysState: MapState[InMessage, OutMessage] = _

  override def processElement(i: InMessage, ctx: KeyedProcessFunction[MValue, InMessage, OutMessage]#Context, out: Collector[OutMessage]): Unit = {
    val values = i.values.map {
      case l: MLong => l.value.toDouble
      case _ => -1.0
    }
    val head = values.head
    out.collect(OutMessage(values.tail.map(x => MDouble(head / x))))
  }

  override def open(parameters: Configuration): Unit = {
    super.open(parameters)
    val receivedIntermediateEventKeysStateDescriptor =
      new MapStateDescriptor("unusedMapState", createTypeInformation[InMessage], createTypeInformation[OutMessage])
    receivedIntermediateEventKeysState = getRuntimeContext.getMapState(receivedIntermediateEventKeysStateDescriptor)
  }
}
