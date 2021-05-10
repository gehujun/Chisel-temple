package cm_top

import chisel3._
import chisel3.iotesters._
import chisel3.iotesters.{Driver,PeekPokeTester}
import scala.io.Source

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

class mixerTest(mixer : Mixer) extends PeekPokeTester(mixer){
    // poke(mixer.io.predictions(0),1.U)
    // poke(mixer.io.predictions(1),2.U)
    // poke(mixer.io.predictions(2),3.U)
    // poke(mixer.io.predictions(3),4.U)
    // poke(mixer.io.predictions(4),5.U)
    // poke(mixer.io.predictions(5),6.U)
    // poke(mixer.io.predictions(6),7.U)
    // poke(mixer.io.cxt,0)
    // poke(mixer.io.y,1.U)
    // println("mixer outpu is："+peek(mixer.io.out)+" err is : "+ peek(mixer.io.err))
    // step(1)

    val source = Source.fromFile("/home/ghj/lpaq1/test/output/predictions.txt")
    val lines = source.getLines().toArray
    poke(mixer.io.Start,true.B)
    for(line<-lines){
        val fields = line.trim.split("\t")
        poke(mixer.io.predictions(0),fields(0).toInt.asSInt)
        poke(mixer.io.predictions(1),fields(1).toInt.asSInt)
        poke(mixer.io.predictions(2),fields(2).toInt.asSInt)
        poke(mixer.io.predictions(3),fields(3).toInt.asSInt)
        poke(mixer.io.predictions(4),fields(4).toInt.asSInt)
        poke(mixer.io.predictions(5),fields(5).toInt.asSInt)
        poke(mixer.io.predictions(6),fields(6).toInt.asSInt)
        poke(mixer.io.cxt,fields(7).toInt.asUInt)
        poke(mixer.io.y,fields(8).toInt.asUInt)
        // if(fields(8).toInt == 1) 
        //     poke(mixer.io.y,1.U)
        // else
        //     poke(mixer.io.y,0.U)
        if(peek(mixer.io.Done).toInt == 1)
            println("mixer's output is "+peek(mixer.io.out)+" cxt "+fields(7).toInt+" y "+fields(8).toInt+" software is : "+fields(9).toInt)
        step(2)
    }

}

object sseDriver extends App{
    //chisel3.Driver.execute(args,() => new APM(8))
    // chisel3.stage.ChiselStage.execute(() => new APM(8))

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

    // chisel3.iotesters.Driver.execute(args,() => new Mixer(7)) (c => new mixerTest(c))

}

class sseDriver extends ChiselFlatSpec {
      "running with --generate-vcd-output on" should "create a vcd file from your test" in {
        iotesters.Driver.execute(
            Array(
                "--generate-vcd-output", "on",
                "--target-dir", "test_run_dir/mixer",
                "--top-name", "mixer",
                ),
            () => new Mixer(7)
        ) {
            c => new mixerTest(c)
        } should be(true)
    }
}