/*
 * Copyright (c) 2015, 2019, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.internal.cdo.view;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.view.CDOLockStatePrefetcher;

/**
 * The default {@link CDOLockStateLoadingPolicy lock state loading policy} which ask to load {@link CDOLockState lock state} for each loaded {@link CDORevision revision} with the same number of request to server as for revisions requests.
 *
 * @author Esteban Dugueperoux
 * @since 4.4
 * @deprecated As of 4.12 use {@link CDOLockStatePrefetcher#setObjectFilter(java.util.function.Predicate)}.
 */
@Deprecated
public class CDODefaultLockStateLoadingPolicy implements CDOLockStateLoadingPolicy
{
  @Deprecated
  @Override
  public boolean loadLockState(CDOID id)
  {
    return true;
  }
}
