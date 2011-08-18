/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.net4j.util.concurrent;

import org.eclipse.net4j.util.concurrent.RWOLockManager.LockState;

import java.util.Collection;
import java.util.List;

/**
 * @author Caspar De Groot
 * @since 3.2
 */
public interface IRWOLockManager<OBJECT, CONTEXT> extends IRWLockManager<OBJECT, CONTEXT>
{
  public List<LockState<OBJECT, CONTEXT>> lock2(LockType type, CONTEXT context,
      Collection<? extends OBJECT> objectsToLock, long timeout) throws InterruptedException;

  public List<LockState<OBJECT, CONTEXT>> unlock2(LockType type, CONTEXT context,
      Collection<? extends OBJECT> objectsToUnlock);
}
