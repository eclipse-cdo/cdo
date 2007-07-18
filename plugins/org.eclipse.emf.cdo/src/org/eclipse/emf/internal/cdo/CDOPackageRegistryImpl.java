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

import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EPackage.Registry;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.emf.ecore.impl.EPackageRegistryImpl;
import org.eclipse.emf.internal.cdo.util.ModelUtil;

import java.text.MessageFormat;
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

  public void putPackageDescriptor(CDOPackageImpl cdoPackage)
  {
    EPackage.Descriptor descriptor = new CDOPackageDescriptor(cdoPackage);
    String uri = cdoPackage.getPackageURI();
    put(uri, descriptor);
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
    Object oldValue = super.get(key);
    if (oldValue instanceof EPackageImpl)
    {
      throw new IllegalArgumentException("Duplicate key: " + key);
    }

    if (value instanceof EPackageImpl)
    {
      EPackageImpl ePackage = (EPackageImpl)value;
      if (EMFUtil.isDynamicEPackage(ePackage))
      {
        ModelUtil.prepareEPackage(ePackage);
      }

      CDOPackageImpl cdoPackage = ModelUtil.getCDOPackage(ePackage, session.getPackageManager());
      cdoPackage.setPersistent(false);
    }

    return super.put(key, value);
  }

  @Override
  public void putAll(Map<? extends String, ? extends Object> m)
  {
    throw new UnsupportedOperationException();
  }

  /**
   * @author Eike Stepper
   */
  private static final class CDOPackageDescriptor implements EPackage.Descriptor
  {
    private CDOPackageImpl cdoPackage;

    private CDOPackageDescriptor(CDOPackageImpl cdoPackage)
    {
      this.cdoPackage = cdoPackage;
    }

    public EFactory getEFactory()
    {
      // TODO Implement method CDOPackageDescriptor.getEFactory()
      throw new UnsupportedOperationException("Not yet implemented");
    }

    public EPackage getEPackage()
    {
      return ModelUtil.createEPackage(cdoPackage);
    }

    @Override
    public String toString()
    {
      return MessageFormat.format("CDOPackageDescriptor[{0}]", cdoPackage.getPackageURI());
    }
  }
}
