package cm_top

import chisel3._
import chisel3.util._


class mixer_top(n:Int) extends Module{
    val io = IO(new Bundle{
        val predictions = Input(Vec(n,SInt(13.W)))
        val cxt = Input(UInt(8.W))
        
        val i = Input(UInt(1.W))
        val mode = Input(UInt(1.W))
        val x = Input(UInt(32.W))
        val y = Output(UInt(1.W))
        val out = Output(UInt(32.W))
        val flag = Output(UInt(4.W))

        val Start = Input(Bool())
        val Done  = Output(Bool())

    })  

    val pr_reg = RegInit(2047.U)

    val encoder = Module(new encoder)
    encoder.io.pin := pr_reg
    encoder.io.i := io.i
    encoder.io.mode := io.mode
    encoder.io.x := io.x
    
    
    val encoder_done = RegNext(io.Start)
    val encoder_y =RegNext(encoder.io.y)
    val encoer_cxt = RegNext(io.cxt)
    val encoder_pre = RegNext(io.predictions)
    val encoder_out = RegNext(encoder.io.out)
    val encoder_flag = RegNext(encoder.io.flag)

    val mixer = Module(new Mixer(7))
    mixer.io.predictions := encoder_pre
    mixer.io.cxt := encoer_cxt
    mixer.io.y := encoder_y
    mixer.io.Start := encoder_done

    val mixer_done = RegNext(mixer.io.Done)
    val mixer_pr = RegNext(mixer.io.out)
    val mixer_out = RegNext(encoder_out)
    val mixer_y = RegNext(encoder_y)
    val mixer_flag = RegNext(encoder_flag)

    pr_reg  :=  Mux(mixer_done,mixer_pr,pr_reg)
    encoder.io.renable := mixer_done 

    io.y    := mixer_y
    io.out  := mixer_out
    io.flag := mixer_flag
    io.Done := mixer.io.Done

}
