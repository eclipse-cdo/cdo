/*
 * Copyright (c) 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.common.lock;

import org.eclipse.emf.cdo.common.revision.CDOIDAndBranch;

import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;

/**
 * @author Eike Stepper
 * @since 4.15
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOLockDelta extends CDOIDAndBranch
{
  public Object getTarget();

  public LockType getType();

  public CDOLockOwner getOldOwner();

  public CDOLockOwner getNewOwner();

  public Kind getKind();

  /**
   * @author Eike Stepper
   */
  public enum Kind
  {
    ADDED, REMOVED, REMAPPED;
  }
}
