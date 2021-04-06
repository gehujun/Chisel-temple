package cm_top

import chisel3._

class SSE_top(val w: Int) extends Module{
  val io = IO(new Bundle{
      val p   = Input(UInt(w.W)) 
      val cxt = Input(UInt(w.W))
      val y   = Input(UInt(8.W))
      val pr  = Output(UInt(32.W))
  })

  val dt = Wire(Vec(33,UInt(12.W)))

  for(i <- 0 until 1024){
    dt(i) = (16384.U / (i.asUInt+i.asUInt+3.U)).asUInt
  }



}
