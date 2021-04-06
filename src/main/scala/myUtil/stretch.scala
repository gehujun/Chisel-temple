package myUtil

import chisel3._



// Inverse of squash. stretch(d) returns ln(p/(1-p))
//优化：加大宽度，减少深度(代做)
class stretch extends Module{
  val io = IO(new Bundle{
      val p = Input(SInt(12.W))
      val d = Output(SInt(8.W))
  })

  //第一版：使用RAM实现4096的表，较低的值的时候可以生成Verilog,深度太高的时候不行
  // val t_RAM = SyncReadMem(10,SInt(12.W))
  //  for(i <- -5 until 5){
  //   val squashI = squash(i.asSInt)   
  //   t_RAM.write((i+5).asUInt,squashI)
  // }

  //第二版：使用ROM实现4096表，
  val t = Wire(Vec(4096,SInt(13.W)))

  for(i <- -2047 to 2047){
    val squashI = squash(i.asSInt)
    t(i+2047) := squashI.asSInt
  }

  t(4095) := 2047.S
  val addr = io.p.asUInt
  io.d := t(addr)

}
