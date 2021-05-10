package myUnit

import chisel3.iotesters.{Driver, PeekPokeTester}

class orderTester(c: order) extends PeekPokeTester(c) {
  poke(c.io.start, true)
  poke(c.io.y, 1)
  step(100)
}

object orderTest {
  def main(args: Array[String]): Unit = {
    if (!Driver.execute(args,() => new order(4096))(c => new orderTester(c))) System.exit(1)
  }
}
object orderTest2 {
  def main(args: Array[String]): Unit = {
    if (!Driver.execute(Array(
      "--generate-vcd-output", "on",
      "--target-dir", "C:/Users/82459/Desktop/",
      "--top-name", "orderTest",
    ),() => new order(4096))(c => new orderTester(c))) System.exit(1)
  }
}