/*
 * Copyright (c) 2011, 2012, 2014, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.util;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.util.CDOException;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.CheckUtil;

/**
 * An unchecked exception being thrown when attempting to
 * {@link CDOView#lockObjects(java.util.Collection, org.eclipse.net4j.util.concurrent.IRWLockManager.LockType, long)
 * lock} <i>stale</i> objects.
 * <p>
 * An {@link CDOObject object} is considered stale if its {@link CDORevision revision} is older than the latest server
 * revision in the same {@link CDOBranch branch}.
 *
 * @author Caspar De Groot
 * @since 4.0
 * @noextend This interface is not intended to be extended by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class StaleRevisionLockException extends CDOException
{
  private static final long serialVersionUID = 5821185370877023119L;

  private final CDORevisionKey[] staleRevisions;

  public StaleRevisionLockException(CDORevisionKey[] staleRevisions)
  {
    CheckUtil.checkArg(staleRevisions, "staleRevisions"); //$NON-NLS-1$
    this.staleRevisions = staleRevisions;
  }

  public CDORevisionKey[] getStaleRevisions()
  {
    return staleRevisions;
  }
}
