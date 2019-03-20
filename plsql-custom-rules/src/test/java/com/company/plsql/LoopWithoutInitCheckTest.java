package com.company.plsql;

import org.junit.Test;
import org.sonar.plsqlopen.checks.verifier.PlSqlCheckVerifier;

public class LoopWithoutInitCheckTest {

    @Test
    public void test() {
        PlSqlCheckVerifier.verify("src/test/resources/LoopWithoutInitCheck.sql", new LoopWithoutInitCheck());
    }
    
}
