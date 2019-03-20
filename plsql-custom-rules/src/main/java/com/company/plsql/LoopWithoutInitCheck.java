package com.company.plsql;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plsqlopen.annnotations.ActivatedByDefault;
import org.sonar.plsqlopen.annnotations.ConstantRemediation;
import org.sonar.plsqlopen.checks.AbstractBaseCheck;
import org.sonar.plugins.plsqlopen.api.PlSqlGrammar;
import org.sonar.plugins.plsqlopen.api.PlSqlKeyword;

import com.sonar.sslr.api.AstNode;

@Rule(
		name = "Variables in loop statement should be initialized.",
		description = "<p>\r\n" + 
				"LOOP循环里面的变量，每次循环开始时，需要注意是否已经初始化，防止上一次的变量被下一次的循环误使用\r\n" + 
				"</p>\r\n" + 
				"\r\n" + 
				"<h2>Noncompliant Code Example</h2>\r\n" + 
				"\r\n" + 
				"<pre>\r\n" + 
				"BEGIN\r\n" + 
				"   FOR i IN c_sub_acct (v_internal_key) \r\n" + 
				"   LOOP -- Noncompliant\r\n" + 
				"      --循环计算单个子账户的v_actual_int,统计单账户利息，未初始化变量导致后续账户利息统计错误    \r\n" + 
				"      v_return :=\r\n" + 
				"         fdstor.get_int_rate (p_int_type          => v_cr_int_type,\r\n" + 
				"                              p_ccy               => v_ccy,\r\n" + 
				"                              p_effect_date       => v_fr,\r\n" + 
				"                              p_balance           => v_bal,\r\n" + 
				"                              p_int_rate          => v_actual_rate);\r\n" + 
				"\r\n" + 
				"      IF v_cr_acct_level_int_rate IS NOT NULL\r\n" + 
				"      THEN\r\n" + 
				"         v_actual_rate := v_cr_acct_level_int_rate;\r\n" + 
				"      END IF;\r\n" + 
				"\r\n" + 
				"      v_diff := (v_to - v_fr);\r\n" + 
				"      v_actual_int := (v_bal * v_actual_rate * v_diff) / v_day_basis / 100;\r\n" + 
				"   END LOOP;\r\n" + 
				"END;\r\n" +
				"</pre>\r\n" + 
				"\r\n" + 
				"<h2>Compliant Solution</h2>\r\n" + 
				"\r\n" + 
				"<pre>\r\n" + 
				"BEGIN\r\n" + 
				"   LOOP\r\n" + 
				"      v_actual_int := 0;    --每次初始化，循环计算单个子账户的利息      \r\n" + 
				"      v_return :=\r\n" + 
				"         fdstor.get_int_rate (p_int_type          => v_cr_int_type,\r\n" + 
				"                              p_ccy               => v_ccy,\r\n" + 
				"                              p_effect_date       => v_fr,\r\n" + 
				"                              p_balance           => v_bal,\r\n" + 
				"                              p_int_rate          => v_actual_rate);\r\n" + 
				"\r\n" + 
				"      IF v_cr_acct_level_int_rate IS NOT NULL\r\n" + 
				"      THEN\r\n" + 
				"         v_actual_rate := v_cr_acct_level_int_rate;\r\n" + 
				"      END IF;\r\n" + 
				"\r\n" + 
				"      v_diff := (v_to - v_fr);\r\n" + 
				"      v_actual_int := (v_bal * v_actual_rate * v_diff) / v_day_basis / 100;\r\n" + 
				"   END LOOP;\r\n" + 
				"END;\r\n"+ 
				"</pre>",
				key = "LoopWithoutInitCheck",
				priority = Priority.INFO
		)
@ConstantRemediation("10min")
@ActivatedByDefault
public class LoopWithoutInitCheck extends AbstractBaseCheck {

	@Override
	public void init() {
		subscribeTo(PlSqlGrammar.FOR_STATEMENT,PlSqlGrammar.WHILE_STATEMENT,PlSqlGrammar.LOOP_STATEMENT);
	}

	@Override
	public void visitNode(AstNode node) {
		AstNode loop = node.getFirstChild(PlSqlKeyword.LOOP);
		AstNode assigment = loop.getNextAstNode().getFirstChild().getFirstChild();
		if (assigment == null) {
			getContext().createLineViolation(this, "Variables in loop statement should be initialized.", loop);
		}
		if (assigment != null && (assigment.isNot(PlSqlGrammar.ASSIGNMENT_STATEMENT) || assigment.getFirstChild(PlSqlGrammar.LITERAL) == null)){		
			getContext().createLineViolation(this, "Variables in loop statement should be initialized.", loop);
		}
	}
}
