package com.example.gooch.lib_get_img;

import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Collector;
import org.jsoup.select.Elements;
import org.jsoup.select.Evaluator;
import org.jsoup.select.NodeFilter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;

public class Download {
    public static void main(String[] args) {
//        parseHtml("https://mp.weixin.qq.com/s/PPv8qbG8t3zzI-2sUUcPfw");
//        parseHtml("https://mp.weixin.qq.com/s/gxdqUgmk91916IjEi9OYjw");
//        parseHtml("https://mp.weixin.qq.com/s/E-Pj2Sm4J2u0AI5X3h1sjg");
        parseHtml("https://mp.weixin.qq.com/s/Gym4c_VtCbFfk70Ow8eEIg");
//        parseHtml("https://mp.weixin.qq.com/s/KEcZEwWGAfbJb_mw4ttWdA");
    }

    private static void parseHtml(String url) {
        try {
            Document doc = Jsoup.connect(url)
                    .data("query", "Java")
                    .userAgent("Mozilla")
                    .ignoreContentType(true)
                    .cookie("auth", "token")
                    .timeout(3000)
                    .get();
            List<ImageBean> imageList = new ArrayList<>();
            Elements richMediaContent = doc.getElementsByClass("rich_media_content");
            Element richElement = richMediaContent.get(0);
            Elements elementsByAttribute = getElementsByAttribute("data-src", "src", richElement);
//            Elements elementsByAttribute = doc.getElementsByAttribute("data-src");
            Elements richMediaTitle = doc.getElementsByClass("rich_media_title");
            String title = richMediaTitle.get(0).text();
            for (Element element : elementsByAttribute) {
                String attr = element.attr("data-src");
                String imgType = element.attr("data-type");
                ImageBean imageBean = new ImageBean();
                imageBean.src = attr;
                imageBean.type = imgType;
                System.out.println(attr);
                if (attr != null && attr.startsWith("http")) {
                    imageList.add(imageBean);
                } else {
                    String imgSrc = element.attr("src");
                    imageBean.src = imgSrc;
                    imageBean.type = imgType;
                    System.out.println(imgSrc);
                    if (imgSrc != null && imgSrc.startsWith("http")) {
                        imageList.add(imageBean);
                    }
                }
            }
            downLoadImage(title, imageList);
//            Elements elements = richMediaContent.tagName("p");
//            Element p = elements.first();
//            if (p != null) {
//                Elements imgData = elements.get(0).children();
//                for (Element element : imgData) {
//                    Elements children = element.children();
//                    for (Element child : children) {
//                        String href = child.attr("href");
//                        if (href!=null&&href.startsWith("http")) {
//                            System.out.println(href);
//                            parseHtml(href);
//                            continue;
//                        }
//
//                        Element imgElement = child.tagName("img");
//                        String img = imgElement.attr("data-src");
//                        System.out.println(img);
//                    }
//                }
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void downLoadImage(String title, List<ImageBean> imageList) {
        for (int i = 0; i < imageList.size(); i++) {
            try {
                download(title, imageList.get(i), i);
            } catch (Exception e) {
                System.out.println("image" + (i + 1) + "download error");
                e.printStackTrace();
            }
        }
    }

    public static void download(String title, ImageBean imageBean, int i) throws Exception {
        // 构造URL
        URL url = new URL(imageBean.src);
        // 打开连接
        URLConnection con = url.openConnection();
        // 输入流
        InputStream is = con.getInputStream();
        // 1K的数据缓冲
        byte[] bs = new byte[1024];
        // 读取到的数据长度
        int len;
        // 输出的文件流
        String filename = title + "_" + (i + 1) + "." + imageBean.type;  //下载路径及下载图片名称
        File file = new File("E:\\image_download\\" + title, filename);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        } else {
            file.delete();
        }
        file.createNewFile();
        FileOutputStream os = new FileOutputStream(file, true);
        // 开始读取
        while ((len = is.read(bs)) != -1) {
            os.write(bs, 0, len);
            os.flush();
        }
        System.out.println(i + 1 + "download complete");
        // 完毕，关闭所有链接
        os.close();
        is.close();
    }

    public static Elements getElementsByAttribute(String key1, String key2, Element root) {
        Validate.notEmpty(key1);
        key1 = key1.trim();
        Validate.notEmpty(key2);
        key2 = key2.trim();

        return Collector.collect(new Attribute(key1, key2), root);
    }

    static final class Attribute extends Evaluator {
        private String key1;
        private String key2;

        public Attribute(String key1, String key2) {
            this.key1 = key1;
            this.key2 = key2;
        }

        @Override
        public boolean matches(Element root, Element element) {
            return element.hasAttr(key1) || element.hasAttr(key2);
        }

        @Override
        public String toString() {
            return String.format("[%s]", key1 + key2);
        }
    }

    static class ImageBean {
        public String src;
        public String type;
    }
}
