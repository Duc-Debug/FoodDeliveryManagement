package com.delivery.repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.delivery.exception.NotFoundException;
import com.delivery.exception.ValidationException;

public abstract class GenericsRepository<T,ID> implements IRepository<T,ID> {
    
protected final Map<ID,T> storage;

protected GenericsRepository(){
    this.storage = new LinkedHashMap<>();
}
protected abstract ID getId(T entity);

@Override
public void create(T entity){
    ID id = getId(entity);
    if(storage.containsKey(id)){
        throw new ValidationException("Duplicate id: " + id);
    }
    storage.put(id, entity);
}

@Override
public List<T> readAll(){
    return new ArrayList<>(storage.values());
}
@Override
public T readById(ID id){
    if(!storage.containsKey(id)){
        throw new NotFoundException("Not Found in DA: " + id);
    }
    return storage.get(id);
}

@Override
public void update(ID id, T entity){
    if(!storage.containsKey(id)){
        throw new NotFoundException("Not Found in DA: " + id);
    }
    storage.put(id, entity);
}

@Override
public void delete(ID id){
    if(!storage.containsKey(id)){
        throw new NotFoundException("Not Found in DA: " + id);
    }
    storage.remove(id);
}
}
