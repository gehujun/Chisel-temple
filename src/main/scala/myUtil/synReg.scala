package myUtil


import chisel3._
import chisel3.util._
import chisel3.util.experimental.loadMemoryFromFile

class synReg extends Module{
  val io = IO(new Bundle{
    val pr1 = Input(UInt(12.W))
    val pr2 = Input(UInt(12.W))
    val pr3 = Input(UInt(12.W))
    val pr4 = Input(UInt(12.W))
    val pr5 = Input(UInt(12.W))
    val pr6 = Input(UInt(12.W))
    val pr7 = Input(UInt(12.W))

    val done1 = Input(Bool())
    val done2 = Input(Bool())

    val predictions = Output(Vec(7,SInt(13.W)))
    val done = Output(Bool())
  })

  val done1_reg = RegInit(false.B)
  val done2_reg = RegInit(false.B)

  
  when(io.done1){
    done1_reg := true.B;
  }.elsewhen(io.done){
    done1_reg := false.B
  } .otherwise{
    done1_reg := done1_reg
  }

  when(io.done2){
    done2_reg := true.B
  }.elsewhen(io.done){
    done2_reg :=false.B
  }.otherwise{
    done2_reg := done2_reg
  }

  io.done       := done1_reg && done2_reg
  io.predictions := VecInit(io.pr1.asSInt,io.pr2.asSInt,io.pr3.asSInt,io.pr4.asSInt,io.pr5.asSInt,io.pr6.asSInt,io.pr7.asSInt)
}