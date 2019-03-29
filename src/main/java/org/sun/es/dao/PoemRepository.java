package org.sun.es.dao;

import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.sun.es.entity.Poem;

/**
 * Created by linziyu on 2018/5/19.
 * dao层
 *
 */
public interface PoemRepository extends ElasticsearchRepository<Poem,Long>{
    Page<Poem> findByTitleLikeOrContentLike(String title, String content, Pageable pageable);
    Page<Poem> findByContentLike(String content, Pageable pageable);


}
