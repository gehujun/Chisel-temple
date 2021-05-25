package cm_top

import chisel3._
import chisel3.util._
import chisel3.util.experimental.loadMemoryFromFile
import myUtil.synReg
// import java.beans.Encoders

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
   val xMid = Output(UInt(32.W))
   val x1 = Output(UInt(32.W))
   val x2 = Output(UInt(32.W))
  })

  val pr = RegInit(2048.U)
  // printf("asdasqer\n");
  val encode = Module(new newEncoder)
  encode.io.pin     := pr
  encode.io.i       := io.i
  encode.io.mode    := io.mode
  encode.io.x       := io.x
  encode.io.renable := io.start

  //test
  io.xMid := encode.io.xMidtest
  io.x1 := encode.io.x1test
  io.x2 := encode.io.x2test

  val out_reg   = RegInit(0.U(32.W))
  val flag_reg  = RegInit(0.U(4.W))
  val y_reg     = RegInit(0.U(1.W))
  out_reg := Mux(encode.io.wenable,encode.io.out,out_reg)
  flag_reg := Mux(encode.io.wenable,encode.io.flag,flag_reg)
  y_reg := Mux(encode.io.wenable,encode.io.y,y_reg)

  printf("y: %d prediction is : %d \n",encode.io.y,pr)

/** 
  * MODEL : order1 oder2......order5 match model
  * 
  */
 
  val order1Modle = Module(new order_1)
  order1Modle.io.y      := y_reg
  order1Modle.io.start  := encode.io.wenable
  val (pr1,done1) = StateMap(y_reg,order1Modle.io.p, order1Modle.io.done)
  // printf("order1's prediction : %d",pr1)
  // val pr1 = 2048.U
  // val done1 = true.B

  // val highOrderModule = Module(new order(2048))
  // highOrderModule.io.y      := y_reg
  // highOrderModule.io.start  := encode.io.wenable
  // highOrderModule.io.y      := 1.U
  // highOrderModule.io.start  := true.B
  
  // var sm1 = Module(new StateMap())
  // sm1.io.y      := y_reg
  // sm1.io.cx     := highOrderModule.io.p1
  // sm1.io.Start  := highOrderModule.io.done
  // val pr2       = sm1.io.p
  // val done2     = sm1.io.Done
  // var sm2 = Module(new StateMap())
  // sm2.io.y      := y_reg
  // sm2.io.cx     := highOrderModule.io.p2
  // sm2.io.Start  := highOrderModule.io.done
  // val pr3       = sm2.io.p
  // val done3     = sm2.io.Done
  // var sm3 = Module(new StateMap)
  // sm3.io.y      := y_reg
  // sm3.io.cx     := highOrderModule.io.p3
  // sm3.io.Start  := highOrderModule.io.done
  // val pr4       = sm3.io.p
  // val done4     = sm3.io.Done
  // var sm4 = Module(new StateMap)
  // sm4.io.y      := y_reg
  // sm4.io.cx     := highOrderModule.io.p4
  // sm4.io.Start  := highOrderModule.io.done
  // val pr5       = sm4.io.p
  // val done5     = sm4.io.Done
  // var sm5 = Module(new StateMap)
  // sm5.io.y      := y_reg
  // sm5.io.cx     := highOrderModule.io.p5
  // sm5.io.Start  := highOrderModule.io.done
  // val pr6       = sm5.io.p
  // val done6     = sm5.io.Done

  // val pr1 = 2048.U
  // val done1 = true.B

  val pr2 = 0.U
  val pr3 = 0.U
  val pr4 = 0.U
  val pr5 = 0.U
  val pr6 = 0.U
  val done2 = true.B

  // val matchModel = Module(new MatchModel)
  // matchModel.io.inY := y_reg
  // matchModel.io.start := encode.io.wenable
  // val pr7 = matchModel.io.toMadd
  val pr7 = 0.U
  // val done3 = matchModel.io.Dones

  val syn_reg = Module(new synReg)
  syn_reg.io.pr1 := pr1
  syn_reg.io.pr2 := pr2
  syn_reg.io.pr3 := pr3
  syn_reg.io.pr4 := pr4
  syn_reg.io.pr5 := pr5
  syn_reg.io.pr6 := pr6
  syn_reg.io.pr7 := pr7
  syn_reg.io.done1 := done1
  syn_reg.io.done2 := done2
  syn_reg.io.done3 := true.B
  
/**
  * MIXER :c
  *   input Vec predictions cxt y
  *   output pr
  */
  val mixer = Module(new Mixer(7))
  mixer.io.predictions  := syn_reg.io.predictions
  mixer.io.cxt          := 0.U
  mixer.io.y            := y_reg
  mixer.io.Start        := syn_reg.io.done

  val apm1 = Module(new APM(256))
  apm1.io.pr := mixer.io.out
  apm1.io.cx := 0.U
  apm1.io.next_y := y_reg
  apm1.io.Start   := mixer.io.Done

  pr := Mux(apm1.io.Done,apm1.io.p,pr)
  io.out := Mux(apm1.io.Done,out_reg,0.U)
  io.flag := Mux(apm1.io.Done,flag_reg,0.U)
  io.Done := apm1.io.Done
  io.y := y_reg

}
