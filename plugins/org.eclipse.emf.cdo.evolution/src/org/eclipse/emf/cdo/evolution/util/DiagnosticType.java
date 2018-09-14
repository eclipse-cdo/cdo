/*
 * Copyright (c) 2004-2018 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.evolution.util;

import org.eclipse.emf.common.util.Diagnostic;

/**
 * @author Eike Stepper
 */
public final class DiagnosticType
{
  private final String source;

  private final int code;

  public DiagnosticType(String source, int code)
  {
    this.source = source;
    this.code = code;
  }

  public String getSource()
  {
    return source;
  }

  public int getCode()
  {
    return code;
  }

  public boolean appliesTo(Diagnostic diagnostic)
  {
    return code == diagnostic.getCode() && source.equals(diagnostic.getSource());
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + code;
    result = prime * result + (source == null ? 0 : source.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
    {
      return true;
    }

    if (obj == null)
    {
      return false;
    }

    if (getClass() != obj.getClass())
    {
      return false;
    }

    DiagnosticType other = (DiagnosticType)obj;
    if (code != other.code)
    {
      return false;
    }

    if (source == null)
    {
      if (other.source != null)
      {
        return false;
      }
    }
    else if (!source.equals(other.source))
    {
      return false;
    }

    return true;
  }

  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append("DiagnosticType[");
    builder.append(source);
    builder.append(":");
    builder.append(code);
    builder.append("]");
    return builder.toString();
  }

  public static DiagnosticType from(Diagnostic diagnostic)
  {
    return new DiagnosticType(diagnostic.getSource(), diagnostic.getCode());
  }
}
