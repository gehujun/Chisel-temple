package cm_top

import chisel3._
import chisel3.iotesters._
import chisel3.iotesters.{Driver,PeekPokeTester}
import scala.io.Source



class statemapTest(sm:StateMap) extends PeekPokeTester(sm){
    // poke(sm.io.cx,0)
    // poke(sm.io.y,1)
    // poke(sm.io.Start ,true.B)
    // for(i <-0 to 100){
    //     step(1)
    //     println("cycles "+ i + " : "+peek(sm.io.Done).toString+" prediction is "+ peek(sm.io.p))
    // }

    val source = Source.fromFile("/home/ghj/lpaq1/output.txt")
    val lines = source.getLines().toArray
    poke(sm.io.Start,true.B)
    for(line<-lines){
        val fields = line.trim.split(" ")
        val y = fields(0)
        println("prediction bit : "+y)
        for(i <- 0 until 7){
            var index = 2*i +1
            poke(sm.io.y,y.toInt.asUInt)
            poke(sm.io.cx,fields(index).toInt.asUInt)
            println("prediction cxt : "+fields(index))
            step(1)
            while(peek(sm.io.Done).toInt != 1){
                step(1)
            }
            println("sm's output is "+peek(sm.io.p))
        }
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

class order1Tester(order : order_1) extends PeekPokeTester(order){
    var count = 0
    val source = Source.fromFile("/home/ghj/lpaq1/oder1_output.txt")
    val lines = source.getLines().toArray
    poke(order.io.start,true.B)
    for(line<-lines){
        val fields = line.trim.split(" ")
        poke(order.io.y, fields(0).toInt.asUInt)
        while(peek(order.io.done)==0){
            step(1)
        }
        // expect(order.io.p,fields(1).toInt)
    }
}

class matchmodelTester(_match : MatchModel) extends PeekPokeTester(_match){
    val source = Source.fromFile("/home/ghj/lpaq1/match_output.txt")
    val lines = source.getLines().toArray
    poke(_match.io.start,true.B)
    for(line<-lines){
        val fields = line.trim.split(" ")
        poke(_match.io.inY, fields(0).toInt.asUInt)
        step(1)
        while(peek(_match.io.Done)==0){
            // poke(_match.io.start,false.B)
            println("runing.....")
            step(1)
        }
        println("one bit pass!")
        // expect(_match.io.outcxt,fields(1).toInt)
        // expect(_match.io.outcxt,fields(2).toInt)
    }
}

class sseTest(apm:APM) extends PeekPokeTester(apm) {
 val source = Source.fromFile("/home/ghj/lpaq1/sse_output.txt")
    val lines = source.getLines().toArray
    poke(apm.io.Start,true.B)
    for(line<-lines){
        val fields = line.trim.split(" ")
        poke(apm.io.next_y,fields(0).toInt.asUInt)
        poke(apm.io.cx,fields(1).toInt.asSInt)
        poke(apm.io.pr,fields(2).toInt.asSInt)
        // if(fields(8).toInt == 1) 
        //     poke(mixer.io.y,1.U)
        // else
        //     poke(mixer.io.y,0.U)

        // if(peek(apm.io.Done).toInt == 1){
        //     // println("apm's output is "+peek(apm.io.p)+" cxt "+fields(7).toInt+" y "+fields(8).toInt+" software is : "+fields(9).toInt)
        //     println("y : "+fields(0)+" apm's out should is: "+fields(3)+" my apm's is : "+peek(apm.io.p))
        // }
        step(1)
        while(peek(apm.io.Done).toInt == 0){
            step(1)
        }
        println("y : "+fields(0)+" apm's out should is: "+fields(3)+" my apm's is : "+peek(apm.io.p))
        // step(2)
    }
}

class apmInterfaceTester(c:apm_rinit) extends PeekPokeTester(c){
    
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

    var N = 256*24
    var table : Array[Int] = Array.fill(N)(0)
    for(i <- 0 until N){
        var p = ((i%24*2+1)*4096)/48-2048;
        table(i) = ((ScalaSquash(p))<<20)+6;
        poke(c.io.pr,0.U)
        poke(c.io.cxt,i.U)
        expect(c.io.out,table(i))
        step(1)
    }
}

object sseDriver extends App{
    //chisel3.Driver.execute(args,() => new APM(8))
    // chisel3.stage.ChiselStage.execute(() => new APM(8))

    chisel3.iotesters.Driver.execute(args,() => new APM(256)) (
        c => new sseTest(c))
}

class sseDriver extends ChiselFlatSpec {
      "running with --generate-vcd-output on" should "create a vcd file from your test" in {
        iotesters.Driver.execute(
            Array(
                "--generate-vcd-output", "on",
                "--target-dir", "test_run_dir/apm",
                "--top-name", "apm",
                ),
            () => new APM(256)
        ) {
            c => new sseTest(c)
        } should be(true)
    }
}