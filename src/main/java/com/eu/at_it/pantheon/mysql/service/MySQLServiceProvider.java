package com.eu.at_it.pantheon.mysql.service;

import com.eu.at_it.pantheon.client.data.DataClient;
import com.eu.at_it.pantheon.service.Service;
import com.eu.at_it.pantheon.service.data.DataServiceProvider;
import com.google.inject.TypeLiteral;

public class MySQLServiceProvider extends DataServiceProvider {
    public MySQLServiceProvider(DataClient dataClient) {
        super(dataClient);
    }

    @Override
    public Service provide(TypeLiteral<?> servingType) {
        MySQLService<?> mySQLService = mySQLService(servingType);
        mySQLService.init(new MySQLServiceFieldsProvider());
        return mySQLService;
    }

    MySQLService<?> mySQLService(TypeLiteral<?> dataType) {
        return new MySQLService<>(dataClient, dataType);
    }

    @Override
    public TypeLiteral<? extends Service> providerFor() {
        return TypeLiteral.get(MySQLService.class);
    }
}
