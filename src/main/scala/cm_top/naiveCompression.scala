package cm_top

import chisel3._
import chisel3.util._
import chisel3.util.experimental.loadMemoryFromFile

class naiveCompression extends Module {
  val io = IO(new Bundle {
//    val pin = Input(UInt(12.W))
    val i = Input(UInt(1.W))
    val mode = Input(UInt(1.W))
    val x = Input(UInt(32.W))
    val y = Output(UInt(1.W))
    val out = Output(UInt(32.W))
    val flag = Output(UInt(4.W))

    val start = Input(Bool())
    val Done = Output(Bool())
//    val xMid = Output(UInt(32.W))
//    val x1 = Output(UInt(32.W))
//    val x2 = Output(UInt(32.W))
  })

  val i_next = RegNext(io.i)

  val encode = Module(new encoder)

  encode.io.i := io.i
  encode.io.mode := io.mode

  encode.io.x := io.x
  io.y := encode.io.y
  io.out := encode.io.out
  io.flag := encode.io.flag
  
  val sm  = Module(new StateMap)
  val pr = RegInit(2048.U)
  pr := Mux(io.Done,sm.io.p,pr)
//  val c0 = UInt(8.W)
  sm.io.Start := encode.io.wenable
  sm.io.y := encode.io.y
  sm.io.cx := 0.U
  encode.io.pin := pr
  encode.io.renable := io.start
  
  io.Done := sm.io.Done
  printf(" input y is %d \n",encode.io.y)
  printf(" sm.io.p is %d ",sm.io.p)

}
