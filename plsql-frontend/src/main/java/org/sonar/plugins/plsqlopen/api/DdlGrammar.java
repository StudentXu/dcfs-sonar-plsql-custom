/*
 * Sonar PL/SQL Plugin (Community)
 * Copyright (C) 2015-2017 Felipe Zorzo
 * mailto:felipebzorzo AT gmail DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.plugins.plsqlopen.api;

import static com.sonar.sslr.api.GenericTokenType.EOF;
import static org.sonar.plugins.plsqlopen.api.PlSqlGrammar.*;
import static org.sonar.plugins.plsqlopen.api.DmlGrammar.SELECT_EXPRESSION;
import static org.sonar.plugins.plsqlopen.api.PlSqlKeyword.*;
import static org.sonar.plugins.plsqlopen.api.PlSqlPunctuator.*;
import static org.sonar.plugins.plsqlopen.api.PlSqlTokenType.*;

import org.sonar.sslr.grammar.GrammarRuleKey;
import org.sonar.sslr.grammar.LexerfulGrammarBuilder;

public enum DdlGrammar implements GrammarRuleKey {
    DDL_COMMENT,
    DDL_COMMAND,
    TABLE_COLUMN_DEFINITION,
    TABLE_RELATIONAL_PROPERTIES,
    CREATE_TABLE,
    ALTER_PLSQL_UNIT,
    ALTER_PROCEDURE_FUNCTION,
    COMPILE_CLAUSE,
    ALTER_TRIGGER,
    ALTER_PACKAGE,
	ALTER_TABLE,
    DROP_COMMAND,
    CREATE_SYNONYM,
	ALTER_SESSION_NLS_LENGTH_SEMANTICS,
	TABLE_OPTION,
	STORAGE_OPTION,
	SEQUENCE_OPTION,
	CREATE_INDEX,
	INDEX_OPTION,
	CREATE_TABLESPACE,
	CREATE_TABLESPACE_OPTION,
	CREATE_USER,
	ALTER_USER,
	ALTER_SEQUENCE,
	USER_OPTION,
	TRUNCATE_TABLE,
	ALTER_TYPE,
	ALTER_INDEX,
	CREATE_DIRECTORY,
	CREATE_CONTEXT,
	CREATE_DBLINK,
	ONE_OR_MORE_IDENTIFIERS,
	REFERENCES_CLAUSE,
	INLINE_CONSTRAINT,
	OUT_OF_LINE_CONSTRAINT,
	CREATE_SEQUENCE;

    
    public static void buildOn(LexerfulGrammarBuilder b) {
        createDdlCommands(b);
    }
    
    private static void createDdlCommands(LexerfulGrammarBuilder b) {
        b.rule(DDL_COMMENT).is(
                COMMENT, ON,
                b.firstOf(
                        b.sequence(
                                COLUMN,
                                IDENTIFIER_NAME, 
                                b.optional(DOT, IDENTIFIER_NAME), 
                                b.optional(DOT, IDENTIFIER_NAME)),
                        b.sequence(
                                b.firstOf(
                                        TABLE,
                                        COLUMN,
                                        OPERATOR,
                                        INDEXTYPE,
                                        b.sequence(MATERIALIZED, VIEW),
                                        b.sequence(MINING, MODEL)),
                                IDENTIFIER_NAME, b.optional(DOT, IDENTIFIER_NAME))
                        ),
                IS, CHARACTER_LITERAL, b.optional(SEMICOLON));

        b.rule(ONE_OR_MORE_IDENTIFIERS).is(LPARENTHESIS, IDENTIFIER_NAME, b.zeroOrMore(COMMA, IDENTIFIER_NAME), RPARENTHESIS).skip();
        
        b.rule(REFERENCES_CLAUSE).is(
                REFERENCES, MEMBER_EXPRESSION,
                b.optional(ONE_OR_MORE_IDENTIFIERS),
                b.optional(ON, DELETE, b.firstOf(CASCADE, b.sequence(SET, NULL)))
                );
        
        b.rule(INLINE_CONSTRAINT).is(
                b.optional(CONSTRAINT, IDENTIFIER_NAME),
                b.firstOf(
                        b.sequence(b.optional(NOT), NULL),
                        UNIQUE,
                        b.sequence(PRIMARY, KEY),
                        REFERENCES_CLAUSE,
                        b.sequence(CHECK, EXPRESSION)));
        
        b.rule(TABLE_COLUMN_DEFINITION).is(
        		IDENTIFIER_NAME, b.optional(DATATYPE),
                b.optional(SORT),
                b.optional(DEFAULT, EXPRESSION),
                b.optional(ENCRYPT),
                b.zeroOrMore(INLINE_CONSTRAINT));
        
        b.rule(OUT_OF_LINE_CONSTRAINT).is(
                b.optional(CONSTRAINT, IDENTIFIER_NAME),
                b.firstOf(
                        b.sequence(UNIQUE, ONE_OR_MORE_IDENTIFIERS),
                        b.sequence(PRIMARY, KEY, ONE_OR_MORE_IDENTIFIERS),
                        b.sequence(FOREIGN, KEY, ONE_OR_MORE_IDENTIFIERS, REFERENCES_CLAUSE),
                        b.sequence(CHECK, EXPRESSION)),b.zeroOrMore(TABLE_OPTION));
        /**
		 b.rule(ALTER_TABLE_OPTION).is(
			b.firstOf(
					b.sequence(FOREIGN,KEY,LPARENTHESIS,b.oneOrMore(IDENTIFIER_NAME, b.optional(COMMA)),RPARENTHESIS),
					b.sequence(b.optional(CONSTRAINT),b.optional(IDENTIFIER_NAME),CHECK,LPARENTHESIS,IN_EXPRESSION,RPARENTHESIS,b.optional(COMMA)),
					b.sequence(b.optional(CONSTRAINT),IDENTIFIER_NAME,FOREIGN,KEY,LPARENTHESIS,b.oneOrMore(IDENTIFIER_NAME, b.optional(COMMA)),RPARENTHESIS),
					b.sequence(b.optional(CONSTRAINT),IDENTIFIER_NAME,PRIMARY,KEY,LPARENTHESIS,b.oneOrMore(IDENTIFIER_NAME, b.optional(COMMA)),RPARENTHESIS,b.optional(DISABLE)),
					b.sequence(b.optional(CONSTRAINT),IDENTIFIER_NAME,UNIQUE,LPARENTHESIS,b.oneOrMore(IDENTIFIER_NAME, b.optional(COMMA)),RPARENTHESIS),
					b.sequence(REFERENCES ,MEMBER_EXPRESSION,LPARENTHESIS,b.oneOrMore(IDENTIFIER_NAME, b.optional(COMMA)),RPARENTHESIS,
								b.optional(b.sequence(ON,DELETE,SET,NULL)),b.optional(DISABLE)),
					b.sequence(LPARENTHESIS,b.oneOrMore(IDENTIFIER_NAME, b.optional(COMMA)),RPARENTHESIS),
					b.sequence(IDENTIFIER_NAME, TO,IDENTIFIER_NAME )
				)

			);
**/
        
        b.rule(TABLE_RELATIONAL_PROPERTIES).is(b.oneOrMore(b.firstOf(OUT_OF_LINE_CONSTRAINT, TABLE_COLUMN_DEFINITION), b.optional(COMMA)));
        
        b.rule(CREATE_TABLE).is(
                CREATE, b.optional(GLOBAL, TEMPORARY), TABLE, UNIT_NAME,b.firstOf(
					b.sequence(AS,SELECT_EXPRESSION),
					b.sequence(LPARENTHESIS,TABLE_RELATIONAL_PROPERTIES,b.zeroOrMore(TABLE_OPTION),RPARENTHESIS,b.optional(ON, COMMIT, b.firstOf(DELETE, PRESERVE), ROWS))),
				b.zeroOrMore(TABLE_OPTION),
				b.optional(SEMICOLON)
			);
        
		b.rule(ALTER_TABLE).is(
                ALTER,TABLE,UNIT_NAME,b.optional(b.firstOf(
					b.sequence(ADD,b.optional(LPARENTHESIS)),
					b.sequence(DROP,PRIMARY,KEY,b.optional(CASCADE)),
					b.sequence(MODIFY,b.optional(LPARENTHESIS)),
					b.sequence(MODIFY,DEFAULT,ATTRIBUTES,FOR,PARTITION,PMAXVALUE),
					b.sequence(RENAME,b.optional(LPARENTHESIS),CONSTRAINT),
					b.sequence(DISABLE,ALL,TRIGGERS),
					b.sequence(DISABLE,CONSTRAINT,IDENTIFIER_NAME),
					b.sequence(ENABLE,CONSTRAINT,IDENTIFIER_NAME),
					b.sequence(ENABLE,ALL,TRIGGERS),
					b.sequence(MOVE,b.optional(SUBPARTITION,SMAXVALUE),TABLESPACE,IDENTIFIER_NAME),
					b.sequence(MOVE,NOLOGGING,PARALLEL,LPARENTHESIS,DEGREE,DEFAULT,RPARENTHESIS)
					)),
				b.zeroOrMore(b.firstOf(TABLE_OPTION,TABLE_RELATIONAL_PROPERTIES)),
				b.optional(RPARENTHESIS),
				b.optional(SEMICOLON)
                );
        
        b.rule(COMPILE_CLAUSE).is(COMPILE, b.optional(DEBUG), b.optional(REUSE, SETTINGS));
		b.rule(TABLE_OPTION).is(
			b.firstOf(
				b.sequence(TABLESPACE,b.optional(SUBSTITUTION_VARIABLE,b.optional(DOT)),IDENTIFIER_NAME),
				b.sequence(PCTUSED, INTEGER_LITERAL),
				b.sequence(PCTFREE, INTEGER_LITERAL),
				b.firstOf(MONITORING, NOMONITORING),
				NOPARALLEL,
				b.firstOf(LOGGING, NOLOGGING),
				b.firstOf(CACHE, NOCACHE),
				b.sequence(INITRANS,INTEGER_LITERAL),
				b.sequence(MAXTRANS,INTEGER_LITERAL),
				b.sequence(STORAGE,LPARENTHESIS,b.oneOrMore(STORAGE_OPTION),RPARENTHESIS),
				b.sequence(COMPUTE,STATISTICS),
				b.sequence(ON,DELETE,SET,NULL),
				b.sequence(ENABLE,b.optional(VALIDATE)),
				b.sequence(RESULT_CACHE,LPARENTHESIS,MODE,DEFAULT,RPARENTHESIS),
				b.firstOf(NOCOMPRESS, b.sequence(COMPRESS, INTEGER_LITERAL)),
				b.sequence(USING,INDEX,IDENTIFIER_NAME, DOT, IDENTIFIER_NAME),				
				b.sequence(USING,INDEX,b.optional(b.anyTokenButNot(TABLE_OPTION)))				
			)

			);
		b.rule(STORAGE_OPTION).is(
			b.firstOf(
				b.sequence(INITIAL,INTEGER_LITERAL,b.firstOf(M,K)),
				b.sequence(NEXT,INTEGER_LITERAL,b.firstOf(M,K)),
				b.sequence(MAXSIZE,b.firstOf(b.sequence(INTEGER_LITERAL,b.firstOf(M,K)),UNLIMITED)),
				b.sequence(MINEXTENTS,INTEGER_LITERAL),
				b.sequence(MAXEXTENTS,b.firstOf(INTEGER_LITERAL,UNLIMITED)),
				b.sequence(PCTINCREASE,INTEGER_LITERAL),
				b.sequence(FREELISTS,INTEGER_LITERAL),
				b.sequence(FREELIST,GROUPS,INTEGER_LITERAL),
				b.sequence(BUFFER_POOL,DEFAULT),
				b.sequence(FLASH_CACHE,DEFAULT),
				b.sequence(CELL_FLASH_CACHE,DEFAULT)
			)
			);
        
        b.rule(ALTER_TRIGGER).is(
                TRIGGER, UNIT_NAME,
                b.firstOf(ENABLE, 
                          DISABLE, 
                          b.sequence(RENAME, TO, IDENTIFIER_NAME), 
                          COMPILE_CLAUSE)
                );
        
        b.rule(ALTER_PROCEDURE_FUNCTION).is(
                b.firstOf(PROCEDURE, FUNCTION)
                , UNIT_NAME, COMPILE_CLAUSE
                );
        
        b.rule(ALTER_PACKAGE).is(
                PACKAGE, UNIT_NAME, COMPILE, 
                b.optional(DEBUG), 
                b.optional(b.firstOf(PACKAGE, SPECIFICATION, BODY)),
                b.optional(REUSE, SETTINGS)
                );
		b.rule(ALTER_SESSION_NLS_LENGTH_SEMANTICS).is(
                SESSION,SET, 
                b.optional(NLS_LENGTH_SEMANTICS,EQUALS,b.firstOf(CHAR,BYTE))
                );

		b.rule(ALTER_TYPE).is(
                TYPE,UNIT_NAME,COMPILE,BODY
                );
		
		b.rule(ALTER_INDEX).is(
                INDEX,UNIT_NAME,b.optional(b.firstOf(b.sequence(REBUILD,b.optional(SUBPARTITION,SMAXVALUE)),
							b.sequence(MODIFY,DEFAULT,ATTRIBUTES,FOR,PARTITION,PMAXVALUE))),
					b.zeroOrMore(INDEX_OPTION)
                );


        

        b.rule(ALTER_PLSQL_UNIT).is(ALTER, b.firstOf(ALTER_TRIGGER, ALTER_PROCEDURE_FUNCTION,ALTER_INDEX, ALTER_PACKAGE,ALTER_SESSION_NLS_LENGTH_SEMANTICS,ALTER_USER,ALTER_SEQUENCE,ALTER_TYPE), b.optional(SEMICOLON));
        
        b.rule(DROP_COMMAND).is(DROP, b.oneOrMore(b.anyTokenButNot(b.firstOf(SEMICOLON, DIVISION, EOF))),b.optional(CASCADE),b.optional(CONSTRAINTS,PURGE), b.optional(SEMICOLON));
        
        b.rule(CREATE_SYNONYM).is(
                CREATE, b.optional(OR, REPLACE), b.optional(b.firstOf(EDITIONABLE, NONEDITIONABLE)),
                b.optional(PUBLIC), SYNONYM, UNIT_NAME,
                b.optional(SHARING, EQUALS, b.firstOf(METADATA, NONE)),
                FOR, DmlGrammar.TABLE_REFERENCE, b.optional(SEMICOLON));
        
 
		
		b.rule(CREATE_SEQUENCE).is(
                CREATE, b.optional(OR, REPLACE),
                SEQUENCE, UNIT_NAME,
				b.zeroOrMore(SEQUENCE_OPTION),
				b.optional(SEMICOLON)
			);

		b.rule(ALTER_SEQUENCE).is(
                SEQUENCE, UNIT_NAME,
				b.zeroOrMore(SEQUENCE_OPTION),
				b.optional(SEMICOLON)
			);

		b.rule(SEQUENCE_OPTION).is(
			b.firstOf(
				b.sequence(INCREMENT,BY,INTEGER_LITERAL),
				b.sequence(START,WITH,INTEGER_LITERAL),
				b.firstOf(NOMAXVALUE, b.sequence(MAXVALUE, INTEGER_LITERAL)),
				b.firstOf(NOMINVALUE, b.sequence(MINVALUE, INTEGER_LITERAL)),
				b.firstOf(CYCLE,NOCYCLE),
				b.firstOf(NOCACHE, b.sequence(CACHE,INTEGER_LITERAL)),
				b.firstOf(ORDER,NOORDER)
				)
			);

		b.rule(CREATE_INDEX).is(
                CREATE, b.optional(b.firstOf(UNIQUE, BITMAP)),
                INDEX, UNIT_NAME,ON,UNIT_NAME,
				b.firstOf(
						b.sequence(LPARENTHESIS,b.oneOrMore(IDENTIFIER_NAME, b.optional(COMMA)),RPARENTHESIS),
						b.sequence(LPARENTHESIS,METHOD_CALL,RPARENTHESIS)),
				b.zeroOrMore(INDEX_OPTION),
				b.optional(SEMICOLON)
			);

		b.rule(INDEX_OPTION).is(
			b.firstOf(
				b.sequence(TABLESPACE,b.optional(SUBSTITUTION_VARIABLE,b.optional(DOT)),IDENTIFIER_NAME),
				b.sequence(PCTUSED, INTEGER_LITERAL),
				b.sequence(PCTFREE, INTEGER_LITERAL),
				b.firstOf(LOGGING, NOLOGGING),
				b.firstOf(SORT, NOSORT),
				b.sequence(INITRANS,INTEGER_LITERAL),	
				b.sequence(MAXTRANS,INTEGER_LITERAL),
				b.sequence(COMPUTE,STATISTICS),
				b.firstOf(NOCOMPRESS, b.sequence(COMPRESS, INTEGER_LITERAL)),
				b.sequence(STORAGE,LPARENTHESIS,b.oneOrMore(STORAGE_OPTION),RPARENTHESIS),
				b.sequence(LOCAL,STORE,IN,LPARENTHESIS,DEFAULT,RPARENTHESIS),
				NOPARALLEL,
				ONLINE,
				REVERSE
			)
			);

		b.rule(CREATE_TABLESPACE).is(
                CREATE, b.optional(b.firstOf(TEMPORARY,UNDO)),
                TABLESPACE, UNIT_NAME,b.firstOf(DATAFILE,TEMPFILE),STRING_LITERAL,SIZE,INTEGER_LITERAL,M,b.optional(REUSE),
				b.zeroOrMore(CREATE_TABLESPACE_OPTION),
				b.optional(SEMICOLON)
			);
		
		

		b.rule(CREATE_TABLESPACE_OPTION).is(
			b.firstOf(
				b.firstOf(b.sequence(AUTOEXTEND,ON,b.optional(NEXT,INTEGER_LITERAL,M),b.optional(MAXSIZE,b.firstOf(b.sequence(INTEGER_LITERAL,M),UNLIMITED))),b.sequence(AUTOEXTEND,OFF)),
				b.sequence(EXTENT,MANAGEMENT,LOCAL,AUTOALLOCATE),
				b.sequence(EXTENT,MANAGEMENT,LOCAL,UNIFORM,SIZE,INTEGER_LITERAL,b.firstOf(M,K)),
				b.sequence(BLOCKSIZE,INTEGER_LITERAL,b.firstOf(M,K)),
				b.sequence(TABLESPACE,GROUP,STRING_LITERAL),
				b.firstOf(LOGGING, NOLOGGING),
				b.sequence(SEGMENT,SPACE,MANAGEMENT,AUTO),
				b.sequence(FLASHBACK,ON),	
				b.firstOf(ONLINE,OFFLINE),
				b.firstOf(PERMANENT,TEMPORARY)
			)
			);

		

		b.rule(CREATE_USER).is(
                CREATE, USER, b.firstOf(UNIT_NAME,SUBSTITUTION_VARIABLE),IDENTIFIED,BY,b.optional(VALUES),b.firstOf(STRING_LITERAL,INTEGER_LITERAL,SUBSTITUTION_VARIABLE,IDENTIFIER_NAME),
				b.zeroOrMore(USER_OPTION),
				b.optional(SEMICOLON)
			);
		
		b.rule(CREATE_DBLINK).is(
                CREATE,b.optional(PUBLIC),DATABASE,LINK, b.firstOf(UNIT_NAME,b.sequence(PARAM,INTEGER_LITERAL)),CONNECT,TO, IDENTIFIER_NAME,
			   IDENTIFIED,BY,b.firstOf(STRING_LITERAL,INTEGER_LITERAL,b.sequence(PARAM,b.optional(PARAM),INTEGER_LITERAL),IDENTIFIER_NAME),
				USING,b.firstOf(STRING_LITERAL,IDENTIFIER_NAME),b.optional(SEMICOLON)
			);
		
		b.rule(ALTER_USER).is(
                 USER, b.firstOf(UNIT_NAME,b.sequence(PARAM,INTEGER_LITERAL)),
				b.optional(b.firstOf(
					b.sequence(IDENTIFIED,BY,b.optional(VALUES),b.firstOf(STRING_LITERAL,INTEGER_LITERAL,SUBSTITUTION_VARIABLE,IDENTIFIER_NAME)),
					b.sequence(IDENTIFIED,EXTERNALLY),
					b.sequence(IDENTIFIED,GLOBALLY,AS,IDENTIFIER_NAME))),
				b.zeroOrMore(USER_OPTION),
				b.optional(SEMICOLON)
			);

		b.rule(USER_OPTION).is(
			b.firstOf(
				b.sequence(DEFAULT,TABLESPACE,b.optional(SUBSTITUTION_VARIABLE,b.optional(DOT)),IDENTIFIER_NAME),
				b.sequence(TEMPORARY,TABLESPACE,b.optional(SUBSTITUTION_VARIABLE,b.optional(DOT)),IDENTIFIER_NAME),
				b.sequence(QUOTA,INTEGER_LITERAL,b.firstOf(M,K),ON,IDENTIFIER_NAME),
				b.sequence(QUOTA,UNLIMITED,ON,IDENTIFIER_NAME),
				b.sequence(PASSWORD,EXPIRE),
				b.sequence(DEFAULT,ROLE,ALL),
				b.sequence(DEFAULT,ROLE,NONE),
				b.sequence(ACCOUNT,b.firstOf(LOCK,UNLOCK)),
				b.sequence(PROFILE,b.firstOf(IDENTIFIER_NAME,DEFAULT))
			)
			);
		
		b.rule(TRUNCATE_TABLE).is(
			TRUNCATE,TABLE,UNIT_NAME,b.optional(SEMICOLON)

			);	
		  
		b.rule(CREATE_DIRECTORY).is(
			CREATE,b.optional(OR,REPLACE),DIRECTORY,UNIT_NAME,AS,STRING_LITERAL,
				b.optional(SEMICOLON)
			);

		b.rule(CREATE_CONTEXT).is(
			CREATE,b.optional(OR,REPLACE),CONTEXT,UNIT_NAME,USING,IDENTIFIER_NAME,
				b.optional(SEMICOLON)
			);
        b.rule(DDL_COMMAND).is(b.firstOf(DDL_COMMENT, CREATE_DIRECTORY,CREATE_TABLE,CREATE_CONTEXT, CREATE_DBLINK,ALTER_TABLE,ALTER_PLSQL_UNIT, DROP_COMMAND, CREATE_SYNONYM,CREATE_SEQUENCE,CREATE_INDEX,CREATE_TABLESPACE,CREATE_USER,TRUNCATE_TABLE));
    }

}
