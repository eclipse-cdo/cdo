/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - major refactoring
 *    Stefan Winkler - 249610: [DB] Support external references (Implementation)
 *    Stefan Winkler - derived branch mapping from audit mapping
 */
package org.eclipse.emf.cdo.server.internal.db.mapping.horizontal;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IPreparedStatementCache.ReuseProbability;
import org.eclipse.emf.cdo.server.db.mapping.IClassMapping;
import org.eclipse.emf.cdo.server.db.mapping.IClassMappingBranchingSupport;
import org.eclipse.emf.cdo.server.db.mapping.IListMapping;
import org.eclipse.emf.cdo.server.db.mapping.ITypeMapping;
import org.eclipse.emf.cdo.server.internal.db.CDODBSchema;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor.Async;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

/**
 * @author Eike Stepper
 * @author Stefan Winkler
 * @since 3.0
 */
public class HorizontalBranchingClassMapping extends AbstractHorizontalClassMapping implements IClassMapping,
    IClassMappingBranchingSupport
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, HorizontalBranchingClassMapping.class);

  private String sqlInsertAttributes;

  private String sqlSelectCurrentAttributes;

  private String sqlSelectAllObjectIds;

  private String sqlSelectAttributesByTime;

  private String sqlSelectAttributesByVersion;

  private String sqlReviseAttributes;

  public HorizontalBranchingClassMapping(AbstractHorizontalMappingStrategy mappingStrategy, EClass eClass)
  {
    super(mappingStrategy, eClass);

    initSqlStrings();
  }

  @Override
  protected IDBField addBranchingField(IDBTable table)
  {
    return table.addField(CDODBSchema.ATTRIBUTES_BRANCH, DBType.INTEGER, true);
  }

  private void initSqlStrings()
  {
    Map<EStructuralFeature, String> unsettableFields = getUnsettableFields();

    // ----------- Select Revision ---------------------------
    StringBuilder builder = new StringBuilder();

    builder.append("SELECT "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_VERSION);
    builder.append(", "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_CREATED);
    builder.append(", "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_REVISED);
    builder.append(", "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_RESOURCE);
    builder.append(", "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_CONTAINER);
    builder.append(", "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_FEATURE);

    for (ITypeMapping singleMapping : getValueMappings())
    {
      builder.append(", "); //$NON-NLS-1$
      builder.append(singleMapping.getField().getName());
    }

    if (unsettableFields != null)
    {
      for (String fieldName : unsettableFields.values())
      {
        builder.append(", ");
        builder.append(fieldName);
      }
    }

    builder.append(" FROM "); //$NON-NLS-1$
    builder.append(getTable().getName());
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_ID);
    builder.append("= ? AND ");
    builder.append(CDODBSchema.ATTRIBUTES_BRANCH);
    builder.append("= ? AND ("); //$NON-NLS-1$
    String sqlSelectAttributesPrefix = builder.toString();

    builder.append(CDODBSchema.ATTRIBUTES_REVISED);
    builder.append(" = 0 )"); //$NON-NLS-1$

    sqlSelectCurrentAttributes = builder.toString();

    builder = new StringBuilder(sqlSelectAttributesPrefix);

    builder.append(CDODBSchema.ATTRIBUTES_CREATED);
    builder.append(" <= ? AND ( "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_REVISED);
    builder.append(" = 0 OR "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_REVISED);
    builder.append(" >= ?))"); //$NON-NLS-1$

    sqlSelectAttributesByTime = builder.toString();

    builder = new StringBuilder(sqlSelectAttributesPrefix);

    builder.append(CDODBSchema.ATTRIBUTES_VERSION);
    builder.append(" = ?)"); //$NON-NLS-1$

    sqlSelectAttributesByVersion = builder.toString();

    // ----------- Insert Attributes -------------------------
    builder = new StringBuilder();
    builder.append("INSERT INTO "); //$NON-NLS-1$
    builder.append(getTable().getName());

    builder.append("("); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_ID);
    builder.append(", "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_VERSION);
    builder.append(", "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_BRANCH);
    builder.append(", "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_CLASS);
    builder.append(", "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_CREATED);
    builder.append(", "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_REVISED);
    builder.append(", "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_RESOURCE);
    builder.append(", "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_CONTAINER);
    builder.append(", "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_FEATURE);

    for (ITypeMapping singleMapping : getValueMappings())
    {
      builder.append(", "); //$NON-NLS-1$
      builder.append(singleMapping.getField().getName());
    }

    if (unsettableFields != null)
    {
      for (String fieldName : unsettableFields.values())
      {
        builder.append(", ");
        builder.append(fieldName);
      }
    }

    builder.append(") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?"); //$NON-NLS-1$

    for (int i = 0; i < getValueMappings().size(); i++)
    {
      builder.append(", ?"); //$NON-NLS-1$
    }

    if (unsettableFields != null)
    {
      for (int i = 0; i < unsettableFields.size(); i++)
      {
        builder.append(", ?"); //$NON-NLS-1$
      }
    }

    builder.append(")"); //$NON-NLS-1$
    sqlInsertAttributes = builder.toString();

    // ----------- Update to set revised ----------------
    builder = new StringBuilder("UPDATE "); //$NON-NLS-1$
    builder.append(getTable().getName());
    builder.append(" SET "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_REVISED);
    builder.append(" = ? WHERE "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_ID);
    builder.append(" = ? AND "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_BRANCH);
    builder.append(" = ? AND "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_REVISED);
    builder.append(" = 0"); //$NON-NLS-1$
    sqlReviseAttributes = builder.toString();

    // ----------- Select all unrevised Object IDs ------
    builder = new StringBuilder("SELECT "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_ID);
    builder.append(" FROM "); //$NON-NLS-1$
    builder.append(getTable().getName());
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_REVISED);
    builder.append(" = 0"); //$NON-NLS-1$
    sqlSelectAllObjectIds = builder.toString();
  }

  public boolean readRevision(IDBStoreAccessor accessor, InternalCDORevision revision, int listChunk)
  {
    PreparedStatement pstmt = null;

    long timeStamp = revision.getTimeStamp();
    int branchId = revision.getBranch().getID();

    try
    {
      if (timeStamp != CDOBranchPoint.UNSPECIFIED_DATE)
      {
        pstmt = accessor.getStatementCache().getPreparedStatement(sqlSelectAttributesByTime, ReuseProbability.MEDIUM);
        pstmt.setLong(1, CDOIDUtil.getLong(revision.getID()));
        pstmt.setLong(2, branchId);
        pstmt.setLong(3, timeStamp);
        pstmt.setLong(4, timeStamp);
      }
      else
      {
        pstmt = accessor.getStatementCache().getPreparedStatement(sqlSelectCurrentAttributes, ReuseProbability.HIGH);
        pstmt.setLong(1, CDOIDUtil.getLong(revision.getID()));
        pstmt.setLong(2, branchId);
      }

      // Read singleval-attribute table always (even without modeled attributes!)
      boolean success = readValuesFromStatement(pstmt, revision, accessor);

      // Read multival tables only if revision exists
      if (success)
      {
        readLists(accessor, revision, listChunk);
      }

      return success;
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      accessor.getStatementCache().releasePreparedStatement(pstmt);
    }
  }

  public boolean readRevisionByVersion(IDBStoreAccessor accessor, InternalCDORevision revision, int listChunk)
  {
    PreparedStatement pstmt = null;
    try
    {
      pstmt = accessor.getStatementCache().getPreparedStatement(sqlSelectAttributesByVersion, ReuseProbability.HIGH);
      pstmt.setLong(1, CDOIDUtil.getLong(revision.getID()));
      pstmt.setInt(2, revision.getBranch().getID());
      pstmt.setInt(3, revision.getVersion());

      // Read singleval-attribute table always (even without modeled attributes!)
      boolean success = readValuesFromStatement(pstmt, revision, accessor);

      // Read multival tables only if revision exists
      if (success)
      {
        readLists(accessor, revision, listChunk);
      }

      return success;
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      accessor.getStatementCache().releasePreparedStatement(pstmt);
    }
  }

  public PreparedStatement createResourceQueryStatement(IDBStoreAccessor accessor, CDOID folderId, String name,
      boolean exactMatch, CDOBranchPoint branchPoint)
  {
    EStructuralFeature nameFeature = EresourcePackage.eINSTANCE.getCDOResourceNode_Name();

    ITypeMapping nameValueMapping = getValueMapping(nameFeature);
    if (nameValueMapping == null)
    {
      throw new ImplementationError(nameFeature + " not found in ClassMapping " + this); //$NON-NLS-1$
    }

    int branchId = branchPoint.getBranch().getID();
    long timeStamp = branchPoint.getTimeStamp();

    StringBuilder builder = new StringBuilder();
    builder.append("SELECT "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_ID);
    builder.append(" FROM "); //$NON-NLS-1$
    builder.append(getTable().getName());
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_BRANCH);
    builder.append("= ? AND "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_CONTAINER);
    builder.append("= ? AND "); //$NON-NLS-1$
    builder.append(nameValueMapping.getField().getName());
    if (name == null)
    {
      builder.append(" IS NULL"); //$NON-NLS-1$
    }
    else
    {
      builder.append(exactMatch ? " = ? " : " LIKE ? "); //$NON-NLS-1$ //$NON-NLS-2$
    }

    builder.append(" AND ( "); //$NON-NLS-1$

    if (timeStamp == CDORevision.UNSPECIFIED_DATE)
    {
      builder.append(CDODBSchema.ATTRIBUTES_REVISED);
      builder.append(" = 0 )"); //$NON-NLS-1$
    }
    else
    {
      builder.append(CDODBSchema.ATTRIBUTES_CREATED);
      builder.append(" <= ? AND ( "); //$NON-NLS-1$
      builder.append(CDODBSchema.ATTRIBUTES_REVISED);
      builder.append(" = 0 OR "); //$NON-NLS-1$
      builder.append(CDODBSchema.ATTRIBUTES_REVISED);
      builder.append(" >= ?))"); //$NON-NLS-1$
    }

    PreparedStatement pstmt = null;
    try
    {
      int idx = 1;

      pstmt = accessor.getStatementCache().getPreparedStatement(builder.toString(), ReuseProbability.MEDIUM);
      pstmt.setInt(idx++, branchId);
      pstmt.setLong(idx++, CDOIDUtil.getLong(folderId));

      if (name != null)
      {
        String queryName = exactMatch ? name : name + "%"; //$NON-NLS-1$
        nameValueMapping.setValue(pstmt, idx++, queryName);
      }

      if (timeStamp != CDORevision.UNSPECIFIED_DATE)
      {
        pstmt.setLong(idx++, timeStamp);
        pstmt.setLong(idx++, timeStamp);
      }

      if (TRACER.isEnabled())
      {
        TRACER.format("Created Resource Query: {0}", pstmt.toString()); //$NON-NLS-1$
      }

      return pstmt;
    }
    catch (SQLException ex)
    {
      accessor.getStatementCache().releasePreparedStatement(pstmt); // only release on error
      throw new DBException(ex);
    }
  }

  public PreparedStatement createObjectIdStatement(IDBStoreAccessor accessor)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Created ObjectID Statement : {0}", sqlSelectAllObjectIds); //$NON-NLS-1$
    }

    return accessor.getStatementCache().getPreparedStatement(sqlSelectAllObjectIds, ReuseProbability.HIGH);
  }

  @Override
  protected final void writeValues(IDBStoreAccessor accessor, InternalCDORevision revision)
  {
    PreparedStatement stmt = null;

    try
    {
      stmt = accessor.getStatementCache().getPreparedStatement(sqlInsertAttributes, ReuseProbability.HIGH);

      int col = 1;

      stmt.setLong(col++, CDOIDUtil.getLong(revision.getID()));
      stmt.setInt(col++, revision.getVersion());
      stmt.setInt(col++, revision.getBranch().getID());
      stmt.setLong(col++, accessor.getStore().getMetaDataManager().getMetaID(revision.getEClass()));
      stmt.setLong(col++, revision.getTimeStamp());
      stmt.setLong(col++, revision.getRevised());
      stmt.setLong(col++, CDODBUtil.convertCDOIDToLong(getExternalReferenceManager(), accessor, revision
          .getResourceID()));
      stmt.setLong(col++, CDODBUtil.convertCDOIDToLong(getExternalReferenceManager(), accessor, (CDOID)revision
          .getContainerID()));
      stmt.setInt(col++, revision.getContainingFeatureID());

      int isSetCol = col + getValueMappings().size();

      for (ITypeMapping mapping : getValueMappings())
      {
        EStructuralFeature feature = mapping.getFeature();
        if (feature.isUnsettable())
        {
          if (revision.getValue(feature) == null)
          {
            stmt.setBoolean(isSetCol++, false);

            // also set value column to default value
            mapping.setDefaultValue(stmt, col++);

            continue;
          }
          else
          {
            stmt.setBoolean(isSetCol++, true);
          }
        }
        mapping.setValueFromRevision(stmt, col++, revision);
      }

      CDODBUtil.sqlUpdate(stmt, true);
    }
    catch (SQLException e)
    {
      throw new DBException(e);
    }
    finally
    {
      accessor.getStatementCache().releasePreparedStatement(stmt);
    }
  }

  public final void detachObject(IDBStoreAccessor accessor, CDOID id, long revised, CDOBranch branch, OMMonitor monitor)
  {
    Async async = null;
    try
    {
      monitor.begin(getListMappings().size() + 1);
      async = monitor.forkAsync();
      reviseObject(accessor, id, branch.getID(), revised);
      async.stop();
      async = monitor.forkAsync(getListMappings().size());
      for (IListMapping mapping : getListMappings())
      {
        mapping.objectRevised(accessor, id, revised);
      }
    }
    finally
    {
      async.stop();
      monitor.done();
    }
  }

  public void detachFirstVersion(IDBStoreAccessor accessor, CDORevision rev, long revised, OMMonitor monitor)
  {
    PreparedStatement stmt = null;

    InternalCDORevision revision = (InternalCDORevision)rev;

    try
    {
      stmt = accessor.getStatementCache().getPreparedStatement(sqlInsertAttributes, ReuseProbability.HIGH);

      int col = 1;

      stmt.setLong(col++, CDOIDUtil.getLong(revision.getID()));
      stmt.setInt(col++, -1); // cdo_version
      stmt.setInt(col++, revision.getBranch().getID());
      stmt.setLong(col++, accessor.getStore().getMetaDataManager().getMetaID(revision.getEClass()));
      stmt.setLong(col++, revised); // cdo_created
      stmt.setLong(col++, 0); // cdo_revised
      stmt.setLong(col++, CDODBUtil.convertCDOIDToLong(getExternalReferenceManager(), accessor, revision
          .getResourceID()));
      stmt.setLong(col++, CDODBUtil.convertCDOIDToLong(getExternalReferenceManager(), accessor, (CDOID)revision
          .getContainerID()));
      stmt.setInt(col++, revision.getContainingFeatureID());

      // XXX Eike: what to do with attributes?
      // the following is currently a copy of writeValues ...

      int isSetCol = col + getValueMappings().size();

      for (ITypeMapping mapping : getValueMappings())
      {
        EStructuralFeature feature = mapping.getFeature();
        if (feature.isUnsettable())
        {
          if (revision.getValue(feature) == null)
          {
            stmt.setBoolean(isSetCol++, false);

            // also set value column to default value
            mapping.setDefaultValue(stmt, col++);

            continue;
          }
          else
          {
            stmt.setBoolean(isSetCol++, true);
          }
        }
        mapping.setValueFromRevision(stmt, col++, revision);
      }

      CDODBUtil.sqlUpdate(stmt, true);
    }
    catch (SQLException e)
    {
      throw new DBException(e);
    }
    finally
    {
      accessor.getStatementCache().releasePreparedStatement(stmt);
    }
  }

  protected void reviseObject(IDBStoreAccessor accessor, CDOID id, int branchId, long revised)
  {
    PreparedStatement stmt = null;

    try
    {
      stmt = accessor.getStatementCache().getPreparedStatement(sqlReviseAttributes, ReuseProbability.HIGH);

      stmt.setLong(1, revised);
      stmt.setLong(2, CDOIDUtil.getLong(id));
      stmt.setInt(3, branchId);

      CDODBUtil.sqlUpdate(stmt, true);
    }
    catch (SQLException e)
    {
      throw new DBException(e);
    }
    finally
    {
      accessor.getStatementCache().releasePreparedStatement(stmt);
    }
  }

  @Override
  protected void reviseObject(IDBStoreAccessor accessor, CDOID id, long revised)
  {
    reviseObject(accessor, id, CDOBranch.MAIN_BRANCH_ID, revised);
  }

  @Override
  public void writeRevision(IDBStoreAccessor accessor, InternalCDORevision revision, OMMonitor monitor)
  {
    Async async = null;
    try
    {
      monitor.begin(10);
      async = monitor.forkAsync();

      CDOID id = revision.getID();

      if (accessor.isNewObject(id))
      {
        // put new objects into objectTypeCache
        ((HorizontalBranchingMappingStrategy)getMappingStrategy()).putObjectType(accessor, id, getEClass());
      }
      else if (revision.getVersion() > 1)
      {
        // if revision is not the first one, revise the old revision
        long revised = revision.getTimeStamp() - 1;
        reviseObject(accessor, id, revision.getBranch().getID(), revised);
        for (IListMapping mapping : getListMappings())
        {
          mapping.objectRevised(accessor, id, revised);
        }
      }

      async.stop();
      async = monitor.forkAsync();

      if (revision.isResourceFolder() || revision.isResource())
      {
        checkDuplicateResources(accessor, revision);
      }

      async.stop();
      async = monitor.forkAsync();

      // Write attribute table always (even without modeled attributes!)
      writeValues(accessor, revision);

      async.stop();
      async = monitor.forkAsync(7);

      // Write list tables only if they exist
      if (getListMappings() != null)
      {
        writeLists(accessor, revision);
      }
    }
    finally
    {
      async.stop();
      monitor.done();
    }
  }
}
