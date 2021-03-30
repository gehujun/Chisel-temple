package myUtil


import chisel3._
import chisel3.iotesters._
import chisel3.iotesters.{Driver,PeekPokeTester}

class squashTest(s : squash) extends 
    PeekPokeTester(s){

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

        for(i <- -2050 to 2050){
            poke(s.io.p,i.asSInt())
            expect(s.io.squshP,ScalaSquash(i))
        }

    }

object squashTest extends App{
    //不生成Verilog只生成中间代码的测试
//   chisel3.iotesters.Driver(() => new squash()){ c =>
//     new squashTest(c)
//   }

  chisel3.iotesters.Driver.execute(args,() => new squash())(c=>new squashTest(c))

}
