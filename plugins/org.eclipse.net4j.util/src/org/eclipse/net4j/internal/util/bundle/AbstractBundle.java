/*
 * Copyright (c) 2007-2012, 2015, 2016, 2019-2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.util.bundle;

import org.eclipse.net4j.internal.util.om.pref.Preferences;
import org.eclipse.net4j.util.ReflectUtil;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.om.OMBundle;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.log.Logger;
import org.eclipse.net4j.util.om.log.OMLogger;
import org.eclipse.net4j.util.om.trace.OMTracer;
import org.eclipse.net4j.util.om.trace.Tracer;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Eike Stepper
 */
public abstract class AbstractBundle implements OMBundle, OMBundle.DebugSupport, OMBundle.TranslationSupport
{
  private static final String CLASS_EXTENSION = ".class";

  private AbstractPlatform platform;

  private String bundleID;

  private Class<?> accessor;

  private Object bundleContext;

  private boolean debugging;

  private Map<String, Tracer> tracers = new ConcurrentHashMap<>(0);

  private OMLogger logger;

  private Preferences preferences;

  private ResourceBundle resourceBundle;

  private ResourceBundle untranslatedResourceBundle;

  private Map<String, String> strings = new HashMap<>(0);

  private Map<String, String> untranslatedStrings = new HashMap<>(0);

  private boolean shouldTranslate = true;

  public AbstractBundle(AbstractPlatform platform, String bundleID, Class<?> accessor)
  {
    this.platform = platform;
    this.bundleID = bundleID;
    this.accessor = accessor;

    boolean debug = getDebugOption("debug", false); //$NON-NLS-1$
    setDebugging(debug);
  }

  @Override
  public OMPlatform getPlatform()
  {
    return platform;
  }

  @Override
  public String getBundleID()
  {
    return bundleID;
  }

  public Class<?> getAccessor()
  {
    return accessor;
  }

  public void setAccessor(Class<?> accessor)
  {
    this.accessor = accessor;
  }

  public Object getBundleContext()
  {
    return bundleContext;
  }

  @Override
  @Deprecated
  public void setBundleContext(Object bundleContext)
  {
    this.bundleContext = bundleContext;
  }

  @Override
  public DebugSupport getDebugSupport()
  {
    return this;
  }

  @Override
  public TranslationSupport getTranslationSupport()
  {
    return this;
  }

  @Override
  public boolean isDebugging()
  {
    if (!platform.isDebugging())
    {
      return false;
    }

    return debugging;
  }

  @Override
  public void setDebugging(boolean debugging)
  {
    this.debugging = debugging;
  }

  @Override
  public String getDebugOption(String option, String defaultValue)
  {
    String value = getDebugOption(option);
    return value == null ? defaultValue : value;
  }

  @Override
  public boolean getDebugOption(String option, boolean defaultValue)
  {
    String value = getDebugOption(option);
    return value == null ? defaultValue : Boolean.parseBoolean(value);
  }

  @Override
  public void setDebugOption(String option, boolean value)
  {
    setDebugOption(option, Boolean.toString(value));
  }

  @Override
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

  @Override
  public void setDebugOption(String option, int value)
  {
    setDebugOption(option, Integer.toString(value));
  }

  @Override
  public String getDebugOption(String option)
  {
    return platform.getDebugOption(bundleID, option);
  }

  @Override
  public void setDebugOption(String option, String value)
  {
    platform.setDebugOption(bundleID, option, value);
  }

  public void setDebugOptionIfAbsent(String option, String value)
  {
    platform.setDebugOption(bundleID, option, value, true);
  }

  @Override
  public synchronized OMTracer tracer(String name)
  {
    OMTracer tracer = tracers.get(name);
    if (tracer == null)
    {
      tracer = createTracer(name);
    }

    return tracer;
  }

  @Override
  public synchronized OMLogger logger()
  {
    if (logger == null)
    {
      logger = createLogger();
    }

    return logger;
  }

  @Override
  public IStatus getStatus(Object obj)
  {
    if (obj instanceof CoreException)
    {
      CoreException ex = (CoreException)obj;
      IStatus st = ex.getStatus();
      return new Status(st.getSeverity(), st.getPlugin(), st.getCode(), st.getMessage(), ex);
    }

    if (obj instanceof Throwable)
    {
      Throwable t = (Throwable)obj;
      String msg = t.getLocalizedMessage();
      if (msg == null || msg.length() == 0)
      {
        msg = t.getClass().getName();
      }

      return new Status(IStatus.ERROR, bundleID, msg, t);
    }

    return new Status(IStatus.INFO, bundleID, obj.toString(), null);
  }

  @Override
  public void coreException(Throwable t) throws CoreException
  {
    if (t instanceof CoreException)
    {
      CoreException ex = (CoreException)t;
      IStatus status = ex.getStatus();
      if (status != null && status.getSeverity() == IStatus.CANCEL)
      {
        throw new OperationCanceledException();
      }

      throw ex;
    }

    if (t instanceof OperationCanceledException)
    {
      throw (OperationCanceledException)t;
    }

    if (t instanceof Error)
    {
      throw (Error)t;
    }

    IStatus status = getStatus(t);
    throw new CoreException(status);
  }

  @Override
  public File getConfigFile()
  {
    return platform.getConfigFile(getConfigFileName());
  }

  @Override
  public Properties getConfigProperties()
  {
    return platform.getConfigProperties(getConfigFileName());
  }

  @Override
  public synchronized Preferences preferences()
  {
    if (preferences == null)
    {
      preferences = new Preferences(this);
    }

    return preferences;
  }

  @Override
  public InputStream getInputStream(String path) throws IOException
  {
    String base = getBaseURL().toString();
    if (!base.endsWith("/")) //$NON-NLS-1$
    {
      base += "/"; //$NON-NLS-1$
    }

    if (path.startsWith("/")) //$NON-NLS-1$
    {
      path = path.substring(1);
    }

    URL url = new URL(base + path);
    return url.openStream();
  }

  @Override
  public boolean shouldTranslate()
  {
    return shouldTranslate;
  }

  @Override
  public void setShouldTranslate(boolean shouldTranslate)
  {
    this.shouldTranslate = shouldTranslate;
  }

  @Override
  public String getString(String key, boolean translate)
  {
    Map<String, String> stringMap = translate ? strings : untranslatedStrings;
    String result = stringMap.get(key);
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
            catch (IOException ignore)
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

  @Override
  public String getString(String key)
  {
    return getString(key, shouldTranslate());
  }

  @Override
  public String getString(String key, Object... args)
  {
    return getString(key, shouldTranslate(), args);
  }

  @Override
  public String getString(String key, boolean translate, Object... args)
  {
    return MessageFormat.format(getString(key, translate), args);
  }

  @Override
  public String toString()
  {
    return bundleID;
  }

  public void start() throws Exception
  {
    invokeMethod("start"); //$NON-NLS-1$
  }

  public void stop() throws Exception
  {
    try
    {
      if (preferences != null)
      {
        preferences.save();
      }
    }
    catch (RuntimeException ex)
    {
      OM.LOG.error(ex);
    }

    invokeMethod("stop"); //$NON-NLS-1$
  }

  protected OMTracer createTracer(String name)
  {
    return new Tracer(this, name);
  }

  protected OMLogger createLogger()
  {
    return new Logger(this);
  }

  protected String getConfigFileName()
  {
    return bundleID + ".properties"; //$NON-NLS-1$
  }

  protected final Class<?> getClassFromBundle(String path)
  {
    if (path.endsWith(CLASS_EXTENSION))
    {
      int start = path.startsWith("/") ? 1 : 0;
      int end = path.length() - CLASS_EXTENSION.length();
      String className = path.substring(start, end).replace('/', '.');

      for (;;)
      {
        try
        {
          ClassLoader classLoader = getAccessor().getClassLoader();
          Class<?> c = classLoader.loadClass(className);
          if (c != null)
          {
            return c;
          }
        }
        catch (NoClassDefFoundError ex)
        {
          //$FALL-THROUGH$
        }
        catch (ClassNotFoundException ex)
        {
          //$FALL-THROUGH$
        }

        int dot = className.indexOf('.');
        if (dot == -1)
        {
          break;
        }

        className = className.substring(dot + 1);
      }
    }

    return null;
  }

  private void invokeMethod(String name) throws Exception
  {
    try
    {
      Method method = accessor.getDeclaredMethod(name, ReflectUtil.NO_PARAMETERS);
      ReflectUtil.makeAccessible(method);
      method.invoke(null, ReflectUtil.NO_ARGUMENTS);
    }
    catch (NoSuchMethodException ignore)
    {
    }
    catch (IllegalAccessException ignore)
    {
    }
    catch (InvocationTargetException ex)
    {
      Throwable targetException = ex.getTargetException();
      if (targetException instanceof Exception)
      {
        throw (Exception)targetException;
      }
      else if (targetException instanceof Error)
      {
        throw (Error)targetException;
      }
      else
      {
        OM.LOG.error(targetException);
      }
    }
  }
}
