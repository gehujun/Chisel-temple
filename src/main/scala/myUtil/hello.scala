package myUtil

import chisel3._
import chisel3.iotesters._
import chisel3.iotesters.{PeekPokeTester, Driver, ChiselFlatSpec}
import _root_.ip_interface.bram_io
import chisel3.util.Cat


class hello extends Module{
 val io = IO(new Bundle{
     val a = Input(UInt(2.W))
     val b = Input(UInt(2.W))
     val c = Output(UInt(2.W))
 }) 

 io.c := io.a & io.b

 val uselesswire = UInt(10.W)

}

