# Docker实战

## Docker安装

## 常用命令

拉取docker镜像

```
docker pull image_name
```

查看宿主机上的镜像，Docker镜像保存在/var/lib/docker目录下:

```
docker images
```

删除镜像

```
docker rmi  docker.io/tomcat:7.0.77-jre7   或者  docker rmi b39c68b7af30
```

查看当前有哪些容器正在运行

```
docker ps
```

查看所有容器

```
docker ps -a
```

启动、停止、重启容器命令：

```
docker start container_name/container_id
docker stop container_name/container_id
docker restart container_name/container_id
```

后台启动一个容器后，如果想进入到这个容器，可以使用attach命令：

```
docker attach container_name/container_id
```

删除容器的命令：

```
docker rm container_name/container_id
```

删除所有停止的容器：

```
docker rm $(docker ps -a -q)
```

查看当前系统Docker信息

```
docker info
```

从Docker hub上下载某个镜像:

```
docker pull centos:latest
docker pull centos:latest
```

查找Docker Hub上的nginx镜像

```
docker search nginx
```

执行docker pull centos会将Centos这个仓库下面的所有镜像下载到本地repository。

## Dockerfile示例

### 构建Nginx运行环境

```dockerfile
# 指定基础镜像
FROM sameersbn/ubuntu:14.04.20161014
# 维护者信息
MAINTAINER sameer@damagehead.com
# 设置环境
ENV RTMP_VERSION=1.1.10 \
    NPS_VERSION=1.11.33.4 \
    LIBAV_VERSION=11.8 \
    NGINX_VERSION=1.10.1 \
    NGINX_USER=www-data \
    NGINX_SITECONF_DIR=/etc/nginx/sites-enabled \
    NGINX_LOG_DIR=/var/log/nginx \
    NGINX_TEMP_DIR=/var/lib/nginx \
    NGINX_SETUP_DIR=/var/cache/nginx

# 设置构建时变量，镜像建立完成后就失效
ARG BUILD_LIBAV=false
ARG WITH_DEBUG=false
ARG WITH_PAGESPEED=true
ARG WITH_RTMP=true

# 复制本地文件到容器目录中
COPY setup/ ${NGINX_SETUP_DIR}/
RUN bash ${NGINX_SETUP_DIR}/install.sh

# 复制本地配置文件到容器目录中
COPY nginx.conf /etc/nginx/nginx.conf
COPY entrypoint.sh /sbin/entrypoint.sh

# 运行指令
RUN chmod 755 /sbin/entrypoint.sh

# 允许指定的端口
EXPOSE 80/tcp 443/tcp 1935/tcp

# 指定网站目录挂载点
VOLUME ["${NGINX_SITECONF_DIR}"]

ENTRYPOINT ["/sbin/entrypoint.sh"]
CMD ["/usr/sbin/nginx"]
```

### 构建tomcat 环境

#### Dockerfile

```dockerfile
# 指定基于的基础镜像
FROM ubuntu:13.10  
# 维护者信息
MAINTAINER zhangjiayang "zhangjiayang@sczq.com.cn"  
# 镜像的指令操作
# 获取APT更新的资源列表
RUN echo "deb http://archive.ubuntu.com/ubuntu precise main universe"> /etc/apt/sources.list
# 更新软件
RUN apt-get update  
  
# Install curl  
RUN apt-get -y install curl  
  
# Install JDK 7  
RUN cd /tmp &&  curl -L 'http://download.oracle.com/otn-pub/java/jdk/7u65-b17/jdk-7u65-linux-x64.tar.gz' -H 'Cookie: oraclelicense=accept-securebackup-cookie; gpw_e24=Dockerfile' | tar -xz  
RUN mkdir -p /usr/lib/jvm  
RUN mv /tmp/jdk1.7.0_65/ /usr/lib/jvm/java-7-oracle/  
  
# Set Oracle JDK 7 as default Java  
RUN update-alternatives --install /usr/bin/java java /usr/lib/jvm/java-7-oracle/bin/java 300     
RUN update-alternatives --install /usr/bin/javac javac /usr/lib/jvm/java-7-oracle/bin/javac 300     

# 设置系统环境
ENV JAVA_HOME /usr/lib/jvm/java-7-oracle/  
  
# Install tomcat7  
RUN cd /tmp && curl -L 'http://archive.apache.org/dist/tomcat/tomcat-7/v7.0.8/bin/apache-tomcat-7.0.8.tar.gz' | tar -xz  
RUN mv /tmp/apache-tomcat-7.0.8/ /opt/tomcat7/  
  
ENV CATALINA_HOME /opt/tomcat7  
ENV PATH $PATH:$CATALINA_HOME/bin  

# 复件tomcat7.sh到容器中的目录 
ADD tomcat7.sh /etc/init.d/tomcat7  
RUN chmod 755 /etc/init.d/tomcat7  
  
# Expose ports.  指定暴露的端口
EXPOSE 8080  
  
# Define default command.  
ENTRYPOINT service tomcat7 start && tail -f /opt/tomcat7/logs/catalina.out
```

#### tomcat7.sh命令文件

```shell
export JAVA_HOME=/usr/lib/jvm/java-7-oracle/  
export TOMCAT_HOME=/opt/tomcat7  
  
case $1 in  
start)  
  sh $TOMCAT_HOME/bin/startup.sh  
;;  
stop)  
  sh $TOMCAT_HOME/bin/shutdown.sh  
;;  
restart)  
  sh $TOMCAT_HOME/bin/shutdown.sh  
  sh $TOMCAT_HOME/bin/startup.sh  
;;  
esac  
exit 0
```

## 原则与建议

- 容器轻量化。从镜像中产生的容器应该尽量轻量化，能在足够短的时间内停止、销毁、重新生成并替换原来的容器。
- 使用 `.gitignore`。在大部分情况下，Dockerfile 会和构建所需的文件放在同一个目录中，为了提高构建的性能，应该使用 `.gitignore` 来过滤掉不需要的文件和目录。
- 为了减少镜像的大小，减少依赖，仅安装需要的软件包。
- 一个容器只做一件事。解耦复杂的应用，分成多个容器，而不是所有东西都放在一个容器内运行。如一个 Python Web 应用，可能需要 Server、DB、Cache、MQ、Log 等几个容器。一个更加极端的说法：One process per container。
- 减少镜像的图层。不要多个 Label、ENV 等标签。
- 对续行的参数按照字母表排序，特别是使用`apt-get install -y`安装包的时候。
- 使用构建缓存。如果不想使用缓存，可以在构建的时候使用参数`--no-cache=true`来强制重新生成中间镜像。

## Docker-compose

### 安装

**方法1：**

```shell
#下载
sudo curl -L https://github.com/docker/compose/releases/download/1.20.0/docker-compose-`uname -s`-`uname -m` -o /usr/local/bin/docker-compose
#安装
chmod +x /usr/local/bin/docker-compose
#查看版本
docker-compose version
```

**方法2：**

```shell
#安装pip
yum -y install epel-release
yum -y install python-pip
#确认版本
pip --version
#更新pip
pip install --upgrade pip
#安装docker-compose
pip install docker-compose 

chmod +x /usr/local/bin/docker-compose
#查看版本
docker-compose version
```

**安装补全工具(可选)**

为了方便我们输入命令，也可以安装 Docker 的补全提示工具帮忙我们快速输入命令

```shell
#安装
yum install bash-completion

#下载docker-compose脚本
curl -L https://raw.githubusercontent.com/docker/compose/$(docker-compose version --short)/contrib/completion/bash/docker-compose > /etc/bash_completion.d/docker-compose
```

### 常用命令

```shell
#查看帮助
docker-compose -h

# -f  指定使用的 Compose 模板文件，默认为 docker-compose.yml，可以多次指定。
docker-compose -f docker-compose.yml up -d 

#启动所有容器，-d 将会在后台启动并运行所有的容器
docker-compose up -d

#停用移除所有容器以及网络相关
docker-compose down

#查看服务容器的输出
docker-compose logs

#列出项目中目前的所有容器
docker-compose ps

#构建（重新构建）项目中的服务容器。服务容器一旦构建后，将会带上一个标记名，例如对于 web 项目中的一个 db 容器，可能是 web_db。可以随时在项目目录下运行 docker-compose build 来重新构建服务
docker-compose build

#拉取服务依赖的镜像
docker-compose pull

#重启项目中的服务
docker-compose restart

#删除所有（停止状态的）服务容器。推荐先执行 docker-compose stop 命令来停止容器。
docker-compose rm 

#在指定服务上执行一个命令。
docker-compose run ubuntu ping docker.com

#设置指定服务运行的容器个数。通过 service=num 的参数来设置数量
docker-compose scale web=3 db=2

#启动已经存在的服务容器。
docker-compose start

#停止已经处于运行状态的容器，但不删除它。通过 docker-compose start 可以再次启动这些容器。
docker-compose stop
```

## Docker-marchine

## Docker-swarm