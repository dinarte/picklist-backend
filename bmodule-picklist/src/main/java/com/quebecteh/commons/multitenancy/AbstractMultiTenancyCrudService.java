package com.quebecteh.commons.multitenancy;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.quebecteh.commons.reflaction.utils.EntityUtils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;


public abstract class AbstractMultiTenancyCrudService<T, ID> implements MultiTenancyService<T, ID> {

	@Autowired
	protected MultiTenancyCrudRepository<T, ID> repository;
	
	
	@PersistenceContext
    private EntityManager entityManager;
	


	protected Optional<BusinessRulesEventTriggers<T>> triggers = Optional.empty();
	

	protected Class<T> entityClass;
	
	
	public void setTrigger(BusinessRulesEventTriggers<T> triggers) {
		this.triggers = Optional.ofNullable(triggers);
	} 
	
	
	
	@SuppressWarnings("unchecked")
    public AbstractMultiTenancyCrudService() {
        this.entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }
	
	@SuppressWarnings("unchecked")
	public T saveOrUpdate(T obj) {
		
		triggers.ifPresent(trigger -> {
			Optional<ID> id = (Optional<ID>) EntityUtils.getId(obj);
			if (id.isPresent()) {
				T oldObj = (T) findById(id.get()).get();
				trigger.beforeUpdata(oldObj, obj);
			}else {
				trigger.beforeCreate(obj);
			}
		});
		
	    return repository.save(obj);
	}
	
	@Transactional
	public void updateField(ID id, String field, Object value) {
		String qlString = "update "+entityClass.getSimpleName()+" set " + field + " = :value where id = :id";
		entityManager.createQuery(qlString).setParameter("value", value).setParameter("id", id).executeUpdate();
	}
	
	public Optional<T> findById(ID id) {
	    return (Optional<T>) repository.findById(id);
	}

	public List<T> findAll() {
	    return (List<T>) repository.findAll();
	}

	public void deleteById(ID id) {
		triggers.ifPresent(trigger -> {
			trigger.beforeDelete(findById(id).get());
		});
	    repository.deleteById(id);
	}

	public boolean existsById(ID id) {
	    return repository.existsById(id);
	}

	public List<T> findByTenantId(String tenantId) {
	    return repository.findByTenantId(tenantId);
	}
	
	public Long countByTenantId(String tenantId) {
		return repository.countByTenantId(tenantId);
	}
	
	@Deprecated
	/**
	 * Use saveOrUpdateAll
	 */
	public Iterable<T> saveAll(Iterable<T> objList) {
        return repository.saveAll(objList);
    }
	
	
	@Override
	public Iterable<T> saveOrUpdateAll(Iterable<T> objList) {
		objList.forEach(obj -> {this.saveOrUpdate(obj);});
		return objList;
    }
	

    public List<T> findBy(String fieldName, Object value) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<T> query = (CriteriaQuery<T>) cb.createQuery(entityClass);
        Root<T> root = (Root<T>) query.from(entityClass);
        Predicate predicate = cb.equal(root.get(fieldName), value);
        query.where(predicate);
        return entityManager.createQuery(query).getResultList();
    }
	
	public T findOneBy(String fieldName, Object value) {
	    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
	    CriteriaQuery<T> query = (CriteriaQuery<T>) cb.createQuery(entityClass);
	    Root<T> root = (Root<T>) query.from(entityClass);
	    Predicate predicate = cb.equal(root.get(fieldName), value);
	    query.where(predicate);
	    return entityManager.createQuery(query).setMaxResults(1).getSingleResult();
	}

	@Override
	public long countBy(String fieldName, Object value) {
	    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
	    CriteriaQuery<Long> query = cb.createQuery(Long.class);
	    Root<T> root = (Root<T>) query.from(entityClass);
	    Predicate predicate = cb.equal(root.get(fieldName), value);
	    query.select(cb.count(root)).where(predicate);
	    return entityManager.createQuery(query).getSingleResult();
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public T findFristWhere(String critiria, Map<String, Object> params) {
		String qlStr = "from "+ entityClass.getName() +" where " + critiria;
		Query query = entityManager.createQuery(qlStr);
		query.setMaxResults(1);
		if (params != null) {
			params.entrySet().forEach(entry -> {
				query.setParameter(entry.getKey(), entry.getValue());
			});
		}
		try {
			return (T) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<T> findAllWhere(String critiria, Map<String, Object> params) {
		String qlStr = "from "+ entityClass.getName() +" where " + critiria;
		Query query = entityManager.createQuery(qlStr);
		if (params != null) {
			params.entrySet().forEach(entry -> {
				query.setParameter(entry.getKey(), entry.getValue());
			});
		}
		return (List<T>) query.getResultList();
	}
	
	
	@Override
	public long countWhere(String critiria) {
		String qlStr = "select count(id) from "+ entityClass.getName() +" where " + critiria;
		return (long) entityManager.createQuery(qlStr).getResultList().get(0);
	}
	
	public Long getLastId() {
		String qlStr = "select max(id) from "+ entityClass.getName();
		Long lastId = (Long) entityManager.createQuery(qlStr).getSingleResult();
		return lastId ==null ? 0 : lastId;
	}
	
	public String getNextSerialCode(int size) {
		long nextId = getLastId() + 1;
		String serial = String.format("%0" + size + "d", nextId);
		return serial;
	}

}