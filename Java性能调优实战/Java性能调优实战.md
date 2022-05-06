# Java性能调优实战

## JVM性能调优

### JVM常用参数

+ -Xms128m：堆初始大小
+ -Xmx512m：堆最大值
+  -XX:+PrintGCTimeStamps：打印GC具体时间
+  -XX:+PrintGCDateStamps：打印GC具体日期
+ -XX:+PrintGCDetails：打印GC详细日志
+ -Xloggc:/log/gc.log：GC日志输出路径
+ -Xmn1g：设置年轻代大小
+ -XX:+/-UseAdaptiveSizePolicy
  + 每次GC后JVM 将会动态调整 Java 堆中各个区域的大小以及进入老年代的年龄，JDK1.8默认开启
+ -XX:NewRatio=8：Eden区大小
+ -XX:SurvivorRatio=2：Survivor区大小
  + Eden：From Survivor：To Survivor=8:1:1
  + 年轻代和老年代默认比例：1:2

### GC收集器

### GC日志查看工具

GCViewer，GCEasy

### JVM查看命令

jmap -heap PID

java -XX:+PrintFlagsFinal -version | grep HeapSize 

- -XX:+PrintFlagsInitial 查看初始值
- -XX:+PrintFlagsFinal 查看最终值
- -XX:+UnlocakExperimentalVMOptions 解锁实验参数
- -XX:+UnlocakDiagnosticVMOptions 解锁诊断参数
- -XX:+PrintCommandLineFlags -version打印命令行参数

jinfo -flags PID

查看JVM启动参数

### 常用监控和诊断内存工具

#### Linux系统工具

##### top

+ top -Hp PID：查看进程中线程使用资源情况

##### vmstat

内存、CPU、swap等使用情况，一般用来查看进程上下文切换。

+ vmstat 1 3：每隔1秒周期采样，循环3次
  + r：等待运行的进程数
  + b：处于非中断睡眠状态的进程数
  + swpd：虚拟内存使用情况
  + free：空闲内存
  + buff：用来作为缓冲的内存数
  + si：从磁盘交换到内存的交换页数量
  + so：从内存交换到磁盘的交换页数量
  + bi：发送到块设备的块数
  + bo：从块设备接收的块数
  + in：每秒中断数
  + cs：每秒上下文切换次数
  + us：用户CPU使用时间
  + sy：内存系统CPU使用时间
  + id：空闲时间
  + wa：等待I/O时间
  + st：运行虚拟机窃取的时间

##### pidstat

深入到线程监测资源使用情况

常用参数：

+ -u：默认参数，显示各个进程的CPU使用情况
+ -r：显示各个进程的内存使用情况
+ -d：显示各个进程的I/O使用情况
+ -w：显示每个进程的上下文切换情况
+ -p：指定进程号
+ -t：显示进程中线程的统计信息

**举例：**pidstat -p PID -r 1 5

+ Minflt/s：任务每秒发生的次要错误，不需要从磁盘中加载页
+ Majflt/s：任务每秒发生的主要错误，需要从磁盘中加载页
+ VSZ：虚拟地址大小，虚拟内存使用KB
+ RSS：常驻集合大小，非交换区内存使用KB

#### JDK工具

##### jstat

+ -class：显示ClassLoad信息
+ -compiler：显示JIT编译相关信息
+ -gc：显示和GC相关的堆信息
+ -gccapacity：显示各代的容量及使用情况
+ -gcmetacapacity：显示Metaspace的大小
+ -gcnew：显示新生代信息
+ -gcnewcapacity：显示新生代大小和使用情况
+ -gcold：显示老年代和永久代的信息
+ -gcoldcapacity：显示老年代大小和使用情况
+ -gcutil：显示垃圾收集信息
+ -gccause：显示垃圾回收的相关信息（通 -gcutil），同时显示最后一次或当前正在发生的垃圾回收的诱因
+ -printcompilation：输出JIT编译的方法信息

**举例：**jstat -gc PID

+ S0U：年轻代中 To Survivor 目前已使用空间（单位 KB）

+ S1U：年轻代中 From Survivor 目前已使用空间（单位 KB）

+ EC：年轻代中 Eden 的容量（单位 KB）

+ EU：年轻代中 Eden 目前已使用空间（单位 KB）

- OC：Old 代的容量（单位 KB）

- OU：Old 代目前已使用空间（单位 KB）

- MC：Metaspace 的容量（单位 KB）
- MU：Metaspace 目前已使用空间（单位 KB）
- YGC：从应用程序启动到采样时年轻代中 gc 次数
- YGCT：从应用程序启动到采样时年轻代中 gc 所用时间 (s)
- FGC：从应用程序启动到采样时 old 代（全 gc）gc 次数
- FGCT：从应用程序启动到采样时 old 代（全 gc）gc 所用时间 (s)
- GCT：从应用程序启动到采样时 gc 用的总时间 (s)

##### jstack

jstack PID命令查看线程的堆栈信息，通常结合top -Hp PID或pidstat -p PID -t一起查看具体线程的状态，也经常用来排查死锁的异常。

**举例：**

+ jstack PID > log.txt

+ printf "%x" PID将进程号转成16进制字符串，在log.txt里搜索对应的16进制进程号

##### jmap

查看堆内存初始化配置信息及堆内存的使用情况。还可以输出堆内存中的对象信息，产生的对象，对象的数量等。

**举例：**

+ jmap -heap PID
+ jmap -histo[:live] PID | more：查看堆内存中对象数量，大小统计直方图，如果带上live就是统计活的对象
+ jmap -dump:format=b, file=/tmp/heap.hprof PID