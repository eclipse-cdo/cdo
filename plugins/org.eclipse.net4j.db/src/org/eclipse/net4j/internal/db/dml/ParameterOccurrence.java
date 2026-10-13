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

import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.dml.SQLParameter;

/**
 * Immutable metadata for one JDBC placeholder occurrence.
 *
 * @author Eike Stepper
 * @since 4.15
 */
public final class ParameterOccurrence
{
  private final SQLParameter parameter;

  private final DBType inferredType;

  /**
   * Creates the metadata for one generated placeholder.
   *
   * @param parameter the logical parameter identity for the placeholder
   * @param inferredType the field type associated with this specific occurrence, or {@code null} when unknown
   */
  public ParameterOccurrence(SQLParameter parameter, DBType inferredType)
  {
    this.parameter = parameter;
    this.inferredType = inferredType;
  }

  /**
   * Returns the logical parameter identity represented by this placeholder.
   *
   * @return the parameter identity
   */
  public SQLParameter getParameter()
  {
    return parameter;
  }

  /**
   * Returns the field type inferred for this occurrence.
   *
   * @return the inferred database type, or {@code null} when the expression has no direct field context
   */
  public DBType getInferredType()
  {
    return inferredType;
  }
}
