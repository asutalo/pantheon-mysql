package com.eu.at_it.pantheon.mysql.query;

import com.eu.at_it.pantheon.helper.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SelectWithAliases extends KeyWord implements QueryPart {
    static final String SELECT = "SELECT ";
    static final String AS = " AS ";
    static final String SEPARATOR = ", ";
    private final List<Pair<String, String>> columnsAndAliases;

    public SelectWithAliases(ArrayList<Pair<String, String>> columnsAndAliases) {
        this.columnsAndAliases = columnsAndAliases;
    }

    @Override
    public String apply(String query) {
        StringBuilder selectionStringBuilder = new StringBuilder(query.concat(SELECT));

        Pair<String, String> columnAndAlias = columnsAndAliases.get(0);

        selectionStringBuilder.append(columnAndAlias.left());
        selectionStringBuilder.append(AS);
        selectionStringBuilder.append(columnAndAlias.right());

        columnsAndAliases.remove(0);

        for (Pair<String, String> cAndA : columnsAndAliases) {
            selectionStringBuilder.append(SEPARATOR);
            selectionStringBuilder.append(cAndA.left());
            selectionStringBuilder.append(AS);
            selectionStringBuilder.append(cAndA.right());
        }
        return selectionStringBuilder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SelectWithAliases that = (SelectWithAliases) o;
        return Objects.equals(columnsAndAliases, that.columnsAndAliases);
    }

    @Override
    public int hashCode() {
        return Objects.hash(columnsAndAliases);
    }
}
