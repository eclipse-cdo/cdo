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
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.util.ImplementationError;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.internal.cdo.bundle.CDO;
import org.eclipse.emf.internal.cdo.protocol.CommitTransactionRequest;
import org.eclipse.emf.internal.cdo.protocol.CommitTransactionResult;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class CDOTransactionImpl
{
  private static final ContextTracer TRACER = new ContextTracer(CDO.DEBUG_TRANSCTION, CDOTransactionImpl.class);

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

  public void commit()
  {
    if (!dirty)
    {
      return;
    }

    if (TRACER.isEnabled())
    {
      TRACER.trace("commit()");
    }

    try
    {
      CDOSessionImpl session = view.getSession();
      CommitTransactionRequest signal = new CommitTransactionRequest(session.getChannel(), this);
      CommitTransactionResult result = signal.send();

      postCommit(newResources, result);
      postCommit(newObjects, result);
      postCommit(dirtyObjects, result);
      session.notifyInvalidation(result.getTimeStamp(), dirtyObjects.keySet(), view);

      newResources.clear();
      newObjects.clear();
      dirtyObjects.clear();
      dirty = false;
      nextTemporaryID = INITIAL_TEMPORARY_ID;
    }
    catch (RuntimeException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw new RuntimeException(ex);
    }
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

    dirty = true;
  }

  private void postCommit(Map objects, CommitTransactionResult result)
  {
    for (Object object : objects.values())
    {
      CDOStateMachine.INSTANCE.commit((CDOObjectImpl)object, result);
    }
  }
}
