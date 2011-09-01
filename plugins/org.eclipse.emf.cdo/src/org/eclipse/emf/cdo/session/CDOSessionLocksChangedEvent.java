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
package org.eclipse.emf.cdo.session;

import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo;
import org.eclipse.emf.cdo.view.CDOView;

/**
 * @author Caspar De Groot
 * @since 4.1
 */
public interface CDOSessionLocksChangedEvent extends CDOSessionEvent, CDOLockChangeInfo
{
  /**
   * Returns the view that caused the lock changes if this view is local, or <code>null</code> if the view was remote.
   */
  public CDOView getSender();
}
