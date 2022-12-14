package cn.neud.knownact.feed.dao;

import cn.neud.knownact.common.dao.BaseDao;
import cn.neud.knownact.model.entity.feed.FeedEntity;
import net.librec.recommender.item.RecommendedItem;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 
 *
 * @author David l729641074@163.com
 * @since 1.0.0 2022-12-03
 */
@Mapper
public interface FeedDao extends BaseDao<FeedEntity> {

    void truncateTable();

    void saveBatch(List<RecommendedItem> recommendedList);
}