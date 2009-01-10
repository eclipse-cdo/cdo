/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.CDOFeature;
import org.eclipse.emf.cdo.common.model.CDOType;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.db.IAttributeMapping;
import org.eclipse.emf.cdo.server.db.IClassMapping;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IFeatureMapping;
import org.eclipse.emf.cdo.server.db.IReferenceMapping;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBIndex;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

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

  private CDOClass cdoClass;

  private IDBTable table;

  private Set<IDBTable> affectedTables = new HashSet<IDBTable>();

  private List<IAttributeMapping> attributeMappings;

  private List<IReferenceMapping> referenceMappings;

  public ClassMapping(MappingStrategy mappingStrategy, CDOClass cdoClass, CDOFeature[] features)
  {
    this.mappingStrategy = mappingStrategy;
    this.cdoClass = cdoClass;

    String tableName = mappingStrategy.getTableName(cdoClass);
    table = addTable(tableName);
    initTable(table, hasFullRevisionInfo());

    if (features != null)
    {
      attributeMappings = createAttributeMappings(features);
      referenceMappings = createReferenceMappings(features);

      // // Special handling of CDOResource table
      // CDOResourceClass resourceClass = getResourceClass();
      // if (cdoClass == resourceClass)
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
      // // TODO Provide better design for store capabilities and repository support
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

  public CDOClass getCDOClass()
  {
    return cdoClass;
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
      table.addField(CDODBSchema.ATTRIBUTES_CLASS, DBType.INTEGER, true);
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

  protected IDBField addField(CDOFeature cdoFeature, IDBTable table) throws DBException
  {
    String fieldName = mappingStrategy.getFieldName(cdoFeature);
    DBType fieldType = getDBType(cdoFeature);
    int fieldLength = getDBLength(cdoFeature);

    IDBField field = table.addField(fieldName, fieldType, fieldLength);
    affectedTables.add(table);
    return field;
  }

  protected DBType getDBType(CDOFeature cdoFeature)
  {
    return DBStore.getDBType(cdoFeature.getType());
  }

  protected int getDBLength(CDOFeature cdoFeature)
  {
    // Derby: The maximum length for a VARCHAR string is 32,672 characters.
    CDOType type = cdoFeature.getType();
    return type == CDOType.STRING || type == CDOType.CUSTOM ? 32672 : IDBField.DEFAULT;
  }

  protected IDBAdapter getDBAdapter()
  {
    IDBStore store = mappingStrategy.getStore();
    return store.getDBAdapter();
  }

  public IFeatureMapping getFeatureMapping(CDOFeature feature)
  {
    if (feature.isReference() && mappingStrategy.getToMany() != ToMany.LIKE_ATTRIBUTES)
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

  public IReferenceMapping getReferenceMapping(CDOFeature feature)
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

  public IAttributeMapping getAttributeMapping(CDOFeature feature)
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

  protected List<IAttributeMapping> createAttributeMappings(CDOFeature[] features)
  {
    List<IAttributeMapping> attributeMappings = new ArrayList<IAttributeMapping>();
    for (CDOFeature feature : features)
    {
      if (feature.isReference())
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

  protected List<IReferenceMapping> createReferenceMappings(CDOFeature[] features)
  {
    List<IReferenceMapping> referenceMappings = new ArrayList<IReferenceMapping>();
    for (CDOFeature feature : features)
    {
      if (feature.isReference() && feature.isMany())
      {
        referenceMappings.add(createReferenceMapping(feature));
      }
    }

    return referenceMappings.isEmpty() ? null : referenceMappings;
  }

  protected AttributeMapping createAttributeMapping(CDOFeature feature)
  {
    CDOType type = feature.getType();
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

  protected ToOneReferenceMapping createToOneReferenceMapping(CDOFeature feature)
  {
    return new ToOneReferenceMapping(this, feature);
  }

  protected ReferenceMapping createReferenceMapping(CDOFeature feature)
  {
    return new ReferenceMapping(this, feature, ToMany.PER_REFERENCE);
  }

  public Object createReferenceMappingKey(CDOFeature cdoFeature)
  {
    return cdoFeature;
  }

  public void writeRevision(IDBStoreAccessor accessor, CDORevision revision, OMMonitor monitor)
  {
    try
    {
      // TODO Better monitoring
      monitor.begin(10);

      if (revision.getVersion() > 1 && hasFullRevisionInfo() && isAuditing())
      {
        writeRevisedRow(accessor, (InternalCDORevision)revision);
      }

      monitor.worked();

      if (revision.isResourceFolder() || revision.isResource())
      {
        checkDuplicateResources(accessor, revision);
      }

      monitor.worked();

      // Write attribute table always (even without modeled attributes!)
      writeAttributes(accessor, (InternalCDORevision)revision);

      monitor.worked();

      // Write reference tables only if they exist
      if (referenceMappings != null)
      {
        writeReferences(accessor, (InternalCDORevision)revision);
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
      // TODO handle !hasFullRevisionInfo() case
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

  public boolean readRevision(IDBStoreAccessor accessor, CDORevision revision, int referenceChunk)
  {
    String where = mappingStrategy.createWhereClause(CDORevision.UNSPECIFIED_DATE);
    return readRevision(accessor, (InternalCDORevision)revision, where, referenceChunk);
  }

  public boolean readRevisionByTime(IDBStoreAccessor accessor, CDORevision revision, long timeStamp, int referenceChunk)
  {
    String where = mappingStrategy.createWhereClause(timeStamp);
    return readRevision(accessor, (InternalCDORevision)revision, where, referenceChunk);
  }

  public boolean readRevisionByVersion(IDBStoreAccessor accessor, CDORevision revision, int version, int referenceChunk)
  {
    String where = CDODBSchema.ATTRIBUTES_VERSION + "=" + version;
    return readRevision(accessor, (InternalCDORevision)revision, where, referenceChunk);
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
}
