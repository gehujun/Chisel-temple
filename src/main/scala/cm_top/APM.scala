package cm_top

import chisel3._
import chisel3.util._
import myUtil.stretch
import myUtil.squash

//在paq8中使用step自定义，插值的精度；
//这里使用33个插值的方法，暂不提供自定义精度
class APM(val n : Int) extends Module{
  val io = IO(new Bundle{
      val cx        = Input(UInt(32.W))
      val pr        = Input(UInt(13.W))
      val next_y    = Input(UInt(1.W))
      // val updateSel = Input(Bool())
      val p         = Output(UInt(12.W))

      val Start  = Input(Bool())
      val Done  = Output(Bool()) 
  })

  val updateSel = WireDefault(false.B)

  //传入的pr是未经过squash函数的概率（-2047 <= pr <= 2047）

  val stretchP = ((stretch(io.pr)+2048.S) * 23.S).asUInt //17: 5 index | 12 weight
  val wt = stretchP & 0xfff.U;
  val contextIndex = io.cx * 24.U + (stretchP >> 12)
  val cxtIndex = Reg(UInt(32.W))

  // val hashTable = Module(new ForwardingMemory(n))
  val hashTable = SyncReadMem(n*24,UInt(32.W))
  // hashTable.io.rdAddr := contextIndex
  // hashTable.io.wrEna  := updateSel
  // hashTable.io.wrAddr := contextIndex  
  val p_c = hashTable.read(contextIndex)
  val p_c_next = hashTable.read(contextIndex+1.U)
  val updatePC = hashTable.read(cxtIndex)
  

  val read_1 = Module(new apm_rinit)
  read_1.io.cxt := contextIndex
  read_1.io.pr  := p_c
  val pc_get = read_1.io.out
 
  val read_2 = Module(new apm_rinit)
  read_2.io.cxt := contextIndex+1.U
  read_2.io.pr  := p_c_next
  val pc_next_get =  read_2.io.out
  printf("y:%d %d %d\n",io.next_y,pc_get,pc_next_get)

  val read_3 = Module(new apm_rinit)
  read_3.io.cxt := cxtIndex
  read_3.io.pr  := updatePC

  val wcePr = Reg(UInt(32.W))

  //update datapath
  def MulTable() :  Array[Int] = {
    var dt:Array[Int] = Array.fill(1024)(0)
    for(i<- 0 until 1024)
      dt(i) = 16384/(i+i+3);
    dt
  }
  var dtTable = MulTable()
  val dt = WireInit(VecInit(dtTable.map(_.U(13.W))))
  

  val prediction_count = read_3.io.out
  val prediction = prediction_count >> 10
  val count = prediction_count & 1023.U
  val newCount = Mux((count === 255.U),count,count+1.U)
  val mulFac = dt(count)
  printf("count : %d ",count)
  printf("mulFac : %d\n",mulFac)
  val changeValue = (((((io.next_y<<22)-prediction).asSInt>>3) * mulFac)).asSInt
  val updateValue = (prediction_count.asSInt + changeValue).asUInt & 0xfffffc00L.U | newCount
  
  when(updateSel){
    wcePr := (pc_get>>13)*(0x1000.U-wt) + (pc_next_get>>13)*wt>>19
    cxtIndex := contextIndex + (wt >> 11) //[更新哈希表的索引]
    hashTable.write(cxtIndex,updateValue)
  }.otherwise{ 
    wcePr     := wcePr
    cxtIndex  := cxtIndex
  }
  
  io.p := wcePr
  // val hashTable = Wire(Vec((2<<w)*33,UInt(32.W)))
  // val hashTable = Wire(VecInit(Seq.fill((2<<w)*33)(1.U(32.W))))
  // val hashTable = RegInit(VecInit(Seq.fill((2<<w)*33)((0x80000000L.U(32.W)))))

  // val outP = RegInit(2047.U(12.W)) 
  // val count =  hashTable(cxtIndex) & 1023.U
  // io.dtIndex :=  count

  // when(updateSel){
  //   //count := Mux((count === 1023.U) , count, count+1.U)
  //   hashTable(cxtIndex) := (hashTable(cxtIndex) + ((((io.next_y<<22)-outP)>>3) * io.dtNUm) & 0xfffffc00L.U)  | count
  //   //hashTable(cxtIndex) := hashTable(cxtIndex) + 1.U
  // }.otherwise{
  //   outP := ((hashTable(contextIndex)>>13) * (4096.U-wt) + (hashTable(contextIndex+1.U)>>13) * wt )>>19
  // }
  // io.p := outP

  //controller FSM
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
      stateReg := idle
    }
  }

  io.Done := stateReg === stage2

}

class apm_rinit extends Module{
  val io = IO(new Bundle{
    val pr = Input(UInt(32.W))
    val cxt = Input(UInt(32.W))
    val out = Output(UInt(32.W))
  })
  val p = (((io.cxt%24.U*2.U+1.U)*4096.U)/48.U-2048.U).asSInt;
  val value = Wire(UInt(32.W))
  value := ((squash(p)<<20)+6.U)
  io.out := Mux(io.pr===0.U,value,io.pr)
}