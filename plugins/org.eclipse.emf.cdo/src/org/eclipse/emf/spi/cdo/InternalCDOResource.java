/*
 * Copyright (c) 2012, 2015, 2021 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.net4j.util.io.EncodingProvider;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.XMLResource;

/**
 * Internal protocol for the {@link CDOResource}.
 *
 * @since 4.2
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface InternalCDOResource extends CDOResource, Resource.Internal, XMLResource, EncodingProvider
{
  /**
   * Informs the resource that an {@code object} contained within it is being loaded.
   */
  public void cdoInternalLoading(EObject object);

  /**
   * Informs the resource that an {@code object} contained within it has finished being loaded.
   */
  public void cdoInternalLoadingDone(EObject object);
}
