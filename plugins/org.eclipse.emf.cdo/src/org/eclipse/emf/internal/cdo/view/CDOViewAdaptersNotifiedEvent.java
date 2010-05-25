/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.internal.cdo.view;

import org.eclipse.emf.cdo.view.CDOViewEvent;
import org.eclipse.emf.cdo.view.CDOViewInvalidationEvent;

/**
 * @author Eike Stepper
 */
public interface CDOViewAdaptersNotifiedEvent extends CDOViewEvent
{
  /**
   * This is the time stamp of the commit operation that caused this client side event. It can be used to correlate this
   * event to the preceding {@link CDOViewInvalidationEvent invalidation event}.
   */
  public long getTimeStamp();
}
