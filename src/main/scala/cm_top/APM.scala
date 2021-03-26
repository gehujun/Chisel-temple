package cm_top

import chisel3._

class APM(val w : Int) extends Module{
  val io = IO(new Bundle{
      val cx = Input(UInt(w.W))
      
  })
}
