package org.tutor.auth.units;

import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tutor.auth.common.SignKeyCommon;
import org.tutor.auth.entity.SignKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.*;

/**
 * 非对称加密算法工具类
 * @author Eugene-Forest
 * {@code @date} 2024/11/27
 */
public class SignKeyUnits {

    public static final String Key_Algorithm = "RSA";
    public static final String Sign_Algorithm = "SHA256withRSA";

    private static Logger log = LoggerFactory.getLogger(SignKeyUnits.class);

//    RSA 算法的执行速度较慢，尤其是对于大型密钥和数据。
//    因此，在实际应用中，通常使用 RSA 算法来加密对称密钥（如 AES），然后使用对称加密算法加密实际的数据，以提高效率。

    public static SignKey createSignKey(String algorithm, int keySize) {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(algorithm);
            //初始化密匙长度
            // 注意，RSA 密钥长度位数必须是 512 的倍数，最小为 512，密钥长度越长，越安全（难破解），
            // 但是 密钥的生成、加密、解密都会更加的耗时。一般来说，目前最常见、安全的 RSA 密钥长度为 2048 位。
            keyPairGenerator.initialize(keySize);
            //生成密匙对
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            //得到公匙
            Key publicKey = keyPair.getPublic();
            //得到密匙
            Key privateKey = keyPair.getPrivate();
            String privateKeyString = Base64.encodeBase64String(privateKey.getEncoded());
            String publicKeyString = Base64.encodeBase64String(publicKey.getEncoded());
            //创建密匙对对象
            SignKey signKey = new SignKey();
            signKey.setPrivateKey(privateKeyString);
            signKey.setPublicKey(publicKeyString);
            return signKey;
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("No such algorithm: " + algorithm);
        } catch (InvalidParameterException e) {
            throw new IllegalArgumentException("Invalid key size: " + keySize + " for " + algorithm);
        }
    }

    public static SignKey createSignKeyByRSA() {
        return createSignKey(Key_Algorithm, 1024);
    }

    /**
     * RSA 签名
     *
     * @param data 被签名数据
     * @return 签名
     */
    public static String signWithRSA(String data) {
        try {
            PrivateKey privateKey = SignKeyCommon.getPrivateKey("TestKey", Key_Algorithm);
            if (privateKey == null) {
                log.warn("sign With RSA - Private key is null");
                return null;
            }
            Signature signature = Signature.getInstance(Sign_Algorithm);
            signature.initSign(privateKey);
            signature.update(data.getBytes(StandardCharsets.UTF_8));
            byte[] signatureBytes = signature.sign();
            return Base64.encodeBase64String(signatureBytes);
        } catch (NoSuchAlgorithmException e) {
            log.error("签名方法 使用了非法加密算法！");
        } catch (InvalidKeyException e) {
            log.error("sign With RSA - Invalid key: {}", e.getMessage());
        } catch (SignatureException e) {
            log.error("sign With RSA - Invalid signature: {}", e.getMessage());
        }
        return null;
    }

    /**
     * RSA 验签
     *
     * @param data 验签内容
     * @param sign 签名
     * @return 验签结果
     */
    public static boolean verifySignWithRSA(String data, String sign) {
        try {
            PublicKey publicKey = SignKeyCommon.getPublicKey("TestKey", Key_Algorithm);
            if (publicKey == null) {
                log.warn("verify Sign With RSA - Public key is null");
                return false;
            }
            Signature signature = Signature.getInstance(Sign_Algorithm);
            signature.initVerify(publicKey);
            signature.update(data.getBytes(StandardCharsets.UTF_8));
            byte[] signBytes = Base64.decodeBase64(sign.getBytes(StandardCharsets.UTF_8));
            return signature.verify(signBytes);
        } catch (NoSuchAlgorithmException e) {
            log.error("验签方法 使用了非法加密算法！");
        } catch (InvalidKeyException e) {
            log.error("verify Sign With RSA - Invalid key: {}", e.getMessage());
        } catch (SignatureException e) {
            log.error("verify Sign With RSA - Invalid signature: {}", e.getMessage());
        }
        return false;
    }

    /**
     * RSA 公匙加密
     *
     * @param data 明文；数据
     * @return 密文；加密数据（Base64编码）
     */
    public static String encryptWithRSA(String data) {
        try {
            PublicKey publicKey = SignKeyCommon.getPublicKey("TestKey", Key_Algorithm);
            if (publicKey == null) {
                log.warn("encrypt With RSA - Public key is null");
                return null;
            }
            Cipher cipher = Cipher.getInstance(Key_Algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] enBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeBase64String(enBytes);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            log.error("加密方法 使用了非法加密算法！");
        } catch (InvalidKeyException e) {
            log.error("encrypt With RSA - Invalid key: {}", e.getMessage());
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            log.error("encrypt With RSA - Invalid : {}", e.getMessage());
        }
        return null;
    }

    /**
     * RSA 私匙解密
     *
     * @param encryptedData 加密数据(Base64编码)
     * @return 解密数据
     */
    public static String decryptWithRSA(String encryptedData) {
        try {
            PrivateKey privateKey = SignKeyCommon.getPrivateKey("TestKey", Key_Algorithm);
            if (privateKey == null) {
                log.warn("decrypt With RSA - Private key is null");
                return null;
            }
            Cipher cipher = Cipher.getInstance(Key_Algorithm);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] enBytes = cipher.doFinal(Base64.decodeBase64(encryptedData));
            return new String(enBytes, StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            log.error("解密方法 使用了非法加密算法！");
        } catch (InvalidKeyException e) {
            log.error("decrypt with RSA - Invalid key: {}", e.getMessage());
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            log.error("decrypt with RSA - Invalid : {}", e.getMessage());
        }
        return null;
    }
}