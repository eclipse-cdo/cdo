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

import org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl;
import org.eclipse.emf.cdo.internal.protocol.model.CDOPackageImpl;
import org.eclipse.emf.cdo.internal.protocol.model.CDOPackageManagerImpl;
import org.eclipse.emf.cdo.internal.protocol.model.core.CDOCorePackageImpl;
import org.eclipse.emf.cdo.internal.protocol.model.resource.CDOResourcePackageImpl;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.util.ImplementationError;

import org.eclipse.net4j.IChannel;
import org.eclipse.net4j.internal.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.protocol.CommitTransactionRequest;
import org.eclipse.emf.internal.cdo.protocol.CommitTransactionResult;
import org.eclipse.emf.internal.cdo.protocol.RegisterPackagesRequest;
import org.eclipse.emf.internal.cdo.util.EMFUtil;
import org.eclipse.emf.internal.cdo.util.PackageClosure;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class CDOTransactionImpl
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_TRANSCTION, CDOTransactionImpl.class);

  private static final long INITIAL_TEMPORARY_ID = -1L;

  private long nextTemporaryID = INITIAL_TEMPORARY_ID;

  private CDOViewImpl view;

  private Map<CDOID, CDOResourceImpl> newResources = new HashMap();

  private Map<CDOID, CDOObjectImpl> newObjects = new HashMap();

  private Map<CDOID, CDOObjectImpl> dirtyObjects = new HashMap();

  private boolean dirty;

  public CDOTransactionImpl(CDOViewImpl view)
  {
    this.view = view;
  }

  public CDOViewImpl getView()
  {
    return view;
  }

  public boolean isDirty()
  {
    return dirty;
  }

  public Map<CDOID, CDOResourceImpl> getNewResources()
  {
    return newResources;
  }

  public Map<CDOID, CDOObjectImpl> getNewObjects()
  {
    return newObjects;
  }

  public Map<CDOID, CDOObjectImpl> getDirtyObjects()
  {
    return dirtyObjects;
  }

  public long getNextTemporaryID()
  {
    return nextTemporaryID--;
  }

  public void resetTemporaryCDOID()
  {
    nextTemporaryID = INITIAL_TEMPORARY_ID;
  }

  /**
   * @return
   * @return A set of the ids that have changed due to object creation
   */
  public CommitTransactionResult commit()
  {
    if (dirty)
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace("commit()");
      }

      try
      {
        CDOSessionImpl session = view.getSession();
        IChannel channel = session.getChannel();

        new RegisterPackagesRequest(channel, calculateNewPackages()).send();

        CommitTransactionResult result = new CommitTransactionRequest(channel, this).send();
        postCommit(newResources, result);
        postCommit(newObjects, result);
        postCommit(dirtyObjects, result);

        if (!dirtyObjects.isEmpty())
        {
          session.notifyInvalidation(result.getTimeStamp(), dirtyObjects.keySet(), view);
        }

        newResources.clear();
        newObjects.clear();
        dirtyObjects.clear();
        dirty = false;
        nextTemporaryID = INITIAL_TEMPORARY_ID;
        return result;
      }
      catch (RuntimeException ex)
      {
        throw ex;
      }
      catch (Exception ex)
      {
        // TODO Better exception handling
        throw new RuntimeException(ex);
      }
    }

    return null;
  }

  public void rollback()
  {
    // TODO Implement method CDOTransactionImpl.rollback()
    throw new UnsupportedOperationException("Not yet implemented");
  }

  public void registerNew(CDOObjectImpl object)
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

  public void registerDirty(CDOObjectImpl object)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Registering dirty object {0}", object);
    }

    register(dirtyObjects, object);
  }

  private void register(Map map, CDOObjectImpl object)
  {
    Object old = map.put(object.cdoID(), object);
    if (old != null)
    {
      throw new ImplementationError("Duplicate ID: " + object);
    }

    if (!dirty)
    {
      dirty = true;
      view.fireDirtyEvent();
    }
  }

  private void postCommit(Map objects, CommitTransactionResult result)
  {
    if (!objects.isEmpty())
    {
      for (Object object : objects.values())
      {
        CDOStateMachine.INSTANCE.commit((CDOObjectImpl)object, result);
      }
    }
  }

  private Collection<CDOPackageImpl> calculateNewPackages()
  {
    CDOSessionImpl session = view.getSession();
    CDOPackageManagerImpl packageManager = session.getPackageManager();
    CDOCorePackageImpl corePackage = packageManager.getCDOCorePackage();
    CDOResourcePackageImpl resourcePackage = packageManager.getCDOResourcePackage();

    Set<String> knownPackages = session.getPackageURIs();
    Map<String, CDOPackageImpl> newPackages = new HashMap();
    for (CDOObjectImpl cdoObject : newObjects.values())
    {
      CDOPackageImpl cdoPackage = cdoObject.cdoClass().getContainingPackage();
      String uri = cdoPackage.getPackageURI();
      if (!newPackages.containsKey(uri) && !knownPackages.contains(uri))
      {
        EPackage ePackage = EMFUtil.getEPackage(cdoPackage);
        Set<EPackage> ePackages = PackageClosure.calculate(ePackage);
        for (EPackage eP : ePackages)
        {
          CDOPackageImpl cdoP = eP == ePackage ? cdoPackage : EMFUtil.getCDOPackage(eP, packageManager);
          if (cdoP == null)
          {
            throw new IllegalStateException("Not a CDO package: " + eP);
          }

          if (cdoP != corePackage && cdoP != resourcePackage)
          {
            uri = cdoP.getPackageURI();
            if (!newPackages.containsKey(uri) && !knownPackages.contains(uri))
            {
              newPackages.put(uri, cdoP);
            }
          }
        }
      }
    }

    return newPackages.values();
  }
}
