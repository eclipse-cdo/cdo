/*
 * Copyright (c) 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ecore.logicalstructure;

import org.eclipse.emf.cdo.ecore.logicalstructure.util.DebugUtil;

import org.eclipse.debug.core.ILogicalStructureProvider;
import org.eclipse.debug.core.ILogicalStructureType;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.jdt.debug.core.IJavaObject;

/**
 * @author Eike Stepper
 */
public class EMFLogicalStructureProvider implements ILogicalStructureProvider
{
  private static final ILogicalStructureType[] NO_TYPES = {};

  @Override
  public ILogicalStructureType[] getLogicalStructureTypes(IValue value)
  {
    try
    {
      if (value instanceof IJavaObject)
      {
        if (DebugUtil.isInstanceOf((IJavaObject)value, "org.eclipse.emf.ecore.InternalEObject"))
        {
          return EMFLogicalStructureType.ARRAY;
        }
      }
    }
    catch (Exception ex)
    {
      Activator.error(ex);
    }

    return NO_TYPES;
  }
}
