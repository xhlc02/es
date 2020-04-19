package com.xhl.esjd.service;

import com.alibaba.fastjson.JSON;
import com.xhl.esjd.pojo.Content;
import com.xhl.esjd.utils.HtmlParseUtil;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.AbstractHighlighterBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.directory.SearchResult;
import javax.swing.text.Highlighter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author MuYan
 * @date 2020-04-19 20:04
 */
@Service
public class ContentService {
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    public static void main(String[] args) {

    }
    /**
     * 解析数据放入es索引库
     * @param keyWords
     * @return
     * @throws Exception
     */
    public Boolean parseContent(String keyWords)throws  Exception{
        List<Content> contents = new HtmlParseUtil().parseJD(keyWords);
        //把数据插入es索引库中
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("2m");
        for(int i=0;i<contents.size();i++){
            bulkRequest.add(new IndexRequest("jd_goods").source(JSON.toJSONString(contents.get(i)), XContentType.JSON));
        }
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        return !bulk.hasFailures();
    }

    /**
     * 获取数据
     */
    public List<Map<String,Object>> serachPage(String keyWords,int pageNo,int pageSize) throws Exception{
        if(pageNo<=1){
            pageNo=1;
        }
        //条件搜索
        SearchRequest searchRequest = new SearchRequest("jd_goods");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //分页
        sourceBuilder.from(pageNo);
        sourceBuilder.size(pageSize);
        //精准匹配
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("title", keyWords);
        sourceBuilder.query(termQueryBuilder);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        //执行搜索
        searchRequest.source(sourceBuilder);
        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        ArrayList<Map<String,Object>> list = new ArrayList<>();
        for (SearchHit documentFields : search.getHits().getHits()) {
            list.add(documentFields.getSourceAsMap());

        }
        return list;
    }

    /**
     * 获取数据,并且高亮
     */
    public List<Map<String,Object>> serachHighlightPage(String keyWords,int pageNo,int pageSize) throws Exception{
        if(pageNo<=1){
            pageNo=1;
        }
        //条件搜索
        SearchRequest searchRequest = new SearchRequest("jd_goods");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //分页
        sourceBuilder.from(pageNo);
        sourceBuilder.size(pageSize);
        //精准匹配
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("title", keyWords);
        sourceBuilder.query(termQueryBuilder);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        //高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        //只要一个高亮，关闭多个
        highlightBuilder.requireFieldMatch(false);
        highlightBuilder.field("title");
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");

        sourceBuilder.highlighter(highlightBuilder);

        //执行搜索
        searchRequest.source(sourceBuilder);
        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        ArrayList<Map<String,Object>> list = new ArrayList<>();
        for (SearchHit hit : search.getHits().getHits()) {

            //解析高亮的字段
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField title = highlightFields.get("title");
            //原来的结果
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            //将高亮的字段替换原来的
            if(title!=null){
                Text[] fragments = title.fragments();
                String n_title="";
                for (Text text : fragments) {
                    n_title +=text;
                }
                sourceAsMap.put("title",n_title);
            }
            list.add(hit.getSourceAsMap());

        }
        return list;
    }
}
