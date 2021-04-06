package cm_top

import chisel3._
import chisel3.iotesters._
import chisel3.iotesters.{Driver,PeekPokeTester}

class sseTest(apm:APM) extends PeekPokeTester(apm) {
  
}

object sseDriver extends App{
    //chisel3.Driver.execute(args,() => new APM(8))
    // chisel3.stage.ChiselStage.execute(() => new APM(8))

    chisel3.iotesters.Driver.execute(args,() => new APM(8)) (c => new sseTest(c))

}