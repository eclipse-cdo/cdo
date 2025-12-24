/*
 * Copyright (c) 2007-2009, 2011, 2012, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.net4j.internal.util.bundle.AbstractBundle;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.event.Notifier;
import org.eclipse.net4j.util.io.IORunnable;
import org.eclipse.net4j.util.io.IORuntimeException;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.om.pref.OMPreference;
import org.eclipse.net4j.util.om.pref.OMPreferences;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Eike Stepper
 */
public class Preferences extends Notifier implements OMPreferences
{
  private AbstractBundle bundle;

  private Map<String, Preference<?>> prefs = new HashMap<>();

  private boolean loaded;

  private boolean dirty;

  public Preferences(AbstractBundle bundle)
  {
    this.bundle = bundle;
  }

  @Override
  public AbstractBundle getBundle()
  {
    return bundle;
  }

  public synchronized void load()
  {
    if (!loaded)
    {
      loaded = true;
      final Properties properties = new Properties();

      File file = getFile();
      if (file.exists())
      {
        IOUtil.safeInput(file, new IORunnable<FileInputStream>()
        {
          @Override
          public void run(FileInputStream io) throws IOException
          {
            properties.load(io);
          }
        });
      }

      for (Preference<?> preference : prefs.values())
      {
        String name = preference.getName();
        String value = properties.getProperty(name);
        preference.init(value);
      }
    }
  }

  @Override
  public synchronized void save()
  {
    if (dirty)
    {
      final Properties properties = new Properties();
      for (Preference<?> preference : prefs.values())
      {
        if (preference.isSet())
        {
          String name = preference.getName();
          String value = preference.getString();
          if (value != null)
          {
            properties.put(name, value);
          }
        }
      }

      File file = getFile();
      if (properties.isEmpty())
      {
        if (file.exists())
        {
          file.delete();
        }
      }
      else
      {
        IOUtil.safeOutput(file, new IORunnable<FileOutputStream>()
        {
          @Override
          public void run(FileOutputStream io) throws IOException
          {
            properties.store(io, "Preferences of " + bundle.getBundleID()); //$NON-NLS-1$
          }
        });
      }

      dirty = false;
    }
  }

  @Override
  public boolean isDirty()
  {
    return dirty;
  }

  @Override
  public OMPreference<Boolean> init(String name, boolean defaultValue)
  {
    return init(new BooleanPreference(this, name, defaultValue));
  }

  @Override
  public OMPreference<Integer> init(String name, int defaultValue)
  {
    return init(new IntegerPreference(this, name, defaultValue));
  }

  @Override
  public OMPreference<Long> init(String name, long defaultValue)
  {
    return init(new LongPreference(this, name, defaultValue));
  }

  @Override
  public OMPreference<Float> init(String name, float defaultValue)
  {
    return init(new FloatPreference(this, name, defaultValue));
  }

  @Override
  public OMPreference<Double> init(String name, double defaultValue)
  {
    return init(new DoublePreference(this, name, defaultValue));
  }

  @Override
  public OMPreference<String> init(String name, String defaultValue)
  {
    return init(new StringPreference(this, name, defaultValue));
  }

  @Override
  public OMPreference<String[]> init(String name, String[] defaultValue)
  {
    return init(new ArrayPreference(this, name, defaultValue));
  }

  @Override
  public OMPreference<byte[]> init(String name, byte[] defaultValue)
  {
    return init(new BytesPreference(this, name, defaultValue));
  }

  @Override
  public OMPreference<Boolean> initBoolean(String name)
  {
    return init(name, DEFAULT_BOOLEAN);
  }

  @Override
  public OMPreference<Integer> initInteger(String name)
  {
    return init(name, DEFAULT_INTEGER);
  }

  @Override
  public OMPreference<Long> initLong(String name)
  {
    return init(name, DEFAULT_LONG);
  }

  @Override
  public OMPreference<Float> initFloat(String name)
  {
    return init(name, DEFAULT_FLOAT);
  }

  @Override
  public OMPreference<Double> initDouble(String name)
  {
    return init(name, DEFAULT_DOUBLE);
  }

  @Override
  public OMPreference<String> initString(String name)
  {
    return init(name, DEFAULT_STRING);
  }

  @Override
  public OMPreference<String[]> initArray(String name)
  {
    return init(name, DEFAULT_ARRAY);
  }

  @Override
  public OMPreference<byte[]> initBytes(String name)
  {
    return init(name, DEFAULT_BYTES);
  }

  @Override
  public boolean contains(String name)
  {
    return prefs.containsKey(name);
  }

  @Override
  public OMPreference<?> get(String name)
  {
    return prefs.get(name);
  }

  @Override
  @SuppressWarnings("unchecked")
  public OMPreference<Boolean> getBoolean(String name)
  {
    return (OMPreference<Boolean>)get(name);
  }

  @Override
  @SuppressWarnings("unchecked")
  public OMPreference<Integer> getInteger(String name)
  {
    return (OMPreference<Integer>)get(name);
  }

  @Override
  @SuppressWarnings("unchecked")
  public OMPreference<Long> getLong(String name)
  {
    return (OMPreference<Long>)get(name);
  }

  @Override
  @SuppressWarnings("unchecked")
  public OMPreference<Float> getFloat(String name)
  {
    return (OMPreference<Float>)get(name);
  }

  @Override
  @SuppressWarnings("unchecked")
  public OMPreference<Double> getDouble(String name)
  {
    return (OMPreference<Double>)get(name);
  }

  @Override
  @SuppressWarnings("unchecked")
  public OMPreference<String> getString(String name)
  {
    return (OMPreference<String>)get(name);
  }

  @Override
  @SuppressWarnings("unchecked")
  public OMPreference<String[]> getArray(String name)
  {
    return (OMPreference<String[]>)get(name);
  }

  @Override
  @SuppressWarnings("unchecked")
  public OMPreference<byte[]> getBytes(String name)
  {
    return (OMPreference<byte[]>)get(name);
  }

  public <T> void fireChangeEvent(Preference<T> preference, T oldValue, T newValue)
  {
    dirty = true;
    IListener[] listeners = getListeners();
    if (listeners.length != 0)
    {
      fireEvent(new PreferencesChangeEvent<>(preference, oldValue, newValue), listeners);
    }
  }

  private <T> OMPreference<T> init(Preference<T> preference)
  {
    String name = preference.getName();
    if (prefs.containsKey(name))
    {
      throw new IllegalArgumentException("Duplicate name: " + name); //$NON-NLS-1$
    }

    prefs.put(name, preference);
    return preference;
  }

  private File getFile()
  {
    File file = new File(bundle.getStateLocation(), ".prefs"); //$NON-NLS-1$
    if (file.exists() && !file.isFile())
    {
      throw new IORuntimeException("Not a file: " + file.getAbsolutePath()); //$NON-NLS-1$
    }

    return file;
  }
}
