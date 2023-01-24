package com.xiaowc.apiinterface;

import com.xiaowc.apiclientsdk.client.ApiClient;
import com.xiaowc.apiclientsdk.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class ApiInterfaceApplicationTests {

    @Resource
    private ApiClient ApiClient;

    @Test
    void contextLoads() {
        String result = ApiClient.getNameByGet("xiaowc");
        User user = new User();
        user.setUsername("lixiaowc");
        String usernameByPost = ApiClient.getUsernameByPost(user);
        System.out.println(result);
        System.out.println(usernameByPost);
    }

}
