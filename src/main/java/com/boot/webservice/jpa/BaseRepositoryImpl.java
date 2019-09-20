package com.boot.webservice.jpa;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.Transformers;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自定义repository的方法接口实现类,该类主要提供自定义的公用方法
 */
public class BaseRepositoryImpl <T, ID extends Serializable>
        extends SimpleJpaRepository<T, ID>
        implements BaseRepository<T,ID>{

    /**
     * 持久化上下文
     */
    @PersistenceContext
    private EntityManager entityManager;
    private final Class<T> entityClass;

    public BaseRepositoryImpl(Class<T> domainClass, EntityManager em) {
        super(domainClass, em);
        this.entityManager = em;
        this.entityClass = domainClass;
    }

    /**
     * 查询分页的方法.
     * <pre>
     * 	带着条件进行动态拼接sql的查询方法
     * </pre>
     *
     */
    @Override
    public HashMap<String, Object> sqlQuery(String queryString, String countSql, Map<String, ?> values, int offset,
                                            int limit, String countName, String rowsName) {

        Assert.hasText(queryString, "queryString不能为空");

        HashMap<String, Object> map = new HashMap<>();

        Query query = entityManager.createNativeQuery(queryString);
        Query countQuery = entityManager.createNativeQuery(countSql);

        //给条件赋上值
        if (values != null && !values.isEmpty()) {
            for (Map.Entry<String, ?> entry : values.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
                countQuery.setParameter(entry.getKey(), entry.getValue());
            }
        }

        query.setFirstResult(offset);
        query.setMaxResults(limit);
        query.unwrap(NativeQueryImpl.class).setResultTransformer(new BeanTransformerAdapter(this.entityClass));
        //返回自定义实体，实体需绑定jpa
//        query.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.aliasToBean(this.entityClass));
        //返回map
//        query.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        Object results = query.getResultList();
        Object resultsCount = countQuery.getSingleResult();

        map.put(countName, resultsCount);
        map.put(rowsName, results);

        return map;
    }

    /**
     * 通过(:name)参数绑定指定参数
     * @param queryString
     * @param values
     * @return
     */
    @Override
    public List sqlQuery(String queryString, Map<String, ?> values) {
        Session session = entityManager.unwrap(Session.class);
        //返回类型是List<map>
        SQLQuery query = session.createSQLQuery(queryString);

//		//给条件赋上值
//		if (values != null && !values.isEmpty()) {
//			for (Map.Entry<String, ?> entry : values.entrySet()) {
//			    query.setParameter(entry.getKey(), entry.getValue());
//			}
//		}

        if (values != null) {
            query.setProperties(values);
        }
        //返回自定义实体
        query.setResultTransformer(new BeanTransformerAdapter(this.entityClass));

        return query.list();
    }

    /**
     * 通过？占位符指定参数
     * @param queryString
     * @param values
     * @return
     */
    @Override
    public List sqlQuery(String queryString, Object... values) {
        Query query = entityManager.createNativeQuery(queryString);

        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                query.setParameter(i + 1, values[i]);
            }
        }

        query.unwrap(NativeQueryImpl.class).setResultTransformer(new BeanTransformerAdapter(this.entityClass));

        return query.getResultList();
    }

    @Override
    public HashMap<String, Object> retrieve(String queryString, String countHql, Map<String, ?> values, int offset,
                                            int limit, String countName, String rowsName) {

        HashMap<String, Object> map = new HashMap<>();

        Query query = entityManager.createNativeQuery(queryString);
        Query countQuery = entityManager.createNativeQuery(countHql);

        //给条件赋上值
        if (values != null && !values.isEmpty()) {
            for (Map.Entry<String, ?> entry : values.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
                countQuery.setParameter(entry.getKey(), entry.getValue());
            }
        }

        query.setFirstResult(offset);
        query.setMaxResults(limit);

        Object results = query.getResultList();
        Object resultsCount = countQuery.getSingleResult();

        map.put(countName, resultsCount);
        map.put(rowsName, results);

        return map;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C> List<C> findListByNativeSql(String sql, Class<C> clzss, Object... values) {
        Query query = entityManager.createNativeQuery(sql);

        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                query.setParameter(i + 1, values[i]);
            }
        }

        query.unwrap(NativeQueryImpl.class).setResultTransformer(new BeanTransformerAdapter(clzss));

        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Map> findListToMapByNativeSql(String sql) {
        Query query = entityManager.createNativeQuery(sql);
        query.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        return query.getResultList();
    }

    @Override
    public long executeCount(String qlString, Object... values) {
        Query query = entityManager.createNativeQuery(qlString);
        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                query.setParameter(i + 1, values[i]);
            }
        }
        return ((BigDecimal)query.getSingleResult()).longValue();
    }

    @Override
    public void update(Object... item) {
        if (null != item) {
            for (Object entity : item) {
                entityManager.merge(entity);
            }
        }
    }

    @Override
    public int executeUpdate(String qlString, Object... values) {
        Query query = entityManager.createNativeQuery(qlString);
        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                query.setParameter(i + 1, values[i]);
            }
        }
        return query.executeUpdate();
    }

    @Override
    public int executeUpdate(String qlString, Map<String, Object> params) {
        Query query = entityManager.createNativeQuery(qlString);
        for (String name : params.keySet()) {
            query.setParameter(name, params.get(name));
        }
        return query.executeUpdate();
    }

    @Override
    public int executeUpdate(String qlString, List<Object> values) {
        Query query = entityManager.createNativeQuery(qlString);
        for (int i = 0; i < values.size(); i++) {
            query.setParameter(i + 1, values.get(i));
        }
        return query.executeUpdate();
    }

}
