/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.lifecycle;

import org.eclipse.net4j.util.event.IEvent;

/**
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ILifecycleEvent extends IEvent
{
  /**
   * @since 3.0
   */
  public ILifecycle getSource();

  public Kind getKind();

  /**
   * @author Eike Stepper
   */
  public enum Kind
  {
    ABOUT_TO_ACTIVATE, ACTIVATED, ABOUT_TO_DEACTIVATE, DEACTIVATED
  }
}
