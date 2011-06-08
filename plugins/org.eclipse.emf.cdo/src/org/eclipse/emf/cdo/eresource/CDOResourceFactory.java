/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 213402
 */
package org.eclipse.emf.cdo.eresource;

import org.eclipse.emf.cdo.eresource.impl.CDOResourceFactoryImpl;

import org.eclipse.emf.ecore.resource.Resource;

/**
 * Creates {@link CDOResource} instances.
 * 
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOResourceFactory extends Resource.Factory
{
  /**
   * @since 4.0
   */
  public static final CDOResourceFactory INSTANCE = CDOResourceFactoryImpl.INSTANCE;
}
