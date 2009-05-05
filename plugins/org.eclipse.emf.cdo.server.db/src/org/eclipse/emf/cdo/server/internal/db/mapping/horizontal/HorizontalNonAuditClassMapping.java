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
 */
package org.eclipse.emf.cdo.server.internal.db.mapping.horizontal;

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
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOUnsetFeatureDelta;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IPreparedStatementCache.ReuseProbability;
import org.eclipse.emf.cdo.server.db.mapping.IClassMapping;
import org.eclipse.emf.cdo.server.db.mapping.IClassMappingDeltaSupport;
import org.eclipse.emf.cdo.server.db.mapping.IListMappingDeltaSupport;
import org.eclipse.emf.cdo.server.db.mapping.ITypeMapping;
import org.eclipse.emf.cdo.server.internal.db.CDODBSchema;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor.Async;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class HorizontalNonAuditClassMapping extends AbstractHorizontalClassMapping implements IClassMapping,
    IClassMappingDeltaSupport
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, HorizontalNonAuditClassMapping.class);

  private String sqlSelectAllObjectIds;

  private String sqlSelectCurrentAttributes;

  private String sqlInsertAttributes;

  private String sqlDelete;

  private String sqlUpdateAffix;

  private String sqlUpdatePrexix;

  private String sqlUpdateContainerPart;

  private ThreadLocal<FeatureDeltaWriter> deltaWriter = new ThreadLocal<FeatureDeltaWriter>()
  {
    @Override
    protected FeatureDeltaWriter initialValue()
    {
      return new FeatureDeltaWriter();
    };
  };

  public HorizontalNonAuditClassMapping(AbstractHorizontalMappingStrategy mappingStrategy, EClass eClass)
  {
    super(mappingStrategy, eClass);

    initSqlStrings();
  }

  private void initSqlStrings()
  {
    // ----------- Select Revision ---------------------------
    StringBuilder builder = new StringBuilder();

    builder.append("SELECT ");
    builder.append(CDODBSchema.ATTRIBUTES_VERSION);
    builder.append(", ");
    builder.append(CDODBSchema.ATTRIBUTES_CREATED);
    builder.append(", ");
    builder.append(CDODBSchema.ATTRIBUTES_REVISED);
    builder.append(", ");
    builder.append(CDODBSchema.ATTRIBUTES_RESOURCE);
    builder.append(", ");
    builder.append(CDODBSchema.ATTRIBUTES_CONTAINER);
    builder.append(", ");
    builder.append(CDODBSchema.ATTRIBUTES_FEATURE);

    for (ITypeMapping singleMapping : getValueMappings())
    {
      builder.append(", ");
      builder.append(singleMapping.getField().getName());
    }

    builder.append(" FROM ");
    builder.append(getTable().getName());
    builder.append(" WHERE ");
    builder.append(CDODBSchema.ATTRIBUTES_ID);
    builder.append("= ?");

    sqlSelectCurrentAttributes = builder.toString();

    // ----------- Insert Attributes -------------------------
    builder = new StringBuilder();
    builder.append("INSERT INTO ");
    builder.append(getTable().getName());
    builder.append(" VALUES (?, ?, ");
    builder.append("?, ?, ?, ?, ?, ?");
    for (int i = 0; i < getValueMappings().size(); i++)
    {
      builder.append(", ?");
    }

    builder.append(")");
    sqlInsertAttributes = builder.toString();

    builder = new StringBuilder("DELETE FROM ");
    builder.append(getTable().getName());
    builder.append(" WHERE ");
    builder.append(CDODBSchema.ATTRIBUTES_ID);
    builder.append(" = ? ");
    sqlDelete = builder.toString();

    // ----------- Select all unrevised Object IDs ------
    builder = new StringBuilder("SELECT ");
    builder.append(CDODBSchema.ATTRIBUTES_ID);
    builder.append(" FROM ");
    builder.append(getTable().getName());
    sqlSelectAllObjectIds = builder.toString();

    // ----------- Update attributes --------------------
    builder = new StringBuilder("UPDATE ");
    builder.append(getTable().getName());
    builder.append(" SET ");
    builder.append(CDODBSchema.ATTRIBUTES_VERSION);
    builder.append(" =? ,");
    builder.append(CDODBSchema.ATTRIBUTES_CREATED);
    builder.append(" =? ");
    sqlUpdatePrexix = builder.toString();

    builder = new StringBuilder(", ");
    builder.append(CDODBSchema.ATTRIBUTES_RESOURCE);
    builder.append(" =? ,");
    builder.append(CDODBSchema.ATTRIBUTES_CONTAINER);
    builder.append(" =? ,");
    builder.append(CDODBSchema.ATTRIBUTES_FEATURE);
    builder.append(" =? ");
    sqlUpdateContainerPart = builder.toString();

    builder = new StringBuilder(" WHERE ");
    builder.append(CDODBSchema.ATTRIBUTES_ID);
    builder.append(" = ? ");
    sqlUpdateAffix = builder.toString();
  }

  @Override
  protected void writeValues(IDBStoreAccessor accessor, InternalCDORevision revision)
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
      stmt.setLong(col++, CDOIDUtil.getLong(revision.getResourceID()));
      stmt.setLong(col++, CDODBUtil.getLong((CDOID)revision.getContainerID()));
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

  public PreparedStatement createObjectIdStatement(IDBStoreAccessor accessor)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Created ObjectID Statement : {0}", sqlSelectAllObjectIds);
    }

    return accessor.getStatementCache().getPreparedStatement(sqlSelectAllObjectIds, ReuseProbability.HIGH);
  }

  public PreparedStatement createResourceQueryStatement(IDBStoreAccessor accessor, CDOID folderId, String name,
      boolean exactMatch, long timeStamp)
  {
    if (timeStamp != CDORevision.UNSPECIFIED_DATE)
    {
      throw new IllegalArgumentException("Non-audit store does not support explicit timeStamp in resource query");
    }

    EStructuralFeature nameFeature = EresourcePackage.eINSTANCE.getCDOResourceNode_Name();

    ITypeMapping nameValueMapping = getValueMapping(nameFeature);
    if (nameValueMapping == null)
    {
      throw new ImplementationError(nameFeature + " not found in ClassMapping " + this);
    }

    StringBuilder builder = new StringBuilder();
    builder.append("SELECT ");
    builder.append(CDODBSchema.ATTRIBUTES_ID);
    builder.append(" FROM ");
    builder.append(getTable().getName());
    builder.append(" WHERE ");
    builder.append(CDODBSchema.ATTRIBUTES_CONTAINER);
    builder.append("= ? AND ");
    builder.append(nameValueMapping.getField().getName());
    if (name == null)
    {
      builder.append(" IS NULL");
    }
    else
    {
      builder.append(exactMatch ? " = ? " : " LIKE ? ");
    }

    PreparedStatement pstmt = null;
    try
    {
      int idx = 1;

      pstmt = accessor.getStatementCache().getPreparedStatement(builder.toString(), ReuseProbability.MEDIUM);
      pstmt.setLong(idx++, CDOIDUtil.getLong(folderId));

      if (name != null)
      {
        String queryName = exactMatch ? name : name + "%";
        nameValueMapping.setValue(pstmt, idx++, queryName);
      }

      if (TRACER.isEnabled())
      {
        TRACER.format("Created Resource Query: {0}", pstmt.toString());
      }

      return pstmt;
    }
    catch (SQLException ex)
    {
      accessor.getStatementCache().releasePreparedStatement(pstmt); // only release on error
      throw new DBException(ex);
    }
  }

  public boolean readRevision(IDBStoreAccessor accessor, InternalCDORevision revision, int listChunk)
  {
    PreparedStatement pstmt = null;
    try
    {
      // TODO add caching
      pstmt = accessor.getStatementCache().getPreparedStatement(sqlSelectCurrentAttributes, ReuseProbability.HIGH);
      pstmt.setLong(1, CDOIDUtil.getLong(revision.getID()));

      // Read singleval-attribute table always (even without modeled attributes!)
      boolean success = readValuesFromStatement(pstmt, revision);

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
  protected void reviseObject(IDBStoreAccessor accessor, CDOID id, long revised)
  {
    PreparedStatement stmt = null;

    try
    {
      stmt = accessor.getStatementCache().getPreparedStatement(sqlDelete, ReuseProbability.HIGH);
      stmt.setLong(1, CDOIDUtil.getLong(id));
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

  private class FeatureDeltaWriter implements CDOFeatureDeltaVisitor
  {
    private CDOID id;

    private int oldVersion;

    private int newVersion;

    private long created;

    private IDBStoreAccessor accessor;

    private boolean updateContainer = false;

    private List<Pair<ITypeMapping, Object>> attributeChanges;

    private int newContainingFeatureID;

    private CDOID newContainerID;

    private CDOID newResourceID;

    public FeatureDeltaWriter()
    {
      attributeChanges = new ArrayList<Pair<ITypeMapping, Object>>();
    }

    protected void reset()
    {
      attributeChanges.clear();
      updateContainer = false;
    }

    public void process(IDBStoreAccessor a, CDORevisionDelta d, long c)
    {
      // set context

      reset();
      id = d.getID();
      oldVersion = d.getOriginVersion();
      newVersion = d.getDirtyVersion();
      created = c;
      accessor = a;

      // process revision delta tree
      d.accept(this);

      // update attributes
      if (updateContainer)
      {
        updateAttributes(accessor, id, newVersion, created, newContainerID, newContainingFeatureID, newResourceID,
            attributeChanges);
      }
      else
      {
        updateAttributes(accessor, id, newVersion, created, attributeChanges);
      }
    }

    public void visit(CDOMoveFeatureDelta delta)
    {
      throw new ImplementationError("Should not be called");
    }

    public void visit(CDOSetFeatureDelta delta)
    {
      if (delta.getFeature().isMany())
      {
        throw new ImplementationError("Should not be called");
      }

      ITypeMapping am = getValueMapping(delta.getFeature());
      if (am == null)
      {
        throw new IllegalArgumentException("AttributeMapping for " + delta.getFeature() + " is null!");
      }

      attributeChanges.add(new Pair<ITypeMapping, Object>(am, delta.getValue()));
    }

    public void visit(CDOUnsetFeatureDelta delta)
    {
      // TODO: correct this when DBStore implements unsettable features
      // see Bugs 259868 and 263010
      ITypeMapping tm = getValueMapping(delta.getFeature());
      attributeChanges.add(new Pair<ITypeMapping, Object>(tm, null));
    }

    public void visit(CDOListFeatureDelta delta)
    {
      IListMappingDeltaSupport listMapping = (IListMappingDeltaSupport)getListMapping(delta.getFeature());
      listMapping.processDelta(accessor, id, oldVersion, newVersion, created, delta);
    }

    public void visit(CDOClearFeatureDelta delta)
    {
      throw new ImplementationError("Should not be called");
    }

    public void visit(CDOAddFeatureDelta delta)
    {
      throw new ImplementationError("Should not be called");
    }

    public void visit(CDORemoveFeatureDelta delta)
    {
      throw new ImplementationError("Should not be called");
    }

    public void visit(CDOContainerFeatureDelta delta)
    {
      newContainingFeatureID = delta.getContainerFeatureID();
      newContainerID = (CDOID)delta.getContainerID();
      newResourceID = delta.getResourceID();
      updateContainer = true;
    }
  }

  public void updateAttributes(IDBStoreAccessor accessor, CDOID id, int newVersion, long created, CDOID newContainerId,
      int newContainingFeatureId, CDOID newResourceId, List<Pair<ITypeMapping, Object>> attributeChanges)
  {
    PreparedStatement stmt = null;

    try
    {
      stmt = accessor.getStatementCache().getPreparedStatement(buildUpdateStatement(attributeChanges, true),
          ReuseProbability.MEDIUM);

      int col = 1;

      stmt.setInt(col++, newVersion);
      stmt.setLong(col++, created);
      stmt.setLong(col++, CDODBUtil.getLong(newResourceId));
      stmt.setLong(col++, CDODBUtil.getLong(newContainerId));
      stmt.setInt(col++, newContainingFeatureId);

      for (Pair<ITypeMapping, Object> change : attributeChanges)
      {
        change.getElement1().setValue(stmt, col++, change.getElement2());
      }

      stmt.setLong(col++, CDOIDUtil.getLong(id));

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

  public void updateAttributes(IDBStoreAccessor accessor, CDOID id, int newVersion, long created,
      List<Pair<ITypeMapping, Object>> attributeChanges)
  {
    PreparedStatement stmt = null;

    try
    {
      stmt = accessor.getStatementCache().getPreparedStatement(buildUpdateStatement(attributeChanges, false),
          ReuseProbability.MEDIUM);

      int col = 1;

      stmt.setInt(col++, newVersion);
      stmt.setLong(col++, created);

      for (Pair<ITypeMapping, Object> change : attributeChanges)
      {
        change.getElement1().setValue(stmt, col++, change.getElement2());
      }

      stmt.setLong(col++, CDOIDUtil.getLong(id));

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

  private String buildUpdateStatement(List<Pair<ITypeMapping, Object>> attributeChanges, boolean withContainment)
  {
    StringBuilder builder = new StringBuilder(sqlUpdatePrexix);
    if (withContainment)
    {
      builder.append(sqlUpdateContainerPart);
    }

    for (Pair<ITypeMapping, Object> change : attributeChanges)
    {
      builder.append(", ");
      builder.append(change.getElement1().getField().getName());
      builder.append(" =? ");
    }

    builder.append(sqlUpdateAffix);
    return builder.toString();
  }
}
