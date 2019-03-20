package com.company.plsql;

import org.junit.Test;
import org.sonar.plsqlopen.checks.verifier.PlSqlCheckVerifier;

public class CursorMustBeCloseTest {

    @Test
    public void test() {
        PlSqlCheckVerifier.verify("src/test/resources/cursormustbeclose.sql", new CursorMustBeClose());
    }
    
}
