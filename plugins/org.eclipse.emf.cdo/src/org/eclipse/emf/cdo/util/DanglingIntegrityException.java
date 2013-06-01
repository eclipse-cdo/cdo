/*
 * Copyright (c) 2008, 2009, 2011, 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
  public DanglingReferenceException getCause()
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
