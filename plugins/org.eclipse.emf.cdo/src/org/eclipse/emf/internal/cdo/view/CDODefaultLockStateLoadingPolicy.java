/*
 * Copyright (c) 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.internal.cdo.view;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.revision.CDORevision;

/**
 * The default {@link CDOLockStateLoadingPolicy lock state loading policy} which ask to load {@link CDOLockState lock state} for each loaded {@link CDORevision revision} with the same number of request to server as for revisions requests.
 *
 * @author Esteban Dugueperoux
 * @since 4.4
 */
public class CDODefaultLockStateLoadingPolicy implements CDOLockStateLoadingPolicy
{
  @Override
  public boolean loadLockState(CDOID id)
  {
    return true;
  }
}
