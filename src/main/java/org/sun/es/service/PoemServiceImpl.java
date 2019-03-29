package org.sun.es.service;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.sun.es.dao.PoemRepository;
import org.sun.es.entity.Poem;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by linziyu on 2018/5/19.
 */

@Service
public class PoemServiceImpl implements PoemService{

    private static final String INDEX_TITLE = "title";
    private static final String INDEX_CONTENT = "content";

    @Autowired
    private PoemRepository poemRepository;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;


    @Override
    public void save(Poem poem) {
        poemRepository.save(poem);
    }

    @Override
    public Page<Poem> search(String title, String content, Pageable pageable) {



        return poemRepository.findByTitleLikeOrContentLike(title,content,pageable);
    }

    /**
     * 查询：标题必须存在，内容可以不存在
     * @param query
     * @param pageable
     * @return 高亮结果集
     */
    @Override
    public Page<Poem> search(String query, Pageable pageable) {
        QueryBuilder queryTitle = QueryBuilders.matchQuery(INDEX_TITLE, query);
        QueryBuilder queryContent = QueryBuilders.multiMatchQuery(query,INDEX_CONTENT);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(queryTitle).should(queryContent);

        HighlightBuilder.Field titleHighLight = new HighlightBuilder.Field(INDEX_TITLE);
        titleHighLight.preTags("<span style='color:red'>");
        titleHighLight.postTags("</span>");

        HighlightBuilder.Field contentHighLight = new HighlightBuilder.Field(INDEX_CONTENT);
        contentHighLight.preTags("<span style='color:red'>");
        contentHighLight.postTags("</span>");

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder)
                .withHighlightFields(
                        titleHighLight,
                        contentHighLight)
                .withPageable(pageable)
                .build();

        Page<Poem> poems = elasticsearchTemplate.queryForPage(searchQuery, Poem.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> aClass, Pageable pageable) {
                List<Poem> poems = new ArrayList<>();
                SearchHits hits = response.getHits();
                for (SearchHit hit : hits) {
                    if (hits.totalHits <= 0) {
                        return null;
                    }
                    Poem poem = new Poem();
                    poem.setId(Long.valueOf(hit.getId()));
                    poem.setTitle(String.valueOf(hit.getSource().get("title")));
                    poem.setContent(String.valueOf(hit.getSource().get("content")));

                    setHighLight(hit, "title", poem);
                    setHighLight(hit, "content", poem);

                    poems.add(poem);

                }
                return new AggregatedPageImpl<>((List<T>) poems);
            }
        });
        return poems;

    }

    @Override
    public Page<Poem> findAll(Pageable pageable) {
        return poemRepository.findAll(pageable);
    }




    /**
     * 设置高亮
     * @param hit 命中记录
     * @param filed 字段
     * @param object 待赋值对象
     */
    private static void setHighLight(SearchHit hit, String filed, Object object){
        //获取对应的高亮域
        Map<String, HighlightField> highlightFields = hit.getHighlightFields();
        HighlightField highlightField = highlightFields.get(filed);
        if (highlightField != null){
            //取得定义的高亮标签
            String highLightMessage = highlightField.fragments()[0].toString();
            // 反射调用set方法将高亮内容设置进去
            try {
                String setMethodName = parSetMethodName(filed);
                Class<?> Clazz = object.getClass();
                Method setMethod = Clazz.getMethod(setMethodName, String.class);
                setMethod.invoke(object, highLightMessage);
            } catch (Exception e) {
                System.out.println("反射报错");
            }
        }
    }

    /**
     * 根据字段名，获取Set方法名
     * @param fieldName 字段名
     * @return  Set方法名
     */
    private static String parSetMethodName(String fieldName){
        if (StringUtils.isBlank(fieldName)){
            return null;
        }
        int startIndex = 0;
        if (fieldName.charAt(0) == '_'){
            startIndex = 1;
        }
        return "set" + fieldName.substring(startIndex, startIndex + 1).toUpperCase()
                + fieldName.substring(startIndex + 1);
    }

}
