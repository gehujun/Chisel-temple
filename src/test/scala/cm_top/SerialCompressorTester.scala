package cm_top

import java.io._
import chisel3._
import chisel3.iotesters._
import chisel3.iotesters.{Driver, PeekPokeTester}
import java.nio.file.{Files, Paths}
import scala.io.Source

class SerialCompressorTester(c: SerialCompressor) extends PeekPokeTester(c) {
  var mode = true //Decompression=false，compression=true
  if(mode){
    val byteArray = Files.readAllBytes(Paths.get("/home/ghj/lpaq1/test/src/hello.txt"))
    //    val writer = new PrintWriter(new File("C:/Users/82459/Desktop/output.txt" ))
    val writer = new DataOutputStream(new FileOutputStream("/home/ghj/lpaq1/test/output/hello.lpaq1"))

    var flag = true
    var out : Long = 1
    var ioflag : Int = 1
    var wbyte : Byte = 0
    for(b <- byteArray)
    {
      for(i <- 0 to 7) {
        poke(c.io.start,true)
        poke(c.io.mode, 0)
        poke(c.io.i, (b >> (7 - i)) & 1)
        step(1)
        while(peek(c.io.Done).toInt == 0){ //Done为bool的输出，true（1）代表完成了压缩流程，false（0）代表还未完成
          poke(c.io.mode, 0)
          poke(c.io.i, (b >> (7 - i)) & 1)
          poke(c.io.start,false)
          step(1)
        }
        poke(c.io.mode, 0)
        poke(c.io.i, (b >> (7 - i)) & 1)
        poke(c.io.start,true)
        out = peek(c.io.out).toLong
        ioflag = peek(c.io.flag).toInt


//        println(f"out is $out%x")
        flag = true
        for(j <- 0 to 3) {
          if(((ioflag >> (3 - j)) & 1) == 1 && flag) {
            //            writer.print(f"${(out >> ((3 - j) * 8)) & 0xff}%02x")
            wbyte = ((out >> ((3 - j) * 8)) & 0xffL).toByte
//            println(f"wbyte : $wbyte%02x")
            writer.write(wbyte)
          } else
            flag = false
        }
//        println("------------------------------------")
        step(1)
        /* 这里的step我考虑了一下，应该删去，因为从上面的while循环出来之后的语句，压缩器实际上已经完成了一个压缩过程
        现在需要新的数据，开始新的压缩。如果还step(1)的话，就是用上一bit的数据又重新来了一遍，会一直重复*/
      }
    }
    var index = 3
    while(((ioflag >> 3) & 1) == 1){
      ioflag <<= 1
      index -= 1
    }
    out += 1
//    println(f"out is $out%x")
//    println(ioflag.toString)
//    println(index.toString)
    for(i <- 0 to index) {
      //      writer.print(f"${(out >> ((index - i) * 8)) & 0xff}%02x")
      wbyte = ((out >> ((index - i) * 8)) & 0xffL).toByte
//      println(f"$wbyte%02x")
      writer.write(wbyte)
    }
    //    out &= 0xffffffffL
    //    writer.print(f"${out + 1}%08x")
    writer.close()
  }
  else{
    val byteArray = Files.readAllBytes(Paths.get("/home/ghj/lpaq1/test/output/hello.lpaq1"))
    //    val writer = new PrintWriter(new File("C:/Users/82459/Desktop/decompress.txt" ))
    val writer = new DataOutputStream(new FileOutputStream("/home/ghj/lpaq1/test/output/hello.dlpaq1"))
    var x : Int = 0
    var ioflag : Int = 0xf
    var bInt : Int = 0
    //由于不知道迭代器如何进行改变，这里用索引，索引为Long型，超出其范围时会出错
    var index : Long = 0
    //    var b : Byte = byteArray(0)
    var bitindex : Int = 0
    var rbyte : Byte = 0
    var wbyte : Int = 0
    while(index <= byteArray.length) {
      while(((ioflag >> 3) & 1) == 1){
        //        b = byteArray(index)
        if(index < byteArray.length){
          rbyte = byteArray(index) //这里rbyte是压缩的数据
          index += 1
          bInt = rbyte.toInt & 0xff
          //        bInt = (byteArray(index).toS)
          //        bInt = b & 0xff
          x = (x << 8) | bInt //不用bInt直接用rbyte的话，或符号后面的数会被有符号扩展为32个1
          ioflag <<= 1
        }
        else{
          ioflag <<= 1
          index += 1
        }
      }
      if(index <= byteArray.length){
        poke(c.io.mode, 1)
        poke(c.io.x, x)
        poke(c.io.start,true)


//        val x1 = peek(c.io.x1)
//        val x2 = peek(c.io.x2)
//        val xMid = peek(c.io.xMid)
//        val y = peek(c.io.y)
//        println(f"$x1%x")
//        println(f"$x2%x")
//        println(f"$xMid%x")
//        println(f"$x%x")
//        println(y.toString)
//        println("!!!!!!!!!!!!!!")



        step(1)
        while(peek(c.io.Done).toInt == 0){ //enable为bool的输出，true（1）代表完成了解压流程，false（0）代表还未完成
          poke(c.io.mode, 1)
          poke(c.io.x, x)
          poke(c.io.start,true)
          step(1)
        }

        poke(c.io.mode, 1)
        poke(c.io.x, x)
        poke(c.io.start,true)
        wbyte = (wbyte << 1) | peek(c.io.y).toInt
        bitindex += 1
        ioflag = peek(c.io.flag).toInt

//        val x3 = peek(c.io.x1)
//        val x4 = peek(c.io.x2)
//        val xMid1 = peek(c.io.xMid)
//        println(f"$x3%x")
//        println(f"$x4%x")
//        println(f"$xMid1%x")
//        println("---------------------")

        if(bitindex == 8){
          writer.write(wbyte.toByte)
          bitindex = 0
          wbyte = 0
        }
        step(1)
      }
    }
    writer.close()
    println("Done")
  }
}

class SerialCompressionTestMain extends ChiselFlatSpec {
      "running with --generate-vcd-output on" should "create a vcd file from your test" in {
        iotesters.Driver.execute(
            Array(
                "--generate-vcd-output", "on",
                "--target-dir", "test_run_dir/serialCompression",
                "--top-name", "serialCompression",
                ),
            () => new SerialCompressor
        ) {
            c => new SerialCompressorTester(c)
        } should be(true)
    }
}

object SerialCompressionTestMain1 {
  def main(args: Array[String]): Unit = {
    if (!Driver.execute(args,() => new SerialCompressor())(c => new SerialCompressorTester(c))) System.exit(1)
  }
}
