package myUtil

import chisel3._
import chisel3.iotesters._
import chisel3.iotesters.{PeekPokeTester, Driver, ChiselFlatSpec}

// class helloTester(h : hello) extends PeekPokeTester(h){
//   poke(h.io.a,0.U)
//   poke(h.io.b,1.U)
//   step(1)
// //   println("Result is: " + peek(h.io.c).toString)
// }

// class helloTest

class helloTests(h:hello) extends PeekPokeTester(h){
    poke(h.io.a,0.U)
    poke(h.io.b,1.U)
    step(1)
    println("Result is: " + peek(h.io.c).toString)
}

class helloTester extends ChiselFlatSpec {
    behavior of "helloTester"
    backends foreach {backend =>
    it should s"correctly add randomly generated numbers $backend" in {
      Driver(() => new hello, backend)(c => new helloTests(c)) should be (true)
    }
  }
}

object helloTests extends App{
    chisel3.iotesters.Driver.execute(args,() => new hello())(c=>new helloTests(c))

    // chisel3.iotesters.Driver(() => new hello()){ c =>
    // new helloTests(c)
  // }

}
