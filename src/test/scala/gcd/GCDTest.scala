package gcd

import chisel3._
import chisel3.iotesters._
import chisel3.iotesters.{Driver,ChiselFlatSpec,PeekPokeTester}

class GCDTest(s : GCD) extends PeekPokeTester(s){
        // for(i <- 0 until 32){
            poke(s.io.value1,2.U)
            poke(s.io.value2,3.U)
            // println("index "+"elem is : "+ peek(s.io.outputValid).toString)
            // step(1)
        // }
    }

// object GCDTest extends App{
//   chisel3.iotesters.Driver(() => new GCD()){ c =>
//     new GCDTest(c)
//   }
//    chisel3.iotesters.Driver.execute(args, () => new GCD)(c => new GCDTest(c))

// }
