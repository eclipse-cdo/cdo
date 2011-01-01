/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ibrahim Sallam - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.objectivity.db;

import org.eclipse.emf.cdo.server.internal.objectivity.schema.ObjyBranchManager;
import org.eclipse.emf.cdo.server.internal.objectivity.schema.ObjyResourceList;
import org.eclipse.emf.cdo.server.internal.objectivity.utils.ObjyDb;

import com.objy.db.app.Session;
import com.objy.db.app.oo;

import java.util.concurrent.ConcurrentHashMap;

/***
 * Extends the session and keep an object manager instance.
 * 
 * @author ibrahim
 */
public class ObjySession extends Session
{
  private ObjyObjectManager objectManger = null;

  private ObjyResourceList resourceList = null;

  private ObjyBranchManager branchManager = null;

  // private ObjectivityStore store = null;

  protected String sessionName;

  protected ConcurrentHashMap<String, ObjySession> sessionPool;

  protected boolean available;

  public ObjySession(String name, ConcurrentHashMap<String, ObjySession> pool, ObjyConnection objyConnection)
  {
    super(objyConnection.getMinSessionCacheSize(), objyConnection.getMaxSessionCacheSize());
    setThreadPolicy(oo.THREAD_POLICY_UNRESTRICTED);
    sessionName = name;
    sessionPool = pool;
    // this.store = store;
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
  public synchronized void terminate()
  {
    // System.out.println("OBJY>>> Terminating session... " + this.toString());
    resourceList = null;
    super.terminate();
  }
}
