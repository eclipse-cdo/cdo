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
package org.eclipse.emf.cdo.internal.common.model.core;

import org.eclipse.emf.cdo.common.model.core.CDOCorePackage;
import org.eclipse.emf.cdo.internal.common.model.CDOPackageImpl;
import org.eclipse.emf.cdo.internal.common.model.CDOPackageManagerImpl;

/**
 * @author Eike Stepper
 */
public final class CDOCorePackageImpl extends CDOPackageImpl implements CDOCorePackage
{
  private CDOObjectClassImpl cdoObjectClass;

  public CDOCorePackageImpl(CDOPackageManagerImpl packageManager)
  {
    super(packageManager, PACKAGE_URI, NAME, null, false, null, null);
    addClass(cdoObjectClass = new CDOObjectClassImpl(this));
  }

  public CDOObjectClassImpl getCDOObjectClass()
  {
    return cdoObjectClass;
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
