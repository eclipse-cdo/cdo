/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.internal.protocol.CDOIDImpl;
import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.protocol.model.CDOClass;
import org.eclipse.emf.cdo.protocol.model.CDOFeature;
import org.eclipse.emf.cdo.protocol.model.CDOType;
import org.eclipse.emf.cdo.server.db.IAttributeMapping;
import org.eclipse.emf.cdo.server.db.IClassMapping;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IReferenceMapping;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBField;
import org.eclipse.net4j.db.IDBTable;
import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.ImplementationError;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public abstract class ClassMapping implements IClassMapping
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, ClassMapping.class);

  private MappingStrategy mappingStrategy;

  private CDOClass cdoClass;

  private IDBTable table;

  private Set<IDBTable> affectedTables = new HashSet<IDBTable>();

  private List<IAttributeMapping> attributeMappings;

  private List<IReferenceMapping> referenceMappings;

  private String selectPrefix;

  private String selectPrefixWithVersion;

  public ClassMapping(MappingStrategy mappingStrategy, CDOClass cdoClass, CDOFeature[] features)
  {
    this.mappingStrategy = mappingStrategy;
    this.cdoClass = cdoClass;

    table = addTable(cdoClass.getName());
    initTable(table, hasFullRevisionInfo());

    if (features != null)
    {
      attributeMappings = createAttributeMappings(features);
      referenceMappings = createReferenceMappings(features);
    }

    selectPrefix = createSelectPrefix(false);
    selectPrefixWithVersion = createSelectPrefix(true);
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
    table.addField(CDODBSchema.ATTRIBUTES_ID, DBType.BIGINT);
    table.addField(CDODBSchema.ATTRIBUTES_VERSION, DBType.INTEGER);
    if (full)
    {
      table.addField(CDODBSchema.ATTRIBUTES_CLASS, DBType.INTEGER);
      table.addField(CDODBSchema.ATTRIBUTES_CREATED, DBType.BIGINT);
      table.addField(CDODBSchema.ATTRIBUTES_REVISED, DBType.BIGINT);
      table.addField(CDODBSchema.ATTRIBUTES_RESOURCE, DBType.BIGINT);
      table.addField(CDODBSchema.ATTRIBUTES_CONTAINER, DBType.BIGINT);
      table.addField(CDODBSchema.ATTRIBUTES_FEATURE, DBType.INTEGER);
    }
  }

  protected void appendRevisionInfos(StringBuilder builder, CDORevisionImpl revision, boolean full)
  {
    builder.append(revision.getID().getValue());
    builder.append(", ");
    builder.append(revision.getVersion());
    if (full)
    {
      builder.append(", ");
      builder.append(ServerInfo.getDBID(revision.getCDOClass()));
      builder.append(", ");
      builder.append(revision.getCreated());
      builder.append(", ");
      builder.append(revision.getRevised());
      builder.append(", ");
      builder.append(revision.getResourceID().getValue());
      builder.append(", ");
      builder.append(revision.getContainerID().getValue());
      builder.append(", ");
      builder.append(revision.getContainingFeatureID());
    }
  }

  protected int sqlUpdate(IDBStoreAccessor storeAccessor, String sql) throws DBException
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace(sql);
    }

    try
    {
      Statement statement = storeAccessor.getStatement();
      return statement.executeUpdate(sql);
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
  }

  protected String mangleTableName(String name, int attempt)
  {
    return getDBAdapter().mangleTableName(name, attempt);
  }

  protected String mangleFieldName(String name, int attempt)
  {
    return getDBAdapter().mangleFieldName(name, attempt);
  }

  protected IDBTable addTable(String name)
  {
    for (int attempt = 0;; ++attempt)
    {
      String tableName = mangleTableName(name, attempt);

      try
      {
        IDBTable table = mappingStrategy.getStore().getSchema().addTable(tableName);
        affectedTables.add(table);
        return table;
      }
      catch (DBException ex)
      {
        if (TRACER.isEnabled())
        {
          TRACER.format("{0}. attempt to add table: {1} ({2})", attempt + 1, tableName, ex.getMessage());
        }
      }
    }
  }

  protected IDBField addField(CDOFeature cdoFeature, IDBTable table) throws DBException
  {
    DBType fieldType = getDBType(cdoFeature);
    int fieldLength = getDBLength(cdoFeature);
    for (int attempt = 0;; ++attempt)
    {
      String fieldName = mangleFieldName(cdoFeature.getName(), attempt);

      try
      {
        IDBField field = table.addField(fieldName, fieldType, fieldLength);
        affectedTables.add(table);
        return field;
      }
      catch (DBException ex)
      {
        if (TRACER.isEnabled())
        {
          TRACER.format("{0}. attempt to add field: {1} ({2})", attempt + 1, fieldName, ex.getMessage());
        }
      }
    }
  }

  protected DBType getDBType(CDOFeature cdoFeature)
  {
    return DBStore.getDBType(cdoFeature.getType());
  }

  protected int getDBLength(CDOFeature cdoFeature)
  {
    // Derby: The maximum length for a VARCHAR string is 32,672 characters.
    return cdoFeature.getType() == CDOType.STRING ? 32672 : IDBField.DEFAULT;
  }

  protected IDBAdapter getDBAdapter()
  {
    IDBStore store = mappingStrategy.getStore();
    return store.getDBAdapter();
  }

  protected String createSelectPrefix(boolean readVersion)
  {
    StringBuilder builder = new StringBuilder();
    builder.append("SELECT ");

    if (hasFullRevisionInfo())
    {
      if (readVersion)
      {
        builder.append(CDODBSchema.ATTRIBUTES_VERSION);
        builder.append(", ");
      }

      builder.append(CDODBSchema.ATTRIBUTES_CREATED);
      builder.append(", ");
      builder.append(CDODBSchema.ATTRIBUTES_REVISED);
      builder.append(", ");
      builder.append(CDODBSchema.ATTRIBUTES_RESOURCE);
      builder.append(", ");
      builder.append(CDODBSchema.ATTRIBUTES_CONTAINER);
      builder.append(", ");
      builder.append(CDODBSchema.ATTRIBUTES_FEATURE);
    }
    else
    {
      if (attributeMappings == null)
      {
        // Only references
        return null;
      }
    }

    if (attributeMappings != null)
    {
      for (IAttributeMapping attributeMapping : attributeMappings)
      {
        builder.append(", ");
        builder.append(attributeMapping.getField().getName());
      }
    }

    builder.append(" FROM ");
    builder.append(getTable());
    builder.append(" WHERE ");
    builder.append(CDODBSchema.ATTRIBUTES_ID);
    builder.append("=");
    return builder.toString();
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
    else if (type == CDOType.STRING)
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

  protected abstract boolean hasFullRevisionInfo();

  public void writeRevision(IDBStoreAccessor storeAccessor, CDORevisionImpl revision)
  {
    if (attributeMappings != null)
    {
      writeAttributes(storeAccessor, revision);
    }

    if (referenceMappings != null)
    {
      writeReferences(storeAccessor, revision);
    }
  }

  protected void writeAttributes(IDBStoreAccessor storeAccessor, CDORevisionImpl revision)
  {
    StringBuilder builder = new StringBuilder();
    builder.append("INSERT INTO ");
    builder.append(getTable());
    builder.append(" VALUES (");
    appendRevisionInfos(builder, revision, hasFullRevisionInfo());

    for (IAttributeMapping attributeMapping : attributeMappings)
    {
      builder.append(", ");
      attributeMapping.appendValue(builder, revision);
    }

    builder.append(")");
    sqlUpdate(storeAccessor, builder.toString());
  }

  protected void writeReferences(IDBStoreAccessor storeAccessor, CDORevisionImpl revision)
  {
    for (IReferenceMapping referenceMapping : referenceMappings)
    {
      referenceMapping.writeReference(storeAccessor, revision);
    }
  }

  public void readRevision(IDBStoreAccessor storeAccessor, CDORevisionImpl revision, int referenceChunk)
  {
    String where = CDODBSchema.ATTRIBUTES_REVISED + "=0";
    readRevision(storeAccessor, revision, where, true, referenceChunk);
  }

  public void readRevisionByTime(IDBStoreAccessor storeAccessor, CDORevisionImpl revision, long timeStamp,
      int referenceChunk)
  {
    StringBuilder where = new StringBuilder();
    where.append("(");
    where.append(CDODBSchema.ATTRIBUTES_REVISED);
    where.append("=0 OR ");
    where.append(CDODBSchema.ATTRIBUTES_REVISED);
    where.append(">=");
    where.append(timeStamp);
    where.append(") AND ");
    where.append(timeStamp);
    where.append(">=");
    where.append(CDODBSchema.ATTRIBUTES_CREATED);
    readRevision(storeAccessor, revision, where.toString(), true, referenceChunk);
  }

  public void readRevisionByVersion(IDBStoreAccessor storeAccessor, CDORevisionImpl revision, int version,
      int referenceChunk)
  {
    String where = CDODBSchema.ATTRIBUTES_VERSION + "=" + version;
    readRevision(storeAccessor, revision, where, false, referenceChunk);
  }

  protected void readRevision(IDBStoreAccessor storeAccessor, CDORevisionImpl revision, String where,
      boolean readVersion, int referenceChunk)
  {
    if (attributeMappings != null)
    {
      readAttributes(storeAccessor, revision, where, readVersion);
    }

    if (referenceMappings != null)
    {
      readReferences(storeAccessor, revision, referenceChunk);
    }
  }

  protected void readAttributes(IDBStoreAccessor storeAccessor, CDORevisionImpl revision, String where,
      boolean readVersion)
  {
    long id = revision.getID().getValue();
    StringBuilder builder = new StringBuilder(readVersion ? selectPrefixWithVersion : selectPrefix);
    builder.append(id);
    if (where != null)
    {
      builder.append(" AND ");
      builder.append(where);
    }

    String sql = builder.toString();
    if (TRACER.isEnabled()) TRACER.trace(sql);
    ResultSet resultSet = null;

    try
    {
      resultSet = storeAccessor.getStatement().executeQuery(sql);
      if (!resultSet.next())
      {
        throw new IllegalStateException("Revision not found: " + id);
      }

      int i = 1;
      if (hasFullRevisionInfo())
      {
        if (readVersion)
        {
          revision.setVersion(resultSet.getInt(i++));
        }

        revision.setCreated(resultSet.getLong(i++));
        revision.setRevised(resultSet.getLong(i++));
        revision.setResourceID(CDOIDImpl.create(resultSet.getLong(i++)));
        revision.setContainerID(CDOIDImpl.create(resultSet.getLong(i++)));
        revision.setContainingFeature(resultSet.getInt(i++));
      }

      if (attributeMappings != null)
      {
        for (IAttributeMapping attributeMapping : attributeMappings)
        {
          attributeMapping.extractValue(resultSet, i++, revision);
        }
      }
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      DBUtil.close(resultSet);
    }
  }

  protected void readReferences(IDBStoreAccessor storeAccessor, CDORevisionImpl revision, int referenceChunk)
  {
    for (IReferenceMapping referenceMapping : referenceMappings)
    {
      referenceMapping.readReference(storeAccessor, revision, referenceChunk);
    }
  }
}
