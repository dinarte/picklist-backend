package com.quebecteh.commons.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.quebecteh.commons.multitenancy.BusinessRulesEventTriggers;

public interface CrudService<T, ID> {

	public T saveOrUpdate(T obj);

	public Optional<T> findById(ID id);

	public List<T> findAll();

	public void deleteById(ID id);

	public boolean existsById(ID id);

	public Iterable<T> saveAll(Iterable<T> objList);
	
	public List<T> findBy(String fieldName, Object value);
	
	public T findOneBy(String fieldName, Object value);
	
	public long countBy(String fieldName, Object value);

	public long countWhere(String critiria);
	
	public void setTrigger(BusinessRulesEventTriggers<T> triggers);
	
	String getNextSerialCode(int size);

	T findFristWhere(String critiria, Map<String, Object> params);
	
	public List<T> findAllWhere(String critiria, Map<String, Object> params);

	Iterable<T> saveOrUpdateAll(Iterable<T> objList);
	
	public void updateField(ID id, String field, Object value);
}