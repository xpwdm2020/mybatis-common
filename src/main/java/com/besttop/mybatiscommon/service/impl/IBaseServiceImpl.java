package com.besttop.mybatiscommon.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.*;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlHelper;
import com.besttop.mybatiscommon.mapper.IBaseMapper;
import com.besttop.mybatiscommon.service.IBaseService;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;


/**
 * <p>Title: IBaseServiceImpl</p>
 * <p>Description: IBaseServiceImpl</p>
 * <p>Copyright: Xi An BestTop Technologies, ltd. Copyright(c) 2018/p>
 *
 * @author Fuqiang
 * @version 0.0.0.1
 * <pre>Histroy:
 *       2019/11/16 0016 16:47 Create by Fuqiang
 * </pre>
 */
public class IBaseServiceImpl<M extends IBaseMapper<T>, T> implements IBaseService<T> {
    @Autowired
    protected M baseMapper;

    public IBaseServiceImpl() {
    }

    protected static boolean retBool(Integer result) {
        return SqlHelper.retBool(result);
    }

    protected Class<T> currentModelClass() {
        return ReflectionKit.getSuperClassGenericType(this.getClass(), 1);
    }

    protected SqlSession sqlSessionBatch() {
        return SqlHelper.sqlSessionBatch(this.currentModelClass());
    }

    protected String sqlStatement(SqlMethod sqlMethod) {
        return SqlHelper.table(this.currentModelClass()).getSqlStatement(sqlMethod.getMethod());
    }

    @Override
    public boolean insert(T entity) {
        return retBool(this.baseMapper.insert(entity));
    }

    @Override
    @Transactional(
            rollbackFor = {Exception.class}
    )
    public boolean insertBatch(Collection<T> entityList) {
        return this.insertBatch(entityList, 30);
    }

    @Override
    @Transactional(
            rollbackFor = {Exception.class}
    )
    public boolean insertBatch(Collection<T> entityList, int batchSize) {
        if (CollectionUtils.isEmpty(entityList)) {
            throw new IllegalArgumentException("Error: entityList must not be empty");
        } else {
            try {
                SqlSession batchSqlSession = this.sqlSessionBatch();
                Throwable var4 = null;

                try {
                    int i = 0;
                    String sqlStatement = this.sqlStatement(SqlMethod.INSERT_ONE);

                    for(Iterator var7 = entityList.iterator(); var7.hasNext(); ++i) {
                        T anEntityList = (T) var7.next();
                        batchSqlSession.insert(sqlStatement, anEntityList);
                        if (i >= 1 && i % batchSize == 0) {
                            batchSqlSession.flushStatements();
                        }
                    }

                    batchSqlSession.flushStatements();
                } catch (Throwable var17) {
                    var4 = var17;
                    throw var17;
                } finally {
                    if (batchSqlSession != null) {
                        if (var4 != null) {
                            try {
                                batchSqlSession.close();
                            } catch (Throwable var16) {
                                var4.addSuppressed(var16);
                            }
                        } else {
                            batchSqlSession.close();
                        }
                    }

                }

                return true;
            } catch (Throwable var19) {
                throw ExceptionUtils.mpe("Error: Cannot execute saveBatch Method. Cause", var19);
            }
        }
    }

    @Override
    public boolean insertOrUpdate(T entity) {
        if (null == entity) {
            return false;
        } else {
            Class<?> cls = entity.getClass();
            TableInfo tableInfo = TableInfoHelper.getTableInfo(cls);
            if (null != tableInfo && StringUtils.isNotEmpty(tableInfo.getKeyProperty())) {
                Object idVal = ReflectionKit.getMethodValue(cls, entity, tableInfo.getKeyProperty());
                if (StringUtils.checkValNull(idVal)) {
                    return this.insert(entity);
                } else {
                    return this.updateById(entity) || this.insert(entity);
                }
            } else {
                throw ExceptionUtils.mpe("Error:  Can not execute. Could not find @TableId.");
            }
        }
    }

    @Override
    @Transactional(
            rollbackFor = {Exception.class}
    )
    public boolean insertOrUpdateBatch(Collection<T> entityList) {
        return this.insertOrUpdateBatch(entityList, 30);
    }

    @Override
    @Transactional(
            rollbackFor = {Exception.class}
    )
    public boolean insertOrUpdateBatch(Collection<T> entityList, int batchSize) {
        if (CollectionUtils.isEmpty(entityList)) {
            throw new IllegalArgumentException("Error: entityList must not be empty");
        } else {
            try {
                SqlSession batchSqlSession = this.sqlSessionBatch();
                Throwable var4 = null;

                try {
                    Iterator var5 = entityList.iterator();

                    while(var5.hasNext()) {
                        T anEntityList = (T) var5.next();
                        this.insertOrUpdate(anEntityList);
                    }

                    batchSqlSession.flushStatements();
                    return true;
                } catch (Throwable var15) {
                    var4 = var15;
                    throw var15;
                } finally {
                    if (batchSqlSession != null) {
                        if (var4 != null) {
                            try {
                                batchSqlSession.close();
                            } catch (Throwable var14) {
                                var4.addSuppressed(var14);
                            }
                        } else {
                            batchSqlSession.close();
                        }
                    }

                }
            } catch (Throwable var17) {
                throw ExceptionUtils.mpe("Error: Cannot execute saveOrUpdateBatch Method. Cause", var17);
            }
        }
    }

    @Override
    public boolean removeById(Serializable id) {
        return SqlHelper.delBool(this.baseMapper.deleteById(id));
    }

    @Override
    public boolean removeByMap(Map<String, Object> columnMap) {
        if (ObjectUtils.isEmpty(columnMap)) {
            throw ExceptionUtils.mpe("removeByMap columnMap is empty.");
        } else {
            return SqlHelper.delBool(this.baseMapper.deleteByMap(columnMap));
        }
    }

    @Override
    public boolean remove(Wrapper<T> wrapper) {
        return SqlHelper.delBool(this.baseMapper.delete(wrapper));
    }

    @Override
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        return SqlHelper.delBool(this.baseMapper.deleteBatchIds(idList));
    }

    @Override
    public boolean updateById(T entity) {
        return retBool(this.baseMapper.updateById(entity));
    }

    @Override
    public boolean update(T entity, Wrapper<T> updateWrapper) {
        return retBool(this.baseMapper.update(entity, updateWrapper));
    }

    @Override
    @Transactional(
            rollbackFor = {Exception.class}
    )
    public boolean updateBatchById(Collection<T> entityList) {
        return this.updateBatchById(entityList, 30);
    }

    @Override
    @Transactional(
            rollbackFor = {Exception.class}
    )
    public boolean updateBatchById(Collection<T> entityList, int batchSize) {
        if (CollectionUtils.isEmpty(entityList)) {
            throw new IllegalArgumentException("Error: entityList must not be empty");
        } else {
            try {
                SqlSession batchSqlSession = this.sqlSessionBatch();
                Throwable var4 = null;

                try {
                    int i = 0;
                    String sqlStatement = this.sqlStatement(SqlMethod.UPDATE_BY_ID);

                    for(Iterator var7 = entityList.iterator(); var7.hasNext(); ++i) {
                        T anEntityList = (T) var7.next();
                        MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap();
                        param.put("et", anEntityList);
                        batchSqlSession.update(sqlStatement, param);
                        if (i >= 1 && i % batchSize == 0) {
                            batchSqlSession.flushStatements();
                        }
                    }

                    batchSqlSession.flushStatements();
                } catch (Throwable var18) {
                    var4 = var18;
                    throw var18;
                } finally {
                    if (batchSqlSession != null) {
                        if (var4 != null) {
                            try {
                                batchSqlSession.close();
                            } catch (Throwable var17) {
                                var4.addSuppressed(var17);
                            }
                        } else {
                            batchSqlSession.close();
                        }
                    }

                }

                return true;
            } catch (Throwable var20) {
                throw ExceptionUtils.mpe("Error: Cannot execute updateBatchById Method. Cause", var20);
            }
        }
    }

    @Override
    public T getById(Serializable id) {
        return this.baseMapper.selectById(id);
    }

    @Override
    public Collection<T> listByIds(Collection<? extends Serializable> idList) {
        return this.baseMapper.selectBatchIds(idList);
    }

    @Override
    public Collection<T> listByMap(Map<String, Object> columnMap) {
        return this.baseMapper.selectByMap(columnMap);
    }

    @Override
    public T getOne(Wrapper<T> queryWrapper) {
        return SqlHelper.getObject(this.baseMapper.selectList(queryWrapper));
    }

    @Override
    public Map<String, Object> getMap(Wrapper<T> queryWrapper) {
        return (Map)SqlHelper.getObject(this.baseMapper.selectMaps(queryWrapper));
    }

    @Override
    public Object getObj(Wrapper<T> queryWrapper) {
        return SqlHelper.getObject(this.baseMapper.selectObjs(queryWrapper));
    }

    @Override
    public int count(Wrapper<T> queryWrapper) {
        return SqlHelper.retCount(this.baseMapper.selectCount(queryWrapper));
    }

    @Override
    public List<T> list(Wrapper<T> queryWrapper) {
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    public IPage<T> page(IPage<T> page, Wrapper<T> queryWrapper) {
        queryWrapper = (Wrapper<T>) SqlHelper.fillWrapper(page, queryWrapper);
        return this.baseMapper.selectPage(page, queryWrapper);
    }

    @Override
    public List<Map<String, Object>> listMaps(Wrapper<T> queryWrapper) {
        return this.baseMapper.selectMaps(queryWrapper);
    }

    @Override
    public List<Object> listObjs(Wrapper<T> queryWrapper) {
        return (List)this.baseMapper.selectObjs(queryWrapper).stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public IPage<Map<String, Object>> pageMaps(IPage<T> page, Wrapper<T> queryWrapper) {
        queryWrapper = (Wrapper<T>) SqlHelper.fillWrapper(page, queryWrapper);
        return this.baseMapper.selectMapsPage(page, queryWrapper);
    }
}
