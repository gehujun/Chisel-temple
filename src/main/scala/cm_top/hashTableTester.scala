package myUnit

import chisel3.iotesters.{Driver, PeekPokeTester}

class hashTableTester(c: hashTable_5) extends PeekPokeTester(c)  {
  poke(c.io.wrEna, true)
  poke(c.io.rdEna, false)
  poke(c.io.wrAddr1, 0x0)
  poke(c.io.wrData1, 0x10000502)
  step(1)
  poke(c.io.wrEna, true)
  poke(c.io.rdEna, false)
  poke(c.io.wrAddr1, 0x1)
  poke(c.io.wrData1, 0x10000424)
  step(1)
  poke(c.io.wrEna, true)
  poke(c.io.rdEna, false)
  poke(c.io.wrAddr1, 0x2)
  poke(c.io.wrData1, 0x10000302)
  step(1)
  poke(c.io.wrEna, false)
  poke(c.io.rdEna, true)
  poke(c.io.addr1, 0x02000000)
  var data = peek(c.io.rdData1)
  var index = peek(c.io.index1)
  step(1)
  data = peek(c.io.rdData1)
  index = peek(c.io.index1)
  println(f"data : $data%x  index : $index%x")
}

object hashTableTest {
  def main(args: Array[String]): Unit = {
    if (!Driver.execute(args,() => new hashTable_5(4096))(c => new hashTableTester(c))) System.exit(1)
  }
}