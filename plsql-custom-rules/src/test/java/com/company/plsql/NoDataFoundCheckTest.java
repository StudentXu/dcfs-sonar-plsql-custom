package com.company.plsql;

import org.junit.Test;
import org.sonar.plsqlopen.checks.verifier.PlSqlCheckVerifier;

public class NoDataFoundCheckTest {

    @Test
    public void test() {
        PlSqlCheckVerifier.verify("src/test/resources/NoDataFoundCheck.sql", new NoDataFoundCheck());
    }
    
}
