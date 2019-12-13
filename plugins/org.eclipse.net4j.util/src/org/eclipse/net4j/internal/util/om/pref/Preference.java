/*
 * Copyright (c) 2007, 2009, 2011, 2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.util.om.pref;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.om.pref.OMPreference;

/**
 * @author Eike Stepper
 */
public abstract class Preference<T> implements OMPreference<T>
{
  private Preferences preferences;

  private String name;

  private T defaultValue;

  private T value;

  public Preference(Preferences preferences, String name, T defaultValue)
  {
    if (defaultValue == null)
    {
      throw new IllegalArgumentException("defaultValue == null"); //$NON-NLS-1$
    }

    this.preferences = preferences;
    this.name = name;
    this.defaultValue = defaultValue;
  }

  @Override
  public Preferences getPreferences()
  {
    return preferences;
  }

  @Override
  public String getName()
  {
    return name;
  }

  @Override
  public T getDefaultValue()
  {
    return defaultValue;
  }

  @Override
  public T getValue()
  {
    load();
    return value;
  }

  @Override
  public T setValue(T value)
  {
    if (value == null)
    {
      throw new IllegalArgumentException("value == null"); //$NON-NLS-1$
    }

    load();
    T oldValue = this.value;
    if (!equals(oldValue, value))
    {
      if (equals(defaultValue, value))
      {
        value = defaultValue;
      }

      this.value = value;
      preferences.fireChangeEvent(this, oldValue, value);
      return oldValue;
    }

    return null;
  }

  @Override
  public boolean isSet()
  {
    return !equals(defaultValue, value);
  }

  @Override
  public T unSet()
  {
    return setValue(defaultValue);
  }

  @Override
  public String toString()
  {
    return getName() + " = " + getValue();
  }

  protected boolean equals(T v1, T v2)
  {
    return ObjectUtil.equals(v1, v2);
  }

  protected void init(String value)
  {
    if (value == null)
    {
      this.value = defaultValue;
    }
    else
    {
      this.value = convert(value);
    }
  }

  protected abstract T convert(String value);

  protected abstract String getString();

  private void load()
  {
    preferences.load();
  }
}
