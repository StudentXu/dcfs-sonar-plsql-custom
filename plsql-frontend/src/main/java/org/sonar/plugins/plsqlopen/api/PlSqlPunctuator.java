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

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.TokenType;

public enum PlSqlPunctuator implements TokenType {
    // Based on http://docs.oracle.com/cd/B19306_01/appdev.102/b14261/fundamentals.htm#sthref297
    COMMA(","),
    PLUS("+"),
    MINUS("-"),
    MOD("%"),
    DOT("."),
    DIVISION("/"),
    LPARENTHESIS("("),
    RPARENTHESIS(")"),
    COLON(":"),
    SEMICOLON(";"),
    MULTIPLICATION("*"),
    EQUALS("="),
    LESSTHAN("<"),
    GREATERTHAN(">"),
    REMOTE("@"),
	PARAM("&"),
	DOUBLEPARAM("&&"),
    SUBTRACTION("-"),
    ASSIGNMENT(":="),
    ASSOCIATION("=>"),
    CONCATENATION("||"),
    EXPONENTIATION("**"),
    LLABEL("<<"),
    RLABEL(">>"),
    RANGE(".."),
    NOTEQUALS("<>"),
    NOTEQUALS2("!="),
    NOTEQUALS3("~="),
    NOTEQUALS4("^="),
    LESSTHANOREQUAL("<="),
    GREATERTHANOREQUAL(">="),
    DOUBLEDOLLAR("$$");

    private final String value;

    private PlSqlPunctuator(String word) {
        this.value = word;
    }

    @Override
    public String getName() {
        return name();
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public boolean hasToBeSkippedFromAst(AstNode node) {
        return false;
    }
}
