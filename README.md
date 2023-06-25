# xiaowc - API 开放平台
项目介绍：基于Spring Boot + Dubbo + Gateway + React的API接口调用平台。管理员可以接口并发布接口，可视化各接口的调
用情况；用户可以浏览接口及在线调试，并通过客户端SDK轻松调用接口。
1. 根据业务流程，将整个项目后端划分为web系统、模拟接口、公共模块、客户端SDK、API网关这5个子项目，并使用Maven进行多模块依赖管理和打包。
2. 为防止接口被恶意调用，设计API签名算法，为用户分配唯一accesskey/secretkey鉴权，保证接口调用的安全性。
3. 为解决开发者调用成本过高的问题(须自己使用http+封装签名去调用接口)，基于Spring Boot Starter开发了客户端SDK，一行代码即可调用接口，提升开发体验。
4. 选用Spring Cloud Gateway作为API网关，实现了路由转发、访问控制、流量染色，并集中处理签名校验、请求参数校验、接口调用统计等业务逻辑，提高安全性的同时，便于系统开发维护。
5. 为解决多个子系统内代码大量重复的问题，抽象模型层和业务层代码为公共模块，并使用Dubbo RPC框架实现子系统间的高性能接口调用，大量减少重复代码。

流程：
1. 先分别对api-client-sdk，api-common进行install打包
2. 先在本地运行nacos服务器：startup.cmd -m standalone
3. 运行api-interface项目
4. 运行api-backend项目
5. 运行api-gateway项目
6. 运行前端api-frontend项目即可访问
