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

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.CDOTransactionCommittedEvent;
import org.eclipse.emf.cdo.CDOTransactionDirtyEvent;
import org.eclipse.emf.cdo.CDOTransactionHandler;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl;
import org.eclipse.emf.cdo.internal.protocol.CDOIDImpl;
import org.eclipse.emf.cdo.internal.protocol.model.CDOPackageImpl;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.model.CDOPackage;
import org.eclipse.emf.cdo.protocol.util.TransportException;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.protocol.CommitTransactionRequest;
import org.eclipse.emf.internal.cdo.protocol.CommitTransactionResult;
import org.eclipse.emf.internal.cdo.util.ModelUtil;

import org.eclipse.net4j.IChannel;
import org.eclipse.net4j.internal.util.event.Notifier;
import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.signal.IFailOverStrategy;
import org.eclipse.net4j.util.ImplementationError;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class CDOTransactionImpl extends CDOViewImpl implements CDOTransaction
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_TRANSCTION, CDOTransactionImpl.class);

  private static final long INITIAL_TEMPORARY_ID = -2L;

  private transient long nextTemporaryID = INITIAL_TEMPORARY_ID;

  /**
   * TODO Optimize by storing an array. See {@link Notifier}.
   */
  private List<CDOTransactionHandler> handlers = new ArrayList<CDOTransactionHandler>(0);

  private List<CDOPackage> newPackages;

  private Map<CDOID, CDOResource> newResources = new HashMap<CDOID, CDOResource>();

  private Map<CDOID, CDOObject> newObjects = new HashMap<CDOID, CDOObject>();

  private Map<CDOID, CDOObject> dirtyObjects = new HashMap<CDOID, CDOObject>();

  private boolean dirty;

  public CDOTransactionImpl(int id, CDOSessionImpl session)
  {
    super(id, session);
  }

  @Override
  public Type getViewType()
  {
    return Type.TRANSACTION;
  }

  public void addHandler(CDOTransactionHandler handler)
  {
    synchronized (handlers)
    {
      handlers.add(handler);
    }
  }

  public void removeHandler(CDOTransactionHandler handler)
  {
    synchronized (handlers)
    {
      handlers.remove(handler);
    }
  }

  public CDOTransactionHandler[] getHandlers()
  {
    synchronized (handlers)
    {
      return handlers.toArray(new CDOTransactionHandler[handlers.size()]);
    }
  }

  @Override
  public boolean isDirty()
  {
    return dirty;
  }

  public List<CDOPackage> getNewPackages()
  {
    return newPackages;
  }

  public Map<CDOID, CDOResource> getNewResources()
  {
    return newResources;
  }

  public Map<CDOID, CDOObject> getNewObjects()
  {
    return newObjects;
  }

  public Map<CDOID, CDOObject> getDirtyObjects()
  {
    return dirtyObjects;
  }

  public CDOID getNextTemporaryID()
  {
    long id = nextTemporaryID;
    --nextTemporaryID;
    --nextTemporaryID;
    return CDOIDImpl.create(id);
  }

  public CDOResource createResource(String path)
  {
    URI createURI = CDOUtil.createResourceURI(path);
    return (CDOResource)getResourceSet().createResource(createURI);
  }

  public void commit()
  {
    checkWritable();
    if (dirty)
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace("commit()");
      }

      for (CDOTransactionHandler handler : getHandlers())
      {
        handler.committingTransaction(this);
      }

      try
      {
        CDOSessionImpl session = getSession();
        newPackages = analyzeNewPackages(session);

        preCommit(newObjects);
        preCommit(dirtyObjects);

        IChannel channel = session.getChannel();
        IFailOverStrategy failOverStrategy = session.getFailOverStrategy();
        CommitTransactionRequest request = new CommitTransactionRequest(channel, this);
        CommitTransactionResult result = failOverStrategy.send(request, 100000L);
        // TODO Change timeout semantics in Net4j

        postCommit(newResources, result);
        postCommit(newObjects, result);
        postCommit(dirtyObjects, result);

        if (!dirtyObjects.isEmpty())
        {
          session.notifyInvalidation(result.getTimeStamp(), dirtyObjects.keySet(), this);
        }

        for (CDOPackage newPackage : newPackages)
        {
          ((CDOPackageImpl)newPackage).setPersistent(true);
        }

        newPackages = null;
        newResources.clear();
        newObjects.clear();
        dirtyObjects.clear();
        dirty = false;
        nextTemporaryID = INITIAL_TEMPORARY_ID;

        Map<CDOID, CDOID> idMappings = result.getIDMappings();
        fireEvent(new CommittedEvent(idMappings));
      }
      catch (RuntimeException ex)
      {
        throw ex;
      }
      catch (Exception ex)
      {
        throw new TransportException(ex);
      }
    }
  }

  public void rollback()
  {
    checkWritable();

    try
    {
      // TODO Implement method CDOTransactionImpl.rollback()
      throw new UnsupportedOperationException("Not yet implemented");
    }
    catch (RuntimeException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw new TransportException(ex);
    }
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("CDOTransaction({0})", getViewID());
  }

  public void registerNew(InternalCDOObject object)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Registering new object {0}", object);
    }

    for (CDOTransactionHandler handler : getHandlers())
    {
      handler.addingObject(this, object);
    }

    if (object instanceof CDOResourceImpl)
    {
      register(newResources, object);
    }
    else
    {
      register(newObjects, object);
    }
  }

  public void registerDirty(InternalCDOObject object)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Registering dirty object {0}", object);
    }

    for (CDOTransactionHandler handler : getHandlers())
    {
      handler.modifyingObject(this, object);
    }

    register(dirtyObjects, object);
  }

  @SuppressWarnings("unchecked")
  private void register(Map map, InternalCDOObject object)
  {
    Object old = map.put(object.cdoID(), object);
    if (old != null)
    {
      throw new ImplementationError("Duplicate ID: " + object);
    }

    if (!dirty)
    {
      dirty = true;
      fireEvent(new DirtyEvent());
    }
  }

  private List<CDOPackage> analyzeNewPackages(CDOSessionImpl session)
  {
    // Find all used classes and their super classes
    Set<EClass> usedClasses = new HashSet<EClass>();
    for (CDOObject object : newObjects.values())
    {
      EClass usedClass = object.eClass();
      if (usedClasses.add(usedClass))
      {
        for (EClass superType : usedClass.getEAllSuperTypes())
        {
          usedClasses.add(superType);
        }
      }
    }

    // Calculate the packages of the used classes
    Set<EPackage> usedPackages = new HashSet<EPackage>();
    for (EClass usedClass : usedClasses)
    {
      usedPackages.add(usedClass.getEPackage());
    }

    // Determine which of the used packages are new
    CDOSessionPackageManager packageManager = session.getPackageManager();
    List<CDOPackage> newPackages = new ArrayList<CDOPackage>();
    for (EPackage usedPackage : usedPackages)
    {
      CDOPackageImpl cdoPackage = ModelUtil.getCDOPackage(usedPackage, packageManager);
      if (cdoPackage == null)
      {
        throw new IllegalStateException("Missing CDO package: " + usedPackage.getNsURI());
      }

      if (!cdoPackage.isPersistent() && !cdoPackage.isSystem())
      {
        newPackages.add(cdoPackage);
      }
    }

    return newPackages;
  }

  @SuppressWarnings("unchecked")
  private void preCommit(Map objects)
  {
    if (!objects.isEmpty())
    {
      for (Object object : objects.values())
      {
        ((InternalCDOObject)object).cdoInternalPreCommit();
      }
    }
  }

  @SuppressWarnings("unchecked")
  private void postCommit(Map objects, CommitTransactionResult result)
  {
    if (!objects.isEmpty())
    {
      for (Object object : objects.values())
      {
        CDOStateMachine.INSTANCE.commit((InternalCDOObject)object, result);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class DirtyEvent extends Event implements CDOTransactionDirtyEvent
  {
    private static final long serialVersionUID = 1L;

    private DirtyEvent()
    {
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class CommittedEvent extends Event implements CDOTransactionCommittedEvent
  {
    private static final long serialVersionUID = 1L;

    private Map<CDOID, CDOID> idMappings;

    private CommittedEvent(Map<CDOID, CDOID> idMappings)
    {
      this.idMappings = idMappings;
    }

    public Map<CDOID, CDOID> getIDMappings()
    {
      return idMappings;
    }
  }

  // private Collection<CDOPackageImpl> calculateNewPackages()
  // {
  // CDOSessionImpl session = view.getSession();
  // CDOPackageManagerImpl packageManager = session.getPackageManager();
  // return packageManager.getTransientPackages();
  //
  // CDOCorePackageImpl corePackage = packageManager.getCDOCorePackage();
  // CDOResourcePackageImpl resourcePackage =
  // packageManager.getCDOResourcePackage();
  //
  // Set<String> knownPackages = session.getPackageURIs();
  // Map<String, CDOPackageImpl> newPackages = new HashMap();
  // for (InternalCDOObject cdoObject : newObjects.values())
  // {
  // CDOPackageImpl cdoPackage = cdoObject.cdoClass().getContainingPackage();
  // String uri = cdoPackage.getPackageURI();
  // if (!newPackages.containsKey(uri) && !knownPackages.contains(uri))
  // {
  // EPackage ePackage = EMFUtil.getEPackage(cdoPackage);
  // Set<EPackage> ePackages = PackageClosure.calculate(ePackage);
  // for (EPackage eP : ePackages)
  // {
  // CDOPackageImpl cdoP = eP == ePackage ? cdoPackage :
  // EMFUtil.getCDOPackage(eP, packageManager);
  // if (cdoP == null)
  // {
  // throw new IllegalStateException("Not a CDO package: " + eP);
  // }
  //
  // if (cdoP != corePackage && cdoP != resourcePackage)
  // {
  // uri = cdoP.getPackageURI();
  // if (!newPackages.containsKey(uri) && !knownPackages.contains(uri))
  // {
  // newPackages.put(uri, cdoP);
  // }
  // }
  // }
  // }
  // }
  //
  // return newPackages.values();
  // }
}
