# Jenkins+Git安装配置

## 安装前准备

+ jdk1.8
+ maven3.6
+ git

## 一、安装Jenkins

### 1、下载

从官网上下载Jenkins的可执行war包。

![1565619820246](D:\data\document\jenkins\assets\1565619820246.png)

![1565619900154](D:\data\document\jenkins\assets\1565619900154.png)

### 2、启动Jenkins

运行java -jar jenkins.war运行jenkins可执行程序，启动后访问http://IP:PORT访问jenkins进行配置，默认端口8080。

![1565620519010](D:\data\document\jenkins\assets\1565620519010.png)

启动后会生成admin用户的初始密码，将密码保存下来，后面会用到。

### 3、初始化

初次访问jenkins会要求输入管理员密码，将刚刚生成的密码输入对话框，点击继续。

![1565620735763](D:\data\document\jenkins\assets\1565620735763.png)

下一步是新手入门，安装插件，点击选择要安装的插件或者直接选择安装推荐插件。

![1565620897272](D:\data\document\jenkins\assets\1565620897272.png)

选择安装推荐的插件后，进入插件安装等待界面，等待jenkins安装完全部插件。

![1565620951248](D:\data\document\jenkins\assets\1565620951248.png)

插件一次可能不会完全安装成功，多试几次，直到全部安装成功为止。

![1565621259748](D:\data\document\jenkins\assets\1565621259748.png)

全部安装成功以后，跳到配置管理员用户界面，配置管理员用户，输入配置信息，点击保存并完成初始化相关配置。

![1565621351269](D:\data\document\jenkins\assets\1565621351269.png)

### 4、Jenkins配置

##### 插件配置

有很多插件都是选择的默认的安装的，所以现在需要我们安装的插件不多：

+ Maven Integration plugin
+ publish over SSH
+ Gitlab Hook Plugin
+ Gitlab Plugin
+ Build Authorization Token Root Plugin

插件安装：系统管理 > 插件管理 > 可选插件,勾选需要安装的插件，点击直接安装或者下载重启后安装。

![1565621739647](D:\data\document\jenkins\assets\1565621739647.png)

##### 全局工具配置

系统管理 > 全局工具配置

**JDK配置**

配置本地 JDK 的路径，去掉勾选自动安装。

![1565622265457](D:\data\document\jenkins\assets\1565622265457.png)

**Maven配置**

配置本地maven的路径，去掉勾选自动安装。

![1565622304896](D:\data\document\jenkins\assets\1565622304896.png)

其他工具可以视情况选择配置。

### 5、配置目标服务器SSH

##### 使用密钥方式登录目标发布服务器

ssh 的配置可使用密钥，也可以使用密码，这里我们使用密钥来配置，在配置之前先配置好jenkins服务器和应用服务器的密钥认证 **Jenkins服务器**上生成密钥对，使用`ssh-keygen -t rsa`命令

输入下面命令 一直回车，一个矩形图形出现就说明成功，在~/.ssh/下会有私钥id_rsa和公钥id_rsa.pub。

```
ssh-keygen -t rsa
```

将**jenkins服务器**的公钥`id_rsa.pub`中的内容复制到**应用服务器** 的~/.ssh/下的 `authorized_keys`文件。

```
ssh-copy-id -i id_rsa.pub 192.168.0.xx
chmod 644 authorized_keys
```

在**应用服务器**上重启 ssh 服务，`service sshd restart`现在 Jenkins 服务器可免密码直接登陆应用服务器

之后在用 ssh B尝试能否免密登录 B 服务器，如果还是提示需要输入密码，则有以下原因

- a. 非 root 账户可能不支持 ssh 公钥认证（看服务器是否有限制）。
- b. 传过来的公钥文件权限不够，可以给这个文件授权下 chmod 644 authorized_keys。
- c. 使用 root 账户执行 ssh-copy-id -i ~/.ssh/id_rsa.pub 这个指令的时候如果需要输入密码则要配置sshd_config。

```
vi /etc/ssh/sshd_config
#内容
PermitRootLogin no
```

修改完后要重启 sshd 服务:

```
service sshd restart
```

最后，如果可以 SSH IP 免密登录成功说明 SSH 公钥认证成功。

**上面这种方式比较复杂，其实在 Jenkins 后台直接添加操作即可，参考下面方式**。

##### 使用用户名+密码方式登录目标发布服务器

配置通过ssh推送服务，点击添加SSH Server。

![1565622957533](D:\data\document\jenkins\assets\1565622957533.png)

点击SSH Server配置信息下方的高级按钮，配置高级信息。勾上Use password authentication,or use a different key，输入远程服务器的登录密码。

![1565623194657](D:\data\document\jenkins\assets\1565623194657.png)

配置完成后可点击“Test Configuration”测试到目标主机的连接，出现”success“则成功连接，如果有多台应用服务器，可以点击”增加“，配置多个“SSH Servers” 点击“保存”以保存配置。

![1565623378448](D:\data\document\jenkins\assets\1565623378448.png)

### 6、Push SSH

Passphrase 不用设置，Path to key 写上生成的ssh路径：`/root/.ssh/id_rsa`

下面的 SSH Servers 是重点

- Name 随意起名代表这个服务，待会要根据它来选择
- Hostname 配置应用服务器的地址
- Username 配置 linux 登陆用户名
- Remote Directory 不填

![1565623720771](D:\data\document\jenkins\assets\1565623720771.png)

> 点击下方增加可以添加多个应用服务器的地址

## 二、配置Github

## 三、Gitlab私服配置

GitLab的官方网站关于安装gitlab的介绍页面如下： https://about.gitlab.com/installation/ 

### 安装依赖的openssh-server

1. 执行： yum install curl policycoreutils openssh-server openssh-clients

2. openssh-server的功能主要是作为一个服务运行在后台，如果这个服务开启，我们就可以用一些远程连接工具来连接centos。因为minimal版本自带openssh-server，所以XShell可以连上centos

3. openssh-client的功能我觉得类似于XShell，可以作为一个客户端连接上openssh-server，但是Centos6.4的minimal版本不包括openssh-client，所以centos之前出现无法使用ssh登录的情况，centos可以使用如下命令安装客户端：

   ```shell
   # 安装wget
   yum install -y wget
   
   # 安装openssh-client
   yum install -y openssh-clients
   ```

### 安装 postfix邮箱服务

执行： yum install postfix

1. systemctl enable postfix
2. systemctl start postfix

### 打开http和ssh访问 

1. systemctl enable sshd
2. systemctl start sshd
3. firewall-cmd --permanent --add-service=http
4. systemctl reload firewalld

### 添加清华镜像下载并安装gitlab

1. /etc/yum.repos.d/gitlab-ce.repo 写入

   ```shell
   [gitlab-ce]
   name=Gitlab CE Repository
   baseurl=https://mirrors.tuna.tsinghua.edu.cn/gitlab-ce/yum/el$releasever/
   gpgcheck=0
   enabled=1
   ```

2. 执行yum makecache

3. yum install gitlab-ce

### 配置并启动gitlab 

1. 修改/etc/gitlab/gitlab.rb文件

   把external_url改成部署机器的域名或者IP地址

2. 修改/var/opt/gitlab/gitlab-rails/etc/gitlab.yml文件

   将host的值改成本机的ip地址： 172.28.255.100

3. 配置启动gitlab

   gitlab-ctl reconfigure

### gitlab使用

输入服务器IP地址并登陆，首次登陆时，需要设置密码，用户名默认为root。

**常用的gitlab命令：** 

+ 重新加载配置并启动（重新应用gitlab的配置,每次修改/etc/gitlab/gitlab.rb文件之后执行）： 
  sudo gitlab-ctl reconfigure

+ 重启gitlab 
  sudo gitlab-ctl restart

+ 查看gitlab运行状态： 
  sudo gitlab-ctl status

+ 停止gitlab服务： 
  sudo gitlab-ctl stop

+ 查看gitlab运行日志： 
  sudo gitlab-ctl tail

+ 查看 nginx 访问日志
  sudo gitlab-ctl tail nginx/gitlab_acces.log 

+ #查看 postgresql 日志
  sudo gitlab-ctl tail postgresql 

+ 停止相关数据连接服务： 
  gitlab-ctl stop unicorn
  gitlab-ctl stop sidekiq

+ 系统信息监测

  gitlab-rake gitlab:env:info   

+ 创建新项目 
  登录gitlab之后 ， 点击导航条右侧的“+” 就可以进入创建项目的页面
  按照要求填写项目名称 ， 项目可见性等信息 。

### 遇到的问题与解决

1. 默认帐户的用户名是root。提供您先前创建的密码并登录。登录后，您可以更改用户名。

2. 访问gitlab时出现502 

   + 端口被占用

   + 主机内存不足 （最好使用4G内存的主机）
   + 参考：https://blog.csdn.net/ouyang_peng/article/details/72903221

## 四、配置构建Job

### 创建测试project

新建一个Job，输入Job名称，选择Freestyle project或者Maven项目，点击OK。

![1565752298269](D:\data\document\jenkins\assets\1565752298269.png)

#### 通用配置

勾选**丢弃旧的构建**，选择是否备份被替换的旧包。我这里选择备份最近的10个。

![1565752630796](D:\data\document\jenkins\assets\1565752630796.png)

#### 源码管理

选择Git，添加jenkins用户在gitlab上的凭据(即用户名密码)，这里选择打包的分支为release分支，这里根据需求自己填写（默认为master分支）。

![1565752705794](D:\data\document\jenkins\assets\1565752705794.png)

#### 构建触发器

勾选gitlab-ci，记住后面的GitLab CI Service URL后面要填在gitlab的webhooks中。

现在有develop分支和release分支，如果不做这一步，开发只要向gitlab中提交代码（develop分支或者release分支），那么jenkins就会进行构建打包。如果需要设置判断过滤只有向release分支push代码时，才会触发构建打包。点开高级，填写根据正则过滤branch，写法如下，并generate一个token，不然后面webhooks会报403：

![1565759611793](D:\data\document\jenkins\assets\1565759611793.png)

#### Build

选择构建工具为maven3，配置好Root POM和Goals and options  ，root pom是你项目的pom.xml，goals and options是mvn的执行命令，可以填 ：install -DskipTests ，不需要填mvn xxx。

![1565762770999](D:\data\document\jenkins\assets\1565762770999.png)

#### Post-build Actions

配置构建后触发动作。

![1565764391388](D:\data\document\jenkins\assets\1565764391388.png)

+ Name：这个是从刚才在“系统设置”里配置的“Publish over SSH”中选择的 
+ Source files：当前构建下你要发送的文件 
+ Remove prefix：需要移除的前缀 
+ Remote directory：发送的远程路径（会在刚才“系统设置”中Push Server配置的“Remote directory”后追加） 
+ Exec command：发送完成后执行的命令或者脚本(这里的shell脚本能够启动项目，实现一键启动。

在git项目配置界面设置链接和token。这里要注意路径，根据部署jenkins的路径填写，不然会报404错误，并填写刚刚对应的token信息，保存：

![1565759941598](D:\data\document\jenkins\assets\1565759941598.png)

这里URL如果是本地IP服务器地址，会报错，报错信息如下：

![1565760691654](D:\data\document\jenkins\assets\1565760691654.png)

这是因为Gitlab 10.6 版本以后为了安全，不允许向本地网络发送webhook请求，如果想向本地网络发送webhook请求，则需要使用管理员帐号登录，默认管理员帐号是root，密码就是你Gitlab搭建好之后第一次输入的密码，登录之后， 点击Admin Area：

![1565760845248](D:\data\document\jenkins\assets\1565760845248.png)

即可进入Admin area，在Admin area中，在settings标签下面，找到Network->OutBound Request，勾选上Allow requests to the local network from hooks and services ，保存更改即可解决问题。

点击测试，返回200的话就表示成功了。其他错误可以根据gitlab日志来排除原因gitlab/gitlab-rails/production.log：

![1565760950001](D:\data\document\jenkins\assets\1565760950001.png)

## 五、附录

附上启动脚本。

```shell
#!/bin/bash -ilex
#export BUILD_ID=dontKillMe这一句很重要，这样指定了，项目启动之后才不会被Jenkins杀掉。
export BUILD_ID=dontKillMe
source /etc/profile
pid_path=/PID
function findJar()
{
	#遍历文件夹获取jar
	for file in `find  * -name "${1}"`
	do
	#Jenkins中编译好的jar名称 
	jar_name=${file##*/}
	#获取运行编译好的进程ID，便于我们在重新部署项目的时候先杀掉以前的进程
	pid=$(cat ${pid_path}/${jar_name}.pid) 
	#rm -f ${www_path}/${jar_name}
	#杀掉以前可能启动的项目进程 
	kill -9 ${pid} 
	#启动jar，后台启动 
	#BUILD_ID=dontKillMe
	BUILD_ID=dontKillMe nohup java -Xmx512m -Xms512m -Xmn200m -jar ${file} > ${jar_name%-*.*.*-SNAPSHOT.jar}.out 2>&1 & 
	echo $! > ${pid_path}/${jar_name}.pid 
	done
}
 
#如果PID目录不存在，则创建
if [ ! -d "${pid_path}" ]; then
  mkdir ${pid_path}
fi
if [ $# -gt 0 ] ;then
	for regx in $*
	do
	findJar $regx
	done
else
	findJar "*.jar"
fi
```

