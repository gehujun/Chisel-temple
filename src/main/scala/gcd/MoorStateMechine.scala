package gcd

import chisel3._
import chisel3.util._

class BRAM extends BlackBox{
  val io = IO(new Bundle{
    
  })
}

class MoorStateMechine extends Module{
  val io =  IO(new Bundle{
    val start = Input(Bool())
    val done  = Output(Bool())
  })

  val green :: red :: blue :: Nil = Enum(3)
  val stateReg = RegInit(green)



}
