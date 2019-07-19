# spring整合Junit

+ 解决思路：Junit是通过@RunWith注解，让我们制定一个自定义的运行器去运行单元测试代码。

+ 示例

  ```java
  @RunWith(SpringJunit4ClassRunner.class)
  @ContextConfiguration(location="classpath:spring.xml")
  //@ContextConfiguration(classes=SpringConfiguration.class)
  public class TestSpring {
      @Autowired
      private Service service;
      
      @Test
      public void test() {
          
      }
  }
  ```

# spring对JDBC的支持

spring JDBC

+ JdbcTemplate：模板类，主要通过该类实现增删改查

```java
@Test
    public void run1(){
        // 创建连接池，先使用Spring框架内置的连接池
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql:///spring");
        dataSource.setUsername("root");
        dataSource.setPassword("root");
        // 创建模板类
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        // 完成数据的添加
        jdbcTemplate.update("insert into account values (null,?,?)", "测试",10000);
    }

```

+ JdbcDaoSupport
  + 封装了JdbcTemplate
  + 继承了该类，则不需要再spring配置文件中注入JdbcTemplate类

# spring应用对事务的支持

+ spring事务：只提供多种事务管理器，做事务管理

  ![1548311900676](E:\kaikeba\wjy\assets\1548311900676.png)

+ jdbc事务

### 事务的四大特性

ACID的理解：

+ A---原子性：操作不可分割，要么都成功，要么都失败

+ C---一致性：账户A（600元）和账户B（400元）总共有1000块钱。

  账户A（600元）给账户B（400元）转账200元，最终的结果不管是成功还是失败，A和B的总额还是1000元

+ I---隔离性：由锁机制实现，会引起并发访问问题

+ D---持久性： 将结果保存到数据库文件中

### 事务并发问题

**隔离性不好会引起事务并发问题。**

- 更新丢失
- 脏读：A事务读到了B事务未提交的数据。
- 不可重复读：A事务两次读取同一行记录，显示的结果不一致。原因是两次读取期间，B事务对该记录进行了更新操作。
- 幻读：A事务两次读取同一张表，显示的结果条数不一致。原因是两次读取期间，B事务对该表进行了增加和删除操作。



SQL92标准提出了四种隔离级别：

①  Read uncommitted (读未提交)：最低级别，任何情况都无法保证。

②  Read committed (读已提交)：可避免脏读的发生。

③  Repeatable read (可重复读)：可避免脏读、不可重复读的发生。（*注意事项：MySQL在该级别的时候，就可以将幻读给解决掉*）

④  Serializable (串行化)：可避免脏读、不可重复读、幻读的发生。

MySQL数据库的默认隔离级别是**Repeatable Read**。



注意事项：

*隔离级别越高，越能保证数据的完整性和一致性，但是对并发性能的影响也越大。*

## spring事务实现

spring有两种事务实现方式：编程式事务、声明式事务

### spring声明式事务

#### XML方式

+ spring配置文件

  ```xml
  <bean id="datasource"></bean>
  <bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
  	<property name="dataSource" ref="datasource"></property>
  </bean>
  <tx:advice id="txAdvice" transaction-manager="transactionManager">
  		<tx:attributes>
  			<!-- 传播行为 -->
              <!-- 传播特性：指定两个都拥有事务的方法，发生调用时，事务的处理方式 -->
  			<tx:method name="save*" propagation="REQUIRED" />
  			<tx:method name="add*" propagation="REQUIRED" />
  			<tx:method name="insert*" propagation="REQUIRED" />
  			<tx:method name="delete*" propagation="REQUIRED" />
  			<tx:method name="del*" propagation="REQUIRED" />
  			<tx:method name="remove*" propagation="REQUIRED" />
  			<tx:method name="update*" propagation="REQUIRED" />
  			<tx:method name="modify*" propagation="REQUIRED" />
  			<tx:method name="find*" read-only="true" />
  			<tx:method name="query*" read-only="true" />
  			<tx:method name="select*" read-only="true" />
  			<tx:method name="get*" read-only="true" />
  		</tx:attributes>
  	</tx:advice>
  	
  <aop:config >
      <aop:advisor advice-ref="txAdvice" pointcut="execution(* com.wjy.service.impl.*.*(..))"/>
  </aop:config>
  ```

  

#### 混合方式

#### 纯注解方式



## Spring和Mybatis整合

## 需求

查询account表的记录

## 整合思路

需要整合的，就是项目中的对象（这些对象都要被spring管理）

- 分析有哪些对象需要被spring管理（确定是XML方式还是纯注解方式来管理）

  - 业务层
    - 实现类（多个）
    - 事务相关的对象
      - 事务管理器
      - 通知类
      - 切面类
  - 持久层（mybatis）
    - 数据源（一个）
    - SqlSessionFactory对象（一个）
    - Mapper代理对象（多个）

- 配置spring文件（分模块配置思想---便于维护）

  - 持久层---一个spring配置文件

    ```xml
    <!-- org.mybatis.spring.SqlSessionFactoryBean -->
    <!-- 可以通过该标签配置mybatis的别名、配置mybatis的插件功能等 -->
    <!-- 只要mybatis的全局配置文件能做的事情，SqlSessionFactory都可以做 -->
    <bean id="sqlSessionFactory" class="SqlSessionFactoryBean的全路径">
    	<property name="dataSource" ref="dataSource"></property>
    </bean>
    
    <!-- 该类可以动态生成mapper代理对象 -->
    <bean class="MapperScannerConfigurer的全路径">
    	<property name="basePackage" value="mapper映射文件所在的包名"></property>
    </bean>
    ```

  - 业务层

    - 业务类---一个spring配置文件
    - 事务类---一个spring配置文件

  - 将所有spring配置文件整合到一起