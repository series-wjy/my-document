# Jenkins+Github安装配置

## 安装前准备

+ jdk1.8
+ maven3.6
+ git

## 安装Jenkins

### 1、下载

从官网上下载Jenkins的可执行war包。

![1565619820246](E:\data\my-document\jenkins\assets\1565619820246.png)

![1565619900154](E:\data\my-document\jenkins\assets\1565619900154.png)

### 2、启动Jenkins

运行java -jar jenkins.war运行jenkins可执行程序，启动后访问http://IP:PORT访问jenkins进行配置，默认端口8080。

![1565620519010](E:\data\my-document\jenkins\assets\1565620519010.png)

启动后会生成admin用户的初始密码，将密码保存下来，后面会用到。

### 3、初始化

初次访问jenkins会要求输入管理员密码，将刚刚生成的密码输入对话框，点击继续。

![1565620735763](E:\data\my-document\jenkins\assets\1565620735763.png)

下一步是新手入门，安装插件，点击选择要安装的插件或者直接选择安装推荐插件。

![1565620897272](E:\data\my-document\jenkins\assets\1565620897272.png)

选择安装推荐的插件后，进入插件安装等待界面，等待jenkins安装完全部插件。

![1565620951248](E:\data\my-document\jenkins\assets\1565620951248.png)

插件一次可能不会完全安装成功，多试几次，直到全部安装成功为止。

![1565621259748](E:\data\my-document\jenkins\assets\1565621259748.png)

全部安装成功以后，跳到配置管理员用户界面，配置管理员用户，输入配置信息，点击保存并完成初始化相关配置。

![1565621351269](E:\data\my-document\jenkins\assets\1565621351269.png)

### 4、Jenkins配置

#### 插件配置

有很多插件都是选择的默认的安装的，所以现在需要我们安装的插件不多， Maven Integration plugin和publish over SSH。

插件安装：系统管理 > 插件管理 > 可选插件,勾选需要安装的插件，点击直接安装或者下载重启后安装。

![1565621739647](E:\data\my-document\jenkins\assets\1565621739647.png)

#### 全局工具配置

系统管理 > 全局工具配置

**JDK配置**

配置本地 JDK 的路径，去掉勾选自动安装。

![1565622265457](E:\data\my-document\jenkins\assets\1565622265457.png)

**Maven配置**

配置本地maven的路径，去掉勾选自动安装。

![1565622304896](E:\data\my-document\jenkins\assets\1565622304896.png)

其他工具可以视情况选择配置。

### 5、配置目标服务器SSH

#### 使用密钥方式登录目标发布服务器

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

#### 使用用户名+密码方式登录目标发布服务器

配置通过ssh推送服务，点击添加SSH Server。

![1565622957533](E:\data\my-document\jenkins\assets\1565622957533.png)

点击SSH Server配置信息下方的高级按钮，配置高级信息。勾上Use password authentication,or use a different key，输入远程服务器的登录密码。

![1565623194657](E:\data\my-document\jenkins\assets\1565623194657.png)

配置完成后可点击“Test Configuration”测试到目标主机的连接，出现”success“则成功连接，如果有多台应用服务器，可以点击”增加“，配置多个“SSH Servers” 点击“保存”以保存配置。

![1565623378448](E:\data\my-document\jenkins\assets\1565623378448.png)

#### Push SSH

Passphrase 不用设置，Path to key 写上生成的ssh路径：`/root/.ssh/id_rsa`

下面的 SSH Servers 是重点

- Name 随意起名代表这个服务，待会要根据它来选择
- Hostname 配置应用服务器的地址
- Username 配置 linux 登陆用户名
- Remote Directory 不填

![1565623720771](E:\data\my-document\jenkins\assets\1565623720771.png)

> 点击下方增加可以添加多个应用服务器的地址

## 配置Github

## Gitlab私服配置

## 配置Job

