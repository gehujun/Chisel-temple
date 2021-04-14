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

 val bram = Module(new bram_io)
 bram.io.clk := clock
 bram.io.resetn := reset
 bram.io.addr := Cat(0.U(30.W),io.a)
 bram.io.dina := Cat(0.U(30.W),io.b)
 io.c := bram.io.data

}

