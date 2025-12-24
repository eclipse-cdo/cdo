/*
 * Copyright (c) 2012, 2013, 2015, 2016, 2019, 2021, 2023, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.admin;

import org.eclipse.emf.cdo.common.admin.CDOAdmin;
import org.eclipse.emf.cdo.common.admin.CDOAdminRepository;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOID.ObjectType;
import org.eclipse.emf.cdo.common.util.RepositoryStateChangedEvent;
import org.eclipse.emf.cdo.common.util.RepositoryTypeChangedEvent;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IRepositorySynchronizer;
import org.eclipse.emf.cdo.server.ISynchronizableRepository;
import org.eclipse.emf.cdo.spi.common.protocol.CDODataOutputImpl;

import org.eclipse.net4j.util.AdapterUtil;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.event.Notifier;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.om.monitor.NotifyingMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitorProgress;

import org.eclipse.core.runtime.IProgressMonitor;

import java.io.IOException;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class CDOAdminServerRepository extends Notifier implements CDOAdminRepository
{
  private final CDOAdminServer admin;

  private final IRepository delegate;

  private IListener delegateListener = new IListener()
  {
    @Override
    public void notifyEvent(IEvent event)
    {
      if (event instanceof TypeChangedEvent)
      {
        TypeChangedEvent e = (TypeChangedEvent)event;
        Type oldType = e.getOldType();
        Type newType = e.getNewType();

        fireEvent(new RepositoryTypeChangedEvent(CDOAdminServerRepository.this, oldType, newType));
        admin.repositoryTypeChanged(getName(), oldType, newType);
      }
      else if (event instanceof StateChangedEvent)
      {
        StateChangedEvent e = (StateChangedEvent)event;
        State oldState = e.getOldState();
        State newState = e.getNewState();

        fireEvent(new RepositoryStateChangedEvent(CDOAdminServerRepository.this, oldState, newState));
        admin.repositoryStateChanged(getName(), oldState, newState);
      }
    }
  };

  private IListener delegateSynchronizerListener = new IListener()
  {
    @Override
    public void notifyEvent(IEvent event)
    {
      if (event instanceof OMMonitorProgress)
      {
        OMMonitorProgress e = (OMMonitorProgress)event;
        double totalWork = e.getTotalWork();
        double work = e.getWork();

        fireEvent(new NotifyingMonitor.ProgressEvent(CDOAdminServerRepository.this, totalWork, work));
        admin.repositoryReplicationProgressed(getName(), totalWork, work);
      }
    }
  };

  public CDOAdminServerRepository(CDOAdminServer admin, IRepository delegate)
  {
    this.admin = admin;
    this.delegate = delegate;

    delegate.addListener(delegateListener);
    if (delegate instanceof ISynchronizableRepository)
    {
      IRepositorySynchronizer synchronizer = ((ISynchronizableRepository)delegate).getSynchronizer();
      synchronizer.addListener(delegateSynchronizerListener);
    }
  }

  @Override
  public final CDOAdmin getAdmin()
  {
    return admin;
  }

  public final IRepository getDelegate()
  {
    return delegate;
  }

  @Override
  public boolean delete(String type)
  {
    return admin.deleteRepository(this, type);
  }

  @Override
  public String getName()
  {
    return delegate.getName();
  }

  @Override
  public String getUUID()
  {
    return delegate.getUUID();
  }

  @Override
  public Type getType()
  {
    return delegate.getType();
  }

  @Override
  public State getState()
  {
    return delegate.getState();
  }

  @Override
  public String getStoreType()
  {
    return delegate.getStoreType();
  }

  @Override
  public Set<ObjectType> getObjectIDTypes()
  {
    return delegate.getObjectIDTypes();
  }

  @Override
  public long getCreationTime()
  {
    return delegate.getCreationTime();
  }

  @Override
  public CDOID getRootResourceID()
  {
    return delegate.getRootResourceID();
  }

  @Override
  public boolean isAuthenticating()
  {
    return delegate.isAuthenticating();
  }

  @Override
  public boolean isSupportingAudits()
  {
    return delegate.isSupportingAudits();
  }

  @Override
  public boolean isSupportingLoginPeeks()
  {
    return delegate.isSupportingLoginPeeks();
  }

  @Override
  public boolean isSupportingBranches()
  {
    return delegate.isSupportingBranches();
  }

  @Override
  public boolean isSupportingUnits()
  {
    return delegate.isSupportingUnits();
  }

  @Override
  public boolean isSerializingCommits()
  {
    return delegate.isSerializingCommits();
  }

  @Override
  public boolean isEnsuringReferentialIntegrity()
  {
    return delegate.isEnsuringReferentialIntegrity();
  }

  @Override
  public boolean isAuthorizingOperations()
  {
    return delegate.isAuthorizingOperations();
  }

  @Override
  public boolean waitWhileInitial(IProgressMonitor monitor)
  {
    return delegate.waitWhileInitial(monitor);
  }

  @Override
  public IDGenerationLocation getIDGenerationLocation()
  {
    return delegate.getIDGenerationLocation();
  }

  @Override
  public String getLobDigestAlgorithm()
  {
    return delegate.getLobDigestAlgorithm();
  }

  @Override
  public CommitInfoStorage getCommitInfoStorage()
  {
    return delegate.getCommitInfoStorage();
  }

  @Override
  public long getTimeStamp() throws UnsupportedOperationException
  {
    return delegate.getTimeStamp();
  }

  @Override
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public Object getAdapter(Class adapter)
  {
    return AdapterUtil.adapt(this, adapter, false);
  }

  public void write(ExtendedDataOutputStream out) throws IOException
  {
    out.writeString(getName());
    out.writeString(getUUID());
    out.writeEnum(getType());
    out.writeEnum(getState());
    out.writeString(getStoreType());

    Set<CDOID.ObjectType> objectIDTypes = getObjectIDTypes();
    int types = objectIDTypes.size();
    out.writeInt(types);
    for (CDOID.ObjectType objectIDType : objectIDTypes)
    {
      out.writeEnum(objectIDType);
    }

    out.writeLong(getCreationTime());

    CDODataOutputImpl wrapper = new CDODataOutputImpl(out);
    wrapper.writeCDOID(getRootResourceID());
    if (ObjectUtil.never())
    {
      // Suppress resource leak warning.
      wrapper.close();
    }

    out.writeBoolean(isAuthenticating());
    out.writeBoolean(isSupportingLoginPeeks());
    out.writeBoolean(isSupportingAudits());
    out.writeBoolean(isSupportingBranches());
    out.writeBoolean(isSupportingUnits());
    out.writeBoolean(isSerializingCommits());
    out.writeBoolean(isEnsuringReferentialIntegrity());
    out.writeBoolean(isAuthorizingOperations());
    out.writeEnum(getIDGenerationLocation());
    out.writeString(getLobDigestAlgorithm());
    out.writeEnum(getCommitInfoStorage());
  }

  public void dispose()
  {
    delegate.removeListener(delegateListener);
    if (delegate instanceof ISynchronizableRepository)
    {
      IRepositorySynchronizer synchronizer = ((ISynchronizableRepository)delegate).getSynchronizer();
      synchronizer.removeListener(delegateSynchronizerListener);
    }
  }

  @Override
  public String toString()
  {
    return delegate.toString();
  }

  @Override
  @Deprecated
  public boolean isSupportingEcore()
  {
    throw new UnsupportedOperationException();
  }
}
