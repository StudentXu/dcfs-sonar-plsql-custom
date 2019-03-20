package com.company.plsql;

import org.junit.Test;
import org.sonar.plsqlopen.checks.verifier.PlSqlCheckVerifier;

public class CreateTableWithTableSpaceCheckTest {

    @Test
    public void test() {
        PlSqlCheckVerifier.verify("src/test/resources/CreateTableWithTableSpaceCheck.sql", new CreateTableWithTableSpaceCheck());
    }
    
}
