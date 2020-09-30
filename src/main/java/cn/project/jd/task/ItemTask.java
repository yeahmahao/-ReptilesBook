package cn.project.jd.task;

import cn.project.jd.pojo.Item;
import cn.project.jd.service.ItemServie;
import cn.project.jd.util.HttpUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.List;


@Component
public class ItemTask {

    @Autowired
    private HttpUtils hus;
    @Autowired
    private ItemServie is;

    public static final ObjectMapper MAPPER = new ObjectMapper();
    /**
     * 每隔多久执行一次任务
     * 定时任务
     * @throws Exception
     */
    //每隔多久执行一次任务
//    @Scheduled(fixedDelay = 100*1000)
//    public void itemTask()throws Exception{
//        //声明需要解析的初始地址 https://search.jd.com/Search?keyword=%E6%89%8B%E6%9C%BA&wq=%E6%89%8B%E6%9C%BA&page=1&s=1&click=0
//        String url ="https://search.jd.com/Search?keyword=%E6%89%8B%E6%9C%BA&wq=%E6%89%8B%E6%9C%BA&s=51&click=0&page=";
//        //按照页码对搜索结果进行遍历解析
//        for(int i = 1;i < 4 ;i = i+2){
//           String html = hus.doGetHtml(url+i);
//           //解析页面获取商品数据并存储
//            this.parse(html);
//        }
//        System.out.println("手机数据抓取完成");
//    }

    /**
     * 解析页面获取商品数据并存储
     * @param html
     */
    private void parse(String html) throws IOException {
        //解析html获取dom对象
        Document doc =  Jsoup.parse(html);
        //获取spu
        Elements spus =  doc.select("div#J_goodsList > ul > li");
        for (Element element : spus) {
            long spu = 0;
            //获取spu
            if(element.attr("data-spu").length() < 1 ){

                spu = 0;
            }else {
                spu = Long.parseLong(element.attr("data-spu"));
            }

           Elements skus = element.select("li.ps-item");
           //对sku的元素进行遍历
            for (Element skue : skus) {
                //获取sku
                long sku = Long.parseLong(skue.select("[data-sku]").attr("data-sku"));
                //根据sku查询数据 如果存在就进行下一个循环，该商品不保存
                Item item = new Item();
                item.setSku(sku);
                List<Item> list = this.is.findall(item);
                if(list.size() > 0){
                    continue;
                }
                //设置商品spu
                item.setSpu(spu);
                //获取商品详情的url https://item.jd.com/100008607171.html
                String itemUrl = "https://item.jd.com/"+sku+".html";
                item.setUrl(itemUrl);
                //图片
                String picurl = "https:"+skue.select("img[data-sku]").first().attr("data-lazy-img");
                picurl = picurl.replace("/n9/","/n1/");

                String newimg = this.hus.downImg(picurl);
                item.setPic(newimg);

                //价格
                String imghttp  = "https://p.3.cn/prices/mgets?skuIds=J_"+sku;
                String imgJson = this.hus.doGetHtml(imghttp);
                //解析json得到价格
                double price =MAPPER.readTree(imgJson).get(0).get("p").asDouble();
//                skue.select("");
                item.setPrice(price);

                //标题
                String itemHtml = this.hus.doGetHtml(item.getUrl());
                String title = Jsoup.parse(itemHtml).select("div.sku-name").text();
                item.setTitle(title);
                //时间
                item.setCreated(new Date());
                //修改时间
                item.setUpdated(item.getCreated());
                //商品地址
                item.setUrl("https://item.jd.com/" + sku + ".html");
                this.is.save(item);
            }
        }

    }
}
