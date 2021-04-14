package myUtil

import chisel3._
import chisel3.iotesters._
import chisel3.iotesters.{Driver,PeekPokeTester}
import scala.io.Source
import java.nio.file.{Files,Paths}

class stretchTests(s:stretch) extends PeekPokeTester(s){
  // poke(s.io.p,0.S)
  //println("stretch of 0 is : "+ peek(s.io.d).toString)
  
  // val byteArray = Files.readAllBytes(Paths.get("/home/ghj/lpaq1/test/hello.txt"))
  
  // for(b <- byteArray){
  //   for(i <- 7 to 0 ){
  //     //调用cm模型开始执行
  //     poke(cm.io.y,i)
  //     step(1)
  //   }
  // }

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

  val table =  ScalaStrectch()

  for(p <- 0 to 4096){
    poke(s.io.p,p.asSInt)
    expect(s.io.d,table(p))
  }

}

object stretchTester extends App{
  
  // chisel3.iotesters.Driver.execute(args,() => new stretch())
  //               (c=>new stretchTests(c))
  
  //不生成Verilog只生成中间代码的测试
  // chisel3.iotesters.Driver(() => new stretch()){ c =>
  //   new stretchTests(c)
  // }

  // println("文件内容为：")
  // for(lines <- Source.fromFile("/home/ghj/lpaq1/test/hello.txt").getLines()){
  //   println(lines)
  // }
  
  
    
  

}