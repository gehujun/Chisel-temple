package ip_interface

import chisel3._

class bram_io extends BlackBox{
  val io = IO(new Bundle{
      val clk = Input(Clock())
      val resetn = Input(Bool())
      val addr   = Input(UInt(32.W))
      val dina   = Input(UInt(32.W))
      val data   = Output(UInt(32.W))
  })

}
