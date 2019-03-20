package com.company.plsql;

import org.junit.Test;
import org.sonar.plsqlopen.checks.verifier.PlSqlCheckVerifier;

public class UpdateAndDeleteWithWhereCheckTest {

    @Test
    public void test() {
        PlSqlCheckVerifier.verify("src/test/resources/UpdateAndDeleteWithWhereCheck.sql", new UpdateAndDeleteWithWhereCheck());
    }
    
}
