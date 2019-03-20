package com.company.plsql;

import org.junit.Test;
import org.sonar.plsqlopen.checks.verifier.PlSqlCheckVerifier;

public class CodeWithInstanceNameCheckTest {

    @Test
    public void test() {
        PlSqlCheckVerifier.verify("src/test/resources/CodeWithInstanceNameCheck.sql", new CodeWithInstanceNameCheck());
    }
    
}
