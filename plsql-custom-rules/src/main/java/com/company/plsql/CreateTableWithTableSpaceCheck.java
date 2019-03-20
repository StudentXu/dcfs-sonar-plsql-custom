package com.company.plsql;
import java.util.List;

/** 
 * @author : liujiey
 * @date 创建时间：2018年9月18日 下午1:32:27 
 */
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plsqlopen.annnotations.ActivatedByDefault;
import org.sonar.plsqlopen.annnotations.ConstantRemediation;
import org.sonar.plsqlopen.checks.AbstractBaseCheck;
import org.sonar.plsqlopen.checks.Tags;
import org.sonar.plugins.plsqlopen.api.DdlGrammar;
import com.sonar.sslr.api.AstNode;

@Rule(
		name = "Avoid creating Table/Index statement without tablespace option",
		description = "<p>\r\n" + 
				"建表、索引必须指定表空间，直接使用默认空间是不合理的用法，不利于代码的规范管理\r\n" + 
				"</p>\r\n" + 
				"\r\n" + 
				"<h2>Noncompliant Code Example</h2>\r\n" + 
				"\r\n" + 
				"<pre>\r\n" + 
				"CREATE TABLE FM_COMM_PARA\r\n" +
				"(\r\n" +
				"UNIT_ID      VARCHAR2 (10 CHAR) NOT NULL,\r\n" +
				"UNIT_DESC    VARCHAR2 (100 CHAR),\r\n" +
				"PARA_VALUE   VARCHAR2 (500 CHAR) NOT NULL,\r\n" +
				"PARA_DESC    VARCHAR2 (100 CHAR)\r\n" +
				");\r\n" +
				"</pre>\r\n" + 
				"\r\n" + 
				"<h2>Compliant Solution</h2>\r\n" + 
				"\r\n" + 
				"<pre>\r\n" + 
				"CREATE TABLE FM_COMM_PARA\r\n" +
				"(\r\n" +
				"UNIT_ID      VARCHAR2 (10 CHAR) NOT NULL,\r\n" +
				"UNIT_DESC    VARCHAR2 (100 CHAR),\r\n" +
				"PARA_VALUE   VARCHAR2 (500 CHAR) NOT NULL,\r\n" +
				"PARA_DESC    VARCHAR2 (100 CHAR)\r\n" +
				")\r\n" +
				"TABLESPACE FM_DATA;\r\n" + 
				"</pre>",
				key = "CreateTableWithTableSpaceCheck",
				priority = Priority.MAJOR,
				tags=Tags.CONVENTION
		)
@ConstantRemediation("10min")
@ActivatedByDefault
public class CreateTableWithTableSpaceCheck extends AbstractBaseCheck {

	@Override
	public void init() {
		subscribeTo(DdlGrammar.CREATE_TABLE);
		subscribeTo(DdlGrammar.CREATE_INDEX);
	}

	@Override
	public void visitNode(AstNode node) {

		if (node.is(DdlGrammar.CREATE_TABLE)){
			List<AstNode> tableoptions = node.getChildren(DdlGrammar.TABLE_OPTION);
			if (tableoptions == null ||tableoptions.isEmpty()) {
				getContext().createLineViolation(this, "There is no tablespace option in the CREATE TABLE statement.", node);
			}else {
				boolean tableflag = false; 
				for (AstNode tableoption : tableoptions) {
					if ("TABLESPACE".equalsIgnoreCase(tableoption.getTokenValue())){
						tableflag = true; 
						break;
					}					 
				}
				if (!tableflag){					 
					getContext().createLineViolation(this, "There is no tablespace option in the CREATE TABLE statement.", node);
				}
			}
		}else{
			List<AstNode> indexoptions = node.getChildren(DdlGrammar.INDEX_OPTION);
			if (indexoptions == null ||indexoptions.isEmpty()) {
				getContext().createLineViolation(this, "There is no tablespace option in the CREATE INDEX statement.", node);
			}else {
				boolean indexflag = false; 
				for (AstNode indexoption : indexoptions) {
					if ("TABLESPACE".equalsIgnoreCase(indexoption.getTokenValue())){
						indexflag = true; 
						break;
					}					 
				}
				if (!indexflag){					 
					getContext().createLineViolation(this, "There is no tablespace option in the CREATE INDEX statement.", node);
				}
			}
		}

	}
}

