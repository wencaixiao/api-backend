package com.xiaowc.apiclientsdk;

import com.xiaowc.apiclientsdk.client.ApiClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 开发一个简单易用的SDK
 *   理想情况：开发者只需要关心调用哪些接口、传递哪些参数，就跟调用自己写的代码一样简单
 * 开发starter的好处：开发者引入之后，可以直接在application.yml中写配置，自动创建客户端
 */
@Configuration // 表明这是一个配置类
@ConfigurationProperties("api.client") // 作用是读取application的配置，读取到可以设置到这个类的属性中
@Data
@ComponentScan // 扫描包
public class ApiClientConfig {

    /**
     * 用户的标识
     */
    private String accessKey;

    /**
     * 用户的密钥
     */
    private String secretKey;

    /**
     * 手动将ApiClient交给spring去管理
     * @return
     */
    @Bean
    public ApiClient ApiClient() {
        return new ApiClient(accessKey, secretKey);
    }

}
