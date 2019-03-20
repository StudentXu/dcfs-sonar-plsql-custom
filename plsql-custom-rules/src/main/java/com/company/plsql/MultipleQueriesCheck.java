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
import com.sonar.sslr.api.AstNode;


@Rule(
		name = "Avoid multiple queries on the same table",
		description = "<p>\r\n" + 
				"一个过程中多次对同一张表进行select操作，可以合并成一次，将不同的多个字段都查询出来\r\n" + 
				"</p>\r\n" + 
				"\r\n" + 
				"<h2>Noncompliant Code Example</h2>\r\n" + 
				"\r\n" + 
				"<pre>\r\n" + 
				"BEGIN\r\n" + 
				"   SELECT acct_Type\r\n" + 
				"     INTO v_acct_Type\r\n" + 
				"     FROM rb_acct\r\n" + 
				"    WHERE internal_key = :p_internal_key;\r\n" + 
				"    \r\n" + 
				"   SELECT ccy   -- Noncompliant\r\n" + 
				"     INTO v_ccy\r\n" + 
				"     FROM rb_acct\r\n" + 
				"    WHERE internal_key = :p_internal_key;\r\n" + 
				"    \r\n" + 
				"END;\r\n" + 
				"</pre>\r\n" + 
				"\r\n" + 
				"<h2>Compliant Solution</h2>\r\n" + 
				"\r\n" + 
				"<pre>\r\n" + 
				"BEGIN\r\n" + 
				"   SELECT acct_Type,ccy\r\n" + 
				"     INTO v_acct_Type,v_ccy\r\n" + 
				"     FROM rb_acct\r\n" + 
				"    WHERE internal_key = :p_internal_key;\r\n" + 
				"END;\r\n" + 
				"</pre>",
				key = "MultipleQueriesCheck",
				priority = Priority.INFO
		)
@ConstantRemediation("10min")
@ActivatedByDefault
public class MultipleQueriesCheck extends AbstractBaseCheck {

	ArrayList<TableReference> tableReferences=new ArrayList<>();



	@Override
	public void init() {
		subscribeTo(PlSqlGrammar.STATEMENTS_SECTION);
	}

	@Override
	public void visitNode(AstNode node) {
		if (node.hasAncestor(PlSqlGrammar.STATEMENTS_SECTION)){
			return;
		}
		List<AstNode> statements = node.getDescendants(DmlGrammar.SELECT_EXPRESSION);
		tableReferences.clear();
		if(statements.size() < 2 ) {
			return;
		}
		for (AstNode statement : statements){
			if (statement.hasAncestor(DmlGrammar.SELECT_EXPRESSION) ||statement.hasAncestor(DmlGrammar.UPDATE_EXPRESSION) ||statement.hasAncestor(DmlGrammar.DELETE_EXPRESSION)) {
				continue;
			}
			try {
				AstNode fromclause = statement.getFirstDescendant(DmlGrammar.FROM_CLAUSE);
				AstNode whereclause = statement.getFirstDescendant(DmlGrammar.WHERE_CLAUSE);

				if (fromclause != null && checkReference(fromclause,whereclause) ) {
					getContext().createViolation(this, "There should not be multiple queries on the same table.", statement);	                
				}else {
					tableReferences.add(new TableReference(fromclause, whereclause));
				}

			}catch(Exception e){
				continue;
			}    	
		}
	}
	
	

	
	private boolean checkReference(AstNode fromclause, AstNode whereclause) {    	
		if (!tableReferences.isEmpty() && fromclause != null) {
			String newfrom =fromclause.getTokens().toString();
			String newwhere = "";
			if (whereclause != null) { 
			newwhere =whereclause.getTokens().toString();
			}

			for (TableReference reference : tableReferences) {
				String refwhere="";
				String reffrom = reference.reffrom.getTokens().toString();
				if (reference.refwhere!=null) {
				refwhere = reference.refwhere.getTokens().toString();
				}
			
				if ((!newwhere.isEmpty()  && newfrom.equals(reffrom) && newwhere.equals(refwhere)) || 
				(newwhere.isEmpty() && refwhere.isEmpty()  && newfrom.equals(reffrom) && !newfrom.contains("IDENTIFIER: DUAL"))) {
					return true;
				}
				
			}
		}   

		return false;
	}

	


	class TableReference {
		public final AstNode reffrom;
		public final AstNode refwhere;

		public TableReference(AstNode fromclause, AstNode whereclause) {
			this.reffrom = fromclause;
			this.refwhere = whereclause;
		}
	}

}
