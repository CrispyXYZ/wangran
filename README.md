# wangran

项目依赖信息：
- Java 17 - 目标编译运行环境
- Spring Boot 3.5.10 - 项目框架
- MyBatis - 持久层框架
- MyBatisPlus 3.5.15 - 简化 MyBatis 相关开发
- MySQL Connector J - 数据库连接驱动
- Lombok - 简化开发
- ModelMapper - 简化实体类数据转换
- Java JWT 4.5.0 - JWT 实现

环境：
```
openjdk 21.0.4 2024-07-16 LTS
OpenJDK Runtime Environment Zulu21.36+17-CA (build 21.0.4+7-LTS)
OpenJDK 64-Bit Server VM Zulu21.36+17-CA (build 21.0.4+7-LTS, mixed mode, sharing)

mysql  Ver 8.0.45-0ubuntu0.22.04.1 for Linux on x86_64 ((Ubuntu))
```

数据库相关：
- 执行 `init_db.sql` 即可删除并重建数据库，**执行前先打开检查一下，删库警告！！！**
- 执行 `reset_db.sql` 即可重置数据表
- 数据库连接url：`jdbc:mysql://localhost:3306/wangran_db`

项目结构相关：
- 结构比较简单
- 主要逻辑集中在 `service`、`handler`、`interceptor` 以及 `util`
- `controller` 只负责转发
- 其它的大部分都是一些数据包装和声明

# 接口文档

- 端口：`80`

## 统一响应格式说明

成功示例：

```json
{
  "success": true,
  "message": "success",
  "data": null
}
```

失败示例：

```json
{
  "success": false,
  "message": "过期的 Token",
  "data": null
}
```

响应参数说明：

| 字段名       | 字段类型      | 字段说明 | 备注                     |
|-----------|-----------|------|------------------------|
| `success` | `boolean` | 是否成功 |                        |
| `message` | `string`  | 消息   | 成功一定为success，失败为错误消息   |
| `data`    | `object`  | 数据   | 成功为数据（可null），失败一定为null |

注：请求成功的HTTP状态码一定为`200 OK`，请求失败也会返回相应的状态码

## POST 用户注册接口

### 接口说明

**接口功能** ：新用户（普通用户或商户）注册。

**接口地址** ：`http://localhost/auth/register`

**请求方式** ：`POST`

**请求头** ：

| 请求头            | 值                  | 说明     |
|----------------|--------------------|--------|
| `Content-Type` | `application/json` | JSON格式 |

### 请求示例

```json
{
  "phoneNumber": "123456",
  "password": "1234567890",
  "merchant": true
}
```

**请求参数说明**：

| 字段名           | 字段类型      | 字段说明  | 是否必填 | 备注/校验规则                |
|---------------|-----------|-------|------|------------------------|
| `phoneNumber` | `string`  | 手机号   | 是    | 不超过20位，纯数字             |
| `password`    | `string`  | 密码    | 是    | 长度在6-50之间              |
| `merchant`    | `boolean` | 是否为商户 | 是    | `true`为商户，`false`为普通用户 |


### 响应示例

```json
{
  "success": true,
  "message": "success",
  "data": {
    "id": 2,
    "username": "user_74c1e8cc030c46699953d02d5692e8ff",
    "phoneNumber": "1234567"
  }
}
```

```json
{
    "success": true,
    "message": "success",
    "data": {
        "id": 6,
        "username": null,
        "merchantId": null,
        "phoneNumber": "12345678",
        "approvalStatus": "PENDING",
        "rejectReason": null
    }
}
```

**响应数据说明**：

| 字段名              | 字段类型     | 字段说明      | 备注                                                       |
|------------------|----------|-----------|----------------------------------------------------------|
| `id`             | `number` | 数据库表中自增id | 用户和商户的id是分开储存的                                           |
| `username`       | `string` | 系统生成的唯一昵称 | 商户需审核通过后才生成                                              |
| `phoneNumber`    | `string` | 手机号       | 与请求的手机号一致                                                |
| `merchantId`     | `string` | 商户编号      | 商户独有字段，需审核通过后才生成                                         |
| `approvalStatus` | `string` | 商户审核状态    | 商户独有字段，固定为`PENDING`(待审核), `APPROVED`(通过), `REJECTED`(驳回) |
| `rejectReason`   | `string` | 商户驳回理由    | 商户独有字段，审核后生成                                             |

## POST 用户登录接口

### 接口说明

**接口功能** ：用户登录

**接口地址** ：`http://localhost/auth/login`

**请求方式** ：`POST`

**请求头** ：

| 请求头            | 值                  | 说明     |
|----------------|--------------------|--------|
| `Content-Type` | `application/json` | JSON格式 |

### 请求示例

```json
{
  "identifier": "123456",
  "password": "123123"
}
```

**请求参数说明**：

| 字段名          | 字段类型      | 字段说明 | 是否必填 | 备注/校验规则                   |
|--------------|-----------|------|------|---------------------------|
| `identifier` | `string`  | 标识符  | 是    | 可以是手机号、商户ID、管理员账号，不超过50字符 |
| `password`   | `string`  | 密码   | 是    | 长度在6-50之间                 |


### 响应示例

```json
{
  "success": true,
  "message": "success",
  "data": {
    "account": {
      "id": 3,
      "username": "user_a2707ac232ff42c0a0f06203214980af",
      "phoneNumber": "1234567"
    },
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyX2EyNzA3YWMyMzJmZjQyYzBhMGYwNjIwMzIxNDk4MGFmIiwiaWF0IjoxNzcwNjE1MDYwLCJleHAiOjE3NzA2MTUxMjAsInJvbGUiOiJ1c2VyIn0.B27ggVvhxYZLjRLbkej-PZ8Ab0jsGgVKVVNmX2OhyWY"
  }
}
```

**响应数据说明**：

| 字段名       | 字段类型     | 字段说明      | 备注           |
|-----------|----------|-----------|--------------|
| `account` | `object` | 账户数据对象    | 与注册时返回数据格式相同 |
| `token`   | `string` | JWT Token |              |


## POST 审核接口

### 接口说明

**接口功能** ：管理员对注册的商户账户进行审核（通过或驳回）。**此接口需持有管理员token才可访问**

**接口地址** ：`http://localhost/auth/review`

**请求方式** ：`POST`

**请求头** ：

| 请求头             | 值                          | 说明        |
|-----------------|----------------------------|-----------|
| `Content-Type`  | `application/json`         | JSON格式    |
| `Authorization` | `Bearer example.jwt.token` | JWT Token |

### 请求示例

```json
{
  "merchantPhoneNumber": "1234566",
  "approved": false,
  "rejectReason": "test reject"
}
```

**请求参数说明**：

| 字段名                   | 字段类型      | 字段说明     | 是否必填 | 备注/校验规则            |
|-----------------------|-----------|----------|------|--------------------|
| `merchantPhoneNumber` | `string`  | 待审核商户手机号 | 是    | 不超过20位             |
| `approved`            | `boolean` | 审核是否通过   | 是    | `true`通过，`false`驳回 |
| `rejectReason`        | `string`  | 驳回理由     | 否    | 不超过50字符            |



### 响应示例

```json
{
  "success": true,
  "message": "success",
  "data": {
    "approved": true,
    "merchantId": "mid_1770454104640",
    "username": "merchant_1dac96b5a10d4e5aabf5e157cdd1072d",
    "phoneNumber": "1234566"
  }
}
```

**响应数据说明**：

| 字段名           | 字段类型      | 字段说明   | 备注       |
|---------------|-----------|--------|----------|
| `approved`    | `boolean` | 审核是否通过 | 与请求参数一致  |
| `merchantId`  | `string`  | 商户编号   | 审核通过才会生成 |
| `username`    | `string`  | 昵称     | 审核通过才会生成 |
| `phoneNumber` | `string`  | 手机号    | 与请求参数一致  |

