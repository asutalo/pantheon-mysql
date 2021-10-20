package com.eu.at_it.sql_wrapper.service;

import com.eu.at_it.service.DataService;
import com.eu.at_it.sql_wrapper.query.QueryBuilder;

public interface DataAccessService<T> extends DataService<T, QueryBuilder> {
}
