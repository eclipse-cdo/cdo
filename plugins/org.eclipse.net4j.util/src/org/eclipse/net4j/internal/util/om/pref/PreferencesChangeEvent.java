/*
 * Copyright (c) 2007, 2008, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.util.om.pref;

import org.eclipse.net4j.util.event.Event;
import org.eclipse.net4j.util.om.pref.OMPreferencesChangeEvent;

/**
 * @author Eike Stepper
 */
public final class PreferencesChangeEvent<T> extends Event implements OMPreferencesChangeEvent<T>
{
  private static final long serialVersionUID = 1L;

  private Preference<T> preference;

  private T oldValue;

  private T newValue;

  public PreferencesChangeEvent(Preference<T> preference, T oldValue, T newValue)
  {
    super(preference.getPreferences());
    this.preference = preference;
    this.oldValue = oldValue;
    this.newValue = newValue;
  }

  @Override
  public Preferences getSource()
  {
    return (Preferences)super.getSource();
  }

  @Override
  public Preference<T> getPreference()
  {
    return preference;
  }

  @Override
  public T getOldValue()
  {
    return oldValue;
  }

  @Override
  public T getNewValue()
  {
    return newValue;
  }
}
