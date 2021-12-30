package com.eu.at_it.pantheon.mysql.service;

import com.eu.at_it.pantheon.mysql.query.QueryBuilder;
import com.eu.at_it.pantheon.service.data.DataService;

public interface DataAccessService<T> extends DataService<T, QueryBuilder> {
}
