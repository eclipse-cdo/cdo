/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.session;

import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.CDOFeature;
import org.eclipse.emf.cdo.common.model.CDOPackage;
import org.eclipse.emf.cdo.common.model.CDOPackageManager;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * Represents the CDO {@link CDOPackage packages} currently stored in the {@link CDOSession.Repository repository} of a
 * {@link CDOSession session}. A package manager can be used to query information about the CDO {@link CDOPackage
 * packages} in the repository as well as convert between the EMF and CDO instances of these packages.
 * 
 * @author Eike Stepper
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOSessionPackageManager extends CDOPackageManager
{
  /**
   * Returns the session this package manager is associated with.
   * 
   * @since 2.0
   */
  public CDOSession getSession();

  /**
   * Returns the CDO instance of the given EMF package.
   */
  public CDOPackage convert(EPackage ePackage);

  /**
   * Returns the CDO instance of the given EMF class.
   */
  public CDOClass convert(EClass eClass);

  /**
   * Returns the CDO instance of the given EMF feature.
   */
  public CDOFeature convert(EStructuralFeature eFeature);

  /**
   * Returns the EMF instance of the given CDO package.
   */
  public EPackage convert(CDOPackage cdoPackage);

  /**
   * Returns the EMF instance of the given CDO class.
   */
  public EClass convert(CDOClass cdoClass);

  /**
   * Returns the EMF instance of the given CDO feature.
   */
  public EStructuralFeature convert(CDOFeature cdoFeature);
}
