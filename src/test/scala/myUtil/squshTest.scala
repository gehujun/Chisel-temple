package myUtil


import chisel3._
import chisel3.iotesters._
import chisel3.iotesters.{Driver,PeekPokeTester}

class squashTest(s : squash) extends 
    PeekPokeTester(s){
        for(i <- 0 until 33){
            println("index "+i+"elem is : "+ peek(s.io.seqT(i)).toString)
            step(1)
        }
        poke(s.io.p,123.U)
        println("squshp is : "+ peek(s.io.squshP).toString)
    }

object squashTest extends App{
//   chisel3.iotesters.Driver(() => new squash()){ c =>
//     new squashTest(c)
//   }

  chisel3.iotesters.Driver.execute(args,() => new squash())(c=>new squashTest(c))


}
