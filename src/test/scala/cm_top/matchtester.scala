    package cm_top
    import chisel3._
    import java.io._
    import chisel3.iotesters.{Driver, PeekPokeTester}
    import java.nio.file.{Files, Paths}

    class matchtester(c: MatchModel) extends PeekPokeTester(c){
        val byteArray = Files.readAllBytes(Paths.get("/home/zwl/lpaq1/English.txt"))
    //    val writer = new PrintWriter(new File("C:/Users/82459/Desktop/output.txt" ))
        //val writer = new DataOutputStream(new FileOutputStream("/home/zwl/lpaq1/Outpu.txt"))
        var outy :Int = 0
        var outLen : Long = 1
        var outcxt :Long = 0
        //var wbyte : Byte = 0
        for(b <- byteArray)
        {
            for(i <- 0 to 7) {
                poke(c.io.start, true.B)
                poke(c.io.inY, ((b >> (7 - i)) & 1).asUInt)
                step(1)
                while(peek(c.io.Done).toInt == 0){     
                    step(1)
                  }
                    step(1) 
                outy = b >> (7 - i) & 1
                outLen = peek(c.io.outlen).toLong
                outcxt = peek(c.io.outcxt).toLong
                //wbyte = (outLen).toByte
                println("for bit " + outy +": len is " + outLen +  "ctx is " + outcxt + "\n")
            }
        //poke(c.io.inY, ((b >> (7 - i)) & 1).asUInt)       
      
    
    }

       // writer.write(wbyte)
        // wbyte = (outcxt).toByte
         //writer.write(wbyte)
      
      }
    
    //writer.close()

object matchtester {
  def main(args: Array[String]): Unit = {
    if (!Driver.execute(args,() => new MatchModel(1024))(c => new matchtester(c))) System.exit(1)
  }
}