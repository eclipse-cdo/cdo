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

import org.eclipse.emf.cdo.evolution.Evolution;
import org.eclipse.emf.cdo.evolution.Migration;

import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.ecore.resource.Resource;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public final class ValidationContext
{
  private static final String KEY = ValidationContext.class.getName();

  private final Evolution evolution;

  private ValidationPhase phase;

  private final Map<Resource, BasicDiagnostic> resourceDiagnostics = new HashMap<Resource, BasicDiagnostic>();

  private final Map<String, Object> identifiedElements = new HashMap<String, Object>();

  private final Map<DiagnosticID, Migration> usedMigrations = new HashMap<DiagnosticID, Migration>();

  public ValidationContext(Evolution evolution)
  {
    this.evolution = evolution;
    phase = ValidationPhase.values()[0];
  }

  public Evolution getEvolution()
  {
    return evolution;
  }

  public ValidationPhase getPhase()
  {
    return phase;
  }

  public void setPhase(ValidationPhase phase)
  {
    this.phase = phase;
  }

  public Map<Resource, BasicDiagnostic> getResourceDiagnostics()
  {
    return resourceDiagnostics;
  }

  public Map<String, Object> getIdentifiedElements()
  {
    return identifiedElements;
  }

  public Map<DiagnosticID, Migration> getUsedMigrations()
  {
    return usedMigrations;
  }

  public void putInto(Map<Object, Object> context)
  {
    context.put(KEY, this);
  }

  public static ValidationContext getFrom(Map<Object, Object> context)
  {
    Object contextObject = context.get(KEY);
    if (contextObject instanceof ValidationContext)
    {
      return (ValidationContext)contextObject;
    }

    return null;
  }

  public static void removeFrom(Map<Object, Object> context)
  {
    context.remove(KEY);
  }

  public static boolean isPhase(Map<Object, Object> context, ValidationPhase phase)
  {
    ValidationContext validationContext = getFrom(context);
    return validationContext != null && validationContext.getPhase() == phase;
  }
}
