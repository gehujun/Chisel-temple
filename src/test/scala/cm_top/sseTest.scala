package cm_top

import chisel3._
import chisel3.iotesters._
import chisel3.iotesters.{Driver,PeekPokeTester}

class sseTest(apm:apmNoPipeline) extends PeekPokeTester(apm) {
  poke(apm.io.cx,"b100".U)
  poke(apm.io.pr,3000.U)
  poke(apm.io.next_y,1.U)
  poke(apm.io.dtNUm,100.U)
  step(1)
}

class statemapTest(sm:StateMap) extends PeekPokeTester(sm){
    poke(sm.io.cx,0)
    poke(sm.io.y,1)
    poke(sm.io.Start ,true.B)
    for(i <-0 to 100){
        step(1)
        println("cycles "+ i + " : "+peek(sm.io.Done).toString+" prediction is "+ peek(sm.io.p))
    }
    
}

class memTest(mem:ForwardingMemory) extends PeekPokeTester(mem){

}

object sseDriver extends App{
    //chisel3.Driver.execute(args,() => new APM(8))
    // chisel3.stage.ChiselStage.execute(() => new APM(8))

    chisel3.iotesters.Driver.execute(args,() => new StateMap()) (c => new statemapTest(c))

}

class sseDriver extends ChiselFlatSpec {
      "running with --generate-vcd-output on" should "create a vcd file from your test" in {
        iotesters.Driver.execute(
            Array(
                "--generate-vcd-output", "on",
                "--target-dir", "test_run_dir/statemap",
                "--top-name", "statemap",
                ),
            () => new StateMap()
        ) {
            c => new statemapTest(c)
        } should be(true)
    }
}