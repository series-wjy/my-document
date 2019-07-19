## tomcat目录结构

tomcat的下载安装有很多教程，不再赘述。

现在的tomcat已经到9了，当tomcat下载安装完成后，其目录大致如下：

![1557128775291](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\1557128775291.png)

除了上面的文件夹，还有四个文件：

![1557128782776](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\1557128782776.png)

先介绍后4个文件：

LICENSE：就是许可证，里面记录了tomcat的一些条款等等。
NOTICE：里面记录了tomcat的新的通知，公告。
RELEASE-NOTES：里面记录的是发行版本的说明，一些捆绑的API，新特性等等。
RUNNING.txt：记录了tomcat的运行环境以及怎样配置参数，变量，启动等等。
再介绍文件夹（并不全）：

### bin文件夹

bin文件夹下面放的是可执行性文件，其中:bat/exe文件是Windows下可执行的脚本文件。sh文件时Linux/Unix下可执行的脚本文件。

bootstrap.jar：这个jar包是引导程序jar包，是tomcat的入口。
catalina.bat：一个重要脚本，这个脚本完成了很多基本操作，如启动、关闭等，catalina.bat都参与其中，Windows中运行。
catalina.sh：文件作用同catalina.bat，在Linux/Unix系统下运行。
catalina-tasks.xml：配置文件，主要是引入各种jar包。
configtest.bat：检测语法是否正确的脚本文件。
configtest.sh：同上。
service.bat：启动tomcat服务。和注册tomcat服务那块有关系。
setclasspath.bat：设置classpath的脚本，在catalin.bat脚本中调用，可以设置java_home,jre_home等。
shutdown.bat：主要是检查catalina.bat执行所需环境，并调用catalina.bat批处理文件关闭tomcat服务。
startup.bat：主要是检查catalina.bat执行所需环境，并调用catalina.bat 批处理文件启动tomcat服务。
tcnative-1.dll：加速器组件，可以提高性能。
tomcat-native.tar.gz：里面放的是tomcat本地的library。
tool-wrapper.bat：工具包装脚本。
version.bat：一般是用来判断系统版本获取系统版本信息等。

### conf文件夹

存放tomcat服务器的配置文件。

catalina.policy：当Tomcat在安全模式下运行，此文件为默认的安全策略配置。
catalina.properties：catalina环境变量配置。
context.xml：用于定义所有Web应用均需要加载的Context配置，如果Web应用指定了自己的context.xml，那么该文件的配置会被覆盖。
logging.properties：日志配置文件，可修改日志级别和日志路径等。
server.xml：核心配置文件，用于配置链接器、监听端口、处理请求的虚拟主机等，可以说，tomcat根据该配置文件创建服务器实例。
tomcat-users.xml：tomcat配置用户的文件，用于定义tomcat默认用户及角色映射信息，tomcat的manage模块使用该文件中定义的用户进行安全认证。

web.xml：tomcat中所有应用默认的部署描述文件，主要定义了基础Servlet和MIME映射，如果应用中不包含web.xml，tomcat将使用此文件初始化部署描述，反之，tomcat会将默认部署描述与自定义配置进行合并。