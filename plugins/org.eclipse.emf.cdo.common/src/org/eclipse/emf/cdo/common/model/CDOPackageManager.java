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

import org.eclipse.emf.cdo.common.id.CDOIDObjectFactory;
import org.eclipse.emf.cdo.common.model.core.CDOCorePackage;
import org.eclipse.emf.cdo.common.model.resource.CDOResourcePackage;

import org.eclipse.net4j.util.container.IContainer;

/**
 * @author Eike Stepper
 */
public interface CDOPackageManager extends IContainer<CDOPackage>
{
  public CDOIDObjectFactory getCDOIDObjectFactory();

  public int getPackageCount();

  public CDOPackage[] getPackages();

  public CDOPackage lookupPackage(String packageURI);

  public CDOCorePackage getCDOCorePackage();

  public CDOResourcePackage getCDOResourcePackage();
}
