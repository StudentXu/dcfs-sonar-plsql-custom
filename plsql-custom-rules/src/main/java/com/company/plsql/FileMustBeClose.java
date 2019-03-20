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
		name = "File Must Be Closed",
		description = "<p>\r\n" + 
				"文件句柄如果打开，则在同一方法中必须关闭句柄\r\n" + 
				"</p>\r\n" + 
				"\r\n" + 
				"<h2>Noncompliant Code Example</h2>\r\n" + 
				"\r\n" + 
				"<pre>\r\n" + 
				"v_file_handle :=\r\n" + 
				"         UTL_FILE.fopen (file_path,file_name,'R',32767);\r\n" + 
				"\r\n" + 
				"</pre>\r\n" + 
				"\r\n" + 
				"<h2>Compliant Solution</h2>\r\n" + 
				"\r\n" + 
				"<pre>\r\n" + 
				"v_file_handle :=\r\n" + 
				"         UTL_FILE.fopen (file_path,file_name,'R',32767);\r\n" + 
				"\r\n" + 
				"/*过程代码*/\r\n" + 
				"UTL_FILE.fclose (v_file_handle);\r\n" + 
				"</pre>",
				key = "FileMustBeClosed",
				priority = Priority.MAJOR,
				tags=Tags.BUG
		)
@ConstantRemediation("10min")
@ActivatedByDefault
public class FileMustBeClose extends AbstractBaseCheck {
	@Override
	public void init() {

		subscribeTo(PlSqlGrammar.STATEMENTS_SECTION);

	}

	@SuppressWarnings("deprecation")
	@Override
	public void visitNode(AstNode node) {

		if (node.getParent().hasParent(PlSqlGrammar.DECLARE_SECTION) || node.getParent().hasAncestor(PlSqlGrammar.ANONYMOUS_BLOCK)) {

			List<AstNode> methods = node.findChildren(PlSqlGrammar.METHOD_CALL);
			if(methods!=null && !methods.isEmpty()){
				List<String> closefilename = new ArrayList<>();
				for(int i = 0 ; i < methods.size() ; i++) {
					try {
						AstNode statement =  methods.get(i);
						String idename = statement.getFirstChild(PlSqlGrammar.MEMBER_EXPRESSION).getFirstChild(PlSqlGrammar.IDENTIFIER_NAME).getTokenOriginalValue();
						if (statement.getFirstChild(PlSqlGrammar.MEMBER_EXPRESSION).getTokenOriginalValue().equalsIgnoreCase("utl_file") && idename.equalsIgnoreCase("fclose")) {
							closefilename.add(statement.getFirstChild(PlSqlGrammar.ARGUMENTS).getFirstChild(PlSqlGrammar.ARGUMENT).getTokenOriginalValue());
						}
					}
					catch(Exception e){
						continue;    					 
					}    			
				}

				for(int i = 0 ; i < methods.size() ; i++) {
					AstNode statement =  methods.get(i);
					try {
						AstNode opennod =statement.getFirstChild(PlSqlGrammar.MEMBER_EXPRESSION);
						String openiden = opennod.getFirstChild(PlSqlGrammar.IDENTIFIER_NAME).getTokenOriginalValue();
						String openfilename = statement.getPreviousAstNode().getPreviousAstNode().getTokenOriginalValue();
						if (opennod.getTokenOriginalValue().equalsIgnoreCase("utl_file") && openiden.equalsIgnoreCase("fopen")) {   					
							if (closefilename ==null || closefilename.isEmpty()) {
								getContext().createViolation(this, "The file must be closed.", statement);
							}

							if (closefilename !=null && !closefilename.isEmpty() && !closefilename.contains(openfilename)) {
								getContext().createViolation(this, "The file must be closed.", statement);
							} 

						}
					}
					catch(Exception e){
						continue;    					 
					}    		
				}

			}
		}

	}
}






