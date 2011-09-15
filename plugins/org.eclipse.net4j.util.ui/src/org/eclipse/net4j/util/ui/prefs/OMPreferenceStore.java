/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.ui.prefs;

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.internal.ui.bundle.OM;
import org.eclipse.net4j.util.om.pref.OMPreference;
import org.eclipse.net4j.util.om.pref.OMPreferences;
import org.eclipse.net4j.util.om.pref.OMPreferencesChangeEvent;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class OMPreferenceStore implements IPreferenceStore
{
  private OMPreferences preferences;

  private List<IPropertyChangeListener> listeners = new ArrayList<IPropertyChangeListener>(0);

  private IListener preferencesListener = new IListener()
  {
    public void notifyEvent(IEvent event)
    {
      if (event instanceof OMPreferencesChangeEvent<?>)
      {
        OMPreferencesChangeEvent<?> e = (OMPreferencesChangeEvent<?>)event;
        firePropertyChangeEvent(e.getPreference().getName(), e.getOldValue(), e.getNewValue());
      }
    }
  };

  public OMPreferenceStore(OMPreferences preferences)
  {
    this.preferences = preferences;
    preferences.addListener(preferencesListener);
  }

  public void dispose()
  {
    preferences.removeListener(preferencesListener);
  }

  public OMPreferences getPreferences()
  {
    return preferences;
  }

  public void addPropertyChangeListener(IPropertyChangeListener listener)
  {
    synchronized (listeners)
    {
      if (!listeners.contains(listener))
      {
        listeners.add(listener);
      }
    }
  }

  public void removePropertyChangeListener(IPropertyChangeListener listener)
  {
    synchronized (listeners)
    {
      listeners.remove(listener);
    }
  }

  public void firePropertyChangeEvent(String name, Object oldValue, Object newValue)
  {
    PropertyChangeEvent event = new PropertyChangeEvent(this, name, oldValue, newValue);
    for (IPropertyChangeListener listener : getPropertyChangeListenerListeners())
    {
      try
      {
        listener.propertyChange(event);
      }
      catch (RuntimeException ex)
      {
        OM.LOG.error(ex);
      }
    }
  }

  public boolean contains(String name)
  {
    return preferences.contains(name);
  }

  public boolean isDefault(String name)
  {
    return !getPreference(name).isSet();
  }

  public boolean needsSaving()
  {
    return preferences.isDirty();
  }

  public boolean getDefaultBoolean(String name)
  {
    return (Boolean)getPreference(name).getDefaultValue();
  }

  public double getDefaultDouble(String name)
  {
    return (Double)getPreference(name).getDefaultValue();
  }

  public float getDefaultFloat(String name)
  {
    return (Float)getPreference(name).getDefaultValue();
  }

  public int getDefaultInt(String name)
  {
    return (Integer)getPreference(name).getDefaultValue();
  }

  public long getDefaultLong(String name)
  {
    return (Long)getPreference(name).getDefaultValue();
  }

  public String getDefaultString(String name)
  {
    return (String)getPreference(name).getDefaultValue();
  }

  public void setDefault(String name, boolean value)
  {
    throw new UnsupportedOperationException();
  }

  public void setDefault(String name, double value)
  {
    throw new UnsupportedOperationException();
  }

  public void setDefault(String name, float value)
  {
    throw new UnsupportedOperationException();
  }

  public void setDefault(String name, int value)
  {
    throw new UnsupportedOperationException();
  }

  public void setDefault(String name, long value)
  {
    throw new UnsupportedOperationException();
  }

  public void setDefault(String name, String defaultObject)
  {
    throw new UnsupportedOperationException();
  }

  public void setToDefault(String name)
  {
    getPreference(name).unSet();
  }

  public boolean getBoolean(String name)
  {
    return (Boolean)getPreference(name).getValue();
  }

  public double getDouble(String name)
  {
    return (Double)getPreference(name).getValue();
  }

  public float getFloat(String name)
  {
    return (Float)getPreference(name).getValue();
  }

  public int getInt(String name)
  {
    return (Integer)getPreference(name).getValue();
  }

  public long getLong(String name)
  {
    return (Long)getPreference(name).getValue();
  }

  public String getString(String name)
  {
    return (String)getPreference(name).getValue();
  }

  public void putValue(String name, String value)
  {
    throw new UnsupportedOperationException();
  }

  @SuppressWarnings("unchecked")
  public void setValue(String name, boolean value)
  {
    OMPreference<Boolean> pref = (OMPreference<Boolean>)getPreference(name);
    pref.setValue(value);
  }

  @SuppressWarnings("unchecked")
  public void setValue(String name, double value)
  {
    OMPreference<Double> pref = (OMPreference<Double>)getPreference(name);
    pref.setValue(value);
  }

  @SuppressWarnings("unchecked")
  public void setValue(String name, float value)
  {
    OMPreference<Float> pref = (OMPreference<Float>)getPreference(name);
    pref.setValue(value);
  }

  @SuppressWarnings("unchecked")
  public void setValue(String name, int value)
  {
    OMPreference<Integer> pref = (OMPreference<Integer>)getPreference(name);
    pref.setValue(value);
  }

  @SuppressWarnings("unchecked")
  public void setValue(String name, long value)
  {
    OMPreference<Long> pref = (OMPreference<Long>)getPreference(name);
    pref.setValue(value);
  }

  @SuppressWarnings("unchecked")
  public void setValue(String name, String value)
  {
    OMPreference<String> pref = (OMPreference<String>)getPreference(name);
    pref.setValue(value);
  }

  private IPropertyChangeListener[] getPropertyChangeListenerListeners()
  {
    synchronized (listeners)
    {
      return listeners.toArray(new IPropertyChangeListener[listeners.size()]);
    }
  }

  private OMPreference<?> getPreference(String name)
  {
    OMPreference<?> pref = preferences.get(name);
    if (pref == null)
    {
      throw new IllegalStateException("No preference for " + name); //$NON-NLS-1$
    }

    return pref;
  }
}
