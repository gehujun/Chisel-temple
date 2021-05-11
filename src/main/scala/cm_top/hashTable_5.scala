package cm_top

import chisel3._
import chisel3.util._

class hashTable_5(N : Int) extends Module{//n >= 16 * 4, n = 2 ^ x, B默认是16了
  val io = IO(new Bundle {
    val wrEna = Input(Bool()) //写使能，0读，1写。写使能只有一个，order2-5一起写
    val rdEna = Input(Bool()) //读使能，读的时候涉及替换，所以也需要使能信号
    //读地址
    val addr1 = Input(UInt(32.W))
    val addr2 = Input(UInt(32.W))
    val addr3 = Input(UInt(32.W))
    val addr4 = Input(UInt(32.W))
    val addr5 = Input(UInt(32.W))
    //读数据
    val rdData1 = Output(UInt(128.W))
    val rdData2 = Output(UInt(128.W))
    val rdData3 = Output(UInt(128.W))
    val rdData4 = Output(UInt(128.W))
    val rdData5 = Output(UInt(128.W))
    //输出读数据的时候顺便把index输出，方便写回
    val index1 = Output(UInt(log2Up(N >> 4).W))
    val index2 = Output(UInt(log2Up(N >> 4).W))
    val index3 = Output(UInt(log2Up(N >> 4).W))
    val index4 = Output(UInt(log2Up(N >> 4).W))
    val index5 = Output(UInt(log2Up(N >> 4).W))
    //与index对应的写地址
    val wrAddr1 = Input(UInt(log2Up(N >> 4).W))
    val wrAddr2 = Input(UInt(log2Up(N >> 4).W))
    val wrAddr3 = Input(UInt(log2Up(N >> 4).W))
    val wrAddr4 = Input(UInt(log2Up(N >> 4).W))
    val wrAddr5 = Input(UInt(log2Up(N >> 4).W))
    //写入数据
    val wrData1 = Input(UInt(128.W))
    val wrData2 = Input(UInt(128.W))
    val wrData3 = Input(UInt(128.W))
    val wrData4 = Input(UInt(128.W))
    val wrData5 = Input(UInt(128.W))
  })
  val t = SyncReadMem(N >> 4, UInt(128.W)) //一行是一个element，共16byte
  val i1 = Wire(Vec(5, UInt(32.W)))
  val i = Wire(Vec(5, UInt(32.W)))
  val chk = Wire(Vec(5, UInt(8.W)))
  val index = Wire(Vec(5, UInt(log2Up(N >> 4).W)))
  val t1 = Wire(Vec(5, UInt(128.W)))
  val t2 = Wire(Vec(5, UInt(128.W)))
  val t3 = Wire(Vec(5, UInt(128.W)))
  val iordData = Wire(Vec(5, UInt(128.W)))
  val ioindex = Wire(Vec(5, UInt(log2Up(N >> 4).W)))

  i1(0) := io.addr1 * 123456791.U
  i1(1) := io.addr2 * 123456791.U
  i1(2) := io.addr3 * 123456791.U
  i1(3) := io.addr4 * 123456791.U
  i1(4) := io.addr5 * 123456791.U
  for(j <- 0 to 4){
    i(j) := (i1(j) << 16 | i1(j) >> 16) * 234567891.U
    chk(j) := i(j) >> 24
    index(j) := ((i(j) * 16.U) & (N.U - 16.U)) >> 4 //此处索引为16byte一个元素的索引，故是源码中的i>>4的结果
    t1(j) := t.read(index(j))
    t2(j) := t.read(index(j)^1.U) //源代码中为i^B即i……16，这里都右移了4位
    t3(j) := t.read(index(j)^2.U)
    //随便赋值 //写在状态机里面的话会出错，感觉是chisel自己的问题啊...
    iordData(j) := 0.U
    ioindex(j) := 0.U
  }
  val idle :: read :: Nil = Enum(2)
  val state = RegInit(idle)

  switch(state){
    is(idle){
      when(io.rdEna){
//        printf("  hash : read addr is %x, %x, %x, %x, %x\n", io.addr1, io.addr2, io.addr3, io.addr4, io.addr5)
//        printf("  hash : index is %x, %x, %x, %x, %x\n", index(0), index(1), index(2), index(3), index(4))
        state := read
      }
    }
    is(read){
      state := idle
      for(j <- 0 to 4){
        when(t1(j)(7, 0) === chk(j)){ //低位是chk
          iordData(j) := t1(j)
          ioindex(j) := index(j)
        } .elsewhen(t2(j)(7, 0) === chk(j)){
          iordData(j) := t2(j)
          ioindex(j) := index(j)^1.U
        } .elsewhen(t3(j)(7, 0) === chk(j)){
          iordData(j) := t3(j)
          ioindex(j) := index(j)^2.U
        } .otherwise{
          iordData(j) := Cat(0.U(120.W), chk(j))
          when(t1(j)(15, 8).asUInt <= t2(j)(15, 8).asUInt && t1(j)(15, 8).asUInt <= t3(j)(15, 8).asUInt){
            t.write(index(j), Cat(0.U(120.W), chk(j)))
            ioindex(j) := index(j)
          } .elsewhen(t2(j)(15, 8).asUInt <= t3(j)(15, 8).asUInt){
            t.write(index(j)^1.U, Cat(0.U(120.W), chk(j)))
            ioindex(j) := index(j)^1.U
          } .otherwise{
            t.write(index(j)^2.U, Cat(0.U(120.W), chk(j)))
            ioindex(j) := index(j)^2.U
          }
        }
      }
    }
  }
  io.index1 := ioindex(0)
  io.index2 := ioindex(1)
  io.index3 := ioindex(2)
  io.index4 := ioindex(3)
  io.index5 := ioindex(4)
  io.rdData1 := iordData(0)
  io.rdData2 := iordData(1)
  io.rdData3 := iordData(2)
  io.rdData4 := iordData(3)
  io.rdData5 := iordData(4)

  when(io.wrEna){
    t.write(io.wrAddr1, io.wrData1)
    t.write(io.wrAddr2, io.wrData2)
    t.write(io.wrAddr3, io.wrData3)
    t.write(io.wrAddr4, io.wrData4)
    t.write(io.wrAddr5, io.wrData5)
  }
}