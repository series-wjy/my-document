# JAVA并发编程实战

## 并发编程思维导图

![1552921772183](E:\data\my-document\concurrent\assets\1552921772183.png)

## 01、可见性、原子性和有序性问题：并发编程的Bug源头

**源头1：缓存导致的可见性问题**

一个线程对共享变量的修改，另外一个线程能够立刻看到，我们称为**可见性**

![1552921804991](E:\data\my-document\concurrent\assets\1552921804991.png)

![1552921861547](E:\data\my-document\concurrent\assets\1552921861547.png)

**源头二：线程切换带来的原子性问题**

**我们把一个或者多个在CPU执行的过程中不被中断的特性称为原子性。**

![1554164526207](E:\data\my-document\concurrent\1554164526207.png)

![1552921979246](E:\data\my-document\concurrent\assets\1552921979246.png)

上面代码中的count +=1，至少需要三条CPU指令：

+ 指令1：首先需要把count从内存加载到CPU寄存器。
+ 指令2：在寄存器执行+1操作。
+ 指令3：将结果写入内存（缓存机制导致可能写入的是CPU缓存而不是内存）。

**源头三：编译优化带来的有序性问题**

![1552922102636](E:\data\my-document\concurrent\assets\1552922102636.png)

**总结：**

​	要写好并发程序，首先要知道并发程序的问题在哪里，只有确定了“靶子”，才有可能把问题解决，毕竟所有的解决方案都是针对问题的。并发程序经常出现的诡异问题看上去非常无厘头，但是深究的话，无外乎就是直觉欺骗了我们，**只要我们能够深刻理解可见性、原子性、有序性在并发场景下的原理，很多并发 Bug 都是可以理解、可以诊断的**。

​	在介绍**可见性、原子性、有序性**的时候，特意提到**缓存**导致的可见性问题，**线程切换**带来的原子性问题，**编译优化**带来的有序性问题，其实缓存、线程、编译优化的目的和我们写并发程序的目的是相同的，都是提高程序性能。但是技术在解决一个问题的同时，必然会带来另外一个问题，所以**在采用一项技术的同时，一定要清楚它带来的问题是什么，以及如何规避**。

## 02、JAVA内存模型：看JAVA如何解决可见性和有序性问题

解决可见性问题和有序性问题的方案是：**按需禁用缓存和编译优化。**这些方法包括：**volatile、synchronized和final**三个关键字，以及六项**happens-before**规则。

**happens-before：前面一个操作的结果对后续操作可见。**

![1552923257940](E:\data\my-document\concurrent\assets\1552923257940.png)

依据传递性依赖规则：线程A写变量v happens-before 线程B读变量v，线程A执行的X=42对线程B的读变量X可见。

+ **程序顺序规则：在一个线程中，前面的操作happens-before于后续的任何操作。**
+ **volatile变量规则：对一个volatile变量的写操作，happens-before于后续对这个变量的读操作。**
+ **传递性规则：A happens-before B，B happens-before C，则A happens-before C。**
+ **管程（synchronized）中的锁规则：对一个锁的解锁happens-before对这个锁的加锁操作。**
+ **线程start()规则：主线程A启动子线程B，线程B可以看线程A在启动线程B之前的操作。**
+ **线程join()规则：主线程A调用子线程B的join()操作，线程B执行完后，线程A可以看到线程B对共享变量的操作。**

**final关键字：final优化变量时，初衷是告诉编译器--这个变量生而不可变，可以使劲优化。注意构造函数逸出。**

**总结：**

​	Java 的内存模型是并发编程领域的一次重要创新，之后 C++、C#、Golang 等高级语言都开始支持内存模型。Java 内存模型里面，最晦涩的部分就是 Happens-Before 规则了，Happens-Before 规则最初是在一篇叫做Time, Clocks, and the Ordering of Events in a Distributed System的论文中提出来的，在这篇论文中，Happens-Before 的语义是一种因果关系。在现实世界里，如果 A 事件是导致 B 事件的起因，那么 A 事件一定是先于（Happens-Before）B 事件发生的，这个就是 Happens-Before 语义的现实理解。

​	在 Java 语言里面，Happens-Before 的语义本质上是一种可见性，A Happens-Before B 意味着 A 事件对 B 事件来说是可见的，无论 A 事件和 B 事件是否发生在同一个线程里。例如 A 事件发生在线程 1 上，B 事件发生在线程 2 上，Happens-Before 规则保证线程 2 上也能看到 A 事件的发生。

​	Java 内存模型主要分为两部分，一部分面向你我这种编写并发程序的应用开发人员，另一部分是面向 JVM 的实现人员的，我们可以重点关注前者，也就是和编写并发程序相关的部分，这部分内容的核心就是 Happens-Before 规则。相信经过本章的介绍，你应该对这部分内容已经有了深入的认识。

## 03、互斥锁：解决原子性问题（如何用一把锁锁住多个资源）

原子性问题的源头是：**线程切换。**受保护的资源和锁之间的关系应该是N:1的关系。

![1552923989258](E:\data\my-document\concurrent\assets\1552923989258.png)

​								**long**类型变量在32位CPU上的写操作

**同一时刻只有一个线程执行，这个条件非常重要，叫做：互斥。**

![1552924098755](E:\data\my-document\concurrent\assets\1552924098755.png)

我们把一段需要互斥执行的代码称为：**临界区。**

![1552924162632](E:\data\my-document\concurrent\assets\1552924162632.png)

**管程中的锁规则：对一个锁的解锁操作happens-before于后续对这个锁的加锁操作。**

![1552924557115](E:\data\my-document\concurrent\assets\1552924557115.png)

![1552924589173](E:\data\my-document\concurrent\assets\1552924589173.png)

**用不同的锁来对受保护的资源进行精细化管理，能够提升性能。这种锁叫细粒度锁。**

![1552924936274](E:\data\my-document\concurrent\assets\1552924936274.png)

```java
class Account {
  private int balance;
  // 转账
  synchronized void transfer(
      Account target, int amt){
    if (this.balance > amt) {
      this.balance -= amt;
      target.balance += amt;
    }
  } 
}
```

存在线程安全问题：无法锁定target。

![1552925157984](E:\data\my-document\concurrent\assets\1552925157984.png)

```java
class Account {
  private int balance;
  // 转账
  void transfer(Account target, int amt){
    synchronized(Account.class) {
      if (this.balance > amt) {
        this.balance -= amt;
        target.balance += amt;
      }
    }
  } 
}
```

![1552925412812](E:\data\my-document\concurrent\assets\1552925412812.png)

**总结：**

​	相信你看完这篇文章后，对如何保护多个资源已经很有心得了，关键是要分析多个资源之间的关系。如果资源之间没有关系，很好处理，每个资源一把锁就可以了。如果资源之间有关联关系，就要选择一个粒度更大的锁，这个锁应该能够覆盖所有相关的资源。除此之外，还要梳理出有哪些访问路径，所有的访问路径都要设置合适的锁，这个过程可以类比一下门票管理。我们再引申一下上面提到的关联关系，关联关系如果用更具体、更专业的语言来描述的话，其实是一种“原子性”特征，在前面的文章中，我们提到的原子性，主要是面向 CPU 指令的，转账操作的原子性则是属于是面向高级语言的，不过它们本质上是一样的。

​	“原子性”的本质是什么？其实不是不可分割，不可分割只是外在表现，其本质是多个资源间有一致性的要求，操作的中间状态对外不可见。例如，在 32 位的机器上写 long 型变量有中间状态（只写了 64 位中的 32 位），在银行转账的操作中也有中间状态（账户 A 减少了 100，账户 B 还没来得及发生变化）。所以解决原子性问题，是要保证中间状态对外不可见。

## 05、一不小心就死锁了，怎么办？

![1552925563847](E:\data\my-document\concurrent\assets\1552925563847.png)

```java
class Account {
  private int balance;
  // 转账
  void transfer(Account target, int amt){
    // 锁定转出账户
    synchronized(this) {              
      // 锁定转入账户
      synchronized(target) {           
        if (this.balance > amt) {
          this.balance -= amt;
          target.balance += amt;
        }
      }
    }
  } 
}
```

**细粒度的锁可以提高并行度，是性能优化的手段。但是，使用细粒度锁的代价是可能会导致死锁。**

![1552925681406](E:\data\my-document\concurrent\assets\1552925681406.png)

**死锁定义：一组互相竞争资源的线程互相等待，导致“永久”阻塞的现象。**

![1552925771393](E:\data\my-document\concurrent\assets\1552925771393.png)

死锁的四个必要条件：

+ 互斥条件：共享资源X和Y只能被一个线程占用。
+ 占有且等待：线程在占有资源X，等待共享资源Y的时候，不释放共享资源X。
+ 不可剥夺：其他线程不能剥夺当前线程占有的资源。
+ 循环等待：线程T1等待线程T2占有的资源，线程T2等待线程T1占有的资源。

破坏死锁条件：

+ 对于“占用且等待”这个条件，我们可以一次性申请所有的资源，这样就不存在等待了。
+ 对于“不可抢占”这个条件，占用部分资源的线程进一步申请其他资源时，如果申请不到，可以主动释放它占有的资源，这样不可抢占这个条件就破坏掉了。
+ 对于“循环等待”这个条件，可以靠按序申请资源来预防。所谓按序申请，是指资源是有线性顺序的，申请的时候可以先申请资源序号小的，再申请资源序号大的，这样线性化后自然就不存在循环了。

**1、破坏占有且等待条件**

![1552926156452](E:\data\my-document\concurrent\assets\1552926156452.png)

**2、破坏不可剥夺条件**

​	通过lock来实现。

**3、破坏循环等待条件**

​	破坏这个条件，需要对资源进行排序，然后按序申请资源。这个实现非常简单，我们假设每个账户都有不同的属性 id，这个 id 可以作为排序字段，申请的时候，我们可以按照从小到大的顺序来申请。比如下面代码中，①~⑥处的代码对转出账户（this）和转入账户（target）排序，然后按照序号从小到大的顺序锁定账户。这样就不存在“循环”等待了。

```java
class Account {
  private int id;
  private int balance;
  // 转账
  void transfer(Account target, int amt){
    Account left = this        ①
    Account right = target;    ②
    if (this.id > target.id) { ③
      left = target;           ④
      right = this;            ⑤
    }                          ⑥
    // 锁定序号小的账户
    synchronized(left){
      // 锁定序号大的账户
      synchronized(right){ 
        if (this.balance > amt){
          this.balance -= amt;
          target.balance += amt;
        }
      }
    }
  } 
}
```

**总结：**

​	当我们在编程世界里遇到问题时，应不局限于当下，可以换个思路，向现实世界要答案，利用现实世界的模型来构思解决方案，这样往往能够让我们的方案更容易理解，也更能够看清楚问题的本质。

​	但是现实世界的模型有些细节往往会被我们忽视。因为在现实世界里，人太智能了，以致有些细节实在是显得太不重要了。在转账的模型中，我们为什么会忽视死锁问题呢？原因主要是在现实世界，我们会交流，并且会很智能地交流。而编程世界里，两个线程是不会智能地交流的。所以在利用现实模型建模的时候，我们还要仔细对比现实世界和编程世界里的各角色之间的差异。

​	我们今天这一篇文章主要讲了**用细粒度锁来锁定多个资源时，要注意死锁的问题**。这个就需要你能把它强化为一个思维定势，遇到这种场景，马上想到可能存在死锁问题。当你知道风险之后，才有机会谈如何预防和避免，因此，**识别出风险很重要**。

​	预防死锁主要是破坏三个条件中的一个，有了这个思路后，实现就简单了。但仍需注意的是，有时候预防死锁成本也是很高的。例如上面转账那个例子，我们破坏占用且等待条件的成本就比破坏循环等待条件的成本高，破坏占用且等待条件，我们也是锁了所有的账户，而且还是用了死循环**while(!actr.apply(this, target));**方法，不过好在 apply() 这个方法基本不耗时。 在转账这个例子中，破坏循环等待条件就是成本最低的一个方案。

​	所以我们在选择具体方案的时候，还需要**评估一下操作成本，从中选择一个成本最低的方案**。

## 06、用“等待-通知”机制优化循环等待

​	**一个完整的等待通知机制：线程首先获取互斥锁，当线程要求的条件不满足时，释放锁并进入等待状态；当要求的条件满足时，通知等待的线程，重新获取互斥锁。**

![1553093506367](E:\data\my-document\concurrent\assets\1553093506367.png)

​	**等待队列和互斥锁是一对一的关系，每一个互斥锁都有自己独立的等待队列。**当某些条件不满足时，调用wait()方法，当前线程会被阻塞，并且进入右边的的等待队列中，进入队列的线程会释放互斥锁，其他线程就有机会获取互斥锁，进入临界区。当条件满足时，可以调用notify()或notifyAll()方法通知等待队列，告诉等待队列中的线程**条件曾经满足过**。

![1553093783126](E:\data\my-document\concurrent\assets\1553093783126.png)

​	**notify()只能保证在通知的时间点，条件是满足的**。而被通知线程的**执行时间点和通知时间点**基本不会重合，所以当线程执行的时候，**很可能条件已经不满足了**（有可能存在插队的情况）。

​	在这个等待-通知机制中，考虑以下四个要素：

+ 互斥锁：可以用this作为互斥锁。
+ 线程要求的条件：转出账户和转入账户都没有被分配过。
+ 何时等待：线程要求的条件不满足就等待。
+ 何时通知：当有线程释放账户时就通知。

```java
  while(条件不满足) {
    wait();
  }
```

利用这种范式可以解决上面提到的**条件曾经满足过**这个问题。因为wait()返回时，有可能条件已经发生了变化，曾经条件满足过，但是现在条件可能已经不满足了，所以需要重新检验条件是否满足。

​	**尽量使用notifyAll()，因为notify()是随机等待队列中的一个线程唤醒，这样有可能造成线程“饥饿”问题。**

**总结：**

​	等待 - 通知机制是一种非常普遍的线程间协作的方式。工作中经常看到有同学使用轮询的方式来等待某个状态，其实很多情况下都可以用今天我们介绍的等待 - 通知机制来优化。Java 语言内置的 synchronized 配合 wait()、notify()、notifyAll() 这三个方法可以快速实现这种机制，但是它们的使用看上去还是有点复杂，所以你需要认真理解等待队列和 wait()、notify()、notifyAll() 的关系。最好用现实世界做个类比，这样有助于你的理解。

​	Java 语言的这种实现，背后的理论模型其实是管程，这个很重要，不过你不用担心，后面会有专门的一章来介绍管程。现在你只需要能够熟练使用就可以了。

## 07、安全性、活跃性以及性能问题

**竟态条件：所谓竟态条件，指的是执行结果依赖线程的执行顺序。**

```
if (状态变量 满足 执行条件) {
  执行操作
}
```

```java
public class Test {
  private long count = 0;
  synchronized long get(){
    return count；
  }
  synchronized void set(long v){
    count = v;
  } 
  void add10K() {
    int idx = 0;
    while(idx++ < 10000) {
      set(get()+1)      
    }
  }
}

```

​	当某个线程发现状态变量满足执行条件后，开始执行操作；可是就在这个线程执行操作的时候，其他线程同时修改了状态变量，导致状态变量不满足执行条件了。当然很多场景下，这个条件不是显式的，例如前面 addOne 的例子中，set(get()+1) 这个复合操作，其实就隐式依赖 get() 的结果。

​	可以用**互斥**的方案来解决**数据竞争和竟态条件问题**，逻辑上就统一归为：**锁**。

**活跃性问题：**

​	除了死锁还有两种情况：

+ **活锁：**有的线程虽然没有阻塞，但是仍然会存在执行不下去的情况。
  + 解决方案：设置一个尝试等待的随机时间。Raft分布式一致性算法用到了。ZAB算法也解决了活锁的问题。
+ **饥饿：**线程无法访问所需资源而无法执行下去的情况。
  + 保证充足的资源
  + **公平的分配资源**
    + 解决方案：公平锁
  + 避免持有锁的线程长时间执行

**性能问题：**

​	锁的过度使用会导致程序串行执行，无法发挥多线程的优势。串行对性能的影响，可以通过公式计算。

​	有个阿姆达尔（Amdahl）定律，代表了处理器并行运算之后效率提升的能力，它正好可以解决这个问题，具体公式如下：

![1553095633749](E:\data\my-document\concurrent\assets\1553095633749.png)

​	公式里的 n 可以理解为 CPU 的核数，p 可以理解为并行百分比，那（1-p）就是串行百分比了，也就是我们假设的 5%。我们再假设 CPU 的核数（也就是 n）无穷大，那加速比 S 的极限就是 20。也就是说，如果我们的串行率是 5%，那么我们无论采用什么技术，最高也就只能提高 20 倍的性能。

怎样避免锁带来的性能问题呢？从方案层面，我们有如下几种：

+ 既然使用锁会带来性能问题，那最好的方案自然就是使用**无锁**的算法和数据结构了。在这方面有很多相关的技术，例如线程本地存储 (Thread Local Storage, TLS)、写入时复制 (Copy-on-write)、乐观锁等；Java 并发包里面的原子类也是一种无锁的数据结构；Disruptor 则是一个无锁的内存队列，性能都非常好……
+ **减少锁持有的时间**。互斥锁本质上是将并行的程序串行化，所以要增加并行度，一定要减少持有锁的时间。这个方案具体的实现技术也有很多，例如使用细粒度的锁，一个典型的例子就是 Java 并发包里的 ConcurrentHashMap，它使用了所谓分段锁的技术（这个技术后面我们会详细介绍）；还可以使用读写锁，也就是读是无锁的，只有写的时候才会互斥。

性能方面的度量指标有很多，我觉得有三个指标非常重要，就是：吞吐量、延迟和并发量。

+ 吞吐量：指的是单位时间内能处理的请求数量。吞吐量越高，说明性能越好。
+ 延迟：指的是从发出请求到收到响应的时间。延迟越小，说明性能越好。
+ 并发量：指的是能同时处理的请求数量，一般来说随着并发量的增加、延迟也会增加。所以延迟这个指标，一般都会是基于并发量来说的。例如并发量是 1000 的时候，延迟是 50 毫秒。

**总结：**

​	并发编程是一个复杂的技术领域，微观上涉及到原子性问题、可见性问题和有序性问题，宏观则表现为安全性、活跃性以及性能问题。

​	我们在设计并发程序的时候，主要是从宏观出发，也就是要重点关注它的安全性、活跃性以及性能。安全性方面要注意数据竞争和竞态条件，活跃性方面需要注意死锁、活锁、饥饿等问题，性能方面我们虽然介绍了两个方案，但是遇到具体问题，你还是要具体分析，根据特定的场景选择合适的数据结构和算法。

​	要解决问题，首先要把问题分析清楚。同样，要写好并发程序，首先要了解并发程序相关的问题，经过这 7 章的内容，相信你一定对并发程序相关的问题有了深入的理解，同时对并发程序也一定心存敬畏，因为一不小心就出问题了。不过这恰恰也是一个很好的开始，因为你已经学会了分析并发问题，然后解决并发问题也就不远了。

## 08、管程：并发编程的万能钥匙

**管程和信号量是等价的，所谓等价就是管程可以实现信号量，也能用信号量实现管程。**

所谓**管程：指的是管理共享变量以及对共享变量的操作过程，让它们支持并发操作。**

并发编程领域两大核心问题：

+ **互斥：**一个时刻只允许一个线程访问共享资源。
+ **同步：**线程之间如何通信、协作。
+ 这两大问题，管程都能够解决。

解决互斥问题：解决互斥问题的思路很简单，就是将共享变量及其对共享变量的操作，统一封装起来。

![1553610798529](E:\data\my-document\concurrent\assets\1553610798529.png)

解决同步问题：

​	在管程模型中，共享变量和对共享变量的操作是被封装起来的，图中最外层的框就代表封装的意思。框的上面有一个入口，并且入口的旁边还有一个入口等待队列。当多个线程同时试图进入管程内部时，只允许一个线程进入，其他线程在入口等待队列中等待。管程中还引入了条件变量的概念，而且**每个条件变量都对应一个等待队列。**

![1553611133991](E:\data\my-document\concurrent\assets\1553611133991.png)

**条件变量和等待队列就是用来解决线程同步问题。**

```java
public class BlockedQueue<T>{
  final Lock lock =
    new ReentrantLock();
  // 条件变量：队列不满  
  final Condition notFull =
    lock.newCondition();
  // 条件变量：队列不空  
  final Condition notEmpty =
    lock.newCondition();

  // 入队
  void enq(T x) {
    lock.lock();
    try {
      while (队列已满){
        // 等待队列不满 
        notFull.await();
      }  
      // 省略入队操作...
      // 入队后, 通知可出队
      notEmpty.signal();
    }finally {
      lock.unlock();
    }
  }
  // 出队
  void deq(){
    lock.lock();
    try {
      while (队列已空){
        // 等待队列不空
        notEmpty.await();
      }
      // 省略出队操作...
      // 出队后，通知可入队
      notFull.signal();
    }finally {
      lock.unlock();
    }  
  }
}

```

代码实现一个阻塞队列，阻塞队列有两个操作分别是入队和出队，两个方法都是先获取互斥锁，类比管程模型的入口。

+ 对于入队操作，如果队列已满，就需要等待队列不满，所以这里用了notFull.await()；
+ 对于出队操作，如果过队列为空，就需要等待队列不为空，所以这里用了notEmpty.await()；
+ 如果入队成功，那么队列不为空，就需要通知条件变量：队列不为空notEmpty对应的等待对列；
+ 如果出队成功，那么队列就不满了，就需要通知条件变量：队列不满notFull对应的等待队列。

需要注意的是：**await()和前面的wait()语义是一样的；signal()和前面的notify()语义是一样的。**

**wait()的正确使用：对于MESA管程来说，有一个编程范式，就是需要在一个while循环里面调用wait()。这个是MESA管程特有的。**

+ Hasen模型：notify()放在代码最后，T1通知完T2后，T1就结束了，然后T2再执行，保证同一个时刻，只有一个线程执行。
+ Hoare模型：T1通知完T2后，T1阻塞，T2马上执行；T2执行完后，再唤醒T1，也能保证统一时刻，只有一个线程执行。但是比Hasen模型多一次等待唤醒操作。
+ MESA模型：T1通知完T2后，T1继续执行，T2不是马上执行，仅仅是从条件变量等待队列进入入口等待队列里面。这样的好处是notify()不用放在代码最后，T1也没有多余的唤醒操作。但是也有副作用，T2再次执行的时候，可能曾经满足的条件，现在已经不满足了，所以需要以循环方式检验条件变量。

**notify()可以何时用：除非经过深思熟虑，否则尽量使用notifyAll()。**

需要满足下面三个条件：

+ 所有等待线程拥有相同的等待条件；
+ 所有等待线程被唤醒后，执行相同的操作；
+ 只需要唤醒一个线程。

## 09、JAVA线程（上）：JAVA线程的生命周期

通用的线程生命周期：

![1553613012491](E:\data\my-document\concurrent\assets\1553613012491.png)

**JAVA线程生命周期**

JAVA语言中线程生命周期共有六种状态：

+ NEW（初始化状态）
+ RUNNABLE（可运行/运行状态）
+ BLOCKED（阻塞状态）
+ WAITING（无时限等待状态）
+ TIME_WAITING（有限时等待状态）
+ TERMINATED（终止状态）

![1553613243939](E:\data\my-document\concurrent\assets\1553613243939.png)

**1、RUNNABLE与BLOCKED的状态转换**

只有一种场景，就是线程等待synchronized的隐式锁。**特别的说明：当JAVA调用阻塞式API时，比如IO，等待CPU使用权等，JVM层面不关心操作系统调度相关的状态，在JVM层面都是RUNNABLE状态。**

**2、RUNNABLE与WATING的状态转换**

有三种场景会触发：

+ 线程获取synchronized隐式锁，调用Object.wait()方法。
+ 调用无参的Thread.join()方法。调用线程等待，被调用线程执行，被调用线程执行完以后，调用线程继续执行。
+ 调用LockSupport.park()方法。

**3、RUNNABLE与TIMED_WAITING的状态转换**

有五种场景会触发：

+ 调用带超时参数的Thread.sleep(long millis)方法。
+ 获取synchronized隐式锁的线程，调用带超时参数的Object.wait(long timeout)方法。
+ 调用带参数参数的Thread.join(long millis)方法。
+ 调用带超时参数的LockSupport.parkNanos(Object blocker,long deadline)方法。
+ 调用带超时参数的LockSupport.parkUntil(long deadline)方法。

**4、从NEW到RUNNABLE状态**

调用start()方法。

**5、从RUNNABLE到TERMINATED状态**

调用interrupt()方法。stop()方法直接杀死线程，如果线程持有synchronized锁，也不会释放，其他线程就获取不到synchronized隐式锁。

interrupt()方法仅仅是给线程发送一个通知，线程可以根据通知做下一步的操作，也可以无视这个通知。

+ 当线程A处于WAITING、TIMED_WAITING状态时，如果其他线程调用线程A的interrupt()方法，线程会返回到RUNNABLE状态，同时线程A的代码会触发InterruptExcption异常。
+ 当线程A处于RUNNABLE状态，并且阻塞在java.nio.channels.InterruptibleChannel上时，如果其他线程调用线程A的Interrupt()方法，会触发java.nio.channels.ClosedByInterruptException；而阻塞在java.nio.channels.Selector上时，如果其他线程调用线程A的interrupt()方法，线程A的java.nio.channels.Selecor会立即返回。
+ 如果线程处于RUNNABLE状态，并且没有阻塞在某个I/O操作上，这时就需要依赖线程A的主动检测中断状态。通过调用isInterrupted()方法，检测自己是否被中断。

## 10、JAVA线程（中）：创建多少线程才合适

**为什么要使用多线程：主要是降低延迟，提高吞吐量。**

+ 延迟：发出请求到收到响应这个过程的时间。
+ 吞吐量：在单位时间内能处理的请求数量。

**同等条件下，延迟越短，吞吐量越大。但是他们属于不同维度，所以不能互相转换。**

**多线程应用场景：**

在并发编程领域，提升性能的本质就是提高硬件的利用率，再具体点就是提升I/O的利用率和CPU的利用率。我们的并发程序，往往需要CPU和I/O设备相互配合工作，也就是说，我们**需要解决CPU和I/O设备的综合利用率问题**。

![1553615389807](E:\data\my-document\concurrent\assets\1553615389807.png)

![1553615446366](E:\data\my-document\concurrent\assets\1553615446366.png)

**如果CPU和I/O设备的利用率很低，那么可以尝试通过增加线程来提供吞吐量。**

![1553615523250](E:\data\my-document\concurrent\assets\1553615523250.png)

**创建多少线程合适**

​	对于CPU密集型的计算场景，理论上“线程数量=CPU核数”就是最合适的，不过在实际业务中，线程数一般会设置成“CPU核数+1”，这样的话，当线程偶尔因为内存页失效或其他原因阻塞的时候，这个额外的线程可以补上，从而保证CPU的利用率。

对应I/O密集型计算场景，如果CPU计算和I/O操作耗时是1:1，那么2个线程最合适；如果CPU计算和I/O操作是1:2，那么3个线程最合适。

![1553615862078](E:\data\my-document\concurrent\assets\1553615862078.png)

通过上面的例子，对于I/O密集型计算场景，我们可以得出公式：

最佳线程数=CPU核数 x [1+（I/O耗时/CPU耗时）]

## 11、JAVA线程（下）：为什么局部变量是安全的

![1553616221621](E:\data\my-document\concurrent\assets\1553616221621.png)

![1553616274116](E:\data\my-document\concurrent\assets\1553616274116.png)

![1553616304599](E:\data\my-document\concurrent\assets\1553616304599.png)

**每个线程都有自己独立的调用栈。**

![1553616350015](E:\data\my-document\concurrent\assets\1553616350015.png)

方法里的局部变量，因为不和其他线程共享，所以没有并发问题，这个思路很好，已经成为解决并发问题的一个重要技术，叫做**线程封闭**，官方解释叫**仅在单线程内访问数据**。

采用线程封闭的案例很多，例如从数据库连接池中获取的连接Connection，在JDBC规范中并没有要求Connction必须是线程安全的。数据库连接池通过线程封闭技术，保证一个Connection一旦被一个线程获取之后，在这个线程关闭Connection之前，不会再分配给其他线程，从而保证了Connection不会有并发问题。

## 12、如何用面向对象的思想写好并发程序

**一、封装共享变量**

讲共享变量封装在对象内部，对外提供公共方法并制定并发访问策略。

```java
public class Counter {
  private long value;
  synchronized long get(){
    return value;
  }
  synchronized long addOne(){
    return ++value;
  }
}

```

**对于一些不会发生改变的共享变量，可以用final来修饰。**

**二、识别共享变量间的约束条件**

共享变量间的约束条件，决定了并发访问策略。

```java
public class SafeWM {
  // 库存上限
  private final AtomicLong upper =
        new AtomicLong(0);
  // 库存下限
  private final AtomicLong lower =
        new AtomicLong(0);
  // 设置库存上限
  void setUpper(long v){
    upper.set(v);
  }
  // 设置库存下限
  void setLower(long v){
    lower.set(v);
  }
  // 省略其他业务代码
}

```

上面这段代码要求**库存的下限要小于库存上限**，这个约束条件就是共享变量间的约束条件。

```java
public class SafeWM {
  // 库存上限
  private final AtomicLong upper =
        new AtomicLong(0);
  // 库存下限
  private final AtomicLong lower =
        new AtomicLong(0);
  // 设置库存上限
  void setUpper(long v){
    // 检查参数合法性
    if (v < lower.get()) {
      throw new IllegalArgumentException();
    }
    upper.set(v);
  }
  // 设置库存下限
  void setLower(long v){
    // 检查参数合法性
    if (v > upper.get()) {
      throw new IllegalArgumentException();
    }
    lower.set(v);
  }
  // 省略其他业务代码
}
```

**一定要识别出所有共享变量之间的约束条件，如果约束条件识别不足，很可能导致并发访问策略的错误。**

**三、指定并发访问策略**

并发访问策略方案：

+ 避免共享：线程本地存储以及为每个任务分配独立线程
+ 不变模式：JAVA领域较少见，其他领域应用广泛，例如Actor模式，CSP模式及函数式编程的基础都是不变模式。
+ 管程及其他同步工具：管程是万能的解决方案，但是对于特定的场景，JAVA并发包提供的读写锁，并发容器等同步工具更好。

并发编程的宏观原则：

+ 优先使用成熟的工具类
+ 迫不得已才使用低级的同步原语：低级的同步原语主要指的是：synchronized、Lock、Semaphore等，感觉简单，其实并不简单。
+ 避免过早优化：首先保证安全，出现性能瓶颈再考虑优化。

## 14、Lock和Condition

**JAVA SDK并发包通过Lock和Condition两个接口实现管程。Lock解决互斥问题，Condition解决同步问题。**

**破坏不可抢占条件方案：**

​	对于“不可抢占”条件，占用部分资源的线程进一步申请其他资源时，如果申请不到，可以主动释放它占有的资源，这样就可以破坏不可抢占条件**。**

**三种方案解决：**

1. 能够响应中断：
2. 支持超时：
3. 非阻塞的获取锁：

```java
// 支持中断的 API
void lockInterruptibly() 
  throws InterruptedException;
// 支持超时的 API
boolean tryLock(long time, TimeUnit unit) 
  throws InterruptedException;
// 支持非阻塞获取锁的 API
boolean tryLock();

```

**Java SDK里面的锁利用了volatile相关的happens-before规则保证可见性。**

```java
class SampleLock {
  volatile int state;
  // 加锁
  lock() {
    // 省略代码无数
    state = 1;
  }
  // 解锁
  unlock() {
    // 省略代码无数
    state = 0;
  }
}

```

**可重入锁：线程可以重复的获取一把锁。**

```java
class X {
  private final Lock rtl = new ReentrantLock();
  int value;
  public int get() {
    // 获取锁
    rtl.lock();         ②
    try {
      return value;
    } finally {
      // 保证锁能释放
      rtl.unlock();
    }
  }
  public void addOne() {
    // 获取锁
    rtl.lock();  
    try {
      value = 1 + get(); ①
    } finally {
      // 保证锁能释放
      rtl.unlock();
    }
  }
}

```

**公平锁：唤醒等待队列中等待时间最长的线程；非公平锁：随机唤醒，可能等待时间短的线程先被唤醒。**

**用锁的最佳实践：**

1. 永远只在更新对象的成员变量时加锁；
2. 永远只在访问可变的成员变量时加锁；
3. 永远不在调用其他对象的方法时加锁；

**Condition实现管程模型中的条件变量。**

利用两个条件变量实现阻塞队列：

```java
public class BlockedQueue<T>{
  final Lock lock = new ReentrantLock();
  // 条件变量：队列不满  
  final Condition notFull = lock.newCondition();
  // 条件变量：队列不空  
  final Condition notEmpty = lock.newCondition();

  // 入队
  void enq(T x) {
    lock.lock();
    try {
      while (队列已满){
        // 等待队列不满
        notFull.await();
      }  
      // 省略入队操作...
      // 入队后, 通知可出队
      notEmpty.signal();
    }finally {
      lock.unlock();
    }
  }
  // 出队
  void deq(){
    lock.lock();
    try {
      while (队列已空){
        // 等待队列不空
        notEmpty.await();
      }  
      // 省略出队操作...
      // 出队后，通知可入队
      notFull.signal();
    }finally {
      lock.unlock();
    }  
  }
}

```

## 16、Semaphore：如何快速实现一个限流器？

信号量模型可以简单概括为：**一个计数器、一个等待队列、三个方法。**计数器和等待队列是透明的，通过信号量模型提供的三个方法来访问它们，三个方法分别为：init()，down()和up()。

![1554722297292](E:\data\my-document\concurrent\assets\1554722297292.png)

三个方法的语义如下：

+ init()：设置计数器的初始值；
+ down()：计数器值减1；如果计数器的值小于0，则当前线程被阻塞，否则当前线程可以继续执行；
+ up()：计数器值加1；如果此时计数器的值小于或等于0，则唤醒等待队列中的一个线程，并将其从等待队列移除。

**Semaphore可以允许多个线程访问临界区。**比如各种池技术：连接池、对象池、线程池等。

## 17、ReadWriteLock：如何快速的实现一个完备的缓存

并发包的工具能的作用：**分场景优化性能，提升易用性。**

读写锁遵循的三个基本原则：

1. 允许多个线程同时读取共享变量；
2. 只允许一个线程写共享变量；
3. 如果一个线程正在写共享变量，此时禁止其他线程读写共享变量。

读写锁与互斥锁的一个重要区别是：**读写锁允许多个线程同时读取共享变量。**

ReadWriteLock实现缓存：

```java
class Cache<K,V> {
  final Map<K, V> m = new HashMap<>();
  final ReadWriteLock rwl = new ReentrantReadWriteLock();
  // 读锁
  final Lock r = rwl.readLock();
  // 写锁
  final Lock w = rwl.writeLock();
  // 读缓存
  V get(K key) {
    r.lock();
    try { return m.get(key); }
    finally { r.unlock(); }
  }
  // 写缓存
  V put(String key, Data v) {
    w.lock();
    try { return m.put(key, v); }
    finally { w.unlock(); }
  }
}

```

缓存加载分为一次性加载和懒加载。

```java
class Cache<K,V> {
  final Map<K, V> m = new HashMap<>();
  final ReadWriteLock rwl = new ReentrantReadWriteLock();
  final Lock r = rwl.readLock();
  final Lock w = rwl.writeLock();
 
  V get(K key) {
    V v = null;
    // 读缓存
    r.lock();         ①
    try {
      v = m.get(key); ②
    } finally{
      r.unlock();     ③
    }
    // 缓存中存在，返回
    if(v != null) {   ④
      return v;
    }  
    // 缓存中不存在，查询数据库
    w.lock();         ⑤
    try {
      // 再次验证
      // 其他线程可能已经查询过数据库
      v = m.get(key); ⑥
      if(v == null){  ⑦
        // 查询数据库
        v= 省略代码无数
        m.put(key, v);
      }
    } finally{
      w.unlock();
    }
    return v; 
  }
}

```

ReadWriteLock支持锁的降级，不支持锁升级。如果读锁还没有释放，此时获取写锁，会导致写锁永久等待，最终导致相关线程被阻塞，永远也没有机会被唤醒。

锁降级示例：

```java
class CachedData {
  Object data;
  volatile boolean cacheValid;
  final ReadWriteLock rwl = new ReentrantReadWriteLock();
  // 读锁  
  final Lock r = rwl.readLock();
  // 写锁
  final Lock w = rwl.writeLock();
  
  void processCachedData() {
    // 获取读锁
    r.lock();
    if (!cacheValid) {
      // 释放读锁，因为不允许读锁的升级
      r.unlock();
      // 获取写锁
      w.lock();
      try {
        // 再次检查状态  
        if (!cacheValid) {
          data = ...
          cacheValid = true;
        }
        // 释放写锁前，降级为读锁
        // 降级是可以的
        r.lock(); ①
      } finally {
        // 释放写锁
        w.unlock(); 
      }
    }
    // 此处仍然持有读锁
    try {use(data);} 
    finally {r.unlock();}
  }
}

```

## 18、StampedLock：有没有比读写锁更快的锁？

StampedLock包含三种模式：**写锁、悲观读锁和乐观读。**写锁和悲观读锁的语义和ReadWriteLock的写锁、读锁语义非常类似。

```java
final StampedLock sl = new StampedLock();
  
// 获取 / 释放悲观读锁示意代码
long stamp = sl.readLock();
try {
  // 省略业务相关代码
} finally {
  sl.unlockRead(stamp);
}

// 获取 / 释放写锁示意代码
long stamp = sl.writeLock();
try {
  // 省略业务相关代码
} finally {
  sl.unlockWrite(stamp);
}

```

**注意：这里的乐观读不是乐观读锁，相对于读锁，性能更好。**

```java
class Point {
  private int x, y;
  final StampedLock sl = new StampedLock();
  // 计算到原点的距离  
  int distanceFromOrigin() {
    // 乐观读
    long stamp = sl.tryOptimisticRead();
    // 读入局部变量，
    // 读的过程数据可能被修改
    int curX = x, curY = y;
    // 判断执行读操作期间，
    // 是否存在写操作，如果存在，
    // 则 sl.validate 返回 false
    if (!sl.validate(stamp)){
      // 升级为悲观读锁
      stamp = sl.readLock();
      try {
        curX = x;
        curY = y;
      } finally {
        // 释放悲观读锁
        sl.unlockRead(stamp);
      }
    }
    return Math.sqrt(
      curX * curX + curY * curY);
  }
}

```

在上面的代码中，如果执行乐观读期间，发生了写操作，会把乐观读升级为悲观读锁。这样做是合理的，否则就需要在一个循环里面反复执行乐观读，直到乐观读操作的期间没有写操作（这样才能保证X和Y的正确性和一致性），而循环会浪费大量的CPU时间。

StampedLock适合读多写少的应用场景。但是StampedLock的功能仅仅是ReadWriteLock功能的子集，使用的时候需要注意如下几个方面：

+ StampedLock不是可重入的锁。
+ StampedLock不支持条件变量。
+ 线程阻塞在readLoc()和writeLock()方法上时，调用当前线程的interrupt()方法，会导致CPU飙升。
  + **使用StampedLock一定不要调用中断操作，如果要响应中断，一定要使用可中断的悲观读锁readLockInterruptibly()和写锁writeLockInterruptibly()。**

StampedLock读模板：

```java
final StampedLock sl = new StampedLock();

// 乐观读
long stamp = 
  sl.tryOptimisticRead();
// 读入方法局部变量
......
// 校验 stamp
if (!sl.validate(stamp)){
  // 升级为悲观读锁
  stamp = sl.readLock();
  try {
    // 读入方法局部变量
    .....
  } finally {
    // 释放悲观读锁
    sl.unlockRead(stamp);
  }
}
// 使用方法局部变量执行业务操作
......

```

StampedLock写模板：

```java
long stamp = sl.writeLock();
try {
  // 写共享变量
  ......
} finally {
  sl.unlockWrite(stamp);
}

```

## 19、CountDownLatch和CyclicBarrier：然后让多线程步调一致？

对账系统逻辑：

![1555564887107](E:\data\my-document\concurrent\assets\1555564887107.png)

核心代码如下，在一个单线程里面循环查询订单、派送单，然后对账入差异库：

```java
while(存在未对账订单){
  // 查询未对账订单
  pos = getPOrders();
  // 查询派送单
  dos = getDOrders();
  // 执行对账操作
  diff = check(pos, dos);
  // 差异写入差异库
  save(diff);
} 

```

利用并行优化对账系统：

![1555565250187](E:\data\my-document\concurrent\assets\1555565250187.png)

![1555565261862](E:\data\my-document\concurrent\assets\1555565261862.png)

核心代码如下：

```java
while(存在未对账订单){
  // 查询未对账订单
  Thread T1 = new Thread(()->{
    pos = getPOrders();
  });
  T1.start();
  // 查询派送单
  Thread T2 = new Thread(()->{
    dos = getDOrders();
  });
  T2.start();
  // 等待 T1、T2 结束
  T1.join();
  T2.join();
  // 执行对账操作
  diff = check(pos, dos);
  // 差异写入差异库
  save(diff);
} 

```

上面的代码有个问题：每次都有创建新的线程，这显然是个耗时的操作。所以最好用线程池来改写一下。但是使用线程池以后，就不能用线程的join()方法来等待线程退出，所以join()方法失效。

```java
// 创建 2 个线程的线程池
Executor executor = Executors.newFixedThreadPool(2);
while(存在未对账订单){
  // 查询未对账订单
  executor.execute(()-> {
    pos = getPOrders();
  });
  // 查询派送单
  executor.execute(()-> {
    dos = getDOrders();
  });
  
  /* ？？如何实现等待？？*/
  
  // 执行对账操作
  diff = check(pos, dos);
  // 差异写入差异库
  save(diff);
}   

```

如果有一个计数器，初始值为0，查询订单操作完成+1，查询派送单操作完成+1，当计数器的值为2的时候，就可以执行对账操作了。JAVA并发包提供了这样的工具类：CountDownLatch。

```java
// 创建 2 个线程的线程池
Executor executor = Executors.newFixedThreadPool(2);
while(存在未对账订单){
  // 计数器初始化为 2
  CountDownLatch latch = new CountDownLatch(2);
  // 查询未对账订单
  executor.execute(()-> {
    pos = getPOrders();
    latch.countDown();
  });
  // 查询派送单
  executor.execute(()-> {
    dos = getDOrders();
    latch.countDown();
  });
  
  // 等待两个查询操作结束
  latch.await();
  
  // 执行对账操作
  diff = check(pos, dos);
  // 差异写入差异库
  save(diff);
}

```

上面的程序还可以进一步优化，两个查询订单的操作和对账操作、保存差异库操作还是串行的。很明显，查询订单操作和对账操作也可以并行操作。

![1555565771579](E:\data\my-document\concurrent\assets\1555565771579.png)

查询操作和对账操作并行，对账操作依赖查询操作的结果，可以通过生产者-消费者的模式来改写。订单和派送单1对1关系，设计两个队列，让这种对应关系不会乱。

![1555565924056](E:\data\my-document\concurrent\assets\1555565924056.png)

​	下面再来看如何用双队列来实现完全的并行。一个最直接的想法是：一个线程 T1 执行订单的查询工作，一个线程 T2 执行派送单的查询工作，当线程 T1 和 T2 都各自生产完 1 条数据的时候，通知线程 T3 执行对账操作。这个想法虽看上去简单，但其实还隐藏着一个条件，那就是线程 T1 和线程 T2 的工作要步调一致，不能一个跑得太快，一个跑得太慢，只有这样才能做到各自生产完 1 条数据的时候，通知线程 T3。

​	下面这幅图形象地描述了上面的意图：线程 T1 和线程 T2 只有都生产完 1 条数据的时候，才能一起向下执行，也就是说，线程 T1 和线程 T2 要互相等待，步调要一致；同时当线程 T1 和 T2 都生产完一条数据的时候，还要能够通知线程 T3 执行对账操作。

![1555566169108](E:\data\my-document\concurrent\assets\1555566169108.png)

上面的同步方案需要CyclicBarrier来实现。非常值得一提的是，CyclicBarrier有自动重置功能，当减到0的时候，会自动重置你设置的初始值。

```java
// 订单队列
Vector<P> pos;
// 派送单队列
Vector<D> dos;
// 执行回调的线程池 
Executor executor = Executors.newFixedThreadPool(1);
final CyclicBarrier barrier = new CyclicBarrier(2, ()->{
    executor.execute(()->check());
  });
  
void check(){
  P p = pos.remove(0);
  D d = dos.remove(0);
  // 执行对账操作
  diff = check(p, d);
  // 差异写入差异库
  save(diff);
}
  
void checkAll(){
  // 循环查询订单库
  Thread T1 = new Thread(()->{
    while(存在未对账订单){
      // 查询订单库
      pos.add(getPOrders());
      // 等待
      barrier.await();
    }
  });
  T1.start();  
  // 循环查询运单库
  Thread T2 = new Thread(()->{
    while(存在未对账订单){
      // 查询运单库
      dos.add(getDOrders());
      // 等待
      barrier.await();
    }
  });
  T2.start();
}

```

**总结：**

**CountDownLatch主要用来解决一个线程等待多个线程的场景。而CyclicBarrier是一组线程相互等待。**CountDownLatch的计数器不能循环利用，一旦计数器减到0，再有线程调用await()，该线程会直接通过。CyclicBarrier具有自动重置功能，一旦计数器减到0会自动重置到你设置的初始值。另外CyclicBarrier还可以设置回调函数。

## 20、并发容器：都有哪些“坑”需要我们填？

**组合操作需要注意竞态条件问题：即便每个单独的操作是原子性的，也不能保证组合操作是原子性的。**

**对并发容器的遍历操作，需要加锁保证互斥。**

![1555918438711](E:\data\my-document\concurrent\assets\1555918438711.png)

**（一）List**

CopyOnWriteArrayList：顾名思义就是写的时候会将共享变量新复制一份出来，这样的好处是读的时候完全无锁。

![1555918581310](E:\data\my-document\concurrent\assets\1555918581310.png)

![1555918671631](E:\data\my-document\concurrent\assets\1555918671631.png)

CopyOnWriteArrayList的使用需要注意两方面：

+ 应用场景：适用于写非常少的场景，并能接受短暂的读写不一致的情况。
+ 迭代器是只读的，不支持增删改，因为遍历的是一个快照。

**（二）Map**

Map接口的两个实现是ConcurrentHashMap和ConcurrentSkipListMap，**主要区别是ConcurrentHashMap的key是无序的，ConcurrentSkipListMap的key是有序的**。

![1555919033352](E:\data\my-document\concurrent\assets\1555919033352.png)

ConcurrentSkipListMap的插入、删除、查询操作平均的时间复杂度为O(logn)，理论上和并发线程数无关，所以在并发度非常高的情况下，若对ConcurrentHashMap的性能不满意，可以尝试ConcurrentSkipListMap。ConcurrentSkipListMap采用空间换时间的概念提升操作效率。

**（三）Set**

Set接口的两个实现CopyOnWriteArraySet和ConcurrentSkipListSet。使用场景和CopyOnWriteArrayList和ConcurrentSkipListMap类似。

**（四）Queue**

Java并发包的Queue可以按两个维度来划分，一个维度是：**阻塞与非阻塞**，另一个维度是：**单端和双端**。

阻塞队列：**用Blocking关键字标识；**单端队列：**用Queue关键字标识；**双端队列：**用Deque关键字标识**。

按这两个维度，可以将Queue分为四大类：

1、**单端阻塞队列**

+ ArrayBlockingQueue、LinkedBlockingQueue、SynchronousQueue、LinkedTransferQueue、PriorityBlockingQueue和DelayQueue。
+ 内部一般会持有一个队列，这个队列可以是数组（其实现是ArrayBlockingQueue）；也可以是一个链表（其实现是LinkedBlockingQueue）；甚至还可以不持有队列（其实现是SynchronousQueue），此时生产者线程的入队操作必须等待消费者线程的出队操作。
+ LinkedTransferQueue融合LinkedBlockingQueue和SynchronousQueue的功能，性能比LinkedBlockingQueue更好。
+ PriorityBlockingQueue支持按照优先级出队。
+ DelayQueue支持延时出队。

![1555919954208](E:\data\my-document\concurrent\assets\1555919954208.png)

2、**双端阻塞队列**

+ LinkedBlockingDeque

![1555920006979](E:\data\my-document\concurrent\assets\1555920006979.png)

3、**单端非阻塞队列**

+ ConcurrentLinkedQueue

4、**双端非阻塞队列**

+ ConcurrentLinkedDeque

另外，使用队列需要格外注意队列是否支持有界。在实际工作中，一般不建议使用无界队列，因为数量大了以后，可能引发OOM。上面提到的Queue中只有ArrayBlockingQueue和LinkedBlockingQueue是支持有界的。**在使用无界队列时，一定要充分考虑是否有导致OOM的隐患。**

## 21、原子类：无锁工具类的典范

对于简单的原子性问题，Java提供了**无锁方案**。无锁方案相对于互斥方案，最大的好处就是**性能**。互斥方案的加锁、解锁，同时拿不到锁的线程会进入阻塞状态，进而触发线程切换，线程切换对性能消耗比较大，这些操作都会影响性能。无锁方案不需要加锁、解锁也能保证互斥。

```java
public class Test {
  AtomicLong count = new AtomicLong(0);
  void add10K() {
    int idx = 0;
    while(idx++ < 10000) {
      count.getAndIncrement();
    }
  }
}

```

无锁方案的原理：**无锁方案利用的CPU的CAS指令本身的原子性实现。**

CAS指令包含三个参数：内存地址A，用于比较的值B，共享变量的新值C。只有当内存地址A中的值等于B时，才能把内存地址A处的值更新为新值C。

模拟CAS原理代码：

```java
class SimulatedCAS{
  int count；
  synchronized int cas(int expect, int newValue){
    // 读目前 count 的值
    int curValue = count;
    // 比较目前 count 值是否 == 期望值
    if(curValue == expect){
      // 如果是，则更新 count 的值
      count = newValue;
    }
    // 返回写入前的值
    return curValue;
  }
}

```

使用CAS来解决并发问题，一般会伴随着自旋，所谓自旋就是循环尝试。CAS+自旋的实现方案如下：

```java
class SimulatedCAS{
  volatile int count;
  // 实现 count+=1
  addOne(){
    do {
      newValue = count+1; //①
    }while(count != cas(count,newValue) //②
  }
  // 模拟实现 CAS，仅用来帮助理解
  synchronized int cas(int expect, int newValue){
    // 读目前 count 的值
    int curValue = count;
    // 比较目前 count 值是否 == 期望值
    if(curValue == expect){
      // 如果是，则更新 count 的值
      count= newValue;
    }
    // 返回写入前的值
    return curValue;
  }
}

```

使用CAS时要注意**ABA**问题。

Java  SDK并发包提供的原子类主要分为五大类：**原子化的基本数据类型、原子化的对象引用类型、原子化数组、原子化对象属性更新器和原子化的累加器。**

![1555921509810](E:\data\my-document\concurrent\assets\1555921509810.png)

**1、原子化的基本数据类型**

```java
getAndIncrement() // 原子化 i++
getAndDecrement() // 原子化的 i--
incrementAndGet() // 原子化的 ++i
decrementAndGet() // 原子化的 --i
// 当前值 +=delta，返回 += 前的值
getAndAdd(delta) 
// 当前值 +=delta，返回 += 后的值
addAndGet(delta)
//CAS 操作，返回是否成功
compareAndSet(expect, update)
// 以下四个方法
// 新值可以通过传入 func 函数来计算
getAndUpdate(func)
updateAndGet(func)
getAndAccumulate(x,func)
accumulateAndGet(x,func)

```

**2、原子化的对象引用类型**

**对象引用的更新需要重点关注ABA问题**。AtomicStampedReference和AtomicMarkableReference这两个原子类可以解决ABA问题。解决ABA问题的思路很简单，增加一个版本号维度就可以。AtomicStampedReference解决ABA问题就是增加了版本号。

```java
boolean compareAndSet(
  V expectedReference,
  V newReference,
  int expectedStamp,
  int newStamp) 

```

AtomicMarkableReference的实现机制更简单，将版本号简化为一个boolean值。

```java
boolean compareAndSet(
  V expectedReference,
  V newReference,
  boolean expectedMark,
  boolean newMark)

```

**3、原子化数组**

利用这些原子类，我们可以原子化的更新数组里面的每一个元素。这些类提供的方法和原子化的基本数据类型的区别仅仅是：每个方法多一个数组的索引参数。

**4、原子化对象属性更新器**

可以原子化的更新对象的属性，利用反射的机制实现，创建更新器的方法如下：

```java
public static <U> AtomicXXXFieldUpdater<U> newUpdater(Class<U> tclass, String fieldName)

```

**需要注意，对象的属性必须是volatile类型的，这样才能保证可见性。**如果对象属性不是volatile，newUpdater()方法会抛出IllegalArgumentException()这个运行时异常。

```java
boolean compareAndSet(
  T obj, 
  int expect, 
  int update)

```

**5、原子化累加器**

累加器仅用来执行累加，相比原子化的基本类型，速度更快，但不支持compareAndSet()方法。

## 22、Executor与线程池：如何正确的创建线程池？

**线程是一个重量级的对象，应该避免频繁创建和销毁。**

线程池的设计，普遍采用的是**生产者-消费者模式**。线程的使用方是生产者，线程池是消费者。

```java
// 简化的线程池，仅用来说明工作原理
class MyThreadPool{
  // 利用阻塞队列实现生产者 - 消费者模式
  BlockingQueue<Runnable> workQueue;
  // 保存内部工作线程
  List<WorkerThread> threads = new ArrayList<>();
  // 构造方法
  MyThreadPool(int poolSize, BlockingQueue<Runnable> workQueue){
    this.workQueue = workQueue;
    // 创建工作线程
    for(int idx=0; idx<poolSize; idx++){
      WorkerThread work = new WorkerThread();
      work.start();
      threads.add(work);
    }
  }
  // 提交任务
  void execute(Runnable command){
    workQueue.put(command);
  }
  // 工作线程负责消费任务，并执行任务
  class WorkerThread extends Thread{
    public void run() {
      // 循环取任务并执行
      while(true){ ①
        Runnable task = workQueue.take();
        task.run();
      } 
    }
  }  
}

/** 下面是使用示例 **/
// 创建有界阻塞队列
BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(2);
// 创建线程池  
MyThreadPool pool = new MyThreadPool(10, workQueue);
// 提交任务  
pool.execute(()->{
    System.out.println("hello");
});

```

Java线程池相关的工具类，最核心的就是ThreadPoolExecutor，它强调的是Executor，而不是一般意义上的池化资源。

```java
ThreadPoolExecutor(
  int corePoolSize,
  int maximumPoolSize,
  long keepAliveTime,
  TimeUnit unit,
  BlockingQueue<Runnable> workQueue,
  ThreadFactory threadFactory,
  RejectedExecutionHandler handler) 
```

线程池的相关参数：

+ corePoolSize：表示线程池保有的最小线程数。
+ maximumPoolSize：表示线程池创建的最大线程数。当任务很多的时候，将线程加到maximumPoolSize，当任务数减少就销毁线程数到corePoolSize。
+ keepAliveTime&unit：定义线程闲置时间。如果一个线程空闲了keepAliveTime&unit时间，线程数又大于corePoolSize，那么这个线程就会被销毁。
+ workQueue：工作队列。
+ threadFactory：创建线程的工厂类，可以自定义创建线程。
+ handler：自定义任务拒绝策略。如果线程池中没有空闲线程，并且工作队列也满了（前提是有界队列），这时提交任务，线程池就会拒绝接收。
  + CallerRunsPolicy：提交任务的线程自己去执行该任务。
  + AbortPolicy：默认的拒绝策略，会throws RejectedExecutionException。
  + DiscardPolicy：直接丢弃任务，没有异常抛出。
  + DiscardOldestPolicy：丢弃最老的任务，其实是把最早进入工作队列的任务丢弃，然后把新的任务加到工作队列。

Java1.6还增加了allowCoreThreadTimeOut(boolean v)方法，它可以让所有线程都支持超时，这意味着没有任务时，所有线程都可以销毁。

线程池的创建不建议使用Executors，不建议使用的重要原因是：Executors提供的很多方法默认都是使用无界队列LinkedBlockingQueue，高负载的情况下，无界队列很容易导致OOM，会导致所有的任务都无法处理，所以**强烈建议使用有界队列。**

使用有界队列，当任务过多时，会触发执行拒绝策略。默认的拒绝策略要慎重使用，在实际工作中，建议采用自定义拒绝策略，并配合降级策略一起使用。

在使用线程池时，还要注意异常处理问题。任务的执行过程中出现运行时异常，会导致执行任务的线程终止。最致命的是，任务虽然异常了，但不会收的任何通知，这会让你误以为任务是正常执行完成的。虽然线程池提供了很多用于异常处理的方法，但是最稳妥和简单的方案还是捕获所有异常，并按需处理。

```java
try {
  // 业务逻辑
} catch (RuntimeException x) {
  // 按需处理
} catch (Throwable x) {
  // 按需处理
} 
```

## 23、Future：如何用多线程实现最优的“烧水泡茶”程序？

Java通过ThreadPoolExecutor提供的3个submit()方法和1个FutureTask工具类来支持获取任务执行结果的需求。

```java
// 提交 Runnable 任务
Future<?> submit(Runnable task);
// 提交 Callable 任务
<T> Future<T> submit(Callable<T> task);
// 提交 Runnable 任务及结果引用  
<T> Future<T> submit(Runnable task, T result);

```

这3个submit()方法的区别在于参数不同，介绍如下：

+ 提交Runnable任务submit(Runnable task)：方法参数是Runnable接口，这个接口没有返回值，所以这个方法返回的Future对象仅可以用来断言任务已经结束，类似于Thread.join()。
+ 提交Callable任务的submit(Callable<T> task)：方法参数是Callable接口，这个接口有一个call()方法，并且这个方法有返回值，所以可以通过返回的Future对象调用get()方法获取执行结果。
+ 提交Runnable任务及结果引用submit(Runnable task, T result)：方法返回的Future对象调用get()方法得到的结果就是调用submit()方法传入的result。Runnable接口的实现类Task声明一个有参的构造函数Task(Result r)，创建Task时传入result对象，这样就能在Task类的run()方法中对result进行各种操作。result相当于主线程和子线程之间的桥梁，通过它可以在主线程和子线程间共享变量。

```java
ExecutorService executor = Executors.newFixedThreadPool(1);
// 创建 Result 对象 r
Result r = new Result();
r.setAAA(a);
// 提交任务
Future<Result> future = executor.submit(new Task(r), r);  
Result fr = future.get();
// 下面等式成立
fr === r;
fr.getAAA() === a;
fr.getXXX() === x

class Task implements Runnable{
  Result r;
  // 通过构造函数传入 result
  Task(Result r){
    this.r = r;
  }
  void run() {
    // 可以操作 result
    a = r.getAAA();
    r.setXXX(x);
  }
}

```

Future接口有5个方法：

+ cancel()：取消任务。
+ isCanceled()：判断任务是否已取消。
+ isDone()：判断任务是否已结束。
+ get()：获取任务执行结果。
+ get(timeout, unit)：响应超时的获取任务结果。

两个get方法都是阻塞式的获取任务结果，如果任务没有执行完，那么调用get()方法的线程就会阻塞，直到任务执行完成才会被唤醒。

```java
// 取消任务
boolean cancel(boolean mayInterruptIfRunning);
// 判断任务是否已取消  
boolean isCancelled();
// 判断任务是否已结束
boolean isDone();
// 获得任务执行结果
get();
// 获得任务执行结果，支持超时
get(long timeout, TimeUnit unit);

```

FutureTask类的简单实用：

```java
// 创建 FutureTask
FutureTask<Integer> futureTask = new FutureTask<>(()-> 1+2);
// 创建线程池
ExecutorService es = Executors.newCachedThreadPool();
// 提交 FutureTask 
es.submit(futureTask);
// 获取计算结果
Integer result = futureTask.get();

```

```java
// 创建 FutureTask
FutureTask<Integer> futureTask = new FutureTask<>(()-> 1+2);
// 创建并启动线程
Thread T1 = new Thread(futureTask);
T1.start();
// 获取计算结果
Integer result = futureTask.get();

```

“烧水泡茶”程序：

![1556006049281](E:\data\my-document\concurrent\assets\1556006049281.png)

采用并发编程的方式，分工执行，将任务拆解给多个线程并行执行：

![1556006146247](E:\data\my-document\concurrent\assets\1556006146247.png)

对于T1线程的等待操作，可以用多种方法实现，比如：Thread.join()、CountDownLatch、甚至阻塞队列等方式。用Future也可以实现。

```java
// 创建任务 T2 的 FutureTask
FutureTask<String> ft2 = new FutureTask<>(new T2Task());
// 创建任务 T1 的 FutureTask
FutureTask<String> ft1 = new FutureTask<>(new T1Task(ft2));
// 线程 T1 执行任务 ft1
Thread T1 = new Thread(ft1);
T1.start();
// 线程 T2 执行任务 ft2
Thread T2 = new Thread(ft2);
T2.start();
// 等待线程 T1 执行结果
System.out.println(ft1.get());

// T1Task 需要执行的任务：
// 洗水壶、烧开水、泡茶
class T1Task implements Callable<String>{
  FutureTask<String> ft2;
  // T1 任务需要 T2 任务的 FutureTask
  T1Task(FutureTask<String> ft2){
    this.ft2 = ft2;
  }
  @Override
  String call() throws Exception {
    System.out.println("T1: 洗水壶...");
    TimeUnit.SECONDS.sleep(1);
    
    System.out.println("T1: 烧开水...");
    TimeUnit.SECONDS.sleep(15);
    // 获取 T2 线程的茶叶  
    String tf = ft2.get();
    System.out.println("T1: 拿到茶叶:"+tf);

    System.out.println("T1: 泡茶...");
    return " 上茶:" + tf;
  }
}
// T2Task 需要执行的任务:
// 洗茶壶、洗茶杯、拿茶叶
class T2Task implements Callable<String> {
  @Override
  String call() throws Exception {
    System.out.println("T2: 洗茶壶...");
    TimeUnit.SECONDS.sleep(1);

    System.out.println("T2: 洗茶杯...");
    TimeUnit.SECONDS.sleep(2);

    System.out.println("T2: 拿茶叶...");
    TimeUnit.SECONDS.sleep(1);
    return " 龙井 ";
  }
}
// 一次执行结果：
T1: 洗水壶...
T2: 洗茶壶...
T1: 烧开水...
T2: 洗茶杯...
T2: 拿茶叶...
T1: 拿到茶叶: 龙井
T1: 泡茶...
上茶: 龙井
	
```

## 24、CompletableFuture：异步编程没那么难

**异步化**是并行方案实施的基础，其实就是：**利用多线程优化性能。**

![1556084569379](E:\data\my-document\concurrent\assets\1556084569379.png)

```java
// 任务 1：洗水壶 -> 烧开水
CompletableFuture<Void> f1 = CompletableFuture.runAsync(()->{
  System.out.println("T1: 洗水壶...");
  sleep(1, TimeUnit.SECONDS);

  System.out.println("T1: 烧开水...");
  sleep(15, TimeUnit.SECONDS);
});
// 任务 2：洗茶壶 -> 洗茶杯 -> 拿茶叶
CompletableFuture<String> f2 = CompletableFuture.supplyAsync(()->{
  System.out.println("T2: 洗茶壶...");
  sleep(1, TimeUnit.SECONDS);

  System.out.println("T2: 洗茶杯...");
  sleep(2, TimeUnit.SECONDS);

  System.out.println("T2: 拿茶叶...");
  sleep(1, TimeUnit.SECONDS);
  return " 龙井 ";
});
// 任务 3：任务 1 和任务 2 完成后执行：泡茶
CompletableFuture<String> f3 = f1.thenCombine(f2, (__, tf)->{
    System.out.println("T1: 拿到茶叶:" + tf);
    System.out.println("T1: 泡茶...");
    return " 上茶:" + tf;
  });
// 等待任务 3 执行结果
System.out.println(f3.join());

void sleep(int t, TimeUnit u) {
  try {
    u.sleep(t);
  }catch(InterruptedException e){}
}
// 一次执行结果：
T1: 洗水壶...
T2: 洗茶壶...
T1: 烧开水...
T2: 洗茶杯...
T2: 拿茶叶...
T1: 拿到茶叶: 龙井
T1: 泡茶...
上茶: 龙井

```

**创建CompletableFuture对象**

```java
// 使用默认线程池
static CompletableFuture<Void> runAsync(Runnable runnable)
static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier)
// 可以指定线程池  
static CompletableFuture<Void> runAsync(Runnable runnable, Executor executor)
static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier, Executor executor)  

```

Runnable接口的run()方法没有返回值，Supplier接口的get()方法有返回值。默认情况下CompletableFuture会使用公共的ForkJoinPool线程池，这个线程池默认创建的线程数是CPU核数-1（也可以通过JVM option：-Djava.util.concurrent.ForkJoinPool.common.parallelism设置ForkJoinPool线程池的线程数）。如果所有CompletableFuture共享一个线程池，一旦有线程执行较慢的I/O操作，就会阻塞其他的线程，导致线程饥饿，影响系统的性能。所以强烈建议：**根据不同的业务创建不同的线程池，以避免相互干扰。**

**理解CompletionStage接口**

任务是有时序关系的，比如：**串行关系、并行关系、汇聚关系等。**

![1556086419137](E:\data\my-document\concurrent\assets\1556086419137.png)

![1556086427145](E:\data\my-document\concurrent\assets\1556086427145.png)

![1556086438684](E:\data\my-document\concurrent\assets\1556086438684.png)

聚合关系分为AND聚合和OR聚合，AND聚合指的是依赖的的任务（烧开水和拿茶叶）都完成了，才开始执行当前任务（泡茶）。OR聚合指的是依赖的任务只要有一个完成了就可以执行当前任务。

**1、描述串行关系**

描述串行关系主要是thenApply、thenAccept、thenRun和thenCompose这四个系列接口。

thenApply系列函数参数fn类型接口Funtion<T,R>，与CompletionStage相关的是R apply(T t)，这个方法可以接受参数并支持返回值，所以thenApply系列方法返回CompletionStage<R>。

thenAccept系列函数参数fn类型接口Consumer<T>，与CompletionStage相关的是void accept(T t)，这个方法可以接受参数但没有返回值，所以thenApply系列方法返回CompletionStage<Void>。

thenRun系列函数参数fn类型接口Runnable，所以action既不能接受参数也没有返回值，所以thenRun系列方法返回CompletionStage<Void>。

这些方法里Async代表的是异步执行fn、consumer或者action。其中，需要特别注意的是thenCompose系列方法，这系列方法会创建出新的子流程，最终结果和thenApply系列相同。

```java
CompletionStage<R> thenApply(fn);
CompletionStage<R> thenApplyAsync(fn);
CompletionStage<Void> thenAccept(consumer);
CompletionStage<Void> thenAcceptAsync(consumer);
CompletionStage<Void> thenRun(action);
CompletionStage<Void> thenRunAsync(action);
CompletionStage<R> thenCompose(fn);
CompletionStage<R> thenComposeAsync(fn);
```

通过supplyAsync()启动一个异步流程，之后两个串行操作。虽然这是一个异步流程，但任务①②③却是串行执行的，②依赖①，③依赖②的执行结果。

```java
CompletableFuture<String> f0 = CompletableFuture.supplyAsync(() -> "Hello World")      //①
  .thenApply(s -> s + " QQ")  //②
  .thenApply(String::toUpperCase);//③

System.out.println(f0.join());
// 输出结果
HELLO WORLD QQ

```

**2、描述AND汇聚关系**

描述AND汇聚关系主要是thenCombine、thenAcceptBoth和runAfterBoth系列接口。这些接口的区别也是源自fn、consumer和action这三个核心参数不同。

```java
CompletionStage<R> thenCombine(other, fn);
CompletionStage<R> thenCombineAsync(other, fn);
CompletionStage<Void> thenAcceptBoth(other, consumer);
CompletionStage<Void> thenAcceptBothAsync(other, consumer);
CompletionStage<Void> runAfterBoth(other, action);
CompletionStage<Void> runAfterBothAsync(other, action);
```

3、描述OR汇聚关系

描述OR汇聚关系主要是applyToEither、acceptEither和runAfterEither系列接口。这些接口的区别也是源自fn、consumer和action这三个核心参数不同。

```java
CompletionStage applyToEither(other, fn);
CompletionStage applyToEitherAsync(other, fn);
CompletionStage acceptEither(other, consumer);
CompletionStage acceptEitherAsync(other, consumer);
CompletionStage runAfterEither(other, action);
CompletionStage runAfterEitherAsync(other, action);
```

```java
CompletableFuture<String> f1 = CompletableFuture.supplyAsync(()->{
    int t = getRandom(5, 10);
    sleep(t, TimeUnit.SECONDS);
    return String.valueOf(t);
});

CompletableFuture<String> f1 = CompletableFuture.supplyAsync(()->{
    int t = getRandom(5, 10);
    sleep(t, TimeUnit.SECONDS);
    return String.valueOf(t);
});

CompletableFuture<String> f3 = f1.applyToEither(f2,s -> s);

System.out.println(f3.join());

```

**4、异常处理**

虽然上面的fn、consumer和action他们的核心方法都不允许抛出可检查异常，但是却无法限制它们抛出运行时异常。

```java
CompletableFuture<Integer> f0 = CompletableFuture.
    .supplyAsync(()->(7/0))
    .thenApply(r->r*10);
System.out.println(f0.join());

```

CompletionStage提供了异常处理机制，可以串行处理异常，支持链式编程。

```java
CompletionStage exceptionally(fn);
CompletionStage<R> whenComplete(consumer);
CompletionStage<R> whenCompleteAsync(consumer);
CompletionStage<R> handle(fn);
CompletionStage<R> handleAsync(fn);
```

下面的代码展示如何使用exceptionally()方法来处理异常，exceptionally()的使用类似于try{}catch{}中的catch{}，但是由于支持链式编程方式，所以相对更简单。whenComplete()和handle()系列方法类似于try{}finally{}中的finally{}，无论是否发生异常都会执行whenComplete()中的回调函数consumer和handle()中的回调函数fn。whenComplete()和handle()的区别是，whenComplete()不支持返回结果，而handle()支持返回结果。

```java
CompletableFuture<Integer> f0 = CompletableFuture
    .supplyAsync(()->7/0))
    .thenApply(r->r*10)
    .exceptionally(e->0);
System.out.println(f0.join());

```

## 25、CompletionService ：如何批量的执行异步任务？

思考下面的询价程序，通过三次获取询价结果，并将结果保存到数据库：

```java
// 创建线程池
ExecutorService executor = Executors.newFixedThreadPool(3);
// 异步向电商 S1 询价
Future<Integer> f1 = executor.submit(
    ()->getPriceByS1());
// 异步向电商 S2 询价
Future<Integer> f2 = executor.submit(
    ()->getPriceByS2());
// 异步向电商 S3 询价
Future<Integer> f3 = executor.submit(
    ()->getPriceByS3());
    
// 获取电商 S1 报价并保存
r=f1.get();
executor.execute(()->save(r));
  
// 获取电商 S2 报价并保存
r=f2.get();
executor.execute(()->save(r));
  
// 获取电商 S3 报价并保存  
r=f3.get();
executor.execute(()->save(r));


```

这个方案本身没有太大的问题，但是如果获取S1的报价耗时很长，即便S2、S3的执行时间很短，主线程也会阻塞等待S1的报价获取完成。

可以通过**阻塞队列**的方式进行改进，让获取报价的方法都进入阻塞队列，然后在主线程中消费这个阻塞队列。这样就能实现先获取到的报价，先消费。

```java
// 创建阻塞队列
BlockingQueue<Integer> bq = new LinkedBlockingQueue<>();
// 电商 S1 报价异步进入阻塞队列  
executor.execute(()-> bq.put(f1.get()));
// 电商 S2 报价异步进入阻塞队列  
executor.execute(()-> bq.put(f2.get()));
// 电商 S3 报价异步进入阻塞队列  
executor.execute(()-> bq.put(f3.get()));
// 异步保存所有报价  
for (int i=0; i<3; i++) {
  Integer r = bq.take();
  executor.execute(()->save(r));
}  

```

在实际的项目中，可以通过CompletionService实现。ComletionService内部也是维护一个阻塞队列，当任务结束后，就把任务结果Future对象加入到阻塞队列。

ComletionService接口的实现类是ExecutorCompletionService，包含两个构造方法：

1、ExecutorCompletionService(Executor executor)。

2、ExecutorCompletionService(Executor executor，BlockingQueue<Future>  completionQueue)。

两个构造方法都需要传入线程池，如果不指定completionQueue，那么默认会使用无边界的LinkedBlockingQueue。下面代码是通过ComletionService提高性能的询价系统。

```java
// 创建线程池
ExecutorService executor = Executors.newFixedThreadPool(3);
// 创建 CompletionService
CompletionService<Integer> cs = new ExecutorCompletionService<>(executor);
// 异步向电商 S1 询价
cs.submit(()->getPriceByS1());
// 异步向电商 S2 询价
cs.submit(()->getPriceByS2());
// 异步向电商 S3 询价
cs.submit(()->getPriceByS3());
// 将询价结果异步保存到数据库
for (int i=0; i<3; i++) {
  Integer r = cs.take().get();
  executor.execute(()->save(r));
}

```

**CompletionService接口方法说明**

submit()方法有两个，用于提交任务。第二个方法有两个参数Runnable task和V result类似于ThreadPoolExecutor的<T> Future<T> submit(Runnable task, T result)。

ComletionService的其余三个方法，都是和阻塞队列有关，take()、poll()都是从阻塞队列获取并移除一个元素。它们的区别是，如果队列是空，那么调用take()方法的线程会被阻塞，而poll()方法会返回null值。poll(long timeout, TimeUnit unit)方法支持以超时的方式获取并移除阻塞队列的头部元素，如果等待了timeout unit，阻塞队列还是空，就返回null值。

```java
Future<V> submit(Callable<V> task);
Future<V> submit(Runnable task, V result);
Future<V> take() throws InterruptedException;
Future<V> poll();
Future<V> poll(long timeout, TimeUnit unit) throws InterruptedException;
```

Dubbo中有一种**Forking的集群模式，这种模式支持并行的调用多个查询服务，只要有一个成功返回结果，整个服务就可以返回。**

```java
// 创建线程池
ExecutorService executor = Executors.newFixedThreadPool(3);
// 创建 CompletionService
CompletionService<Integer> cs = new ExecutorCompletionService<>(executor);
// 用于保存 Future 对象
List<Future<Integer>> futures = new ArrayList<>(3);
// 提交异步任务，并保存 future 到 futures 
futures.add(cs.submit(()->geocoderByS1()));
futures.add(cs.submit(()->geocoderByS2()));
futures.add(cs.submit(()->geocoderByS3()));
// 获取最快返回的任务执行结果
Integer r = 0;
try {
  // 只要有一个成功返回，则 break
  for (int i = 0; i < 3; ++i) {
    r = cs.take().get();
    // 简单地通过判空来检查是否成功返回
    if (r != null) {
      break;
    }
  }
} finally {
  // 取消所有任务
  for(Future<Integer> f : futures)
    f.cancel(true);
}
// 返回结果
return r;

```

## 26、Fork/Join：单机版的MapReduce

线程池、Future、CompletableFuture和CompletionService都是帮助我们站在任务的视角来解决并发问题，而使我们不必纠缠在线程间如何协作的问题上（比如线程间如何实现等待、通知等）。**对于简单的并行任务，可以通过“线程池+Future”的方案解决；如果任务之间有耦合，无论是AND耦合还是OR耦合，都可以通过CompletableFuture来解决；而批量并行任务，可以通过CompletionService来解决。**

![1556518589855](E:\data\my-document\concurrent\assets\1556518589855.png)

上面说的简单、聚合和批量任务基本涵盖日常工作中的并发场景，但还不够全面，还有一种“分治”模型。**分治**顾名思义就是分而治之，是一种解决复杂问题的思维方法和模式。具体来讲，就是**把一个复杂的问题分解成多个相似的子问题，然后把子问题分解成更小的子问题，直到子问题简单到可以直接求解。**算法领域的分治算法（归并排序、快速排序、二分查找都是分治算法）；大数据领域的MapReduce背后也是分治思想。Fork/Join并行计算框架，就是用来支持分治模型的。

分治任务模型可分为两个阶段：

+ **任务分解：**将任务迭代的分解为子任务，直至子任务可以直接计算出结果。
+ **结果合并：**逐层合并子任务的执行结果，直至获取最终结果。

![1556519062575](E:\data\my-document\concurrent\assets\1556519062575.png)

在分治任务模型里，任务和分解后的子任务具有相似性，这种相似性往往体现在任务和子任务具有相同的算法，但计算的数据规模不同。具备这种相似性的问题，我们往往采用递归算法。

Fork/Join是一个并行计算框架，主要用来支持分治任务模型，这个计算框架里的**Fork对应分治任务模型里的任务分解，Join对应结果合并**。Fork/Join计算框架主要包含两部分，一部分是**分治任务线程池ForkJoinPool**，另一部分是**分治任务ForkJoinTask。**

ForkJoinTask是一个抽象类，它最核心的方法时fork()方法和join()方法，其中fork()方法会异步的执行一个子任务，而join()方法则会阻塞当前线程来等待子任务的执行结果。ForkJoinTask有两个子类：RecursiveAction和RecursiceTask，它们都是通过递归的方式来处理分治任务。这两个子类定义了抽象的compute()方法，区别是RecursiveAction定义的compute()方法没有返回值，RecursiveTask定义的compute()方法有返回值。在实际使用的时候，需要你自己去扩展这两个抽象类。

下面的示例是利用Fork/Join框架计算斐波那契数列。

```java
static void main(String[] args){
  // 创建分治任务线程池  
  ForkJoinPool fjp = new ForkJoinPool(4);
  // 创建分治任务
  Fibonacci fib = new Fibonacci(30);   
  // 启动分治任务  
  Integer result = fjp.invoke(fib);
  // 输出结果  
  System.out.println(result);
}
// 递归任务
static class Fibonacci extends RecursiveTask<Integer>{
  final int n;
  Fibonacci(int n){this.n = n;}
  protected Integer compute(){
    if (n <= 1)
      return n;
    Fibonacci f1 = new Fibonacci(n - 1);
    // 创建子任务  
    f1.fork();
    Fibonacci f2 = new Fibonacci(n - 2);
    // 等待子任务结果，并合并结果  
    return f2.compute() + f1.join();
  }
}

```

**ForkJoinPool工作原理**

ThreadPoolExecutor本质上是生产者-消费者模式的实现，内部有一个队列，这个队列是生产者和消费者通信的媒介；ThreadPoolExecutor可以有多个工作线程，但这些工作线程共享一个任务队列。

ForkJoinPool本质上也是一个生产者-消费者的实现，但是更加的智能。ForkJoinPool内部有多个任务队列，当我们通过ForkJoinPool的invoke()方法或者submit()方法提交任务时，ForkJoinPool根据一定的路由规则把任务提交到一个任务队列中，如果任务在执行过程中会创建出子任务，那么子任务会提交到工作线程对应的任务队列中。

如果工作线程的任务队列为空了，ForkJoinPool支持一种**任务窃取**机制，如果工作线程空闲了，它可以“窃取”其他工作线程对应的任务队列中的任务。ForkJoinPool采用双端队列，工作线程正常获取任务和“窃取任务”分别从任务不同端消费，这样就避免了不必要的数据竞争。

![1556520840504](E:\data\my-document\concurrent\assets\1556520840504.png)

**模拟MapReduce统计单词数量**

```java
static void main(String[] args){
  String[] fc = {"hello world", "hello me", "hello fork", "hello join", "fork join in world"};
  // 创建 ForkJoin 线程池    
  ForkJoinPool fjp = new ForkJoinPool(3);
  // 创建任务    
  MR mr = new MR(fc, 0, fc.length);  
  // 启动任务    
  Map<String, Long> result = fjp.invoke(mr);
  // 输出结果    
  result.forEach((k, v)-> System.out.println(k+":"+v));
}
//MR 模拟类
static class MR extends RecursiveTask<Map<String, Long>> {
  private String[] fc;
  private int start, end;
  // 构造函数
  MR(String[] fc, int fr, int to){
    this.fc = fc;
    this.start = fr;
    this.end = to;
  }
  @Override protected 
  Map<String, Long> compute(){
    if (end - start == 1) {
      return calc(fc[start]);
    } else {
      int mid = (start+end)/2;
      MR mr1 = new MR(fc, start, mid);
      mr1.fork();
      MR mr2 = new MR(fc, mid, end);
      // 计算子任务，并返回合并的结果    
      return merge(mr2.compute(), mr1.join());
    }
  }
  // 合并结果
  private Map<String, Long> merge(Map<String, Long> r1, Map<String, Long> r2) {
    Map<String, Long> result = new HashMap<>();
    result.putAll(r1);
    // 合并结果
    r2.forEach((k, v) -> {
      Long c = result.get(k);
      if (c != null)
        result.put(k, c+v);
      else 
        result.put(k, v);
    });
    return result;
  }
  // 统计单词数量
  private Map<String, Long> calc(String line) {
    Map<String, Long> result = new HashMap<>();
    // 分割单词    
    String [] words = line.split("\\s+");
    // 统计单词数量    
    for (String w : words) {
      Long v = result.get(w);
      if (v != null) 
        result.put(w, v+1);
      else
        result.put(w, 1L);
    }
    return result;
  }
}

```

Java1.8提供的Stream API里面的并行流也是以ForkJoinPool为基础的。默认情况下，所有的并行流计算都共享一个ForkJoinPool，这个共享的ForkJoinPool默认的线程数是CPU核数。如果计算中有存在有很慢的I/O计算，会拖慢整个系统的性能。**所以建议不同的ForkJoinPool执行不同类型的计算任务。**

