package xyz.tcreopargh.textconverter;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

/**
 * Created by lu on 2017/11/10 19:34
 */
public class AESUtils {


    /**
     * 生成一个key。该key用于加密明文与解密密文
     * @param password 口令
     * @param salt 盐值（密码学中的“颜值”，这玩意黑黑的）
     * @return 密钥串
     */
    public static String generateKey(String password, String salt) {
        try {
            AesCbcWithIntegrity.SecretKeys key = AesCbcWithIntegrity.generateKeyFromPassword(password, salt);
            if (key != null) {
                return key.toString();
            }
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String getEnString(String clearText, String keyStr) {
        return getEnString(clearText, keyStr, "utf-8");
    }

    /**
     * aes字符串加密
     *
     * @param clearText 待加密的明文
     * @param keyStr    密钥串
     * @param charset   字符串编码，utf-8，gbk等
     * @return 加密String（base64Iv And Cipher text）
     */
    public static String getEnString(String clearText, String keyStr, String charset) {
        try {
            AesCbcWithIntegrity.SecretKeys key = AesCbcWithIntegrity.keys(keyStr);
            AesCbcWithIntegrity.CipherTextIvMac cipherTextIvMac = AesCbcWithIntegrity.encrypt(clearText, key, charset);
            return cipherTextIvMac.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            return "加密失败！错误信息" + e.toString();
        }
        return null;
    }

    public static String getDeString(String cipherText, String keyStr) {
        return getDeString(cipherText, keyStr, "utf-8");
    }

    /**
     * aes解密
     *
     * @param cipherText 已使用aes加密的密文（base64Iv And Cipher text）
     * @param keyStr     密钥串
     * @param charset    字符串编码，utf-8，gbk等
     * @return 解密结果，明文
     */
    public static String getDeString(String cipherText, String keyStr, String charset) {
        try {
            AesCbcWithIntegrity.SecretKeys key = AesCbcWithIntegrity.keys(keyStr);
            AesCbcWithIntegrity.CipherTextIvMac cipherTextIvMac = new AesCbcWithIntegrity.CipherTextIvMac(cipherText);
            return AesCbcWithIntegrity.decryptString(cipherTextIvMac, key, charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            return "密码错误！";
        }
        return null;
    }
}