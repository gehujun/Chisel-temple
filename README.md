开发参考：
1. chisel tutorial
2. chisel-book
3. 西交大的博客
4. chisel_cheatSheet

开发日记：
3/26
完成squash模块。
4/6
完成APM模块。

4/16
编写的chisel代码生成的verilog不对，今日未解决。

5/12
之前解决了Statemap概率映射的错误：有符号和无符号混用的错误。
解决I/O测试代码中解压终止条件的错误，解压和压缩的比特通过次数应该是一样，而不是通过读取到压缩文件的EOF作为终止。
5/13
解决算术编码器时序bug，重构算术编码器。
修改顶层逻辑bug，成功通过功能呢测试。
