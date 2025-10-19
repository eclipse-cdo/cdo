/*
 * Copyright (c) 2010-2016, 2018, 2019, 2021, 2023 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;
import org.eclipse.emf.cdo.common.revision.delta.CDOContainerFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOListFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOUnsetFeatureDelta;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStoreAccessor.QueryXRefsContext;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IIDHandler;
import org.eclipse.emf.cdo.server.db.mapping.IBranchDeletionSupport;
import org.eclipse.emf.cdo.server.db.mapping.IClassMappingAuditSupport;
import org.eclipse.emf.cdo.server.db.mapping.IClassMappingDeltaSupport;
import org.eclipse.emf.cdo.server.db.mapping.IListMapping;
import org.eclipse.emf.cdo.server.db.mapping.IListMappingDeltaSupport;
import org.eclipse.emf.cdo.server.db.mapping.ITypeMapping;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.common.commit.CDOChangeSetSegment;
import org.eclipse.emf.cdo.spi.common.revision.DetachedCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.spi.server.InternalRepository;

import org.eclipse.net4j.db.Batch;
import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBPreparedStatement;
import org.eclipse.net4j.db.IDBPreparedStatement.ReuseProbability;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor.Async;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

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
public class HorizontalBranchingClassMapping extends AbstractHorizontalClassMapping
    implements IClassMappingAuditSupport, IClassMappingDeltaSupport, IBranchDeletionSupport
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, HorizontalBranchingClassMapping.class);

  private String sqlInsertAttributes;

  private String sqlSelectCurrentAttributes;

  private String sqlSelectAllObjectIDs;

  private String sqlSelectAttributesByTime;

  private String sqlSelectAttributesByVersion;

  private String sqlReviseAttributes;

  private String sqlRawDeleteAttributes;

  public HorizontalBranchingClassMapping(AbstractHorizontalMappingStrategy mappingStrategy, EClass eClass)
  {
    super(mappingStrategy, eClass);
  }

  @Override
  protected IDBField addBranchField(IDBTable table)
  {
    return table.addField(MappingNames.ATTRIBUTES_BRANCH, DBType.INTEGER, true);
  }

  @Override
  protected void initSQLStrings()
  {
    super.initSQLStrings();
    IDBTable table = getTable();

    // ----------- Select Revision ---------------------------
    StringBuilder builder = new StringBuilder();
    builder.append("SELECT "); //$NON-NLS-1$
    builder.append(versionField);
    builder.append(", "); //$NON-NLS-1$
    builder.append(createdField);
    builder.append(", "); //$NON-NLS-1$
    builder.append(revisedField);
    builder.append(", "); //$NON-NLS-1$
    builder.append(resourceField);
    builder.append(", "); //$NON-NLS-1$
    builder.append(containerField);
    builder.append(", "); //$NON-NLS-1$
    builder.append(featureField);
    appendTypeMappingNames(builder, getValueMappings());
    appendFieldNames(builder, getUnsettableFields());
    appendFieldNames(builder, getListSizeFields());
    builder.append(" FROM "); //$NON-NLS-1$
    builder.append(table);
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(idField);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(branchField);
    builder.append("=? AND ("); //$NON-NLS-1$
    String sqlSelectAttributesPrefix = builder.toString();
    builder.append(revisedField);
    builder.append("=0)"); //$NON-NLS-1$
    sqlSelectCurrentAttributes = builder.toString();

    builder = new StringBuilder(sqlSelectAttributesPrefix);
    builder.append(createdField);
    builder.append("<=? AND ("); //$NON-NLS-1$
    builder.append(revisedField);
    builder.append("=0 OR "); //$NON-NLS-1$
    builder.append(revisedField);
    builder.append(">=?))"); //$NON-NLS-1$
    sqlSelectAttributesByTime = builder.toString();

    builder = new StringBuilder(sqlSelectAttributesPrefix);
    builder.append("ABS("); //$NON-NLS-1$
    builder.append(versionField);
    builder.append(")=?)"); //$NON-NLS-1$
    sqlSelectAttributesByVersion = builder.toString();

    // ----------- Insert Attributes -------------------------
    builder = new StringBuilder();
    builder.append("INSERT INTO "); //$NON-NLS-1$
    builder.append(table);
    builder.append("("); //$NON-NLS-1$
    builder.append(idField);
    builder.append(", "); //$NON-NLS-1$
    builder.append(versionField);
    builder.append(", "); //$NON-NLS-1$
    builder.append(branchField);
    builder.append(", "); //$NON-NLS-1$
    builder.append(createdField);
    builder.append(", "); //$NON-NLS-1$
    builder.append(revisedField);
    builder.append(", "); //$NON-NLS-1$
    builder.append(resourceField);
    builder.append(", "); //$NON-NLS-1$
    builder.append(containerField);
    builder.append(", "); //$NON-NLS-1$
    builder.append(featureField);
    appendTypeMappingNames(builder, getValueMappings());
    appendFieldNames(builder, getUnsettableFields());
    appendFieldNames(builder, getListSizeFields());
    builder.append(") VALUES (?, ?, ?, ?, ?, ?, ?, ?"); //$NON-NLS-1$
    appendTypeMappingParameters(builder, getValueMappings());
    appendFieldParameters(builder, getUnsettableFields());
    appendFieldParameters(builder, getListSizeFields());
    builder.append(")"); //$NON-NLS-1$
    sqlInsertAttributes = builder.toString();

    // ----------- Update to set revised ----------------
    builder = new StringBuilder("UPDATE "); //$NON-NLS-1$
    builder.append(table);
    builder.append(" SET "); //$NON-NLS-1$
    builder.append(revisedField);
    builder.append("=? WHERE "); //$NON-NLS-1$
    builder.append(idField);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(branchField);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(revisedField);
    builder.append("=0"); //$NON-NLS-1$
    sqlReviseAttributes = builder.toString();

    // ----------- Select all unrevised Object IDs ------
    builder = new StringBuilder("SELECT "); //$NON-NLS-1$
    builder.append(idField);
    builder.append(" FROM "); //$NON-NLS-1$
    builder.append(table);
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(revisedField);
    builder.append("=0"); //$NON-NLS-1$
    sqlSelectAllObjectIDs = builder.toString();

    // ----------- Raw delete one specific revision ------
    builder = new StringBuilder("DELETE FROM "); //$NON-NLS-1$
    builder.append(table);
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(idField);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(branchField);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(versionField);
    builder.append("=?"); //$NON-NLS-1$
    sqlRawDeleteAttributes = builder.toString();
  }

  @Override
  protected void appendSelectForHandleFields(StringBuilder builder)
  {
    builder.append(", "); //$NON-NLS-1$
    builder.append(branchField);
    builder.append(", "); //$NON-NLS-1$
    builder.append(createdField);
    builder.append(", "); //$NON-NLS-1$
    builder.append(revisedField);
  }

  @Override
  public boolean readRevision(IDBStoreAccessor accessor, InternalCDORevision revision, int listChunk)
  {
    long timeStamp = revision.getTimeStamp();
    int branchID = revision.getBranch().getID();

    IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
    IDBPreparedStatement stmt = null;
    boolean success = false;

    try
    {
      if (timeStamp != CDOBranchPoint.UNSPECIFIED_DATE)
      {
        stmt = accessor.getDBConnection().prepareStatement(sqlSelectAttributesByTime, ReuseProbability.MEDIUM);
        idHandler.setCDOID(stmt, 1, revision.getID());
        stmt.setInt(2, branchID);
        stmt.setLong(3, timeStamp);
        stmt.setLong(4, timeStamp);
      }
      else
      {
        stmt = accessor.getDBConnection().prepareStatement(sqlSelectCurrentAttributes, ReuseProbability.HIGH);
        idHandler.setCDOID(stmt, 1, revision.getID());
        stmt.setInt(2, branchID);
      }

      // Read singleval-attribute table always (even without modeled attributes!)
      success = readValuesFromStatement(stmt, revision, accessor);
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      DBUtil.close(stmt);
    }

    // Read multival tables only if revision exists
    if (success && revision.getVersion() >= CDOBranchVersion.FIRST_VERSION)
    {
      readLists(accessor, revision, listChunk);
    }

    return success;
  }

  @Override
  public boolean readRevisionByVersion(IDBStoreAccessor accessor, InternalCDORevision revision, int listChunk)
  {
    IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
    IDBPreparedStatement stmt = accessor.getDBConnection().prepareStatement(sqlSelectAttributesByVersion, ReuseProbability.HIGH);
    boolean success = false;

    try
    {
      idHandler.setCDOID(stmt, 1, revision.getID());
      stmt.setInt(2, revision.getBranch().getID());
      stmt.setInt(3, revision.getVersion());

      // Read singleval-attribute table always (even without modeled attributes!)
      success = readValuesFromStatement(stmt, revision, accessor);
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      DBUtil.close(stmt);
    }

    // Read multival tables only if revision exists
    if (success)
    {
      readLists(accessor, revision, listChunk);
    }

    return success;
  }

  @Override
  public IDBPreparedStatement createResourceQueryStatement(IDBStoreAccessor accessor, CDOID folderId, String name, boolean exactMatch,
      CDOBranchPoint branchPoint)
  {
    IDBTable table = getTable();
    if (table == null)
    {
      return null;
    }

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
    builder.append(idField);
    builder.append(" FROM "); //$NON-NLS-1$
    builder.append(table);
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(versionField);
    builder.append(">0 AND "); //$NON-NLS-1$
    builder.append(branchField);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(containerField);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(nameValueMapping.getField());
    if (name == null)
    {
      builder.append(" IS NULL"); //$NON-NLS-1$
    }
    else
    {
      builder.append(exactMatch ? " =?" : " LIKE ?"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    builder.append(" AND ("); //$NON-NLS-1$

    if (timeStamp == CDORevision.UNSPECIFIED_DATE)
    {
      builder.append(revisedField);
      builder.append("=0)"); //$NON-NLS-1$
    }
    else
    {
      builder.append(createdField);
      builder.append("<=? AND ("); //$NON-NLS-1$
      builder.append(revisedField);
      builder.append("=0 OR "); //$NON-NLS-1$
      builder.append(revisedField);
      builder.append(">=?))"); //$NON-NLS-1$
    }

    IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
    IDBPreparedStatement stmt = accessor.getDBConnection().prepareStatement(builder.toString(), ReuseProbability.MEDIUM);

    try
    {
      int column = 1;
      stmt.setInt(column++, branchID);
      idHandler.setCDOID(stmt, column++, folderId);

      if (name != null)
      {
        String queryName = exactMatch ? name : name + "%"; //$NON-NLS-1$
        nameValueMapping.setValue(stmt, column++, queryName);
      }

      if (timeStamp != CDORevision.UNSPECIFIED_DATE)
      {
        stmt.setLong(column++, timeStamp);
        stmt.setLong(column++, timeStamp);
      }

      if (TRACER.isEnabled())
      {
        TRACER.format("Created Resource Query: {0}", stmt.toString()); //$NON-NLS-1$
      }

      return stmt;
    }
    catch (Throwable ex)
    {
      DBUtil.close(stmt); // only release on error
      throw new DBException(ex);
    }
  }

  @Override
  public IDBPreparedStatement createObjectIDStatement(IDBStoreAccessor accessor)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Created ObjectID Statement : {0}", sqlSelectAllObjectIDs); //$NON-NLS-1$
    }

    return accessor.getDBConnection().prepareStatement(sqlSelectAllObjectIDs, ReuseProbability.HIGH);
  }

  @Override
  protected final void writeValues(IDBStoreAccessor accessor, InternalCDORevision revision)
  {
    IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
    IDBPreparedStatement stmt = accessor.getDBConnection().prepareStatement(sqlInsertAttributes, ReuseProbability.HIGH);

    try
    {
      int column = 1;
      idHandler.setCDOID(stmt, column++, revision.getID());
      stmt.setInt(column++, revision.getVersion());
      stmt.setInt(column++, revision.getBranch().getID());
      stmt.setLong(column++, revision.getTimeStamp());
      stmt.setLong(column++, revision.getRevised());
      idHandler.setCDOID(stmt, column++, revision.getResourceID());
      idHandler.setCDOID(stmt, column++, (CDOID)revision.getContainerID());
      stmt.setInt(column++, revision.getContainerFeatureID());

      int isSetCol = column + getValueMappings().size();

      for (ITypeMapping mapping : getValueMappings())
      {
        EStructuralFeature feature = mapping.getFeature();
        if (feature.isUnsettable())
        {
          if (revision.getValue(feature) == null)
          {
            stmt.setBoolean(isSetCol++, false);

            // also set value column to default value
            mapping.setDefaultValue(stmt, column++);
            continue;
          }

          stmt.setBoolean(isSetCol++, true);
        }

        mapping.setValueFromRevision(stmt, column++, revision);
      }

      Map<EStructuralFeature, IDBField> listSizeFields = getListSizeFields();
      if (listSizeFields != null)
      {
        // isSetCol now points to the first listTableSize-column
        column = isSetCol;

        for (EStructuralFeature feature : listSizeFields.keySet())
        {
          CDOList list = revision.getListOrNull(feature);
          int size = list == null ? UNSET_LIST : list.size();
          stmt.setInt(column++, size);
        }
      }

      DBUtil.update(stmt, true);
    }
    catch (SQLException e)
    {
      throw new DBException(e);
    }
    finally
    {
      DBUtil.close(stmt);
    }
  }

  @Override
  protected void detachAttributes(IDBStoreAccessor accessor, CDOID id, int version, CDOBranch branch, long timeStamp, OMMonitor mon)
  {
    IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
    IDBPreparedStatement stmt = accessor.getDBConnection().prepareStatement(sqlInsertAttributes, ReuseProbability.HIGH);

    try
    {
      int column = 1;
      idHandler.setCDOID(stmt, column++, id);
      stmt.setInt(column++, -version); // cdo_version
      stmt.setInt(column++, branch.getID());
      stmt.setLong(column++, timeStamp); // cdo_created
      stmt.setLong(column++, CDOBranchPoint.UNSPECIFIED_DATE); // cdo_revised
      idHandler.setCDOID(stmt, column++, CDOID.NULL); // resource
      idHandler.setCDOID(stmt, column++, CDOID.NULL); // container
      stmt.setInt(column++, 0); // containing feature ID

      int isSetCol = column + getValueMappings().size();

      for (ITypeMapping mapping : getValueMappings())
      {
        EStructuralFeature feature = mapping.getFeature();
        if (feature.isUnsettable())
        {
          stmt.setBoolean(isSetCol++, false);
        }

        mapping.setDefaultValue(stmt, column++);
      }

      Map<EStructuralFeature, IDBField> listSizeFields = getListSizeFields();
      if (listSizeFields != null)
      {
        // list size columns begin after isSet-columns
        column = isSetCol;

        for (int i = 0; i < listSizeFields.size(); i++)
        {
          stmt.setInt(column++, 0);
        }
      }

      DBUtil.update(stmt, true);
    }
    catch (SQLException e)
    {
      throw new DBException(e);
    }
    finally
    {
      DBUtil.close(stmt);
    }
  }

  @Override
  protected void rawDeleteAttributes(IDBStoreAccessor accessor, CDOID id, CDOBranch branch, int version, OMMonitor fork)
  {
    IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
    IDBPreparedStatement stmt = accessor.getDBConnection().prepareStatement(sqlRawDeleteAttributes, ReuseProbability.HIGH);

    try
    {
      idHandler.setCDOID(stmt, 1, id);
      stmt.setInt(2, branch.getID());
      stmt.setInt(3, version);
      DBUtil.update(stmt, false);
    }
    catch (SQLException e)
    {
      throw new DBException(e);
    }
    finally
    {
      DBUtil.close(stmt);
    }
  }

  @Override
  protected void reviseOldRevision(IDBStoreAccessor accessor, CDOID id, CDOBranch branch, long revised)
  {
    IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
    IDBPreparedStatement stmt = accessor.getDBConnection().prepareStatement(sqlReviseAttributes, ReuseProbability.HIGH);

    try
    {
      stmt.setLong(1, revised);
      idHandler.setCDOID(stmt, 2, id);
      stmt.setInt(3, branch.getID());
      DBUtil.update(stmt, false); // No row affected if old revision from other branch!
    }
    catch (SQLException e)
    {
      throw new DBException(e);
    }
    finally
    {
      DBUtil.close(stmt);
    }
  }

  @Override
  public void writeRevision(IDBStoreAccessor accessor, InternalCDORevision revision, boolean firstRevision, boolean revise, OMMonitor monitor)
  {
    if (getTable() == null)
    {
      initTable(accessor);
    }

    CDOID id = revision.getID();
    int version = revision.getVersion();
    InternalCDOBranch branch = revision.getBranch();
    long timeStamp = revision.getTimeStamp();

    // A DetachedCDORevision can only come from DBStoreAccessor.rawStore().
    if (revision instanceof DetachedCDORevision)
    {
      detachAttributes(accessor, id, version, branch, timeStamp, monitor);

      long revised = revision.getRevised();
      if (revised != CDOBranchPoint.UNSPECIFIED_DATE)
      {
        reviseOldRevision(accessor, id, branch, revised);
      }

      return;
    }

    // If the repository's root resource ID is not yet set, then this must be the initial initRootResource()
    // commit. The duplicate check is certainly not needed in this case, and it appears that Mysql has problems
    // with it (Table definition has changed, please retry transaction), see bug 482886.
    boolean duplicateResourcesCheckNeeded = revision.isResourceNode() && getMappingStrategy().getStore().getRepository().getRootResourceID() != null;

    monitor.begin(duplicateResourcesCheckNeeded ? 10 : 9);
    Async async = null;

    try
    {
      try
      {
        async = monitor.forkAsync();
        if (firstRevision)
        {
          // Put new objects into objectTypeMapper
          EClass eClass = getEClass();

          AbstractHorizontalMappingStrategy mappingStrategy = getMappingStrategy();
          if (!mappingStrategy.putObjectType(accessor, timeStamp, id, eClass))
          {
            firstRevision = false;
          }
        }

        if (!firstRevision && revise && version > CDOBranchVersion.FIRST_VERSION)
        {
          // If revision is not the first one, revise the old revision
          long revised = timeStamp - 1;

          reviseOldRevision(accessor, id, branch, revised);
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

      if (duplicateResourcesCheckNeeded)
      {
        try
        {
          async = monitor.forkAsync();
          checkDuplicateResources(accessor, revision);
        }
        finally
        {
          if (async != null)
          {
            async.stop();
          }
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
          writeLists(accessor, revision, firstRevision, !revise);
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
  public void handleRevisions(IDBStoreAccessor accessor, CDOBranch branch, long timeStamp, boolean exactTime, CDORevisionHandler handler)
  {
    if (getTable() == null)
    {
      return;
    }

    StringBuilder builder = new StringBuilder(getSQLSelectForHandle());
    boolean whereAppend = false;

    if (branch != null)
    {
      // TODO: Prepare this string literal
      builder.append(" WHERE "); //$NON-NLS-1$
      builder.append(branchField);
      builder.append("=?"); //$NON-NLS-1$

      whereAppend = true;
    }

    int timeParameters = 0;
    if (timeStamp != CDOBranchPoint.INVALID_DATE)
    {
      if (exactTime)
      {
        if (timeStamp != CDOBranchPoint.UNSPECIFIED_DATE)
        {
          builder.append(whereAppend ? " AND " : " WHERE "); //$NON-NLS-1$ //$NON-NLS-2$
          builder.append(createdField);
          builder.append("=?"); //$NON-NLS-1$
          timeParameters = 1;
        }
      }
      else
      {
        builder.append(whereAppend ? " AND " : " WHERE "); //$NON-NLS-1$ //$NON-NLS-2$
        if (timeStamp != CDOBranchPoint.UNSPECIFIED_DATE)
        {
          builder.append(createdField);
          builder.append("<=? AND ("); //$NON-NLS-1$
          builder.append(revisedField);
          builder.append("=0 OR "); //$NON-NLS-1$
          builder.append(revisedField);
          builder.append(">=?)"); //$NON-NLS-1$
          timeParameters = 2;
        }
        else
        {
          builder.append(revisedField);
          builder.append("=0"); //$NON-NLS-1$
        }
      }
    }

    builder.append(" ORDER BY "); //$NON-NLS-1$
    builder.append(idField);
    builder.append(", "); //$NON-NLS-1$
    builder.append(versionField);
    builder.append(", "); //$NON-NLS-1$
    builder.append(branchField);

    IRepository repository = accessor.getStore().getRepository();
    CDORevisionManager revisionManager = repository.getRevisionManager();
    CDOBranchManager branchManager = repository.getBranchManager();

    IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
    IDBPreparedStatement stmt = accessor.getDBConnection().prepareStatement(builder.toString(), ReuseProbability.LOW);
    ResultSet resultSet = null;

    try
    {
      int column = 1;
      if (branch != null)
      {
        stmt.setInt(column++, branch.getID());
      }

      for (int i = 0; i < timeParameters; i++)
      {
        stmt.setLong(column++, timeStamp);
      }

      resultSet = stmt.executeQuery();
      while (resultSet.next())
      {
        CDOID id = idHandler.getCDOID(resultSet, 1);
        int version = resultSet.getInt(2);
        CDOBranch revisionBranch = branchManager.getBranch(resultSet.getInt(3));

        if (version >= CDOBranchVersion.FIRST_VERSION)
        {
          CDOBranchVersion branchVersion = revisionBranch.getVersion(version);
          InternalCDORevision revision = (InternalCDORevision)revisionManager.getRevisionByVersion(id, branchVersion, CDORevision.UNCHUNKED, true);

          if (!handler.handleRevision(revision))
          {
            break;
          }
        }
        else
        {
          EClass eClass = getEClass();
          long created = resultSet.getLong(4);
          long revised = resultSet.getLong(5);

          // Tell handler about detached IDs
          InternalCDORevision revision = new DetachedCDORevision(eClass, id, revisionBranch, -version, created, revised);
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
      DBUtil.close(resultSet);
      DBUtil.close(stmt);
    }
  }

  @Override
  public void deleteBranches(IDBStoreAccessor accessor, Batch batch, String idList)
  {
    IDBTable table = getTable();
    if (table == null)
    {
      return;
    }

    // Delete the revisions.
    batch.add("DELETE FROM " + table + " WHERE " + branchField + " IN (" + idList + ")");

    // Delete the type mappings.
    ObjectTypeTable objects = getMappingStrategy().objects();
    String metaID = getMetaIDStr();

    String join = "SELECT o." + objects.id() + " FROM " + objects + " o LEFT JOIN " + table + " c ON c." + objects.id() + "=o." + objects.id() + " WHERE o."
        + objects.clazz() + "=" + metaID + " AND c." + objects.id() + " IS NULL";

    batch.add("DELETE FROM " + objects + " WHERE " + objects.id() + " IN (" + join + ")");

    // Delete the list elements.
    for (IListMapping listMapping : getListMappings())
    {
      if (listMapping instanceof IBranchDeletionSupport)
      {
        ((IBranchDeletionSupport)listMapping).deleteBranches(accessor, batch, idList);
      }
    }
  }

  @Override
  public Set<CDOID> readChangeSet(IDBStoreAccessor accessor, CDOChangeSetSegment[] segments)
  {
    Set<CDOID> result = new HashSet<>();
    if (getTable() == null)
    {
      return result;
    }

    StringBuilder builder = new StringBuilder(getSQLSelectForChangeSet());
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

      builder.append(branchField);
      builder.append("=? AND "); //$NON-NLS-1$

      builder.append(createdField);
      builder.append(">=?"); //$NON-NLS-1$
      builder.append(" AND ("); //$NON-NLS-1$
      builder.append(revisedField);
      builder.append("<=? OR "); //$NON-NLS-1$
      builder.append(revisedField);
      builder.append("=0)"); //$NON-NLS-1$
    }

    String sql = builder.toString();

    IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
    IDBPreparedStatement stmt = accessor.getDBConnection().prepareStatement(sql, ReuseProbability.LOW);
    ResultSet resultSet = null;

    try
    {
      int column = 1;
      for (CDOChangeSetSegment segment : segments)
      {
        stmt.setInt(column++, segment.getBranch().getID());
        stmt.setLong(column++, segment.getTimeStamp());
        stmt.setLong(column++, segment.getEndTime());
      }

      resultSet = stmt.executeQuery();
      while (resultSet.next())
      {
        CDOID id = idHandler.getCDOID(resultSet, 1);
        result.add(id);
      }

      return result;
    }
    catch (SQLException e)
    {
      throw new DBException(e);
    }
    finally
    {
      DBUtil.close(resultSet);
      DBUtil.close(stmt);
    }
  }

  @Override
  protected String getListXRefsWhere(QueryXRefsContext context)
  {
    StringBuilder builder = new StringBuilder();
    builder.append(branchField);
    builder.append("=");
    builder.append(context.getBranch().getID());
    builder.append(" AND (");

    long timeStamp = context.getTimeStamp();
    if (timeStamp == CDORevision.UNSPECIFIED_DATE)
    {
      builder.append(revisedField);
      builder.append("=0)"); //$NON-NLS-1$
    }
    else
    {
      builder.append(createdField);
      builder.append("<=");
      builder.append(timeStamp);
      builder.append(" AND ("); //$NON-NLS-1$
      builder.append(revisedField);
      builder.append("=0 OR "); //$NON-NLS-1$
      builder.append(revisedField);
      builder.append(">=");
      builder.append(timeStamp);
      builder.append("))"); //$NON-NLS-1$
    }

    return builder.toString();
  }

  @Override
  public void writeRevisionDelta(IDBStoreAccessor accessor, InternalCDORevisionDelta delta, long created, OMMonitor monitor)
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

        FeatureDeltaWriter writer = new FeatureDeltaWriter();
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
      repository.ensureChunks(oldRevision, CDORevision.UNCHUNKED);

      InternalCDORevision newRevision = oldRevision.copy();
      newRevision.adjustForCommit(accessor.getTransaction().getBranch(), created);
      delta.applyTo(newRevision);
      monitor.worked();
      writeRevision(accessor, newRevision, false, true, monitor.fork());
    }
    finally
    {
      monitor.done();
    }
  }

  /**
   * @author Stefan Winkler
   */
  private final class FeatureDeltaWriter extends AbstractFeatureDeltaWriter
  {
    private CDOBranch targetBranch;

    private int oldVersion;

    private int newVersion;

    private InternalCDORevision newRevision;

    @Override
    protected void doProcess(InternalCDORevisionDelta delta)
    {
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

    @Override
    public void visit(CDOSetFeatureDelta delta)
    {
      delta.applyTo(newRevision);
    }

    @Override
    public void visit(CDOUnsetFeatureDelta delta)
    {
      delta.applyTo(newRevision);
    }

    @Override
    public void visit(CDOListFeatureDelta delta)
    {
      delta.applyTo(newRevision);

      IListMappingDeltaSupport listMapping = (IListMappingDeltaSupport)getListMapping(delta.getFeature());
      listMapping.processDelta(accessor, id, targetBranch.getID(), oldVersion, newVersion, created, delta);
    }

    @Override
    public void visit(CDOContainerFeatureDelta delta)
    {
      delta.applyTo(newRevision);
    }
  }
}
