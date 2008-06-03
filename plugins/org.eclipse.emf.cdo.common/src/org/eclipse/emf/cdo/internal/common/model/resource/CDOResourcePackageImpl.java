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
package org.eclipse.emf.cdo.internal.common.model.resource;

import org.eclipse.emf.cdo.common.model.resource.CDOResourcePackage;
import org.eclipse.emf.cdo.internal.common.model.CDOPackageImpl;
import org.eclipse.emf.cdo.internal.common.model.CDOPackageManagerImpl;

/**
 * @author Eike Stepper
 */
public final class CDOResourcePackageImpl extends CDOPackageImpl implements CDOResourcePackage
{
  private CDOResourceClassImpl cdoResourceClass;

  public CDOResourcePackageImpl(CDOPackageManagerImpl packageManager)
  {
    super(packageManager, PACKAGE_URI, NAME, null, false, null, null);
    addClass(cdoResourceClass = new CDOResourceClassImpl(this, packageManager));
  }

  public CDOResourceClassImpl getCDOResourceClass()
  {
    return cdoResourceClass;
  }

  @Override
  public String getEcore()
  {
    return null;
  }

  @Override
  public boolean isSystem()
  {
    return true;
  }
}
