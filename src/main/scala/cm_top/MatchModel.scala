package cm_top
import chisel3._
import chisel3.util._

class MatchModel(_n:Int) extends Module { //y is the last bit
    val io = IO(new Bundle{
        val start = Input(Bool())
        val inY = Input(UInt(1.W))
        val outlen = Output(UInt(64.W))
        // val toMadd = Output(UInt(64.W))
        val Done = Output(Bool())
        val outcxt = Output(UInt(64.W))

    })

    // io.outlen := 1.U
    // io.toMadd := 1.U
    // io.Done   := io.start
    // io.outcxt := io.inY

    val n = _n.asUInt
    val N : UInt = n/2.U - 1.U
    val HN : UInt = n/8.U - 1.U
    val buf = Reg(Vec(512, UInt(9.W)))
    //val buflag = Wire(Vec(512,UInt(1.W)))
    val flag = Reg(Bool())
    val flag2 = Reg(Bool())
    val flag3 = Reg(Bool())
    val flag4 = Reg(Bool())
    val len = RegInit(0.U(32.W))
    val newlen = len + 1.U
    val lenV1 = Wire(Vec(63, UInt(1.W)))
    val lenV1reg = Reg(Vec(63, UInt(1.W)))
    val lenV11 = Wire(UInt(63.W))
    lenV11 := lenV1reg.asTypeOf(UInt(63.W))
    val lenV2 = Wire(Vec(63, UInt(1.W)))
    val lenV2reg = Reg(Vec(63, UInt(1.W)))
    val lenV22 = Wire(UInt(63.W))
    lenV22 := lenV2reg.asTypeOf(UInt(63.W))
    val mxLen = 62.U
    val pos = RegInit(0.U(63.W))
    val newpos =  (pos + 1.U) & N
    val c0 = RegInit(1.U(9.W))
    val newc0 = (c0<<1.U) | io.inY
    val cxt = c0
    val bcount = RegInit(0.U(8.W)) //  denotes the number of bits in c0(0..7)
    val newbcount = bcount + 1.U
    // val ht = RegInit(VecInit(n/8.U, 0.U(32.W)))
    val ht = RegInit(VecInit(Seq.fill(_n/8)(0.U(32.W))))
    
    val h1 = RegInit(0.U(32.W))
    val newh1 = (h1 * 24.U + c0)&HN
    val idxh1 = ht(newh1) + 1.U
    val h2 = RegInit(0.U(32.W)) 
    val newh2 = (h2 * 160.U + c0)&HN
    val idxh2 = ht(newh2) + 1.U
    val match_find = RegInit(0.U(64.W))
    val newmatch = match_find + 1.U
    val partialByte= (buf(match_find)+256.U)>>(8.U-bcount)
    val b = ((buf(match_find)+256.U)>>(7.U-bcount))&1.U
    val cxtOp = cxt * 256.U + buf((pos - 1.U)&N)
    // val sm =Module(new StateMap)
    val idle::state1::state2::state3::state4::state5::Nil = Enum(6)
    val state = RegInit(idle)

    val condition1 = WireDefault(false.B)
    val condition2 = WireDefault(false.B)

    io.outlen := len
    io.outcxt := cxt
    lenV1(0) := 1.U 
    lenV2(0) := 1.U
    lenV1reg(0) := lenV1(0)
    lenV2reg(0) := lenV2(0)
    /*
    for(i <- 0 to 511){
        buflag(i) := 0.U
    }
    */
    for(i <- 0 to 511){
        buf(i):=scala.util.Random.nextInt(511).asUInt
    }
    for(i <- 1 to 62){
        lenV1(i) := 0.U
        lenV2(i) := 0.U
        lenV1reg(i) := lenV1(i)
        lenV2reg(i) := lenV2(i)
    }

    flag := false.B
    flag2 := false.B
    flag3 := false.B
    flag4 := false.B
    //io.inY := y 

    switch(state){
     is (idle){
        flag := false.B
        flag2 := false.B
        flag3 := false.B
        flag4 := false.B

        when(io.start === false.B){
            state := idle
        } .otherwise{
            printf("--------------bit %d------------------\n",io.inY)
            bcount := Mux(newbcount===8.U, 0.U, newbcount)
            c0 := newc0
            when(newbcount === 8.U){
                buf(pos) := newc0
                printf("buf(%d) is %d, newc0 is %d\n",pos, buf(pos), newc0)
                 
                state := state1
                printf("newbcount=%d  ,so we are going to state1\n", newbcount)
            } .otherwise {
                state := state3
                printf("newbcount=%d ,so we are going to state3 directly\n", newbcount)
            }
            } 
        }
    is(state1){
        pos := newpos
        printf("now we are in state1!\n")
        printf("buf(%d) is %d \n",pos, buf(pos))
        h1 := newh1
        h2 := newh2
            //buf(pos) := c0
            //buflag(pos) := 1.U
                            
        when((len>0.U) && (len<mxLen)){ //extend match
            //match_find := newmatch & N
            len := newlen
            flag := true.B
        } .elsewhen( len === 0.U){ //find match
                //match_find := ht(newh1)
            when(ht(newh1)=/= newpos){
                flag3 :=true.B
                for(i <- 1 to 62){
                    //when((buflag((ht(newh1) - i.asUInt)&N)===true.B) && (buflag((newpos-i.asUInt)&N)===true.B) && (buf((ht(newh1) - i.asUInt)&N)===buf((newpos-i.asUInt)&N)) &&(((idxh1-i.asUInt)&N) =/= newpos))
                    // && (newpos>(ht(newh1)+1.U-i.asUInt))  && (ht(newh1)<=newpos) && (ht(newh1)>=(i.asUInt))&& (newpos>=i.asUInt) && (idxh1>=i.asUInt) &&
                    printf("buf(%d) and buf(%d)\n",(ht(newh1) - i.asUInt), (newpos-i.asUInt))
                    when((buf((ht(newh1) - i.asUInt)&N)===buf((newpos-i.asUInt)&N)) && (((ht(newh1)-i.asUInt)&N) =/= newpos)){
                        lenV1(i) := 1.U & lenV1(i-1)
                        when(lenV1(i)=== 1.U){
                            printf("buf(%d) is %d, buf(%d) is %d lenV1(%d) is %d \n",(ht(newh1) - i.asUInt)&N,buf((ht(newh1) - i.asUInt)&N),(newpos-i.asUInt)&N,buf((newpos-i.asUInt)&N),i.asUInt,lenV1(i))
                        }
                    } .otherwise {
                        lenV1(i) := 0.U
                        //printf("buf(%d) is %d, buf(%d) is %d lenV1(%d) is %d \n",(ht(newh1) - i.asUInt)&N,buf((ht(newh1) - i.asUInt)&N),(newpos-i.asUInt)&N,buf((newpos-i.asUInt)&N),i.asUInt,lenV1(i))
                    }
                } 
            }
        }
        when(ht(newh2) =/= newpos){
            flag4 := true.B
            for(i <- 1 to 62){ //len<2;match:= ht(newh2),match等比较len，lenv1,lenv2后再选值  &&(newpos>(ht(newh2)+1.U-i.asUInt))&& (ht(newh2)<=newpos)&&(ht(newh2)>=(i.asUInt)) &&(newpos>=i.asUInt) && (idxh2>=i.asUInt) && 
                //when((buflag((ht(newh2) - i.asUInt)&N)===true.B) && (buflag((newpos-i.asUInt)&N)===true.B) && (buf((ht(newh2) - i.asUInt)&N)===buf((newpos-i.asUInt)&N)) &&(((idxh2-i.asUInt)&N) =/= newpos))
                condition1 := (buf((ht(newh2) - i.asUInt)&N)===buf((newpos-i.asUInt)&N)) 
                condition2 := (((ht(newh2)-i.asUInt)&N) =/= newpos)
                printf("condition1:%d condition2: %d\n",condition1,condition2)
                when(condition1 & condition2){
                    lenV2(i) := 1.U & lenV2(i-1)
                    when(lenV2(i)=== 1.U){
                        printf("buf(%d) is %d, buf(%d) is %d lenV2(%d) is %d \n",(ht(newh2) - i.asUInt)&N,buf((ht(newh2) - i.asUInt)&N),(newpos-i.asUInt)&N,buf((newpos-i.asUInt)&N),i.asUInt,lenV2(i))
                    }
                } .otherwise{
                    lenV2(i) := 0.U
                    //printf("buf(%d) is %d, buf(%d) is %d, lenV2(%d) is %d \n",(ht(newh2) - i.asUInt)&N,buf((ht(newh2) - i.asUInt)&N),(newpos-i.asUInt)&N,buf((newpos-i.asUInt)&N),i.asUInt,lenV2(i))
                    }
            }
        }
        state:=state2
    }
    is(state2) {
        for(i <- 0 to 62){
            when(lenV1reg(i) === 1.U){
                printf("in state2 lenV1(%d) is %d lenV11(%d) is %d \n", i.asUInt, lenV1(i),i.asUInt,lenV11(i))
            }
            
        }
        for(i <- 0 to 62){
            when(lenV2reg(i) === 1.U){
                printf("in state2 lenV2(%d) is %d lenV22(%d) is %d \n", i.asUInt, lenV2(i),i.asUInt,lenV22(i))
            }
        }
        c0 := 1.U
        //cxt := c0
        val V1now = PopCount(lenV11) -1.U
        val V2now = PopCount(lenV22) -1.U
        when(flag === true.B){
            printf("now we are in state2.1 update len, len is %d, lenv2 is %d, flag4 is %d\n",len,V2now, flag4)
            //len :=Mux(len>2.U, len, Mux(flag4===true.B,PopCount(lenV22)-1.U, 0.U))
            len :=Mux(len>2.U, len, Mux(flag4===true.B,V1now, 0.U))
            match_find:=Mux(len>2.U, newmatch&N, ht(h2))
        } .otherwise{
            printf("now we are  in state2.2 update len, lenV1-1 is %d, lenV2 is %d, flag4 is %d\n",V1now,V2now, flag4)
            //len :=Mux((PopCount(lenV11)-1.U)>2.U, PopCount(lenV11)-1.U, Mux(flag4===true.B,PopCount(lenV22)-1.U,0.U))
            len :=Mux(V1now>2.U, V1now, Mux(flag4===true.B,V2now,0.U))
            match_find :=Mux(V1now>2.U, ht(h1), ht(h2))
        }
        state := state3
    }
    is(state3){
        when(bcount === 0.U){
            ht(h1) := pos
            ht(h2) := pos
        }
        when((len<16.U) & (len>0.U) && (partialByte === c0)){
            cxt := len*2.U + b
            printf("now in state3, 0<len<16, so we dont change len, len is %d, partialByte is %d, c0 is %d \n",len,partialByte,c0)
        } .elsewhen((len>16.U) && (partialByte === c0)){
            cxt := (len>>2.U)*2.U + b + 24.U
            printf("now in state3 , len>16, so we dont change len, len is %d, partialByte is %d, c0 is %d\n",len,partialByte,c0)
        } .otherwise { //
            len := 0.U
            printf("now in state3 we overwirte len to 0 , origial len is  %d, partialByte is %d, c0 is %d\n",len,partialByte,c0)
            flag2 := true.B
        }
        state := state4
    } 
    is(state4){
        printf("now in state4 len is:%d \n",len)
        io.outlen := len
        when(flag2){
            io.outcxt := c0
        } .otherwise{io.outcxt := cxtOp}
        state :=idle
    }
    is(state5){
        printf("now in state 5 len is :%d \n",len)
        state := idle
    }

        //////
 
}
    
    // sm.io.cx := Mux(state === state4 && flag2, c0, cxtOp)
    // sm.io.y := Mux(state === state3, io.inY, 0.U)
    // sm.io.Start := Mux(state===state3, true.B, false.B)
    // io.toMadd := Mux(state===state5, sm.io.p, 0.U) //lack of strech()
    //io.outlen := Mux(state===state4, len, 0.U)
    io.Done := Mux(state===state4, true.B, false.B)
    //io.outcxt := Mux(state===state4 && flag2, c0, cxtOp)
    
}
