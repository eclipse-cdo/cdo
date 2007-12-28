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
package org.eclipse.net4j.util.lifecycle;

import org.eclipse.net4j.util.event.IEvent;

/**
 * @author Eike Stepper
 */
public interface ILifecycleEvent extends IEvent
{
  public ILifecycle getLifecycle();

  public Kind getKind();

  /**
   * @author Eike Stepper
   */
  public enum Kind
  {
    ABOUT_TO_ACTIVATE, ACTIVATED, ABOUT_TO_DEACTIVATE, DEACTIVATED
  }
}
