package cm_top

import chisel3._
import chisel3.util._

class order(N : Int) extends Module {
  val io = IO(new Bundle {
    val y = Input(UInt(1.W))
    val start = Input(Bool())
    val done = Output(Bool())
    val p1 = Output(UInt(8.W))
    val p2 = Output(UInt(8.W))
    val p3 = Output(UInt(8.W))
    val p4 = Output(UInt(8.W))
    val p5 = Output(UInt(8.W))
  })

  val stateTable: Vec[UInt] = VecInit(1.U, 2.U, 3.U, 5.U, 4.U, 6.U, 7.U, 10.U, 8.U, 12.U, 9.U, 13.U, 11.U, 14.U, 15.U, 19.U,
    16.U, 23.U, 17.U, 24.U, 18.U, 25.U, 20.U, 27.U, 21.U, 28.U, 22.U, 29.U, 26.U, 30.U, 31.U, 33.U,
    32.U, 35.U, 32.U, 35.U, 32.U, 35.U, 32.U, 35.U, 34.U, 37.U, 34.U, 37.U, 34.U, 37.U, 34.U, 37.U,
    34.U, 37.U, 34.U, 37.U, 36.U, 39.U, 36.U, 39.U, 36.U, 39.U, 36.U, 39.U, 38.U, 40.U, 41.U, 43.U,
    42.U, 45.U, 42.U, 45.U, 44.U, 47.U, 44.U, 47.U, 46.U, 49.U, 46.U, 49.U, 48.U, 51.U, 48.U, 51.U,
    50.U, 52.U, 53.U, 43.U, 54.U, 57.U, 54.U, 57.U, 56.U, 59.U, 56.U, 59.U, 58.U, 61.U, 58.U, 61.U,
    60.U, 63.U, 60.U, 63.U, 62.U, 65.U, 62.U, 65.U, 50.U, 66.U, 67.U, 55.U, 68.U, 57.U, 68.U, 57.U,
    70.U, 73.U, 70.U, 73.U, 72.U, 75.U, 72.U, 75.U, 74.U, 77.U, 74.U, 77.U, 76.U, 79.U, 76.U, 79.U,
    62.U, 81.U, 62.U, 81.U, 64.U, 82.U, 83.U, 69.U, 84.U, 71.U, 84.U, 71.U, 86.U, 73.U, 86.U, 73.U,
    44.U, 59.U, 44.U, 59.U, 58.U, 61.U, 58.U, 61.U, 60.U, 49.U, 60.U, 49.U, 76.U, 89.U, 76.U, 89.U,
    78.U, 91.U, 78.U, 91.U, 80.U, 92.U, 93.U, 69.U, 94.U, 87.U, 94.U, 87.U, 96.U, 45.U, 96.U, 45.U,
    48.U, 99.U, 48.U, 99.U, 88.U, 101.U, 88.U, 101.U, 80.U, 102.U, 103.U, 69.U, 104.U, 87.U, 104.U, 87.U,
    106.U, 57.U, 106.U, 57.U, 62.U, 109.U, 62.U, 109.U, 88.U, 111.U, 88.U, 111.U, 80.U, 112.U, 113.U, 85.U,
    114.U, 87.U, 114.U, 87.U, 116.U, 57.U, 116.U, 57.U, 62.U, 119.U, 62.U, 119.U, 88.U, 121.U, 88.U, 121.U,
    90.U, 122.U, 123.U, 85.U, 124.U, 97.U, 124.U, 97.U, 126.U, 57.U, 126.U, 57.U, 62.U, 129.U, 62.U, 129.U,
    98.U, 131.U, 98.U, 131.U, 90.U, 132.U, 133.U, 85.U, 134.U, 97.U, 134.U, 97.U, 136.U, 57.U, 136.U, 57.U,
    62.U, 139.U, 62.U, 139.U, 98.U, 141.U, 98.U, 141.U, 90.U, 142.U, 143.U, 95.U, 144.U, 97.U, 144.U, 97.U,
    68.U, 57.U, 68.U, 57.U, 62.U, 81.U, 62.U, 81.U, 98.U, 147.U, 98.U, 147.U, 100.U, 148.U, 149.U, 95.U,
    150.U, 107.U, 150.U, 107.U, 108.U, 151.U, 108.U, 151.U, 100.U, 152.U, 153.U, 95.U, 154.U, 107.U, 108.U, 155.U,
    100.U, 156.U, 157.U, 95.U, 158.U, 107.U, 108.U, 159.U, 100.U, 160.U, 161.U, 105.U, 162.U, 107.U, 108.U, 163.U,
    110.U, 164.U, 165.U, 105.U, 166.U, 117.U, 118.U, 167.U, 110.U, 168.U, 169.U, 105.U, 170.U, 117.U, 118.U, 171.U,
    110.U, 172.U, 173.U, 105.U, 174.U, 117.U, 118.U, 175.U, 110.U, 176.U, 177.U, 105.U, 178.U, 117.U, 118.U, 179.U,
    110.U, 180.U, 181.U, 115.U, 182.U, 117.U, 118.U, 183.U, 120.U, 184.U, 185.U, 115.U, 186.U, 127.U, 128.U, 187.U,
    120.U, 188.U, 189.U, 115.U, 190.U, 127.U, 128.U, 191.U, 120.U, 192.U, 193.U, 115.U, 194.U, 127.U, 128.U, 195.U,
    120.U, 196.U, 197.U, 115.U, 198.U, 127.U, 128.U, 199.U, 120.U, 200.U, 201.U, 115.U, 202.U, 127.U, 128.U, 203.U,
    120.U, 204.U, 205.U, 115.U, 206.U, 127.U, 128.U, 207.U, 120.U, 208.U, 209.U, 125.U, 210.U, 127.U, 128.U, 211.U,
    130.U, 212.U, 213.U, 125.U, 214.U, 137.U, 138.U, 215.U, 130.U, 216.U, 217.U, 125.U, 218.U, 137.U, 138.U, 219.U,
    130.U, 220.U, 221.U, 125.U, 222.U, 137.U, 138.U, 223.U, 130.U, 224.U, 225.U, 125.U, 226.U, 137.U, 138.U, 227.U,
    130.U, 228.U, 229.U, 125.U, 230.U, 137.U, 138.U, 231.U, 130.U, 232.U, 233.U, 125.U, 234.U, 137.U, 138.U, 235.U,
    130.U, 236.U, 237.U, 125.U, 238.U, 137.U, 138.U, 239.U, 130.U, 240.U, 241.U, 125.U, 242.U, 137.U, 138.U, 243.U,
    130.U, 244.U, 245.U, 135.U, 246.U, 137.U, 138.U, 247.U, 140.U, 248.U, 249.U, 135.U, 250.U, 69.U, 80.U, 251.U,
    140.U, 252.U, 249.U, 135.U, 250.U, 69.U, 80.U, 251.U, 140.U, 252.U, 0.U, 0.U, 0.U, 0.U, 0.U, 0.U)

  val h = RegInit(VecInit(0.U(32.W), 0.U(32.W), 0.U(32.W), 0.U(32.W), 0.U(32.W)))
//  val cache = RegInit(VecInit(0.U(128.W), 0.U(128.W), 0.U(128.W), 0.U(128.W), 0.U(128.W)))
  val cache1 = Reg(Vec(16, UInt(8.W)))
  val cache2 = Reg(Vec(16, UInt(8.W)))
  val cache3 = Reg(Vec(16, UInt(8.W)))
  val cache4 = Reg(Vec(16, UInt(8.W)))
  val cache5 = Reg(Vec(16, UInt(8.W)))
  val cp = RegInit(1.U(4.W)) //是取出的cache的指针，指向当前状态
  val hupdate = Wire(Bool()) //h寄存器组更新信号，0不更新，1更新
  val cpupdate = Wire(Bool())
  val wrbacken = RegInit(false.B) //写回标识，源码中最初的四个bit是对t0的修改，此处忽略

  val bcount = RegInit(0.U(4.W))
  val c0 = RegInit(1.U(9.W))
  val c4 = RegInit(0.U(32.W))
  val index = Reg(Vec(5, UInt(log2Up(N >> 4).W)))
  //for test
//  val rount = RegInit(1.U(8.W))
//  rount := rount

  bcount := bcount
  c0 := c0
  val bcounttmp = bcount + 1.U //预读bcount
  val c0tmp = Wire(UInt(9.W))
  c0tmp := (c0 << 1) | io.y
  hupdate := false.B
  cpupdate := false.B
  io.done := false.B
  wrbacken := wrbacken
  cp := cp
  for(i <- 0 to 15){
    cache1(i) := cache1(i)
    cache2(i) := cache2(i)
    cache3(i) := cache3(i)
    cache4(i) := cache4(i)
    cache5(i) := cache5(i)
  }

  //hashtable
  val hashtable = Module(new hashTable_5(N))
  hashtable.io.wrEna := false.B
  hashtable.io.rdEna := false.B
  when(bcount === 4.U){
    hashtable.io.addr1 := h(0) + c0
    hashtable.io.addr2 := h(1) + c0
    hashtable.io.addr3 := h(2) + c0
    hashtable.io.addr4 := h(3) + c0
    hashtable.io.addr5 := h(4) + c0
  } .otherwise{
    hashtable.io.addr1 := h(0)
    hashtable.io.addr2 := h(1)
    hashtable.io.addr3 := h(2)
    hashtable.io.addr4 := h(3)
    hashtable.io.addr5 := h(4)
  }
  hashtable.io.wrAddr1 := 0.U
  hashtable.io.wrAddr2 := 0.U
  hashtable.io.wrAddr3 := 0.U
  hashtable.io.wrAddr4 := 0.U
  hashtable.io.wrAddr5 := 0.U
  hashtable.io.wrData1 := 0.U
  hashtable.io.wrData2 := 0.U
  hashtable.io.wrData3 := 0.U
  hashtable.io.wrData4 := 0.U
  hashtable.io.wrData5 := 0.U

  val idle :: stage1 :: stage2 :: Nil = Enum(3)
  val state = RegInit(idle)
  switch(state){
    is(idle){
      when(io.start === false.B){
        state := idle
      } .otherwise{
//        rount := rount + 1.U
        state := stage1
        //cache更新
        cache1(cp) := stateTable(cache1(cp) * 2.U + io.y)
        cache2(cp) := stateTable(cache2(cp) * 2.U + io.y)
        cache3(cp) := stateTable(cache3(cp) * 2.U + io.y)
        cache4(cp) := stateTable(cache4(cp) * 2.U + io.y)
        cache5(cp) := stateTable(cache5(cp) * 2.U + io.y)

        when(bcounttmp === 8.U){ //此时bcount为7，c0为八位（包括前导1）；bcounttmp为8，c0tmp为九位（包括前导1）
          bcount := 0.U
          c0 := 1.U
          hupdate := true.B
        } .otherwise{
          bcount := bcounttmp
          c0 := c0tmp
        }
        when(bcounttmp =/= 4.U && bcounttmp =/= 8.U){
          cp := cp + ((io.y + 1.U(2.W)) << ((bcounttmp & 3.U) - 1.U))
        }
      }
    }
    is(stage1){
      when(bcount === 4.U || bcount === 0.U){ //bcount不可能等于8，等于8的情况在这里对应的是等于0
        state := stage2
        wrbacken := true.B
        //write back and read
        when(wrbacken){
          hashtable.io.wrEna := true.B
          hashtable.io.wrAddr1 := index(0)
          hashtable.io.wrAddr2 := index(1)
          hashtable.io.wrAddr3 := index(2)
          hashtable.io.wrAddr4 := index(3)
          hashtable.io.wrAddr5 := index(4)
//          printf("write index is %x, %x, %x, %x, %x\n", index(0), index(1), index(2), index(3), index(4))
          hashtable.io.wrData1 := Cat(cache1(15), cache1(14), cache1(13), cache1(12), cache1(11), cache1(10), cache1(9),
            cache1(8), cache1(7), cache1(6), cache1(5), cache1(4), cache1(3), cache1(2), cache1(1), cache1(0))
          hashtable.io.wrData2 := Cat(cache2(15), cache2(14), cache2(13), cache2(12), cache2(11), cache2(10), cache2(9),
            cache2(8), cache2(7), cache2(6), cache2(5), cache2(4), cache2(3), cache2(2), cache2(1), cache2(0))
          hashtable.io.wrData3 := Cat(cache3(15), cache3(14), cache3(13), cache3(12), cache3(11), cache3(10), cache3(9),
            cache3(8), cache3(7), cache3(6), cache3(5), cache3(4), cache3(3), cache3(2), cache3(1), cache3(0))
          hashtable.io.wrData4 := Cat(cache4(15), cache4(14), cache4(13), cache4(12), cache4(11), cache4(10), cache4(9),
            cache4(8), cache4(7), cache4(6), cache4(5), cache4(4), cache4(3), cache4(2), cache4(1), cache4(0))
          hashtable.io.wrData5 := Cat(cache5(15), cache5(14), cache5(13), cache5(12), cache5(11), cache5(10), cache5(9),
            cache5(8), cache5(7), cache5(6), cache5(5), cache5(4), cache5(3), cache5(2), cache5(1), cache5(0))
//          printf("write data is %x, %x, %x, %x, %x\n", hashtable.io.wrData1, hashtable.io.wrData2, hashtable.io.wrData3, hashtable.io.wrData4, hashtable.io.wrData5)
        }
        cp := 1.U
        hashtable.io.rdEna := true.B
      } .otherwise{
        state := idle
        io.done := true.B
      }
//      printf("read addr is %x, %x, %x, %x, %x\n", hashtable.io.addr1, hashtable.io.addr2, hashtable.io.addr3, hashtable.io.addr4, hashtable.io.addr5)
    }
    is(stage2){
      state := idle
      io.done := true.B
//      cache1 := hashtable.io.rdData1
      cache1(0) := hashtable.io.rdData1(7, 0)
      cache1(1) := hashtable.io.rdData1(15, 8)
      cache1(2) := hashtable.io.rdData1(23, 16)
      cache1(3) := hashtable.io.rdData1(31, 24)
      cache1(4) := hashtable.io.rdData1(39, 32)
      cache1(5) := hashtable.io.rdData1(47, 40)
      cache1(6) := hashtable.io.rdData1(55, 48)
      cache1(7) := hashtable.io.rdData1(63, 56)
      cache1(8) := hashtable.io.rdData1(71, 64)
      cache1(9) := hashtable.io.rdData1(79, 72)
      cache1(10) := hashtable.io.rdData1(87, 80)
      cache1(11) := hashtable.io.rdData1(95, 88)
      cache1(12) := hashtable.io.rdData1(103, 96)
      cache1(13) := hashtable.io.rdData1(111, 104)
      cache1(14) := hashtable.io.rdData1(119, 112)
      cache1(15) := hashtable.io.rdData1(127, 120)
//      cache2 := hashtable.io.rdData2
      cache2(0) := hashtable.io.rdData2(7, 0)
      cache2(1) := hashtable.io.rdData2(15, 8)
      cache2(2) := hashtable.io.rdData2(23, 16)
      cache2(3) := hashtable.io.rdData2(31, 24)
      cache2(4) := hashtable.io.rdData2(39, 32)
      cache2(5) := hashtable.io.rdData2(47, 40)
      cache2(6) := hashtable.io.rdData2(55, 48)
      cache2(7) := hashtable.io.rdData2(63, 56)
      cache2(8) := hashtable.io.rdData2(71, 64)
      cache2(9) := hashtable.io.rdData2(79, 72)
      cache2(10) := hashtable.io.rdData2(87, 80)
      cache2(11) := hashtable.io.rdData2(95, 88)
      cache2(12) := hashtable.io.rdData2(103, 96)
      cache2(13) := hashtable.io.rdData2(111, 104)
      cache2(14) := hashtable.io.rdData2(119, 112)
      cache2(15) := hashtable.io.rdData2(127, 120)
//      cache3 := hashtable.io.rdData3
      cache3(0) := hashtable.io.rdData3(7, 0)
      cache3(1) := hashtable.io.rdData3(15, 8)
      cache3(2) := hashtable.io.rdData3(23, 16)
      cache3(3) := hashtable.io.rdData3(31, 24)
      cache3(4) := hashtable.io.rdData3(39, 32)
      cache3(5) := hashtable.io.rdData3(47, 40)
      cache3(6) := hashtable.io.rdData3(55, 48)
      cache3(7) := hashtable.io.rdData3(63, 56)
      cache3(8) := hashtable.io.rdData3(71, 64)
      cache3(9) := hashtable.io.rdData3(79, 72)
      cache3(10) := hashtable.io.rdData3(87, 80)
      cache3(11) := hashtable.io.rdData3(95, 88)
      cache3(12) := hashtable.io.rdData3(103, 96)
      cache3(13) := hashtable.io.rdData3(111, 104)
      cache3(14) := hashtable.io.rdData3(119, 112)
      cache3(15) := hashtable.io.rdData3(127, 120)
//      cache4 := hashtable.io.rdData4
      cache4(0) := hashtable.io.rdData4(7, 0)
      cache4(1) := hashtable.io.rdData4(15, 8)
      cache4(2) := hashtable.io.rdData4(23, 16)
      cache4(3) := hashtable.io.rdData4(31, 24)
      cache4(4) := hashtable.io.rdData4(39, 32)
      cache4(5) := hashtable.io.rdData4(47, 40)
      cache4(6) := hashtable.io.rdData4(55, 48)
      cache4(7) := hashtable.io.rdData4(63, 56)
      cache4(8) := hashtable.io.rdData4(71, 64)
      cache4(9) := hashtable.io.rdData4(79, 72)
      cache4(10) := hashtable.io.rdData4(87, 80)
      cache4(11) := hashtable.io.rdData4(95, 88)
      cache4(12) := hashtable.io.rdData4(103, 96)
      cache4(13) := hashtable.io.rdData4(111, 104)
      cache4(14) := hashtable.io.rdData4(119, 112)
      cache4(15) := hashtable.io.rdData4(127, 120)
//      cache5 := hashtable.io.rdData5
      cache5(0) := hashtable.io.rdData5(7, 0)
      cache5(1) := hashtable.io.rdData5(15, 8)
      cache5(2) := hashtable.io.rdData5(23, 16)
      cache5(3) := hashtable.io.rdData5(31, 24)
      cache5(4) := hashtable.io.rdData5(39, 32)
      cache5(5) := hashtable.io.rdData5(47, 40)
      cache5(6) := hashtable.io.rdData5(55, 48)
      cache5(7) := hashtable.io.rdData5(63, 56)
      cache5(8) := hashtable.io.rdData5(71, 64)
      cache5(9) := hashtable.io.rdData5(79, 72)
      cache5(10) := hashtable.io.rdData5(87, 80)
      cache5(11) := hashtable.io.rdData5(95, 88)
      cache5(12) := hashtable.io.rdData5(103, 96)
      cache5(13) := hashtable.io.rdData5(111, 104)
      cache5(14) := hashtable.io.rdData5(119, 112)
      cache5(15) := hashtable.io.rdData5(127, 120)

      index(0) := hashtable.io.index1
      index(1) := hashtable.io.index2
      index(2) := hashtable.io.index3
      index(3) := hashtable.io.index4
      index(4) := hashtable.io.index5
//      printf("index is %x, %x, %x, %x, %x\n", hashtable.io.index1, hashtable.io.index2, hashtable.io.index3, hashtable.io.index4, hashtable.io.index5)
//      printf("data is %x, %x, %x, %x, %x\n", hashtable.io.rdData1, hashtable.io.rdData2, hashtable.io.rdData3, hashtable.io.rdData4, hashtable.io.rdData5)
    }
  }
  //update context
  val c4tmp = Wire(UInt(32.W))
  c4tmp := (c4 << 8) | c0tmp(7, 0) //注意，这里要使用的一定是c0tmp(7, 0)
  when(hupdate){
    c4 := c4tmp
    h(0) := (c4tmp & 0xffff.U) << 5 | "h57000000".U
    h(1) := (c4tmp << 8) * 3.U
    h(2) := c4tmp * 5.U
    h(3) := h(3) * (11.U << 5) + c0tmp(7, 0) * 13.U & "h3fffffff".U
    when(c0tmp(7, 0) >= 65.U && c0tmp(7, 0) <= 90.U){
      h(4) := (h(4) + c0tmp(7, 0) + 32.U) * (7.U << 3)
    } .elsewhen(c0tmp(7, 0) >= 97.U && c0tmp(7, 0) <= 122.U){
      h(4) := (h(4) + c0tmp(7, 0)) * (7.U << 3)
    } .otherwise{
      h(4) := 0.U
    }
  } .otherwise{
    c4 := c4
    h(0) := h(0)
    h(1) := h(1)
    h(2) := h(2)
    h(3) := h(3)
    h(4) := h(4)
  }

  io.p1 := cache1(cp)
  io.p2 := cache2(cp)
  io.p3 := cache3(cp)
  io.p4 := cache4(cp)
  io.p5 := cache5(cp)
}
