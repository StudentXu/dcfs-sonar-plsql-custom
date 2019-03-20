package com.company.plsql;

import java.util.List;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plsqlopen.annnotations.ActivatedByDefault;
import org.sonar.plsqlopen.annnotations.ConstantRemediation;
import org.sonar.plsqlopen.checks.AbstractBaseCheck;
import org.sonar.plsqlopen.checks.Tags;
import org.sonar.plugins.plsqlopen.api.PlSqlGrammar;

import com.sonar.sslr.api.AstNode;

@Rule(
		name = "Avoid masking the NO_DATA_FOUND exception",
		description = "<p>\r\n" + 
				"NO_DATA_FOUND异常捕获时不能只有NULL处理,显式赋值可以增强可读性，建议修改\r\n" + 
				"</p>\r\n" + 
				"\r\n" + 
				"<h2>Noncompliant Code Example</h2>\r\n" + 
				"\r\n" + 
				"<pre>\r\n" + 
				"begin\r\n" + 
				"  select empno\r\n" + 
				"    into var\r\n" + 
				"    from emp;\r\n" + 
				"exception\r\n" + 
				"  when NO_DATA_FOUND then\r\n" + 
				"    null;\r\n" + 
				"end;\r\n" + 
				"</pre>\r\n" + 
				"\r\n" + 
				"<h2>Compliant Solution</h2>\r\n" + 
				"\r\n" + 
				"<pre>\r\n" + 
				"begin\r\n" + 
				"  select empno\r\n" + 
				"    into var\r\n" + 
				"    from emp;\r\n" + 
				"exception\r\n" + 
				"  when NO_DATA_FOUND then\r\n" + 
				"    var := null;\r\n" + 
				"end;\r\n" + 
				"</pre>",
				key = "TooManyRowsHandlerCheck",
				priority = Priority.MINOR,
				tags=Tags.CONVENTION
		)
@ConstantRemediation("10min")
@ActivatedByDefault
public class NoDataFoundHandleCheck extends AbstractBaseCheck {

	@Override
	public void init() {
		subscribeTo(PlSqlGrammar.EXCEPTION_HANDLER);
	}

	@Override
	public void visitNode(AstNode node) {
		// is a NO_DATA_FOUND handler
		List<AstNode> exceptions = node.getChildren(PlSqlGrammar.VARIABLE_NAME);

		for (AstNode exception : exceptions) {
			AstNode child = exception.getFirstChild();

			if (child.is(PlSqlGrammar.IDENTIFIER_NAME) && 
					"NO_DATA_FOUND".equalsIgnoreCase(child.getTokenValue())) {
				// and have only one NULL_STATEMENT
				List<AstNode> children = node.getFirstChild(PlSqlGrammar.STATEMENTS).getChildren();
				if (children.size() == 1 && children.get(0).getFirstChild().is(PlSqlGrammar.NULL_STATEMENT)) {
					getContext().createLineViolation(this,"Handle the variables used in the SELECT INTO statements here so their values do not become undefined.", node);
				}
			}
		}
	}
}
