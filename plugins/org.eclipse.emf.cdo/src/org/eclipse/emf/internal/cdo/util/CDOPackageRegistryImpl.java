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
package org.eclipse.emf.internal.cdo.util;

import org.eclipse.emf.cdo.internal.protocol.model.InternalCDOPackage;
import org.eclipse.emf.cdo.protocol.id.CDOIDMetaRange;
import org.eclipse.emf.cdo.protocol.model.CDOPackage;
import org.eclipse.emf.cdo.util.CDOPackageRegistry;
import org.eclipse.emf.cdo.util.CDOPackageType;
import org.eclipse.emf.cdo.util.CDOPackageTypeRegistry;
import org.eclipse.emf.cdo.util.EMFUtil;

import org.eclipse.emf.internal.cdo.CDOSessionImpl;
import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.internal.util.container.ContainerEventAdapter;
import org.eclipse.net4j.internal.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycle;

import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EPackage;
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

  public CDOSessionImpl getSession()
  {
    return session;
  }

  public void putPackageDescriptor(CDOPackage cdoPackage)
  {
    EPackage.Descriptor descriptor = new RemotePackageDescriptor(cdoPackage);
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
    if (value instanceof EPackage)
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
      }

      EPackage ePackage = (EPackage)value;
      CDOPackage cdoPackage = ModelUtil.getCDOPackage(ePackage, session.getPackageManager());
      ((InternalCDOPackage)cdoPackage).setPersistent(!cdoPackage.getTopLevelPackage().getMetaIDRange().isTemporary());
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
  private final class RemotePackageDescriptor implements EPackage.Descriptor
  {
    private CDOPackage cdoPackage;

    private RemotePackageDescriptor(CDOPackage cdoPackage)
    {
      this.cdoPackage = cdoPackage;
    }

    public CDOPackage getCDOPackage()
    {
      return cdoPackage;
    }

    public EFactory getEFactory()
    {
      // TODO Implement method LocalPackageDescriptor.getEFactory()
      throw new UnsupportedOperationException("Not yet implemented");
    }

    public EPackage getEPackage()
    {
      EPackage ePackage = ModelUtil.createEPackage(cdoPackage);
      CDOIDMetaRange idRange = cdoPackage.getMetaIDRange();
      if (idRange != null)
      {
        session.registerEPackage(ePackage, idRange);
      }

      return ePackage;
    }

    @Override
    public String toString()
    {
      return MessageFormat.format("LocalPackageDescriptor[{0}]", cdoPackage.getPackageURI());
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class SelfPopulating extends CDOPackageRegistryImpl
  {
    private static final long serialVersionUID = 1L;

    private IListener sessionListener = new LifecycleEventAdapter()
    {
      @Override
      protected void onActivated(ILifecycle lifecycle)
      {
        populate();
      }

      @Override
      protected void onAboutToDeactivate(ILifecycle lifecycle)
      {
        getSession().removeListener(this);
        CDOPackageTypeRegistry.INSTANCE.removeListener(typeListener);
      }
    };

    private IListener typeListener = new ContainerEventAdapter<Map.Entry<String, CDOPackageType>>()
    {
      @Override
      protected void onAdded(IContainer<java.util.Map.Entry<String, CDOPackageType>> container,
          java.util.Map.Entry<String, CDOPackageType> entry)
      {
        addEntry(entry);
      }
    };

    public SelfPopulating(CDOSessionImpl session)
    {
      super(session);
      session.addListener(sessionListener);
    }

    protected void populate()
    {
      for (Map.Entry<String, CDOPackageType> entry : CDOPackageTypeRegistry.INSTANCE.entrySet())
      {
        addEntry(entry);
      }

      CDOPackageTypeRegistry.INSTANCE.addListener(typeListener);
    }

    protected void addEntry(Map.Entry<String, CDOPackageType> entry)
    {
      CDOPackageType packageType = entry.getValue();
      if (packageType != CDOPackageType.LEGACY)
      {
        String uri = entry.getKey();
        if (!containsKey(uri))
        {
          try
          {
            EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(uri);
            putEPackage(ePackage);
          }
          catch (RuntimeException ex)
          {
            OM.LOG.error(ex);
          }
        }
      }
    }
  }
}
