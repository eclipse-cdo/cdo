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
import org.eclipse.emf.cdo.server.IStore;

import org.eclipse.net4j.internal.util.lifecycle.Lifecycle;

import java.util.UUID;

/**
 * @author Eike Stepper
 */
public class Repository extends Lifecycle implements IRepository
{
  private static final long INITIAL_OID_VALUE = 2;

  private String name;

  private IStore store;

  private String uuid;

  private RepositoryPackageManager packageManager;

  private SessionManager sessionManager;

  private ResourceManager resourceManager;

  private RevisionManager revisionManager;

  private long nextOIDValue = INITIAL_OID_VALUE;

  public Repository(String name, IStore store)
  {
    this.name = name;
    this.store = store;
    this.uuid = UUID.randomUUID().toString();
    packageManager = new RepositoryPackageManager(this);
    sessionManager = new SessionManager(this);
    resourceManager = new ResourceManager(this);
    revisionManager = new RevisionManager(this);
  }

  public String getName()
  {
    return name;
  }

  public IStore getStore()
  {
    return store;
  }

  public String getUUID()
  {
    return uuid;
  }

  public RepositoryPackageManager getPackageManager()
  {
    return packageManager;
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

  public CDOID getNextCDOID()
  {
    CDOID id = CDOIDImpl.create(nextOIDValue);
    ++nextOIDValue;
    ++nextOIDValue;
    return id;
  }
}
