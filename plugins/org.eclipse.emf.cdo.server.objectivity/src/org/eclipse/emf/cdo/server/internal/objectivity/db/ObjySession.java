/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Ibrahim Sallam - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.objectivity.db;

import org.eclipse.emf.cdo.internal.server.Session;
import org.eclipse.emf.cdo.server.internal.objectivity.ObjectivityStore;
import org.eclipse.emf.cdo.server.internal.objectivity.clustering.ObjyPlacementManager;
import org.eclipse.emf.cdo.server.internal.objectivity.schema.OoResourceList;

import java.util.concurrent.ConcurrentHashMap;

/***
 * Extends the session and keep an object manager instance.
 * 
 * @author ibrahim
 */
public class ObjySession extends Session
{
  private ObjyObjectManager objectManger = null;

  private OoResourceList resourceList;

  private ObjectivityStore store = null;

  protected String sessionName;

  protected ConcurrentHashMap<String, ObjySession> sessionPool;

  protected boolean available;

  public ObjySession(String name, ConcurrentHashMap<String, ObjySession> pool, ObjectivityStore store)
  {
    super(600, 1000);
    setThreadPolicy(oo.THREAD_POLICY_UNRESTRICTED);
    // for 10.0 -> setThreadPolicy(oo.THREAD_POLICY_CONCURRENT); // this is a workaround for issue with deaklocking
    // threads
    sessionName = name;
    sessionPool = pool;
    this.store = store;
    objectManger = new ObjyObjectManager(store.getPlacementManager());
  }

  public ObjyObjectManager getObjectManager()
  {
    return objectManger;
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

  public OoResourceList getResourceList()
  {
    if (resourceList == null)
    {
      resourceList = new OoResourceList(this, store.getResourceList());
    }
    return resourceList;
  }

  public ObjyPlacementManager getGlobalPlacementManager()
  {
    return store.getPlacementManager();
  }

  public void terminate()
  {
    // System.out.println("OBJY>>> Terminating session... " + this.toString());
    resourceList = null;
    super.terminate();
  }
}
