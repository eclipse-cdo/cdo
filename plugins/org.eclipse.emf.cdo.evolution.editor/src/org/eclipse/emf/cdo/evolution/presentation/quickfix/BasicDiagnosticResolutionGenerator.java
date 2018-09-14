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

import org.eclipse.emf.cdo.evolution.presentation.EvolutionEditorPlugin;

import org.eclipse.emf.common.util.ResourceLocator;

/**
 * @author Eike Stepper
 */
public abstract class BasicDiagnosticResolutionGenerator implements DiagnosticResolution.Generator
{
  protected ResourceLocator getResourceLocator()
  {
    return EvolutionEditorPlugin.INSTANCE;
  }

  protected String getString(String key, Object... substitutions)
  {
    return getString(getResourceLocator(), key, substitutions);
  }

  private String getString(ResourceLocator resourceLocator, String key, Object[] substitutions)
  {
    return substitutions == null || substitutions.length == 0 ? resourceLocator.getString(key) : resourceLocator.getString(key, substitutions);
  }
}
