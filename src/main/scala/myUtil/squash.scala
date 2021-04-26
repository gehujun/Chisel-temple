package myUtil

import chisel3._

// return p = 1/(1 + exp(-d)), d scaled by 8 bits, p scaled by 12 bits
class squash extends Module{

    val io = IO(new Bundle{
        val p       = Input(SInt(32.W))
        val squshP  = Output(UInt(12.W))
        // val seqT    = Output(Vec(33,SInt(13.W))) 
    })

    // //val t = Wire(Vec(33,UInt(12.W)))
    // val t = VecInit(Seq.fill(33)(0.U(12.W))

    // val t   = VecInit(1.S, 2.S, 3.S, 6.S, 10.S, 16.S, 27.S, 45.S, 73.S, 120.S, 194.S, 310.S, 488.S, 747.S, 1101.S, 1546.S, 2047.S, 2549.S, 2994.S, 3348.S, 3607.S, 3785.S, 3901.S,
    //                      3975.S, 4022.S, 4050.S, 4068.S, 4079.S, 4085.S, 4089.S, 4092.S, 4093.S, 4094.S)
    val t   = VecInit(1.U, 2.U, 3.U, 6.U, 10.U, 16.U, 27.U, 45.U, 73.U, 120.U, 194.U, 310.U, 488.U, 747.U, 1101.U, 1546.U, 2047.U, 2549.U, 2994.U, 3348.U, 3607.U, 3785.U, 3901.U,
                         3975.U, 4022.U, 4050.U, 4068.U, 4079.U, 4085.U, 4089.U, 4092.U, 4093.U, 4094.U)

    val w = io.p & 127.S
    val d = (io.p >> 7).asUInt + 16.U
    
    when(io.p > 2047.S){
        io.squshP := 4095.U
    }.elsewhen(io.p < -2047.S){
        io.squshP := 0.U
    }.otherwise{
        io.squshP := (t(d) * (128.U - w.asUInt) + t(d + 1.U) * w.asUInt + 64.U) >> 7
    }
}

//伴生对象和工厂方法
object squash {
    def apply(p : SInt) : UInt = {
        val a = Module(new squash)
        a.io.p := p
        a.io.squshP
    }
}