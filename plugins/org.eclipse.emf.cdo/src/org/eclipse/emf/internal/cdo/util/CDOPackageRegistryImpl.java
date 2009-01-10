/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Eike Stepper - maintenance
 **************************************************************************/
package org.eclipse.emf.internal.cdo.util;

import org.eclipse.emf.cdo.CDOCommitContext;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.CDOTransactionHandler;
import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.common.id.CDOIDMetaRange;
import org.eclipse.emf.cdo.common.model.CDOPackage;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackage;
import org.eclipse.emf.cdo.util.CDOPackageRegistry;
import org.eclipse.emf.cdo.util.CDOPackageType;
import org.eclipse.emf.cdo.util.CDOPackageTypeRegistry;
import org.eclipse.emf.cdo.util.EMFUtil;

import org.eclipse.emf.internal.cdo.CDOSessionPackageManagerImpl;
import org.eclipse.emf.internal.cdo.InternalCDOSession;
import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.util.container.ContainerEventAdapter;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageRegistryImpl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class CDOPackageRegistryImpl extends EPackageRegistryImpl implements CDOPackageRegistry
{
  private static final long serialVersionUID = 1L;

  private final ContextTracer tracer = new ContextTracer(OM.DEBUG_MODEL, CDOPackageRegistryImpl.class);

  private InternalCDOSession session;

  public CDOPackageRegistryImpl()
  {
  }

  public InternalCDOSession getSession()
  {
    return session;
  }

  public void setSession(CDOSession session)
  {
    this.session = (InternalCDOSession)session;
  }

  public void putPackageDescriptor(CDOPackage cdoPackage)
  {
    checkSession();
    EPackage.Descriptor descriptor = new RemotePackageDescriptor(cdoPackage);
    String uri = cdoPackage.getPackageURI();
    if (tracer.isEnabled())
    {
      tracer.format("Registering package descriptor for {0}", uri);
    }

    put(uri, descriptor);
  }

  /**
   * Insert the topPackage and its sub-packages by their URI
   */
  public EPackage putEPackage(EPackage ePackage) throws IllegalArgumentException
  {
    checkSession();
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
    checkSession();
    if (value instanceof EPackage)
    {
      if (tracer.isEnabled())
      {
        tracer.format("Registering package for {0}", key);
      }

      EPackage ePackage = (EPackage)value;
      EMFUtil.prepareDynamicEPackage(ePackage);

      CDOSessionPackageManagerImpl packageManager = (CDOSessionPackageManagerImpl)session.getPackageManager();
      CDOPackage cdoPackage = ModelUtil.getCDOPackage(ePackage, packageManager);
      CDOIDMetaRange metaIDRange = cdoPackage.getTopLevelPackage().getMetaIDRange();
      ((InternalCDOPackage)cdoPackage).setPersistent(metaIDRange != null && !metaIDRange.isTemporary());
    }

    return super.put(key, value);
  }

  @Override
  public void putAll(Map<? extends String, ? extends Object> m)
  {
    throw new UnsupportedOperationException();
  }

  private void checkSession()
  {
    if (session == null)
    {
      throw new IllegalStateException("session == null");
    }
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
      // TODO Implement method RemotePackageDescriptor.getEFactory()
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
      return MessageFormat.format("RemotePackageDescriptor[{0}]", cdoPackage.getPackageURI());
    }
  }

  /**
   * @author Eike Stepper
   */
  public static abstract class SessionBound extends CDOPackageRegistryImpl
  {
    private static final long serialVersionUID = 1L;

    private IListener sessionLifecycleListener = new LifecycleEventAdapter()
    {
      @Override
      protected void onActivated(ILifecycle lifecycle)
      {
        sessionActivated();
      }

      @Override
      protected void onAboutToDeactivate(ILifecycle lifecycle)
      {
        getSession().removeListener(this);
        sessionAboutToDeactivate();
      }
    };

    public SessionBound()
    {
    }

    @Override
    public void setSession(CDOSession session)
    {
      super.setSession(session);
      session.addListener(sessionLifecycleListener);
    }

    protected abstract void sessionActivated();

    protected abstract void sessionAboutToDeactivate();
  }

  /**
   * @author Eike Stepper
   */
  public static class Eager extends SessionBound
  {
    private static final long serialVersionUID = 1L;

    private IListener typeListener = new ContainerEventAdapter<Map.Entry<String, CDOPackageType>>()
    {
      @Override
      protected void onAdded(IContainer<java.util.Map.Entry<String, CDOPackageType>> container,
          java.util.Map.Entry<String, CDOPackageType> entry)
      {
        addEntry(entry);
      }
    };

    public Eager()
    {
    }

    @Override
    protected void sessionActivated()
    {
      for (Map.Entry<String, CDOPackageType> entry : CDOPackageTypeRegistry.INSTANCE.entrySet())
      {
        addEntry(entry);
      }

      CDOPackageTypeRegistry.INSTANCE.addListener(typeListener);
    }

    @Override
    protected void sessionAboutToDeactivate()
    {
      CDOPackageTypeRegistry.INSTANCE.removeListener(typeListener);
    }

    protected void addEntry(Map.Entry<String, CDOPackageType> entry)
    {
      CDOPackageType packageType = entry.getValue();
      // TODO LEGACY
      if (packageType != CDOPackageType.LEGACY)
      {
        String uri = entry.getKey();
        if (!containsKey(uri))
        {
          try
          {
            EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(uri);
            if (ePackage.getESuperPackage() == null)
            {
              putEPackage(ePackage);
            }
          }
          catch (RuntimeException ex)
          {
            OM.LOG.error(ex);
          }
        }
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class TransactionBound extends SessionBound implements CDOTransactionHandler
  {
    private static final long serialVersionUID = 1L;

    private List<CDOTransaction> transactions = new ArrayList<CDOTransaction>();

    private IListener sessionContainerListener = new ContainerEventAdapter<CDOView>()
    {
      @Override
      protected void onAdded(IContainer<CDOView> session, CDOView view)
      {
        if (view instanceof CDOTransaction)
        {
          CDOTransaction transaction = (CDOTransaction)view;
          transaction.addHandler(TransactionBound.this);
          synchronized (transactions)
          {
            transactions.add(transaction);
          }
        }
      }

      @Override
      protected void onRemoved(IContainer<CDOView> session, CDOView view)
      {
        if (view instanceof CDOTransaction)
        {
          CDOTransaction transaction = (CDOTransaction)view;
          transaction.removeHandler(TransactionBound.this);
          synchronized (transactions)
          {
            transactions.remove(transaction);
          }
        }
      }
    };

    public TransactionBound()
    {
    }

    @Override
    protected void sessionActivated()
    {
      getSession().addListener(sessionContainerListener);
    }

    @Override
    protected void sessionAboutToDeactivate()
    {
      getSession().removeListener(sessionContainerListener);
      synchronized (transactions)
      {
        for (CDOTransaction transaction : transactions)
        {
          transaction.removeHandler(this);
        }

        transactions.clear();
      }
    }

    public void attachingObject(CDOTransaction transaction, CDOObject object)
    {
    }

    public void modifyingObject(CDOTransaction transaction, CDOObject object, CDOFeatureDelta featureDelta)
    {
    }

    public void committingTransaction(CDOTransaction transaction, CDOCommitContext commitContext)
    {
    }

    public void rolledBackTransaction(CDOTransaction transaction)
    {
    }

    public void detachingObject(CDOTransaction transaction, CDOObject object)
    {
    }

    public void committedTransaction(CDOTransaction transaction, CDOCommitContext commitContext)
    {
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class Lazy extends TransactionBound
  {
    private static final long serialVersionUID = 1L;

    private Set<EClass> usedClasses = new HashSet<EClass>();

    public Lazy()
    {
    }

    @Override
    public void attachingObject(CDOTransaction transaction, CDOObject object)
    {
      EClass usedClass = object.eClass();
      addAllEPackages(usedClass);
    }

    private void addAllEPackages(EClass eClass)
    {
      if (usedClasses.add(eClass))
      {
        addPackage(eClass.getEPackage());
        for (EClass superType : eClass.getEAllSuperTypes())
        {
          addAllEPackages(superType);
        }

        for (EReference eReference : eClass.getEAllReferences())
        {
          addAllEPackages(eReference.getEReferenceType());
        }
      }
    }

    @Override
    protected void sessionAboutToDeactivate()
    {
      usedClasses.clear();
      super.sessionAboutToDeactivate();
    }

    private void addPackage(EPackage ePackage)
    {
      if (!containsKey(ePackage.getNsURI()))
      {
        EPackage topLevelPackage = ModelUtil.getTopLevelPackage(ePackage);
        // PutEPackage
        putEPackage(topLevelPackage);
      }
    }
  }
}
