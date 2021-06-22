package com.eu.at_it.sql_wrapper.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.RowSet;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import java.sql.SQLException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class CachedRowSetConversionFunctionTest {
    @Mock
    private RowSetFactory mockRowSetFactory;

    @InjectMocks
    private CachedRowSetConversionFunction cachedRowSetConversionFunction;

    @Mock
    private RowSet mockRowSet;

    @Test
    void apply_shouldReturnPopulatedCachedResultSet() throws SQLException {
        CachedRowSet mockCachedRowSet = mock(CachedRowSet.class);
        when(mockRowSetFactory.createCachedRowSet()).thenReturn(mockCachedRowSet);

        cachedRowSetConversionFunction.apply(mockRowSet);

        verify(mockCachedRowSet).populate(mockRowSet);
    }

    @Test
    void apply_shouldThrowRuntimeException() throws SQLException {
        when(mockRowSetFactory.createCachedRowSet()).thenThrow(SQLException.class);

        Assertions.assertThrows(RuntimeException.class, () -> cachedRowSetConversionFunction.apply(mockRowSet));
    }
}