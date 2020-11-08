package com.itheima;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itheima.pojo.Article;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/***
 * @ClassName EsApplicationTest01
 * @Description 测试类，操作ElasticSearch
 * @version 1.0.0
 * @author ayuan
 * @createTime 2020/11/07 15:12:00
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class EsApplicationTest01 {

    @Autowired
    private TransportClient transportClient;

    @Autowired
    private ObjectMapper objectMapper;//json操作的工具类的（将POJO 转成JSON 也可以将json转成POJO）

    //创建索引 并 添加文档  增加   //修改文档
    @Test
    public void createIndexAndDocument() throws Exception {
        //设置数据
        Article article = new Article();
        article.setTitle("华为手机很棒");
        article.setContent("华为手机真的很棒");
        article.setId(1L);
        //使用json工具将数据转换成json类型
        String jsonStr = objectMapper.writeValueAsString(article);
        //参数1指定的索引的名称
        //参数2指定类型的名称
        //参数3指定文档的唯一标识
        IndexResponse indexResponse = transportClient
                .prepareIndex("blog01", "article", "1")
                //设置文档数据是JSON 是一篇文章
                //设置json数据，并指定JSON类型
                .setSource(jsonStr, XContentType.JSON)
                .get();
        System.out.println("版本："+indexResponse.getVersion());
        System.out.println("索引名叫："+indexResponse.getIndex());
    }

    //根据Id查询文档获取数据
    @Test
    public void getById() {
        //参数1指定的索引的名称
        //参数2指定类型的名称
        //参数3指定文档的唯一标识
        GetResponse documentFields = transportClient.prepareGet("blog01", "article", "1").get();
        //获取到JSON数据
        String sourceAsString = documentFields.getSourceAsString();
        System.out.println(sourceAsString);
    }

    //根据id删除文档
    @Test
    public void deleteById() {
        //参数1指定的索引的名称
        //参数2指定类型的名称
        //参数3指定文档的唯一标识
        transportClient.prepareDelete("blog01", "article", "1");
    }
}
