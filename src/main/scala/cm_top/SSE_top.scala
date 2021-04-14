package cm_top

import chisel3._
import chisel3.util._

class SSE_top(val w: Int) extends Module{
  val io = IO(new Bundle{
      val p   = Input(UInt(w.W)) 
      val cxt = Input(UInt(w.W))
      val y   = Input(UInt(8.W))
      val pr  = Output(UInt(32.W))
  })

  val dt = Wire(Vec(33,UInt(12.W)))

  for(i <- 0 until 1024){
    dt(i) := (16384.U / (i.asUInt+i.asUInt+3.U)).asUInt
  }

  val sse1 :: sse2 :: sse3 ::sse4 :: Nil = Enum(4)
  val stateReg = RegInit(sse1)

  val updateSel0 = WireDefault(false.B)
  val updateSel1 = WireDefault(false.B)

  val dtInputSel = Wire(Bool())

  switch(stateReg){
    is(sse1){
      updateSel0 := false.B
      updateSel1 := false.B
     
    }
  }



}
