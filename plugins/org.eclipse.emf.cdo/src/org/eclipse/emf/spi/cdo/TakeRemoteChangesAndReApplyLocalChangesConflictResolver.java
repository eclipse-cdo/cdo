/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.spi.cdo;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;

/**
 * A conflict resolver implementation that takes all the new remote state of the conflicting objects and then applies
 * the locally existing changes of the current transaction.
 * 
 * @author Eike Stepper
 * @since 2.0
 */
public class TakeRemoteChangesAndReApplyLocalChangesConflictResolver extends ObjectConflictResolver
{
  public TakeRemoteChangesAndReApplyLocalChangesConflictResolver()
  {
  }

  @Override
  protected boolean resolveConflict(CDOTransaction transaction, CDOObject conflict, CDORevisionDelta revisionDelta)
  {
    rollbackObject(conflict);
    changeObject(conflict, revisionDelta);
    return true;
  }
}
