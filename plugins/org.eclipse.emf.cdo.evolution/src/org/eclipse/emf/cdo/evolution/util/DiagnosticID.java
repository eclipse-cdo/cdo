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
import org.eclipse.emf.ecore.EModelElement;

/**
 * @author Eike Stepper
 */
public final class DiagnosticID
{
  private final String value;

  public DiagnosticID(String value)
  {
    this.value = value;
  }

  public String getValue()
  {
    return value;
  }

  @Override
  public int hashCode()
  {
    return 31 + (value == null ? 0 : value.hashCode());
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

    DiagnosticID other = (DiagnosticID)obj;
    if (value == null)
    {
      if (other.value != null)
      {
        return false;
      }
    }
    else if (!value.equals(other.value))
    {
      return false;
    }

    return true;
  }

  @Override
  public String toString()
  {
    return "DiagnosticID[" + value + "]";
  }

  public static DiagnosticID get(Diagnostic diagnostic)
  {
    for (Object data : diagnostic.getData())
    {
      if (data instanceof DiagnosticID)
      {
        return (DiagnosticID)data;
      }
    }

    StringBuilder builder = new StringBuilder();
    builder.append(diagnostic.getSource());
    builder.append("|");
    builder.append(diagnostic.getCode());

    for (Object data : diagnostic.getData())
    {
      appendData(builder, data);
    }

    return new DiagnosticID(builder.toString());
  }

  private static void appendData(final StringBuilder builder, Object data)
  {
    if (data instanceof Provider)
    {
      Provider provider = (Provider)data;
      provider.extractDiagnosticData(new Provider.Context()
      {
        public void add(String data)
        {
          builder.append("|");
          builder.append(data);
        }
      });

      return;
    }

    if (data instanceof EModelElement)
    {
      EModelElement modelElement = (EModelElement)data;
      String label = ElementHandler.getLabel(modelElement);
      if (label != null)
      {
        builder.append("|");
        builder.append(label);
        return;
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public interface Provider
  {
    public void extractDiagnosticData(Context context);

    /**
     * @author Eike Stepper
     */
    public interface Context
    {
      public void add(String data);
    }
  }
}
