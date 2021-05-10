package myUnit

import chisel3._
import chisel3.util._

class order_1 extends Module {
  val io = IO(new Bundle {
    val y = Input(UInt(1.W))
    val p = Output(UInt(8.W))
    val start = Input(Bool())
    val done = Output(Bool())
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

  val t0 = SyncReadMem(0x10000, UInt(8.W))
  val c0 = RegInit(1.U(9.W))
  val c0tmp = Wire(UInt(9.W))
  val h0 = RegInit(0.U(8.W))
  val cp = RegInit(0.U(16.W)) //cp中存储的是t0的index，从0到0xffff
  val t0read = Wire(UInt(8.W)) //t0read是t0[cp]

  c0 := c0
  h0 := h0
  cp := cp
  io.done := false.B

  c0tmp := c0 << 1 | io.y
  t0read := t0.read(cp)
  val idle :: stage1 :: stage2 :: Nil = Enum(3)
  val state = RegInit(idle)
  switch(state){
    is(idle) {
      when(io.start) {
        state := stage1
        printf("idle : cp is %d, wd is %d\n", cp, stateTable(t0read * 2.U + io.y))
        t0.write(cp, stateTable(t0read * 2.U + io.y))
        when(c0tmp < 256.U){ //c0tmp := c0 << 1 | io.y
          c0 := c0tmp
        } .otherwise{
          c0 := 1.U
          h0 := c0tmp & 0x0ff.U
        }
      } .otherwise{
        state := idle
      }
    }
    is(stage1) {
      printf("stage1 : c0 is %d, cat is %d\n", c0, Cat(h0, c0(7, 0)))
      cp := Cat(h0, c0(7, 0))
      state := stage2
    }
    is(stage2) {
      printf("stage2 : p is %d\n", io.p)
      io.done := true.B
      state := idle
    }
  }
  io.p := t0.read(Cat(h0, c0(7, 0)))
}
