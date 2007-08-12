/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.CDOTransactionCommittedEvent;
import org.eclipse.emf.cdo.CDOTransactionDirtyEvent;
import org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl;
import org.eclipse.emf.cdo.internal.protocol.CDOIDImpl;
import org.eclipse.emf.cdo.internal.protocol.model.CDOPackageImpl;
import org.eclipse.emf.cdo.internal.protocol.model.CDOPackageManagerImpl;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.util.TransportException;

import org.eclipse.net4j.IChannel;
import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.ImplementationError;

import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.protocol.CommitTransactionRequest;
import org.eclipse.emf.internal.cdo.protocol.CommitTransactionResult;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class CDOTransactionImpl extends CDOViewImpl implements CDOTransaction
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_TRANSCTION, CDOTransactionImpl.class);

  private static final long INITIAL_TEMPORARY_ID = -2L;

  private transient long nextTemporaryID = INITIAL_TEMPORARY_ID;

  private List<CDOPackageImpl> newPackages;

  private Map<CDOID, CDOResourceImpl> newResources = new HashMap();

  private Map<CDOID, InternalCDOObject> newObjects = new HashMap();

  private Map<CDOID, InternalCDOObject> dirtyObjects = new HashMap();

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

  @Override
  public boolean isDirty()
  {
    return dirty;
  }

  public List<CDOPackageImpl> getNewPackages()
  {
    return newPackages;
  }

  public Map<CDOID, CDOResourceImpl> getNewResources()
  {
    return newResources;
  }

  public Map<CDOID, InternalCDOObject> getNewObjects()
  {
    return newObjects;
  }

  public Map<CDOID, InternalCDOObject> getDirtyObjects()
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

  public void commit()
  {
    checkWritable();
    if (dirty)
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace("commit()");
      }

      try
      {
        CDOSessionImpl session = getSession();
        CDOPackageManagerImpl packageManager = session.getPackageManager();
        newPackages = packageManager.getTransientPackages();

        preCommit(newObjects);
        preCommit(dirtyObjects);

        IChannel channel = session.getChannel();
        CommitTransactionResult result = new CommitTransactionRequest(channel, this).send(100000L);
        // TODO Change timeout semantics in Net4j

        postCommit(newResources, result);
        postCommit(newObjects, result);
        postCommit(dirtyObjects, result);

        if (!dirtyObjects.isEmpty())
        {
          session.notifyInvalidation(result.getTimeStamp(), dirtyObjects.keySet(), this);
        }

        for (CDOPackageImpl newPackage : newPackages)
        {
          newPackage.setPersistent(true);
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
    if (object instanceof CDOResourceImpl)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Registering new resource {0}", object);
      }

      register(newResources, object);
    }
    else
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Registering new object {0}", object);
      }

      register(newObjects, object);
    }
  }

  public void registerDirty(InternalCDOObject object)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Registering dirty object {0}", object);
    }

    register(dirtyObjects, object);
  }

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
