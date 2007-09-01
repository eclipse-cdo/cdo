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

import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.model.CDOClass;
import org.eclipse.emf.cdo.protocol.model.CDOClassRef;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IMapping;
import org.eclipse.emf.cdo.server.db.IMappingStrategy;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.IDBTable;
import org.eclipse.net4j.util.io.CloseableIterator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public abstract class MappingStrategy implements IMappingStrategy
{
  public static final String PROP_MAPPING_PRECEDENCE = "mappingPrecedence";

  public static final String PROP_TO_MANY_REFERENCE_MAPPING = "toManyReferenceMapping";

  public static final String PROP_TO_ONE_REFERENCE_MAPPING = "toOneReferenceMapping";

  private IDBStore store;

  private Map<String, String> properties;

  private Precedence precedence;

  private ToMany toMany;

  private ToOne toOne;

  private Map<Object, IDBTable> referenceTables = new HashMap<Object, IDBTable>();

  private Map<Integer, CDOClassRef> classRefs = new HashMap<Integer, CDOClassRef>();

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
      properties = new HashMap<String, String>();
    }

    return properties;
  }

  public void setProperties(Map<String, String> properties)
  {
    this.properties = properties;
  }

  public Precedence getPrecedence()
  {
    if (precedence == null)
    {
      String value = getProperties().get(PROP_MAPPING_PRECEDENCE);
      precedence = value == null ? Precedence.STRATEGY : Precedence.valueOf(value);
    }

    return precedence;
  }

  public ToMany getToMany()
  {
    if (toMany == null)
    {
      String value = getProperties().get(PROP_TO_MANY_REFERENCE_MAPPING);
      toMany = value == null ? ToMany.PER_REFERENCE : ToMany.valueOf(value);
    }

    return toMany;
  }

  public ToOne getToOne()
  {
    if (toOne == null)
    {
      String value = getProperties().get(PROP_TO_ONE_REFERENCE_MAPPING);
      toOne = value == null ? ToOne.LIKE_ATTRIBUTES : ToOne.valueOf(value);
    }

    return toOne;
  }

  public Map<Object, IDBTable> getReferenceTables()
  {
    return referenceTables;
  }

  public IMapping getMapping(CDOClass cdoClass)
  {
    IMapping mapping = ClassServerInfo.getMapping(cdoClass);
    if (mapping == NoMapping.INSTANCE)
    {
      return null;
    }

    if (mapping == null)
    {
      mapping = createMapping(cdoClass);
      ClassServerInfo.setMapping(cdoClass, mapping == null ? NoMapping.INSTANCE : mapping);
    }

    return mapping;
  }

  public CloseableIterator<CDOID> readObjectIDs(final IDBStoreAccessor storeAccessor, final boolean withTypes)
  {
    List<CDOClass> classes = getClassesWithObjectInfo();
    final Iterator<CDOClass> classIt = classes.iterator();
    return new ObjectIDIterator(this, storeAccessor, withTypes)
    {
      @Override
      protected ResultSet getNextResultSet()
      {
        while (classIt.hasNext())
        {
          CDOClass cdoClass = classIt.next();
          ValueMapping mapping = (ValueMapping)ClassServerInfo.getMapping(cdoClass);
          if (mapping != null)
          {
            IDBTable table = mapping.getTable();
            if (table != null)
            {
              StringBuilder builder = new StringBuilder();
              builder.append("SELECT DISTINCT ");
              builder.append(Mapping.FIELD_NAME_ID);
              if (withTypes)
              {
                builder.append(", ");
                builder.append(Mapping.FIELD_NAME_CLASS);
              }

              builder.append(" FROM ");
              builder.append(table);
              String sql = builder.toString();

              try
              {
                return storeAccessor.getStatement().executeQuery(sql);
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

  public CDOClassRef readObjectType(IDBStoreAccessor storeAccessor, CDOID id)
  {
    String prefix = "SELECT DISTINCT " + Mapping.FIELD_NAME_CLASS + " FROM ";
    String suffix = " WHERE " + Mapping.FIELD_NAME_ID + "=" + id;
    for (CDOClass cdoClass : getClassesWithObjectInfo())
    {
      ValueMapping mapping = (ValueMapping)ClassServerInfo.getMapping(cdoClass);
      if (mapping != null)
      {
        IDBTable table = mapping.getTable();
        if (table != null)
        {
          String sql = prefix + table + suffix;

          try
          {
            ResultSet resultSet = storeAccessor.getStatement().executeQuery(sql);
            if (resultSet.next())
            {
              int classID = resultSet.getInt(1);
              return getClassRef(storeAccessor, classID);
            }
          }
          catch (SQLException ex)
          {
            throw new DBException(ex);
          }
        }
      }
    }

    throw new DBException("No object with id " + id);
  }

  public CDOClassRef getClassRef(IDBStoreAccessor storeAccessor, int classID)
  {
    CDOClassRef classRef = classRefs.get(classID);
    if (classRef == null)
    {
      classRef = storeAccessor.readClassRef(classID);
      classRefs.put(classID, classRef);
    }

    return classRef;
  }

  @Override
  public String toString()
  {
    return getType();
  }

  protected abstract IMapping createMapping(CDOClass cdoClass);

  protected abstract List<CDOClass> getClassesWithObjectInfo();
}