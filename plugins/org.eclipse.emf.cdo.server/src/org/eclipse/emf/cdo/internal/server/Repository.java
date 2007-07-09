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
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.internal.protocol.CDOIDImpl;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.ITransaction;

import org.eclipse.net4j.internal.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.store.IStoreManager;

import java.util.UUID;

/**
 * @author Eike Stepper
 */
public class Repository extends Lifecycle implements IRepository
{
  private static final long INITIAL_OID_VALUE = 1;

  private String name;

  private String uuid;

  private SessionManager sessionManager;

  private ResourceManager resourceManager;

  private RevisionManager revisionManager;

  private IStoreManager<ITransaction> storeManager;

  private long nextOIDValue = INITIAL_OID_VALUE;

  public Repository(String name, IStoreManager<ITransaction> storeManager)
  {
    this.name = name;
    this.uuid = UUID.randomUUID().toString();
    sessionManager = new SessionManager(this);
    resourceManager = new ResourceManager(this);
    revisionManager = new RevisionManager(this);
    this.storeManager = storeManager;
  }

  public String getName()
  {
    return name;
  }

  public String getUUID()
  {
    return uuid;
  }

  public SessionManager getSessionManager()
  {
    return sessionManager;
  }

  public ResourceManager getResourceManager()
  {
    return resourceManager;
  }

  public RevisionManager getRevisionManager()
  {
    return revisionManager;
  }

  public IStoreManager<ITransaction> getStoreManager()
  {
    return storeManager;
  }

  public CDOID getNextCDOID()
  {
    return CDOIDImpl.create(nextOIDValue++);
  }
}
