/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Victor Roldan Betancort - http://bugs.eclipse.org/208689    
 */
package org.eclipse.emf.cdo.server.internal.db.mapping;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.IStoreAccessor.QueryResourcesContext;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.mapping.IClassMapping;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.server.internal.db.CDODBSchema;
import org.eclipse.emf.cdo.server.internal.db.ObjectIDIterator;
import org.eclipse.emf.cdo.server.internal.db.ToMany;
import org.eclipse.emf.cdo.server.internal.db.ToOne;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.collection.CloseableIterator;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public abstract class MappingStrategy extends Lifecycle implements IMappingStrategy
{
  public static final String NAME_SEPARATOR = "_";

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, MappingStrategy.class);

  private IDBStore store;

  private Map<String, String> properties;

  private Map<Object, IDBTable> referenceTables = new HashMap<Object, IDBTable>();

  private Map<EClass, IClassMapping> classMappings = new HashMap<EClass, IClassMapping>();

  public MappingStrategy()
  {
  }

  public IDBStore getStore()
  {
    return store;
  }

  public void setStore(IDBStore store)
  {
    this.store = store;
  }

  public synchronized Map<String, String> getProperties()
  {
    if (properties == null)
    {
      properties = new HashMap<String, String>();
    }

    return properties;
  }

  public synchronized void setProperties(Map<String, String> properties)
  {
    this.properties = properties;
  }

  public int getMaxTableNameLength()
  {
    String value = getProperties().get(PROP_MAX_TABLE_NAME_LENGTH);
    return value == null ? store.getDBAdapter().getMaxTableNameLength() : Integer.valueOf(value);
  }

  public int getMaxFieldNameLength()
  {
    String value = getProperties().get(PROP_MAX_FIELD_NAME_LENGTH);
    return value == null ? store.getDBAdapter().getMaxFieldNameLength() : Integer.valueOf(value);
  }

  public String getTableNamePrefix()
  {
    String value = getProperties().get(PROP_TABLE_NAME_PREFIX);
    return StringUtil.safe(value);
  }

  public boolean isQualifiedNames()
  {
    String value = getProperties().get(PROP_QUALIFIED_NAMES);
    return value == null ? false : Boolean.valueOf(value);
  }

  public boolean isForceNamesWithID()
  {
    String value = getProperties().get(PROP_FORCE_NAMES_WITH_ID);
    return value == null ? false : Boolean.valueOf(value);
  }

  public ToMany getToMany()
  {
    String value = getProperties().get(PROP_TO_MANY_REFERENCE_MAPPING);
    return value == null ? ToMany.PER_REFERENCE : ToMany.valueOf(value);
  }

  public ToOne getToOne()
  {
    String value = getProperties().get(PROP_TO_ONE_REFERENCE_MAPPING);
    return value == null ? ToOne.LIKE_ATTRIBUTES : ToOne.valueOf(value);
  }

  public Map<Object, IDBTable> getReferenceTables()
  {
    return referenceTables;
  }

  public IClassMapping getClassMapping(EClass eClass)
  {
    synchronized (classMappings)
    {
      IClassMapping mapping = classMappings.get(eClass);
      if (mapping == null)
      {
        mapping = createClassMapping(eClass);
        classMappings.put(eClass, mapping);
      }

      if (mapping == NoClassMapping.INSTANCE)
      {
        return null;
      }

      return mapping;
    }
  }

  public String getTableName(EPackage ePackage)
  {
    String name = isQualifiedNames() ? EMFUtil.getQualifiedName(ePackage, NAME_SEPARATOR) : ePackage.getName();
    return getTableName(name, "P" + getStore().getMetaID(ePackage));
  }

  public String getTableName(EClass eClass)
  {
    String name = isQualifiedNames() ? EMFUtil.getQualifiedName(eClass, NAME_SEPARATOR) : eClass.getName();
    return getTableName(name, "C" + getStore().getMetaID(eClass));
  }

  public String getReferenceTableName(EClass eClass, EStructuralFeature feature)
  {
    String name = isQualifiedNames() ? EMFUtil.getQualifiedName(eClass, NAME_SEPARATOR) : eClass.getName();
    name += NAME_SEPARATOR;
    name += feature.getName();
    name += "_refs";
    return getTableName(name, "F" + getStore().getMetaID(feature));
  }

  public String getReferenceTableName(EClass eClass)
  {
    String name = isQualifiedNames() ? EMFUtil.getQualifiedName(eClass, NAME_SEPARATOR) : eClass.getName();
    name += "_refs";
    return getTableName(name, "F" + getStore().getMetaID(eClass));
  }

  public String getReferenceTableName(EPackage ePackage)
  {
    String name = isQualifiedNames() ? EMFUtil.getQualifiedName(ePackage, NAME_SEPARATOR) : ePackage.getName();
    name += "_refs";
    return getTableName(name, "F" + getStore().getMetaID(ePackage));
  }

  public String getFieldName(EStructuralFeature feature)
  {
    return getName(feature.getName(), "F" + getStore().getMetaID(feature), getMaxFieldNameLength());
  }

  private String getTableName(String name, String suffix)
  {
    String prefix = getTableNamePrefix();
    if (prefix.length() != 0 && !prefix.endsWith(NAME_SEPARATOR))
    {
      prefix += NAME_SEPARATOR;
    }

    return getName(prefix + name, suffix, getMaxTableNameLength());
  }

  private String getName(String name, String suffix, int maxLength)
  {
    boolean forceNamesWithID = isForceNamesWithID();
    if (store.getDBAdapter().isReservedWord(name))
    {
      forceNamesWithID = true;
    }

    if (name.length() > maxLength || forceNamesWithID)
    {
      suffix = NAME_SEPARATOR + suffix.replace('-', 'S');
      int length = Math.min(name.length(), maxLength - suffix.length());
      name = name.substring(0, length) + suffix;
    }

    return name;
  }

  public String createWhereClause(long timeStamp)
  {
    StringBuilder builder = new StringBuilder();
    if (timeStamp == CDORevision.UNSPECIFIED_DATE)
    {
      builder.append(CDODBSchema.ATTRIBUTES_REVISED);
      builder.append("=0");
    }
    else
    {
      builder.append("(");
      builder.append(CDODBSchema.ATTRIBUTES_REVISED);
      builder.append("=0 OR ");
      builder.append(CDODBSchema.ATTRIBUTES_REVISED);
      builder.append(">=");
      builder.append(timeStamp);
      builder.append(") AND ");
      builder.append(timeStamp);
      builder.append(">=");
      builder.append(CDODBSchema.ATTRIBUTES_CREATED);
    }

    return builder.toString();
  }

  public final CloseableIterator<CDOID> readObjectIDs(final IDBStoreAccessor accessor)
  {
    List<EClass> classes = getClassesWithObjectInfo();
    final Iterator<EClass> classIt = classes.iterator();
    return new ObjectIDIterator(this, accessor)
    {
      @Override
      protected ResultSet getNextResultSet()
      {
        while (classIt.hasNext())
        {
          EClass eClass = classIt.next();
          IClassMapping mapping = getClassMapping(eClass);
          if (mapping != null)
          {
            IDBTable table = mapping.getTable();
            if (table != null)
            {
              StringBuilder builder = new StringBuilder();
              builder.append("SELECT DISTINCT ");
              builder.append(CDODBSchema.ATTRIBUTES_ID);
              builder.append(" FROM ");
              builder.append(table);
              String sql = builder.toString();

              try
              {
                return accessor.getJDBCDelegate().getStatement().executeQuery(sql);
              }
              catch (SQLException ex)
              {
                throw new DBException(ex);
              }
            }
          }
        }

        return null;
      }
    };
  }

  public final void queryResources(IDBStoreAccessor accessor, QueryResourcesContext context)
  {
    CDOID folderID = context.getFolderID();
    String name = context.getName();
    boolean exactMatch = context.exactMatch();
    String where = createWhereClause(context.getTimeStamp());

    String[] queries = getResourceQueries(folderID, name, exactMatch);
    for (String query : queries)
    {
      StringBuilder builder = new StringBuilder();
      builder.append(query);
      builder.append(" AND (");
      builder.append(where);
      builder.append(")");

      String sql = builder.toString();
      if (TRACER.isEnabled())
      {
        TRACER.trace(sql);
      }

      ResultSet resultSet = null;

      try
      {
        resultSet = accessor.getJDBCDelegate().getStatement().executeQuery(sql);
        while (resultSet.next())
        {
          long longID = resultSet.getLong(1);
          CDOID id = CDOIDUtil.createLong(longID);
          if (!context.addResource(id))
          {
            // No more results allowed
            return;
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
  }

  protected abstract String[] getResourceQueries(CDOID folderID, String name, boolean exactMatch);

  public long repairAfterCrash(IDBAdapter dbAdapter, Connection connection)
  {
    long maxCDOID = 0L;
    for (EClass idClass : getClassesWithObjectInfo())
    {
      IClassMapping classMapping = getClassMapping(idClass);
      IDBTable table = classMapping.getTable();
      IDBField idField = table.getField(CDODBSchema.ATTRIBUTES_ID);
      long classMaxCDOID = DBUtil.selectMaximumLong(connection, idField);
      if (TRACER.isEnabled())
      {
        TRACER.format("Max CDOID of table {0}: {1}", table, classMaxCDOID);
      }

      maxCDOID = Math.max(maxCDOID, classMaxCDOID);
    }

    return maxCDOID + 2L;
  }

  @Override
  public String toString()
  {
    return getType();
  }

  /**
   * The implementation of this method must take care of creating a unique ids to prevent duplicate resource paths.
   */
  protected abstract IClassMapping createClassMapping(EClass eClass);

  protected abstract List<EClass> getClassesWithObjectInfo();

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    checkState(store, "store");
  }
}
