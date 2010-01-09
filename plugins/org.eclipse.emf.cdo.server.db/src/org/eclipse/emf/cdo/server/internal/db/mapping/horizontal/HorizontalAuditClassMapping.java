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
 */
package org.eclipse.emf.cdo.server.internal.db.mapping.horizontal;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IPreparedStatementCache.ReuseProbability;
import org.eclipse.emf.cdo.server.db.mapping.IClassMapping;
import org.eclipse.emf.cdo.server.db.mapping.IClassMappingAuditSupport;
import org.eclipse.emf.cdo.server.db.mapping.ITypeMapping;
import org.eclipse.emf.cdo.server.internal.db.CDODBSchema;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * TODO use async monitors
 * 
 * @author Eike Stepper
 * @since 2.0
 */
public class HorizontalAuditClassMapping extends AbstractHorizontalClassMapping implements IClassMapping,
    IClassMappingAuditSupport
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, HorizontalAuditClassMapping.class);

  private String sqlInsertAttributes;

  private String sqlSelectCurrentAttributes;

  private String sqlSelectAllObjectIds;

  private String sqlSelectAttributesByTime;

  private String sqlSelectAttributesByVersion;

  private String sqlReviseAttributes;

  public HorizontalAuditClassMapping(AbstractHorizontalMappingStrategy mappingStrategy, EClass eClass)
  {
    super(mappingStrategy, eClass);

    initSqlStrings();
  }

  private void initSqlStrings()
  {
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

    builder.append(") VALUES (?, ?, ?, ?, ?, ?, ?, ?"); //$NON-NLS-1$

    for (int i = 0; i < getValueMappings().size(); i++)
    {
      builder.append(", ?"); //$NON-NLS-1$
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

  public boolean readRevisionByTime(IDBStoreAccessor accessor, InternalCDORevision revision, long timeStamp,
      int listChunk)
  {
    PreparedStatement pstmt = null;

    try
    {
      pstmt = accessor.getStatementCache().getPreparedStatement(sqlSelectAttributesByTime, ReuseProbability.MEDIUM);
      pstmt.setLong(1, CDOIDUtil.getLong(revision.getID()));
      pstmt.setLong(2, timeStamp);
      pstmt.setLong(3, timeStamp);

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

  public boolean readRevisionByVersion(IDBStoreAccessor accessor, InternalCDORevision revision, int version,
      int listChunk)
  {
    PreparedStatement pstmt = null;
    try
    {
      pstmt = accessor.getStatementCache().getPreparedStatement(sqlSelectAttributesByVersion, ReuseProbability.HIGH);
      pstmt.setLong(1, CDOIDUtil.getLong(revision.getID()));
      pstmt.setInt(2, version);

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
      boolean exactMatch, long timeStamp)
  {
    EStructuralFeature nameFeature = EresourcePackage.eINSTANCE.getCDOResourceNode_Name();

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

  public boolean readRevision(IDBStoreAccessor accessor, InternalCDORevision revision, int listChunk)
  {
    PreparedStatement pstmt = null;
    try
    {
      pstmt = accessor.getStatementCache().getPreparedStatement(sqlSelectCurrentAttributes, ReuseProbability.HIGH);
      pstmt.setLong(1, CDOIDUtil.getLong(revision.getID()));

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
      stmt.setLong(col++, revision.getCreated());
      stmt.setLong(col++, revision.getRevised());
      stmt.setLong(col++, CDODBUtil.convertCDOIDToLong(getExternalReferenceManager(), accessor, revision
          .getResourceID()));
      stmt.setLong(col++, CDODBUtil.convertCDOIDToLong(getExternalReferenceManager(), accessor, (CDOID)revision
          .getContainerID()));
      stmt.setInt(col++, revision.getContainingFeatureID());

      for (ITypeMapping mapping : getValueMappings())
      {
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
}
