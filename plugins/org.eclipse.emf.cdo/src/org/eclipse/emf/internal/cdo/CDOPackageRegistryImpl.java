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

import org.eclipse.emf.cdo.internal.protocol.model.CDOPackageImpl;
import org.eclipse.emf.cdo.util.CDOPackageRegistry;
import org.eclipse.emf.cdo.util.EMFUtil;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EPackage.Registry;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.emf.ecore.impl.EPackageRegistryImpl;
import org.eclipse.emf.internal.cdo.util.ModelUtil;

import java.util.Map;

/**
 * @author Eike Stepper
 */
public class CDOPackageRegistryImpl extends EPackageRegistryImpl implements CDOPackageRegistry
{
  private static final long serialVersionUID = 1L;

  private CDOSessionImpl session;

  public CDOPackageRegistryImpl(CDOSessionImpl session)
  {
    this.session = session;
  }

  public CDOPackageRegistryImpl(CDOSessionImpl session, Registry delegateRegistry)
  {
    super(delegateRegistry);
    this.session = session;
  }

  public CDOSessionImpl getSession()
  {
    return session;
  }

  public EPackage putEPackage(EPackage ePackage)
  {
    String uri = ePackage.getNsURI();
    put(uri, ePackage);
    return getEPackage(uri);
  }

  @Override
  public Object put(String key, Object value)
  {
    if (EMFUtil.isDynamicEPackage(value))
    {
      EPackageImpl ePackage = (EPackageImpl)value;
      ModelUtil.prepareEPackage(ePackage);
      CDOPackageImpl cdoPackage = ModelUtil.getCDOPackage(ePackage, session.getPackageManager());
      cdoPackage.setPersistent(false);
      value = ePackage;
    }

    return internalPut(key, value);
  }

  public Object internalPut(String key, Object value)
  {
    return super.put(key, value);
  }

  @Override
  public void putAll(Map<? extends String, ? extends Object> m)
  {
    throw new UnsupportedOperationException();
  }
}
