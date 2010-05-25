/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - 271444: [DB] Multiple refactorings https://bugs.eclipse.org/bugs/show_bug.cgi?id=271444  
 */
package org.eclipse.emf.cdo.server.db;

import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;

import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EPackage;

import java.sql.Connection;
import java.util.Collection;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public interface IMetaDataManager
{
  /**
   * Returns the meta ID of the given {@link EModelElement}. <code> getMetaID(getMetaInstance(x))</code> yields
   * <code>x</code>
   * 
   * @param modelElement
   *          the element
   * @return the corresponding ID
   * @since 2.0
   */
  public long getMetaID(EModelElement modelElement);

  /**
   * Returns the {@link EModelElement} referred to by the given ID. <code> getMetaInstance(getMetaID(m))</code> yields
   * <code>m</code>
   * 
   * @since 2.0
   */
  public EModelElement getMetaInstance(long id);

  /**
   * Loads a package unit from the database.
   * 
   * @param connection
   *          the DB connection to read from.
   * @param packageUnit
   *          the package unit to load.
   * @return the loaded package unit.
   * @since 2.0
   */
  public EPackage[] loadPackageUnit(Connection connection, InternalCDOPackageUnit packageUnit);

/**
   * Reads information about package units present in the database.
   * @param connection the DB connection to read from.
   * @return a collection of package unit information records which can be 
   *   passed to {@link IMetaDataManager#loadPackageUnit(Connection, InternalCDOPackageUnit))
   *   in order to read the EPackage.
   *   
   * @since 2.0
   */
  public Collection<InternalCDOPackageUnit> readPackageUnits(Connection connection);

  /**
   * Write package units to the database.
   * 
   * @param connection
   *          the DB connection to write to.
   * @param packageUnits
   *          the package units to write.
   * @param monitor
   *          the monitor to indicate progress.
   * @since 2.0
   */
  public void writePackageUnits(Connection connection, InternalCDOPackageUnit[] packageUnits, OMMonitor monitor);

  /**
   * Get the DB type associated with the given {@link EClassifier}.
   * 
   * @param eType
   *          the type to look up.
   * @return the {@link DBType} of the given {@link EClassifier}.
   * @since 2.0
   */
  public DBType getDBType(EClassifier eType);
}
