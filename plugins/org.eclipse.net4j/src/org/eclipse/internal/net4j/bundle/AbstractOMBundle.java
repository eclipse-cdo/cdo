/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.internal.net4j.bundle;

import org.eclipse.net4j.util.IOUtil;
import org.eclipse.net4j.util.ReflectUtil;
import org.eclipse.net4j.util.om.OMBundle;
import org.eclipse.net4j.util.om.OMLogger;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.OMTracer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Eike Stepper
 */
public abstract class AbstractOMBundle implements OMBundle
{
  private AbstractOMPlatform platform;

  private String bundleID;

  private Class accessor;

  private Object bundleContext;

  private boolean debugging;

  private boolean debuggingInitialized;

  private Map<String, OMTracerImpl> tracers = new ConcurrentHashMap();

  private OMLogger logger;

  public ResourceBundle resourceBundle;

  public ResourceBundle untranslatedResourceBundle;

  public Map<String, String> strings = new HashMap();

  public Map<String, String> untranslatedStrings = new HashMap();

  public boolean shouldTranslate = true;

  public AbstractOMBundle(AbstractOMPlatform platform, String bundleID, Class accessor)
  {
    this.platform = platform;
    this.bundleID = bundleID;
    this.accessor = accessor;
  }

  public OMPlatform getPlatform()
  {
    return platform;
  }

  public String getBundleID()
  {
    return bundleID;
  }

  public Class getAccessor()
  {
    return accessor;
  }

  public Object getBundleContext()
  {
    return bundleContext;
  }

  public void setBundleContext(Object bundleContext)
  {
    this.bundleContext = bundleContext;
  }

  public boolean isDebugging()
  {
    if (!debuggingInitialized)
    {
      debugging = getDebugOption("debug", false); //$NON-NLS-1$
      debuggingInitialized = true;
    }

    return debugging;
  }

  public void setDebugging(boolean debugging)
  {
    this.debugging = debugging;
  }

  public String getDebugOption(String option, String defaultValue)
  {
    String value = getDebugOption(option);
    return value == null ? defaultValue : value;
  }

  public boolean getDebugOption(String option, boolean defaultValue)
  {
    String value = getDebugOption(option);
    return value == null ? defaultValue : Boolean.parseBoolean(value);
  }

  public void setDebugOption(String option, boolean value)
  {
    setDebugOption(option, Boolean.toString(value));
  }

  public int getDebugOption(String option, int defaultValue)
  {
    try
    {
      String value = getDebugOption(option);
      return value == null ? defaultValue : Integer.parseInt(value);
    }
    catch (NumberFormatException e)
    {
      return defaultValue;
    }
  }

  public void setDebugOption(String option, int value)
  {
    setDebugOption(option, Integer.toString(value));
  }

  public String getDebugOption(String option)
  {
    return platform.getDebugOption(bundleID, option);
  }

  public void setDebugOption(String option, String value)
  {
    platform.setDebugOption(bundleID, option, value);
  }

  public OMTracer tracer(String name)
  {
    OMTracer tracer = tracers.get(name);
    if (tracer == null)
    {
      tracer = createTracer(name);
    }

    return tracer;
  }

  public OMLogger logger()
  {
    if (logger == null)
    {
      logger = createLogger();
    }

    return logger;
  }

  public InputStream getInputStream(String path) throws IOException
  {
    URL url = new URL(getBaseURL().toString() + ".options"); //$NON-NLS-1$
    return url.openStream();
  }

  public boolean shouldTranslate()
  {
    return shouldTranslate;
  }

  public void setShouldTranslate(boolean shouldTranslate)
  {
    this.shouldTranslate = shouldTranslate;
  }

  public String getString(String key, boolean translate)
  {
    Map stringMap = translate ? strings : untranslatedStrings;
    String result = (String)stringMap.get(key);
    if (result == null)
    {

      ResourceBundle bundle = translate ? resourceBundle : untranslatedResourceBundle;
      if (bundle == null)
      {
        String packageName = ReflectUtil.getPackageName(accessor);
        if (translate)
        {
          try
          {
            bundle = resourceBundle = ResourceBundle.getBundle(packageName + ".plugin"); //$NON-NLS-1$
          }
          catch (MissingResourceException exception)
          {
            // If the bundle can't be found the normal way, try to find it as
            // the base URL. If that also doesn't work, rethrow the original
            // exception.
            InputStream inputStream = null;
            try
            {
              inputStream = getInputStream("plugin.properties"); //$NON-NLS-1$
              bundle = new PropertyResourceBundle(inputStream);
              untranslatedResourceBundle = resourceBundle = bundle;
              inputStream.close();
            }
            catch (IOException ioException)
            {
            }
            finally
            {
              IOUtil.closeSilent(inputStream);
            }

            if (resourceBundle == null)
            {
              throw exception;
            }
          }
        }
        else
        {
          InputStream inputStream = null;
          try
          {
            inputStream = getInputStream("plugin.properties"); //$NON-NLS-1$
            bundle = untranslatedResourceBundle = new PropertyResourceBundle(inputStream);
            inputStream.close();
          }
          catch (IOException ioException)
          {
            throw new MissingResourceException("Missing resource: plugin.properties", accessor //$NON-NLS-1$
                .getName(), key);
          }
          finally
          {
            IOUtil.closeSilent(inputStream);
          }
        }
      }

      result = bundle.getString(key);
      stringMap.put(key, result);
    }

    return result;
  }

  public String getString(String key)
  {
    return getString(key, shouldTranslate());
  }

  public String getString(String key, Object... args)
  {
    return getString(key, shouldTranslate(), args);
  }

  public String getString(String key, boolean translate, Object... args)
  {
    return MessageFormat.format(getString(key, translate), args);
  }

  protected OMTracer createTracer(String name)
  {
    return new OMTracerImpl(this, name);
  }

  protected OMLogger createLogger()
  {
    return new OMLoggerImpl(this);
  }
}
