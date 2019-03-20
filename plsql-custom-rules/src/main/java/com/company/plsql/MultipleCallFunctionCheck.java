package com.company.plsql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.sonar.api.utils.WildcardPattern;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plsqlopen.annnotations.ActivatedByDefault;
import org.sonar.plsqlopen.annnotations.ConstantRemediation;
import org.sonar.plsqlopen.checks.AbstractBaseCheck;
import org.sonar.plugins.plsqlopen.api.PlSqlGrammar;

import com.sonar.sslr.api.AstNode;

@Rule(
		name = "Avoid multiple calls to the same function.",
		description = "<p>\r\n" + 
				"一个过程中同一个function多次调用,应该尽量减少调用次数，简洁代码，提升效率\r\n" + 
				"</p>\r\n" + 
				"\r\n" + 
				"<h2>Noncompliant Code Example</h2>\r\n" + 
				"\r\n" + 
				"<pre>\r\n" + 
				"CREATE OR REPLACE PACKAGE BODY ENSEMBLE.AD_SOD \r\n" + 
				"IS\r\n" + 
				"procedure print_sdept(psno char) as\r\n" + 
				"    psdept student.sdept%type;\r\n" + 
				"begin\r\n" + 
				"	RBSTOR.get_acct_detail_aio (v_acct_no, \r\n" + 
				"                                 p_acct_stats.aio_internal_key,\r\n" + 
				"                                 v_acct_det\r\n" + 
				"                                );\r\n" + 
				"    select sdept into psdept\r\n" + 
				"    from student\r\n" + 
				"    where sno=psno;\r\n" + 
				"    RBSTOR.get_acct_detail_aio (v_acct_no,\r\n" + 
				"                                 p_acct_stats.aio_internal_key,\r\n" + 
				"                                 v_acct_det\r\n" + 
				"                                );\r\n" + 
				"    RBSTOR.get_acct_detail_aio (v_acct_no,     -- Noncompliant\r\n" + 
				"                                 p_acct_stats.aio_internal_key,\r\n" + 
				"                                 v_acct_det\r\n" + 
				"                                ); \r\n" + 
				"end;\r\n" + 
				"end;\r\n" + 
				"</pre>\r\n" + 
				"\r\n" + 
				"<h2>Compliant Solution</h2>\r\n" + 
				"\r\n" + 
				"<pre>\r\n" + 
				"CREATE OR REPLACE PACKAGE BODY ENSEMBLE.AD_SOD \r\n" + 
				"IS\r\n" + 
				"procedure print_sdept(psno char) as\r\n" + 
				"    psdept student.sdept%type;\r\n" + 
				"begin\r\n" + 
				"	RBSTOR.get_acct_detail_aio (v_acct_no, \r\n" + 
				"                                 p_acct_stats.aio_internal_key,\r\n" + 
				"                                 v_acct_det\r\n" + 
				"                                );\r\n" + 
				"    select max(sdept),max(id),max(score) into psdept,pid,psocre\r\n" + 
				"    from student\r\n" + 
				"    where sno=psno;\r\n" + 
				"    RBSTOR.get_acct_detail_aio (v_acct_no,\r\n" + 
				"                                 p_acct_stats.aio_internal_key,\r\n" + 
				"                                 v_acct_det\r\n" + 
				"                                );                     \r\n" + 
				"end;\r\n" + 
				"end;\r\n" + 
				"</pre>",
				key = "MultipleCallFunctionCheck",
				priority = Priority.INFO
		)
@ConstantRemediation("10min")
@ActivatedByDefault
public class MultipleCallFunctionCheck extends AbstractBaseCheck {
	private static final int DEFAULT_ACCEPTED_LENGTH = 2;

	private static final String DEFAULT_MESSAGE = "sys.**,raise_application_error,cbsd_log.*,fm_util.log_error,dbms_output.*";
	private static final String INCLUDE_MESSAGE = "**";

	@RuleProperty(key = "maximumquantity",
			description = "一个过程中允许调用函数的最大次数",
			defaultValue = "" + DEFAULT_ACCEPTED_LENGTH)
	public int maximumquantity = DEFAULT_ACCEPTED_LENGTH;

	@RuleProperty(key = "ignoremethods",
			description = "调用这些函数时，不进行次数的判断,**代表所有函数,函数请输入小写字母",
			defaultValue = DEFAULT_MESSAGE)
	public String ignoremethods = DEFAULT_MESSAGE;
	
	@RuleProperty(key = "inclusionmethods",
			description = "调用这些函数时，进行次数的判断,**代表所有的函数,函数请输入小写字母",
			defaultValue = INCLUDE_MESSAGE)
	public String inclusionmethods = INCLUDE_MESSAGE;
	
	private WildcardPattern[] inclusionPatterns;
	private WildcardPattern[] exclusionPatterns;
	@Override
	public void init() {
		subscribeTo(PlSqlGrammar.STATEMENTS_SECTION);
	}

	@Override
	public void visitNode(AstNode node) {
		if (node.hasAncestor(PlSqlGrammar.STATEMENTS_SECTION)){
			return;
		}
		List<AstNode> statements = node.getDescendants(PlSqlGrammar.CALL_STATEMENT);
		if(statements.size() > maximumquantity){
			List<String> methodnames= new ArrayList<>();
			for (AstNode statement : statements) {
				try {
					String methodname = getmethodName(statement).toLowerCase();
					if (methodname != null && methodname.length() != 0 && !isMatchingExclusionPattern(methodname)  && isMatchingInclusionPattern(methodname)) {
						methodnames.add(methodname);
					}	
					int freq = Collections.frequency(methodnames, methodname);
					if (freq > maximumquantity) {
						getContext().createLineViolation(this, "There should not be multiple calls to the same function.", statement);   					
					}

				}catch(Exception e){
					continue;
				}    		

			}
		}
	}
	private boolean isMatchingExclusionPattern(String methodname) {
		return WildcardPattern.match(getExclusionPatterns(), methodname);
	}
	
 private boolean isMatchingInclusionPattern(String methodname) {
		    return WildcardPattern.match(getInclusionPatterns(), methodname);
		  }

	private String getmethodName(AstNode statement) {
		String methodname;
		if (statement.getFirstChild(PlSqlGrammar.METHOD_CALL) != null ) {
			String fathername = statement.getFirstChild(PlSqlGrammar.METHOD_CALL).getTokenValue();
			if (statement.getFirstChild(PlSqlGrammar.METHOD_CALL).getFirstChild(PlSqlGrammar.MEMBER_EXPRESSION) != null){
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append(fathername);
				List<AstNode> surnames = statement.getFirstChild(PlSqlGrammar.METHOD_CALL).getFirstChild(PlSqlGrammar.MEMBER_EXPRESSION).getChildren(PlSqlGrammar.IDENTIFIER_NAME);
				for (AstNode surname : surnames) {
					stringBuilder.append('.');
					stringBuilder.append(surname.getTokenValue());
				}
				methodname = stringBuilder.toString();
			}else {
				methodname = fathername;
			}
			return methodname;
		}
		return "";

	}

	private static WildcardPattern[] createPatterns(String patterns) {
		String[] p = StringUtils.split(patterns, ',');
		WildcardPattern[] result = new WildcardPattern[p.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = WildcardPattern.create(StringUtils.trim(p[i]), ".");
		}
		return result;
	}

	private WildcardPattern[] getExclusionPatterns() {  
		if (exclusionPatterns == null) {
			if (StringUtils.isEmpty(ignoremethods)) {
				exclusionPatterns = new WildcardPattern[0];
			}
			exclusionPatterns = createPatterns(ignoremethods);
		}
		return exclusionPatterns;
	}
	
	  private WildcardPattern[] getInclusionPatterns() {
		    if (inclusionPatterns == null) {
		      if (StringUtils.isEmpty(inclusionmethods)) {
		    	  inclusionmethods="**";
		      } else {
		    	  inclusionPatterns = createPatterns(inclusionmethods);
		      }
		    }
		    return inclusionPatterns;
		  }

}
