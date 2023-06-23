package com.xiaowc.apigateway;

import com.xiaowc.apiclientsdk.utils.SignUtils;
import com.xiaowc.apicommon.model.entity.InterfaceInfo;
import com.xiaowc.apicommon.model.entity.User;
import com.xiaowc.apicommon.service.InnerInterfaceInfoService;
import com.xiaowc.apicommon.service.InnerUserInterfaceInfoService;
import com.xiaowc.apicommon.service.InnerUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 编程式实现：
 * 自定全局过滤器，自己定义处理每个请求的规则，所有请求只要经过网关都要执行这个过滤器的逻辑
 *   将业务逻辑写到这个自定义的全局过滤器中
 * 详见官网：https://docs.spring.io/spring-cloud-gateway/docs/3.1.4/reference/html/#gateway-combined-global-filter-and-gatewayfilter-ordering
 */
@Slf4j
@Component // 将他交给spring容器进行管理
public class CustomGlobalFilter implements GlobalFilter, Ordered {

    @DubboReference  // 使用@DubboReference表示从其他项目中引入这个接口进行使用
    private InnerUserService innerUserService;

    @DubboReference  // 使用@DubboReference表示从其他项目中引入这个接口进行使用
    private InnerInterfaceInfoService innerInterfaceInfoService;

    @DubboReference  // 使用@DubboReference表示从其他项目中引入这个接口进行使用
    private InnerUserInterfaceInfoService innerUserInterfaceInfoService;

    /**
     * 请求白名单，只有用户在这个名单中，才可以调用
     */
    private static final List<String> IP_WHITE_LIST = Arrays.asList("127.0.0.1", "0:0:0:0:0:0:0:1");

    // TODO: 2023/1/23 问题：如何获取接口转发服务器的地址？
    // TODO: 2023/1/23 网关启动时，获取所有的接口信息，维护到内存hashmap中，有请求时，根据请求的url路径或者其他参数(比如host请求头)
    // TODO: 2023/1/23 来判断应该转发到哪台服务器，以及用于校验接口是否存在
    private static final String INTERFACE_HOST = "http://localhost:8123";

    /**
     * 全局过滤器，将业务逻辑写到这个过滤器中，所有请求只要经过网关都要执行这个过滤器的逻辑
     * 业务逻辑使用了GlovalFilter，全局请求拦截处理(类似于AOP)
     *
     * 问题：网关项目比较纯净，并没有引入MyBatis等操作数据库的类库，并且还要调用我们之前写过的代码，复制粘贴会很麻烦、维护麻烦
     * 解决：RPC：通过RPC远程过程调用技术直接请求到api-backend项目中的方法
     *
     * 怎么调用其他项目的方法？
     *   1.复制代码和依赖、环境
     *   2.HTTP请求(提供一个接口，供其他项目调用，比如我们api-client-sdk项目中利用hutool工具包发送请求来进行调用)。怎么调用？
     *     1.提供方开发一个接口(地址、请求方法、参数、返回值)
     *     2.调用方使用HTTP Client之类的代码去发送HTTP请求(比如hutool)
     *   3.RPC(Dubbo)：看官网：https://cn.dubbo.apache.org/zh/docs3-v2/java-sdk/quick-start/spring-boot/
     *     1.作用：像调用本地方法一样调用远程方法，对开发者更透明，减少了很多沟通成本
     *     2.RPC像远程服务器发送请求时，未必要使用HTTP协议，比如还可以用TCP/IP协议，性能更高(内部服务更适用)
     *     3.两种使用方式：
     *       1.spring boot代码(注解+编程式)：写java接口，服务提供者和消费者都去引用这个接口
     *           先启动注册中心、再启动服务的提供者、最后启动服务的消费者
     *       2.IDL(接口调用语言)：创建一个公共的接口定义文件，服务提供者和消费者读取这个文件来判断自己要调用哪个接口。优点是跨语言，所有的框架都认识
     *     4.Dubbo底层是Triple协议
     *   4.把公共的代码打个jar包，其他项目去引入(客户端sdk)
     *
     * 整合应用：
     *   1.api-backend项目作为服务提供者，提供3个方法：
     *     1.实际情况应该是去数据库中查是否已分配给用户
     *     2.从数据库中查询接口是否存在，以及请求方法是否匹配(还可以校验请求参数)
     *     3.调用成功，接口调用次数+1  invokeCount
     *   2.api-gateway项目作为服务调用者，调用这3个方法：
     *   3.我们这里直接用dubbo示例的方式来，不自己搭建注册中心了
     * @param exchange
     * @param chain
     * @return Mono就是返回一个响应式的对象
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1.用户发送请求到API网关(已经实现，就是在application.yml配置文件中定义的)
        // 2.请求日志
        ServerHttpRequest request = exchange.getRequest(); // 拿到request请求体，再通过request去拿到相应的信息
        String path = INTERFACE_HOST + request.getPath().value();
        String method = request.getMethod().toString();
        log.info("请求唯一标识：" + request.getId());
        log.info("请求路径：" + path);
        log.info("请求方法：" + method);
        log.info("请求参数：" + request.getQueryParams());
        String sourceAddress = request.getLocalAddress().getHostString();
        log.info("请求来源地址：" + sourceAddress);
        log.info("请求来源地址：" + request.getRemoteAddress());
        ServerHttpResponse response = exchange.getResponse(); // 拿到response响应体，可以控制响应
        // 3.访问控制 -> 黑白名单
        // 如果不在名单内，就访问拒绝，直接返回一个状态码然后拦截掉
        if (!IP_WHITE_LIST.contains(sourceAddress)) {
            response.setStatusCode(HttpStatus.FORBIDDEN); //设置一个禁止访问的状态码
            return response.setComplete(); // 表示这个响应完成了
        }
        // 4.用户鉴权(判断accessKey和secretKey是否合法)
        HttpHeaders headers = request.getHeaders();
        String accessKey = headers.getFirst("accessKey"); // 前端传入的用户的标识
//        String secretKey = request.getHeader("secretKey"); // 前端的secretKey千万不能放到map集合中
        String nonce = headers.getFirst("nonce"); // 前端传入的随机数
        String timestamp = headers.getFirst("timestamp"); // 前端传入的时间戳
        String sign = headers.getFirst("sign"); // 前端传入的签名
        String body = headers.getFirst("body"); // 前端传入的请求体信息
        // 这里对前端传入的东西进行校验，和后端生成的对应的参数进行比对，比对成功则可以成功调用API接口
        // TODO: 2023/1/16 实际情况应该是去数据库中查是否已分配给用户
        User invokeUser = null;
        try {
            // 因为这个网关项目没有引入mybatis，所以这里用了Dubbo远程调用的方法去调用远程backend增删改查项目提供的接口
            invokeUser = innerUserService.getInvokeUser(accessKey); // 根据用户的accessKey获取用户信息
        } catch (Exception e) {
            log.error("getInvokeUser error", e);
        }
        if (invokeUser == null) { // 说明没有这个用户
            return handleNoAuth(response);
        }
//        if (!"xiaowc".equals(accessKey)) { // 实际情况要从数据库中去查
//            return handleNoAuth(response);
//        }
        if (Long.parseLong(nonce) > 10000L) {
            return handleNoAuth(response);
        }
        // 时间和当前时间不能超过 5 分钟
        Long currentTime = System.currentTimeMillis() / 1000;
        final Long FIVE_MINUTES = 60 * 5L;
        if ((currentTime - Long.parseLong(timestamp)) >= FIVE_MINUTES) {
            return handleNoAuth(response);
        }
        // 实际情况中是从数据库中查出 secretKey
        String secretKey = invokeUser.getSecretKey(); // 从数据库查询到用户的secretKey
        String serverSign = SignUtils.genSign(body, secretKey); // 服务端生成的签名
        if (sign == null || !sign.equals(serverSign)) { // 如果服务端生成的签名和前端生成的签名不一致，就直接报错
            return handleNoAuth(response);
        }
        // 4. 请求的模拟接口是否存在，以及请求方法是否匹配
        InterfaceInfo interfaceInfo = null;
        try {
            // 因为这个网关项目没有引入mybatis，所以这里用了Dubbo远程调用的方法去调用远程backend增删改查项目提供的接口
            interfaceInfo = innerInterfaceInfoService.getInterfaceInfo(path, method); // 根据请求路径和请求方法获取接口的信息
        } catch (Exception e) {
            log.error("getInterfaceInfo error", e);
        }
        if (interfaceInfo == null) { // 接口信息为空，无权限
            return handleNoAuth(response);
        }
        // 5.请求的模拟接口是否存在
        // TODO: 2023/1/18 从数据库中查询模拟接口是否存在，以及请求方法是否匹配(还可以校验请求参数)
        // 因为网关项目没引入MyBatis等操作数据库的类库，如果该操作较为复杂，可以由backend增删改查项目提供接口，我们直接调用，不用再重复写逻辑了
        //   (1)HTTP请求(用HTTPClient、用RestTemplate、Feign)
        //   (2)RPC(Dubbo)

        // 6.请求转发，调用模拟接口
        /**
         * DEBUG之后发现问题：预期是等模拟接口调用完成，才记录响应日志、统计调用次数。但现实是chain.filter方法立刻返回了，直到filter过滤器return之后才调用了模拟接口
         * 原因是：chain.filter是个异步操作，理解为前端的promised
         *    从官网的网关图可以发现，只有等所有的过滤器都执行完，我们才会执行代理的接口
         *
         * 解决方案：利用response装饰者，增强原有response的处理能力，就是下面定义的handleResponse()方法
         */
//        Mono<Void> filter = chain.filter(exchange); // 调用责任链方法chain.filter()，会发现上面的问题
        // TODO: 2023/1/23 是否还有调用次数，一定要放在发送请求前
        return handleResponse(exchange, chain, interfaceInfo.getId(), invokeUser.getId());
    }

    /**
     * 装饰者设计模式：利用response装饰者，增强原有response的处理能力
     * 自定义处理的装饰器：给response对象做了增强
     *  处理响应：
     * DEBUG之后发现问题：预期是等模拟接口调用完成，才记录响应日志、统计调用次数。但现实是chain.filter方法立刻返回了，直到filter过滤器return之后才调用了模拟接口
     * 原因是：chain.filter是个异步操作，理解为前端的promised
     *    从官网的网关图可以发现，只有等所有的过滤器都执行完，我们才会执行代理的接口
     *
     * 解决方案：利用response装饰者，增强原有response的处理能力
     */
    public Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain, long interfaceInfoId, long userId) {
        try {
            ServerHttpResponse originalResponse = exchange.getResponse(); // 拿到request请求体，再通过request去拿到相应的信息
            // 缓存数据的工厂
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            // 拿到响应码
            HttpStatus statusCode = originalResponse.getStatusCode();
            if (statusCode == HttpStatus.OK) {
                // 装饰后的response对象，增强能力，下面重写的函数说明了如何增强能力
                ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                    /**
                     * 当我们调用完接口之后，等他返回了结果之后，就会调用这个writeWith()方法，我们就可以根据这个响应结果做一些自己的处理逻辑
                     * 比如：
                     *   调用成功：接口调用次数+1  invokeCount
                     *   调用失败：返回一个规范的错误码
                     * @param body 调用接口后的响应对象
                     * @return Mono就是返回一个响应式的对象
                     */
                    @Override
                    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                        log.info("body instanceof Flux: {}", (body instanceof Flux));
                        // 如果调用的接口是反应式的Flux，就必须从这个反应式对象body中拿到真实的值fluxBody，如果调用的接口返回的是真实的值，就直接从body中拿到结果直接响应出去
                        if (body instanceof Flux) { // 能成功返回的话就返回装饰过的response，否则降级返回原来的response(按原来的方式去调用)
                            Flux<? extends DataBuffer> fluxBody = Flux.from(body); // 拿到真正的body，fluxBody就是我们调用接口之后响应的值
                            // 往返回值里写数据，拼接字符串
                            return super.writeWith(
                                    fluxBody.map(dataBuffer -> { // 把DataBuffer缓冲区里面的数据取出来一点一点拼接成字符串之后返回，因为缓冲区里面的数据并不是字符串
                                        // 8. TODO: 2023/1/18 调用成功，接口调用次数+1 invokeCount
                                        try {
                                            innerUserInterfaceInfoService.invokeCount(interfaceInfoId, userId); // 接口调用成功，调用次数+1
                                        } catch (Exception e) {
                                            log.error("invokeCount error", e);
                                        }
                                        byte[] content = new byte[dataBuffer.readableByteCount()];
                                        dataBuffer.read(content); // content就是我们原本的调用接口返回的数据
                                        DataBufferUtils.release(dataBuffer);// 释放掉内存
                                        // 构建日志
                                        StringBuilder sb2 = new StringBuilder(200);
                                        List<Object> rspArgs = new ArrayList<>();
                                        rspArgs.add(originalResponse.getStatusCode());
                                        String data = new String(content, StandardCharsets.UTF_8); // ata
                                        sb2.append(data);
                                        // 打印日志
                                        log.info("响应结果：" + data);
                                        return bufferFactory.wrap(content); // 利用工厂进行加工把零散的内容拼接成字符串
                                    }));
                        } else {
                            // 9.调用失败，返回一个规范的错误码
                            log.error("<--- {} 响应code异常", getStatusCode());
                        }
                        return super.writeWith(body); // 最后直接返回body
                    }
                };
                // 设置 response 对象为装饰过的
                return chain.filter(exchange.mutate().response(decoratedResponse).build());
            }
            return chain.filter(exchange); // 降级处理返回数据
        } catch (Exception e) {
            log.error("网关处理响应异常" + e);
            return chain.filter(exchange);
        }
    }

    /**
     * 请求接口会调用多个过滤器，我们可以通过这个方法去编排各个过滤器的优先级，先拦截哪个后拦截哪个
     * @return
     */
    @Override
    public int getOrder() {
        return -1;
    }

    /**
     * 没有权限
     * 访问拒绝，直接返回一个状态码然后拦截掉
     * @param response
     * @return
     */
    public Mono<Void> handleNoAuth(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.setComplete();
    }

    /**
     * 调用次数失败
     * @param response
     * @return
     */
    public Mono<Void> handleInvokeError(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return response.setComplete();
    }
}