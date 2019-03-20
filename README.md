# 插件模式

## [1]()版本说明

| 名称         | 版本           | 说明                       |
| ------------ | -------------- | -------------------------- |
| Eclipse Neon | JDK1.8         | 插件开发，支持JDK1.8,MAVEN |
| Sonarqube    | 5.6            | Sonarqube平台版本          |
| Plsql plugin | 2.1.0-SNAPSHOT | PLSQL插件版本              |

 

## [2]()插件模式介绍

​        插件模式是使用sonarqube提供的插件机制，使用JAVA语言来编写自定义规则。编写完成后，打包jar文件，放在对应的插件目录即可使用。

### 2.3.1开发环境

插件的开发依赖于**SonarQube PL/SQL Community plugin 2.1****版本**。该插件发布于github（https://github.com/felipebz/sonar-plsql）。本仓库在该版本上进行了部分修改，解决了一些parse fail错误。

在正式开发插件如下，需要配置开发环境。

1) 首先下载Maven，环境变量中配置maven/bin路径。

2）获取版本源码，在源码目录，执行mvn install，安装sonar-plsql-open-plugin-2.1.0-SNAPSHOT.jar到maven本地仓库。

3）继续以下配置。

Ø  在plsql-custom-rules目录，运行mvn clean package，保证运行成功。

Ø  在Eclipse中 配置maven，Window-Preferences-Maven-Installations中增加Maven路径。

然后将样例源码（plsql-custom-rules）导入进Eclipse中（Import-Maven-Exsiting Maven Projects）。

4) 接着，在该项目下进行Java插件的开发。开发结束后，使用mvn clean package命令将项目打包，并且复制sonar-plsql-open-plugin-2.1.0-SNAPSHOT.jar和plsql-custom-rules-1.0-SNAPSHOT.jar到sonarqube-5.6.6\extensions\plugins目录下，重启Sonarqube。

### 2.3.2 SSLR工具

​    SSLR是支持sonarqube的语言解析器，使用SSLR，您可以快速创建词法分析器，解析器和一些抽象语法树AST visitors，来帮助开发规则插件。

该插件提供了一个sslr工具，在sslr-plsql-toolkit目录下，工程编译后，在target目录下，生成sslr-plsql-toolkit-2.1.0-SNAPSHOT.jar文件，运行java –jar sslr-plsql-toolkit-2.1.0-SNAPSHOT.jar。就可以进行PLSQL解析。后续的开发会依赖于AST的解析。 

### 2.3.3 Rule开发

以下以“**禁止操作USER表**”来演示实现PL/SQL自定义规则开发的完整过程：

##### 制定规则的三个文件

首先，需要明确开发一个完整的PL/SQL规则要建立3个新文件，这3个文件分别是：

1、 一个包含规则实现的规则类，该类是规则检查的核心：例如ForbiddenDmlCheck.java（规则实现文件）；

2、 一个测试类，它包含规则的单元测试：例如ForbiddenDmlCheckTest.java（规则测试文件）；

3、 测试文件，包含正确和错误的代码，用于测试规则：例如forbidden-dml.sql（测试文件）

可以根据现有的例子来分别新建这3个文件，建立后再逐步开发。请注意按约定命名，例如测试类以Test结尾，规则类与测试类命名一致。

 

##### forbidden-dml.sql

文件路径：src/test/resources

插件以TDD模式进行开发，因此首先要做的是编写我们的规则将要针对的代码示例。在这个文件中，我们需要考虑我们的规则在分析过程中可能遇到的许多情况，并对需要违规的问题进行标记。例如：

**select** * 

  **from** **user** u **inner** **join** profile p **on** (u.id = p.user_id); -- Noncompliant {{Replace this query by a function of the USER_WRAPPER package.}} 

 

该文件不要求编译，但是构造应该是正确的，否则会解析错误。

 

##### ForbiddenDmlCheckTest.java

文件路径: src/test/java的包：com.company.plsql

更新测试文件后，需要更新测试类来使用它，并将测试链接到我们的（尚未实现）规则。

新建ForbiddenDmlCheckTest.java文件，该类的写法固定，只是名称需要改变。例如：

 

*package* *com.company.plsql;*

 

*import* *org.junit.Test;*

*import* *org.sonar.plsqlopen.checks.verifier.PlSqlCheckVerifier;*

 

**public** **class** ForbiddenDmlCheckTest {

 

​    @Test

​    **public** **void** test() {

​        PlSqlCheckVerifier.*verify*("src/test/resources/forbidden-dml.sql", **new** ForbiddenDmlCheck());

​    }   

}

说明：

PlSqlCheckVerifier.*verify*("src/test/resources/forbidden-dml.sql", **new** ForbiddenDmlCheck());

forbidden-dml.sql表示调试的时候分析这个文件。

new ForbiddenDmlCheck ()表示使用的是规则实现类ForbiddenDmlCheck。

 

##### ForbiddenDmlCheck.java

文件路径: src/main/java的包：com.company.plsql

编写好测试文件之后，开始编写规则实现的文件。以下为样例：

package com.company.plsql;

 

import org.sonar.check.Priority;

import org.sonar.check.Rule;

import org.sonar.plsqlopen.annnotations.ActivatedByDefault;

import org.sonar.plsqlopen.annnotations.ConstantRemediation;

import org.sonar.plsqlopen.checks.AbstractBaseCheck;

import org.sonar.plugins.plsqlopen.api.DmlGrammar;

 

import com.sonar.sslr.api.AstNode;

 

@Rule(

​                  name = "Avoid DML on table USER",                  //规则名称

​                  description = "You should use the functions from the USER_WRAPPER package.", //规则描述

​                  key = "ForbiddenDmlCheck",   //规则关键字

​                  priority = Priority.MAJOR,       //违规级别

​                  tags=Tags.BUG                                  //违规标签

​                  )

@ConstantRemediation("10min")    //修改时间

@ActivatedByDefault

public class ForbiddenDmlCheck extends AbstractBaseCheck {

 

​         @Override

​         public void init() {

​                  subscribeTo(DmlGrammar.DML_TABLE_EXPRESSION_CLAUSE);

​         }

 

​         @Override

​         public void visitNode(AstNode node) {

​                  AstNode table = node.getFirstChild(DmlGrammar.TABLE_REFERENCE);

 

​                  if (table != null && table.getTokenOriginalValue().equalsIgnoreCase("user")) {

​                          getContext().createViolation(this, "Replace this query by a function of the USER_WRAPPER package.", table);

​                  }

​         }

 

}可以仔细分析一下代码：

（1）**public class ForbiddenDmlCheck extends AbstractBaseCheck**，该类的实现类和继承类都是固定的，不用改变，变的只有不同的类名。

（2）**public** **void** *init()**的*subscribeTo(DmlGrammar.DML_TABLE_EXPRESSION_CLAUSE);获取抽象语法树AST的某一个visitor，例如**DML_TABLE_EXPRESSION_CLAUSE****，***这里根据规则的需要获取，可以在获取前通过**SSLR**工具分析样例文件来确认获取**AST**的那一个部分*。常用的类有*PlSqlGrammar**，**DdlGrammar**，**DclGrammar**，**DmlGrammar**等。*

（3）**public** **void** *visitNode(AstNode* *node**)*作为开发起点。具体的业务逻辑就在这个方法内实现，其实，就是一些简单的Java判断代码，掌握几个要点就可以了。

我们的判断依据就是从这段代码中获取符合规则的代码。我们需要确认的是是否有flose的操作。

**（4****）需要重点明白的一点是，使用Java****插件开发的话，必须借助于Debug****模式来开发，否则将会寸步难行。进入FileMustBeCloseTest****，右键Debug As****，选择JUnit Test****即可调试。这样的话，通过断点来观察每一步地树形结构，来确定规则的编写。同时，也要充分的使用SSLR****工具，如例子中fclose****，fopen****其实都在****METHOD_CALL****中，这些信息的获取都可以利用****SSLR****来获取****。**

（5）getContext().createViolation(this, "The cursor must be closed.", statement);固定写法，用于产生警报，"The cursor must be closed.",是违规的警告内容，根据实际修改，statement表示在statement处来产生警报。

 

经过以上这几个步骤，一个完整的PLSQL自定义规则就开发完成了。

### 2.3.4注册插件

1、文件路径: src/main/java的包：com.company.plsql

插件开发完成后，需要进行激活，否则在页面上是无法找到并使用该规则的。

修改PlSqlCustomRulesDefinition.java文件：

**public** Class[] checkClasses() {

​        **return** **new** Class[] {

​            *ForbiddenDmlCheck.***class***,* **//****规则类，新增规则**

​            *NoDataFoundCheck.***class***,*

​            *CursorMustBeClose.***class**

​       };

}

 

也可以修改资源库的名称（可选步骤，非必须）：

   **public** *String repositoryName() {*

​        **return** *"dcits"**;                 //**资源库名称*

​    *}*

 

​    *@Override*

​    *public String repositoryKey() {*

​        *return "dcits-rules";           //**资源库关键字*

*}*

 

2、文件路径: src/test/java的包：com.company.plsql

修改单元测试文件：PlSqlCustomRulesPluginTest.java，修改规则个数：

*assertThat*(plugin.checkClasses().length).isEqualTo(3);

### 2.3.5测试

开发调试：进入ForbiddenDmlCheckTest，右键Debug As，选择JUnit Test即可调试。这样的话，通过断点来观察每一步地树形结构，来确定规则的编写。

部署调试：

在部署插件时，需要先将依赖的sonar-plsql-open-plugin-2.1.0-SNAPSHOT.jar放入sonarqube-5.6.6\extensions\plugins目录下。

1、 进入Java代码目录，使用mvn clean package命令将项目打包

2、 复制到sonarqube-5.6.6\extensions\plugins目录下，重启sonarqube

3、 将自定义规则添加到默认质量配置，自定义规则的选择：代码规则-语言选择PLSQL，资源库选择：MyCompany Custom Repository或者DCITS自定义Java规则（使用dcits-java-custom-rules开发）

4、 接着输入cmd进入命令行模式，进入需要调试的工程目录。比如，C:\work\manage>，输入sonar-scanner进行代码扫描，注意：sonar.language=plsqlopen

5、 待扫描结束后，可以在Sonarqube界面查看bug情况。

