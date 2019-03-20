package com.company.plsql;

import org.junit.Test;
import org.sonar.plsqlopen.checks.verifier.PlSqlCheckVerifier;

public class NoDataFoundHandleCheckTest {

	@Test
	public void test() {
		PlSqlCheckVerifier.verify("src/test/resources/NoDataFoundHandleCheck.sql", new NoDataFoundHandleCheck());
	}

}
