package myUtil

import chisel3._

class squash extends Module{

    val io = IO(new Bundle{
        val p       = Input(UInt(32.W))
        val squshP  = Output(UInt(12.W))
        val seqT    = Output(Vec(33,UInt(12.W))) 
    })

    // //val t = Wire(Vec(33,UInt(12.W)))
    var seq  = Seq(1.U, 2.U, 3.U, 6.U, 10.U, 16.U, 27.U, 45.U, 73.U, 120.U, 194.U, 310.U, 488.U, 747.U, 1101.U, 1546.U, 2047.U, 2549.U, 2994.U, 3348.U, 3607.U, 3785.U, 3901.U,
                         3975.U, 4022.U, 4050.U, 4068.U, 4079.U, 4085.U, 4089.U, 4092.U, 4093.U, 4094.U)

    val temp = RegInit(VecInit(Seq.fill(33)(0.U(12.W))))
    
    for(i <- 0 until 33){
        temp(i) := seq(i)
    }

    io.seqT := temp
   

    val w = io.p & 127.U
    val d = (io.p >> 7) + 16.U

    io.squshP := (temp(d)* (128.U - w) + temp(d + 1.U) * w + 64.U) >> 7

}
