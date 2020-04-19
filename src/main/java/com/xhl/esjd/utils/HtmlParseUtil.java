package com.xhl.esjd.utils;

import com.xhl.esjd.pojo.Content;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author MuYan
 * @date 2020-04-19 19:44
 */
@Component
public class HtmlParseUtil {
   /* public static void main(String[] args)throws Exception {
        new HtmlParseUtil().parseJD("java").forEach(System.out::println);
    }*/

    public List<Content> parseJD(String keyWords)throws Exception {
        //获取请求 https://search.jd.com/Search?keyword=java
        String url="https://search.jd.com/Search?keyword="+keyWords;
        //解析网页
        Document parse = Jsoup.parse(new URL(url), 30000);
        //获取div
        Element element = parse.getElementById("J_goodsList");
        //获取所有的li元素
        Elements elements = element.getElementsByTag("li");
        ArrayList<Content> list = new ArrayList<>();
        for (Element el : elements) {
            //获取不到图片的问题，网页图片多的时候，所有的图片都是延迟加载,原因是懒加载
            String img = el.getElementsByTag("img").eq(0).attr("source-data-lazy-img");
            String price = el.getElementsByClass("p-price").eq(0).text();
            String title = el.getElementsByClass("p-name").eq(0).text();
            Content content = Content.builder().img(img).price(price).title(title).build();
            list.add(content);

        }
        return list;
    }
}
