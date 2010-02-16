/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - major refactoring
 *    Stefan Winkler - Bug 249610: [DB] Support external references (Implementation)
 *    Lothar Werzinger - Bug 296440: [DB] Change RDB schema to improve scalability of to-many references in audit mode
 */
package org.eclipse.emf.cdo.server.internal.db.mapping.horizontal;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDOAddFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOClearFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOContainerFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDeltaVisitor;
import org.eclipse.emf.cdo.common.revision.delta.CDOListFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOMoveFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORemoveFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOUnsetFeatureDelta;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IPreparedStatementCache.ReuseProbability;
import org.eclipse.emf.cdo.server.db.mapping.IClassMappingAuditSupport;
import org.eclipse.emf.cdo.server.db.mapping.IClassMappingDeltaSupport;
import org.eclipse.emf.cdo.server.db.mapping.IListMappingDeltaSupport;
import org.eclipse.emf.cdo.server.db.mapping.ITypeMapping;
import org.eclipse.emf.cdo.server.internal.db.CDODBSchema;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;

import org.eclipse.net4j.db.DBException;
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
 * @since 2.0
 */
public class HorizontalAuditClassMapping extends AbstractHorizontalClassMapping implements IClassMappingAuditSupport,
    IClassMappingDeltaSupport
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, HorizontalAuditClassMapping.class);

  private String sqlInsertAttributes;

  private String sqlSelectCurrentAttributes;

  private String sqlSelectAllObjectIds;

  private String sqlSelectAttributesByTime;

  private String sqlSelectAttributesByVersion;

  private String sqlReviseAttributes;

  private ThreadLocal<FeatureDeltaWriter> deltaWriter = new ThreadLocal<FeatureDeltaWriter>()
  {
    @Override
    protected FeatureDeltaWriter initialValue()
    {
      return new FeatureDeltaWriter();
    }
  };

  public HorizontalAuditClassMapping(AbstractHorizontalMappingStrategy mappingStrategy, EClass eClass)
  {
    super(mappingStrategy, eClass);

    initSqlStrings();
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

    builder.append(") VALUES (?, ?, ?, ?, ?, ?, ?, ?"); //$NON-NLS-1$

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

    try
    {
      long timeStamp = revision.getTimeStamp();
      if (timeStamp != CDOBranchPoint.UNSPECIFIED_DATE)
      {
        pstmt = accessor.getStatementCache().getPreparedStatement(sqlSelectAttributesByTime, ReuseProbability.MEDIUM);
        pstmt.setLong(1, CDOIDUtil.getLong(revision.getID()));
        pstmt.setLong(2, timeStamp);
        pstmt.setLong(3, timeStamp);
      }
      else
      {
        pstmt = accessor.getStatementCache().getPreparedStatement(sqlSelectCurrentAttributes, ReuseProbability.HIGH);
        pstmt.setLong(1, CDOIDUtil.getLong(revision.getID()));
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
      pstmt.setInt(2, revision.getVersion());

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
    long timeStamp = branchPoint.getTimeStamp();

    ITypeMapping nameValueMapping = getValueMapping(nameFeature);
    if (nameValueMapping == null)
    {
      throw new ImplementationError(nameFeature + " not found in ClassMapping " + this); //$NON-NLS-1$
    }

    StringBuilder builder = new StringBuilder();
    builder.append("SELECT "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_ID);
    builder.append(" FROM "); //$NON-NLS-1$
    builder.append(getTable().getName());
    builder.append(" WHERE "); //$NON-NLS-1$
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

          stmt.setBoolean(isSetCol++, true);
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

  @Override
  protected void reviseObject(IDBStoreAccessor accessor, CDOID id, long revised)
  {
    PreparedStatement stmt = null;

    try
    {
      stmt = accessor.getStatementCache().getPreparedStatement(sqlReviseAttributes, ReuseProbability.HIGH);

      stmt.setLong(1, revised);
      stmt.setLong(2, CDOIDUtil.getLong(id));

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

  public void writeRevisionDelta(IDBStoreAccessor accessor, InternalCDORevisionDelta delta, long created,
      OMMonitor monitor)
  {
    monitor.begin();
    Async async = monitor.forkAsync();

    try
    {
      FeatureDeltaWriter writer = deltaWriter.get();
      writer.process(accessor, delta, created);
    }
    finally
    {
      async.stop();
      monitor.done();
    }
  }

  /**
   * @author Stefan Winkler
   */
  private class FeatureDeltaWriter implements CDOFeatureDeltaVisitor
  {
    private IDBStoreAccessor accessor;

    private long created;

    private CDOID id;

    private int oldVersion;

    private InternalCDORevision newRevision;

    public void process(IDBStoreAccessor accessor, InternalCDORevisionDelta delta, long created)
    {
      this.accessor = accessor;
      this.created = created;
      id = delta.getID();
      oldVersion = delta.getVersion();

      if (TRACER.isEnabled())
      {
        TRACER.format("FeatureDeltaWriter: old version: {0}, new version: {1}", oldVersion, oldVersion + 1); //$NON-NLS-1$
      }

      InternalCDORevision originalRevision = (InternalCDORevision)accessor.getStore().getRepository()
          .getRevisionManager().getRevisionByVersion(id, delta, 0, true);

      newRevision = originalRevision.copy();

      newRevision.setVersion(oldVersion + 1);
      newRevision.setBranchPoint(CDOBranchUtil.createBranchPoint(delta.getBranch(), created));

      // process revision delta tree
      delta.accept(this);

      long revised = newRevision.getTimeStamp() - 1;
      reviseObject(accessor, id, revised);

      writeValues(accessor, newRevision);
    }

    public void visit(CDOMoveFeatureDelta delta)
    {
      throw new ImplementationError("Should not be called"); //$NON-NLS-1$
    }

    public void visit(CDOAddFeatureDelta delta)
    {
      throw new ImplementationError("Should not be called"); //$NON-NLS-1$
    }

    public void visit(CDORemoveFeatureDelta delta)
    {
      throw new ImplementationError("Should not be called"); //$NON-NLS-1$
    }

    public void visit(CDOSetFeatureDelta delta)
    {
      delta.apply(newRevision);
    }

    public void visit(CDOUnsetFeatureDelta delta)
    {
      delta.apply(newRevision);
    }

    public void visit(CDOListFeatureDelta delta)
    {
      IListMappingDeltaSupport listMapping = (IListMappingDeltaSupport)getListMapping(delta.getFeature());
      listMapping.processDelta(accessor, id, oldVersion, oldVersion + 1, created, delta);
    }

    public void visit(CDOClearFeatureDelta delta)
    {
      throw new ImplementationError("Should not be called"); //$NON-NLS-1$
    }

    public void visit(CDOContainerFeatureDelta delta)
    {
      delta.apply(newRevision);
    }
  }
}
