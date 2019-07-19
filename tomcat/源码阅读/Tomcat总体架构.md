# Tomcat总体架构

## 一、Tomcat顶层架构

![1557123624769](D:\data\tomcat\源码阅读\assets\1557123624769.png)

从图上可以看出，Tomcat最顶层的容器是Server，代表整个服务器。Server包含至少一个Service，可以对外提供不同的服务。

Service包含两个核心组件：Connector和Container。

```
Connector:用于处理连接相关的事情，并提供Socket与Request和Response相关的转化; 
Container:用于封装和管理Servlet，以及具体处理Request请求；
```

一个Tomcat只有一个Server，一个Server可以有多个Service，一个Service对应一个Container，一个Service包含多个Connector。这就是因为一个服务可以有多个连接，比如http和https连接，也可以提供相同协议不同端口的连接，示意图如下：

![1557124315952](D:\data\tomcat\源码阅读\assets\1557124315952.png)

多个Connector和一个Container就组成一个Service，有了Service就可以对外提供服务。Server对这些组件提供生命周期管理，整个Tomcat的生命周期由Server控制。

上述的父子关系，通过Tomcat的配置文件也可以看出来，如下图：

![1557124707392](D:\data\tomcat\源码阅读\assets\1557124707392.png)

上面的配置文件，通过下面的结构图可以更清楚的理解：

![1557125803596](D:\data\tomcat\源码阅读\assets\1557125803596.png)

Server标签的端口号设置为8080，shutdow=“SHUTDOWN”表示8005端口监听“SHUTDOWN”命令，如果接收到“SHUTDOWN”命令就关闭Server，也就是关闭Tomcat。一个Server有多个Service，Service的左边都是Container的管理的组件，Service管理多个Connector。

**Tomcat顶层架构总结**

1. Tomcat中只有一个Server，一个Server可以有多个Service，一个Service可以有多个Connector和一个Container； 
2. Server掌管着整个Tomcat的生死大权； 
3. Service 是对外提供服务的； 
4. Connector用于接受请求并将请求封装成Request和Response来具体处理； 
5. Container用于封装和管理Servlet，以及具体处理request请求；

知道了整个Tomcat顶层的分层架构和各个组件之间的关系以及作用，对于绝大多数的开发人员来说Server和Service是比较抽象的概念，而我们开发中绝大部分进行配置的内容是属于Connector和Container的，接下来介绍一下Connector和Container。

## 二、Server

Server是Tomcat最顶层的容器，代表着整个服务器，即一个Tomcat只有一个Server，Server中包含至少一个Service组件，用于提供具体服务。这个在配置文件中也得到很好的体现（port=“8005” shutdown="SHUTDOWN"是在8005端口监听到"SHUTDOWN"命令，服务器就会停止）。

Tomcat中其标准实现是：org.apache.catalina.core.StandardServer类，其继承结构类图如下：

![1557129994079](D:\data\tomcat\源码阅读\assets\1557129994079.png)

StandardServer实现Server很好理解，Tomcat为所有的组件都提供了生命周期管理，继承LifecycleMBeanBase则跟Tomcat中的生命周期机制有关，后续文章会有介绍。

## 三、Service

可以想象，一个Server服务器，它最基本的功能肯定是：

```
接收客户端的请求，然后解析请求，完成相应的业务逻辑，然后把处理后的结果返回给客户端，一般会提供两个节本方法，一个start打开服务Socket连接，监听服务端口，一个stop停止服务释放网络资源。
```

这时的服务器就是一个Server类：

![1557130124113](D:\data\tomcat\源码阅读\assets\1557130124113.png)

如果将请求监听和请求处理放在一起，扩展性会变差，毕竟网络协议不止HTTP一种，如果想适配多种网络协议，请求处理又相同，这时就无能为力了，Tomcat的设计大师不会采取这种做法，而是将请求监听和请求处理分开为两个模块，分别是Connector和Container，Connector负责处理请求监听，Container负责处理请求处理。

但显然Tomcat可以有多个Connector，同时Container也可以有多个。那这就存在一个问题，哪个Connector对应哪个Container，提供复杂的映射吗？相信看过server.xml文件的人已经知道了Tomcat是怎么处理的了。

没错，Service就是这样来的。在conf/server.xml文件中，可以看到Service组件包含了Connector组件和Engine组件（前面有提过，Engine就是一种容器），即Service相当于Connector和Engine组件的包装器，将一个或者多个Connector和一个Engine建立关联关系。在默认的配置文件中，定义了一个叫Catalina 的服务，它将HTTP/1.1和AJP/1.3这两个Connector与一个名为Catalina 的Engine关联起来。

一个Server可以包含多个Service（它们相互独立，只是公用一个JVM及类库），一个Service负责维护多个Connector和一个Container。

其标准实现是StandardService，UML类图如下：

![1557130610530](D:\data\tomcat\源码阅读\assets\1557130610530.png)

这时Tomcat就是这样了：

![1557130630530](D:\data\tomcat\源码阅读\assets\1557130630530.png)



## 四、Connector和Container的关系

通过顶层架构的分析，可以大致理清Tomcat是如何处理一个请求的。Tomcat接收到一个请求，首先经过Service以后交给Conector处理。Connector接收请求并将请求封装成Request和Response，封装完成以后交给Container处理，处理完以后再交回给Connector，最后由Connector通过Socket将响应结果返回给客户端。这就是完整的请求处理过程。

Connector底层使用Socket来处理连接，Request和Response按照Http协议来封装，所以Connector需要同时支持Tcp/Ip协议和Http协议。

## 五、Connector架构分析

Connector用于接受请求并将请求封装成Request和Response，然后交给Container进行处理，Container处理完之后在交给Connector返回给客户端。

一个Connector会监听一个独立的端口来处理来自客户端的请求。server.xml默认配置了两个Connector：

+ **\<Connector port="8080" protocol="HTTP/1.1" connectionTimeout="20000" redirectPort="8443"/>**，它监听端口8080,这个端口值可以修改，connectionTimeout定义了连接超时时间，单位是毫秒，redirectPort 定义了ssl的重定向接口，根据上述配置，Connector会将ssl请求转发到8443端口。
+ **\<Connector port="8009" protocol="AJP/1.3" redirectPort="8443" />**, AJP表示Apache Jserv Protocol，它将处理Tomcat和Apache http服务器之间的交互，此连接器用于处理我们将Tomcat和Apache http服务器结合使用的情况，如在同一台物理Server上部署一个Apache http服务器和多台Tomcat服务器，通过Apache服务器来处理静态资源以及负载均衡时，针对不同的Tomcat实例需要AJP监听不同的端口。

我们可以把Connector分为四个方面进行理解：

1. Connector如何接受请求的？ 
2. 如何将请求封装成Request和Response的？ 
3. 封装完之后的Request和Response如何交给Container进行处理的？ 
4. Container处理完之后如何交给Connector并返回给客户端的？

Connector结构图如下：

![1557126711582](D:\data\tomcat\源码阅读\assets\1557126711582.png)

Connector就是使用ProtocolHandler来处理请求的，不同的ProtocolHandler代表不同的连接类型，比如：Http11Protocol使用的是普通Socket来连接的，Http11NioProtocol使用的是NioSocket来连接的。

其中ProtocolHandler由包含了三个部件：Endpoint、Processor、Adapter。

（1）Endpoint用来处理底层Socket的网络连接，Processor用于将Endpoint接收到的Socket封装成Request，Adapter用于将Request交给Container进行具体的处理。

（2）Endpoint由于是处理底层的Socket网络连接，因此Endpoint是用来实现TCP/IP协议的，而Processor用来实现HTTP协议的，Adapter将请求适配到Servlet容器进行具体的处理。

（3）Endpoint的抽象实现AbstractEndpoint里面定义的Acceptor和AsyncTimeout两个内部类和一个Handler接口。Acceptor用于监听请求，AsyncTimeout用于检查异步Request的超时，Handler用于处理接收到的Socket，在内部调用Processor进行处理。

至此，我们应该很轻松的回答（1）（2）（3）的问题了，但是（4）还是不知道，那么我们就来看一下Container是如何进行处理的以及处理完之后是如何将处理完的结果返回给Connector的？

## 六、Container架构分析

Container用于封装和管理Servlet，处理Request请求。Container包含4个子容器，结构如下：

![1557127494720](D:\data\tomcat\源码阅读\assets\1557127494720.png)

4个子容器的作用分别是：

- Engine：引擎，用来管理多个站点，一个Service最多只能有一个Engine；
- Host：代表一个站点，也可以叫虚拟主机，通过配置Host就可以添加站点； 
- Context：代表一个应用程序，对应着平时开发的一套程序，或者一个WEB-INF目录以及下面的web.xml文件； 
- Wrapper：每一Wrapper封装着一个Servlet。

下面是Tomcat目录对照：

![1557127956114](D:\data\tomcat\源码阅读\assets\1557127956114.png)

Context和Host的区别是Context表示一个应用，我们的Tomcat中默认的配置webapps下的每一个文件夹目录都是一个Context。其中ROOT目录中存放着主应用，其他目录存放着子应用，而整个webapps就是一个Host站点。

我们访问应用Context的时候，如果是ROOT下的则直接使用域名就可以访问，例如：www.ledouit.com。如果是Host（webapps）下的其他应用，则可以使用[http://www.ledouit.com/docs](https://link.zhihu.com/?target=http%3A//www.ledouit.com/docs)进行访问。当然默认指定的根应用（ROOT）是可以进行设定的，只不过Host站点下默认的主营用是ROOT目录下的。

### Engine

一个Service中有多个Connector和一个Engine，Engine表示整个Servlet引擎，一个Engine下面可以包含一个或者多个Host，即一个Tomcat实例可以配置多个虚拟主机，默认的情况下 conf/server.xml 配置文件中**\<Engine name="Catalina" defaultHost="localhost">** 定义了一个名为Catalina的Engine。

Engine的UML类图如下：

![1557130728840](D:\data\tomcat\源码阅读\assets\1557130728840.png)

### Host

Host，代表一个站点，也可以叫虚拟主机，一个Host可以配置多个Context，在server.xml文件中的默认配置为**\<Host name="localhost" appBase="webapps" unpackWARs="true" autoDeploy="true">**, 其中appBase=webapps， 也就是<CATALINA_HOME>\webapps目录，unpackingWARS=true 属性指定在appBase指定的目录中的war包都自动的解压，autoDeploy=true 属性指定对加入到appBase目录的war包进行自动的部署。

一个Engine包含多个Host的设计，使得一个服务器实例可以承担多个域名的服务，是很灵活的设计。

其标准实现继承图如下：

![1557130774896](D:\data\tomcat\源码阅读\assets\1557130774896.png)

### Context

Context，代表一个应用程序，就是日常开发中的web程序，或者一个WEB-INF目录以及下面的web.xml文件，换句话说每一个运行的webapp最终都是以Context的形式存在，每个Context都有一个根路径和请求路径；与Host的区别是Context代表一个应用，如，默认配置下webapps下的每个目录都是一个应用，其中ROOT目录中存放主应用，其他目录存放别的子应用，而整个webapps是一个站点。

在Tomcat中通常采用如下方式创建一个Context：

1. 在<CATALINA_HOME>\webapps 目录中创建一个目录dirname，此时将自动创建一个context，默认context的访问url为http://host:port/dirname，也可以通过在ContextRoot\META-INF 中创建一个context.xml文件，其中指定应用的访问路径。
2. 在server.xml文件中增加context 元素，如下：**\<Context path="/urlpath" docBase="/test/xxx" reloadable=true />**这样就可以通过http://host:port/urlpath访问上面配置的应用。

其标准实现类图如下：

![1557130943717](D:\data\tomcat\源码阅读\assets\1557130943717.png)

### Wrapper

一个Context可以包含多个Servlet处理不同请求，当然现在的SpringMVC，struts框架的出现导致程序中不再是大量的Servlet，但其实本质是没变的，都是由Servlet来处理或者当作入口。

在Tomcat中Servlet被称为wrapper，其标准类图如下：

![1557130970159](D:\data\tomcat\源码阅读\assets\1557130970159.png)

那么为什么要用Wrapper来表示Servlet？这和Tomcat的处理机制有关，为了更加灵活，便于扩展，Tomcat是用管道（pipeline）和阀(valve)的形式来处理请求，所以将Servlet丢给Wrapper。这个后续再分析。

那么现在Tomcat就是这样的：

![1557131001724](D:\data\tomcat\源码阅读\assets\1557131001724.png)

## 七、Container如何处理请求

Container处理请求是使用Pipeline-Valve管道来处理的（Valve是阀门的意思）。

Pipeline-Valve是责任链模式，责任链模式是指在一个请求处理的过程中有很多处理者依次对请求进行处理，每个处理者处理自己负责的数据。处理完以后返回处理结果，下一个处理者继续处理。

![1557128249213](D:\data\tomcat\源码阅读\assets\1557128249213.png)

Pipeline-Valve责任链模式和普通的责任链模式稍有不同，主要区别是：

（1）每个Pipeline都有特定的Valve，而且是在管道的最后一个执行，这个Valve叫做BaseValve，BaseValve是不可删除的；

（2）在上层容器的管道的BaseValve中会调用下层容器的管道。

我们知道Container包含四个子容器，而这四个子容器对应的BaseValve分别在：**StandardEngineValve、StandardHostValve、StandardContextValve、StandardWrapperValve。**

Pipeline的处理流程图如下：

![1557128359748](D:\data\tomcat\源码阅读\assets\1557128359748.png)

（1）Connector在接收到请求后会首先调用最顶层容器的Pipeline来处理，这里的最顶层容器的Pipeline就是EnginePipeline（Engine的管道）；

（2）在Engine的管道中依次会执行EngineValve1、EngineValve2等等，最后会执行StandardEngineValve，在StandardEngineValve中会调用Host管道HostPipeline，然后再依次执行Host的HostValve1、HostValve2等，最后在执行StandardHostValve，然后再依次调用Context的管道ContextPipeline和Wrapper的管道WrapperPipeline，最后执行到StandardWrapperValve。

（3）当执行到StandardWrapperValve的时候，会在StandardWrapperValve中创建FilterChain，并调用其doFilter方法来处理请求，**这个FilterChain包含着我们配置的与请求相匹配的Filter和Servlet**，其doFilter方法会依次调用所有的Filter的doFilter方法和Servlet的service方法，这样请求就得到了处理。

（4）当所有的Pipeline-Valve都执行完之后，并且处理完了具体的请求，这个时候就可以将返回的结果交给Connector了，Connector再通过Socket的方式将结果返回给客户端。

## 八、Tomcat的启动流程

Tomcat的启动流程很标准化，入口是BootStrap，统一按照生命周期管理接口Lifecycle的定义进行启动。首先，调用init()方法逐级初始化，接着调用start()方法进行启动，同时，每次调用伴随着生命周期状态变更事件的触发。

每一级组件除完成自身的处理外，还有负责调用子组件的相关调用，组件和组件之间是松耦合的，可以通过配置进行修改。

大致流程图如下：

![1557131147981](D:\data\tomcat\源码阅读\assets\1557131147981.png)

## 总结

至此，我们已经对Tomcat的整体架构有了大致的了解，从上面的几幅图可以看出来每一个组件的基本要素和作用。我们在脑海里应该有一个大概的轮廓了！