package com.company.plsql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plsqlopen.annnotations.ActivatedByDefault;
import org.sonar.plsqlopen.annnotations.ConstantRemediation;
import org.sonar.plsqlopen.checks.AbstractBaseCheck;
import org.sonar.plsqlopen.checks.Tags;
import org.sonar.plugins.plsqlopen.api.DmlGrammar;
import org.sonar.plugins.plsqlopen.api.PlSqlGrammar;

import com.sonar.sslr.api.AstNode;

@Rule(
		name = "NO_DATA_FOUND must be caught",
		description = "<p>\r\n" + 
				"SELECT语句（非聚合操作）必须有NO_DATA_FOUND 异常捕获\r\n" + 
				"</p>\r\n" + 
				"\r\n" + 
				"<h2>Noncompliant Code Example</h2>\r\n" + 
				"\r\n" + 
				"<pre>\r\n" + 
				"SELECT branch\r\n" + 
				"           INTO v_branch\r\n" + 
				"           FROM gl_saving_base_acct\r\n" + 
				"          WHERE acct_no = v_input.acct_no;\r\n" + 
				"\r\n" + 
				"</pre>\r\n" + 
				"\r\n" + 
				"<h2>Compliant Solution</h2>\r\n" + 
				"\r\n" + 
				"<pre>\r\n" + 
				"BEGIN\r\n" + 
				"         SELECT branch\r\n" + 
				"           INTO v_branch\r\n" + 
				"           FROM gl_saving_base_acct\r\n" + 
				"          WHERE acct_no = v_input.acct_no;\r\n" + 
				"      EXCEPTION\r\n" + 
				"         WHEN NO_DATA_FOUND    \r\n" + 
				"         THEN\r\n" + 
				"            /* 未查找到的处理*/                                 \r\n" + 
				"      END;\r\n" + 
				"\r\n" + 
				"</pre>",
				key = "NoDataFoundCheck",
				priority = Priority.MAJOR,
				tags=Tags.BUG
		)
@ConstantRemediation("10min")
@ActivatedByDefault
public class NoDataFoundCheck extends AbstractBaseCheck {
	private static final List<String> methodlist = Arrays.asList("AVG", "MAX","MIN","STDDEV","SUM","COUNT","MEDIAN");   
	@Override
	public void init() {
		subscribeTo(PlSqlGrammar.STATEMENTS_SECTION);
	}

	@Override
	public void visitNode(AstNode node) {
		List<AstNode> statements = node.getFirstChild(PlSqlGrammar.STATEMENTS).getChildren();
		if(statements!=null && !statements.isEmpty()){    		
			for(AstNode statement : statements) {
				if (statement != null && statement.getTokenOriginalValue().equalsIgnoreCase("select")) {
					if (statement.getFirstChild(PlSqlGrammar.SELECT_STATEMENT).getFirstChild(DmlGrammar.SELECT_EXPRESSION).getFirstChild(DmlGrammar.INTO_CLAUSE) == null) {
						continue;
					}
					if (methodlist.contains(statement.getFirstChild(PlSqlGrammar.SELECT_STATEMENT).getFirstChild(DmlGrammar.SELECT_EXPRESSION).getFirstChild(DmlGrammar.SELECT_COLUMN).getTokenOriginalValue().toUpperCase())){
						continue;
					}

					 List<AstNode> excepts = node.getChildren(PlSqlGrammar.EXCEPTION_HANDLER);
					 List<String> varis =  new ArrayList<>();  
					if (excepts.isEmpty()) {
						getContext().createViolation(this, "The SELECT statement must have NO_DATA_FOUND exception capture.", statement);
					}else {						
						for (AstNode except :excepts ) {
							AstNode vari = except.getFirstChild(PlSqlGrammar.VARIABLE_NAME);
							if (vari != null) {
							varis.add(vari.getTokenOriginalValue().toLowerCase()); 
							}		
						}
						if (varis.isEmpty() || !varis.contains("no_data_found")) {
							getContext().createViolation(this, "The SELECT statement must have NO_DATA_FOUND exception capture.", statement);
						}

					}
				}
			}    		
		}

	}

}
