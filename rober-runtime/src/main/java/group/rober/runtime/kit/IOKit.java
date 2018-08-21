package group.rober.runtime.kit;

//import group.rober.runtime.io.ByteArrayBuffer;
import group.rober.runtime.io.ByteInputStream;
import group.rober.runtime.io.ByteOutputStream;
import group.rober.runtime.lang.RoberException;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

/**
 * IO常用工具类
 * Created by tisir yangsong158@qq.com on 2017-02-19
 */
public abstract class IOKit extends IOUtils {
    public static void close(Closeable stream){
        try {
            if (stream != null) stream.close();
        } catch (IOException e) {
            new RoberException("close {0} error",stream.getClass().getName());
        }
    }
    public static void close(AutoCloseable stream){
        try {
            if (stream != null) stream.close();
        } catch (Exception e) {
            new RoberException("close {0} error",stream.getClass().getName());
        }
    }

    /**
     * 把流转为字符数组缓冲区
     *
     * @param inputStream  inputStream
     * @return ByteOutputStream
     * @throws IOException IOException
     */
    public static ByteOutputStream convertToByteArrayBuffer(InputStream inputStream) throws IOException {
        ByteOutputStream byteBuffer = new ByteOutputStream();

        int bufferSize = 1024 * 4;
        byte[] buffer = new byte[bufferSize];
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer;
    }

    /**
     * 将输入流中的数据写入字节数组
     *
     * @param inputStream inputStream
     * @param isClose isClose
     * @return byte[]
     * @throws IOException IOException
     */
    @SuppressWarnings("deprecation")
    public static byte[] convertToBytes(InputStream inputStream, boolean isClose) throws IOException {
        ByteOutputStream byteBuffer = convertToByteArrayBuffer(inputStream);
        if(isClose)IOKit.close(inputStream);
//        return byteBuffer.toByteArray();
        return byteBuffer.toBytes();
    }

    /**
     * 把流转为二进制
     *
     * @param inputStream inputStream
     * @return byte
     * @throws IOException IOException
     */
    public static byte[] convertToBytes(InputStream inputStream) throws IOException {
        return convertToBytes(inputStream,false);
    }

    /**
     * 转成二进制流
     *
     * @param inputStream inputStream
     * @return ByteInputStream
     * @throws IOException IOException
     */
    public static ByteInputStream convertToByteArrayInputStream(InputStream inputStream) throws IOException {
        ByteOutputStream byteBuffer = convertToByteArrayBuffer(inputStream);
        return byteBuffer.createInputStream();
    }

    /**
     * 把二进制转为输入流
     *
     * @param bytes bytes
     * @return InputStream
     */
    public static InputStream convertToInputStream(byte[] bytes) {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        return bis;
    }

}
