package com.itheima.dao;

import com.itheima.pojo.Article;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/***
 * @ClassName ArticleDao
 * @Description 描述
 * @version 1.0.0
 * @author ayuan
 * @createTime 2020/11/08 16:19:00
 */
public interface ArticleDao extends ElasticsearchRepository<Article,Long> {
    //根据title进行条件查询搜索
    List<Article> findByTitle(String title);
}
