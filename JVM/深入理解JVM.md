# 深入理解JVM

## 重要参数

配置示例：

-Dfile.encoding=UTF-8 -Xms2048M -Xmx2048M -Xmn1024M -XX:+UseG1GC -XX:MaxGCPauseMillis=100 -XX:ParallelGCThreads=8 -XX:ConcGCThreads=8 -XX:PermSize=256m -XX:MaxPermSize=256M -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=d:/sw-mem.dump -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintHeapAtGC -Xloggc:d:/sw-gc.log  

最大堆大小

-Xmx2048m     

初始堆大小              　　　　　　

-Xms2048m      

年轻代大小          　　　　　　　　

-Xmn1024m  
每个线程栈大小，JDK5.0以后每个线程堆栈大小为1M。
-Xss512k

Eden区与Survivor区的大小比值，设置为8,则两个Survivor区与一个Eden区的比值为2:8,一个Survivor区占整个年轻代的1/10              　  　　　　　　

-XX:SurvivorRatio=8　　　　　

使用 G1 (Garbage First) 垃圾收集器    

-XX:+UseG1GC

#设置垃圾收集暂停时间最大值指标，默认值：4294967295 。这是一个软目标，Java虚拟机将尽最大努力实现它
-XX:MaxGCPauseMillis=200

提升年老代的最大临界值(tenuring threshold). 默认值为 15[每次GC，增加1岁，到15岁如果还要存活，放入Old区]       　　　　　　

-XX:MaxTenuringThreshold=14    

设置垃圾收集器在并行阶段使用的线程数[一般设置为本机CPU线程数相等，即本机同时可以处理的个数，设置过大也没有用]　

-XX:ParallelGCThreads=8            　　

并发垃圾收集器使用的线程数量

-XX:ConcGCThreads=8    

设置堆内存保留为假天花板的总量,以降低提升失败的可能性. 默认值是 10.

-XX:G1ReservePercent=10    

使用G1时Java堆会被分为大小统一的的区(region)。此参数可以指定每个heap区的大小. 默认值将根据 heap size 算出最优解. 最小值为 1Mb, 最大值为 32Mb

-XX:G1HeapRegionSize=n    

指定整个堆的使用率达到多少时, 执行一次并发标记周期, 默认45， 过大会导致并发标记周期迟迟不能启动, 增加FullGC的可能, 过小会导致GC频繁, 会导致应用程序性能有所下降

-XX:InitiatingHeapOccpancyPercent=n　　

设置Matespace内存大小的参数

-XX:MetaspaceSize=256m 
-XX:MaxMetaspaceSize=512M 


###################  以下为辅助功能     ###################   　 　　　

禁止在启动期间显式调用System.gc()

-XX:+DisableExplicitGC

OOM时导出堆到文件

-XX:+HeapDumpOnOutOfMemoryError 

导出OOM的路径     

-XX:HeapDumpPath=d:/a.dump 

打印GC详细信息       　　 

-XX:+PrintGCDetails         

打印CG发生的时间戳  　　　　 

-XX:+PrintGCTimeStamps       

每一次GC前和GC后，都打印堆信息     　　　 

-XX:+PrintHeapAtGC            　

监控类的加载　　　　 

-XX:+TraceClassLoading     

按下Ctrl+Break后，打印类的信息       　　　  

-XX:+PrintClassHistogram    

## 命令行

### 模式选择

+ 启用编译模式：java -Xcomp
+ 启用解释模式：java -Xint
+ 启用混合模式：java -Xmixed

## GC

![1567236587021](E:\data\my-document\JVM\assets\1567236587021.png)

### Serial GC

### ParNew GC

### Parallel Scavenge

+ Serial Old
+ Parallel Old

### CMS GC

### G1 GC