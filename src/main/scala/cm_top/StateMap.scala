package cm_top

import chisel3._
import chisel3.util._

class ForwardingMemory() extends Module{
    val io = IO(new Bundle{
        val rdAddr = Input(UInt(16.W))
        val rdData = Output(UInt(32.W))
        val wrEna  = Input(Bool())
        val wrData = Input(UInt(32.W))
        val wrAddr = Input(UInt(16.W))
        
    })

    val mem = SyncReadMem(256,UInt(32.W))

    val wrDataReg = RegNext(io.wrData)
    val doForwardReg = RegNext(io.wrAddr === io.rdAddr 
        &&io.wrEna)

    val memData = mem.read(io.rdAddr)

    when(io.wrEna){
        mem.write(io.wrAddr, io.wrData)
    }

    io.rdData := Mux(doForwardReg , wrDataReg , memData)
}

class SinglePortRAM extends Module{
  val io = IO(new Bundle{
    val addr = Input(UInt(16.W))
    val rdData = Output(UInt(32.W))
    val ena = Input(Bool())
    val wrEna  = Input(Bool())
    val wrData = Input(UInt(32.W))
  })
  
  val mem = SyncReadMem(256,UInt(32.W))

  when(io.ena){
    when(io.wrEna){
      mem.write(io.addr,io.wrData)
      io.rdData := DontCare
    }.otherwise{
      io.rdData := mem.read(io.addr)
    }
  } .otherwise {
    io.rdData := DontCare
  }

}

class StateMap extends Module{
  val io = IO(new Bundle{
      val y = Input((UInt(1.W)))
      val cx = Input(UInt(16.W))
      val p = Output(UInt(12.W))
      //control signal
      val Start  = Input(Bool())
      val Done  = Output(Bool())

  })
  val updateSel = WireDefault(false.B)
  val cx = RegNext(io.cx)

  val dt = Wire(Vec(1024,UInt(13.W)))
  for(i <- 0 until 1024){
    dt(i) := (16384.U / (i.asUInt+i.asUInt+3.U)).asUInt
  }

//双接口读写RAM
  val hashTable = Module(new ForwardingMemory())
  hashTable.io.rdAddr := io.cx
  hashTable.io.wrEna := updateSel
  hashTable.io.wrAddr := cx
//单接口RAM
  // val hashTable = Module(new SinglePortRAM())
  // hashTable.io.addr := io.cx
  // hashTable.io.wrEna := io.updateSel
  // hashTable.io.ena := true.B
  // hashTable.io.wrData := 0x000003ffL.U 

  val pc = hashTable.io.rdData
  
  val rdata = RegInit(0x80000000L.U(32.W))
  val prediction_count = Mux(pc===(0x00000000L).U,0x80000000L.U,pc)
  // printf("statemap prediction :%x",prediction_count)
  val prediction = prediction_count >> 10
  val count = prediction_count & 1023.U
  val newCount = Mux((count === 1023.U),count,count+1.U)
  val mulFac = dt(count)
  // val mulFac = 8.U

 
  val updateValue = (prediction_count + ((((io.y<<22)-prediction)>>3) * mulFac) & 0xfffffc00L.U) | newCount
  
  hashTable.io.wrData := updateValue
  rdata := Mux(updateSel , updateValue , rdata)

  io.p := rdata >> 20

  val idle :: stage1 :: stage2 :: Nil = Enum(3)

  val stateReg = RegInit(idle)

  switch(stateReg){
    is(idle) {
      updateSel := false.B
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
