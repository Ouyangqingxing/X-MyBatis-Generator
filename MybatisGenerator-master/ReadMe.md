# MybatisGenerator

## QuickStart
* 关于MybatisGenerator
MybatisGenerator是一款Mybatis/ibatis的代码生成工具，该项目是在1.3.2版本上进行修改、扩展，如果需要1.3.2版的源码，可通过Maven获得
```
<dependency>
    <groupId>org.mybatis.generator</groupId>
        <artifactId>mybatis-generator-core</artifactId>
        <version>1.3.2</version>
</dependency>
```
* 如何配置
通过文件**generatorConfig.xml**进行配置，文件中有详细注释

* 如何运行
找到 src -> org.mybatis.generator -> api -> **ShellRunner.java** 运行


## 进阶 
* 关键类解析
1.  **配置文件解析器 MyBatisGeneratorConfigurationParser**
该类对generatorConfig.xml进行解析，并将解析结果放在成员变量properties中。在扩展了xml/dtd之后需要修改该类，才能将配置信息加载到Java中。
2. **上下文 Context**
在配置文件被解析到properties之后，各个Configuration（如UserConfiguration、TableConfiguration）会从properties中获取到需要的信息。而上下文中存放了这些Configuration，这几乎是我们所需的所有信息，故在大多时候我们都可以通过this.context来获取信息。
3. **数据库中表 IntrospectedTable**
IntrospectedTable是数据库表在Java中的抽象，它存放了tableConfiguration以及表的字段等信息
4. **Java代码生成器 JavaMapperGenerator / BaseRecordGenerator**
在Java代码生成器中我们可以控制生成的类的具体内容，包括类拥有哪些字段、哪些方法，以及方法的注释、访问权限符、参数、返回类型、代码段等等。
5. **注释生成器 CommentGenerator**
在注释生成器中我们可以控制生成代码的注释

* 如何进行扩展
1. 通过修改 mybatis-generator-config_1_0.dtd 约束文件 以及 generatorConfig.xml 配置文件可配置更多信息，关键类获取到后进行处理，使代码生成功能更全面更强大
2. 扩展方法、扩展类，例如如果想生成html、js代码，则需要有JavaScriptElement.java、JavaScriptMethod.java……

---
