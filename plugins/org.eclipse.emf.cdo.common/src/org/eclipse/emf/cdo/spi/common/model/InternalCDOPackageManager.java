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
package org.eclipse.emf.cdo.spi.common.model;

import org.eclipse.emf.cdo.common.model.CDOPackage;
import org.eclipse.emf.cdo.common.model.CDOPackageManager;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public interface InternalCDOPackageManager extends CDOPackageManager
{
  /**
   * @param cdoPackage
   *          A proxy CDO package.
   */
  public void loadPackage(CDOPackage cdoPackage);

  /**
   * @param cdoPackage
   *          A CDO package with <code>ecore == null</code>.
   */
  public void loadPackageEcore(CDOPackage cdoPackage);
}
