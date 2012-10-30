/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Christian W. Damus (CEA) - initial API and implementation
 */
package org.eclipse.emf.spi.cdo;

import org.eclipse.emf.cdo.eresource.CDOResource;

import org.eclipse.emf.ecore.EObject;

/**
 * Internal protocol for the {@link CDOResource}.
 * 
 * @since 4.2
 */
public interface InternalCDOResource extends CDOResource
{
  /**
   * Informs the resource that an {@code object} contained within it is being loaded.
   */
  void cdoInternalLoading(EObject object);

  /**
   * Informs the resource that an {@code object} contained within it has finished being loaded.
   */
  void cdoInternalDoneLoading(EObject object);
}
