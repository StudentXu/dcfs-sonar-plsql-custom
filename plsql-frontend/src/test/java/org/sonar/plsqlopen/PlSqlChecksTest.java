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
package org.sonar.plsqlopen;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.batch.rule.ActiveRules;
import org.sonar.api.batch.rule.CheckFactory;
import org.sonar.api.batch.rule.Checks;
import org.sonar.api.batch.rule.internal.ActiveRulesBuilder;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Rule;
import org.sonar.plsqlopen.checks.PlSqlCheck;

import com.google.common.collect.ImmutableList;

public class PlSqlChecksTest {
    
    private static final String DEFAULT_REPOSITORY_KEY = "DefaultRuleRepository";
    private static final String DEFAULT_RULE_KEY = "MyRule";
    private static final String CUSTOM_REPOSITORY_KEY = "CustomRuleRepository";
    private static final String CUSTOM_RULE_KEY = "MyCustomRule";
    
    private MyCustomPlSqlRulesDefinition customRulesDefinition;
    private CheckFactory checkFactory;
    
    @Before
    public void setUp() {
        ActiveRules activeRules = (new ActiveRulesBuilder())
                .create(RuleKey.of(DEFAULT_REPOSITORY_KEY, DEFAULT_RULE_KEY)).activate()
                .create(RuleKey.of(CUSTOM_REPOSITORY_KEY, CUSTOM_RULE_KEY)).activate()
                .build();
        checkFactory = new CheckFactory(activeRules);
        
        customRulesDefinition = new MyCustomPlSqlRulesDefinition();
        RulesDefinition.Context context = new RulesDefinition.Context();
        customRulesDefinition.define(context);
    }
    
    @SuppressWarnings("rawtypes")
    @Test
    public void shouldReturnDefaultChecks() {
        PlSqlChecks checks = PlSqlChecks.createPlSqlCheck(checkFactory);
        checks.addChecks(DEFAULT_REPOSITORY_KEY, ImmutableList.<Class>of(MyRule.class));
        
        PlSqlCheck defaultCheck = check(checks, DEFAULT_REPOSITORY_KEY, DEFAULT_RULE_KEY);
        
        assertThat(checks.all()).hasSize(1);
        assertThat(checks.ruleKey(defaultCheck)).isNotNull();
        assertThat(checks.ruleKey(defaultCheck).rule()).isEqualTo(DEFAULT_RULE_KEY);
        assertThat(checks.ruleKey(defaultCheck).repository()).isEqualTo(DEFAULT_REPOSITORY_KEY);
    }
    
    @Test
    public void shouldReturnCustomChecks() {
        PlSqlChecks checks = PlSqlChecks.createPlSqlCheck(checkFactory);
        checks.addCustomChecks(new CustomPlSqlRulesDefinition[] { customRulesDefinition });
        
        PlSqlCheck customCheck = check(checks, CUSTOM_REPOSITORY_KEY, CUSTOM_RULE_KEY);
        
        assertThat(checks.all()).hasSize(1);
        assertThat(checks.ruleKey(customCheck)).isNotNull();
        assertThat(checks.ruleKey(customCheck).rule()).isEqualTo(CUSTOM_RULE_KEY);
        assertThat(checks.ruleKey(customCheck).repository()).isEqualTo(CUSTOM_REPOSITORY_KEY);
    }
    
    @Test
    public void shouldWorkWithoutCustomChecks() {
        PlSqlChecks checks = PlSqlChecks.createPlSqlCheck(checkFactory);
        checks.addCustomChecks(null);
        assertThat(checks.all()).hasSize(0);
    }
    
    @SuppressWarnings("rawtypes")
    @Test
    public void shouldNotReturnRuleKeyIfCheckDoesNotExists() {
        PlSqlChecks checks = PlSqlChecks.createPlSqlCheck(checkFactory);
        checks.addChecks(DEFAULT_REPOSITORY_KEY, ImmutableList.<Class>of(MyRule.class));
        
        assertThat(checks.ruleKey(new MyCustomRule())).isNull();
    }
    
    public PlSqlCheck check(PlSqlChecks plSqlChecks, String repository, String rule) {
        RuleKey key = RuleKey.of(repository, rule);
        
        PlSqlCheck check;

        for (Checks<PlSqlCheck> checks : plSqlChecks.getChecks()) {
            check = (PlSqlCheck)checks.of(key);

            if (check != null) {
                return check;
            }
        }
        return null;
    }

    @Rule(key = DEFAULT_RULE_KEY, name = "This is the default rule", description = "desc")
    public static class MyRule extends PlSqlCheck {
    }
    
    @Rule(key = CUSTOM_RULE_KEY, name = "This is a custom rule", description = "desc")
    public static class MyCustomRule extends PlSqlCheck {
    }

    public static class MyCustomPlSqlRulesDefinition extends CustomPlSqlRulesDefinition {

        @Override
        public String repositoryName() {
            return "Custom Rule Repository";
        }

        @Override
        public String repositoryKey() {
            return CUSTOM_REPOSITORY_KEY;
        }

        @SuppressWarnings("rawtypes")
        @Override
        public Class[] checkClasses() {
            return new Class[] { MyCustomRule.class };
        }
    }
}
