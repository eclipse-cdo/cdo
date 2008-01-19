/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
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
import org.eclipse.emf.cdo.internal.protocol.model.CDOClassProxy;
import org.eclipse.emf.cdo.internal.protocol.model.CDOFeatureImpl;
import org.eclipse.emf.cdo.internal.protocol.model.CDOPackageImpl;
import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.protocol.CDOIDRange;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.server.db.IClassMapping;
import org.eclipse.emf.cdo.server.db.IDBStoreWriter;
import org.eclipse.emf.cdo.server.db.IMappingStrategy;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.internal.util.om.trace.ContextTracer;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class DBStoreWriter extends DBStoreReader implements IDBStoreWriter
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, DBStoreWriter.class);

  public DBStoreWriter(DBStore store, IView view) throws DBException
  {
    super(store, view);
  }

  public void writePackages(CDOPackageImpl... cdoPackages)
  {
    for (CDOPackageImpl cdoPackage : cdoPackages)
    {
      writePackage(cdoPackage);
    }

    Set<IDBTable> affectedTables = mapPackages(cdoPackages);
    getStore().getDBAdapter().createTables(affectedTables, getConnection());
  }

  protected void writePackage(CDOPackageImpl cdoPackage)
  {
    int id = getStore().getNextPackageID();
    PackageServerInfo.setDBID(cdoPackage, id);
    if (TRACER.isEnabled())
    {
      TRACER.format("Inserting package: {0} --> {1}", cdoPackage, id);
    }

    String packageURI = cdoPackage.getPackageURI();
    String name = cdoPackage.getName();
    String ecore = cdoPackage.getEcore();
    boolean dynamic = cdoPackage.isDynamic();
    CDOIDRange metaIDRange = cdoPackage.getMetaIDRange();
    long lb = metaIDRange == null ? 0L : metaIDRange.getLowerBound().getValue();
    long ub = metaIDRange == null ? 0L : metaIDRange.getUpperBound().getValue();

    String sql = "INSERT INTO " + CDODBSchema.PACKAGES + " VALUES (?, ?, ?, ?, ?, ?, ?)";
    DBUtil.trace(sql);
    PreparedStatement pstmt = null;

    try
    {
      pstmt = getConnection().prepareStatement(sql);
      pstmt.setInt(1, id);
      pstmt.setString(2, packageURI);
      pstmt.setString(3, name);
      pstmt.setString(4, ecore);
      pstmt.setBoolean(5, dynamic);
      pstmt.setLong(6, lb);
      pstmt.setLong(7, ub);

      if (pstmt.execute())
      {
        throw new DBException("No result set expected");
      }

      if (pstmt.getUpdateCount() == 0)
      {
        throw new DBException("No row inserted into table " + CDODBSchema.PACKAGES);
      }
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      DBUtil.close(pstmt);
    }

    for (CDOClassImpl cdoClass : cdoPackage.getClasses())
    {
      writeClass(cdoClass);
    }
  }

  protected void writeClass(CDOClassImpl cdoClass)
  {
    int id = getStore().getNextClassID();
    ClassServerInfo.setDBID(cdoClass, id);

    CDOPackageImpl cdoPackage = cdoClass.getContainingPackage();
    int packageID = ServerInfo.getDBID(cdoPackage);
    int classifierID = cdoClass.getClassifierID();
    String name = cdoClass.getName();
    boolean isAbstract = cdoClass.isAbstract();
    DBUtil.insertRow(getConnection(), getStore().getDBAdapter(), CDODBSchema.CLASSES, id, packageID, classifierID,
        name, isAbstract);

    for (CDOClassProxy superType : cdoClass.getSuperTypeProxies())
    {
      writeSuperType(id, superType);
    }

    for (CDOFeatureImpl feature : cdoClass.getFeatures())
    {
      writeFeature(feature);
    }
  }

  protected void writeSuperType(int type, CDOClassProxy superType)
  {
    String packageURI = superType.getPackageURI();
    int classifierID = superType.getClassifierID();
    DBUtil
        .insertRow(getConnection(), getStore().getDBAdapter(), CDODBSchema.SUPERTYPES, type, packageURI, classifierID);
  }

  protected void writeFeature(CDOFeatureImpl feature)
  {
    int id = getStore().getNextFeatureID();
    FeatureServerInfo.setDBID(feature, id);

    int classID = ServerInfo.getDBID(feature.getContainingClass());
    String name = feature.getName();
    int featureID = feature.getFeatureID();
    int type = feature.getType().getTypeID();
    CDOClassProxy reference = feature.getReferenceTypeProxy();
    String packageURI = reference == null ? null : reference.getPackageURI();
    int classifierID = reference == null ? 0 : reference.getClassifierID();
    boolean many = feature.isMany();
    boolean containment = feature.isContainment();
    int idx = feature.getFeatureIndex();
    DBUtil.insertRow(getConnection(), getStore().getDBAdapter(), CDODBSchema.FEATURES, id, classID, featureID, name,
        type, packageURI, classifierID, many, containment, idx);
  }

  public void writeRevision(CDORevisionImpl revision)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Inserting revision: {0}", revision);
    }

    CDOClassImpl cdoClass = revision.getCDOClass();
    IMappingStrategy mappingStrategy = getStore().getMappingStrategy();
    IClassMapping mapping = mappingStrategy.getClassMapping(cdoClass);
    mapping.writeRevision(this, revision);
  }

  public void commit()
  {
    try
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Committing transaction: {0}", getView());
      }

      getConnection().commit();
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
  }

  public void rollback()
  {
    try
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Rolling back transaction: {0}", getView());
      }

      getConnection().rollback();
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
  }
}
