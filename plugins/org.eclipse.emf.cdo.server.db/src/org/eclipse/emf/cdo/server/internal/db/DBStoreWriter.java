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
import org.eclipse.emf.cdo.internal.protocol.model.CDOClassProxy;
import org.eclipse.emf.cdo.internal.protocol.model.CDOFeatureImpl;
import org.eclipse.emf.cdo.internal.protocol.model.CDOPackageImpl;
import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.protocol.CDOIDRange;
import org.eclipse.emf.cdo.server.IStoreWriter;
import org.eclipse.emf.cdo.server.IView;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;

import java.sql.SQLException;

/**
 * @author Eike Stepper
 */
public class DBStoreWriter extends DBStoreReader implements IStoreWriter
{
  private IView view;

  public DBStoreWriter(DBStore store, IView view) throws DBException
  {
    super(store);
    this.view = view;

    try
    {
      connection.setAutoCommit(false);
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
  }

  @Override
  public void release() throws DBException
  {
    try
    {
      connection.commit();
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }

    super.release();
  }

  public IView getView()
  {
    return view;
  }

  public void writePackage(CDOPackageImpl cdoPackage)
  {
    int id = store.getNextPackageID();
    cdoPackage.setServerInfo(id);

    CDOIDRange metaIDRange = cdoPackage.getMetaIDRange();
    long lb = metaIDRange.getLowerBound().getValue();
    long ub = metaIDRange.getUpperBound().getValue();

    DBUtil.insertRow(connection, CDODBSchema.PACKAGES, id, cdoPackage.getPackageURI(), cdoPackage.getName(), cdoPackage
        .getEcore(), cdoPackage.isDynamic(), lb, ub);

    for (CDOClassImpl cdoClass : cdoPackage.getClasses())
    {
      writeClass(cdoClass);
    }
  }

  public void writeClass(CDOClassImpl cdoClass)
  {
    int id = store.getNextClassID();
    cdoClass.setServerInfo(id);

    CDOPackageImpl cdoPackage = cdoClass.getContainingPackage();
    DBUtil.insertRow(connection, CDODBSchema.CLASSES, id, cdoPackage.getPackageURI(), cdoClass.getClassifierID(),
        cdoClass.getName(), cdoClass.isAbstract());

    for (CDOClassProxy superType : cdoClass.getSuperTypeProxies())
    {
      writeSuperType(id, superType);
    }

    for (CDOFeatureImpl feature : cdoClass.getFeatures())
    {
      writeFeature(feature);
    }
  }

  public void writeSuperType(int type, CDOClassProxy superType)
  {
    DBUtil.insertRow(connection, CDODBSchema.SUPERTYPES, type, superType.getPackageURI(), superType.getClassifierID());
  }

  public void writeFeature(CDOFeatureImpl feature)
  {
    int id = store.getNextFeatureID();
    feature.setServerInfo(id);

    CDOClassProxy reference = feature.getReferenceTypeProxy();
    DBUtil.insertRow(connection, CDODBSchema.FEATURES, id, feature.getName(), reference.getPackageURI(), reference
        .getClassifierID(), feature.isMany(), feature.isContainment(), feature.getFeatureIndex());
  }

  public void writeRevision(CDORevisionImpl revision)
  {
  }
}
