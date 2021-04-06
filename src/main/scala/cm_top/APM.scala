package cm_top

import chisel3._

//在paq8中使用step自定义，插值的精度；
//这里使用33个插值的方法，暂不提供自定义精度
class APM(val w : Int) extends Module{
  val io = IO(new Bundle{
      val cx        = Input(UInt(w.W))
      val pr        = Input(SInt(13.W))
      val next_y    = Input(UInt(1.W))
      val updateSel = Input(Bool())
      val p         = Output(UInt(12.W))

      val dtNUm     = Input(UInt(16.W))   //sse外部使用一个dt模块
      val dtIndex   = Output(UInt(10.W))  //传输给sse的dt索引
  })
  
  //传入的pr是未经过squash函数的概率（-2047 <= pr <= 2047）
  val stretchP = (io.pr+2048.S)<<5 //17: 5 index | 12 weight
  val wt = stretchP.asUInt & 0xfff.U;
  val contextIndex = io.cx * 33.U + (stretchP.asUInt >> 12)
  val cxtIndex = Reg(UInt(32.W))
  cxtIndex := contextIndex + (wt >> 11) //[更新哈希表的索引]
  
  // val hashTable = Wire(Vec((2<<w)*33,UInt(32.W)))
  // val hashTable = Wire(VecInit(Seq.fill((2<<w)*33)(1.U(32.W))))
  val hashTable = RegInit(VecInit(Seq.fill((2<<w)*33)((0x80000000L.U(32.W)))))

  val outP = RegInit(2047.U(12.W)) 
  val count =  hashTable(cxtIndex) & 1023.U
  io.dtIndex :=  count

  when(io.updateSel){
    count := Mux((count === 1023.U) , count, count+1.U)
    hashTable(cxtIndex) := (hashTable(cxtIndex) + ((((io.next_y<<22)-outP)>>3) * io.dtNUm) & 0xfffffc00L.U)  | count
    //hashTable(cxtIndex) := hashTable(cxtIndex) + 1.U
  }.otherwise{
    outP := ((hashTable(contextIndex)>>13) * (4096.U-wt) + (hashTable(contextIndex+1.U)>>13) * wt )>>19
  }
  io.p := outP
}
