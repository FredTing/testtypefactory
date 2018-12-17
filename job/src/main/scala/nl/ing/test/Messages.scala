package nl.ing.test

import nl.ing.test.mvalue.{MDouble, MValue}

case class InMessage(values: Seq[MValue])
case class OutMessage(values: Seq[MDouble])
