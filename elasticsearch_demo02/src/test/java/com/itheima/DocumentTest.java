package com.itheima;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itheima.pojo.Article;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

/***
 * @ClassName DocumentTest
 * @Description 文档相关测试
 * @version 1.0.0
 * @author ayuan
 * @createTime 2020/11/07 20:01:00
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class DocumentTest {

    @Autowired
    private TransportClient transportClient;

    @Autowired
    private ObjectMapper objectMapper;//json操作的工具类的（将POJO 转成JSON 也可以将json转成POJO）

    /**
     * 创建 / 更新文档
     */
    //通过ObjctMapper 创建文档/更新文档，使用的是ik分词器
    @Test
    public void createIndexAndDocument() throws Exception {
        //设置数据
        Article article = new Article();
        article.setTitle("华为手机很棒");
        article.setContent("华为手机真的很棒");
        article.setId(1L);
        //使用JSON操作工具将数据转换成JSON类型
        String jsonStr = objectMapper.writeValueAsString(article);
        IndexResponse indexResponse = transportClient
                //参数1指定的索引的名称
                //参数2指定类型的名称
                //参数3指定文档的唯一标识
                .prepareIndex("blog02", "article", "1")
                //设置json数据，并指定JSON类型
                .setSource(jsonStr, XContentType.JSON)
                .get();
        System.out.println(indexResponse);
    }

    //通过JSON xcontentBuidler 创建文档/更新文档
    /*提供的JSON格式如下
    {
         "id": 1,
         "content": "华为手机真的很棒",
         "title": "华为手机很棒"
     }
     */
    @Test
    public void createDocumentByJsons() throws Exception{
        //设置资源（映射具体配置的JSON值）
        XContentBuilder xContentBuilder = XContentFactory.jsonBuilder()
            .startObject()
                .field("id",2)
                .field("content","华为手机真的很棒你猜猜")
                .field("title","华为手机很棒但是我现在真的忧桑")
            .endObject();
        IndexResponse indexResponse = transportClient
                //参数1指定的索引的名称
                //参数2指定类型的名称
                //参数3指定文档的唯一标识
                .prepareIndex("blog02", "article", "2")
                //设置json数据
                .setSource(xContentBuilder)
                .get();
        System.out.println(indexResponse);
    }

    //批量添加文档，一次性提交数据
    @Test
    public void createMultiDocument() throws Exception {
        //构建批量添加builder
        BulkRequestBuilder bulkRequestBuilder = transportClient.prepareBulk();
        //记录开始时间
        long start = System.currentTimeMillis();
        for (long i = 0; i < 10000; i++) {
            //数据构建
            Article article = new Article();
            article.setTitle("华为手机很棒" + i);
            article.setContent("华为手机真的很棒啊" + i);
            article.setId(i);
            //转成JSON
            String jsonStr = objectMapper.writeValueAsString(article);
            //设置值
            IndexRequest indexRequest = new IndexRequest("blog02", "article", "" + i).source(jsonStr, XContentType.JSON);
            //添加请求对象buidler中
            bulkRequestBuilder.add(indexRequest);
        }
        //一次性提交
        BulkResponse bulkItemResponses = bulkRequestBuilder.get();
        System.out.println("获取状态：" + bulkItemResponses.status());   //如果成功输出200
        //记录结束时间
        long end = System.currentTimeMillis();
        //计算消耗多长时间
        System.out.println("消耗了:"+(end-start)/1000);
        if (bulkItemResponses.hasFailures()) {
            System.out.println("还有些--->有错误");
        }
    }

    /**
     * 删除文档
     */
    @Test
    public void deleteByDocument() {
        //参数1指定的索引的名称
        //参数2指定类型的名称
        //参数3指定文档的唯一标识
        transportClient.prepareDelete("blog02", "article", "2").get();
    }

    /**
     * 查询文档
     */
    //查询所有
    @Test
    public void matchAllQuery() {
        //1.创建查询对象，设置查询条件，执行查询动作
        SearchResponse response = transportClient
                //设置查询的索引名
                .prepareSearch("blog02")
                //设置查询的类型
                .setTypes("article")
                //设置查询对象
                .setQuery(QueryBuilders.matchAllQuery())
                //执行搜索
                .get();
        //2.获取结果集
        SearchHits hits = response.getHits();
        System.out.println("获取到的总命中数：" + hits.getTotalHits());
        //3.循环遍历结果 打印
        for (SearchHit hit : hits) {
            //获取JSON类型数据
            String sourceAsString = hit.getSourceAsString();
            System.out.println(sourceAsString);
            //可以根据情况将JSON转成POJO类型
        }
    }

    //queryStringQuery():字符串查询
    //默认采用的是[标准分词器],只能针对字符串数据类型的字段进行查询。   用的不多
    @Test
    public void queryStringQuery() {
        //1.创建查询对象，设置查询条件，
        SearchResponse response = transportClient
                //设置查询的索引名
                .prepareSearch("blog02")
                //设置查询的类型
                .setTypes("article")
                //设置查询对象，如果没有指定从哪个field，从所有的字段(一定是字符串类型的)进行查询 并且获取到并集
                .setQuery(QueryBuilders.queryStringQuery("手机")
                        //指定从哪一个字段搜索
                        .field("title"))
                //执行查询动作
                .get();
        //2.获取结果集
        SearchHits hits = response.getHits();
        System.out.println("获取到的总命中数：" + hits.getTotalHits());
        //3.循环遍历结果 打印
        for (SearchHit hit : hits) {
            //获取JSON类型结果
            String sourceAsString = hit.getSourceAsString();
            System.out.println(sourceAsString);
        }
    }

    //termQuery词条查询
    //查询时，不分词，将其作为整体作为条件去倒排索引中匹配是否存在。 简述为：不分词，整体匹配查询
    @Test
    public void termQuery() {
        //1.创建查询对象，设置查询条件，
        SearchResponse response = transportClient
                //设置查询的索引名
                .prepareSearch("blog02")
                //设置查询的类型
                .setTypes("article")
                //参数1：指定要搜索的字段
                //参数2：指定要搜索的内容
                .setQuery(QueryBuilders.termQuery("title","手机"))
                //执行查询动作
                .get();
        //2.获取结果集
        SearchHits hits = response.getHits();
        System.out.println("获取到的总命中数：" + hits.getTotalHits());
        //3.循环遍历结果 打印
        for (SearchHit hit : hits) {
            //获取JSON类型结果
            String sourceAsString = hit.getSourceAsString();
            System.out.println(sourceAsString);
        }
    }

    //matchQuery    匹配查询
    //特点：先使用ik_smart进行分词，再查询，可以指定任意数据类型。需要指定要查询的哪个字段
    @Test
    public void matchQuery() {
        //1.创建查询对象，设置查询条件，
        SearchResponse response = transportClient
                //设置查询的索引名
                .prepareSearch("blog02")
                //设置查询的类型
                .setTypes("article")
                //参数1：指定要搜索的字段
                //参数2：指定要搜索的内容
                .setQuery(QueryBuilders.matchQuery("title","手机"))
                //执行查询动作
                .get();
        //2.获取结果集
        SearchHits hits = response.getHits();
        System.out.println("获取到的总命中数：" + hits.getTotalHits());
        //3.循环遍历结果 打印
        for (SearchHit hit : hits) {
            //获取JSON类型结果
            String sourceAsString = hit.getSourceAsString();
            System.out.println(sourceAsString);
        }
    }

    //multiMatch查询
    //多字段匹配查询 特点：先分词（根据ik_smart分词器进行分词） 再匹配查询  多个字段再合并
    @Test
    public void matchMultiQuery() {
        //1.创建查询对象，设置查询条件，
        SearchResponse response = transportClient
                //设置查询的索引名
                .prepareSearch("blog02")
                //设置查询的类型
                .setTypes("article")
                //参数1 指定要搜索的内容
                //参数2 指定要搜索的多个字段的字段名 标识 从多个字段中搜索数据进行合并返回
                .setQuery(QueryBuilders.multiMatchQuery("手机","content","title"))
                //执行查询动作
                .get();
        //2.获取结果集
        SearchHits hits = response.getHits();
        System.out.println("获取到的总命中数：" + hits.getTotalHits());
        //3.循环遍历结果 打印
        for (SearchHit hit : hits) {
            //获取JSON类型结果
            String sourceAsString = hit.getSourceAsString();
            System.out.println(sourceAsString);
        }
    }

    //wildcardQuery():模糊查询
    //模糊搜索: 也叫通配符搜索
    //? 表示任意字符 一定占用一个字符空间，相当于占位符
    //* 表示任意字符 可以占用也可以不占用
    @Test
    public void wildcardQuery() {
        //1.创建查询对象，设置查询条件，
        SearchResponse response = transportClient
                //设置查询的索引名
                .prepareSearch("blog02")
                //设置查询的类型
                .setTypes("article")
                //参数1：指定要搜索的字段
                //参数2：指定要搜索的内容
                .setQuery(QueryBuilders.wildcardQuery("title","手?"))
                //执行查询动作
                .get();
        //2.获取结果集
        SearchHits hits = response.getHits();
        System.out.println("获取到的总命中数：" + hits.getTotalHits());
        //3.循环遍历结果 打印
        for (SearchHit hit : hits) {
            //获取JSON类型结果
            String sourceAsString = hit.getSourceAsString();
            System.out.println(sourceAsString);
        }
    }

    //相似度查询fuzzyQuery()
    //相似度查询 目前支持英文，输入错误的单词也能搜索出来,错误的单词数量（默认是2个）
    @Test
    public void fuzzyQuery() {
        //1.创建查询对象，设置查询条件，
        SearchResponse response = transportClient
                //设置查询的索引名
                .prepareSearch("blog02")
                //设置查询的类型
                .setTypes("article")
                //参数1：指定要搜索的字段
                //参数2：指定要搜索的内容
                .setQuery(QueryBuilders.fuzzyQuery("title","eaasticsearch"))
                //执行查询动作
                .get();
        //2.获取结果集
        SearchHits hits = response.getHits();
        System.out.println("获取到的总命中数：" + hits.getTotalHits());
        //3.循环遍历结果 打印
        for (SearchHit hit : hits) {
            //获取JSON类型结果
            String sourceAsString = hit.getSourceAsString();
            System.out.println(sourceAsString);
        }
    }

    //范围查询rangeQuery()
    /**
     *  范围查询     查询id 从0 到20之间的数据包含0 和20
     *  from  to
     *  gt lt
     */
    @Test
    public void rangeQuery() {
        //1.创建查询对象，设置查询条件，
        SearchResponse response = transportClient
                //设置查询的索引名
                .prepareSearch("blog02")
                //设置查询的类型
                .setTypes("article")
                //参数1：指定要搜索的字段
                //参数2：指定要搜索的内容
                //.setQuery(QueryBuilders.rangeQuery("id").gte(1).lte(20))  //>=1 <=20
                .setQuery(QueryBuilders.rangeQuery("id").from(1,true).to(20,false)) //从1 到 20
                //执行查询动作
                .get();
        //2.获取结果集
        SearchHits hits = response.getHits();
        System.out.println("获取到的总命中数：" + hits.getTotalHits());
        //3.循环遍历结果 打印
        for (SearchHit hit : hits) {
            //获取JSON类型结果
            String sourceAsString = hit.getSourceAsString();
            System.out.println(sourceAsString);
        }
    }

    //布尔查询（多条件查询）
    //需求：查询id为1-30之间并且从title上搜索手机
    @Test
    public void boolquery() {
        //1.创建组合bool条件对象
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //2.创建条件1 ： id 在1-30 之间
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("id").from(0, true).to(30, true);
        //3.创建条件2 ： title 上搜索手机
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("title", "手机");
        //将2个条件添加到bool条件对象中 四种组合方式
        // MUST       表示必须满足条件  相当于数据库中的 and
        // SHOULD     表示应该满足条件 相当于数据中的 or
        // MUST_NOT   必须不满足条件  相当于数据库的 not
        // FILTER     表示必须满足条件  相当于数据库中的 and
        boolQueryBuilder.must(rangeQueryBuilder).must(termQueryBuilder);
        //3.创建查询对象，设置查询条件，
        SearchResponse response = transportClient
                //设置查询的索引名
                .prepareSearch("blog02")
                //设置查询的类型
                .setTypes("article")
                //设置查询对象
                .setQuery(boolQueryBuilder)
                //执行查询动作
                .get();
        //4.获取结果集
        SearchHits hits = response.getHits();
        System.out.println("获取到的总命中数：" + hits.getTotalHits());
        //5.循环遍历结果 打印
        for (SearchHit hit : hits) {
            //获取JSON类型结果
            String sourceAsString = hit.getSourceAsString();
            System.out.println(sourceAsString);
        }
    }

    //分页查询和排序
    //需求：排序和分页 每页显示2行记录，按照Id升序排列
    @Test
    public void pageAndSort() {
        //1.创建查询对象，设置查询条件，执行查询动作
        SearchResponse response = transportClient
                //设置查询的索引名
                .prepareSearch("blog02")
                //设置查询的类型
                .setTypes("article")
                //设置查询对象
                .setQuery(QueryBuilders.termQuery("title", "手机"))
                .setFrom(0)// 起始文档的下标【(当前页-1)*每页数量】 (page - 1) * rows
                .setSize(2)// 查询的文档数量 rows
                //参数1 设置排序的字段
                //参数2 设置排序的类型 DESC 降序  ASC 升序  默认是没有排序的
                .addSort("id", SortOrder.ASC) //添加排序  设置排序的字段名 和设置排序的类型 DESC/ASC
                //执行查询动作
                .get();
        //2.获取结果集
        SearchHits hits = response.getHits();
        System.out.println("获取到的总命中数：" + hits.getTotalHits());
        //3.循环遍历结果 打印
        for (SearchHit hit : hits) {
            String sourceAsString = hit.getSourceAsString();
            System.out.println(sourceAsString);
        }
    }

    //高亮
    @Test
    public void highlight() throws Exception{
        //创建高亮的对象
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        //1.设置高亮的字段 为title和content
        highlightBuilder.field("title").field("content")
                //2.设置高亮的前缀和后缀
                .preTags("<em style=\"color:red\">").postTags("</em>");
        SearchResponse response = transportClient
                //设置查询的索引名
                .prepareSearch("blog03")
                //设置查询的类型
                .setTypes("article")
                //参数1 指定要搜索的字段
                //参数2 指定要搜索的内容  <em style="color:red">华为</em>手机很棒
                .setQuery(QueryBuilders.matchQuery("title","华为手机"))
                .highlighter(highlightBuilder)//设置高亮的对象
                //执行查询动作
                .get();
        //4.获取结果
        SearchHits hits = response.getHits();
        System.out.println("根据条件查询的到总命中数："+hits.getTotalHits());
        //5.打印或者遍历处理进行业务处理
        //6.获取高亮数据
        for (SearchHit hit : hits) {
            String sourceAsString = hit.getSourceAsString();//不带高亮的数据
            //高亮的数据封装的对象
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField highlightField = highlightFields.get("title");//是要获取高亮字段为title的高亮数据
            HighlightField highlightField2 = highlightFields.get("content");//是要获取高亮字段为content的高亮数据
            //高亮数据
            Text[] fragments = highlightField.getFragments();
            StringBuffer sb = new StringBuffer();
            for (Text fragment : fragments) {
                String string = fragment.string();//真正的高亮数据 <em style="color:red">华为</em>手机很棒
                sb.append(string);
            }
            String s = sb.toString();//拼接之后的，但是我们的业务中title只有一个数据，元素长度就是1 高亮数据
            //json类型数据
            String source = hit.getSourceAsString();
            //将json转为pojo
            Article article = objectMapper.readValue(source, Article.class);
            //设置pojo对象的title值
            article.setTitle(s);
            //返回给前端页面
            System.out.println(objectMapper.writeValueAsString(article));
        }
    }
}
