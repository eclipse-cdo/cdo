/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - https://bugs.eclipse.org/bugs/show_bug.cgi?id=259402
 */
package org.eclipse.emf.cdo.server.internal.db.mapping;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOType;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDOAddFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOClearFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOContainerFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDeltaVisitor;
import org.eclipse.emf.cdo.common.revision.delta.CDOListFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOMoveFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORemoveFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOUnsetFeatureDelta;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.mapping.IAttributeMapping;
import org.eclipse.emf.cdo.server.db.mapping.IClassMapping;
import org.eclipse.emf.cdo.server.db.mapping.IFeatureMapping;
import org.eclipse.emf.cdo.server.db.mapping.IReferenceMapping;
import org.eclipse.emf.cdo.server.internal.db.CDODBSchema;
import org.eclipse.emf.cdo.server.internal.db.DBStore;
import org.eclipse.emf.cdo.server.internal.db.ToMany;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBIndex;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor.Async;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public abstract class ClassMapping implements IClassMapping
{
  private MappingStrategy mappingStrategy;

  private EClass eClass;

  private IDBTable table;

  private Set<IDBTable> affectedTables = new HashSet<IDBTable>();

  private List<IAttributeMapping> attributeMappings;

  private List<IReferenceMapping> referenceMappings;

  private ThreadLocal<FeatureDeltaWriter> deltaWriter = new ThreadLocal<FeatureDeltaWriter>()
  {
    @Override
    protected FeatureDeltaWriter initialValue()
    {
      return new FeatureDeltaWriter();
    };
  };

  public ClassMapping(MappingStrategy mappingStrategy, EClass eClass, EStructuralFeature[] features)
  {
    this.mappingStrategy = mappingStrategy;
    this.eClass = eClass;

    String tableName = mappingStrategy.getTableName(eClass);
    table = addTable(tableName);
    initTable(table, hasFullRevisionInfo());

    if (features != null)
    {
      attributeMappings = createAttributeMappings(features);
      referenceMappings = createReferenceMappings(features);

      // // Special handling of CDOResource table
      // CDOResourceClass resourceClass = getResourceClass();
      // if (eClass == resourceClass)
      // {
      // // Create a unique ids to prevent duplicate resource paths
      // for (IAttributeMapping attributeMapping : attributeMappings)
      // {
      // if (attributeMapping.getFeature() == resourceClass.getCDOPathFeature())
      // {
      // IDBField versionField = table.getField(CDODBSchema.ATTRIBUTES_VERSION);
      // IDBField pathField = attributeMapping.getField();
      // pathField.setPrecision(760);// MYSQL key limitation 767
      // pathField.setNotNull(true);
      //
      // // Example: Currently a store can not specify that it does not support non-auditing mode!
      // if (false && !mappingStrategy.getStore().getRepository().isSupportingAudits())
      // {
      // // Create a unique ids to prevent duplicate resource paths
      // table.addIndex(IDBIndex.Type.UNIQUE, versionField, pathField);
      // }
      //
      // break;
      // }
      // }
      // }
    }
  }

  public MappingStrategy getMappingStrategy()
  {
    return mappingStrategy;
  }

  public EClass getEClass()
  {
    return eClass;
  }

  public IDBTable getTable()
  {
    return table;
  }

  public Set<IDBTable> getAffectedTables()
  {
    return affectedTables;
  }

  protected void initTable(IDBTable table, boolean full)
  {
    IDBField idField = table.addField(CDODBSchema.ATTRIBUTES_ID, DBType.BIGINT, true);
    table.addField(CDODBSchema.ATTRIBUTES_VERSION, DBType.INTEGER, true);
    if (full)
    {
      table.addField(CDODBSchema.ATTRIBUTES_CLASS, DBType.BIGINT, true);
      table.addField(CDODBSchema.ATTRIBUTES_CREATED, DBType.BIGINT, true);
      IDBField revisedField = table.addField(CDODBSchema.ATTRIBUTES_REVISED, DBType.BIGINT, true);
      table.addField(CDODBSchema.ATTRIBUTES_RESOURCE, DBType.BIGINT, true);
      table.addField(CDODBSchema.ATTRIBUTES_CONTAINER, DBType.BIGINT, true);
      table.addField(CDODBSchema.ATTRIBUTES_FEATURE, DBType.INTEGER, true);

      table.addIndex(IDBIndex.Type.NON_UNIQUE, idField, revisedField);
    }
  }

  protected IDBTable addTable(String name)
  {
    IDBTable table = mappingStrategy.getStore().getDBSchema().addTable(name);
    affectedTables.add(table);
    return table;
  }

  protected IDBField addField(EStructuralFeature feature, IDBTable table) throws DBException
  {
    String fieldName = mappingStrategy.getFieldName(feature);
    DBType fieldType = getDBType(feature);
    int fieldLength = getDBLength(feature);

    IDBField field = table.addField(fieldName, fieldType, fieldLength);
    affectedTables.add(table);
    return field;
  }

  protected DBType getDBType(EStructuralFeature feature)
  {
    return DBStore.getDBType(feature.getEType());
  }

  protected int getDBLength(EStructuralFeature feature)
  {
    // Derby: The maximum length for a VARCHAR string is 32,672 characters.
    CDOType type = CDOModelUtil.getType(feature.getEType());
    return type == CDOType.STRING || type == CDOType.CUSTOM ? 32672 : IDBField.DEFAULT;
  }

  protected IDBAdapter getDBAdapter()
  {
    IDBStore store = mappingStrategy.getStore();
    return store.getDBAdapter();
  }

  public IFeatureMapping getFeatureMapping(EStructuralFeature feature)
  {
    if (feature instanceof EReference && mappingStrategy.getToMany() != ToMany.LIKE_ATTRIBUTES)
    {
      return getReferenceMapping(feature);
    }

    return getAttributeMapping(feature);
  }

  public List<IAttributeMapping> getAttributeMappings()
  {
    return attributeMappings;
  }

  public List<IReferenceMapping> getReferenceMappings()
  {
    return referenceMappings;
  }

  public IReferenceMapping getReferenceMapping(EStructuralFeature feature)
  {
    // TODO Optimize this?
    for (IReferenceMapping referenceMapping : referenceMappings)
    {
      if (referenceMapping.getFeature() == feature)
      {
        return referenceMapping;
      }
    }

    return null;
  }

  public IAttributeMapping getAttributeMapping(EStructuralFeature feature)
  {
    // TODO Optimize this?
    for (IAttributeMapping attributeMapping : attributeMappings)
    {
      if (attributeMapping.getFeature() == feature)
      {
        return attributeMapping;
      }
    }

    return null;
  }

  protected List<IAttributeMapping> createAttributeMappings(EStructuralFeature[] features)
  {
    List<IAttributeMapping> attributeMappings = new ArrayList<IAttributeMapping>();
    for (EStructuralFeature feature : features)
    {
      if (feature instanceof EReference)
      {
        if (!feature.isMany())
        {
          attributeMappings.add(createToOneReferenceMapping(feature));
        }
      }
      else
      {
        attributeMappings.add(createAttributeMapping(feature));
      }
    }

    return attributeMappings.isEmpty() ? null : attributeMappings;
  }

  protected List<IReferenceMapping> createReferenceMappings(EStructuralFeature[] features)
  {
    List<IReferenceMapping> referenceMappings = new ArrayList<IReferenceMapping>();
    for (EStructuralFeature feature : features)
    {
      if (feature instanceof EReference && feature.isMany())
      {
        referenceMappings.add(createReferenceMapping(feature));
      }
    }

    return referenceMappings.isEmpty() ? null : referenceMappings;
  }

  protected AttributeMapping createAttributeMapping(EStructuralFeature feature)
  {
    CDOType type = CDOModelUtil.getType(feature.getEType());
    if (type == CDOType.BOOLEAN || type == CDOType.BOOLEAN_OBJECT)
    {
      return new AttributeMapping.AMBoolean(this, feature);
    }
    else if (type == CDOType.BYTE || type == CDOType.BYTE_OBJECT)
    {
      return new AttributeMapping.AMByte(this, feature);
    }
    else if (type == CDOType.CHAR || type == CDOType.CHARACTER_OBJECT)
    {
      return new AttributeMapping.AMCharacter(this, feature);
    }
    else if (type == CDOType.DATE)
    {
      return new AttributeMapping.AMDate(this, feature);
    }
    else if (type == CDOType.DOUBLE || type == CDOType.DOUBLE_OBJECT)
    {
      return new AttributeMapping.AMDouble(this, feature);
    }
    else if (type == CDOType.FLOAT || type == CDOType.FLOAT_OBJECT)
    {
      return new AttributeMapping.AMFloat(this, feature);
    }
    else if (type == CDOType.INT || type == CDOType.INTEGER_OBJECT)
    {
      return new AttributeMapping.AMInteger(this, feature);
    }
    else if (type == CDOType.LONG || type == CDOType.LONG_OBJECT)
    {
      return new AttributeMapping.AMLong(this, feature);
    }
    else if (type == CDOType.OBJECT)
    {
      return new AttributeMapping.AMObject(this, feature);
    }
    else if (type == CDOType.SHORT || type == CDOType.SHORT_OBJECT)
    {
      return new AttributeMapping.AMShort(this, feature);
    }
    else if (type == CDOType.STRING || type == CDOType.CUSTOM)
    {
      return new AttributeMapping.AMString(this, feature);
    }

    throw new ImplementationError("Unrecognized CDOType: " + type);
  }

  protected ToOneReferenceMapping createToOneReferenceMapping(EStructuralFeature feature)
  {
    return new ToOneReferenceMapping(this, feature);
  }

  protected ReferenceMapping createReferenceMapping(EStructuralFeature feature)
  {
    return new ReferenceMapping(this, feature, ToMany.PER_REFERENCE);
  }

  public Object createReferenceMappingKey(EStructuralFeature feature)
  {
    return feature;
  }

  public void writeRevision(IDBStoreAccessor accessor, InternalCDORevision revision, OMMonitor monitor)
  {
    try
    {
      // TODO Better monitoring
      monitor.begin(10);

      if (revision.getVersion() > 1 && hasFullRevisionInfo() && isAuditing())
      {
        writeRevisedRow(accessor, revision);
      }

      monitor.worked();

      if (revision.isResourceFolder() || revision.isResource())
      {
        checkDuplicateResources(accessor, revision);
      }

      monitor.worked();

      // Write attribute table always (even without modeled attributes!)
      writeAttributes(accessor, revision);

      monitor.worked();

      // Write reference tables only if they exist
      if (referenceMappings != null)
      {
        writeReferences(accessor, revision);
      }

      monitor.worked(7);
    }
    finally
    {
      monitor.done();
    }
  }

  private boolean isAuditing()
  {
    return mappingStrategy.getStore().getRevisionTemporality() == IStore.RevisionTemporality.AUDITING;
  }

  protected abstract void checkDuplicateResources(IDBStoreAccessor accessor, CDORevision revision)
      throws IllegalStateException;

  public void detachObject(IDBStoreAccessor accessor, CDOID id, long revised, OMMonitor monitor)
  {
    try
    {
      monitor.begin();
      if (hasFullRevisionInfo())
      {
        if (isAuditing())
        {
          writeRevisedRow(accessor, id, revised);
          monitor.worked(1);
        }
        else
        {
          deleteRevision(accessor, id, monitor.fork(1));
        }
      }

      // TODO Handle !hasFullRevisionInfo() case
    }
    finally
    {
      monitor.done();
    }
  }

  protected void deleteRevision(IDBStoreAccessor accessor, CDOID id, OMMonitor monitor)
  {
    try
    {
      monitor.begin(2);
      deleteAttributes(accessor, id);
      monitor.worked(1);
      deleteReferences(accessor, id);
      monitor.worked(1);
    }
    finally
    {
      monitor.done();
    }
  }

  protected final void writeRevisedRow(IDBStoreAccessor accessor, InternalCDORevision revision)
  {
    accessor.getJDBCDelegate().updateRevisedForReplace(revision, this);
  }

  protected final void writeRevisedRow(IDBStoreAccessor accessor, CDOID id, long revised)
  {
    accessor.getJDBCDelegate().updateRevisedForDetach(id, revised, this);
  }

  protected final void writeAttributes(IDBStoreAccessor accessor, InternalCDORevision revision)
  {
    if (revision.getVersion() == 1 || isAuditing())
    {
      accessor.getJDBCDelegate().insertAttributes(revision, this);
    }
    else
    {
      accessor.getJDBCDelegate().updateAttributes(revision, this);
    }
  }

  protected final void deleteAttributes(IDBStoreAccessor accessor, CDOID id)
  {
    accessor.getJDBCDelegate().deleteAttributes(id, this);
  }

  protected final void deleteReferences(IDBStoreAccessor accessor, CDOID id)
  {
    if (referenceMappings != null)
    {
      for (IReferenceMapping referenceMapping : referenceMappings)
      {
        referenceMapping.deleteReference(accessor, id);
      }
    }
  }

  protected void writeReferences(IDBStoreAccessor accessor, InternalCDORevision revision)
  {
    if (mappingStrategy.getStore().getRevisionTemporality() == IStore.RevisionTemporality.NONE)
    {
      deleteReferences(accessor, revision.getID());
    }

    for (IReferenceMapping referenceMapping : referenceMappings)
    {
      referenceMapping.writeReference(accessor, revision);
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

  public boolean readRevision(IDBStoreAccessor accessor, InternalCDORevision revision, int referenceChunk)
  {
    String where = mappingStrategy.createWhereClause(CDORevision.UNSPECIFIED_DATE);
    return readRevision(accessor, revision, where, referenceChunk);
  }

  public boolean readRevisionByTime(IDBStoreAccessor accessor, InternalCDORevision revision, long timeStamp,
      int referenceChunk)
  {
    String where = mappingStrategy.createWhereClause(timeStamp);
    return readRevision(accessor, revision, where, referenceChunk);
  }

  public boolean readRevisionByVersion(IDBStoreAccessor accessor, InternalCDORevision revision, int version,
      int referenceChunk)
  {
    String where = CDODBSchema.ATTRIBUTES_VERSION + "=" + version;
    return readRevision(accessor, revision, where, referenceChunk);
  }

  /**
   * Read a revision.
   * 
   * @return <code>true</code> if the revision has been read successfully.<br>
   *         <code>false</code> if the revision does not exist in the DB.
   */
  protected boolean readRevision(IDBStoreAccessor accessor, InternalCDORevision revision, String where,
      int referenceChunk)
  {
    // Read attribute table always (even without modeled attributes!)
    boolean success = readAttributes(accessor, revision, where);

    // Read reference tables only if revision exists and if references exist
    if (success && referenceMappings != null)
    {
      readReferences(accessor, revision, referenceChunk);
    }

    return success;
  }

  /**
   * Read the revision's attributes from the DB.
   * 
   * @return <code>true</code> if the revision has been read successfully.<br>
   *         <code>false</code> if the revision does not exist in the DB.
   */
  protected final boolean readAttributes(IDBStoreAccessor accessor, InternalCDORevision revision, String where)
  {
    return accessor.getJDBCDelegate().selectRevisionAttributes(revision, this, where);
  }

  protected void readReferences(IDBStoreAccessor accessor, InternalCDORevision revision, int referenceChunk)
  {
    for (IReferenceMapping referenceMapping : referenceMappings)
    {
      referenceMapping.readReference(accessor, revision, referenceChunk);
    }
  }

  private class FeatureDeltaWriter implements CDOFeatureDeltaVisitor
  {
    private CDOID id;

    private int newVersion;

    private long created;

    private IDBStoreAccessor accessor;

    private boolean updateContainer = false;

    private List<Pair<IAttributeMapping, Object>> attributeChanges;

    private int newContainingFeatureID;

    private CDOID newContainerID;

    private CDOID newResourceID;

    public FeatureDeltaWriter()
    {
      attributeChanges = new ArrayList<Pair<IAttributeMapping, Object>>();
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
      newVersion = d.getDirtyVersion();
      created = c;
      accessor = a;

      // process revision delta tree
      d.accept(this);

      // update attributes
      if (updateContainer)
      {
        accessor.getJDBCDelegate().updateAttributes(id, newVersion, created, newContainerID, newContainingFeatureID,
            newResourceID, attributeChanges, ClassMapping.this);
      }
      else
      {
        accessor.getJDBCDelegate().updateAttributes(id, newVersion, created, attributeChanges, ClassMapping.this);
      }

      // update version number of all references to current version
      if (referenceMappings != null)
      {
        for (IReferenceMapping referenceMapping : getReferenceMappings())
        {
          referenceMapping.updateReferenceVersion(accessor, id, newVersion);
        }
      }
    }

    public void visit(CDOMoveFeatureDelta delta)
    {
      getReferenceMapping(delta.getFeature()).moveReferenceEntry(accessor, id, newVersion, delta.getOldPosition(),
          delta.getNewPosition());
    }

    public void visit(CDOSetFeatureDelta delta)
    {
      if (delta.getFeature().isMany())
      {
        IReferenceMapping rm = getReferenceMapping(delta.getFeature());
        if (rm == null)
        {
          throw new IllegalArgumentException("ReferenceMapping for " + delta.getFeature() + " is null!");
        }
        rm.updateReference(accessor, id, newVersion, delta.getIndex(), (CDOID)delta.getValue());
      }
      else
      {
        IAttributeMapping am = getAttributeMapping(delta.getFeature());
        if (am == null)
        {
          throw new IllegalArgumentException("AttributeMapping for " + delta.getFeature() + " is null!");
        }
        attributeChanges.add(new Pair<IAttributeMapping, Object>(am, delta.getValue()));
      }
    }

    public void visit(CDOUnsetFeatureDelta delta)
    {
      // TODO Correct this when DBStore implements unsettable features
      // see Bugs 259868 and 263010
      IAttributeMapping am = getAttributeMapping(delta.getFeature());
      attributeChanges.add(new Pair<IAttributeMapping, Object>(am, null));
    }

    public void visit(CDOListFeatureDelta delta)
    {
      for (CDOFeatureDelta listChange : delta.getListChanges())
      {
        listChange.accept(this);
      }
    }

    public void visit(CDOClearFeatureDelta delta)
    {
      getReferenceMapping(delta.getFeature()).deleteReference(accessor, id);
    }

    public void visit(CDOAddFeatureDelta delta)
    {
      getReferenceMapping(delta.getFeature()).insertReferenceEntry(accessor, id, newVersion, delta.getIndex(),
          (CDOID)delta.getValue());
    }

    public void visit(CDORemoveFeatureDelta delta)
    {
      getReferenceMapping(delta.getFeature()).removeReferenceEntry(accessor, id, newVersion, delta.getIndex());
    }

    public void visit(CDOContainerFeatureDelta delta)
    {
      newContainingFeatureID = delta.getContainerFeatureID();
      newContainerID = (CDOID)delta.getContainerID();
      newResourceID = delta.getResourceID();
      updateContainer = true;
    }
  }
}
