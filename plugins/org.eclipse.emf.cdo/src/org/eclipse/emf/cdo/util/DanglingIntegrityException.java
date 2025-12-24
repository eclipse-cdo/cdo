/*
 * Copyright (c) 2013 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.util;

import org.eclipse.emf.cdo.transaction.CDOAutoAttacher;

import org.eclipse.emf.ecore.EObject;

/**
 * A local {@link DataIntegrityException data integrity exception} that indicates the addition of one or more cross references to objects
 * that are not (or no longer) contained in the repository.
 * <p>
 * The target objects of the respective dangling references must be attached to the repository.
 * A {@link CDOAutoAttacher} can help to do so.
 *
 * @author Eike Stepper
 * @since 4.2
 * @noextend This interface is not intended to be extended by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class DanglingIntegrityException extends DataIntegrityException
{
  private static final long serialVersionUID = 1L;

  public DanglingIntegrityException(DanglingReferenceException cause)
  {
    super(cause.getMessage(), cause);
  }

  @Override
  public synchronized DanglingReferenceException getCause()
  {
    return (DanglingReferenceException)super.getCause();
  }

  public EObject getTarget()
  {
    return getCause().getTarget();
  }

  @Override
  public boolean isLocal()
  {
    return true;
  }
}
