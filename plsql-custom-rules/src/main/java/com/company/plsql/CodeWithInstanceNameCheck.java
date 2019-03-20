package com.company.plsql;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plsqlopen.annnotations.ActivatedByDefault;
import org.sonar.plsqlopen.annnotations.ConstantRemediation;
import org.sonar.plsqlopen.checks.AbstractBaseCheck;
import org.sonar.plugins.plsqlopen.api.DdlGrammar;
import org.sonar.plugins.plsqlopen.api.DmlGrammar;
import org.sonar.plugins.plsqlopen.api.PlSqlGrammar;
import org.sonar.plugins.plsqlopen.api.PlSqlPunctuator;

import com.sonar.sslr.api.AstNode;

@Rule(
		name = "Do not add instance names in code",
		description = "<p>\r\n" + 
				"代码中不要加实例名\r\n" + 
				"</p>\r\n" + 
				"\r\n" + 
				"<h2>Noncompliant Code Example</h2>\r\n" + 
				"\r\n" + 
				"<pre>\r\n" + 
				"CREATE OR REPLACE PACKAGE BODY ENSEMBLE.AD_SOD\r\n" + 
				"SELECT * INTO v_ad_bill_def FROM ensemble.ad_bill_def;\r\n" + 
				"</pre>\r\n" + 
				"\r\n" + 
				"<h2>Compliant Solution</h2>\r\n" + 
				"\r\n" + 
				"<pre>\r\n" + 
				"CREATE OR REPLACE PACKAGE BODY AD_SOD\r\n" + 
				"SELECT * INTO v_ad_bill_def FROM ad_bill_def;\r\n"+ 
				"</pre>",
				key = "CodeWithInstanceNameCheck",
				priority = Priority.INFO
		)
@ConstantRemediation("10min")
@ActivatedByDefault
public class CodeWithInstanceNameCheck extends AbstractBaseCheck {

	@Override
	public void init() {
		subscribeTo(PlSqlGrammar.CREATE_FUNCTION);
		subscribeTo(PlSqlGrammar.CREATE_PACKAGE);
		subscribeTo(PlSqlGrammar.CREATE_PACKAGE_BODY);
		subscribeTo(PlSqlGrammar.CREATE_PROCEDURE);
		subscribeTo(DmlGrammar.TABLE_REFERENCE);
		subscribeTo(DdlGrammar.CREATE_TABLE);
		subscribeTo(DdlGrammar.CREATE_INDEX);
	}

	@Override
	public void visitNode(AstNode node) {

		if ((node.is(PlSqlGrammar.CREATE_FUNCTION)||node.is(PlSqlGrammar.CREATE_PACKAGE)||node.is(PlSqlGrammar.CREATE_PACKAGE_BODY)
				||node.is(PlSqlGrammar.CREATE_PROCEDURE) ||node.is(DdlGrammar.CREATE_TABLE) || node.is(DdlGrammar.CREATE_INDEX)) && node.getFirstChild(PlSqlGrammar.UNIT_NAME)!=null
				&& node.getFirstChild(PlSqlGrammar.UNIT_NAME).hasDirectChildren(PlSqlPunctuator.DOT)) {
			getContext().createLineViolation(this, "Do not add instance names in code.", node);
		}
		if (node.is(DmlGrammar.TABLE_REFERENCE) && node.hasDirectChildren(PlSqlPunctuator.DOT)) {
			getContext().createLineViolation(this, "Do not add instance names in code.", node);
		}
	}
}
