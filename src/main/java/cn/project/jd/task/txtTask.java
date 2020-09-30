package cn.project.jd.task;


import cn.project.jd.util.HttpUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import java.io.*;
import java.util.UUID;

@Component
public class txtTask {

    @Autowired
    private HttpUtils hus;


    /**
     * 每隔多久执行一次任务
     * 定时任务
     * @throws Exception
     */
    //每隔多久执行一次任务
    @Scheduled(fixedDelay = 100000*1000)
    public void itemTask()throws Exception{
        //声明需要解析的初始地址 https://search.jd.com/Search?keyword=%E6%89%8B%E6%9C%BA&wq=%E6%89%8B%E6%9C%BA&page=1&s=1&click=0
//        https://www.qidian.com/all?chanId=21&action=1&orderId=&page=1&vip=0&style=1&pageSize=20&siteid=1&pubflag=0&hiddenField=0   完本玄幻
//        https://www.qidian.com/all?chanId=22&action=1&orderId=&page=1&vip=0&style=1&pageSize=20&siteid=1&pubflag=0&hiddenField=0
//        String url ="https://www.qidian.com/all?chanId=21&subCateId=8";
        String url ="https://www.qidian.com/all?chanId=10&subCateId=26&size=2&orderId=&page=1&vip=0&style=1&pageSize=20&siteid=1&pubflag=0&hiddenField=0&page=";
        for(int i = 1;i < 3 ;i ++){
            String html = hus.doGetHtml(url+i);
            //解析页面获取商品数据并存储
            this.parse(html);
        }
//        String html = hus.doGetHtml(url);
        //解析页面获取商品数据并存储
//        this.parse(html);
        System.out.println("数据抓取完成");
    }

    public void parse(String html) throws IOException {
//        String ht = hus.doGetHtml(html);
        //解析html获取dom对象
        Document doc =  Jsoup.parse(html);
        Elements els = doc.select("div.all-book-list > div > ul >li");
        for (Element lis : els) {
            //书名
            String  bookName= lis.select(".book-mid-info > h4").text();
            System.out.println("书名：《"+bookName+"》");
            //作者
            String zz = lis.select(".book-mid-info > .author > [data-eid=qd_B59]").text();
            System.out.println("作者："+zz);
            //简介
            String jj = lis.select(".book-mid-info > .intro").text();
            System.out.println("简介："+jj);
//            https://book.qidian.com/info/1017125042
            String herfs = lis.select(".book-mid-info > h4 > a").attr("href");
//            System.out.println("地址：https:"+herfs);
            //读取单本书的内容
            String bookhtml = hus.doGetHtml("https:"+herfs);
            Document bookdoc =  Jsoup.parse(bookhtml);
            Elements boks = bookdoc.select(".wrap > .book-detail-wrap > #j-catalogWrap > .volume-wrap > div");
//            > .book-detail-wrap center990 > #j-catalogWrap > .volume-wrap > div
            //命名
            String txtname = bookName +".txt";
            File file = new File("C:\\Users\\mahao\\Desktop\\txtbook\\悬疑\\"+txtname);
            //保存成文件
            PrintStream ps = new PrintStream(new FileOutputStream(file));
            for (Element bok : boks) {
                Elements boklis =bok.select("ul > li ");
                for (Element bokli : boklis) {
                    String bokredurlz= bokli.select("a").attr("href");
//                    System.out.println("读书：https:"+bokredurlz);
                    String bokhtml = hus.doGetHtml("https:"+bokredurlz);
                    Document zdoc =  Jsoup.parse(bokhtml);
                    Elements zs = zdoc.select(".wrap > .main-read-container > .read-main-wrap > #j_chapterBox > .text-wrap[data-purl]");

                    //章节
                    String zhangjie = zdoc.select(".wrap > .main-read-container > .read-main-wrap > #j_chapterBox > .text-wrap[data-purl] > .main-text-wrap > .text-head > h3").text();
                    System.out.println(zhangjie);
                    ps.append("\n");
                    ps.append("\n");
                    ps.append("\n");
                    ps.append(zhangjie);
                    ps.append("\n");
                    ps.append("\n");
                   //内容
                    String neirong = zdoc.select(".wrap > .main-read-container > .read-main-wrap > #j_chapterBox > .text-wrap[data-purl] > .main-text-wrap > .read-content").text();
//                    System.out.println(neirong);
                    ps.append(neirong);
                }

            }
            ps.close();
        }
    }
}
