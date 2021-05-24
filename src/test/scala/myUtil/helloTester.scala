package myUtil

import chisel3._
import chisel3.iotesters._
import chisel3.iotesters.{PeekPokeTester, Driver, ChiselFlatSpec}
import java.io.PrintWriter
import scala.io.Source

// class helloTester(h : hello) extends PeekPokeTester(h){
//   poke(h.io.a,0.U)
//   poke(h.io.b,1.U)
//   step(1)
// //   println("Result is: " + peek(h.io.c).toString)
// }

// class helloTest

class helloTests(h:hello) extends PeekPokeTester(h){
    for(i <- 0 to 10){
        step(1)
    }
   
}

class helloTester extends ChiselFlatSpec {
  //   behavior of "helloTester"
  //   backends foreach {backend =>
  //   it should s"correctly add randomly generated numbers $backend" in {
  //     Driver(() => new hello, backend)(c => new helloTests(c)) should be (true)
  //   }
  // }

    "running with --generate-vcd-output on" should "create a vcd file from your test" in {
        iotesters.Driver.execute(
            Array(
                "--generate-vcd-output", "on",
                "--target-dir", "test_run_dir/hello",
                "--top-name", "hello",
                ),
            () => new hello()
        ) {
            c => new helloTests(c)
        } should be(true)
      }
}

import java.io.File
object helloTests extends App{
    //chisel3.iotesters.Driver.execute(args,() => new hello())(c=>new helloTests(c))

    // chisel3.iotesters.Driver(() => new hello()){ c =>
    // new helloTests(c)
  // }
    val writer = new PrintWriter(new File("./text.txt"))

    val source = Source.fromFile("/home/ghj/lpaq1/test/output/predictions.txt")
    val lines = source.getLines()
    for(line<-lines){
        val fields = line.trim.split("\t")
        print(fields(0).toInt+"#")
        print(fields(1).toInt+"#")
        print(fields(2).toInt+"#")
        print(fields(3).toInt+"#")
        print(fields(4).toInt+"#")
        print(fields(5).toInt+"#")
        print(fields(6).toInt+"#")
        print("\n")
    }

}
