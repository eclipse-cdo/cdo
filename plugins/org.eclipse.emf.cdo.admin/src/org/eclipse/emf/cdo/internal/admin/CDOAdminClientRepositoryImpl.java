/*
 * Copyright (c) 2012, 2013 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.net4j.util.event.Notifier;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.om.monitor.NotifyingMonitor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;

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

  private boolean supportingAudits;

  private boolean supportingBranches;

  private boolean serializingCommits;

  private boolean ensuringReferentialIntegrity;

  private IDGenerationLocation idGenerationLocation;

  public CDOAdminClientRepositoryImpl(CDOAdminClientImpl admin, ExtendedDataInputStream in) throws IOException
  {
    this.admin = admin;
    name = in.readString();
    uuid = in.readString();
    type = in.readEnum(Type.class);
    state = in.readEnum(State.class);
    storeType = in.readString();

    Set<CDOID.ObjectType> objectIDTypes = new HashSet<ObjectType>();
    int types = in.readInt();
    for (int i = 0; i < types; i++)
    {
      CDOID.ObjectType objectIDType = in.readEnum(CDOID.ObjectType.class);
      objectIDTypes.add(objectIDType);
    }

    creationTime = in.readLong();
    rootResourceID = new CDODataInputImpl.Default(in).readCDOID();
    supportingAudits = in.readBoolean();
    supportingBranches = in.readBoolean();
    serializingCommits = in.readBoolean();
    ensuringReferentialIntegrity = in.readBoolean();
    idGenerationLocation = in.readEnum(IDGenerationLocation.class);
  }

  public CDOAdminClient getAdmin()
  {
    return admin;
  }

  public String getName()
  {
    return name;
  }

  public String getUUID()
  {
    return uuid;
  }

  public Type getType()
  {
    return type;
  }

  public State getState()
  {
    return state;
  }

  public String getStoreType()
  {
    return storeType;
  }

  public Set<ObjectType> getObjectIDTypes()
  {
    return objectIDTypes;
  }

  public long getCreationTime()
  {
    return creationTime;
  }

  public CDOID getRootResourceID()
  {
    return rootResourceID;
  }

  public boolean isSupportingAudits()
  {
    return supportingAudits;
  }

  public boolean isSupportingBranches()
  {
    return supportingBranches;
  }

  @Deprecated
  public boolean isSupportingEcore()
  {
    return true;
  }

  public boolean isSerializingCommits()
  {
    return serializingCommits;
  }

  public boolean isEnsuringReferentialIntegrity()
  {
    return ensuringReferentialIntegrity;
  }

  public IDGenerationLocation getIDGenerationLocation()
  {
    return idGenerationLocation;
  }

  public boolean waitWhileInitial(IProgressMonitor monitor)
  {
    return CDOCommonUtil.waitWhileInitial(this, this, monitor);
  }

  public long getTimeStamp() throws UnsupportedOperationException
  {
    throw new UnsupportedOperationException();
  }

  public boolean delete(String type)
  {
    return admin.deleteRepository(this, type);
  }

  public CDONet4jSession openSession()
  {
    return openSession(null);
  }

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

  public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter)
  {
    return Platform.getAdapterManager().getAdapter(this, adapter);
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
