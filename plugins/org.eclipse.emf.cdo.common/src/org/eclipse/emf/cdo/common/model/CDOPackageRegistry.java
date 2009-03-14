/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.common.model;

import org.eclipse.emf.ecore.EPackage;

/**
 * @author Eike Stepper
 */
public interface CDOPackageRegistry extends EPackage.Registry
{
  public boolean isReplacingDescriptors();

  /**
   * Registers an {@link EPackage} with this package registry.
   */
  public Object putEPackage(EPackage ePackage);

  public CDOPackageUnit getPackageUnit(EPackage ePackage);

  /**
   * Returns all package units that are registered in this package registry.
   */
  public CDOPackageUnit[] getPackageUnits();

  public CDOPackageInfo getPackageInfo(EPackage ePackage);

  /**
   * Returns all package infos that are registered in this package registry.
   */
  public CDOPackageInfo[] getPackageInfos();
}
