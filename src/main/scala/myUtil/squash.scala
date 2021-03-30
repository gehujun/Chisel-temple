package myUtil

import chisel3._

// return p = 1/(1 + exp(-d)), d scaled by 8 bits, p scaled by 12 bits
class squash extends Module{

    val io = IO(new Bundle{
        val p       = Input(SInt(32.W))
        val squshP  = Output(SInt(13.W))
        val seqT    = Output(Vec(33,SInt(13.W))) 
    })

    // //val t = Wire(Vec(33,UInt(12.W)))
    // val t = VecInit(Seq.fill(33)(0.U(12.W))

    val t   = VecInit(1.S, 2.S, 3.S, 6.S, 10.S, 16.S, 27.S, 45.S, 73.S, 120.S, 194.S, 310.S, 488.S, 747.S, 1101.S, 1546.S, 2047.S, 2549.S, 2994.S, 3348.S, 3607.S, 3785.S, 3901.S,
                         3975.S, 4022.S, 4050.S, 4068.S, 4079.S, 4085.S, 4089.S, 4092.S, 4093.S, 4094.S)

    val w = io.p & 127.S
    val d = (io.p >> 7).asUInt + 16.U
    
    io.seqT := t
    
    when(io.p > 2047.S){
        io.squshP := 4095.S
    }.elsewhen(io.p < -2047.S){
        io.squshP := 0.S
    }.otherwise{
        io.squshP := (t(d) * (128.S - w) + t(d + 1.U) * w + 64.S) >> 7
    }

}

//伴生对象和工厂方法
object squash {
    def apply(p : SInt) : SInt = {
        val a = Module(new squash)
        a.io.p := p
        a.io.squshP
    }
}