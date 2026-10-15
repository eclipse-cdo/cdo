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

import org.eclipse.net4j.db.dml.SQLParameter;
import org.eclipse.net4j.db.dml.SQLValueSetter;

import java.util.function.Function;

/**
 * Immutable identity for a future JDBC parameter occurrence group.
 *
 * @author Eike Stepper
 * @since 4.15
 */
public final class Parameter extends Expression implements SQLParameter
{
  private final Integer sqlType;

  private final Function<?, ?> converter;

  private final SQLValueSetter<?> setter;

  public Parameter(Integer sqlType, Function<?, ?> converter)
  {
    this(sqlType, converter, null);
  }

  public Parameter(Integer sqlType, SQLValueSetter<?> setter)
  {
    this(sqlType, null, setter);
  }

  private Parameter(Integer sqlType, Function<?, ?> converter, SQLValueSetter<?> setter)
  {
    super(Kind.PARAMETER);
    this.sqlType = sqlType;
    this.converter = converter;
    this.setter = setter;
  }

  public Integer getSQLType()
  {
    return sqlType;
  }

  public Function<?, ?> getConverter()
  {
    return converter;
  }

  public SQLValueSetter<?> getSetter()
  {
    return setter;
  }

  @Override
  public String toString()
  {
    return "Parameter@" + Integer.toHexString(System.identityHashCode(this));
  }
}
