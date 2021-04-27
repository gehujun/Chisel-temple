package cm_top

import chisel3._
import chisel3.util._
import scala.collection.mutable
import myUtil.squash



class Mixer(n:Int) extends Module{
    val io = IO(new Bundle{
        val predictions = Input(Vec(n,SInt(13.W)))
        val cxt = Input(UInt(8.W))
        val y   = Input(UInt(1.W))
        val out = Output(UInt(12.W))
        val err = Output(SInt(32.W))
        
        val Start = Input(Bool())
        val Done  = Output(Bool())

        // val softPredictions_test = Input(SInt(13.W))
    })    
    // val out_softpredictions = RegNext(RegNext(io.softPredictions_test))

    val y_reg = RegNext(io.y)
    val strech_prediction = Wire(Vec(n,SInt(16.W)))
    for(i <- 0 until n){
        strech_prediction(i) := io.predictions(i).asSInt
    }

    // val t = SyncReadMem(4096,SInt(13.W))

    // when(reset.asBool) {
    //     for(i <- -2048 until 2048){
    //         val squashI = squash(i.asSInt)
    //         // val pi = Mux(i.asSInt === -2048.S,0.S,squashI + 1.S)
    //         // for(j <- pi to squashI)
    //         //     t(j) := i
    //         t.write(squashI.asUInt,i.asSInt)
    //     }
    // }

    val weight_buffer = SyncReadMem(80,Vec(7,SInt(32.W)))
    
    val weights = weight_buffer.read(io.cxt)
    val updateSel = WireDefault(false.B)

    val sum = Wire(Vec(7, SInt(32.W)))
    for(i <- 0 until 7){
        if(i == 0)  sum(i) := strech_prediction(i) * weights(i)
        else        sum(i) := strech_prediction(i) * weights(i) + sum(i-1)
    }
    
    val pr = squash(sum(6)>>16) 
    // printf(" pr : %d\t",pr)
    // printf(" pr : %d\t",pr)
    val pr_reg = RegInit(2047.U(12.W))
    pr_reg := Mux(updateSel,pr,pr_reg)
    io.out := pr_reg

    val err  = ((io.y<<12).asUInt - pr.asUInt).asSInt * 7.S
    // printf(" error : %d (y_reg): %d\t",err,(io.y<<12).asSInt)
    val change_weight = Wire(Vec(n,SInt(32.W)))
    for(i <- 0 until n){
        change_weight(i) := ((strech_prediction(i) * err + 0x8000.S)>>16) + weights(i)
        // printf("%d\t",change_weight(i))
    }
    
    // printf("\n")
    when(updateSel){
        weight_buffer.write(io.cxt,change_weight)
    }

    io.err := err

    val idle :: stage1 :: stage2 :: Nil = Enum(3)

    val stateReg = RegInit(idle)
    
    switch(stateReg){
      is(idle) {
        when(io.Start) {
          stateReg := stage1
        }
      }
      is(stage1) {
        updateSel := true.B
        stateReg := stage2
      }
      is(stage2) {
        updateSel := false.B
        stateReg := stage1
      }
    }

    io.Done := stateReg === stage2
}
