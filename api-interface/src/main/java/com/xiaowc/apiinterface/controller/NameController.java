package com.xiaowc.apiinterface.controller;

import com.xiaowc.apiclientsdk.model.User;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 提供三个模拟接口
 *  1.GET接口
 *  2.POST接口(url传参)
 *  3.POST接口(Restful传参)
 * 名称API
 *
 * 扩展：
 *  1.怎么让其他用户也上传接口？
 *    需要提供一个机制(界面)，让用户输入子集的接口host(服务器地址)、接口信息，将接口信息写入数据库，将
 *    接口信息写入数据之前，要对接口进行校验(比如检查他的地址是否遵循规则，测试调用)，保证他是正常的。将接口信息写入数据库之前
 *    遵循咱们的要求(并且使用给咱们的sdk)，在接入时，平台需要测试调用这个接口，保证他是正常的
 *  2.在interfaceInfo表里加个host字段，区分服务器地址
 */
@RestController
@RequestMapping("/name")
public class NameController {

    /**
     * 模拟接口1：GET接口
     * http://localhost:8123/api/name/get?name=xiaowc
     *
     * 利用网关：
     *  1.请求转发：
     *    使用一个前缀匹配断言？在application.yml配置文件中进行配置
     *      所有路径为：/api/**的请求进行转发，转发到http://localhost:8123/api/*
     *      比如请求网关：http://localhost:8090/api/name/get?name=xiaowc
     *      转发到：http://localhost:8123/api/name/get?name=xiaowc
     * @param name
     * @return
     */
    @GetMapping("/get")
    public String getNameByGet(String name, HttpServletRequest request) {
        System.out.println(request.getHeader("xiaowc")); // 这里可以取到请求染色的哪个请求头
        return "GET 你的名字是" + name;
    }

    /**
     * 模拟接口2：POST接口(url传参)
     * @param name url传参的那个参数
     * @return
     */
    @PostMapping("/post")
    public String getNameByPost(@RequestParam String name) {
        return "POST 你的名字是" + name;
    }

    /**
     * API签名认证是一个很灵活的设计，具体要有哪些参数，参数名如何一定要根据场景来。(比如userId,appId,version,固定值等等)
     *
     * 模拟接口3：POST接口(Restful传参)
     * @param user 前端传过来的user
     * @param request
     *
     * 这里还有一个问题：要想知道是哪个用户调用了这个接口，这时引出了签名认证
     *  1.API签名认证：要求每次调用都要认证，而不是像用户登录那样只登录一次就行
     *    本质：1.签发签名。2.使用签名(校验签名)
     *  2.为什么需要？
     *    1.保证安全性
     *  3.怎么实现签名认证？类似于用户名和密码，区别：ak,sk是无状态的，每次都要认证
     *    1.accessKey: 调用的标识userA,userB
     *    2.secretKey: 密钥
     *
     * 千万不要把密钥直接在服务器之间传递，因为如果别人拦截到了你这个请求，也可以像上面一样从你的请求头中拿到密钥，不安全
     *   这时我们要对这个密钥再次进行加密(sign参数)，一般叫做签名
     *     加密方式：对称加密、非对称加密、md5加密(不可解密)
     *        用户参数+密钥 -> 签名生成算法 -> 不可解密的值
     *        abc+12345678 -> aophegepwohgao
     *   服务端怎么知道你的这个签名对不对？
     *     服务端用一模一样的参数和算法去生成签名，只要和用户传的一致，就标识这个签名对
     *
     *  一个标准的API签名认证算法要加以下6个参数：
     *      1.参数1：accessKey用户的标识
     *      2.参数2：secretKey密钥
     *      3.参数3：用户参数，就是请求地址那个参数(建议也放入，这样会更严格)
     *    密钥不能直接在服务器之间传递，因为如果别人拦截到了你这个请求，也可以像上面一样从你的请求头中拿到密钥，不安全
     *      4.参数4：sign对密钥再次进行加密的参数
     *    怎么防重放？
     *      5.参数5：加nonce随机数，只能用一次，如果拿之前的随机数访问重放，后端就不认识，因为你之前用这个随机数访问过了
     *        缺点：服务端要保存用过的随机数，
     *      6.参数6：加timestamp时间戳，校验时间戳是否过期。可以和上面打配合，比如5分钟过期，就将上面保存的随机数删除掉
     *
     * @return
     */
    @PostMapping("/user")
    public String getUsernameByPost(@RequestBody User user, HttpServletRequest request) {
//        // 签名认证的权限校验，这里从request的请求头中去获取，不要把accessKey和secretKey直接传入
//        // 因为客户端把accessKey和secretKey存入到了请求头中，所以这里可以直接取到
//        String accessKey = request.getHeader("accessKey"); // 前端传入的用户的标识
//        // String secretKey = request.getHeader("secretKey"); // 前端的secretKey千万不能放到map集合中
//        String nonce = request.getHeader("nonce"); // 前端传入的随机数
//        String timestamp = request.getHeader("timestamp"); // 前端传入的时间戳
//        String sign = request.getHeader("sign"); // 前端传入的签名
//        String body = request.getHeader("body"); // 前端传入的请求体信息
//        // 这里只是演示，千万不要把密钥直接在服务器之间传递，因为如果别人拦截到了你这个请求，也可以像上面一样从你的请求头中拿到密钥，不安全
//        // 这时我们要对这个密钥再次进行加密(sign)，一般叫做签名
//
//        // 这里对前端传入的东西进行校验，和后端生成的对应的参数进行比对，比对成功则可以成功调用API接口
//        // TODO: 2023/1/16 实际情况应该是去数据库中查是否已分配给用户
//        if (!accessKey.equals("xiaowc")) {
//            throw new RuntimeException("无权限");
//        }
//        if (Long.parseLong(nonce) > 10000) {
//            throw new RuntimeException("无权限");
//        }
        // TODO: 2023/1/16 时间和当前时间不能超过5分钟
//        if (timestamp) {
//
//        }
        // TODO: 2023/1/16 实际情况中是从数据库中查出secretKey
        // serverSign是后端生成的签名
//        String serverSign = SignUtils.genSign(body, "abcdefgh");
//        if (!sign.equals(serverSign)) {
//            throw new RuntimeException("无权限");
//        }
        // todo 调用次数 + 1 invokeCount
        String result = "POST 用户名字是" + user.getUsername();

        // 调用成功后，次数+1
        /**
         * 问题：如果每个接口的方法都写调用次数+1，是不是比较麻烦。接口开发者需要自己去添加统计代码
         * 解决：1.AOP切面。2.Servlet拦截器(过滤器)。3.通用方法
         * 调用次数切面
         * AOP的作用：
         *   就是在我们调用某个接口前、后替我们先去做一些事情(比如执行一个方法等等)，动态代理
         * 使用AOP切面的优点：独立于接口，在每个接口调用后统计次数+1
         * 使用AOP切面的缺点：只存在于单个项目中，如果每个团队都要开发自己的模拟接口，那么都要写一个切面
         *
         * 网关：
         *   我们这里使用的是比AOP更高级的技术来统计接口调用的次数，也就是网关：我们使用Spring Cloud Gateway
         *   1.什么是网关？理解成火车站的检票口，统一去检票
         *   2.网关的优点：统一去进行一些操作，处理一些问题，对于用户来说，屏蔽了底层的调用细节
         *   3.网关的作用：
         *      1.路由：起到转发的作用，比如有接口A和接口B，网关会记录这些信息，根据用户访问的地址和参数，转发请求到对应的接口(服务器/集群)
         *        /a -> 接口A       /b -> 接口B
         *      2.负载均衡：在路由的基础上，/c -> 服务A/集群A(随机转发到其中的某一个机器)，从固定地址改成赋值均衡的地址
         *      3.统一鉴权：判断用户是否有权限进行操作，无论访问什么接口，我都统一去判断权限，不用重复写
         *      4.统一处理跨域：网关统一处理跨域，不用在每个项目里单独处理
         *      5.统一业务处理(缓存)：把一些每个项目中都要做的通用逻辑放到上层(网关)，统一处理，比如本项目的接口调用次数统计
         *      6.访问控制：黑白名单，比如限制 DDOS IP
         *      7.发布控制：灰度发布，比如上线新接口，先给新用户分配20%的流量，老接口80%，再慢慢调整比重，防止不稳定
         *      8.流量染色：给请求(流量)添加一些标识，一般是设置请求头中，添加新的请求头，证明这个请求是经过网关的。防止一些用户绕过网关直接调用接口
         *      9.统一接口保护：(1)限制请求。(2)信息脱敏。(3)降低(熔断)。(4)限流。(5)超时时间
         *      10.统一日志：统一的请求、响应信息记录
         *      11.统一文档：将下游项目的文档进行聚合，在一个页面统一查看
         *   4.网关的分类：
         *       1.全局网关(接入层网关)：作用是负载均衡，请求日志等，不和业务逻辑绑定
         *       2.业务网关(微服务网关)：会有一些业务逻辑，作用是将请求转发到不同的业务/项目/接口/服务
         *   5.实现：
         *       1.Nginx(全局网关)、kong网关(API网关)：编程成本相对高一点
         *       2.Spring Cloud Gateway(取代了Zuul)性能高、可以用java代码来写逻辑，适于学习
         */
        return result;
    }
}
