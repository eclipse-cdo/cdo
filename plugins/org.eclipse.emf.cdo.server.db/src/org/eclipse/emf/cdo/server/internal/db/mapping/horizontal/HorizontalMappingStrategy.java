/*
 * Copyright (c) 2011, 2012, 2015, 2016, 2019, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.db.mapping.horizontal;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStoreAccessor.QueryResourcesContext;
import org.eclipse.emf.cdo.server.IStoreAccessor.QueryXRefsContext;
import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.evolution.phased.Context;
import org.eclipse.emf.cdo.server.db.evolution.phased.ISchemaMigration;
import org.eclipse.emf.cdo.server.db.mapping.IClassMapping;
import org.eclipse.emf.cdo.server.db.mapping.IListMapping;
import org.eclipse.emf.cdo.server.db.mapping.ILobRefsUpdater;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.server.db.mapping.ITypeMapping;
import org.eclipse.emf.cdo.spi.common.commit.CDOChangeSetSegment;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;

import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.util.collection.CloseableIterator;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class HorizontalMappingStrategy extends Lifecycle implements ISchemaMigration, ILobRefsUpdater
{
  private Map<String, String> properties;

  private IDBStore store;

  private IMappingStrategy delegate;

  public HorizontalMappingStrategy()
  {
  }

  @Override
  public IMappingStrategy getDelegate()
  {
    return delegate;
  }

  @Override
  public Map<String, String> getProperties()
  {
    if (delegate != null)
    {
      return delegate.getProperties();
    }

    if (properties != null)
    {
      return properties;
    }

    return new HashMap<>();
  }

  @Override
  public void setProperties(Map<String, String> properties)
  {
    if (delegate != null)
    {
      delegate.setProperties(properties);
    }
    else
    {
      this.properties = properties;
    }
  }

  @Override
  public IDBStore getStore()
  {
    if (delegate != null)
    {
      return delegate.getStore();
    }

    return store;
  }

  @Override
  public void setStore(IDBStore store)
  {
    if (delegate != null)
    {
      delegate.setStore(store);
    }
    else
    {
      this.store = store;
    }
  }

  @Override
  public ITypeMapping createValueMapping(EStructuralFeature feature)
  {
    return delegate.createValueMapping(feature);
  }

  @Override
  public IListMapping createListMapping(EClass containingClass, EStructuralFeature feature)
  {
    return delegate.createListMapping(containingClass, feature);
  }

  @Override
  public String getTableName(ENamedElement element)
  {
    return delegate.getTableName(element);
  }

  @Override
  public String getTableName(EClass containingClass, EStructuralFeature feature)
  {
    return delegate.getTableName(containingClass, feature);
  }

  @Override
  public String getFieldName(EStructuralFeature feature)
  {
    return delegate.getFieldName(feature);
  }

  @Override
  public void createMapping(Connection connection, InternalCDOPackageUnit[] packageUnits, OMMonitor monitor)
  {
    delegate.createMapping(connection, packageUnits, monitor);
  }

  @Override
  public void removeMapping(Connection connection, InternalCDOPackageUnit[] packageUnits)
  {
    delegate.removeMapping(connection, packageUnits);
  }

  @Override
  public IClassMapping getClassMapping(EClass eClass)
  {
    return delegate.getClassMapping(eClass);
  }

  @Override
  public Map<EClass, IClassMapping> getClassMappings()
  {
    return delegate.getClassMappings();
  }

  @Override
  public Map<EClass, IClassMapping> getClassMappings(boolean createOnDemand)
  {
    return delegate.getClassMappings(createOnDemand);
  }

  @Override
  public boolean hasDeltaSupport()
  {
    return delegate.hasDeltaSupport();
  }

  @Override
  public boolean hasAuditSupport()
  {
    return delegate.hasAuditSupport();
  }

  @Override
  public boolean hasBranchingSupport()
  {
    return delegate.hasBranchingSupport();
  }

  @Override
  public void queryResources(IDBStoreAccessor accessor, QueryResourcesContext context)
  {
    delegate.queryResources(accessor, context);
  }

  @Override
  public void queryXRefs(IDBStoreAccessor accessor, QueryXRefsContext context)
  {
    delegate.queryXRefs(accessor, context);
  }

  @Override
  public CDOClassifierRef readObjectType(IDBStoreAccessor accessor, CDOID id)
  {
    return delegate.readObjectType(accessor, id);
  }

  @Override
  public CloseableIterator<CDOID> readObjectIDs(IDBStoreAccessor accessor)
  {
    return delegate.readObjectIDs(accessor);
  }

  @Override
  public void repairAfterCrash(IDBAdapter dbAdapter, Connection connection)
  {
    delegate.repairAfterCrash(dbAdapter, connection);
  }

  @Override
  public void handleRevisions(IDBStoreAccessor accessor, EClass eClass, CDOBranch branch, long timeStamp, boolean exactTime, CDORevisionHandler handler)
  {
    delegate.handleRevisions(accessor, eClass, branch, timeStamp, exactTime, handler);
  }

  @Override
  public Set<CDOID> readChangeSet(IDBStoreAccessor accessor, OMMonitor monitor, CDOChangeSetSegment[] segments)
  {
    return delegate.readChangeSet(accessor, monitor, segments);
  }

  @Override
  public void migrateSchema(Context context, IDBStoreAccessor accessor) throws SQLException
  {
    if (delegate instanceof ISchemaMigration)
    {
      ((ISchemaMigration)delegate).migrateSchema(context, accessor);
    }
    else
    {
      throw new SchemaMigrationNotSupportedException();
    }
  }

  @Override
  public void updateLobRefs(Connection connection)
  {
    if (delegate instanceof ILobRefsUpdater)
    {
      ((ILobRefsUpdater)delegate).updateLobRefs(connection);
    }
    else
    {
      throw new LobRefsUpdateNotSupportedException();
    }
  }

  @Override
  public void rawExport(IDBStoreAccessor accessor, CDODataOutput out, int lastReplicatedBranchID, int lastBranchID, long lastReplicatedCommitTime,
      long lastCommitTime) throws IOException
  {
    delegate.rawExport(accessor, out, lastReplicatedBranchID, lastBranchID, lastReplicatedCommitTime, lastCommitTime);
  }

  @Override
  public void rawImport(IDBStoreAccessor accessor, CDODataInput in, long fromCommitTime, long toCommitTime, OMMonitor monitor) throws IOException
  {
    delegate.rawImport(accessor, in, fromCommitTime, toCommitTime, monitor);
  }

  @Override
  public String getListJoin(String attrTable, String listTable)
  {
    return delegate.getListJoin(attrTable, listTable);
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();

    boolean auditing = getBooleanProperty(IRepository.Props.SUPPORTING_AUDITS);
    boolean branching = getBooleanProperty(IRepository.Props.SUPPORTING_BRANCHES);

    boolean withRanges = false;
    if (auditing || branching)
    {
      withRanges = getBooleanProperty(CDODBUtil.PROP_WITH_RANGES);
    }

    delegate = CDODBUtil.createHorizontalMappingStrategy(auditing, branching, withRanges);
    delegate.setStore(store);
    delegate.setProperties(properties);
    LifecycleUtil.activate(delegate);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    LifecycleUtil.deactivate(delegate);
    super.doDeactivate();
  }

  private boolean getBooleanProperty(String prop)
  {
    String valueAudits = properties.get(prop);
    if (valueAudits != null)
    {
      return Boolean.valueOf(valueAudits);
    }

    return false;
  }
}
