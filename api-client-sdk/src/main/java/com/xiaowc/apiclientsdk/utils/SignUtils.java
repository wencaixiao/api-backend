package com.xiaowc.apiclientsdk.utils;

import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;

/**
 * API签名工具
 */
public class SignUtils {
    /**
     * 生成签名
     * @param body
     * @param secretKey
     * @return
     */
    public static String genSign(String body, String secretKey) {
        Digester md5 = new Digester(DigestAlgorithm.SHA256); // 指定加密的算法：摘要加密算法
        String content = body + "." + secretKey; // 要加密的内容
        return md5.digestHex(content);
    }
}
