package myUtil

import chisel3._
import chisel3.iotesters._
import chisel3.iotesters.{Driver,PeekPokeTester}

class stretchTests(s:stretch) extends PeekPokeTester(s){
  poke(s.io.p,0.S)
  println("stretch of 0 is : "+ peek(s.io.d).toString)

}

object stretchTester extends App{
  
  chisel3.iotesters.Driver.execute(args,() => new stretch())(c=>new stretchTests(c))
  
  // chisel3.iotesters.Driver(() => new stretch()){ c =>
  //   new stretchTests(c)
  // }

}