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
package org.eclipse.emf.cdo;

import java.util.Set;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public interface CDOConflictResolver
{
  public static final CDOConflictResolver NOOP = new CDOConflictResolver()
  {
    public void resolveConflicts(CDOTransaction transaction, Set<CDOObject> conflicts)
    {
      // Do nothing
    }
  };

  /**
   * Resolves conflicts after remote invalidations arrived for objects that are locally dirty or detached.
   * <p>
   * The implementor might want to use API in {@link CDOConflictUtil}.
   * <p>
   * <b>Important:</b> Those conflicts that are resolved by the implementation of this method <b>must</b> be removed
   * from the passed set of conflicts!
   */
  public void resolveConflicts(CDOTransaction transaction, Set<CDOObject> conflicts);
}
