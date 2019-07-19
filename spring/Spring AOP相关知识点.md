# Spring AOP相关知识点

**AOP**：

- 面向切面编程，是AOP联盟提出的编程思想，有很多实现：AspectJ， SpringAOP， Spring结合AspectJ

- 横向抽取代码，实现代码复用

  ![1548298153048](d:\data\assets\1548298153048.png)

- 纵向抽取（继承）

- ![1548298171183](d:\data\assets\1548298171183.png)

#### 原理

**Aop**的作用：在不修改业务代码的前提下，对功能进行增强（符合开闭原则）

增强方式：预编译方式和运行时动态代理方式

+ AspectJ采用预编译方式（静态织入）
+ Spring AOP采用运行时代理方式（动态织入）
  + JDK动态代理 --- 基于接口
  + CGLIB动态代理---基于继承（任何一个非final类都可以被代理）

#### AOP术语

+ 切入点：待增强的方法
+ 通知（增强）：日志、事务等功能
+ 织入：
+ 切面：切入点和通知的组合
+ 目标对象：表现层、业务层、持久层的相关代码。目标对象无须被修改
+ 代理：最终生成的代理对象。

一句话概括AOP就是：在**目标对象**中定位**切入点**，**织入**对应的**通知**，就变成**代理对象**

## 基于XML（spring整合AspectJ）

+ 目标对象（Service实现类）

+ 编写通知类（独立的类，无须继承任何类和实现任何接口）

+ spring配置文件

  ```xml
  <bean class="目标类"></bean>
  <bean id="advice" class="Advice实现类"></bean>
  <aop:config>
      <!-- 使用spring AOP实现 -->
  	<!-- <aop:advisor advice-ref="" pointcut=""></aop:advisor> -->
      
      <!-- 配置切面：切面是由通知和切入点组成 -->
      <aop:aspect ref="advice">
          <!-- method：通知类的方法名称 -->
          <!-- pointcut：切入点表达式 -->
      	<aop:before method="before" pointcut="execution(* *..*.*serviceImpl.*(..))">
      </aop:aspect>
  </aop:config>
  ```

  + 通知类型（五种）
  + 切入点表达式：execution(返回值 包名.类名.方法名(参数类型))

## 基于注解和XML混合（spring整合AspectJ）

+ 目标对象（service实现类）

+ 编写切面

  + 类上必须加**@Aspect**（标记该类是一个AOP切面类）、@Component（标记该类可以被组件扫描加载到spring容器）

  + 切面方法上需要加@Before、@AfterReturning等五个注解，对应五种通知类型

  + 可以在方法上加@PointCut注解（声明切入点表达式），该方法可以被@Before、@AfterReturning等注解使用

  + 示例

    ```java
    @Component
    @Aspect
    public class MyAspect{
        public static final String pcut = "execution(* *..*.*serviceImpl.*(..))";
        
        @Before(value="execution(* *..*.*serviceImpl.*(..))")
        public void before() {
        }
        
        @After(pcut)
        public void after() {
        }
        
        @AfterReturning("MyAspect.fn()")
        public void after() {
        }
        
        @PointCut("execution(* *..*.*serviceImpl.*(..))")
        public void fn() {
        }
    }
    ```

+ spring配置文件

  ```xml
  <context:component-scan base-package="com.kkb.spring.aop"></context:component-scan>
  <aop:aspectj-autoproxy />
  ```

## 基于纯注解（spring整合AspectJ）

以下代码主要替换的是spring配置文件

```java
@Configuration
@ComponentScan("com.kkb.spring.aop")
@EnableAspectJAutoProxy
public class SpringConfiguration {
    
}
```

