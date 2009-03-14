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

import org.eclipse.emf.cdo.common.id.CDOIDMetaRange;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.ecore.EPackage;

/**
 * @author Eike Stepper
 */
public interface CDOPackageInfo extends Adapter, EPackage.Descriptor, Comparable<CDOPackageInfo>
{
  public CDOPackageUnit getPackageUnit();

  public String getPackageURI();

  public String getParentURI();

  public CDOIDMetaRange getMetaIDRange();

  public EPackage getEPackage(boolean loadOnDemand);

  public boolean isCorePackage();

  public boolean isResourcePackage();

  public boolean isSystemPackage();

}
