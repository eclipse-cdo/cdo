/*
 * Copyright (c) 2012, 2013, 2015, 2016, 2019, 2021, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.admin;

import org.eclipse.emf.cdo.admin.CDOAdminClient;
import org.eclipse.emf.cdo.admin.CDOAdminClientRepository;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOID.ObjectType;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.common.util.RepositoryStateChangedEvent;
import org.eclipse.emf.cdo.common.util.RepositoryTypeChangedEvent;
import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.net4j.CDONet4jSessionConfiguration;
import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.spi.common.protocol.CDODataInputImpl;

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.util.AdapterUtil;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.event.Notifier;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.om.monitor.NotifyingMonitor;

import org.eclipse.core.runtime.IProgressMonitor;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class CDOAdminClientRepositoryImpl extends Notifier implements CDOAdminClientRepository
{
  private CDOAdminClientImpl admin;

  private String name;

  private String uuid;

  private Type type;

  private State state;

  private String storeType;

  private Set<ObjectType> objectIDTypes;

  private long creationTime;

  private CDOID rootResourceID;

  private boolean authenticating;

  private boolean supportingLoginPeeks;

  private boolean supportingAudits;

  private boolean supportingBranches;

  private boolean supportingUnits;

  private boolean serializingCommits;

  private boolean ensuringReferentialIntegrity;

  private boolean authorizingOperations;

  private IDGenerationLocation idGenerationLocation;

  private CommitInfoStorage commitInfoStorage;

  public CDOAdminClientRepositoryImpl(CDOAdminClientImpl admin, ExtendedDataInputStream in) throws IOException
  {
    this.admin = admin;
    name = in.readString();
    uuid = in.readString();
    type = in.readEnum(Type.class);
    state = in.readEnum(State.class);
    storeType = in.readString();

    Set<CDOID.ObjectType> objectIDTypes = new HashSet<>();
    int types = in.readInt();
    for (int i = 0; i < types; i++)
    {
      CDOID.ObjectType objectIDType = in.readEnum(CDOID.ObjectType.class);
      objectIDTypes.add(objectIDType);
    }

    creationTime = in.readLong();

    CDODataInputImpl.Default wrapper = new CDODataInputImpl.Default(in);
    rootResourceID = wrapper.readCDOID();
    if (ObjectUtil.never())
    {
      // Suppress resource leak warning.
      wrapper.close();
    }

    authenticating = in.readBoolean();
    supportingLoginPeeks = in.readBoolean();
    supportingAudits = in.readBoolean();
    supportingBranches = in.readBoolean();
    supportingUnits = in.readBoolean();
    serializingCommits = in.readBoolean();
    ensuringReferentialIntegrity = in.readBoolean();
    authorizingOperations = in.readBoolean();
    idGenerationLocation = in.readEnum(IDGenerationLocation.class);
    commitInfoStorage = in.readEnum(CommitInfoStorage.class);
  }

  @Override
  public CDOAdminClient getAdmin()
  {
    return admin;
  }

  @Override
  public String getName()
  {
    return name;
  }

  @Override
  public String getUUID()
  {
    return uuid;
  }

  @Override
  public Type getType()
  {
    return type;
  }

  @Override
  public State getState()
  {
    return state;
  }

  @Override
  public String getStoreType()
  {
    return storeType;
  }

  @Override
  public Set<ObjectType> getObjectIDTypes()
  {
    return objectIDTypes;
  }

  @Override
  public long getCreationTime()
  {
    return creationTime;
  }

  @Override
  public CDOID getRootResourceID()
  {
    return rootResourceID;
  }

  @Override
  public boolean isAuthenticating()
  {
    return authenticating;
  }

  @Override
  public boolean isSupportingLoginPeeks()
  {
    return supportingLoginPeeks;
  }

  @Override
  public boolean isSupportingAudits()
  {
    return supportingAudits;
  }

  @Override
  public boolean isSupportingBranches()
  {
    return supportingBranches;
  }

  @Override
  public boolean isSupportingUnits()
  {
    return supportingUnits;
  }

  @Override
  @Deprecated
  public boolean isSupportingEcore()
  {
    return true;
  }

  @Override
  public boolean isSerializingCommits()
  {
    return serializingCommits;
  }

  @Override
  public boolean isEnsuringReferentialIntegrity()
  {
    return ensuringReferentialIntegrity;
  }

  @Override
  public boolean isAuthorizingOperations()
  {
    return authorizingOperations;
  }

  @Override
  public IDGenerationLocation getIDGenerationLocation()
  {
    return idGenerationLocation;
  }

  @Override
  public CommitInfoStorage getCommitInfoStorage()
  {
    return commitInfoStorage;
  }

  @Override
  public boolean waitWhileInitial(IProgressMonitor monitor)
  {
    return CDOCommonUtil.waitWhileInitial(this, this, monitor);
  }

  @Override
  public long getTimeStamp() throws UnsupportedOperationException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean delete(String type)
  {
    return admin.deleteRepository(this, type);
  }

  @Override
  public CDONet4jSession openSession()
  {
    return openSession(null);
  }

  @Override
  public CDONet4jSession openSession(SessionConfigurator configurator)
  {
    IConnector connector = admin.getConnector();
    if (connector == null)
    {
      return null;
    }

    CDONet4jSessionConfiguration configuration = CDONet4jUtil.createNet4jSessionConfiguration();
    configuration.setConnector(connector);
    configuration.setRepositoryName(name);

    if (configurator != null)
    {
      configurator.prepare(configuration);
    }

    return configuration.openNet4jSession();
  }

  @Override
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public Object getAdapter(Class adapter)
  {
    return AdapterUtil.adapt(this, adapter, false);
  }

  @Override
  public String toString()
  {
    return name;
  }

  public void typeChanged(Type oldType, Type newType)
  {
    type = newType;
    fireEvent(new RepositoryTypeChangedEvent(this, oldType, newType));
  }

  public void stateChanged(State oldState, State newState)
  {
    state = newState;
    fireEvent(new RepositoryStateChangedEvent(this, oldState, newState));
  }

  public void replicationProgressed(double totalWork, double work)
  {
    fireEvent(new NotifyingMonitor.ProgressEvent(this, totalWork, work));
  }
}
