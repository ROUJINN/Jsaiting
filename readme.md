### 如何通过git合作?

**注意: 每次开始修改代码前先git pull**

目前只用一个branch就是main, 我们自己都在main上面改. 如果自己在修改的过程中, 远程的main发生了更新, 那么操作是: 先git stash,再git pull, 再git stash apply, 参考下面的命令.
```
git stash # 保存当前修改
git stash list #列出所有的stash
git stash apply stash@{1} #恢复指定的 stash，例如 stash@{1}
git stash drop # 删除最近的stash
```

### 运行方式
IDEA中启动main之后，在本地浏览器打开

http://localhost:8080/

数据库H2的控制台

http://localhost:8080/h2-console/

打开控制台后，要设置JDBC URL为jdbc:h2:file:./filedb

用户名密码为

spring.datasource.username=sa

spring.datasource.password=password

### 登录
用户名：me\
密码：password\
也可以自己注册

### 用到的技术

Backend Technologies
Java 17: The programming language version used as specified in pom.xml
Spring Boot 3.0.6: Main framework for building the application, configured in pom.xml and used in Main.java
Spring MVC: Handles web requests through controllers like FileController.java
Spring Data JPA: Manages database operations via FileRepository.java
H2 Database: File-based database for storing file metadata, configured in application.properties
Frontend Technologies
Thymeleaf: Template engine for rendering HTML pages in index.html
Bootstrap 5: CSS framework for styling, loaded from CDN in the HTML templates
Build Tools
Maven: For dependency management and project building, defined in pom.xml