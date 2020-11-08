package com.itheima.pojo;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;

/*
 * 加入注解的目的：
 * 1.创建索引
 * 2.创建类型
 * 3.设置文档的唯一标识
 * 4.进行映射 数据类型？是否分词？是否索引？是否存储？如果分词使用什么分词器？
 *
 * @Document(indexName = "blog",type = "article")
 * @Document        修饰类 标识要和es服务器建立映射关系
 *  indexName       指定索引的名称
 *  type            指定类型的名称
 * @Field(type= FieldType.Text,index = true,store = false,analyzer = "ik_smart",searchAnalyzer = "ik_smart")
 * @Field           注解修饰 类中的属性 标识和es中的文档中field 建立映射关系
 *   type           指定该类型是什么数据类型 text 标识为文本
 *   index          指定是否要索引 ，默认值true
 *   store          指定是否要存储 ，默认是false
 *   analyzer       指定的是当数据保存到es服务器中的时候使用的分词器
 *   searchAnalyzer 指定的是当实现查询的时候使用分词器 一般这个不用配置，使用的是相同的分词器。
 */

/***
 * @ClassName Article
 * @Description pojo类，用于存储数据转成JSON
 * @version 1.0.0
 * @author ayuan
 * @createTime 2020/11/07 15:10:00
 */
@Document(indexName = "blog",type = "article")
public class Article implements Serializable {
    @Id//标识为文档唯一的标识
    private Long id;
    @Field(type= FieldType.Text,index = true,store = false,analyzer = "ik_smart",searchAnalyzer = "ik_smart")
    private String content;
    @Field(type= FieldType.Text,index = true,store = false,analyzer = "ik_smart",searchAnalyzer = "ik_smart")
    private String title;

    @Override
    public String toString() {
        return "Article{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", title='" + title + '\'' +
                '}';
    }

    public Article() {
    }

    public Article(Long id, String content, String title) {

        this.id = id;
        this.content = content;
        this.title = title;
    }

    public Long getId() {

        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
