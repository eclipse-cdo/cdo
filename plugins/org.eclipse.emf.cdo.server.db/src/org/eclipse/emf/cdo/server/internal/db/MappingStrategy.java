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

import org.eclipse.emf.cdo.internal.protocol.model.CDOPackageImpl;
import org.eclipse.emf.cdo.protocol.model.CDOClass;
import org.eclipse.emf.cdo.protocol.model.CDOFeature;
import org.eclipse.emf.cdo.protocol.model.CDOType;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.IMappingStrategy;
import org.eclipse.emf.cdo.server.db.MappingPrecedence;
import org.eclipse.emf.cdo.server.db.ToManyReferenceMapping;
import org.eclipse.emf.cdo.server.db.ToOneReferenceMapping;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.IDBField;
import org.eclipse.net4j.db.IDBSchema;
import org.eclipse.net4j.db.IDBTable;
import org.eclipse.net4j.internal.db.DBSchema;
import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.ObjectUtil;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * @author Eike Stepper
 */
public abstract class MappingStrategy implements IMappingStrategy
{
  public static final int FULL_NUMBER_OF_FIELDS = 8;

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, MappingStrategy.class);

  private IDBStore store;

  private Map<String, String> properties;

  private IDBSchema schema;

  private MappingPrecedence mappingPrecedence;

  private ToManyReferenceMapping toManyReferenceMapping;

  private ToOneReferenceMapping toOneReferenceMapping;

  private Map<CDOClass, ClassMapping> classMappings = new HashMap();

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

  public Map<String, String> getProperties()
  {
    if (properties == null)
    {
      properties = new HashMap();
    }

    return properties;
  }

  public void setProperties(Map<String, String> properties)
  {
    this.properties = properties;
  }

  public IDBSchema getSchema()
  {
    return schema;
  }

  public MappingPrecedence getMappingPrecedence()
  {
    if (mappingPrecedence == null)
    {
      mappingPrecedence = MappingPrecedence.STRATEGY;
      if (properties != null)
      {
        String value = properties.get("mappingPrecedence");
        if (value != null)
        {
          mappingPrecedence = MappingPrecedence.valueOf(value);
        }
      }
    }

    return mappingPrecedence;
  }

  public ToManyReferenceMapping getToManyReferenceMapping()
  {
    if (toManyReferenceMapping == null)
    {
      toManyReferenceMapping = ToManyReferenceMapping.ONE_TABLE_PER_REFERENCE;
      if (properties != null)
      {
        String value = properties.get("toManyReferenceMapping");
        if (value != null)
        {
          toManyReferenceMapping = ToManyReferenceMapping.valueOf(value);
        }
      }
    }

    return toManyReferenceMapping;
  }

  public ToOneReferenceMapping getToOneReferenceMapping()
  {
    if (toOneReferenceMapping == null)
    {
      toOneReferenceMapping = ToOneReferenceMapping.LIKE_ATTRIBUTES;
      if (properties != null)
      {
        String value = properties.get("toOneReferenceMapping");
        if (value != null)
        {
          toOneReferenceMapping = ToOneReferenceMapping.valueOf(value);
        }
      }
    }

    return toOneReferenceMapping;
  }

  @Override
  public String toString()
  {
    return getType();
  }

  public Set<IDBTable> map(CDOPackageImpl[] cdoPackages)
  {
    // Lazily create the schema
    if (schema == null)
    {
      schema = createSchema();
    }

    // Prepare data structures
    Set<IDBTable> affectedTables = new HashSet();
    List<CDOClass> cdoClasses = new ArrayList();

    // Map all packages before classes are mapped
    for (CDOPackageImpl cdoPackage : cdoPackages)
    {
      ((DBPackageInfo)cdoPackage.getServerInfo()).setSchema(schema);
      if (TRACER.isEnabled())
      {
        TRACER.format("Mapped package: {0} --> {1}", cdoPackage, schema);
      }
    }

    // Map all classes before features are mapped
    for (CDOPackageImpl cdoPackage : cdoPackages)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Mapping classes of package {0}", cdoPackage);
      }

      for (CDOClass cdoClass : cdoPackage.getClasses())
      {
        cdoClasses.add(cdoClass);
        IDBTable table = mapClass(cdoClass, affectedTables);
        if (table != null)
        {
          ((DBClassInfo)cdoClass.getServerInfo()).setTable(table);
          affectedTables.add(table);
          if (TRACER.isEnabled())
          {
            TRACER.format("Mapped class: {0} --> {1}", cdoClass, table);
          }
        }
      }
    }

    // Map all features
    for (CDOClass cdoClass : cdoClasses)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Mapping features of class {0}", cdoClass);
      }

      for (CDOFeature cdoFeature : cdoClass.getAllFeatures())
      {
        IDBField field = mapFeature(cdoClass, cdoFeature, affectedTables);
        if (field != null)
        {
          ((DBFeatureInfo)cdoFeature.getServerInfo()).addField(cdoClass, field);
          affectedTables.add(field.getTable());
          if (TRACER.isEnabled())
          {
            TRACER.format("Mapped feature: {0} --> {1}", cdoFeature, field);
          }
        }
      }
    }

    return affectedTables;
  }

  /**
   * @param affectedTables
   *          Can be used to indicate the creation or modification of additional
   *          tables. There is no need to add the returned table to this set of
   *          affected tables. The caller takes care of that.
   */
  protected abstract IDBTable mapClass(CDOClass cdoClass, Set<IDBTable> affectedTables);

  /**
   * @param affectedTables
   *          Can be used to indicate the creation or modification of additional
   *          tables. There is no need to add the table of the returned field to
   *          this set of affected tables. The caller takes care of that.
   */
  protected abstract IDBField mapFeature(CDOClass cdoClass, CDOFeature cdoFeature, Set<IDBTable> affectedTables);

  protected ClassMapping getClassMapping(CDOClass cdoClass)
  {
    ClassMapping classMapping = classMappings.get(cdoClass);
    if (classMapping == null)
    {
      classMapping = new ClassMapping(cdoClass);
      classMappings.put(cdoClass, classMapping);
      for (CDOFeature feature : cdoClass.getAllFeatures())
      {
        DBFeatureInfo featureInfo = (DBFeatureInfo)feature.getServerInfo();
        IDBField field = featureInfo.getField(cdoClass);
        if (field != null)
        {
          classMapping.addFeatureMapping(feature, field);
        }
      }
    }

    classMapping.sortFeatureMappings();
    return classMapping;
  }

  protected String mangleTableName(String name, int attempt)
  {
    return store.getDBAdapter().mangleTableName(name, attempt);
  }

  protected String mangleFieldName(String name, int attempt)
  {
    return store.getDBAdapter().mangleFieldName(name, attempt);
  }

  protected IDBSchema createSchema()
  {
    String name = store.getRepository().getName();
    return new DBSchema(name);
  }

  protected int initTable(IDBTable table, boolean full)
  {
    table.addField("cdo_id", DBType.BIGINT);
    if (full)
    {
      table.addField("cdo_class", DBType.INTEGER);
      table.addField("cdo_version", DBType.INTEGER);
      table.addField("cdo_created", DBType.TIMESTAMP);
      table.addField("cdo_revised", DBType.TIMESTAMP);
      table.addField("cdo_resource", DBType.BIGINT);
      table.addField("cdo_container", DBType.BIGINT);
      table.addField("cdo_feature", DBType.INTEGER);
      return FULL_NUMBER_OF_FIELDS;
    }

    return 1;
  }

  protected IDBTable addTable(CDOClass cdoClass)
  {
    return addTable(cdoClass.getName());
  }

  protected IDBTable addTable(String name)
  {
    for (int attempt = 0;; ++attempt)
    {
      try
      {
        String tableName = mangleTableName(name, attempt);
        return getSchema().addTable(tableName);
      }
      catch (DBException ignore)
      {
      }
    }
  }

  protected IDBField addField(CDOFeature cdoFeature, IDBTable table) throws DBException
  {
    for (int attempt = 0;; ++attempt)
    {
      try
      {
        String fieldName = mangleFieldName(cdoFeature.getName(), attempt);
        DBType fieldType = getDBType(cdoFeature.getType());
        return table.addField(fieldName, fieldType);
      }
      catch (DBException ignore)
      {
      }
    }
  }

  protected DBType getDBType(CDOType type)
  {
    if (type == CDOType.BOOLEAN || type == CDOType.BOOLEAN_OBJECT)
    {
      return DBType.BOOLEAN;
    }
    else if (type == CDOType.BYTE || type == CDOType.BYTE_OBJECT)
    {
      return DBType.SMALLINT;
    }
    else if (type == CDOType.CHAR || type == CDOType.CHARACTER_OBJECT)
    {
      return DBType.CHAR;
    }
    else if (type == CDOType.DATE)
    {
      return DBType.DATE;
    }
    else if (type == CDOType.DOUBLE || type == CDOType.DOUBLE_OBJECT)
    {
      return DBType.DOUBLE;
    }
    else if (type == CDOType.FLOAT || type == CDOType.FLOAT_OBJECT)
    {
      return DBType.FLOAT;
    }
    else if (type == CDOType.INT || type == CDOType.INTEGER_OBJECT)
    {
      return DBType.INTEGER;
    }
    else if (type == CDOType.LONG || type == CDOType.INTEGER_OBJECT)
    {
      return DBType.BIGINT;
    }
    else if (type == CDOType.OBJECT)
    {
      return DBType.BIGINT;
    }
    else if (type == CDOType.SHORT || type == CDOType.SHORT_OBJECT)
    {
      return DBType.SMALLINT;
    }
    else if (type == CDOType.STRING)
    {
      return DBType.LONGVARCHAR;
    }

    throw new ImplementationError("Unrecognized CDOType: " + type);
  }

  /**
   * @author Eike Stepper
   */
  public static final class ClassMapping
  {
    private CDOClass cdoClass;

    private Map<IDBTable, FeatureMapping[]> tables;

    public ClassMapping(CDOClass cdoClass)
    {
      this.cdoClass = cdoClass;
    }

    public CDOClass getCdoClass()
    {
      return cdoClass;
    }

    public Map<IDBTable, FeatureMapping[]> getTables()
    {
      return tables;
    }

    public void addFeatureMapping(CDOFeature feature, IDBField field)
    {
      if (tables == null)
      {
        tables = new HashMap();
      }

      IDBTable table = field.getTable();
      FeatureMapping featureMapping = new FeatureMapping(feature, field);
      FeatureMapping[] featureMappings = tables.get(table);
      if (featureMappings == null)
      {
        FeatureMapping[] newFeatureMappings = { featureMapping };
        tables.put(table, newFeatureMappings);
      }
      else
      {
        FeatureMapping[] newFeatureMappings = ObjectUtil.appendtoArray(featureMappings, featureMapping);
        tables.put(table, newFeatureMappings);
      }
    }

    public void sortFeatureMappings()
    {
      for (Entry<IDBTable, FeatureMapping[]> entry : tables.entrySet())
      {
        FeatureMapping[] featureMappings = entry.getValue();
        Arrays.sort(featureMappings);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class FeatureMapping implements Comparable
  {
    private CDOFeature feature;

    private IDBField field;

    public FeatureMapping(CDOFeature feature, IDBField field)
    {
      this.feature = feature;
      this.field = field;
    }

    public CDOFeature getFeature()
    {
      return feature;
    }

    public IDBField getField()
    {
      return field;
    }

    public IDBTable getTable()
    {
      return field.getTable();
    }

    @Override
    public String toString()
    {
      return MessageFormat.format("FeatureMapping[{0}, {1}]", feature, field);
    }

    public int compareTo(Object o)
    {
      if (o instanceof FeatureMapping)
      {
        FeatureMapping that = (FeatureMapping)o;
        return new Integer(field.getPosition()).compareTo(that.getField().getPosition());
      }

      throw new IllegalArgumentException("Not a FeatureMapping: " + o);
    }
  }
}