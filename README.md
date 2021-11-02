# Spring R2DBC iClient

The primary goal of Spring R2DBC iClient is to provide similar experience as Apache MyBatis in Reactive environment. There are some tweaks required to comply with Spring Data R2DBC.

Spring R2DBC iClient is totally annotation driven. Currently it does not support XML mapping unlike Apache MyBatis.

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

Annotate a method with **@Select** annotation providing SQL statment to execute select queries.
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

As show above mapping, **@Result** requires `property` which will refer to domain object property, `column` refers to SQL result column and `javaType` refers to domain object property data type.
If domain property is another domain object then provide existing `resultMap` ID and no `column` requires.

If query requires a parameter then following was paramters can be passed:
<pre>
@ResultMap("userMap")
@Select("select * from user where user_id = :userId")
public Mono&lt;User&gt; getUserById(@Param("userId") Integer userId);
</pre>

**@Param** annotation is required when passing parameter to SQL statement. ** Refer Note #1 for `propertyMapper`**

### INSERT

Annotate a method with **@Insert** annotation providing SQL statment to execute insert queries.
<pre>
@Insert(value = "insert into user (user_name, user_phone, user_address, user_city, user_state) values (:user.userName, :user.userPhone, :user.userAddress.userAddress, :user.userAddress.userCity, :user.userAddress.userState)", 
	propertyMapper = {
		@PropertyMapper(javaType = String.class, properties = "user.userPhone, user.userName, user.userAddress.userAddress, user.userAddress.userCity, user.userAddress.userState") }, 
	retrieveId = "user_id", 
	idType = Integer.class)
public Mono&lt;Integer&gt; insertUser(@Param("user") User user);
</pre>

** Refer Note #1 for `propertyMapper`** 

`retrieveId` is optional property. Provide SQL result column name to get value as return of SQL method call. If no `retrieveId` provided then default return would be number of records affected by execution of statement.

`idType` is required when defining `retrieveId`. It will hold java data type in value must be returned. If `idType` provided then return type would by Mono<`idType`> else Mono<Integer>.

### UPDATE

Annotate a method with **@Update** annotation providing SQL statment to execute update queries.
<pre>
@Update(value = "update user set user_name = :user.userName where user_id = :user.userId",
	propertyMapper = {
		@PropertyMapper(javaType = String.class, properties = "user.userName"),
		@PropertyMapper(javaType = Integer.class, properties = "user.userId") })
public Mono<Integer> updateUserName(@Param("user") User user);
</pre>

** Refer Note #1 for `propertyMapper`**

Update statement will always return Mono<Integer> providing number of records affected by executing statement.

### DELETE

Annotate a method with **@Delete** annotation providing SQL statment to execute delete queries.
<pre>
@Delete(value = "delete from user where user_id = :user.userId",
	propertyMapper = {
		@PropertyMapper(javaType = Integer.class, properties = "user.userId") })
public Mono<Integer> deleteUser(@Param("user") User user);
</pre>

** Refer Note #1 for `propertyMapper`**

Delete statement will always return Mono<Integer> providing number of records affected by executing statement.

### TypeConverter

`TypeConverter` interface is used to convert SQL result into different java data type or execute certain code before mapping column value to java property.

`TypeConverter` can be applied to mapping following way. `javaType` is require for returning resulting value into provided data type.
<pre>
@Results(id = "userAddressMap", type = UserAddress.class, value = {
			@Result(property = "userAddress", column = "user_address", javaType = String.class),
			@Result(property = "userCity", column = "user_city", javaType = String.class),
			@Result(property = "userState", column = "user_state", javaType = String.class),
			@Result(property = "userFullAddress", javaType = String.class, typeConverter = AddressCombiner.class})
@Select("select user_address, user_city, user_state, user_zip from user")
public Flux&lt;UserAddress&gt; getUserAddress();
</pre>

`TypeConverter` can be used following way to change data type of column value to java data type
<pre>
import org.reactive.r2dbc.iclient.type.TypeConverter;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;

public class StringTypeConverter implements TypeConverter{

	@Override
	public String convert(Row row, RowMetadata rowMetadata) {
		return String.valueOf(row.get("user_id", Integer.class));
	}
}
</pre>

`io.r2dbc.spi.Row` will hold each SQL result record with column name and value mapping. Single or all column values can be accessed here to apply code.

Use `TypeConverter` to execute code logic before returning value
<pre>
import org.reactive.r2dbc.iclient.type.TypeConverter;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;

public class AddressCombiner implements TypeConverter{

	@Override
	public String convert(Row row, RowMetadata rowMetadata) {
		return String.valueOf(row.get("user_address", Integer.class))
			.concat(", ")
			.concat(String.valueOf(row.get("user_city", Integer.class)))
			.concat(", ")
			.concat(String.valueOf(row.get("user_state", Integer.class)))
			.concat(" - ")
			.concat(String.valueOf(row.get("user_zip", Integer.class)));
	}
}
</pre>

## Notes
**1] Why to provide `propertyMapper` to SELECT|INSERT|UPDATE|DELETE annotation while passing parameters**

Unlike MyBatis, `propertyMapper` is required when passing paramter as paramter value can be null. Please read [Spring R2DBC DatabaseClient.BindSpec](https://docs.spring.io/spring-data/r2dbc/docs/current/api/org/springframework/data/r2dbc/core/DatabaseClient.BindSpec.html#bindNull-int-java.lang.Class-) documentation. As Spring R2DBC iClient uses DatabaseClient to communicate with database, it needs `propertyMapper` with possible properties and their java data type listing to parse a query with parameters.

**@PropertyMapper** requires `javaType` which is java data type and `properties` having list of parameters or if parameter is domain object then list of properties that domain object having this java data type.
