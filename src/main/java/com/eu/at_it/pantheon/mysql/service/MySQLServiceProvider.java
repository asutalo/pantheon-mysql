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
        return new MySQLService<>(dataClient, servingType);
    }

    @Override
    public TypeLiteral<? extends Service> providerFor() {
        return TypeLiteral.get(MySQLService.class);
    }
}
