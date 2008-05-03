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
package org.eclipse.emf.cdo.util;

import org.eclipse.net4j.util.registry.IRegistry;

import org.eclipse.emf.ecore.EPackage;

/**
 * Can only be used with Eclipse running!
 * 
 * @author Eike Stepper
 */
public interface CDOPackageTypeRegistry extends IRegistry<String, CDOPackageType>
{
  public static final CDOPackageTypeRegistry INSTANCE = org.eclipse.emf.internal.cdo.util.CDOPackageTypeRegistryImpl.INSTANCE;

  public CDOPackageType getPackageType(EPackage ePackage);

  public void register(EPackage ePackage);

  public void registerLegacy(String uri);

  public void registerNative(String uri);

  public void registerConverted(String uri);
}
