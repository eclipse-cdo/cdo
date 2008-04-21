/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.internal.protocol.model.InternalCDOPackage;
import org.eclipse.emf.cdo.protocol.model.CDOPackage;
import org.eclipse.emf.cdo.util.CDOPackageRegistry;
import org.eclipse.emf.cdo.util.EMFUtil;

import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.util.ModelUtil;

import org.eclipse.net4j.internal.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EPackage.Registry;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.emf.ecore.impl.EPackageRegistryImpl;

import java.text.MessageFormat;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class CDOPackageRegistryImpl extends EPackageRegistryImpl implements CDOPackageRegistry
{
  private final ContextTracer TRACER = new ContextTracer(OM.DEBUG_MODEL, CDOPackageRegistryImpl.class);

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

  public void putPackageDescriptor(CDOPackage cdoPackage)
  {
    EPackage.Descriptor descriptor = new CDOPackageDescriptor(cdoPackage);
    String uri = cdoPackage.getPackageURI();
    if (TRACER.isEnabled())
    {
      TRACER.format("Registering package descriptor for {0}", uri);
    }

    put(uri, descriptor);
  }

  public EPackage putEPackage(EPackage ePackage)
  {
    String uri = ePackage.getNsURI();
    if (ePackage.getESuperPackage() != null)
    {
      throw new IllegalArgumentException("Not a top level package: " + uri);
    }

    putEPackage(uri, ePackage);
    return getEPackage(uri);
  }

  private void putEPackage(String uri, EPackage ePackage)
  {
    if (uri != null)
    {
      put(uri, ePackage);
    }

    for (EPackage subPackage : ePackage.getESubpackages())
    {
      putEPackage(subPackage.getNsURI(), subPackage);
    }
  }

  @Override
  public Object put(String key, Object value)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Registering package for {0}", key);
    }

    if (value instanceof EPackageImpl)
    {
      EPackageImpl ePackage = (EPackageImpl)value;
      if (EMFUtil.isDynamicEPackage(ePackage))
      {
        ModelUtil.prepareEPackage(ePackage);
      }

      CDOPackage cdoPackage = ModelUtil.getCDOPackage(ePackage, session.getPackageManager());
      ((InternalCDOPackage)cdoPackage).setPersistent(!cdoPackage.getMetaIDRange().isTemporary());
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
  private final class CDOPackageDescriptor implements EPackage.Descriptor
  {
    private CDOPackage cdoPackage;

    private CDOPackageDescriptor(CDOPackage cdoPackage)
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
      EPackage ePackage = ModelUtil.createEPackage(cdoPackage);
      session.registerEPackage(ePackage, cdoPackage.getMetaIDRange());
      return ePackage;
    }

    @Override
    public String toString()
    {
      return MessageFormat.format("CDOPackageDescriptor[{0}]", cdoPackage.getPackageURI());
    }
  }
}
