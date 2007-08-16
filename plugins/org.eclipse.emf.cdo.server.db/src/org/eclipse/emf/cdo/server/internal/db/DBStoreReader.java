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

import org.eclipse.emf.cdo.internal.protocol.CDOIDRangeImpl;
import org.eclipse.emf.cdo.internal.protocol.model.CDOClassImpl;
import org.eclipse.emf.cdo.internal.protocol.model.CDOClassProxy;
import org.eclipse.emf.cdo.internal.protocol.model.CDOClassRefImpl;
import org.eclipse.emf.cdo.internal.protocol.model.CDOFeatureImpl;
import org.eclipse.emf.cdo.internal.protocol.model.CDOPackageImpl;
import org.eclipse.emf.cdo.internal.protocol.model.CDOTypeImpl;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.CDOIDRange;
import org.eclipse.emf.cdo.protocol.model.CDOClassRef;
import org.eclipse.emf.cdo.protocol.model.CDOType;
import org.eclipse.emf.cdo.protocol.revision.CDORevision;
import org.eclipse.emf.cdo.server.IStoreReader;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBRowHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Eike Stepper
 */
public class DBStoreReader implements IStoreReader
{
  protected DBStore store;

  protected Connection connection;

  public DBStoreReader(DBStore store) throws DBException
  {
    this.store = store;

    try
    {
      connection = store.getDataSource().getConnection();
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
  }

  public void release() throws DBException
  {
    try
    {
      connection.close();
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
  }

  public DBStore getStore()
  {
    return store;
  }

  public Collection<PackageInfo> readPackageInfos()
  {
    final Collection<PackageInfo> result = new ArrayList(0);
    IDBRowHandler rowHandler = new IDBRowHandler()
    {
      public boolean handle(int row, final Object... values)
      {
        result.add(new PackageInfo()
        {
          public String getPackageURI()
          {
            return (String)values[0];
          }

          public boolean isDynamic()
          {
            return values[1] != null;
          }

          public CDOIDRange getMetaIDRange()
          {
            long rangeLB = (Long)values[2];
            long rangeUB = (Long)values[3];
            return CDOIDRangeImpl.create(rangeLB, rangeUB);
          }
        });

        return true;
      }
    };

    DBUtil.select(connection, rowHandler, CDODBSchema.PACKAGES_URI, CDODBSchema.PACKAGES_DYNAMIC,
        CDODBSchema.PACKAGES_RANGE_LB, CDODBSchema.PACKAGES_RANGE_UB);
    return result;
  }

  public void readPackage(final CDOPackageImpl cdoPackage)
  {
    Object[] values = DBUtil.select(connection, "", CDODBSchema.PACKAGES_ID, CDODBSchema.PACKAGES_NAME,
        CDODBSchema.PACKAGES_ECORE);
    cdoPackage.setServerInfo(values[0]);
    cdoPackage.setName((String)values[1]);
    cdoPackage.setEcore((String)values[2]);

    IDBRowHandler rowHandler = new IDBRowHandler()
    {
      public boolean handle(int row, Object... values)
      {
        int classifierID = (Integer)values[1];
        String name = (String)values[2];
        boolean isAbstract = (Boolean)values[3];
        CDOClassImpl cdoClass = new CDOClassImpl(cdoPackage, classifierID, name, isAbstract);
        cdoClass.setServerInfo(values[0]);
        cdoPackage.addClass(cdoClass);
        readClass(cdoClass);
        return true;
      }
    };

    String where = CDODBSchema.CLASSES_PACKAGE.toString() + " = '" + cdoPackage.getPackageURI() + "'";
    DBUtil.select(connection, rowHandler, where, CDODBSchema.CLASSES_ID, CDODBSchema.CLASSES_CLASSIFIER,
        CDODBSchema.CLASSES_NAME, CDODBSchema.CLASSES_ABSTRACT);
  }

  public void readClass(final CDOClassImpl cdoClass)
  {
    int classID = (Integer)cdoClass.getServerInfo();
    readSuperTypes(cdoClass, classID);
    readFeatures(cdoClass, classID);
  }

  public void readSuperTypes(final CDOClassImpl cdoClass, int classID)
  {
    IDBRowHandler rowHandler = new IDBRowHandler()
    {
      public boolean handle(int row, Object... values)
      {
        String packageURI = (String)values[0];
        int classifierID = (Integer)values[1];
        cdoClass.addSuperType(new CDOClassRefImpl(packageURI, classifierID));
        return true;
      }
    };

    String where = CDODBSchema.SUPERTYPES_TYPE.toString() + " = " + classID;
    DBUtil.select(connection, rowHandler, where, CDODBSchema.SUPERTYPES_SUPERTYPE_PACKAGE,
        CDODBSchema.SUPERTYPES_SUPERTYPE_CLASSIFIER);
  }

  public void readFeatures(final CDOClassImpl cdoClass, int classID)
  {
    IDBRowHandler rowHandler = new IDBRowHandler()
    {
      public boolean handle(int row, Object... values)
      {
        int featureID = (Integer)values[1];
        String name = (String)values[2];
        CDOTypeImpl type = CDOTypeImpl.getType((Integer)values[3]);
        boolean many = (Boolean)values[6];

        CDOFeatureImpl feature;
        if (type == CDOType.OBJECT)
        {
          String packageURI = (String)values[4];
          int classifierID = (Integer)values[5];
          boolean containment = (Boolean)values[7];
          CDOClassRefImpl classRef = new CDOClassRefImpl(packageURI, classifierID);
          CDOClassProxy referenceType = new CDOClassProxy(classRef, cdoClass.getPackageManager());
          feature = new CDOFeatureImpl(cdoClass, featureID, name, referenceType, many, containment);
        }
        else
        {
          feature = new CDOFeatureImpl(cdoClass, featureID, name, type, many);
        }

        feature.setServerInfo(values[0]);
        cdoClass.addFeature(feature);
        return true;
      }
    };

    String where = CDODBSchema.FEATURES_CLASS.toString() + " = " + classID;
    DBUtil.select(connection, rowHandler, where, CDODBSchema.FEATURES_ID, CDODBSchema.FEATURES_FEATURE,
        CDODBSchema.FEATURES_NAME, CDODBSchema.FEATURES_TYPE, CDODBSchema.FEATURES_REFERENCE_PACKAGE,
        CDODBSchema.FEATURES_REFERENCE_CLASSIFIER, CDODBSchema.FEATURES_MANY, CDODBSchema.FEATURES_CONTAINMENT);
  }

  public CDORevision readRevision(CDOID id, long timeStamp)
  {
    // TODO Implement method DBStoreReader.readRevision()
    throw new UnsupportedOperationException("Not yet implemented");
  }

  public CDOID readResourceID(String path)
  {
    // TODO Implement method DBStoreReader.readResourceID()
    throw new UnsupportedOperationException("Not yet implemented");
  }

  public String readResourcePath(CDOID id)
  {
    // TODO Implement method DBStoreReader.readResourcePath()
    throw new UnsupportedOperationException("Not yet implemented");
  }

  public CDORevision readRevision(CDOID id)
  {
    // TODO Implement method DBStoreReader.readRevision()
    throw new UnsupportedOperationException("Not yet implemented");
  }

  public CDOClassRef readObjectType(CDOID id)
  {
    // TODO Implement method DBStoreReader.readObjectType()
    throw new UnsupportedOperationException("Not yet implemented");
  }
}
