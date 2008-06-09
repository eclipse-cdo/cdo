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
package org.eclipse.emf.cdo;

import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.CDOFeature;
import org.eclipse.emf.cdo.common.model.CDOPackage;
import org.eclipse.emf.cdo.common.model.CDOPackageManager;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * @author Eike Stepper
 */
public interface CDOSessionPackageManager extends CDOPackageManager
{
  public CDOPackage convert(EPackage ePackage);

  public CDOClass convert(EClass eClass);

  public CDOFeature convert(EStructuralFeature eFeature);

  public EPackage convert(CDOPackage cdoPackage);

  public EClass convert(CDOClass cdoClass);

  public EStructuralFeature convert(CDOFeature cdoFeature);
}
