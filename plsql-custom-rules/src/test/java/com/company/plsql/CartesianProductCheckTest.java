package com.company.plsql;

import org.junit.Test;
import org.sonar.plsqlopen.checks.verifier.PlSqlCheckVerifier;

public class CartesianProductCheckTest {

    @Test
    public void test() {
        PlSqlCheckVerifier.verify("src/test/resources/CartesianProductCheck.sql", new CartesianProductCheck());
    }
    
}
