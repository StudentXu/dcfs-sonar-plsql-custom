package com.company.plsql;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plsqlopen.annnotations.ActivatedByDefault;
import org.sonar.plsqlopen.annnotations.ConstantRemediation;
import org.sonar.plsqlopen.checks.AbstractBaseCheck;
import org.sonar.plsqlopen.checks.Tags;
import org.sonar.plugins.plsqlopen.api.DmlGrammar;
import org.sonar.plugins.plsqlopen.api.PlSqlGrammar;

import com.sonar.sslr.api.AstNode;

@Rule(
		name = "Avoid Update/Delete statement without where clause ",
		description = "<p>\r\n" + 
				"update、delete操作没有where条件\r\n" + 
				"</p>\r\n" + 
				"\r\n" + 
				"<h2>Noncompliant Code Example</h2>\r\n" + 
				"\r\n" + 
				"<pre>\r\n" + 
				"UPDATE rb_acct\r\n" + 
				"   SET acct_status = 'A';\r\n" + 
				"delete from rb_acct;\r\n" + 
				"</pre>\r\n" + 
				"\r\n" + 
				"<h2>Compliant Solution</h2>\r\n" + 
				"\r\n" + 
				"<pre>\r\n" + 
				"UPDATE rb_acct\r\n" + 
				"   SET acct_status = 'A'\r\n"+ 
				"where id = 1;\r\n" +
				"delete from rb_acct where acct_id=1;\r\n" + 
				"</pre>",
				key = "UpdateAndDeleteWithWhereCheck",
				priority = Priority.INFO,
				tags=Tags.CONVENTION
		)
@ConstantRemediation("10min")
@ActivatedByDefault
public class UpdateAndDeleteWithWhereCheck extends AbstractBaseCheck {
	@RuleProperty (
			key = "strict",
			defaultValue = "false",
			description = "设置参数为true时，仅扫描过程内的update/delete语句"
			)
	public boolean strictMode = false;
	@Override
	public void init() {
		subscribeTo(DmlGrammar.DELETE_EXPRESSION);
		subscribeTo(DmlGrammar.UPDATE_EXPRESSION);
	}

	@Override
	public void visitNode(AstNode node) {
		AstNode whereclause = node.getFirstChild(DmlGrammar.WHERE_CLAUSE);
		if (strictMode) {
			if (whereclause == null  && node.getFirstAncestor(PlSqlGrammar.STATEMENTS_SECTION) != null  ) {
				getContext().createLineViolation(this, "The where clause should be included in the delete/update statement.", node);
			}
		}else {
			if (whereclause == null) {
				getContext().createLineViolation(this, "The where clause should be included in the delete/update statement.", node);
			}
		}
	}

}
