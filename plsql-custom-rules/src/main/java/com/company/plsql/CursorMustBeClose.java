package com.company.plsql;

import java.util.ArrayList;
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
		name = "Cursor Must Be Closed",
		description = "<p>\r\n" + 
				"如果游标打开，在该方法中必须游标关闭\r\n" + 
				"</p>\r\n" + 
				"\r\n" + 
				"<h2>Noncompliant Code Example</h2>\r\n" + 
				"\r\n" + 
				"<pre>\r\n" + 
				"CURSOR c_atm_settle \r\n" + 
				"      IS\r\n" + 
				"         SELECT ……;\r\n" + 
				"      ……;\r\n" + 
				"    BEGIN\r\n" + 
				"      \r\n" + 
				"      OPEN c_atm_settle;\r\n" + 
				"\r\n" + 
				"         FETCH c_atm_settle\r\n" + 
				"          INTO /*变量*/;\r\n" + 
				"\r\n" + 
				"    END;\r\n" + 
				"\r\n"+
				"\r\n"+
				"OPEN c_atm_settle (SELECT….);\r\n" + 
				"\r\n" + 
				"         FETCH c_atm_settle\r\n" + 
				"          INTO ……;\r\n" + 
				"</pre>\r\n" + 
				"\r\n" + 
				"<h2>Compliant Solution</h2>\r\n" + 
				"\r\n" + 
				"<pre>\r\n" + 
				"CURSOR c_atm_settle \r\n" + 
				"      IS\r\n" + 
				"         SELECT ……;\r\n" + 
				"      ……\r\n" + 
				"    BEGIN\r\n" + 
				"      OPEN c_atm_settle;\r\n" + 
				"\r\n" + 
				"         FETCH c_atm_settle\r\n" + 
				"          INTO /*变量*/;        \r\n" + 
				"         ……\r\n" + 
				"         CLOSE c_atm_settle;   \r\n" + 
				"      END;\r\n" + 
				"\r\n" +
				"OPEN c_atm_settle (SELECT….);\r\n" + 
				"\r\n" + 
				"         FETCH c_atm_settle\r\n" + 
				"          INTO ……;\r\n" + 
				"\r\n" + 
				"         ……\r\n" + 
				"\r\n" + 
				"         CLOSE c_atm_settle;\r\n" + 
				"</pre>",
				key = "CursorMustBeClosed",
				priority = Priority.MAJOR,
				tags=Tags.BUG
		)
@ConstantRemediation("10min")
@ActivatedByDefault
public class CursorMustBeClose extends AbstractBaseCheck {
	@Override
	public void init() {
		subscribeTo(PlSqlGrammar.STATEMENTS_SECTION);

	}

	@Override
	public void visitNode(AstNode node) {

		List<AstNode> statements = node.getFirstChild(PlSqlGrammar.STATEMENTS).getChildren();    	
		if(statements!=null && !statements.isEmpty()){

			List<String> closecurname = new ArrayList<>();    
			for(int i = 0 ; i < statements.size() ; i++) {
				AstNode statement = statements.get(i);
				if (statement != null && statement.getTokenOriginalValue().equalsIgnoreCase("close") && statement.getFirstChild(PlSqlGrammar.CLOSE_STATEMENT) !=null){
					if (statement.getFirstChild(PlSqlGrammar.CLOSE_STATEMENT).getFirstChild(PlSqlGrammar.VARIABLE_NAME) !=null) {
						closecurname.add(statement.getFirstChild(PlSqlGrammar.CLOSE_STATEMENT).getFirstChild(PlSqlGrammar.VARIABLE_NAME).getTokenOriginalValue());
					}else if (statement.getFirstChild(PlSqlGrammar.CLOSE_STATEMENT).getFirstChild(PlSqlGrammar.MEMBER_EXPRESSION) !=null) {
						try {
							String variable=statement.getFirstChild(PlSqlGrammar.CLOSE_STATEMENT).getFirstChild(PlSqlGrammar.MEMBER_EXPRESSION).getFirstChild(PlSqlGrammar.VARIABLE_NAME).getTokenOriginalValue();
							String iden=statement.getFirstChild(PlSqlGrammar.CLOSE_STATEMENT).getFirstChild(PlSqlGrammar.MEMBER_EXPRESSION).getFirstChild(PlSqlGrammar.IDENTIFIER_NAME).getTokenOriginalValue();
							String clostr=variable + '.' + iden;
							closecurname.add(clostr);
						}
						catch(Exception e){
							continue;    					 
						}
					}
				}
				if(statement !=null && statement.getTokenOriginalValue().equalsIgnoreCase("if") 
						&& statement.getFirstChild(PlSqlGrammar.IF_STATEMENT).getFirstChild(PlSqlGrammar.MEMBER_EXPRESSION) !=null 
						&& statement.getFirstChild(PlSqlGrammar.IF_STATEMENT).getFirstChild(PlSqlGrammar.MEMBER_EXPRESSION).getFirstChild(PlSqlGrammar.IDENTIFIER_NAME).getTokenOriginalValue().equalsIgnoreCase("isopen")) {
					try{
						closecurname.add(statement.getFirstChild(PlSqlGrammar.IF_STATEMENT).getFirstChild(PlSqlGrammar.STATEMENTS).getFirstChild(PlSqlGrammar.STATEMENT).getFirstChild(PlSqlGrammar.CLOSE_STATEMENT).getFirstChild(PlSqlGrammar.VARIABLE_NAME).getTokenOriginalValue());
					}
					catch(Exception e){
						continue;    					 
					}
					try{
						AstNode tmp=statement.getFirstChild(PlSqlGrammar.IF_STATEMENT).getFirstChild(PlSqlGrammar.STATEMENTS).getFirstChild(PlSqlGrammar.STATEMENT).getFirstChild(PlSqlGrammar.CLOSE_STATEMENT).getFirstChild(PlSqlGrammar.MEMBER_EXPRESSION);
						String ifvariable=tmp.getFirstChild(PlSqlGrammar.VARIABLE_NAME).getTokenOriginalValue();
						String ifiden=tmp.getFirstChild(PlSqlGrammar.IDENTIFIER_NAME).getTokenOriginalValue();
						String ifclostr=ifvariable + '.' + ifiden;
						closecurname.add(ifclostr);
					}
					catch(Exception e){
						continue;    					 
					}
				}

			}


			for(int i = 0 ; i < statements.size() ; i++) {
				AstNode statement =  statements.get(i);
				AstNode opennod = null;
				if (statement != null && statement.getTokenOriginalValue().equalsIgnoreCase("open")) {
					if (statement.getFirstChild(PlSqlGrammar.OPEN_STATEMENT) !=null) {
						opennod = statement.getFirstChild(PlSqlGrammar.OPEN_STATEMENT);
					} else if(statement.getFirstChild(PlSqlGrammar.OPEN_FOR_STATEMENT) !=null) {
						opennod = statement.getFirstChild(PlSqlGrammar.OPEN_FOR_STATEMENT);
					} 		


					if (opennod != null && opennod.getFirstChild(PlSqlGrammar.VARIABLE_NAME) !=null){
						String opencurname=opennod.getFirstChild(PlSqlGrammar.VARIABLE_NAME).getTokenOriginalValue();
						if (closecurname ==null || closecurname.isEmpty()) {
							getContext().createViolation(this, "The cursor must be closed.", statement);
						}
						if (opencurname !=null && closecurname !=null && !closecurname.isEmpty() && !closecurname.contains(opencurname)) {
							getContext().createViolation(this, "The cursor must be closed.", statement);
						}   	        	    	        	
					}else if (opennod != null && opennod.getFirstChild(PlSqlGrammar.MEMBER_EXPRESSION) !=null) {
						String openvariable=opennod.getFirstChild(PlSqlGrammar.MEMBER_EXPRESSION).getFirstChild(PlSqlGrammar.VARIABLE_NAME).getTokenOriginalValue();
						String openiden=opennod.getFirstChild(PlSqlGrammar.MEMBER_EXPRESSION).getFirstChild(PlSqlGrammar.IDENTIFIER_NAME).getTokenOriginalValue();
						String openstr=openvariable + '.' + openiden;
						if (closecurname ==null || closecurname.isEmpty()) {
							getContext().createViolation(this, "The cursor must be closed.", statement);
						}
						if (openstr !=null && closecurname !=null && !closecurname.isEmpty() && !closecurname.contains(openstr)) {
							getContext().createViolation(this, "The cursor must be closed.", statement);
						}   	
					}
				}

			}
		}
	}
}






