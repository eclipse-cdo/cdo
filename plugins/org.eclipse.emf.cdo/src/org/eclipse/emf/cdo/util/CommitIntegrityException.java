/*
 * Copyright (c) 2010-2013 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.util;

import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.ecore.EObject;

import java.util.Set;

/**
 * A local {@link DataIntegrityException data integrity exception} that indicates that the subset of object modifications in a
 * {@link CDOTransaction#setCommittables(Set) partial commit} is inconsistent.
 *
 * @author Caspar De Groot
 * @since 4.0
 * @noextend This interface is not intended to be extended by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class CommitIntegrityException extends DataIntegrityException
{
  private static final long serialVersionUID = 1L;

  private transient Set<? extends EObject> missingObjects;

  public CommitIntegrityException(String msg, Set<? extends EObject> missingObjects)
  {
    super(msg);
    this.missingObjects = missingObjects;
  }

  public Set<? extends EObject> getMissingObjects()
  {
    return missingObjects;
  }

  @Override
  public boolean isLocal()
  {
    return true;
  }
}
