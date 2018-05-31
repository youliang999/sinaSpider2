package com.youliang;

import net.coobird.thumbnailator.Thumbnails;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class Pic2Min {
    private static final String BASE_DIR = "D:\\reducePics\\";
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        String url = "https://f1.dajieimg.com/n/micro_blog/T1Tph_B4bT1RXrhCrK_c.jpg";
        String name = uploadQianURL(url);
        System.out.println("fileName: " + name);
        String filePath = BASE_DIR  + name;

        System.out.println("cost " + (System.currentTimeMillis() - start) + " ms");
    }

    public static String uploadQianURL(String fileUrl) {
        //获取文件名，文件名实际上在URL中可以找到
        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/"), fileUrl.lastIndexOf(".")) + ".jpg";
        //这里服务器上要将此图保存的路径
        String savePath = "D:\\reducePics\\";// getRequest().getRealPath(UPLOAD_PATH);
        try {
            URL url = new URL(fileUrl);/*将网络资源地址传给,即赋值给url*/
            /*此为联系获得网络资源的固定格式用法，以便后面的in变量获得url截取网络资源的输入流*/
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            DataInputStream in = new DataInputStream(connection.getInputStream());
            try {

                Thumbnails.of(in)
                        .scale(0.5)
                        .outputQuality(0.5)
                        .toFile(BASE_DIR + "min" + fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("+++++++++++++++++++++++++++++++++++++++++");
            /*此处也可用BufferedInputStream与BufferedOutputStream*/
            DataOutputStream out = new DataOutputStream(new FileOutputStream(BASE_DIR+fileName));
            /*将参数savePath，即将截取的图片的存储在本地地址赋值给out输出流所指定的地址*/
            byte[] buffer = new byte[4096];
            int count = 0;
            /*将输入流以字节的形式读取并写入buffer中*/
            while ((count = in.read(buffer)) > 0) {
                out.write(buffer, 0, count);
            }
            out.close();/*后面三行为关闭输入输出流以及网络资源的固定格式*/
            in.close();
            connection.disconnect();
            //返回内容是保存后的完整的URL
            return fileName;/*网络资源截取并存储本地成功返回true*/

        } catch (Exception e) {
            System.out.println(e + fileUrl + savePath);
            return null;
        }
    }


}
