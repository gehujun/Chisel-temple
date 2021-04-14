package cm_top

import chisel3._
import chisel3.util._

class ForwardingMemory() extends Module{
    val io = IO(new Bundle{
        val rdAddr = Input(UInt(16.W))
        val rdData = Output(UInt(13.W))
        val wrEna  = Input(Bool())
        val wrData = Input(UInt(13.W))
        val wrAddr = Input(UInt(16.W))
    })

    val mem = SyncReadMem(256,UInt(13.W))

    val wrDataReg = RegNext(io.wrData)
    val doForwardReg = RegNext(io.wrAddr === io.rdAddr 
        &&io.wrEna)

    val memData = mem.read(io.rdAddr)

    when(io.wrEna){
        mem.write(io.wrAddr, io.wrData)
    }

    io.rdData := Mux(doForwardReg , wrDataReg , memData)
}

class StateMap extends Module{
  val io = IO(new Bundle{
      val y = Input(UInt(1.W))
      val cx = Input(UInt(16.W))
      val p = Output(UInt(12.W))
  })

  val dt = Wire(Vec(1024,UInt(13.W)))
  for(i <- 0 until 1024){
    dt(i) := (16384.U / (i.asUInt+i.asUInt+3.U)).asUInt
  }

  val hashTable = Module(new ForwardingMemory)
  hashTable.io.rdAddr := io.cx
  hashTable.io.wrEna := true.B
  hashTable.io.wrAddr := io.cx

  val prediction_count = hashTable.io.rdData
  val prediction = prediction_count >> 10
  val count = prediction_count & 1023.U
  val newCount = Mux((count === 1023.U),count,count+1.U)
  val mulFac = dt(count)
//   val updateValue = (prediction_count + ((((io.y<<22)-prediction)>>3) * mulFac) & 0xfffffc00L.U) | newCount
  hashTable.io.wrData := (prediction_count + ((((io.y<<22)-prediction)>>3) * mulFac) & 0xfffffc00L.U) | newCount
    
  io.p := prediction >> 10;

    // io.p := io.y+io.cx

}
