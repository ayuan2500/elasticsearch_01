package com.itheima.pojo;

import java.io.Serializable;

/***
 * @ClassName Article
 * @Description pojo类，用于存储数据转成JSON
 * @version 1.0.0
 * @author ayuan
 * @createTime 2020/11/07 15:10:00
 */
public class Article implements Serializable {
    private Long id;
    private String content;
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
