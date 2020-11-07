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
    private ObjectMapper objectMapper;

    //创建索引 并 添加文档  增加   //修改文档
    @Test
    public void createIndexAndDocument() throws Exception {
        //设置数据
        Article article = new Article();
        article.setTitle("华为手机很棒");
        article.setContent("华为手机真的很棒");
        article.setId(1L);

        IndexResponse indexResponse = transportClient
                .prepareIndex("blog01", "article", "1")
                .setSource(objectMapper.writeValueAsString(article), XContentType.JSON)
                .get();
        System.out.println(indexResponse);
    }

    //根据Id查询文档获取数据
    @Test
    public void getById() {
        GetResponse documentFields = transportClient.prepareGet("blog01", "article", "1").get();
        System.out.println(documentFields.getSourceAsString());
    }

    //根据id删除文档
    @Test
    public void deleteById() {
        transportClient.prepareDelete("blog01", "article", "1");
    }
}
