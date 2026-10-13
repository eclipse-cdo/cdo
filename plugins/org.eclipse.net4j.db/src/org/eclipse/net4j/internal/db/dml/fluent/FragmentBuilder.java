/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.db.dml.fluent;

import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.db.dml.SQLColumnRef;
import org.eclipse.net4j.db.dml.SQLCondition;
import org.eclipse.net4j.db.dml.SQLExpression;
import org.eclipse.net4j.db.dml.SQLFragment;
import org.eclipse.net4j.db.dml.SQLParameter;
import org.eclipse.net4j.db.dml.SQLTableRef;
import org.eclipse.net4j.db.dml.fluent.SQLFragmentStep;
import org.eclipse.net4j.internal.db.dml.Fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Collects trusted SQL text and typed fragment operands into an immutable fragment.
 *
 * @author Eike Stepper
 * @since 4.15
 */
public final class FragmentBuilder implements SQLFragmentStep
{
  private final List<Object> parts = new ArrayList<>();

  public FragmentBuilder()
  {
  }

  @Override
  public SQLFragmentStep sql(String trustedSQL)
  {
    String text = Objects.requireNonNull(trustedSQL, "trustedSQL");
    if (text.indexOf('?') >= 0)
    {
      throw new IllegalArgumentException(
          "Raw SQL text must not contain '?'; represent parameter placeholders with SQL.param() and structured fragment parameter tokens");
    }

    parts.add(text);
    return this;
  }

  @Override
  public SQLFragmentStep table(IDBTable table)
  {
    parts.add(Objects.requireNonNull(table, "table"));
    return this;
  }

  @Override
  public SQLFragmentStep table(SQLTableRef table)
  {
    parts.add(Objects.requireNonNull(table, "table"));
    return this;
  }

  @Override
  public SQLFragmentStep column(IDBField column)
  {
    parts.add(Objects.requireNonNull(column, "column"));
    return this;
  }

  @Override
  public SQLFragmentStep column(SQLColumnRef column)
  {
    parts.add(Objects.requireNonNull(column, "column"));
    return this;
  }

  @Override
  public SQLFragmentStep param(SQLParameter parameter)
  {
    parts.add(Objects.requireNonNull(parameter, "parameter"));
    return this;
  }

  @Override
  public SQLFragmentStep expression(SQLExpression expression)
  {
    parts.add(Objects.requireNonNull(expression, "expression"));
    return this;
  }

  @Override
  public SQLFragmentStep condition(SQLCondition condition)
  {
    parts.add(Objects.requireNonNull(condition, "condition"));
    return this;
  }

  @Override
  public SQLFragmentStep fragment(SQLFragment fragment)
  {
    parts.add(Objects.requireNonNull(fragment, "fragment"));
    return this;
  }

  @Override
  public SQLFragment build()
  {
    return new Fragment(parts);
  }
}
