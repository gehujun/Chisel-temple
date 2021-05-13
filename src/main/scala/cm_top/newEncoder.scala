package cm_top

import chisel3._
import chisel3.util._


class newEncoder extends Module{
  val io = IO(new Bundle {
    val pin = Input(UInt(12.W))
    val i = Input(UInt(1.W))
    val mode = Input(UInt(1.W))
    val x = Input(UInt(32.W))
    val renable = Input(Bool()) //true则编码器输出有效数据且寄存器变化，false则输出无效数据且寄存器不变
    val wenable = Output(Bool())
    val y = Output(UInt(1.W))
    val out = Output(UInt(32.W))
    val flag = Output(UInt(4.W))
   val xMidtest = Output(UInt(32.W))
   val x1test = Output(UInt(32.W))
   val x2test = Output(UInt(32.W))
  })

  val p = Mux(io.pin === 0.U, 1.U, io.pin)
  val x1 = RegInit(0.U(32.W))
  val x2 = RegInit("hffffffff".U(32.W))

  val xMid = x1 + ((x2 - x1) >> 12) * p + (((x2 - x1) & 0xfff.U) * p >> 12)
  
  val x1Select = Mux(io.y === 0.U, xMid + 1.U, x1)
  val x2Select = Mux(io.y === 0.U, x2, xMid)

  io.wenable := RegNext(io.renable)

  val x1_next = Wire(UInt(32.W))
  val x2_next = Wire(UInt(32.W))
   when(x1Select(31, 24) =/= x2Select(31, 24)){
    x1_next := x1Select
    x2_next := x2Select
    io.flag := "b0000".U
  } otherwise{
    when(x1Select(23, 16) =/= x2Select(23, 16)){
      x1_next := x1Select << 8
      x2_next := (x2Select << 8) | "hff".U
      io.flag := "b1000".U
    } otherwise{
      when(x1Select(15, 8) =/= x2Select(15, 8)){
        x1_next := x1Select << 16
        x2_next := (x2Select << 16) | "hffff".U
        io.flag := "b1100".U
      } otherwise{
        when(x1Select(7, 0) =/= x2Select(7, 0)){
          x1_next := x1Select << 24
          x2_next := (x2Select << 24) | "hffffff".U
          io.flag := "b1110".U
        } otherwise{
          x1_next := x1Select << 32
          x2_next := (x2Select << 32) | "hffffffff".U
          io.flag := "b1111".U
        }
      }
    }
  }

  x1 := Mux(io.wenable,x1_next,x1)
  x2 := Mux(io.wenable,x2_next,x2)

  io.out := x1Select
  io.y := Mux(io.mode === 1.U, io.x <= xMid, io.i)

  //test
  io.xMidtest := xMid
  io.x1test := x1
  io.x2test := x2

}
