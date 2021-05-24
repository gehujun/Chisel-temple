package myUtil

import chisel3._



// Inverse of squash. stretch(d) returns ln(p/(1-p))
//优化：加大宽度，减少深度(代做)
class stretch extends Module{
  val io = IO(new Bundle{
      val p = Input(UInt(12.W))
      val d = Output(SInt(13.W))
  })

  //第一版：使用RAM实现4096的表，较低的值的时候可以生成Verilog,深度太高的时候不行
  // val t_RAM = SyncReadMem(10,SInt(12.W))
  //  for(i <- -5 until 5){
  //   val squashI = squash(i.asSInt)   
  //   t_RAM.write((i+5).asUInt,squashI)
  // }

  //第二版：使用ROM实现4096表，太大了.而且实现错误
  // val t = Wire(Vec(4096,SInt(13.W)))

  // for(i <- -2047 to 2047){
  //   val squashI = squash(i.asSInt)
  //   t(i+2047) := squashI.asSInt
  // }

  // t(4095) := 2047.S
  // val addr = io.p.asUInt
  // io.d := t(addr)

  //第三版。使用Vectinitde array.map,成功通过测试✌

  def ScalaSquash(d : Int) : Int = {
    var res = -1
    var t = Seq(1,2,3,6,10,16,27,45,73,120,194,310,488,747,1101,
            1546,2047,2549,2994,3348,3607,3785,3901,3975,4022,
            4050,4068,4079,4085,4089,4092,4093,4094)
    if(d > 2047) res = 4095
    else if(d < -2047)  res = 0
    else{
        var w = d&127
        var index = (d>>7) +16
        res = (t(index)*(128-w)+t(index + 1)*w+64) >> 7
    }
    res
  }

  def ScalaStrectch() :  Array[Int] = {
    var t:Array[Int] = Array.fill(4096)(2047)
    var pi = 0
    for(x <- -2047 to 2047){
      var i = ScalaSquash(x)
      for(j <- pi to i){
        t(j) = x
      }      
      pi = i+1
    }
    t 
  }

  def tablegetfunc() : Array[Int] = {
    // var z:Array[Int] = new Array[Int](3)
    var myList = ScalaStrectch()
    myList
  }

  var elements = tablegetfunc()
  val table = WireInit(VecInit(elements.map(_.S(13.W))))

  io.d := table(io.p)

}

object stretch{
  def apply(p:UInt) : SInt = {
    val func = Module(new stretch)
    func.io.p := p
    func.io.d
  }
}