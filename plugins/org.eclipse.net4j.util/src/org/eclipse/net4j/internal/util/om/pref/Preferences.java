/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.internal.util.om.pref;

import org.eclipse.net4j.internal.util.bundle.AbstractOMBundle;
import org.eclipse.net4j.internal.util.event.Notifier;
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
  public static final boolean DEFAULT_BOOLEAN = false;

  public static final int DEFAULT_INTEGER = 0;

  public static final long DEFAULT_LONG = 0L;

  public static final float DEFAULT_FLOAT = 0.0f;

  public static final double DEFAULT_DOUBLE = 0.0d;

  public static final String DEFAULT_STRING = "";

  private AbstractOMBundle bundle;

  private Map<String, Preference> prefs = new HashMap();

  private boolean loaded;

  private boolean dirty;

  public Preferences(AbstractOMBundle bundle)
  {
    this.bundle = bundle;
    load();
  }

  public AbstractOMBundle getBundle()
  {
    return bundle;
  }

  public synchronized void load()
  {
    if (!loaded)
    {
      loaded = true;
      File file = getFile();
      if (file.exists())
      {
        final Properties properties = new Properties();
        IOUtil.input(file, new IORunnable<FileInputStream>()
        {
          public void run(FileInputStream io) throws IOException
          {
            properties.load(io);
          }
        });

        for (Preference preference : prefs.values())
        {
          String name = preference.getName();
          String property = properties.getProperty(name);
          if (property != null)
          {
            preference.init(property);
          }
        }
      }
    }
  }

  public synchronized void save()
  {
    if (dirty)
    {
      final Properties properties = new Properties();
      File file = getFile();
      IOUtil.output(file, new IORunnable<FileOutputStream>()
      {
        public void run(FileOutputStream io) throws IOException
        {
          properties.store(io, "Preferences of " + bundle.getBundleID());
        }
      });

      dirty = false;
    }
  }

  public boolean isDirty()
  {
    return dirty;
  }

  public OMPreference<Boolean> init(String name, boolean defaultValue)
  {
    return new BooleanPreference(this, name, defaultValue);
  }

  public OMPreference<Integer> init(String name, int defaultValue)
  {
    return new IntegerPreference(this, name, defaultValue);
  }

  public OMPreference<Long> init(String name, long defaultValue)
  {
    return new LongPreference(this, name, defaultValue);
  }

  public OMPreference<Float> init(String name, float defaultValue)
  {
    return new FloatPreference(this, name, defaultValue);
  }

  public OMPreference<Double> init(String name, double defaultValue)
  {
    return new DoublePreference(this, name, defaultValue);
  }

  public OMPreference<String> init(String name, String defaultValue)
  {
    return new StringPreference(this, name, defaultValue);
  }

  public OMPreference<Boolean> initBoolean(String name)
  {
    return new BooleanPreference(this, name, DEFAULT_BOOLEAN);
  }

  public OMPreference<Integer> initInteger(String name)
  {
    return new IntegerPreference(this, name, DEFAULT_INTEGER);
  }

  public OMPreference<Long> initLong(String name)
  {
    return new LongPreference(this, name, DEFAULT_LONG);
  }

  public OMPreference<Float> initFloat(String name)
  {
    return new FloatPreference(this, name, DEFAULT_FLOAT);
  }

  public OMPreference<Double> initDouble(String name)
  {
    return new DoublePreference(this, name, DEFAULT_DOUBLE);
  }

  public OMPreference<String> initString(String name)
  {
    return new StringPreference(this, name, DEFAULT_STRING);
  }

  public OMPreference<Boolean> getBoolean(String name)
  {
    return null;
  }

  public OMPreference<Integer> getInteger(String name)
  {
    return null;
  }

  public OMPreference<Long> getLong(String name)
  {
    return null;
  }

  public OMPreference<Float> getFloat(String name)
  {
    return null;
  }

  public OMPreference<Double> getDouble(String name)
  {
    return null;
  }

  public OMPreference<String> getString(String name)
  {
    return null;
  }

  public <T> void fireChangeEvent(Preference<T> preference, T oldValue, T newValue)
  {
    fireEvent(new PreferencesChangeEvent<T>(preference, oldValue, newValue));
  }

  private File getFile()
  {
    File file = new File(bundle.getStateLocation(), ".prefs");
    if (!file.isFile())
    {
      throw new IORuntimeException("Not a file: " + file.getAbsolutePath());
    }

    return file;
  }
}
