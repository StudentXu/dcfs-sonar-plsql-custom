package com.company.plsql;

import org.junit.Test;
import org.sonar.plsqlopen.checks.verifier.PlSqlCheckVerifier;

public class MultipleQueriesCheckTest {

    @Test
    public void test() {
        PlSqlCheckVerifier.verify("src/test/resources/MultipleQueriesCheck.sql", new MultipleQueriesCheck());
    }
    
}
