/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.db.mapping.horizontal;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IObjectTypeMapper;

import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.emf.ecore.EClass;

import java.io.IOException;
import java.sql.Connection;

/**
 * @author Eike Stepper
 * @since 4.0
 */
public abstract class DelegatingObjectTypeMapper extends AbstractObjectTypeMapper
{
  public static final int DEFAULT_CACHE_CAPACITY = 10000000;

  private IObjectTypeMapper delegate;

  public DelegatingObjectTypeMapper()
  {
  }

  public IObjectTypeMapper getDelegate()
  {
    return delegate;
  }

  public void setDelegate(IObjectTypeMapper delegate)
  {
    this.delegate = delegate;
  }

  public CDOClassifierRef getObjectType(IDBStoreAccessor accessor, CDOID id)
  {
    long longId = CDOIDUtil.getLong(id);
    Long type = doGetObjectType(longId);
    if (type != null)
    {
      long classID = type;
      EClass eClass = (EClass)getMetaDataManager().getMetaInstance(accessor, classID);
      return new CDOClassifierRef(eClass);
    }

    return delegate.getObjectType(accessor, id);
  }

  public void putObjectType(IDBStoreAccessor accessor, long timeStamp, CDOID id, EClass type)
  {
    long longId = CDOIDUtil.getLong(id);
    long classID = getMetaDataManager().getMetaID(accessor, type, timeStamp);
    doPutObjectType(longId, classID);

    delegate.putObjectType(accessor, timeStamp, id, type);
  }

  public void removeObjectType(IDBStoreAccessor accessor, CDOID id)
  {
    long longId = CDOIDUtil.getLong(id);
    doRemoveObjectType(longId);

    delegate.removeObjectType(accessor, id);
  }

  public long getMaxID(Connection connection)
  {
    Long maxID = doGetMaxID();
    if (maxID != null)
    {
      return maxID;
    }

    return delegate.getMaxID(connection);
  }

  public void rawExport(Connection connection, CDODataOutput out, long fromCommitTime, long toCommitTime)
      throws IOException
  {
    delegate.rawExport(connection, out, fromCommitTime, toCommitTime);
  }

  public void rawImport(Connection connection, CDODataInput in, OMMonitor monitor) throws IOException
  {
    delegate.rawImport(connection, in, monitor);
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    checkState(delegate, "delegate");
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    LifecycleUtil.activate(delegate);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    LifecycleUtil.deactivate(delegate);
    super.doDeactivate();
  }

  protected abstract Long doGetObjectType(long id);

  protected abstract void doPutObjectType(long id, long type);

  protected abstract void doRemoveObjectType(long id);

  protected abstract Long doGetMaxID();
}
