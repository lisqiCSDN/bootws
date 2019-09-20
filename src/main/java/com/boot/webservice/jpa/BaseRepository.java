package com.boot.webservice.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自定义Repository的方法接口
 * @author xiaowen
 * @param <T> 领域对象即实体类
 * @param <ID>领域对象的注解
 */
@NoRepositoryBean
public interface BaseRepository <T,ID extends Serializable> extends JpaRepository<T,ID>,
        JpaSpecificationExecutor<T> {

    /**
     * 分页参数绑定
     * @param queryString
     * @param countSql
     * @param values
     * @param offset
     * @param limit
     * @param countName
     * @param rowsName
     * @return
     */
    HashMap<String, Object> sqlQuery(String queryString, String countSql, Map<String, ?> values, int offset, int limit, String countName, String rowsName);

    /**
     * sql查询参数绑定
     * @param queryString
     * @param values
     * @return
     */
    List<T> sqlQuery(String queryString, Map<String, ?> values);

    /**
     * sql查询占位符
     * @param queryString
     * @param values
     * @return
     */
    List<T> sqlQuery(String queryString, Object... values);

    /**
     * 分页参数绑定
     * @param queryString
     * @param countHql
     * @param values
     * @param offset
     * @param limit
     * @param countName
     * @param rowsName
     * @return
     */
    HashMap<String, Object> retrieve(String queryString, String countHql, Map<String, ?> values, int offset, int limit, String countName, String rowsName);

    /**
     * 使用原生sql 查询 list列表
     * @param sql
     * @param clzss
     * @return
     */
    <C> List<C> findListByNativeSql(String sql, Class<C> clzss, Object... values);

    /**
     * 使用原生sql 查询 list列表
     * @param sql
     * @return
     */
    List<Map> findListToMapByNativeSql(String sql);

    /**
     * 执行ql语句
     * @param qlString 基于jpa标准的ql语句
     * @param values ql中的?参数值,单个参数值或者多个参数值
     * @return 返回执行后受影响的数据个数
     */
    long executeCount(String qlString, Object... values);

    /**
     * 更新对象数据
     *
     * @param item
     * 持久对象，或者对象集合
     * @throws Exception
     */
    void update(Object... item);

    /**
     * 执行ql语句
     * @param qlString 基于jpa标准的ql语句
     * @param values ql中的?参数值,单个参数值或者多个参数值
     * @return 返回执行后受影响的数据个数
     */
    int executeUpdate(String qlString, Object... values);

    /**
     * 执行ql语句
     * @param qlString 基于jpa标准的ql语句
     * @param params key表示ql中参数变量名，value表示该参数变量值
     * @return 返回执行后受影响的数据个数
     */
    int executeUpdate(String qlString, Map<String, Object> params);

    /**
     * 执行ql语句，可以是更新或者删除操作
     * @param qlString 基于jpa标准的ql语句
     * @param values ql中的?参数值
     * @return 返回执行后受影响的数据个数
     * @throws Exception
     */
    int executeUpdate(String qlString, List<Object> values);
}
