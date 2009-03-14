/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - maintenance 
 */
package org.eclipse.emf.cdo.util;

import org.eclipse.emf.internal.cdo.CDOFactoryImpl;

import org.eclipse.net4j.util.ObjectUtil;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * @author Eike Stepper
 */
public final class ModelUtil
{
  private ModelUtil()
  {
  }

  public static EPackage getGeneratedEPackage(EPackage ePackage)
  {
    String packageURI = ePackage.getNsURI();
    if (packageURI.equals(EcorePackage.eINSTANCE.getNsURI()))
    {
      return EcorePackage.eINSTANCE;
    }

    EPackage.Registry registry = EPackage.Registry.INSTANCE;
    return registry.getEPackage(packageURI);
  }

  public static EPackageImpl prepareDynamicEPackage(EPackageImpl ePackage, String nsURI)
  {
    CDOFactoryImpl.prepareDynamicEPackage(ePackage);
    EPackageImpl result = ObjectUtil.equals(ePackage.getNsURI(), nsURI) ? ePackage : null;
    for (EPackage subPackage : ePackage.getESubpackages())
    {
      EPackageImpl p = prepareDynamicEPackage((EPackageImpl)subPackage, nsURI);
      if (p != null && result == null)
      {
        result = p;
      }
    }

    return result;
  }
}
