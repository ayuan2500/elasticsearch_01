package com.itheima;

import com.itheima.dao.ArticleDao;
import com.itheima.pojo.Article;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
/***
 * @ClassName com.itheima.DataElasticsearchTest
 * @Description Spring Data ElasticSearch 测试类
 * @version 1.0.0
 * @author ayuan
 * @createTime 2020/11/08 16:58:00
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class DataElasticsearchTest {
    @Autowired
    private ArticleDao articleDao;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    //创建索引 创建类型 进行手动映射
    @Test
    public void createIndex(){
        elasticsearchTemplate.createIndex(Article.class);
        elasticsearchTemplate.putMapping(Article.class);
    }

    //创建一个文档 和更新文档
    @Test
    public void createDocument(){
        List<Article> articles = new ArrayList<>();
        for (long i = 0; i < 100; i++) {
            Article article = new Article(i, "华为手机很好"+i, "占上风"+i);
            articles.add(article);
        }
        articleDao.saveAll(articles);
    }

    //根据id删除文档
    @Test
    public void deleteDocument(){
        articleDao.deleteById(1L);
    }

    //根据id查询文档
    @Test
    public void getDocument(){
        Article article = articleDao.findById(1L).get();
        System.out.println(article);
    }

    //分页查询  排序
    @Test
    public void findByPage(){
        Sort sort = new Sort(Sort.Direction.DESC,"id");//设置排序的类型， 要排序的字段
        //参数1 指定当前的页码 0表示第一页。
        //参数2 指定每页显示的行
        //参数3 指定排序的对象
        Pageable pagelbe= PageRequest.of(0,10,sort);
        //总记录数
        //总页数
        //当前的页码
        //当前的页的集合
        Page<Article> all = articleDao.findAll(pagelbe);
        long totalElements = all.getTotalElements();//总记录数
        int totalPages = all.getTotalPages();//总页数
        List<Article> content = all.getContent();//当前的页的集合
        for (Article article : content) {
            System.out.println(article);
        }
    }

    //条件查询 从title 上搜索内容为 华为的数据
    @Test
    public void findByCondition(){
        List<Article> list = articleDao.findByTitle("华为");
        for (Article article : list) {
            System.out.println(article);
        }
    }
}