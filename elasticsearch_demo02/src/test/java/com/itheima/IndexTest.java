package com.itheima;

import org.elasticsearch.client.transport.TransportClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/***
 * @ClassName IndexTest
 * @Description 索引相关测试
 * @version 1.0.0
 * @author ayuan
 * @createTime 2020/11/07 18:03:00
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class IndexTest {
    @Autowired
    private TransportClient transportClient;

    //创建索引
    @Test
    public void createIndex() {
        //准备创建索引 ，指定索引名 执行创建的动作（get方法）
        //参数：索引名称
        transportClient.admin().indices().prepareCreate("blog03").get();
    }

    //删除索引
    @Test
    public void deleteIndex() {
        //准备删除索引 ，指定索引名 指定删除的动作（get）
        //参数：索引名称
        transportClient.admin().indices().prepareDelete("blog02").get();
    }
}
