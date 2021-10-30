# Spring R2DBC iClient

The primary goal of Spring R2DBC iClient is to provide similar experience as Apache MyBatis in Reactive environment. There are some tweaks required to comply with Spring Data R2DBC.

## How to start with

To start using Spring R2DBC iClient, provide following properties in application.properties or application.yml file or initialize custome **ConnectionFactory** bean.

<pre>
spring.r2dbc.host=localhost
spring.r2dbc.port=3306
spring.r2dbc.username=user
spring.r2dbc.password=password
spring.r2dbc.database=mydb
spring.r2dbc.dbtype=mysql|postgresql|mariadb|oracle|mssql
</pre>

Spring R2DBC iClient support **MySql**, **PostgreSQL**, **MariaDB**, **Oracle** and **MSSql** databases. Additional options can be set by **spring.r2dbc.options** property with "**,**" separation and "**:**" for key name and value separation. 
As show below:
<pre>spring.r2dbc.options=useSSL:false,currentSchema:mydb</pre>

Once configs are provided or customer ConnectionFactory bean is initalized, need to provide **@R2dbcMapperScanner** annotation with package name to any configuration or spring boot application class, which will indicate Spring R2DBC iClient to search Mappers.
<pre><code>@R2dbcMapperScanner("example.mappers")</code></pre>

## Create Mapper

Create an interface annotating with **@R2dbcMapper**.
<pre><code>
@R2dbcMapper
public interface UserRepository {
  // mapper method goes here
}
</code></pre>

## Initialize SQL Statement methods

Use following annotations to create required SQL Statmement methods:

### SELECT

Annotate a method with **@SELECT** annotation providing SQL statment to execute.
<pre>
@Select("select * from user")
public Flux&lt;User&gt; getAll();
</pre>

If result mapping is not provided then default return type would be Map<String, Object>. 
To map SQL result to domain object, annotate a method with either **@Results** to provide a new mapping or with **@ResultMapping** to use exisiting mapping.
<pre>
@Results(id = "userMap", type = User.class, value = {
			@Result(property = "userId", column = "user_id", javaType = Integer.class),
			@Result(property = "userName", column = "user_name", javaType = String.class),
			@Result(property = "userPhone", column = "user_phone", javaType = String.class),
			@Result(property = "userAddress", javaType = UserAddress.class, resultMap = "userAddressMap") })
@Select("select * from user")
public Flux&lt;User&gt; getAll();

@Results(id = "userAddressMap", type = UserAddress.class, value = {
			@Result(property = "userAddress", column = "user_address", javaType = String.class),
			@Result(property = "userCity", column = "user_city", javaType = String.class),
			@Result(property = "userState", column = "user_state", javaType = String.class) })
@Select("select user_address, user_city, user_state from user")
public Flux&lt;UserAddress&gt; getUserAddress();
</pre>

As show above mapping, **@Result** requires `property` which will refer to domain object property, `column` refers to SQL result column and `javaType` refers to domain object property type.
If domain property is another domain object then provide existing `resultMap` ID and no `column` requires.

If query requires a parameter then following was paramters can be passed:
<pre>
@ResultMap("userMap")
@Select("select * from user where user_id = :userId")
public Mono&lt;User&gt; getUserById(@Param("userId") Integer userId);
</pre>

**@Param** annotation is required when passing parameter to SQL statement.

### INSERT

