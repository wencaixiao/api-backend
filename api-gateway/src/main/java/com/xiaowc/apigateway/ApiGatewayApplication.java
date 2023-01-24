package com.xiaowc.apigateway;

import com.xiaowc.project.provider.DemoService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

@SpringBootApplication(exclude = { // 排除指定加载的类，不让他加载关于数据库的类，因为我们这里是使用RPC调用的是远程的方法
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class})  // 禁用数据库，因为引入了mybatis，但是我们没有配置数据库，所以这里要警用数据库
@EnableDubbo
@Service
public class ApiGatewayApplication {

    @DubboReference
    private DemoService demoService;

    public static void main(String[] args) {

        ConfigurableApplicationContext context = SpringApplication.run(ApiGatewayApplication.class, args);
        ApiGatewayApplication application = context.getBean(ApiGatewayApplication.class);
        String result = application.doSayHello("world");
        String result2 = application.doSayHello2("world");
        System.out.println("result: " + result);
        System.out.println("result: " + result2);
    }

    public String doSayHello(String name) {
        return demoService.sayHello(name);
    }

    public String doSayHello2(String name) {
        return demoService.sayHello2(name);
    }

//    @SpringBootApplication
//    public class DemogatewayApplication {
//
//        /**
//         * 这里就是定义了一个路由器
//         * 核心概念：
//         *   路由：根据什么条件，转发请求到哪里
//         *   断言：一组规则、条件，用来确定如何转发路由
//         *   过滤器：对请求进行一序列的处理，比如添加请求头、添加请求参数
//         *
//         * spring cloud gatewat 流程：
//         *   1.客户端发起请求
//         *   2.Handler Mapping：根据断言，去将请求转发到对于的路由
//         *   3.Web Handler：处理请求(一层层经过过滤器)
//         *   4.实际调用服务
//         * 两种配置方式：
//         *   1.配置式(方便、规范|)
//         *   2.编程式(灵活、相对)
//         *
//         * 这里使用的是spring cloud gateway的编程式
//         * @param builder
//         * @return
//         */
//        @Bean
//        public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
//            return builder.routes()
//                    .route("tobaidu", r -> r.path("/baidu")  // 这里就是一个断言，访问/get，就将请求转发到uri中定义的路径中
//                            .uri("http://baidu.com"))
//                    .route("totaobao", r -> r.path("/taobao")
//                            .uri("http://ai.taobao.com"))
//                    .build();
//        }
//    }

}
