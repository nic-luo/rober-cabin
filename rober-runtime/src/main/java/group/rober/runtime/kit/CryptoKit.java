package group.rober.runtime.kit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public abstract class CryptoKit{
    protected static Logger logger = LoggerFactory.getLogger(CryptoKit.class);

    public static final String getMD5(File file){
        String fileMD5 = "";
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            fileMD5 = CryptoKit.getMD5(fileInputStream);
            fileInputStream.close();
        } catch (IOException e) {
            logger.error("",e);
        }finally {
            IOKit.close(fileInputStream);
        }
        return fileMD5;
    }

    public static final String getMD5(InputStream inputStream) {

        MessageDigest digest = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            while ((len = inputStream.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
        } catch (NoSuchAlgorithmException e) {
            logger.error("",e);
        } catch (FileNotFoundException e) {
            logger.error("",e);
        } catch (IOException e) {
            logger.error("",e);
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16).substring(8, 24);
    }
}
