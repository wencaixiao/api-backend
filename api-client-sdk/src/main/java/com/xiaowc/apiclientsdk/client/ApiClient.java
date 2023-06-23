package com.xiaowc.apiclientsdk.client;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.xiaowc.apiclientsdk.model.User;


import java.util.HashMap;
import java.util.Map;

import static com.xiaowc.apiclientsdk.utils.SignUtils.genSign;

/**
 * API签名认证是一个很灵活的设计，具体要有哪些参数，参数名如何一定要根据场景来。(比如userId,appId,version,固定值等等)
 *
 * 客户端：
 *
 * 调用第三方接口的客户端，利用hutool工具的HTTP客户端
 *
 * 在这里调用NameController这个类中的接口
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
 *        用户参数+密钥 -> 签名生成算法(MD5、HMac、Sha1) -> 不可解密的值
 *        abc+12345678 -> aophegepwohgao
 *   服务端怎么知道你的这个签名对不对？
 *     服务端用一模一样的参数和算法去生成签名，只要和用户传的一致，就标识这个签名对
 *
 *  一个标准的API签名认证算法要加以下5个参数：
 *      1.参数1：accessKey用户的标识
 *      2.参数2：secretKey密钥(这个参数不传递，不放到请求头中，不能直接发送给后端，因为不安全)
 *      3.参数3：用户参数，就是请求地址那个参数(建议也放入，这样会更严格)
 *    密钥不能直接在服务器之间传递，因为如果别人拦截到了你这个请求，也可以像上面一样从你的请求头中拿到密钥，不安全
 *      4.参数4：sign对密钥再次进行加密的参数
 *    怎么防重放？
 *      5.参数5：加nonce随机数，只能用一次，如果拿之前的随机数访问重放，后端就不认识，因为你之前用这个随机数访问过了
 *        缺点：服务端要保存用过的随机数，
 *      6.参数6：加timestamp时间戳，校验时间戳是否过期。可以和上面打配合，比如5分钟过期，就将上面保存的随机数删除掉
 */
public class ApiClient {

    /**
     * 将客户端的调用地址改成请求网关地址localhost:8090，这样请求只要经过网关都要执行这个自定义的过滤器的逻辑
     * 网关再将这个调用地址转发给localhost:8123去调用api-interface
     */
    private static final String GATEWAY_HOST = "http://localhost:8090";

    /**
     * 调用的标识
     */
    private String accessKey;

    /**
     * 密钥
     */
    private String secretKey;

    /**
     * 构造函数
     * @param accessKey
     * @param secretKey
     */
    public ApiClient(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    /**
     * 利用hutool工具的HTTP客户端调用NameController这个类中的getNameByGet接口方法
     * 这样就不用每次从地址栏去输入要请求的地址了
     */
    public String getNameByGet(String name) {
        // 可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);
        String result = HttpUtil.get(GATEWAY_HOST + "/api/name/get", paramMap); // GET请求
        System.out.println(result);
        return result;
    }

    /**
     * 利用hutool工具的HTTP客户端调用NameController这个类中的getNameByPost接口方法
     * 这样就不用每次从地址栏去输入要请求的地址了
     */
    public String getNameByPost(String name) {
        // 可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);
        String result = HttpUtil.post(GATEWAY_HOST + "/api/name/post", paramMap); // POST请求
        System.out.println(result);
        return result;
    }

    /**
     * 将生成签名认证所需要的5个参数放入map中，便于传到请求头中
     * @return
     */
    private Map<String, String> getHeaderMap(String body) {
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("accessKey", accessKey); // 用户的标识
        // 一定不能直接发送
//        hashMap.put("secretKey", secretKey);
        hashMap.put("nonce", RandomUtil.randomNumbers(4)); // 随机数
        hashMap.put("body", body); // 用户参数
        hashMap.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000)); // 时间戳
        // 对密钥再次进行加密的参数，secretKey是前端用户输入的参数，这个sign是客户端加密后的sign，
        hashMap.put("sign", genSign(body, secretKey));
        return hashMap;
    }

    /**
     * 利用hutool工具的HTTP客户端调用NameController这个类中的getUsernameByPost接口方法
     * 这样就不用每次从地址栏去输入要请求的地址了
     * @param user 前端向后端传递的内容
     */
    public String getUsernameByPost(User user) {
        String json = JSONUtil.toJsonStr(user); // 将User对象转换成json字符串
        // 得到一个响应对象，里面有状态码、响应体之类的东西
        HttpResponse httpResponse = HttpRequest.post(GATEWAY_HOST + "/api/name/user")
                .addHeaders(getHeaderMap(json)) // 将accessKey和secretKey放入到请求头中
                .body(json) // 将json字符串当作请求体传过去，后序在request中可以取到这个请求头信息
                .execute(); // 执行这个post请求
        System.out.println(httpResponse.getStatus());
        String result = httpResponse.body();
        System.out.println(result);
        return result;
    }
}
