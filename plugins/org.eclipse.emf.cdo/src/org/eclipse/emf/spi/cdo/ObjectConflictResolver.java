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

import org.eclipse.emf.cdo.CDOConflictResolver;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;

import org.eclipse.emf.internal.cdo.CDOStateMachine;
import org.eclipse.emf.internal.cdo.InternalCDOObject;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public abstract class ObjectConflictResolver implements CDOConflictResolver
{
  public ObjectConflictResolver()
  {
  }

  public void resolveConflicts(CDOTransaction transaction, Set<CDOObject> conflicts)
  {
    Map<CDOID, CDORevisionDelta> revisionDeltas = transaction.getRevisionDeltas();
    for (Iterator<CDOObject> it = conflicts.iterator(); it.hasNext();)
    {
      CDOObject conflict = it.next();
      CDORevisionDelta revisionDelta = revisionDeltas.get(conflict.cdoID());
      if (resolveConflict(transaction, conflict, revisionDelta))
      {
        it.remove();
      }
    }
  }

  /**
   * Resolves the conflict of a single object in the current transaction and returns <code>true</code> if successful,
   * <code>false</code> otherwise.
   */
  protected abstract boolean resolveConflict(CDOTransaction transaction, CDOObject conflict,
      CDORevisionDelta revisionDelta);

  public static void rollbackObject(CDOObject object)
  {
    CDOStateMachine.INSTANCE.rollback((InternalCDOObject)object);
  }

  public static void changeObject(CDOObject object, CDORevisionDelta revisionDelta)
  {
    for (CDOFeatureDelta featureDelta : revisionDelta.getFeatureDeltas())
    {
      changeObject(object, featureDelta);
    }
  }

  public static void changeObject(CDOObject object, CDOFeatureDelta featureDelta)
  {
    CDOStateMachine.INSTANCE.write((InternalCDOObject)object, featureDelta);
  }
}
