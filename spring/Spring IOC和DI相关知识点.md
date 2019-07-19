# Spring IOC和DI相关知识点

**spring容器**其实指的就是IOC容器，IOC容器就是BeanFactory工厂（DefaultListableFactory）,BeanFactory有个子接口**ApplicationContext**（应用上下文接口）

**IOC**：控制翻转，创建bean的角色发生了转变，反转为容器创建。

**DI**：基于IOC，在bean的创建的过程中，需要注入属性，依赖关系。

## Spring入口

spring入口指的是如何启动spring容器

+ 基于XML

  + java应用

    ``` java
    ApplicationContext ctx = new ClasspathXmlApplicationContext("spring.xml");
    ```

  + web应用

    **web.xml**

    ```xml
    <context-param>
    	<param-name>contextConfigLocation</param-name>
        <param-value>classpath:spring.xml</param-value>
    </context-param>
    <listener>
    	<listener-class>
        	org.springframework.web.context.ContextLoaderListener
        </listener-class>
    </listener>
    ```

    ContextLoaderListener监听器中，会去调用getWebApplicationContext()->AbstractApplicationContext()

+ 基于注解

  + java应用

    ```java
    ApplicationContext ctx = new AnnotationConfigApplicationContext(@Configuration配置类);
    ```

  + web应用

    **web.xml**

    ```xml
    <context-param>
    	<param-name>contextConfigLocation</param-name>
        <param-value>classpath:spring.xml</param-value>
    </context-param>
    <listener>
    	<listener-class>
        	org.springframework.web.context.ContextLoaderListener
        </listener-class>
    </listener>
    ```

    ContextLoaderListener监听器中，会去调用getWebApplicationContext()->AbstractApplicationContext()

    getWebApplication得到的默认实现类是：AnnotationConfigWebApplicationContext

## IOC容器

### 基于XML

* 代码--面向接口开发
* spring xml配置文件
  + IOC---bean标签
    + id 
    + init-method 初始化方法（执行时机）
    + destroy-method 它的值就是一个方法名称
      + 数据库连接池配置的时候，必须配置它，具体配置看bean的class类中定义的销毁方法是什么
  + DI---bean标签的property子标签和constractor-arg子标签
    + ref
    + value

### 基于注解和XML混合

+ java代码
  + IOC注解：@Component、@Controller、@Service、@Repository
  + DI注解：第一步在IOC容器中查找指定的依赖；第二步：属性注入
    + @Value(注入基本类型和String类型)
      + ${}
      + context:property-placeholder：首先加载指定的properties文件，然后将读取到的key/value数据，去替换spring上下文中出现的属性占位符**${}**
    + @Autowired（byType）
      + byName---需要配合@Qualifier
    + @Resource（默认byName【bean的id或者name】、byType），它是由java提供的注解
    + @Inject（默认byType）
      + byName---配合@Name
+ spring xml配置
  + context:component-scan
    + 开启@Autowired等几个注解的功能(BeanFactoryPostProcessor)
      + 专门开启@Autowired注解的配置**context:annotation-driven**
    + 扫描该应用上下文中指定包下面的IOC注解，交给spring容器进行管理
  + dataSource这种第三方的对应的Bean标签

### 基于纯注解

+ 零配置：没有spring.xml配置文件
+ java代码
  + @Configuration：替代XML配置文件
  + @ComponentScan：替代context:component-scan标签
  + @Bean：替代<bean>标签
  + @PropertySource：主要替代context:property-placeholder标签
  + @Import：替代<import>标签，引入其他@Configuration配置类

### 基于xml和注解的优缺点

#### 注解的优势 

配置简单，维护方便

#### xml的优势

修改时不用改源码重新编译和部署