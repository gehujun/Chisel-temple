package myUtil

import chisel3._
import chisel3.util._
import chisel3.iotesters._
import chisel3.iotesters.{PeekPokeTester, Driver, ChiselFlatSpec}
import chisel3.util.Cat


class hello extends Module{
 val io = IO(new Bundle{
    val a = Input(UInt(2.W))
    val b = Input(UInt(2.W))
    val c = Output(UInt(4.W))

    val done = Output(Bool())
 }) 

//  io.c := io.a & io.b

//  val uselesswire = UInt(10.W)

// val idle::state1::state2::Nil = Enum(3)
// val stateReg = RegInit(idle)
// val regVec = Wire(Vec(10,UInt(10.W)))

// val ref = WireInit(VecInit(Seq.fill(10)(0.U(10.W))))

// val testHash = SyncReadMem(n,UInt(32.W))

// io.c := testHash.read(io.a+io.b);
// switch(stateReg){
//     is(idle){
        
//         stateReg := state1
//     }
//     is(state1){
//         for(i <- 1 until 10 by -1){
//             regVec(i) := 1.U
//         }
//         stateReg := state2
//     }
//     is(state2){
//         stateReg := idle
//     }
// }

// io.done := stateReg===state2


}

