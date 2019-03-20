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
package org.sonar.plsqlopen.checks;

public class Tags {
  public static final String BRAIN_OVERLOAD = "brain-overload";
  public static final String BUG = "bug";
  public static final String CLUMSY = "clumsy";
  public static final String CONVENTION = "convention";
  public static final String OBSOLETE = "obsolete";
  public static final String SECURITY = "security";
  public static final String UNUSED = "unused";
  public static final String CERT = "cert";
  public static final String PITFALL = "pitfall";
  public static final String MISRA = "misra";
  public static final String CONFUSING = "confusing";
  public static final String CWE = "cwe";
  public static final String PERFORMANCE = "performance";

  private Tags() {
    // This class only defines constants
  }
}
