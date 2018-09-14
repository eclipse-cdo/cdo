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

import org.eclipse.emf.cdo.evolution.presentation.EvolutionEditor;
import org.eclipse.emf.cdo.evolution.presentation.quickfix.DiagnosticResolution.Generator.Context;

import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.factory.ProductCreationException;

import org.eclipse.emf.common.util.Diagnostic;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.graphics.Image;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public abstract class DiagnosticResolution
{
  public static final Diagnostic[] NO_DIAGNOSTICS = {};

  public abstract Image getImage();

  public abstract String getText();

  public abstract String getDescription();

  public abstract void run(Diagnostic diagnostic);

  public void run(Diagnostic[] diagnostics, IProgressMonitor monitor)
  {
    for (Diagnostic diagnostic : diagnostics)
    {
      monitor.subTask(diagnostic.getMessage());
      run(diagnostic);
    }
  }

  public Diagnostic[] findOtherDiagnostics(Diagnostic[] diagnostics)
  {
    return NO_DIAGNOSTICS;
  }

  public static DiagnosticResolution[] getResolutions(Diagnostic diagnostic, final EvolutionEditor editor)
  {
    final List<DiagnosticResolution> result = new ArrayList<DiagnosticResolution>();
    Context context = new Generator.Context()
    {
      public EvolutionEditor getEditor()
      {
        return editor;
      }

      public void add(DiagnosticResolution resolution)
      {
        result.add(resolution);
      }
    };

    for (String factoryType : IPluginContainer.INSTANCE.getFactoryTypes(Generator.Factory.PRODUCT_GROUP))
    {
      Generator generator = (Generator)IPluginContainer.INSTANCE.getElement(Generator.Factory.PRODUCT_GROUP, factoryType, null);
      generator.getResolutions(diagnostic, context);
    }

    return result.toArray(new DiagnosticResolution[result.size()]);
  }

  /**
   * @author Eike Stepper
   */
  public interface Generator
  {
    public void getResolutions(Diagnostic diagnostic, Context context);

    /**
     * @author Eike Stepper
     */
    public interface Context
    {
      public EvolutionEditor getEditor();

      public void add(DiagnosticResolution resolution);
    }

    /**
     * @author Eike Stepper
     */
    public static abstract class Factory extends org.eclipse.net4j.util.factory.Factory
    {
      public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.evolution.diagnosticResolutionGenerators";

      public static final String DEFAULT_TYPE = "default";

      public Factory()
      {
        this(DEFAULT_TYPE);
      }

      public Factory(String type)
      {
        super(PRODUCT_GROUP, type);
      }

      public abstract Generator create(String description) throws ProductCreationException;
    }
  }
}
