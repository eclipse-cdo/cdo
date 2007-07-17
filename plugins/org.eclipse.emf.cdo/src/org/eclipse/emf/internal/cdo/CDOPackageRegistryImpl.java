/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.util.CDOPackageRegistry;

import org.eclipse.net4j.util.ReflectUtil;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EPackage.Registry;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.emf.ecore.impl.EPackageRegistryImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.internal.cdo.bundle.OM;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class CDOPackageRegistryImpl extends EPackageRegistryImpl implements CDOPackageRegistry
{
  private static final long serialVersionUID = 1L;

  public CDOPackageRegistryImpl()
  {
  }

  public CDOPackageRegistryImpl(Registry delegateRegistry)
  {
    super(delegateRegistry);
  }

  public EPackage putEPackage(EPackage ePackage)
  {
    return (EPackage)put(ePackage.getNsURI(), ePackage);
  }

  @Override
  public Object put(String key, Object value)
  {
    EPackage ePackage = (EPackage)value;
    if (isDynamicPackage(ePackage))
    {
      EPackageImpl copy = (EPackageImpl)EcoreUtil.copy(ePackage);
      copy.setEFactoryInstance(createCDOFactory(copy));
      fixEClassifiers(copy);
      ePackage = copy;
    }

    super.put(key, ePackage);
    return ePackage;
  }

  @Override
  public void putAll(Map<? extends String, ? extends Object> m)
  {
    throw new UnsupportedOperationException();
  }

  protected CDOFactoryImpl createCDOFactory(EPackage ePackage)
  {
    return new CDOFactoryImpl(ePackage);
  }

  private static boolean isDynamicPackage(EPackage ePackage)
  {
    return ePackage.getClass() == EPackageImpl.class;
  }

  private static void fixEClassifiers(EPackageImpl ePackage)
  {
    try
    {
      Method method = EPackageImpl.class.getDeclaredMethod("fixEClassifiers", ReflectUtil.NO_PARAMETERS);
      if (!method.isAccessible())
      {
        method.setAccessible(true);
      }

      method.invoke(ePackage, ReflectUtil.NO_ARGUMENTS);
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
    }
  }
}
