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
package org.eclipse.net4j.internal.db.dml;

import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.dml.SQLStatement;
import org.eclipse.net4j.db.dml.SQLSubSelect;
import org.eclipse.net4j.db.dml.SQLTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Immutable structural definition shared by fluent builders and templates. Compilation is
 * delegated to the internal adapter-aware SQL renderer.
 *
 * @author Eike Stepper
 * @since 4.15
 */
public final class Definition implements SQLTemplate, SQLSubSelect
{
  private final String kind;

  private final Object target;

  private final List<Object> parts;

  public Definition(String kind, Object target, Object... parts)
  {
    this.kind = kind;
    this.target = target;
    this.parts = Collections.unmodifiableList(Arrays.asList(parts.clone()));
  }

  public String getKind()
  {
    return kind;
  }

  public Object getTarget()
  {
    return target;
  }

  public List<Object> getParts()
  {
    return parts;
  }

  /**
   * Compiles this immutable definition using adapter-specific identifier quoting.
   *
   * @param adapter the database adapter used for rendering
   * @return the immutable compiled SQL statement
   */
  @Override
  public SQLStatement compile(IDBAdapter adapter)
  {
    return Compiler.compile(this, adapter);
  }

  /**
   * One ordered operation in a fluent statement definition.
   *
   * @author Eike Stepper
   * @since 4.15
   */
  public static final class Operation
  {
    private final String name;

    private final Object[] operands;

    public Operation(String name, Object... operands)
    {
      this.name = name;
      this.operands = operands.clone();
    }

    public String getName()
    {
      return name;
    }

    public Object[] getOperands()
    {
      return operands.clone();
    }
  }
}
