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
import org.eclipse.emf.cdo.internal.protocol.CDOIDRangeImpl;
import org.eclipse.emf.cdo.internal.server.store.Store;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.CDOIDRange;
import org.eclipse.emf.cdo.server.IRepository;

import org.eclipse.net4j.internal.util.lifecycle.Lifecycle;

import java.util.UUID;

/**
 * @author Eike Stepper
 */
public class Repository extends Lifecycle implements IRepository
{
  private static final long INITIAL_OID_VALUE = 2;

  private static final long INITIAL_META_ID_VALUE = 1;

  private String name;

  private Store store;

  private String uuid;

  private RepositoryPackageManager packageManager;

  private SessionManager sessionManager;

  private ResourceManager resourceManager;

  private RevisionManager revisionManager;

  private long nextOIDValue = INITIAL_OID_VALUE;

  private long nextMetaIDValue = INITIAL_META_ID_VALUE;

  public Repository(String name, Store store)
  {
    this.name = name;
    this.store = store;
    this.uuid = UUID.randomUUID().toString();

    packageManager = new RepositoryPackageManager(this);
    sessionManager = new SessionManager(this);
    resourceManager = new ResourceManager(this);
    revisionManager = new RevisionManager(this);

    store.setRepository(this);
  }

  public String getName()
  {
    return name;
  }

  public Store getStore()
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

  public CDOIDRange getMetaIDRange(long count)
  {
    long lowerBound = nextMetaIDValue;
    nextMetaIDValue += count;
    nextMetaIDValue += count;
    return CDOIDRangeImpl.create(lowerBound, nextMetaIDValue - 2);
  }

  public CDOID getNextCDOID()
  {
    CDOID id = CDOIDImpl.create(nextOIDValue);
    ++nextOIDValue;
    ++nextOIDValue;
    return id;
  }
}
