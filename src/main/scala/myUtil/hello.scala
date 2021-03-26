package myUtil

import chisel3._
import chisel3.iotesters._
import chisel3.iotesters.{PeekPokeTester, Driver, ChiselFlatSpec}


class hello extends Module{
 val io = IO(new Bundle{
     val a = Input(UInt(2.W))
     val b = Input(UInt(2.W))
     val c = Output(UInt(2.W))
 }) 

 io.c := io.a & io.b

}

