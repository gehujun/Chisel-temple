package cm_top

import chisel3.iotesters.{Driver, PeekPokeTester}

class order_1Tester(c: order_1) extends PeekPokeTester(c)  {
  poke(c.io.start, true)
  poke(c.io.y, 0)
  step(48)
//  printf("%d, %d\n", peek(c.io.done), peek(c.io.p))
//  step(3)
//  printf("%d, %d\n", peek(c.io.done), peek(c.io.p))
//  step(3)
//  printf("%d, %d\n", peek(c.io.done), peek(c.io.p))
//  peek(c.io.done)
//  poke(c.io.start, true)
//  poke(c.io.y, 0)
//  step(3)
//  printf("%d, %d\n", peek(c.io.done), peek(c.io.p))
//  poke(c.io.start, true)
//  poke(c.io.y, 1)
//  step(3)
//  printf("%d, %d\n", peek(c.io.done), peek(c.io.p))
//  poke(c.io.start, true)
//  poke(c.io.y, 1)
//  step(3)
//  poke(c.io.start, true)
//  poke(c.io.y, 0)
//  step(3)
//  poke(c.io.start, true)
//  poke(c.io.y, 0)
//  step(3)
//  poke(c.io.start, true)
//  poke(c.io.y, 1)
//  step(3)
//  poke(c.io.start, true)
//  poke(c.io.y, 1)
//  step(3)
//  poke(c.io.start, true)
//  poke(c.io.y, 0)
//  step(3)
//  poke(c.io.start, true)
//  poke(c.io.y, 0)
//  step(3)
//  poke(c.io.start, true)
//  poke(c.io.y, 1)
//  step(3)
//  poke(c.io.start, true)
//  poke(c.io.y, 1)
//  step(3)
//  poke(c.io.start, true)
//  poke(c.io.y, 0)
//  step(3)
//  poke(c.io.start, true)
//  poke(c.io.y, 0)
//  step(3)
//  poke(c.io.start, true)
//  poke(c.io.y, 1)
//  step(3)
//  poke(c.io.start, true)
//  poke(c.io.y, 1)
//  step(3)
}

object order_1Test {
  def main(args: Array[String]): Unit = {
    if (!Driver.execute(args,() => new order_1())(c => new order_1Tester(c))) System.exit(1)
  }
}
