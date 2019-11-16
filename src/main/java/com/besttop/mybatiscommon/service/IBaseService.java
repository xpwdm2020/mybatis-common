package com.besttop.mybatiscommon.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * <p>Title: IBaseService</p>
 * <p>Description: IBaseService</p>
 * <p>Copyright: Xi An BestTop Technologies, ltd. Copyright(c) 2018/p>
 *
 * @author Fuqiang
 * @version 0.0.0.1
 * <pre>Histroy:
 *       2019/11/16 0016 16:46 Create by Fuqiang
 * </pre>
 */
public interface IBaseService<T> {
    boolean insert(T var1);

    boolean insertBatch(Collection<T> var1);

    boolean insertBatch(Collection<T> var1, int var2);

    boolean insertOrUpdateBatch(Collection<T> var1);

    boolean insertOrUpdateBatch(Collection<T> var1, int var2);

    boolean removeById(Serializable var1);

    boolean removeByMap(Map<String, Object> var1);

    boolean remove(Wrapper<T> var1);

    boolean removeByIds(Collection<? extends Serializable> var1);

    boolean updateById(T var1);

    boolean update(T var1, Wrapper<T> var2);

    boolean updateBatchById(Collection<T> var1);

    boolean updateBatchById(Collection<T> var1, int var2);

    boolean insertOrUpdate(T var1);

    T getById(Serializable var1);

    Collection<T> listByIds(Collection<? extends Serializable> var1);

    Collection<T> listByMap(Map<String, Object> var1);

    T getOne(Wrapper<T> var1);

    Map<String, Object> getMap(Wrapper<T> var1);

    Object getObj(Wrapper<T> var1);

    int count(Wrapper<T> var1);

    List<T> list(Wrapper<T> var1);

    IPage<T> page(IPage<T> var1, Wrapper<T> var2);

    List<Map<String, Object>> listMaps(Wrapper<T> var1);

    List<Object> listObjs(Wrapper<T> var1);

    IPage<Map<String, Object>> pageMaps(IPage<T> var1, Wrapper<T> var2);
}
