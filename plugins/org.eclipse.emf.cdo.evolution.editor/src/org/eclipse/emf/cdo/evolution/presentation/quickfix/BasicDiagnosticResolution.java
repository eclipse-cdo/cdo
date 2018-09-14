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
package org.eclipse.emf.cdo.evolution.presentation.quickfix;

import org.eclipse.emf.cdo.evolution.util.DiagnosticType;

import org.eclipse.emf.common.util.Diagnostic;

import org.eclipse.swt.graphics.Image;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public abstract class BasicDiagnosticResolution extends DiagnosticResolution implements DiagnosticResolutionRelevance
{
  private final Image image;

  private final String text;

  private final String description;

  private final boolean multi;

  public BasicDiagnosticResolution(Image image, String text, String description, boolean multi)
  {
    this.image = image;
    this.text = text;
    this.description = description;
    this.multi = multi;
  }

  @Override
  public Image getImage()
  {
    return image;
  }

  @Override
  public String getText()
  {
    return text;
  }

  @Override
  public String getDescription()
  {
    return description;
  }

  public boolean isMulti()
  {
    return multi;
  }

  @Override
  public Diagnostic[] findOtherDiagnostics(Diagnostic[] diagnostics)
  {
    if (multi && diagnostics != null && diagnostics.length != 0)
    {
      List<Diagnostic> result = new ArrayList<Diagnostic>();
      for (int i = 0; i < diagnostics.length; i++)
      {
        Diagnostic diagnostic = diagnostics[i];
        if (isCompatibleDiagnostic(diagnostic))
        {
          result.add(diagnostic);
        }
      }

      return result.toArray(new Diagnostic[result.size()]);
    }

    return super.findOtherDiagnostics(diagnostics);
  }

  protected boolean isCompatibleDiagnostic(Diagnostic diagnostic)
  {
    return false;
  }

  public int getRelevanceForResolution()
  {
    return 0;
  }

  @Override
  public abstract void run(Diagnostic diagnostic);

  /**
   * @author Eike Stepper
   */
  public static abstract class TypedDiagnosticResolution extends BasicDiagnosticResolution
  {
    private final DiagnosticType type;

    public TypedDiagnosticResolution(Image image, String text, String description, boolean multi, String source, int code)
    {
      super(image, text, description, multi);
      type = new DiagnosticType(source, code);
    }

    public DiagnosticType getType()
    {
      return type;
    }

    protected DiagnosticType[] getCompatibleTypes()
    {
      return new DiagnosticType[] { type };
    }

    @Override
    protected boolean isCompatibleDiagnostic(Diagnostic diagnostic)
    {
      for (int i = 0; i < getCompatibleTypes().length; i++)
      {
        DiagnosticType type = getCompatibleTypes()[i];
        if (type.appliesTo(diagnostic))
        {
          return true;
        }
      }

      return super.isCompatibleDiagnostic(diagnostic);
    }
  }
}
