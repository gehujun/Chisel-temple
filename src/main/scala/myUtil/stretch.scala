package myUtil

import chisel3._



// Inverse of squash. stretch(d) returns ln(p/(1-p)),
class stretch extends Module{
  val io = IO(new Bundle{
      val p = Input(SInt(12.W))
      val d = Output(SInt(8.W))
  })



  // val t = WireInit(Vec(4096,SInt(12.W)))
  //val _squash = Module(new squash())

  val pi = 0 

  val t_RAM = SyncReadMem(10,SInt(12.W))

  for(i <- -5 to 5){
    //println(" i is : "+i)
    val squshI = squash(i.asSInt)
    //println("squshI is : "+squshI.toString)
    t_RAM.write((i+5).asUInt,squshI)
  }
  
  val addr = io.p.asUInt
  io.d := t_RAM.read(addr)

}
