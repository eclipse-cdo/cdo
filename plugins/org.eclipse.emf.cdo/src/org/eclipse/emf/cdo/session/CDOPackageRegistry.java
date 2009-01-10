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

import org.eclipse.emf.cdo.common.model.CDOPackage;

import org.eclipse.emf.ecore.EPackage;

/**
 * @author Eike Stepper
 */
public interface CDOPackageRegistry extends EPackage.Registry
{
  /**
   * Registers a top level {@link EPackage} <b>and</b> its sub packages with this package registry.
   * 
   * @param ePackage
   *          a top level EPackage (i.e. a package with <code>eSuperPackage == null</code>).
   * @return the package that is registered under the <code>nsURI</code> of the top level package or <code>null</code>.
   * @throws IllegalArgumentException
   *           if the given EPackage is not a top level package.
   */
  public EPackage putEPackage(EPackage ePackage) throws IllegalArgumentException;

  public void putPackageDescriptor(CDOPackage proxy);

  /**
   * @since 2.0
   */
  public CDOSession getSession();

  public void setSession(CDOSession session);
}
