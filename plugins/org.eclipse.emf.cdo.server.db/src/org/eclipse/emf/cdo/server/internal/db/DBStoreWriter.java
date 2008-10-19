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

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDMeta;
import org.eclipse.emf.cdo.common.id.CDOIDMetaRange;
import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.CDOClassProxy;
import org.eclipse.emf.cdo.common.model.CDOFeature;
import org.eclipse.emf.cdo.common.model.CDOPackage;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.server.db.IClassMapping;
import org.eclipse.emf.cdo.server.db.IDBStoreWriter;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;
import org.eclipse.emf.cdo.spi.common.InternalCDOClass;
import org.eclipse.emf.cdo.spi.common.InternalCDOFeature;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.util.om.trace.ContextTracer;

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

  @Override
  public void commit()
  {
    super.commit();

    try
    {
      getConnection().commit();
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
  }

  @Override
  public void rollback(CommitContext context)
  {
    super.rollback(context);

    try
    {
      getConnection().rollback();
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
  }

  @Override
  protected void writePackages(CDOPackage... cdoPackages)
  {
    super.writePackages(cdoPackages);
    Set<IDBTable> affectedTables = mapPackages(cdoPackages);
    getStore().getDBAdapter().createTables(affectedTables, getConnection());
  }

  @Override
  protected void writePackage(CDOPackage cdoPackage)
  {
    int id = getStore().getNextPackageID();
    PackageServerInfo.setDBID(cdoPackage, id);
    if (TRACER.isEnabled())
    {
      TRACER.format("Writing package: {0} --> {1}", cdoPackage, id);
    }

    String packageURI = cdoPackage.getPackageURI();
    String name = cdoPackage.getName();
    String ecore = cdoPackage.getEcore();
    boolean dynamic = cdoPackage.isDynamic();
    CDOIDMetaRange metaIDRange = cdoPackage.getMetaIDRange();
    long lowerBound = metaIDRange == null ? 0L : ((CDOIDMeta)metaIDRange.getLowerBound()).getLongValue();
    long upperBound = metaIDRange == null ? 0L : ((CDOIDMeta)metaIDRange.getUpperBound()).getLongValue();
    String parentURI = cdoPackage.getParentURI();

    String sql = "INSERT INTO " + CDODBSchema.PACKAGES + " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
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
      pstmt.setLong(6, lowerBound);
      pstmt.setLong(7, upperBound);
      pstmt.setString(8, parentURI);

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

    super.writePackage(cdoPackage);
  }

  @Override
  protected void writeClass(InternalCDOClass cdoClass)
  {
    int id = getStore().getNextClassID();
    ClassServerInfo.setDBID(cdoClass, id);

    CDOPackage cdoPackage = cdoClass.getContainingPackage();
    int packageID = ServerInfo.getDBID(cdoPackage);
    int classifierID = cdoClass.getClassifierID();
    String name = cdoClass.getName();
    boolean isAbstract = cdoClass.isAbstract();
    DBUtil.insertRow(getConnection(), getStore().getDBAdapter(), CDODBSchema.CLASSES, id, packageID, classifierID,
        name, isAbstract);

    super.writeClass(cdoClass);
  }

  @Override
  protected void writeSuperType(InternalCDOClass type, CDOClassProxy superType)
  {
    int id = ClassServerInfo.getDBID(type);
    String packageURI = superType.getPackageURI();
    int classifierID = superType.getClassifierID();
    DBUtil.insertRow(getConnection(), getStore().getDBAdapter(), CDODBSchema.SUPERTYPES, id, packageURI, classifierID);
  }

  @Override
  protected void writeFeature(CDOFeature feature)
  {
    int id = getStore().getNextFeatureID();
    FeatureServerInfo.setDBID(feature, id);

    int classID = ServerInfo.getDBID(feature.getContainingClass());
    String name = feature.getName();
    int featureID = feature.getFeatureID();
    int type = feature.getType().getTypeID();
    CDOClassProxy reference = ((InternalCDOFeature)feature).getReferenceTypeProxy();
    String packageURI = reference == null ? null : reference.getPackageURI();
    int classifierID = reference == null ? 0 : reference.getClassifierID();
    boolean many = feature.isMany();
    boolean containment = feature.isContainment();
    int idx = feature.getFeatureIndex();
    DBUtil.insertRow(getConnection(), getStore().getDBAdapter(), CDODBSchema.FEATURES, id, classID, featureID, name,
        type, packageURI, classifierID, many, containment, idx);
  }

  @Override
  protected void writeRevisions(CDORevision[] revisions)
  {
    for (CDORevision revision : revisions)
    {
      writeRevision(revision);
    }
  }

  protected void writeRevision(CDORevision revision)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Writing revision: {0}", revision);
    }

    CDOClass cdoClass = revision.getCDOClass();
    IClassMapping mapping = getStore().getMappingStrategy().getClassMapping(cdoClass);
    mapping.writeRevision(this, revision);
  }

  @Override
  protected void detachObjects(CDOID[] detachedObjects, long revised)
  {
    for (CDOID id : detachedObjects)
    {
      detachObject(id, revised);
    }
  }

  /**
   * @param revised
   * @since 2.0
   */
  protected void detachObject(CDOID id, long revised)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Detaching object: {0}", id);
    }

    CDOClass cdoClass = getObjectType(id);
    IClassMapping mapping = getStore().getMappingStrategy().getClassMapping(cdoClass);
    mapping.detachObject(this, id, revised);
  }
}
