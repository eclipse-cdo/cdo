/*
 * Copyright (c) 2010-2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ibrahim Sallam - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.objectivity.db;

import org.eclipse.emf.cdo.server.internal.objectivity.bundle.OM;
import org.eclipse.emf.cdo.server.internal.objectivity.schema.ObjyBranchManager;
import org.eclipse.emf.cdo.server.internal.objectivity.schema.ObjyLockAreaManager;
import org.eclipse.emf.cdo.server.internal.objectivity.schema.ObjyResourceList;
import org.eclipse.emf.cdo.server.internal.objectivity.utils.ObjyDb;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import com.objy.db.CannotUpgradeLockException;
import com.objy.db.app.Session;
import com.objy.db.app.oo;
import com.objy.db.app.ooContObj;
import com.objy.db.app.ooId;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/* 
 * @author ibrahim
 */
public class ObjySession extends Session
{
  private static final ContextTracer TRACER_INFO = new ContextTracer(OM.INFO, ObjySession.class);

  private ObjyObjectManager objectManger = null;

  private ObjyResourceList resourceList = null;

  private ObjyBranchManager branchManager = null;

  private ObjyLockAreaManager lockAreaManager = null;

  protected String sessionName;

  protected ConcurrentHashMap<String, ObjySession> sessionPool;

  protected boolean available;

  public ObjySession(String name, ConcurrentHashMap<String, ObjySession> pool, ObjyConnection objyConnection)
  {
    super(objyConnection.getMinSessionCacheSize(), objyConnection.getMaxSessionCacheSize());
    setThreadPolicy(oo.THREAD_POLICY_UNRESTRICTED);
    // setHotMode(false);
    setIndexMode(oo.EXPLICIT_UPDATE);
    sessionName = name;
    sessionPool = pool;
    objectManger = new ObjyObjectManager(objyConnection.getDefaultPlacementManager());
  }

  public ObjyObjectManager getObjectManager()
  {
    return objectManger;
  }

  public ObjyBranchManager getBranchManager(String repositoryName)
  {
    if (branchManager == null)
    {
      branchManager = ObjyDb.getOrCreateBranchManager(repositoryName);
    }
    return branchManager;
  }

  public ObjyLockAreaManager getLockAreaManager(String repositoryName)
  {
    if (lockAreaManager == null)
    {
      lockAreaManager = ObjyDb.getOrCreateLockAreaManager(repositoryName);
    }
    return lockAreaManager;
  }

  public void setAvailable(boolean value)
  {
    available = value;
  }

  public boolean isAvailable()
  {
    return available;
  }

  public String getName()
  {
    return sessionName;
  }

  public void setName(String name)
  {
    sessionName = name;
  }

  public ConcurrentHashMap<String, ObjySession> getPool()
  {
    return sessionPool;
  }

  public ObjyResourceList getResourceList(String repositoryName)
  {
    if (resourceList == null)
    {
      resourceList = new ObjyResourceList(this, ObjyDb.getOrCreateResourceList(repositoryName));
    }
    return resourceList;
  }

  @Override
  public synchronized void returnSessionToPool()
  {
    // System.out.println(">>> IS: returning session: " + session.getName());
    leave();
    setAvailable(true);
  }

  @Override
  public synchronized void terminate()
  {
    // System.out.println("OBJY>>> Terminating session... " + sessionName + " - " + toString());
    resourceList = null;
    super.terminate();
  }

  public void lockContainers(Set<ooId> containerToLocks)
  {
    // Locks all containers for modified objects
    if (!containerToLocks.isEmpty())
    {
      ooId idsToLock[] = containerToLocks.toArray(new ooId[containerToLocks.size()]);
      // 100920 - IS: for debugging... TBR.
      // for (ooId id : idsToLock)
      // {
      // TRACER_INFO.trace("Locking container: " + id.getStoreString());
      // }

      int count = 10;
      while (0 != count--)
      {
        try
        {
          openContainers(idsToLock, oo.openReadWrite);
          break;
        }
        catch (CannotUpgradeLockException cule)
        {
          // refresh containers.
          for (ooId contId : idsToLock)
          {
            ooContObj contObj = (ooContObj)getFD().objectFrom(contId);
            contObj.refresh(oo.WRITE);
          }
        }
        catch (Exception e)
        {
          TRACER_INFO.trace("Locking problem try again : " + e.getMessage());
          // this.ensureNewBeginSession();
          if (!isOpen())
          {
            TRACER_INFO.trace("Objy session is not open");
          }
          try
          {
            wait(500);
          }
          catch (InterruptedException ex)
          {
          }
        }
      }
    }
  }

}
