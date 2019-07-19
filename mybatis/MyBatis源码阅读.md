# MyBatis源码阅读

## 接口和对象介绍

### SqlSessionFactoryBuilder

~~~java
public class SqlSessionFactoryBuilder {

  public SqlSessionFactory build(Reader reader) {
    return build(reader, null, null);
  }

  public SqlSessionFactory build(Reader reader, String environment) {
    return build(reader, environment, null);
  }

  public SqlSessionFactory build(Reader reader, Properties properties) {
    return build(reader, null, properties);
  }

  public SqlSessionFactory build(Reader reader, String environment, Properties properties) {
    try {
      // XMLConfigBuilder解析xml配置文件
      // 使用建造者模式
      XMLConfigBuilder parser = new XMLConfigBuilder(reader, environment, properties);
      // parser.parse()：使用XPATH解析xml配置文件，将配置文件封装成Configuration对象
      // 返回SqlSessionFactory对象，该对象拥有Configuration对象
      return build(parser.parse());
    } catch (Exception e) {
      throw ExceptionFactory.wrapException("Error building SqlSession.", e);
    } finally {
      ErrorContext.instance().reset();
      try {
        reader.close();
      } catch (IOException e) {
        // Intentionally ignore. Prefer previous error.
      }
    }
  }

  public SqlSessionFactory build(InputStream inputStream) {
    return build(inputStream, null, null);
  }

  public SqlSessionFactory build(InputStream inputStream, String environment) {
    return build(inputStream, environment, null);
  }

  public SqlSessionFactory build(InputStream inputStream, Properties properties) {
    return build(inputStream, null, properties);
  }

  public SqlSessionFactory build(InputStream inputStream, String environment, Properties properties) {
    try {
      XMLConfigBuilder parser = new XMLConfigBuilder(inputStream, environment, properties);
      return build(parser.parse());
    } catch (Exception e) {
      throw ExceptionFactory.wrapException("Error building SqlSession.", e);
    } finally {
      ErrorContext.instance().reset();
      try {
        inputStream.close();
      } catch (IOException e) {
        // Intentionally ignore. Prefer previous error.
      }
    }
  }
  public SqlSessionFactory build(Configuration config) {
    return new DefaultSqlSessionFactory(config);
  }
}
~~~

### XMLConfigBuilder

解析全局配置文件。

### XMLMapperBuilder

解析Mapper映射文件。

### Configuration

全局配置文件，XMLConfigBuilder.parse()方法解析全局配置文件，封装成Configuration对象。最终通过SqlSessionFactoryBuilder.build(Configuration configuration)方法返回DefaultSqlSessionFactory对象，将Configuration对象作为属性设置到DefaultSqlSessionFactory中。DefaultSqlSessionFactory根据Configuration的配置信息，为每个Client生成SqlSession对象。

```java
private void parseConfiguration(XNode root) {
    try {
      //issue #117 read properties first
      propertiesElement(root.evalNode("properties"));
      Properties settings = settingsAsProperties(root.evalNode("settings"));
      loadCustomVfs(settings);
      loadCustomLogImpl(settings);
      typeAliasesElement(root.evalNode("typeAliases"));
      pluginElement(root.evalNode("plugins"));
      objectFactoryElement(root.evalNode("objectFactory"));
      objectWrapperFactoryElement(root.evalNode("objectWrapperFactory"));
      reflectorFactoryElement(root.evalNode("reflectorFactory"));
      settingsElement(settings);
      // read it after objectFactory and objectWrapperFactory issue #631
      environmentsElement(root.evalNode("environments"));
      databaseIdProviderElement(root.evalNode("databaseIdProvider"));
      typeHandlerElement(root.evalNode("typeHandlers"));
      mapperElement(root.evalNode("mappers"));
    } catch (Exception e) {
      throw new BuilderException("Error parsing SQL Mapper Configuration. Cause: " + e, e);
    }
  }
```

### SqlSource接口

负责根据用户传递的parameterObject，动态地生成SQL语句，将信息封装到BoundSql对象中，并返回BoundSql表示动态生成的SQL语句以及相应的参数信息。

![](D:\data\mybatis\assets\SqlSource.png)

DynamicSqlSource：封装动态SQL标签解析后的SQL语句和带有${}的语句。

RawSqlSource：封装带有#{}的SQL语句。

StaticSqlSource：是BoundSql中要存储SQL语句的一个载体，上面两个SqlSource的SQL语句最终都要存储StaticSqlSource中。

### SqlSessionFactory接口

默认实现类DefaultSqlSessionFactory。

```java
public interface SqlSessionFactory {

  SqlSession openSession();

  SqlSession openSession(boolean autoCommit);

  SqlSession openSession(Connection connection);

  SqlSession openSession(TransactionIsolationLevel level);

  SqlSession openSession(ExecutorType execType);

  SqlSession openSession(ExecutorType execType, boolean autoCommit);

  SqlSession openSession(ExecutorType execType, TransactionIsolationLevel level);

  SqlSession openSession(ExecutorType execType, Connection connection);

  Configuration getConfiguration();

}
```

### SqlSession接口

默认实现类DefaultSqlSession。提供各种数据库操作及获取Connection、Configuration、Mapper列表等操作

### Executor接口

![](D:\data\mybatis\assets\Executor.png)

### StatementHandler接口

![StatementHandler](D:\data\mybatis\assets\StatementHandler.png)

RoutingStatementHandler：使用静态代理模式对PreparedStatementHanlder，CallableStatementHandler，SimpleStatementHandler进行访问。

### ParameterHandler接口

为PreparedStatement设置参数。

### ResultSetHandler接口

默认实现类DefaultResultSetHandler。

```java
public interface ResultSetHandler {

  <E> List<E> handleResultSets(Statement stmt) throws SQLException;

  <E> Cursor<E> handleCursorResultSets(Statement stmt) throws SQLException;

  void handleOutputParameters(CallableStatement cs) throws SQLException;
}
```

## 源码阅读

### 加载全局配置文件流程

+ 入口：

```java
 Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
 SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
```

+ 解析流程：

SqlSessionFactoryBuilder().build(reader)：执行完成返回SqlSessionFactory对象

+ 