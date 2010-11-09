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
 *    Stefan Winkler - 249610: [DB] Support external references (Implementation)
 *    Stefan Winkler - derived branch mapping from audit mapping
 *    Stefan Winkler - Bug 329025: [DB] Support branching for range-based mapping strategy
 */
package org.eclipse.emf.cdo.server.internal.db.mapping.horizontal;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;
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
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStoreAccessor.QueryXRefsContext;
import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IPreparedStatementCache;
import org.eclipse.emf.cdo.server.db.IPreparedStatementCache.ReuseProbability;
import org.eclipse.emf.cdo.server.db.mapping.IClassMappingAuditSupport;
import org.eclipse.emf.cdo.server.db.mapping.IClassMappingDeltaSupport;
import org.eclipse.emf.cdo.server.db.mapping.IListMapping;
import org.eclipse.emf.cdo.server.db.mapping.IListMappingDeltaSupport;
import org.eclipse.emf.cdo.server.db.mapping.ITypeMapping;
import org.eclipse.emf.cdo.server.internal.db.CDODBSchema;
import org.eclipse.emf.cdo.server.internal.db.DBStore;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;
import org.eclipse.emf.cdo.spi.common.commit.CDOChangeSetSegment;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.spi.server.InternalRepository;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor.Async;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 * @author Stefan Winkler
 * @since 3.0
 */
public class HorizontalBranchingClassMapping extends AbstractHorizontalClassMapping implements
    IClassMappingAuditSupport, IClassMappingDeltaSupport
{
  /**
   * @author Stefan Winkler
   */
  private class FeatureDeltaWriter implements CDOFeatureDeltaVisitor
  {
    private IDBStoreAccessor accessor;

    private long created;

    private CDOID id;

    private CDOBranch targetBranch;

    private int oldVersion;

    private int newVersion;

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

      InternalCDORevision originalRevision = (InternalCDORevision)accessor.getTransaction().getRevision(id);
      newRevision = originalRevision.copy();
      targetBranch = accessor.getTransaction().getBranch();
      newRevision.adjustForCommit(targetBranch, created);

      newVersion = newRevision.getVersion();

      // process revision delta tree
      delta.accept(this);

      if (newVersion != CDORevision.FIRST_VERSION)
      {
        reviseOldRevision(accessor, id, delta.getBranch(), newRevision.getTimeStamp() - 1);
      }

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
      delta.apply(newRevision);
      IListMappingDeltaSupport listMapping = (IListMappingDeltaSupport)getListMapping(delta.getFeature());
      listMapping.processDelta(accessor, id, targetBranch.getID(), oldVersion, newVersion, created, delta);
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

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, HorizontalBranchingClassMapping.class);

  private String sqlInsertAttributes;

  private String sqlSelectCurrentAttributes;

  private String sqlSelectAllObjectIDs;

  private String sqlSelectAttributesByTime;

  private String sqlSelectAttributesByVersion;

  private String sqlReviseAttributes;

  private String sqlSelectForHandle;

  private String sqlSelectForChangeSet;

  private ThreadLocal<FeatureDeltaWriter> deltaWriter = new ThreadLocal<FeatureDeltaWriter>()
  {
    @Override
    protected FeatureDeltaWriter initialValue()
    {
      return new FeatureDeltaWriter();
    }
  };

  public HorizontalBranchingClassMapping(AbstractHorizontalMappingStrategy mappingStrategy, EClass eClass)
  {
    super(mappingStrategy, eClass);

    initSQLStrings();
  }

  @Override
  protected IDBField addBranchingField(IDBTable table)
  {
    return table.addField(CDODBSchema.ATTRIBUTES_BRANCH, DBType.INTEGER, true);
  }

  private void initSQLStrings()
  {
    Map<EStructuralFeature, String> unsettableFields = getUnsettableFields();
    Map<EStructuralFeature, String> listSizeFields = getListSizeFields();

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
      builder.append(singleMapping.getField());
    }

    if (unsettableFields != null)
    {
      for (String fieldName : unsettableFields.values())
      {
        builder.append(", "); //$NON-NLS-1$
        builder.append(fieldName);
      }
    }

    if (listSizeFields != null)
    {
      for (String fieldName : listSizeFields.values())
      {
        builder.append(", "); //$NON-NLS-1$
        builder.append(fieldName);
      }
    }

    builder.append(" FROM "); //$NON-NLS-1$
    builder.append(getTable());
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_ID);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_BRANCH);
    builder.append("=? AND ("); //$NON-NLS-1$
    String sqlSelectAttributesPrefix = builder.toString();

    builder.append(CDODBSchema.ATTRIBUTES_REVISED);
    builder.append("=0)"); //$NON-NLS-1$

    sqlSelectCurrentAttributes = builder.toString();

    builder = new StringBuilder(sqlSelectAttributesPrefix);

    builder.append(CDODBSchema.ATTRIBUTES_CREATED);
    builder.append("<=? AND ("); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_REVISED);
    builder.append("=0 OR "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_REVISED);
    builder.append(">=?))"); //$NON-NLS-1$

    sqlSelectAttributesByTime = builder.toString();

    builder = new StringBuilder(sqlSelectAttributesPrefix);

    builder.append("ABS("); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_VERSION);
    builder.append(")=?)"); //$NON-NLS-1$

    sqlSelectAttributesByVersion = builder.toString();

    // ----------- Insert Attributes -------------------------
    builder = new StringBuilder();
    builder.append("INSERT INTO "); //$NON-NLS-1$
    builder.append(getTable());

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
      builder.append(singleMapping.getField());
    }

    if (unsettableFields != null)
    {
      for (String fieldName : unsettableFields.values())
      {
        builder.append(", "); //$NON-NLS-1$
        builder.append(fieldName);
      }
    }

    if (listSizeFields != null)
    {
      for (String fieldName : listSizeFields.values())
      {
        builder.append(", "); //$NON-NLS-1$
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

    if (listSizeFields != null)
    {
      for (int i = 0; i < listSizeFields.size(); i++)
      {
        builder.append(", ?"); //$NON-NLS-1$
      }
    }

    builder.append(")"); //$NON-NLS-1$
    sqlInsertAttributes = builder.toString();

    // ----------- Update to set revised ----------------
    builder = new StringBuilder("UPDATE "); //$NON-NLS-1$
    builder.append(getTable());
    builder.append(" SET "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_REVISED);
    builder.append("=? WHERE "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_ID);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_BRANCH);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_REVISED);
    builder.append("=0"); //$NON-NLS-1$
    sqlReviseAttributes = builder.toString();

    // ----------- Select all unrevised Object IDs ------
    builder = new StringBuilder("SELECT "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_ID);
    builder.append(" FROM "); //$NON-NLS-1$
    builder.append(getTable());
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_REVISED);
    builder.append("=0"); //$NON-NLS-1$
    sqlSelectAllObjectIDs = builder.toString();

    // ----------- Select all revisions (for handleRevision) ---
    builder = new StringBuilder("SELECT "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_ID);
    builder.append(", "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_VERSION);
    builder.append(", "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_BRANCH);
    builder.append(" FROM "); //$NON-NLS-1$
    builder.append(getTable());
    sqlSelectForHandle = builder.toString();

    // ----------- Select all revisions (for handleRevision) ---
    builder = new StringBuilder("SELECT DISTINCT "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_ID);
    builder.append(" FROM "); //$NON-NLS-1$
    builder.append(getTable());
    builder.append(" WHERE "); //$NON-NLS-1$
    sqlSelectForChangeSet = builder.toString();
  }

  public boolean readRevision(IDBStoreAccessor accessor, InternalCDORevision revision, int listChunk)
  {
    IPreparedStatementCache statementCache = accessor.getStatementCache();
    PreparedStatement pstmt = null;

    long timeStamp = revision.getTimeStamp();
    int branchID = revision.getBranch().getID();

    try
    {
      if (timeStamp != DBStore.UNSPECIFIED_DATE)
      {
        pstmt = statementCache.getPreparedStatement(sqlSelectAttributesByTime, ReuseProbability.MEDIUM);
        pstmt.setLong(1, CDOIDUtil.getLong(revision.getID()));
        pstmt.setLong(2, branchID);
        pstmt.setLong(3, timeStamp);
        pstmt.setLong(4, timeStamp);
      }
      else
      {
        pstmt = statementCache.getPreparedStatement(sqlSelectCurrentAttributes, ReuseProbability.HIGH);
        pstmt.setLong(1, CDOIDUtil.getLong(revision.getID()));
        pstmt.setLong(2, branchID);
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
      statementCache.releasePreparedStatement(pstmt);
    }
  }

  public boolean readRevisionByVersion(IDBStoreAccessor accessor, InternalCDORevision revision, int listChunk)
  {
    IPreparedStatementCache statementCache = accessor.getStatementCache();
    PreparedStatement pstmt = null;

    try
    {
      pstmt = statementCache.getPreparedStatement(sqlSelectAttributesByVersion, ReuseProbability.HIGH);
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
      statementCache.releasePreparedStatement(pstmt);
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

    int branchID = branchPoint.getBranch().getID();
    long timeStamp = branchPoint.getTimeStamp();

    StringBuilder builder = new StringBuilder();
    builder.append("SELECT "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_ID);
    builder.append(" FROM "); //$NON-NLS-1$
    builder.append(getTable());
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_VERSION);
    builder.append(">0 AND "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_BRANCH);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_CONTAINER);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(nameValueMapping.getField());
    if (name == null)
    {
      builder.append(" IS NULL"); //$NON-NLS-1$
    }
    else
    {
      builder.append(exactMatch ? " =? " : " LIKE ? "); //$NON-NLS-1$ //$NON-NLS-2$
    }

    builder.append(" AND ("); //$NON-NLS-1$

    if (timeStamp == CDORevision.UNSPECIFIED_DATE)
    {
      builder.append(CDODBSchema.ATTRIBUTES_REVISED);
      builder.append("=0)"); //$NON-NLS-1$
    }
    else
    {
      builder.append(CDODBSchema.ATTRIBUTES_CREATED);
      builder.append("<=? AND ("); //$NON-NLS-1$
      builder.append(CDODBSchema.ATTRIBUTES_REVISED);
      builder.append("=0 OR "); //$NON-NLS-1$
      builder.append(CDODBSchema.ATTRIBUTES_REVISED);
      builder.append(">=?))"); //$NON-NLS-1$
    }

    IPreparedStatementCache statementCache = accessor.getStatementCache();
    PreparedStatement pstmt = null;

    try
    {
      int idx = 1;

      pstmt = statementCache.getPreparedStatement(builder.toString(), ReuseProbability.MEDIUM);
      pstmt.setInt(idx++, branchID);
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
      statementCache.releasePreparedStatement(pstmt); // only release on error
      throw new DBException(ex);
    }
  }

  public PreparedStatement createObjectIDStatement(IDBStoreAccessor accessor)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Created ObjectID Statement : {0}", sqlSelectAllObjectIDs); //$NON-NLS-1$
    }

    IPreparedStatementCache statementCache = accessor.getStatementCache();
    return statementCache.getPreparedStatement(sqlSelectAllObjectIDs, ReuseProbability.HIGH);
  }

  @Override
  protected final void writeValues(IDBStoreAccessor accessor, InternalCDORevision revision)
  {
    long commitTime = revision.getTimeStamp();
    IPreparedStatementCache statementCache = accessor.getStatementCache();
    PreparedStatement stmt = null;

    try
    {
      int col = 1;
      stmt = statementCache.getPreparedStatement(sqlInsertAttributes, ReuseProbability.HIGH);
      stmt.setLong(col++, CDOIDUtil.getLong(revision.getID()));
      stmt.setInt(col++, revision.getVersion());
      stmt.setInt(col++, revision.getBranch().getID());
      stmt.setLong(col++, accessor.getStore().getMetaDataManager().getMetaID(revision.getEClass()));
      stmt.setLong(col++, commitTime);
      stmt.setLong(col++, revision.getRevised());
      stmt.setLong(col++,
          CDODBUtil.convertCDOIDToLong(getExternalReferenceManager(), accessor, revision.getResourceID(), commitTime));
      stmt.setLong(col++, CDODBUtil.convertCDOIDToLong(getExternalReferenceManager(), accessor,
          (CDOID)revision.getContainerID(), commitTime));
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

      Map<EStructuralFeature, String> listSizeFields = getListSizeFields();
      if (listSizeFields != null)
      {
        // isSetCol now points to the first listTableSize-column
        col = isSetCol;

        for (EStructuralFeature feature : listSizeFields.keySet())
        {
          CDOList list = revision.getList(feature);
          stmt.setInt(col++, list.size());
        }
      }

      CDODBUtil.sqlUpdate(stmt, true);
    }
    catch (SQLException e)
    {
      throw new DBException(e);
    }
    finally
    {
      statementCache.releasePreparedStatement(stmt);
    }
  }

  @Override
  protected void detachAttributes(IDBStoreAccessor accessor, CDOID id, int version, CDOBranch branch, long timeStamp,
      OMMonitor mon)
  {
    IPreparedStatementCache statementCache = accessor.getStatementCache();
    PreparedStatement stmt = null;

    try
    {
      stmt = statementCache.getPreparedStatement(sqlInsertAttributes, ReuseProbability.HIGH);

      int col = 1;
      stmt.setLong(col++, CDOIDUtil.getLong(id));
      stmt.setInt(col++, -version); // cdo_version
      stmt.setInt(col++, branch.getID());
      stmt.setLong(col++, accessor.getStore().getMetaDataManager().getMetaID(getEClass()));
      stmt.setLong(col++, timeStamp); // cdo_created
      stmt.setLong(col++, DBStore.UNSPECIFIED_DATE); // cdo_revised
      stmt.setLong(col++, DBStore.NULL); // resource
      stmt.setLong(col++, DBStore.NULL); // container
      stmt.setInt(col++, 0); // containing feature ID

      int isSetCol = col + getValueMappings().size();

      for (ITypeMapping mapping : getValueMappings())
      {
        EStructuralFeature feature = mapping.getFeature();
        if (feature.isUnsettable())
        {
          stmt.setBoolean(isSetCol++, false);
        }

        mapping.setDefaultValue(stmt, col++);
      }

      Map<EStructuralFeature, String> listSizeFields = getListSizeFields();
      if (listSizeFields != null)
      {
        // list size columns begin after isSet-columns
        col = isSetCol;

        for (int i = 0; i < listSizeFields.size(); i++)
        {
          stmt.setInt(col++, 0);
        }
      }

      CDODBUtil.sqlUpdate(stmt, true);
    }
    catch (SQLException e)
    {
      throw new DBException(e);
    }
    finally
    {
      statementCache.releasePreparedStatement(stmt);
    }
  }

  @Override
  protected void reviseOldRevision(IDBStoreAccessor accessor, CDOID id, CDOBranch branch, long revised)
  {
    IPreparedStatementCache statementCache = accessor.getStatementCache();
    PreparedStatement stmt = null;

    try
    {
      stmt = statementCache.getPreparedStatement(sqlReviseAttributes, ReuseProbability.HIGH);

      stmt.setLong(1, revised);
      stmt.setLong(2, CDOIDUtil.getLong(id));
      stmt.setInt(3, branch.getID());

      CDODBUtil.sqlUpdate(stmt, false); // No row affected if old revision from other branch!
    }
    catch (SQLException e)
    {
      throw new DBException(e);
    }
    finally
    {
      statementCache.releasePreparedStatement(stmt);
    }
  }

  @Override
  public void writeRevision(IDBStoreAccessor accessor, InternalCDORevision revision, boolean newRevision,
      OMMonitor monitor)
  {
    Async async = null;
    monitor.begin(10);

    try
    {
      try
      {
        async = monitor.forkAsync();
        CDOID id = revision.getID();
        if (newRevision)
        {
          // put new objects into objectTypeMapper
          long timeStamp = revision.getTimeStamp();
          AbstractHorizontalMappingStrategy mappingStrategy = (AbstractHorizontalMappingStrategy)getMappingStrategy();
          mappingStrategy.putObjectType(accessor, timeStamp, id, getEClass());
        }
        else if (revision.getVersion() > CDOBranchVersion.FIRST_VERSION)
        {
          // if revision is not the first one, revise the old revision
          long revised = revision.getTimeStamp() - 1;
          reviseOldRevision(accessor, id, revision.getBranch(), revised);
          for (IListMapping mapping : getListMappings())
          {
            mapping.objectDetached(accessor, id, revised);
          }
        }
      }
      finally
      {
        if (async != null)
        {
          async.stop();
        }
      }

      try
      {
        async = monitor.forkAsync();
        if (revision.isResourceFolder() || revision.isResource())
        {
          checkDuplicateResources(accessor, revision);
        }
      }
      finally
      {
        if (async != null)
        {
          async.stop();
        }
      }

      try
      {
        // Write attribute table always (even without modeled attributes!)
        async = monitor.forkAsync();
        writeValues(accessor, revision);
      }
      finally
      {
        if (async != null)
        {
          async.stop();
        }
      }

      try
      {
        // Write list tables only if they exist
        async = monitor.forkAsync(7);
        if (getListMappings() != null)
        {
          writeLists(accessor, revision);
        }
      }
      finally
      {
        if (async != null)
        {
          async.stop();
        }
      }
    }
    finally
    {
      monitor.done();
    }
  }

  @Override
  public void handleRevisions(IDBStoreAccessor accessor, CDOBranch branch, long timeStamp, boolean exactTime,
      CDORevisionHandler handler)
  {
    StringBuilder builder = new StringBuilder(sqlSelectForHandle);
    boolean whereAppend = false;

    if (branch != null)
    {
      // TODO: Prepare this string literal
      builder.append(" WHERE "); //$NON-NLS-1$
      builder.append(CDODBSchema.ATTRIBUTES_BRANCH);
      builder.append("=?"); //$NON-NLS-1$

      whereAppend = true;
    }

    int timeParameters = 0;
    if (exactTime)
    {
      if (timeStamp != DBStore.UNSPECIFIED_DATE)
      {
        builder.append(whereAppend ? " AND " : " WHERE "); //$NON-NLS-1$ //$NON-NLS-2$
        builder.append(CDODBSchema.ATTRIBUTES_CREATED);
        builder.append("=?"); //$NON-NLS-1$
        timeParameters = 1;
      }
    }
    else
    {
      builder.append(whereAppend ? " AND " : " WHERE "); //$NON-NLS-1$ //$NON-NLS-2$
      if (timeStamp != DBStore.UNSPECIFIED_DATE)
      {
        builder.append(CDODBSchema.ATTRIBUTES_CREATED);
        builder.append(">=?"); //$NON-NLS-1$
        builder.append(" AND ("); //$NON-NLS-1$
        builder.append(CDODBSchema.ATTRIBUTES_REVISED);
        builder.append("<=? OR "); //$NON-NLS-1$
        builder.append(CDODBSchema.ATTRIBUTES_REVISED);
        builder.append("="); //$NON-NLS-1$
        builder.append(DBStore.UNSPECIFIED_DATE);
        builder.append(")"); //$NON-NLS-1$
        timeParameters = 2;
      }
      else
      {
        builder.append(CDODBSchema.ATTRIBUTES_REVISED);
        builder.append("="); //$NON-NLS-1$
        builder.append(DBStore.UNSPECIFIED_DATE);
      }
    }

    IRepository repository = accessor.getStore().getRepository();
    CDORevisionManager revisionManager = repository.getRevisionManager();
    CDOBranchManager branchManager = repository.getBranchManager();

    IPreparedStatementCache statementCache = accessor.getStatementCache();
    PreparedStatement stmt = null;
    ResultSet rs = null;

    try
    {
      stmt = statementCache.getPreparedStatement(builder.toString(), ReuseProbability.LOW);

      int col = 1;
      if (branch != null)
      {
        stmt.setInt(col++, branch.getID());
      }

      for (int i = 0; i < timeParameters; i++)
      {
        stmt.setLong(col++, timeStamp);
      }

      rs = stmt.executeQuery();
      while (rs.next())
      {
        long id = rs.getLong(1);
        int version = rs.getInt(2);
        int branchID = rs.getInt(3);

        if (version >= CDOBranchVersion.FIRST_VERSION)
        {
          CDOBranchVersion branchVersion = branchManager.getBranch(branchID).getVersion(Math.abs(version));
          InternalCDORevision revision = (InternalCDORevision)revisionManager.getRevisionByVersion(
              CDOIDUtil.createLong(id), branchVersion, CDORevision.UNCHUNKED, true);

          if (!handler.handleRevision(revision))
          {
            break;
          }
        }
      }
    }
    catch (SQLException e)
    {
      throw new DBException(e);
    }
    finally
    {
      DBUtil.close(rs);
      statementCache.releasePreparedStatement(stmt);
    }
  }

  @Override
  public Set<CDOID> readChangeSet(IDBStoreAccessor accessor, CDOChangeSetSegment[] segments)
  {
    StringBuilder builder = new StringBuilder(sqlSelectForChangeSet);
    boolean isFirst = true;

    for (int i = 0; i < segments.length; i++)
    {
      if (isFirst)
      {
        isFirst = false;
      }
      else
      {
        builder.append(" OR "); //$NON-NLS-1$
      }

      builder.append(CDODBSchema.ATTRIBUTES_BRANCH);
      builder.append("=? AND "); //$NON-NLS-1$

      builder.append(CDODBSchema.ATTRIBUTES_CREATED);
      builder.append(">=?"); //$NON-NLS-1$
      builder.append(" AND ("); //$NON-NLS-1$
      builder.append(CDODBSchema.ATTRIBUTES_REVISED);
      builder.append("<=? OR "); //$NON-NLS-1$
      builder.append(CDODBSchema.ATTRIBUTES_REVISED);
      builder.append("="); //$NON-NLS-1$
      builder.append(DBStore.UNSPECIFIED_DATE);
      builder.append(")"); //$NON-NLS-1$
    }

    IPreparedStatementCache statementCache = accessor.getStatementCache();
    PreparedStatement stmt = null;
    ResultSet rs = null;

    Set<CDOID> result = new HashSet<CDOID>();

    try
    {
      stmt = statementCache.getPreparedStatement(builder.toString(), ReuseProbability.LOW);
      int col = 1;
      for (CDOChangeSetSegment segment : segments)
      {
        stmt.setInt(col++, segment.getBranch().getID());
        stmt.setLong(col++, segment.getTimeStamp());
        stmt.setLong(col++, segment.getEndTime());
      }

      rs = stmt.executeQuery();
      while (rs.next())
      {
        long id = rs.getLong(1);
        result.add(CDOIDUtil.createLong(id));
      }

      return result;
    }
    catch (SQLException e)
    {
      throw new DBException(e);
    }
    finally
    {
      DBUtil.close(rs);
      statementCache.releasePreparedStatement(stmt);
    }
  }

  @Override
  protected String getListXRefsWhere(QueryXRefsContext context)
  {
    StringBuilder builder = new StringBuilder();
    builder.append(CDODBSchema.ATTRIBUTES_BRANCH);
    builder.append("=");
    builder.append(context.getBranch().getID());
    builder.append(" AND (");

    long timeStamp = context.getTimeStamp();
    if (timeStamp == CDORevision.UNSPECIFIED_DATE)
    {
      builder.append(CDODBSchema.ATTRIBUTES_REVISED);
      builder.append("=0)"); //$NON-NLS-1$
    }
    else
    {
      builder.append(CDODBSchema.ATTRIBUTES_CREATED);
      builder.append("<=");
      builder.append(timeStamp);
      builder.append(" AND ("); //$NON-NLS-1$
      builder.append(CDODBSchema.ATTRIBUTES_REVISED);
      builder.append("=0 OR "); //$NON-NLS-1$
      builder.append(CDODBSchema.ATTRIBUTES_REVISED);
      builder.append(">=");
      builder.append(timeStamp);
      builder.append("))"); //$NON-NLS-1$
    }

    return builder.toString();
  }

  public void writeRevisionDelta(IDBStoreAccessor accessor, InternalCDORevisionDelta delta, long created,
      OMMonitor monitor)
  {
    monitor.begin();

    try
    {
      if (accessor.getTransaction().getBranch() != delta.getBranch())
      {
        // new branch -> decide, if branch should be copied
        if (((HorizontalBranchingMappingStrategyWithRanges)getMappingStrategy()).shallCopyOnBranch())
        {
          doCopyOnBranch(accessor, delta, created, monitor.fork());
          return;
        }
      }

      Async async = null;

      try
      {
        async = monitor.forkAsync();
        FeatureDeltaWriter writer = deltaWriter.get();
        writer.process(accessor, delta, created);
      }
      finally
      {
        if (async != null)
        {
          async.stop();
        }
      }
    }
    finally
    {
      monitor.done();
    }
  }

  private void doCopyOnBranch(IDBStoreAccessor accessor, InternalCDORevisionDelta delta, long created, OMMonitor monitor)
  {
    monitor.begin(2);
    try
    {
      InternalRepository repository = (InternalRepository)accessor.getStore().getRepository();

      InternalCDORevision oldRevision = (InternalCDORevision)accessor.getTransaction().getRevision(delta.getID());
      if (oldRevision == null)
      {
        throw new IllegalStateException("Origin revision not found for " + delta);
      }

      // Make sure all chunks are loaded
      for (EStructuralFeature feature : CDOModelUtil.getAllPersistentFeatures(oldRevision.getEClass()))
      {
        if (feature.isMany())
        {
          repository.ensureChunk(oldRevision, feature, 0, oldRevision.getList(feature).size());
        }
      }

      InternalCDORevision newRevision = oldRevision.copy();
      newRevision.adjustForCommit(accessor.getTransaction().getBranch(), created);
      delta.apply(newRevision);
      monitor.worked();
      writeRevision(accessor, newRevision, false, monitor.fork());
    }
    finally
    {
      monitor.done();
    }
  }
}
