package cm_top

import chisel3._
import chisel3.util._
import chisel3.util.experimental.loadMemoryFromFile
import myUtil.synReg

class SerialCompressor extends Module{
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

  val pr = RegInit(2048.U)
  printf("asdasqer\n");
  val encode = Module(new encoder)
  encode.io.pin     := pr
  encode.io.i       := io.i
  encode.io.mode    := io.mode
  encode.io.x       := io.x
  encode.io.renable := io.start

  //val y_reg     = RegNext(encode.io.y)
  val out_reg   = RegInit(0.U(32.W))
  val flag_reg  = RegInit(0.U(4.W))
  out_reg := Mux(encode.io.wenable,encode.io.out,out_reg)
  flag_reg := Mux(encode.io.wenable,encode.io.flag,flag_reg)



/**
  * MODEL : order1 oder2......5 match model
  * 
  */
 
  val order1Modle = Module(new order_1)
  order1Modle.io.y      := encode.io.y
  order1Modle.io.start  := encode.io.wenable
  val (pr1,done1) = StateMap(encode.io.y,order1Modle.io.p, order1Modle.io.done)

  // val pr1 = 0.U
  // val done1 = true.B

  val highOrderModule = Module(new order(2048))
  highOrderModule.io.y      := encode.io.y
  highOrderModule.io.start  := encode.io.wenable
  // highOrderModule.io.y      := 1.U
  // highOrderModule.io.start  := true.B
  
  // var sm1 = Module(new StateMap())
  // sm1.io.y      := encode.io.y
  // sm1.io.cx     := highOrderModule.io.p1
  // sm1.io.Start  := highOrderModule.io.done
  // val pr2       = sm1.io.p
  // val done2     = sm1.io.Done
  // var sm2 = Module(new StateMap())
  // sm2.io.y      := encode.io.y
  // sm2.io.cx     := highOrderModule.io.p2
  // sm2.io.Start  := highOrderModule.io.done
  // val pr3       = sm2.io.p
  // val done3     = sm2.io.Done
  // var sm3 = Module(new StateMap)
  // sm3.io.y      := encode.io.y
  // sm3.io.cx     := highOrderModule.io.p3
  // sm3.io.Start  := highOrderModule.io.done
  // val pr4       = sm3.io.p
  // val done4     = sm3.io.Done
  // var sm4 = Module(new StateMap)
  // sm4.io.y      := encode.io.y
  // sm4.io.cx     := highOrderModule.io.p4
  // sm4.io.Start  := highOrderModule.io.done
  // val pr5       = sm4.io.p
  // val done5     = sm4.io.Done
  // var sm5 = Module(new StateMap)
  // sm5.io.y      := encode.io.y
  // sm5.io.cx     := highOrderModule.io.p5
  // sm5.io.Start  := highOrderModule.io.done
  // val pr6       = sm5.io.p
  // val done6     = sm5.io.Done

  // val syn_reg = Module(new synReg)
  // syn_reg.io.pr1 := pr1
  // syn_reg.io.pr2 := pr2
  // syn_reg.io.pr3 := pr3
  // syn_reg.io.pr4 := pr4
  // syn_reg.io.pr5 := pr5
  // syn_reg.io.pr6 := pr6
  // syn_reg.io.pr7 := 2047.U
  // syn_reg.io.done1 := done1
  // syn_reg.io.done2 := done2

  
  io.Done := true.B
  io.y := 1.U
  io.flag := 1.U
  io.out := 1.U
  
/**
  * MIXER :
  *   input Vec predictions cxt y
  *   output pr
  */
  // val mixer = Module(new Mixer(7))
  // mixer.io.predictions  := syn_reg.io.predictions
  // mixer.io.cxt          := 0.U
  // mixer.io.y            := encode.io.y
  // mixer.io.Start        := syn_reg.io.done

  // pr := Mux(mixer.io.Done,mixer.io.out,pr)
  // io.out := Mux(mixer.io.Done,out_reg,0.U)
  // io.flag := Mux(mixer.io.Done,flag_reg,0.U)
  // io.Done := mixer.io.Done
  // io.y := encode.io.y

}
