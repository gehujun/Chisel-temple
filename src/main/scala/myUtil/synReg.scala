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
    val done3 = Input(Bool())

    val predictions = Output(Vec(7,SInt(13.W)))
    val done = Output(Bool())
  })

  val done1_reg = RegInit(false.B)
  val done2_reg = RegInit(false.B)
  val done3_reg = RegInit(false.B)
  
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

  done3_reg := Mux(io.done3,true.B,
                Mux(io.done,false.B,
                  done3_reg))

  io.done       := done1_reg && done2_reg && done3_reg
  // io.predictions := VecInit(io.pr1.asSInt,io.pr2.asSInt,io.pr3.asSInt,io.pr4.asSInt,io.pr5.asSInt,io.pr6.asSInt,io.pr7.asSInt)

  // to do list: use stretch function ture the predictions to -2048-2048

  io.predictions(0) := stretch(io.pr1)
  io.predictions(1) := stretch(io.pr2)
  io.predictions(2) := stretch(io.pr3)
  io.predictions(3) := stretch(io.pr4)
  io.predictions(4) := stretch(io.pr5)
  io.predictions(5) := stretch(io.pr6)
  io.predictions(6) := stretch(io.pr7)

}