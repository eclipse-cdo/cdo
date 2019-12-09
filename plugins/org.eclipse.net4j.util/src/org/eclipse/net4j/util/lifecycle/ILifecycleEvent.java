/*
 * Copyright (c) 2007, 2009, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
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
 * An {@link IEvent event} fired from an entity with a {@link ILifecycle lifecycle} when its lifecycle {@link Kind
 * state} has changed.
 *
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement Thi import org.eclipse.net4j.util.event.IEvent; import org.eclipse.net4j.util.event.IEvent; import
 *              org.eclipse.net4j.util.event.IEvent; s interface is not intended to be implemented by clients.
 */
public interface ILifecycleEvent extends IEvent
{
  /**
   * @since 3.0
   */
  public ILifecycle getSource();

  public Kind getKind();

  /**
   * Enumerates the possible {@link ILifecycle#getLifecycleState() lifecycle state} changes of an entity.
   *
   * @author Eike Stepper
   */
  public enum Kind
  {
    ABOUT_TO_ACTIVATE, ACTIVATED, ABOUT_TO_DEACTIVATE, DEACTIVATED
  }
}
