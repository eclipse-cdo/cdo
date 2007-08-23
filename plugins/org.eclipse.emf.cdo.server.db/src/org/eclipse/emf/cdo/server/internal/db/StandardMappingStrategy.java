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

import org.eclipse.emf.cdo.internal.protocol.model.CDOClassImpl;
import org.eclipse.emf.cdo.internal.protocol.model.CDOFeatureImpl;
import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.model.CDOClass;
import org.eclipse.emf.cdo.protocol.model.CDOFeature;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;

import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBField;
import org.eclipse.net4j.db.IDBTable;
import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.ObjectUtil;

import java.sql.Connection;
import java.sql.Date;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Eike Stepper
 */
public abstract class StandardMappingStrategy extends MappingStrategy
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, StandardMappingStrategy.class);

  private Precedence precedence;

  private ToMany toMany;

  private ToOne toOne;

  private Map<CDOClass, ClassMapping> classMappings = new HashMap();

  private Map<Object, IDBTable> referenceTables = new HashMap();

  public StandardMappingStrategy()
  {
  }

  public Precedence getPrecedence()
  {
    if (precedence == null)
    {
      String value = getProperties().get("mappingPrecedence");
      precedence = value == null ? Precedence.STRATEGY : Precedence.valueOf(value);
    }

    return precedence;
  }

  public ToMany getToMany()
  {
    if (toMany == null)
    {
      String value = getProperties().get("toManyReferenceMapping");
      toMany = value == null ? ToMany.PER_REFERENCE : ToMany.valueOf(value);
    }

    return toMany;
  }

  public ToOne getToOne()
  {
    if (toOne == null)
    {
      String value = getProperties().get("toOneReferenceMapping");
      toOne = value == null ? ToOne.LIKE_ATTRIBUTES : ToOne.valueOf(value);
    }

    return toOne;
  }

  // public Set<IDBTable> map(CDOPackageImpl[] cdoPackages)
  // {
  // Set<IDBTable> affectedTables = new HashSet();
  // for (CDOPackageImpl cdoPackage : cdoPackages)
  // {
  // PackageServerInfo.setSchema(cdoPackage, getSchema());
  // if (TRACER.isEnabled())
  // {
  // TRACER.format("Mapped package: {0} --> {1}", cdoPackage, getSchema());
  // }
  // }
  //
  // for (CDOPackageImpl cdoPackage : cdoPackages)
  // {
  // for (CDOClass cdoClass : cdoPackage.getClasses())
  // {
  // if (TRACER.isEnabled())
  // {
  // TRACER.format("Mapping features of class {0}", cdoClass);
  // }
  //
  // for (CDOFeature cdoFeature : cdoClass.getAllFeatures())
  // {
  // IDBField field = mapFeature(cdoClass, cdoFeature, affectedTables);
  // if (field != null)
  // {
  // ((FeatureServerInfo)cdoFeature.getServerInfo()).addField(cdoClass, field);
  // affectedTables.add(field.getTable());
  // if (TRACER.isEnabled())
  // {
  // TRACER.format("Mapped feature: {0} --> {1}", cdoFeature, field);
  // }
  // }
  // }
  // }
  // }
  //
  // return affectedTables;
  // }
  //
  // /**
  // * @param affectedTables
  // * Can be used to indicate the creation or modification of additional
  // * tables. There is no need to add the table of the returned field to
  // * this set of affected tables. The framework takes care of that.
  // */
  // protected abstract IDBField mapFeature(CDOClass cdoClass, CDOFeature
  // cdoFeature, Set<IDBTable> affectedTables);
  //
  // protected IDBField mapAttribute(CDOClass cdoClass, CDOFeature cdoFeature)
  // {
  // IDBTable table = ClassServerInfo.getTable(cdoClass);
  // if (table == null)
  // {
  // table = addTable(cdoClass);
  // initTable(table, true);
  // ClassServerInfo.setTable(cdoClass, table);
  // }
  //
  // return addField(cdoFeature, table);
  //
  // // TODO Implement method StandardMappingStrategy.mapAttribute()
  // throw new UnsupportedOperationException("Not yet implemented");
  // }

  protected ClassMapping getClassMapping(CDOClass cdoClass)
  {
    ClassMapping classMapping = classMappings.get(cdoClass);
    if (classMapping == null)
    {
      classMapping = new ClassMapping(cdoClass);
      classMappings.put(cdoClass, classMapping);
      for (CDOFeature feature : cdoClass.getAllFeatures())
      {
        FeatureServerInfo featureInfo = (FeatureServerInfo)feature.getServerInfo();
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

  @Deprecated
  public void writeRevision(Connection connection, CDORevisionImpl revision)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Inserting revision: {0}", revision);
    }

    CDOClassImpl cdoClass = revision.getCDOClass();
    ClassMapping classMapping = getClassMapping(cdoClass);
    Map<IDBTable, FeatureMapping[]> tables = classMapping.getTables();
    Entry<IDBTable, FeatureMapping[]> entry = tables.entrySet().iterator().next();
    IDBTable table = entry.getKey();

    int i = 0;
    Object[] values = new Object[table.getFieldCount()];
    values[i++] = revision.getID().getValue();
    values[i++] = ServerInfo.getDBID(revision.getCDOClass());
    values[i++] = revision.getVersion();
    values[i++] = new Date(revision.getCreated());
    values[i++] = new Date(revision.getRevised());
    values[i++] = revision.getResourceID().getValue();
    values[i++] = revision.getContainerID().getValue();
    values[i++] = revision.getContainingFeatureID();

    for (CDOFeatureImpl feature : cdoClass.getAllFeatures())
    {
      Object value = revision.getValue(feature);
      if (value instanceof CDOID)
      {
        values[i++] = ((CDOID)value).getValue();
      }
      else
      {
        values[i++] = value;
      }
    }

    DBUtil.insertRow(connection, table, values);
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