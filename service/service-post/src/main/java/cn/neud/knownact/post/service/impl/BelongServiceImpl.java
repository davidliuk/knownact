package cn.neud.knownact.post.service.impl;

import cn.neud.knownact.model.entity.post.PostEntity;
import cn.neud.knownact.post.dao.BelongDao;
import cn.neud.knownact.post.service.BelongService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import cn.neud.knownact.common.service.impl.CrudServiceImpl;
import cn.neud.knownact.model.dto.post.BelongDTO;
import cn.neud.knownact.model.entity.post.BelongEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 
 *
 * @author David l729641074@163.com
 * @since 1.0.0 2022-12-02
 */
@Service
@Slf4j
public class BelongServiceImpl extends CrudServiceImpl<BelongDao, BelongEntity, BelongDTO> implements BelongService {

    @Override
    public QueryWrapper<BelongEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<BelongEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }


    @Override
    public List<Long> getTagsId(Long postId) {
        LambdaQueryWrapper<BelongEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BelongEntity::getPostId, "post_id");
        wrapper.select(BelongEntity::getTagId);
        return baseDao.selectList(wrapper).stream().map(BelongEntity::getTagId).collect(Collectors.toList());
    }
}