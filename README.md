# “望冉”购票平台（后端）

## TODO(s)

- 某些类需要扁平化以修复bug

## 快速开始

### 环境要求

- JDK 17+
- MySQL 8.0+
- Maven 3.6+

### 配置与运行

- 数据库建表文件（理论上会自动执行）： `src/main/resources/schema.sql`
- 配置文件： `src/main/resources/application.yml`
- 运行： `mvn spring-boot:run`
- 服务默认运行在 `http://localhost:80`

## 项目结构

```text
src/main/java/io/github/crispyxyz/wangran
├── aspect          AOP 日志切面（监控方法执行耗时）
├── component       Excel 导入监听器、ModelMapper 辅助类等其他组件
├── config          配置类（MyBatis-Plus、ModelMapper、Security、Swagger）
├── controller      REST 控制器（按模块划分）
├── exception       全局异常处理及自定义异常
├── mapper          MyBatis-Plus 数据访问层
├── model           实体类
├── request         请求对象（带校验注解）
├── response        响应对象（统一返回格式 BaseResponse）
├── security        安全相关（JWT 过滤器、自定义权限注解、Principal 对象）
├── service         业务逻辑层（含并发控制、事务管理）
└── util            工具类（JWT、SHA-256、唯一序列生成、响应构造）
```

## 接口文档

启动项目后访问 `http://localhost/swagger-ui.html`

## 关键设计与实现

- 假删除：使用 `deleted` 字段作为假删除字段，通过 MyBatisPlus 自动配置
- 分页查询：使用 MyBatisPlus 分页插件实现
- JWT 认证：通过 `JwtFilter` 类拦截请求，解析，并设置 `SecurityContext` ，在 Controller 层使用自定义权限注解进行权限控制
- 批量导入：使用 Apache Fesod （原 FastExcel ）实现批量导入表格文件
- 高并发问题：通过悲观锁（ `SELECT ... FOR UPDATE` ）解决，详见相关 Mapper 类。已经通过集成测试
- AOP+SLF4J日志记录：实现方法耗时监控，以及必要日志打印
- ER 图：见 `wangran_er.drawio.png`
- 单元测试和redis：没有实现qwq

## 友情链接

[yan-forever/wangran-webui](https://github.com/yan-forever/wangran-webui)
