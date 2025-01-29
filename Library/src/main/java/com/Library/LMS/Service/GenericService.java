package com.Library.LMS.Service;

import java.util.List;

public interface GenericService<T, ID> {

    T update(ID id, T dto);
    void delete(ID id);
    T getById(ID id);
    List<T> getAll();
}