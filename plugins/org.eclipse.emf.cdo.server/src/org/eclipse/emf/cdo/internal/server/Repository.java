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
import org.eclipse.emf.cdo.internal.protocol.model.CDOClassRefImpl;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.CDOIDRange;
import org.eclipse.emf.cdo.protocol.model.CDOClassRef;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.IStoreReader;

import org.eclipse.net4j.internal.util.container.Container;
import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import java.text.MessageFormat;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Eike Stepper
 */
public class Repository extends Container implements IRepository
{
  private static final long INITIAL_OID_VALUE = 2;

  private static final long INITIAL_META_ID_VALUE = 1;

  private String name;

  private IStore store;

  private String uuid;

  private PackageManager packageManager = new PackageManager(this);

  private SessionManager sessionManager = new SessionManager(this);

  private ResourceManager resourceManager = new ResourceManager(this);

  private RevisionManager revisionManager = new RevisionManager(this);

  private Object[] elements = { packageManager, sessionManager, resourceManager, revisionManager };

  private long nextOIDValue = INITIAL_OID_VALUE;

  private long nextMetaIDValue = INITIAL_META_ID_VALUE;

  private ConcurrentMap<CDOID, CDOClassRef> objectTypes = new ConcurrentHashMap();

  public Repository(String name)
  {
    this.name = name;
    uuid = UUID.randomUUID().toString();
  }

  public String getName()
  {
    return name;
  }

  public String getUUID()
  {
    return uuid;
  }

  public IStore getStore()
  {
    return store;
  }

  public void setStore(IStore store)
  {
    this.store = store;
  }

  public PackageManager getPackageManager()
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

  public Object[] getElements()
  {
    return elements;
  }

  @Override
  public boolean isEmpty()
  {
    return false;
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

  public CDOClassRef getObjectType(IStoreReader storeReader, CDOID id)
  {
    CDOClassRef type = objectTypes.get(id);
    if (type == null)
    {
      type = storeReader.readObjectType(id);
      if (type == null)
      {
        throw new ImplementationError("type == null");
      }

      objectTypes.put(id, type);
    }

    return type;
  }

  public void registerObjectType(CDOID id, CDOClassRefImpl type)
  {
    objectTypes.putIfAbsent(id, type);
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("Repository[{0}, {1}]", name, uuid);
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    if (store == null)
    {
      throw new IllegalStateException("No store for repository " + name);
    }

    if (!LifecycleUtil.isActive(store))
    {
      throw new IllegalStateException("Inactive store for repository " + name);
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    packageManager.activate();
    sessionManager.activate();
    resourceManager.activate();
    revisionManager.activate();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    revisionManager.deactivate();
    resourceManager.deactivate();
    sessionManager.deactivate();
    packageManager.deactivate();
    super.doDeactivate();
  }
}
