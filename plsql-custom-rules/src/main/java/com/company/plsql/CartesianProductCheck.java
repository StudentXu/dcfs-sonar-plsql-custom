package com.company.plsql;

import java.util.ArrayList;
import java.util.List;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plsqlopen.annnotations.ActivatedByDefault;
import org.sonar.plsqlopen.annnotations.ConstantRemediation;
import org.sonar.plsqlopen.checks.AbstractBaseCheck;
import org.sonar.plugins.plsqlopen.api.DmlGrammar;
import org.sonar.plugins.plsqlopen.api.PlSqlGrammar;
import org.sonar.plugins.plsqlopen.api.PlSqlPunctuator;

import com.sonar.sslr.api.AstNode;

@Rule(
		name = "Avoid Cartesian product of query results.",
		description = "<p>\r\n" + 
				"sql结果集不能出现笛卡尔积。在多表关联查询时，两个表直接必须要有外键关联。\r\n" + 
				"</p>\r\n" + 
				"\r\n" + 
				"<h2>Noncompliant Code Example</h2>\r\n" + 
				"\r\n" + 
				"<pre>\r\n" + 
				"SELECT *\r\n" + 
				"  FROM rb_acct a, rb_base_acct b, rb_aio_acct c\r\n" + 
				" WHERE a.base_acct_no = c.aio_acct_no;\r\n" + 
				"</pre>\r\n" + 
				"\r\n" + 
				"<h2>Compliant Solution</h2>\r\n" + 
				"\r\n" + 
				"<pre>\r\n" + 
				"SELECT *\r\n" + 
				"  FROM rb_acct a, rb_base_acct b, rb_aio_acct c\r\n" + 
				" WHERE a.base_acct_no = c.aio_acct_no AND a.base_acct_no = b.base_acct_no;\r\n" + 
				"END;\r\n" + 
				"</pre>",
				key = "CartesianProductCheck",
				priority = Priority.MAJOR
		)
@ConstantRemediation("10min")
@ActivatedByDefault
public class CartesianProductCheck extends AbstractBaseCheck {
	private static final String CHECK_KEY = "Avoid Cartesian product of query results.";

	@Override
	public void init() {
		subscribeTo(DmlGrammar.SELECT_EXPRESSION);
	}

	@Override
	public void visitNode(AstNode node) {
		if (node.hasAncestor(DmlGrammar.SELECT_EXPRESSION)) {
            return;
        }
		
		if (node.hasDescendant(DmlGrammar.SELECT_EXPRESSION)) {
            return;
        }

		if (node.getFirstDescendant(DmlGrammar.FROM_CLAUSE).getChildren(DmlGrammar.DML_TABLE_EXPRESSION_CLAUSE).size() > 1) {
			List<AstNode> whereClause = node.getChildren(DmlGrammar.WHERE_CLAUSE);			
			if (whereClause.isEmpty()) {
				getContext().createViolation(this, CHECK_KEY, node);
				return;
			}
			List<AstNode> whereComparisonConditions = whereClause.get(0).getDescendants(PlSqlGrammar.COMPARISON_EXPRESSION);
			if (whereComparisonConditions.isEmpty()) {
				getContext().createViolation(this, CHECK_KEY, node);
				return;
			}

			ArrayList<String> tablenames = new ArrayList<>();
			for (AstNode whereComparisonCondition : whereComparisonConditions) {
				for (AstNode tablename : whereComparisonCondition.getDescendants(PlSqlGrammar.VARIABLE_NAME)) {
					if (tablename.getNextAstNode() != null && tablename.getNextAstNode().is(PlSqlPunctuator.DOT))
					tablenames.add(tablename.getTokenValue());				
				}	        	
			}

			for (AstNode table : node.getFirstChild(DmlGrammar.FROM_CLAUSE).getDescendants(DmlGrammar.DML_TABLE_EXPRESSION_CLAUSE)) {
				if (!tablenames.contains(table.getTokenValue()) && table.getFirstChild(DmlGrammar.ALIAS)==null) {
					getContext().createViolation(this, CHECK_KEY, node);
					return;
				}	
				if (table.getFirstChild(DmlGrammar.ALIAS)!=null && !tablenames.contains(table.getFirstChild(DmlGrammar.ALIAS).getTokenValue())) {
					getContext().createViolation(this, CHECK_KEY, node);
					return;
				}
			}

		} 


	}
}
