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
package org.eclipse.emf.cdo.common.model;

import org.eclipse.emf.cdo.common.id.CDOIDMetaRange;

/**
 * @author Eike Stepper
 */
public interface CDOPackage extends CDOModelElement, Comparable<CDOPackage>
{
  public CDOPackageManager getPackageManager();

  public String getPackageURI();

  public int getClassCount();

  public CDOClass[] getClasses();

  public CDOClass[] getConcreteClasses();

  public CDOClass lookupClass(int classifierID);

  public String getEcore();

  public boolean isSystem();

  public boolean isDynamic();

  public boolean isProxy();

  public boolean isPersistent();

  public CDOIDMetaRange getMetaIDRange();

  public String getParentURI();

  public CDOPackage getParentPackage();

  public CDOPackage getTopLevelPackage();

  public CDOPackage[] getSubPackages(boolean recursive);
}
