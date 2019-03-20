package com.company.plsql;

import org.junit.Test;
import org.sonar.plsqlopen.checks.verifier.PlSqlCheckVerifier;

public class FileMustBeCloseTest {

    @Test
    public void test() {
        PlSqlCheckVerifier.verify("src/test/resources/filemustbeclose.sql", new FileMustBeClose());
    }
    
}
