package com.delivery.repository;

import java.util.List;

public interface IRepository<T, ID> {
    void create(T entity);

    List<T> readAll();
    T readById(ID id);

    void update(ID id,T entity);

    void delete(ID id);
}
