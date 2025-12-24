/*
 * Copyright (c) 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ecore.logicalstructure.util;

import org.eclipse.debug.core.DebugException;
import org.eclipse.jdt.debug.core.IJavaClassType;
import org.eclipse.jdt.debug.core.IJavaInterfaceType;
import org.eclipse.jdt.debug.core.IJavaObject;
import org.eclipse.jdt.debug.core.IJavaType;

/**
 * @author Eike Stepper
 */
public final class DebugUtil
{
  private DebugUtil()
  {
  }

  public static boolean isInstanceOf(IJavaObject javaValue, String typeConstraint) throws DebugException
  {
    IJavaType type = javaValue.getJavaType();
    if (type instanceof IJavaClassType)
    {
      IJavaClassType classType = (IJavaClassType)type;

      IJavaInterfaceType[] interfaceTypes = classType.getAllInterfaces();
      for (int i = 0; i < interfaceTypes.length; i++)
      {
        if (typeConstraint.equals(interfaceTypes[i].getName()))
        {
          return true;
        }
      }
    }

    return false;
  }
}
