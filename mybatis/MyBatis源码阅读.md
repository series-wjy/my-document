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

![](D:\data\document\mybatis\assets\SqlSource.png)

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

![](D:\data\document\mybatis\assets\Executor.png)

### StatementHandler接口

![StatementHandler](D:\data\document\mybatis\assets\StatementHandler.png)

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

SqlSessionFactoryBuilder().build(reader)：执行完成返回SqlSessionFactory对象。

1. 初始化全局配置文件解析器，声明原始Configuration对象。

   ```java
   XMLConfigBuilder parser = new XMLConfigBuilder(reader, environment, properties);
   ```

   + 构造XPath语法解析器。

     ```java
     XPathParser parser = new XPathParser(inputStream, true, props, new XMLMapperEntityResolver());
     ```

     + 解析全局配置文件，封装为Document对象（用XPath语法解析）

       ```java
       public XPathParser(Reader reader, boolean validation, Properties variables, EntityResolver entityResolver) {
           commonConstructor(validation, variables, entityResolver);
           this.document = createDocument(new InputSource(reader));
       }
       ```

   + 初始化Configuration对象，同时初始化内置类的别名

     ```java
     private XMLConfigBuilder(XPathParser parser, String environment, Properties props) {
         super(new Configuration());
         ......
     }
     
     public Configuration() {
         typeAliasRegistry.registerAlias("JDBC", JdbcTransactionFactory.class);
         typeAliasRegistry.registerAlias("MANAGED", ManagedTransactionFactory.class);
         ......
         languageRegistry.setDefaultDriverClass(XMLLanguageDriver.class);
         languageRegistry.register(RawLanguageDriver.class);
       }
     ```

2. 调用XMLConfigBuilder.parse()解析全局配置文件内容，并将配置信息设置到Configuration对象。

   ```java
   public Configuration parse() {
       if (parsed) {
         throw new BuilderException("Each XMLConfigBuilder can only be used once.");
       }
       parsed = true;
       parseConfiguration(parser.evalNode("/configuration"));
       return configuration;
   }
   ```

   + 调用XPathParser.evalNode()方法，通过XPath解析器，解析XML配置文件，返回XNode对象。

     ```java
     public XNode evalNode(Object root, String expression) {
         Node node = (Node) evaluate(expression, root, XPathConstants.NODE);
         if (node == null) {
           return null;
         }
         return new XNode(this, node, variables);
       }
     ```

   + 调用XMLConfigBuilder.parseConfiguration(XNode)解析XNode对象信息，从全局配置文件根节点开始，设置配置信息到Configuration对象

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
           // 加载映射文件
           mapperElement(root.evalNode("mappers"));
         } catch (Exception e) {
           throw new BuilderException("Error parsing SQL Mapper Configuration. Cause: " + e, e);
         }
     }
     ```

3. 创建SqlSessionFactory接口的默认实现

   ```java
   public SqlSessionFactory build(Configuration config) {
       return new DefaultSqlSessionFactory(config);
   }
   ```

### 加载映射文件流程

- 入口XMLConfigBuilder.mapperElement(root.evalNode("mappers"));

  ```java
  // parent=<mappers><package name="tk.mybatis.simple.mapper"/></mappers>
  private void mapperElement(XNode parent) throws Exception {
      if (parent != null) {
        // 获取<mappers>标签的子标签
        for (XNode child : parent.getChildren()) {
          // <package>标签解析
          if ("package".equals(child.getName())) {
            String mapperPackage = child.getStringAttribute("name");
            // 通过注解解析mapper配置
            configuration.addMappers(mapperPackage);
          } else {
            // <mapper>标签解析
            String resource = child.getStringAttribute("resource");
            String url = child.getStringAttribute("url");
            String mapperClass = child.getStringAttribute("class");
            if (resource != null && url == null && mapperClass == null) {
              ErrorContext.instance().resource(resource);
              InputStream inputStream = Resources.getResourceAsStream(resource);
              XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, resource, configuration.getSqlFragments());
               // 解析mapper映射文件
              mapperParser.parse();
            } else if (resource == null && url != null && mapperClass == null) {
              ErrorContext.instance().resource(url);
              InputStream inputStream = Resources.getUrlAsStream(url);
              XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, url, configuration.getSqlFragments());
              mapperParser.parse();
            } else if (resource == null && url == null && mapperClass != null) {
              // mapper接口解析
              Class<?> mapperInterface = Resources.classForName(mapperClass);
              configuration.addMapper(mapperInterface);
            } else {
              throw new BuilderException("A mapper element may only specify a url, resource or class, but not more than one.");
            }
          }
        }
      }
    }
  ```

- 解析流程

  解析mapper配置分为两大类，一类是通过\<package>标签配置的注解解析，一类是通过\<mapper [class|url|class]>标签配置的单个类或mapper.xml配置文件解析。

1. 初始化mapper文件解析器XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, resource, configuration.getSqlFragments());

   + 构造XPath语法解析器。

     ```java
     XPathParser parser = new XPathParser(inputStream, true, configuration.getVariables(), new XMLMapperEntityResolver());
     ```
   
     + 解析全局配置文件，封装为Document对象（用XPath语法解析）。
   
       ```java
       public XPathParser(InputStream inputStream, boolean validation, Properties variables, EntityResolver entityResolver) {  commonConstructor(validation, variables, entityResolver);  this.document = createDocument(new InputSource(inputStream));}
       ```
   
   + 初始化MapperBuilderAssistant属性对象，用于构建MappedStatement对象。
   
     ```java
     private XMLMapperBuilder(XPathParser parser, Configuration configuration, String resource, Map<String, XNode> sqlFragments) {
         super(configuration);
         this.builderAssistant = new MapperBuilderAssistant(configuration, resource);
         this.parser = parser;
         this.sqlFragments = sqlFragments;
         this.resource = resource;
     }
     ```
   
2. 调用XMLMapperBuilder.parse()方法，解析mapper配置文件。

   + 调用XMLMapperBuilder.configurationElement(XNode context)方法，解析mapper配置文件内容。

     ```java
     public void parse() {
         if (!configuration.isResourceLoaded(resource)) {
           configurationElement(parser.evalNode("/mapper"));
           configuration.addLoadedResource(resource);
           bindMapperForNamespace();
         }
     
         parsePendingResultMaps();
         parsePendingCacheRefs();
         parsePendingStatements();
       }
     ```

     + 调用XMLMapperBuilder.buildStatementFromContext(List\<XNode> list)用于构造MappedStatement对象。

       ```java
       private void configurationElement(XNode context) {
           try {
             String namespace = context.getStringAttribute("namespace");
             if (namespace == null || namespace.equals("")) {
               throw new BuilderException("Mapper's namespace cannot be empty");
             }
             builderAssistant.setCurrentNamespace(namespace);
             cacheRefElement(context.evalNode("cache-ref"));
             cacheElement(context.evalNode("cache"));
             parameterMapElement(context.evalNodes("/mapper/parameterMap"));
             resultMapElements(context.evalNodes("/mapper/resultMap"));
             sqlElement(context.evalNodes("/mapper/sql"));
             buildStatementFromContext(context.evalNodes("select|insert|update|delete"));
           } catch (Exception e) {
             throw new BuilderException("Error parsing Mapper XML. The XML location is '" + resource + "'. Cause: " + e, e);
           }
         }
       ```

       + 循环mapper配置SQL语句节点信息，初始化为XMLStatementBuilder对象，用于创建对应的MappedStatement对象。

         ```java
         private void buildStatementFromContext(List<XNode> list, String requiredDatabaseId) {
             for (XNode context : list) {
               final XMLStatementBuilder statementParser = new XMLStatementBuilder(configuration, builderAssistant, context, requiredDatabaseId);
               try {
                 statementParser.parseStatementNode();
               } catch (IncompleteElementException e) {
                 configuration.addIncompleteStatement(statementParser);
               }
             }
         }
         ```

         + 调用XMLStatementBuilder.parseStatementNode()生成MappedStatement对象。

           ```java
           public void parseStatementNode() {
               .......
               builderAssistant.addMappedStatement(id, sqlSource, statementType, sqlCommandType,
                       fetchSize, timeout, parameterMap, parameterTypeClass, resultMap, resultTypeClass,
                       resultSetTypeEnum, flushCache, useCache, resultOrdered,
                       keyGenerator, keyProperty, keyColumn, databaseId, langDriver, resultSets);
           }
           ```

           

           + 调用MapperBuilderAssistant.addMappedStatement(...)方法生成MappedStatement对象，并将生成的MappedStatement对象设置到Configuration对象中。

             ```java
             public MappedStatement addMappedStatement(...) {
                 ......
                 MappedStatement.Builder statementBuilder = new MappedStatement.Builder(configuration, id, sqlSource, sqlCommandType)
                     .resource(resource)
                     .fetchSize(fetchSize)
                     .timeout(timeout)
                     .statementType(statementType)
                     .keyGenerator(keyGenerator)
                     .keyProperty(keyProperty)
                     .keyColumn(keyColumn)
                     .databaseId(databaseId)
                     .lang(lang)
                     .resultOrdered(resultOrdered)
                     .resultSets(resultSets)
                     .resultMaps(getStatementResultMaps(resultMap, resultType, id))
                     .resultSetType(resultSetType)
                     .flushCacheRequired(valueOrDefault(flushCache, !isSelect))
                     .useCache(valueOrDefault(useCache, isSelect))
                     .cache(currentCache);
             
                 ParameterMap statementParameterMap = getStatementParameterMap(parameterMap, parameterType, id);
                 if (statementParameterMap != null) {
                   statementBuilder.parameterMap(statementParameterMap);
                 }
             
                 MappedStatement statement = statementBuilder.build();
                 configuration.addMappedStatement(statement);
             }
             ```

### SQL解析流程

+ 入口

  XMLLanguageDriver.createSqlSource(configuration, context, parameterTypeClass);

+ 解析流程

  