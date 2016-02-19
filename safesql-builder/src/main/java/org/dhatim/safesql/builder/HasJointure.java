package org.dhatim.safesql.builder;

public interface HasJointure {

    Jointure innerJoin(String schema, String tableName, Alias alias);
    
    Jointure insertInnerJoin(Jointure before, String schema, String tableName, Alias alias);
    
    Jointure leftJoin(String schema, String tableName, Alias alias);
    
    Jointure insertLeftJoin(Jointure before, String schema, String tableName, Alias alias);
    
    default Jointure innerJoin(String tableName) {
        return innerJoin(null, tableName, null);
    }
    
    default Jointure innerJoin(String schema, String tableName) {
        return innerJoin(schema, tableName, null);
    }
    
    default Jointure innerJoin(String tableName, Alias alias) {
        return innerJoin(null, tableName, alias);
    }
    
    default Jointure leftJoin(String tableName) {
        return leftJoin(null, tableName, null);
    }
    
    default Jointure leftJoin(String schema, String tableName) {
        return leftJoin(schema, tableName, null);
    }
    
    default Jointure leftJoin(String tableName, Alias alias) {
        return leftJoin(null, tableName, alias);
    }
    
}
