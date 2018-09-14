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

import org.eclipse.emf.cdo.evolution.Change;
import org.eclipse.emf.cdo.evolution.Evolution;
import org.eclipse.emf.cdo.evolution.Migration;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.Diagnostician;
import org.eclipse.emf.ecore.util.EcoreUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public enum ValidationPhase
{
  MODEL_AVAILABILITY
  {
    @Override
    public List<? extends EObject> getContentsToValidate(Diagnostician diagnostician, Evolution evolution, EObject eObject, Map<Object, Object> context)
    {
      if (eObject == evolution)
      {
        return evolution.getModels();
      }

      return Collections.emptyList();
    }
  },

  MODEL_UNIQUENESS
  {
    @Override
    public List<? extends EObject> getContentsToValidate(Diagnostician diagnostician, Evolution evolution, EObject eObject, Map<Object, Object> context)
    {
      if (eObject == evolution)
      {
        return evolution.getModels();
      }

      return Collections.emptyList();
    }
  },

  MODEL_INTEGRITY
  {
    @Override
    public List<? extends EObject> getContentsToValidate(Diagnostician diagnostician, Evolution evolution, EObject eObject, Map<Object, Object> context)
    {
      return Collections.emptyList();
    }
  },

  MODEL_VALIDITY
  {
    @Override
    public List<? extends EObject> getContentsToValidate(Diagnostician diagnostician, Evolution evolution, EObject eObject, Map<Object, Object> context)
    {
      if (eObject == evolution)
      {
        return evolution.getRootPackages();
      }

      if (isPackageElement(evolution, eObject))
      {
        return super.getContentsToValidate(diagnostician, evolution, eObject, context);
      }

      return Collections.emptyList();
    }
  },

  IDENTITY_COMPLETENESS
  {
    @Override
    public List<? extends EObject> getContentsToValidate(Diagnostician diagnostician, Evolution evolution, EObject eObject, Map<Object, Object> context)
    {
      return MODEL_AVAILABILITY.getContentsToValidate(diagnostician, evolution, eObject, context);
    }
  },

  IDENTITY_UNIQUENESS
  {
    @Override
    public List<? extends EObject> getContentsToValidate(Diagnostician diagnostician, Evolution evolution, EObject eObject, Map<Object, Object> context)
    {
      return Collections.emptyList();
    }
  },

  CHANGE_VALIDITY
  {
    @Override
    public List<? extends EObject> getContentsToValidate(Diagnostician diagnostician, Evolution evolution, EObject eObject, Map<Object, Object> context)
    {
      if (eObject == evolution)
      {
        return Collections.singletonList(evolution.getChange());
      }

      if (eObject instanceof Change)
      {
        Change change = (Change)eObject;
        if (EcoreUtil.isAncestor(evolution.getChange(), change))
        {
          return change.getChildren();
        }
      }

      return Collections.emptyList();
    }
  },

  MIGRATION_OBSOLETENESS
  {
    @Override
    public List<? extends EObject> getContentsToValidate(Diagnostician diagnostician, Evolution evolution, EObject eObject, Map<Object, Object> context)
    {
      if (eObject == evolution)
      {
        return evolution.getMigrations();
      }

      return Collections.emptyList();
    }
  },

  MIGRATION_VALIDITY
  {
    @Override
    public List<? extends EObject> getContentsToValidate(Diagnostician diagnostician, Evolution evolution, EObject eObject, Map<Object, Object> context)
    {
      if (eObject == evolution)
      {
        return evolution.getMigrations();
      }

      if (eObject instanceof Migration)
      {
        return super.getContentsToValidate(diagnostician, evolution, eObject, context);
      }

      return Collections.emptyList();
    }
  },

  RELEASE
  {
    @Override
    public List<? extends EObject> getContentsToValidate(Diagnostician diagnostician, Evolution evolution, EObject eObject, Map<Object, Object> context)
    {
      return Collections.emptyList();
    }
  };

  public static final List<ValidationPhase> ALL = Arrays.asList(ValidationPhase.values());

  public List<? extends EObject> getContentsToValidate(Diagnostician diagnostician, Evolution evolution, EObject eObject, Map<Object, Object> context)
  {
    return eObject.eContents();
  }

  private static boolean isPackageElement(Evolution evolution, EObject eObject)
  {
    EObject rootContainer = EcoreUtil.getRootContainer(eObject);
    for (EPackage rootPackage : evolution.getRootPackages())
    {
      if (rootContainer == rootPackage)
      {
        return true;
      }
    }

    return false;
  }
}
