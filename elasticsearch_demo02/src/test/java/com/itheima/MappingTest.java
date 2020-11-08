package com.itheima;

import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/***
 * @ClassName MappingTest
 * @Description 描述
 * @version 1.0.0
 * @author ayuan
 * @createTime 2020/11/07 18:08:00
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class MappingTest {
    @Autowired
    private TransportClient transportClient;

    //创建映射
    @Test
    public void putMapping() throws Exception {
        //1.创建索引 如果已有索引 可以先删除再测试
        transportClient.admin().indices().prepareCreate("blog02").get();
        //2.手动的创建映射
        PutMappingRequest putMappingRequest = new PutMappingRequest();
        //设置映射所在的索引名
        putMappingRequest.indices("blog02");
        //设置类型
        putMappingRequest.type("article");
        //设置资源（映射具体配置的JSON值）
        XContentBuilder xcontentbuidler = XContentFactory.jsonBuilder()
                .startObject()
                    .startObject("article")
                        .startObject("properties")
                             .startObject("id")
                                 .field("type", "long")
                                 .field("store", "true")
                             .endObject()
                             .startObject("title")
                                 .field("type", "text")
                                 .field("analyzer", "ik_smart")
                                 .field("store", "true")
                             .endObject()
                             .startObject("content")
                                 .field("type", "text")
                                 .field("analyzer", "ik_smart")
                                 .field("store", "true")
                             .endObject()
                        .endObject()
                    .endObject()
                .endObject();
        putMappingRequest.source(xcontentbuidler);
        //执行创建映射的动作
        transportClient.admin().indices().putMapping(putMappingRequest).get();
    }
}
