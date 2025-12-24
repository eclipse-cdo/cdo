/*
 * Copyright (c) 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.event;

/**
 * An {@link IEvent} fired from {@link ValueNotifier value notifiers} after value changes.
 *
 * @author Eike Stepper
 * @since 3.1
 * @noextend This class is not intended to be subclassed by clients.
 */
public class ValueEvent<VALUE> extends Event
{
  private static final long serialVersionUID = 1L;

  private VALUE oldValue;

  private VALUE newValue;

  ValueEvent(ValueNotifier<VALUE> notifier, VALUE oldValue, VALUE newValue)
  {
    super(notifier);
    this.oldValue = oldValue;
    this.newValue = newValue;
  }

  @Override
  @SuppressWarnings("unchecked")
  public ValueNotifier<VALUE> getSource()
  {
    return (ValueNotifier<VALUE>)super.getSource();
  }

  public VALUE getOldValue()
  {
    return oldValue;
  }

  public VALUE getNewValue()
  {
    return newValue;
  }
}
