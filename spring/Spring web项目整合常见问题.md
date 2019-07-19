# Spring web项目整合常见问题

### web.xml中servelet、filter、listener、context-param加载顺序

1）、加载\<context-param>,（多个context-param的加载顺序由容器自行决定，可以认为是自上而下，但有待于考证）根据配置信息，初始化上下文信息（ServletContext），因为上下文信息在整个容器中会被servlet、filter、listener的使用，所以会首先被加载；

2）、加载\<listener>,（多个listener的加载顺序由容器自行决定，可以认为是自上而下，但有待于考证）,根据上下文信息，创建监听器类的实例；

3）、加载\<filter>,（多个filter的加载顺序由容器自行决定，可以认为是自上而下，但有待于考证），对于过滤器，需要注意，与 filter 相关的一个配置节 是 filter-mapping，这里一定要注意，对于拥有相同 filter-name 的 filter 和 filter-mapping 配置 节而言，filter-mapping 必须出现在 filter 之后，否则当解析到 filter-mapping 时，它所对应的 filter- name 还未定义。web 容器启动时初始化每个 filter 时，是按照 filter 配置节出现的顺序来初始化的，当请求资源匹配多 个 filter-mapping 时，filter 拦截资源是按照 filter-mapping 配置节出现的顺序来依次调 用 doFilter() 方法的。 

4）、加载\<servlet>,对于配置多个servlet,容器会根据load- on-startup 元素在web应用启动的时候指定了servlet被加载的顺序，它的值必须是一个整数。如果它的值是一个负整数或是这个元素不存在，那么容器会在该servlet被调用的时候，加载这个servlet 。如果值是正整数或零，容器在配置的时候就加载并初始化这个servlet，容器必须保证值小的先被加载。如果值相等，容器可以自动选择先加载谁。

**总结**：**context-param>listner>filter>servlet**

### load-on-startup标签的作用，影响了servlet对象创建的时机

1)、load-on-startup元素标记容器是否在启动的时候就加载这个servlet(实例化并调用其init()方法)。

2)、它的值必须是一个整数，表示servlet应该被载入的顺序

2)、当值为0或者大于0时，表示容器在应用启动时就加载并初始化这个servlet；

3)、当值小于0或者没有指定时，则表示容器在该servlet被选择时才会去加载。

4)、正数的值越小，该servlet的优先级越高，应用启动时就越先加载。

5)、当值相同时，容器就会自己选择顺序来加载。

所以，\<load-on-startup>x\</load-on-startup>，中x的取值1，2，3，4，5代表的是优先级，而非启动延迟时间。

### url-pattern标签的配置方式有四种：/dispatcherServlet、 /servlet/*  、*  、/ ,以上四种配置，加载顺序

1. 精确匹配，servlet-mapping1：\<url-pattern>/user/users.html\</url-pattern>，servlet-mapping2：\<url-pattern>/*\</url-pattern>。当一个请求http://localhost:8080/appDemo/user/users.html来的时候，servlet-mapping1匹配到，不再用servlet-mapping2匹配
2. 路径匹配，先最长路径匹配，再最短路径匹配servlet-mapping1：\<url-pattern>/user/*\</url-pattern>，servlet-mapping2：\<url-pattern>/*\</url-pattern>。当一个请求http://localhost:8080/appDemo/user/users.html来的时候，servlet-mapping1匹配到，不再用servlet-mapping2匹配
3. 扩展名匹配，servlet-mapping1：\<url-pattern>/user/\*\</url-pattern>，servlet-mapping2：\<url-pattern>*.action\</url-pattern>。当一个请求http://localhost:8080/appDemo/user/addUser.action来的时候，servlet-mapping1匹配到，不再用servlet-mapping2匹配
4. 缺省匹配，以上都找不到servlet，就用默认的servlet，配置为\<url-pattern>/\</url-pattern>

### 为什么配置/就不拦截JSP请求，而配置/*，就会拦截JSP请求

​	url-pattern标签的配置为/\*报错，原因是它拦截了JSP请求，但是又不能处理JSP请求。

- “/*”属于路径匹配，并且可以匹配所有request，由于路径匹配的优先级仅次于精确匹配，所以“/\*”会覆盖所有的扩展名匹配，很多404错误均由此引起，所以这是一种特别恶劣的匹配模式，一般只用于filter的url-pattern
- “/”是servlet中特殊的匹配模式，且该模式有且仅有一个实例，优先级最低，不会覆盖其他任何url-pattern，只是会替换servlet容器的内建default servlet ，该模式同样会匹配所有request。
- 配置“/”后，一种可能的现象是myServlet会拦截诸如http://localhost:8080/appDemo/user/addUser.action、http://localhost:8080/appDemo/user/updateUser的格式的请求，但是并不会拦截http://localhost:8080/appDemo/user/users.jsp、http://localhost:8080/appDemo/index.jsp，这是因为servlet容器有内置的“*.jsp”匹配器，而扩展名匹配的优先级高于缺省匹配，所以才会有上述现象

### 配置了springmvc去读取spring配置文件之后，就产生了spring父子容器的问题

父容器不能访问子容器的bean，子容器可以访问父容器的bean。

配置springmvc子容器，过滤掉service、dao层的组件

```xml
<!-- use-default-filters配置，注意扫描覆盖AOP问题，如果扫描到services层组件，而事务配置
		等配置又不在该配置文件时，会覆盖AOP代码
	 -->
<context:component-scan base-package="com.wjy.controller" use-default-filters="false">
		<!-- 只扫描@Controller注解 -->
		<context:include-filter type="annotation" expression="org.springframework.stereotype.Controller"/>
</context:component-scan>
```

