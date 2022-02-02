/*
 * Copyright (c) 2007, 2008, 2011, 2012, 2015, 2019, 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.om;

import org.eclipse.net4j.util.om.log.OMLogger;
import org.eclipse.net4j.util.om.pref.OMPreferences;
import org.eclipse.net4j.util.om.trace.OMTracer;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osgi.service.debug.DebugOptions;

import org.osgi.framework.Bundle;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * Represents a {@link Bundle bundle}, whether OSGi {@link OMPlatform#isOSGiRunning() is running} or not.
 *
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface OMBundle
{
  public OMPlatform getPlatform();

  public String getBundleID();

  public URL getBaseURL();

  /**
   * @since 3.2
   */
  public Iterator<Class<?>> getClasses();

  public OMTracer tracer(String name);

  public OMLogger logger();

  public OMPreferences preferences();

  /**
   * @since 3.5
   */
  public IStatus getStatus(Object obj);

  /**
   * @since 3.5
   */
  public void coreException(Throwable t) throws CoreException;

  public File getConfigFile();

  public Properties getConfigProperties();

  /**
   * @since 3.18
   */
  public String getUserLocation();

  public String getStateLocation();

  public InputStream getInputStream(String path) throws IOException;

  public DebugSupport getDebugSupport();

  public TranslationSupport getTranslationSupport();

  /**
   * @deprecated For internal use only.
   */
  @Deprecated
  public void setBundleContext(Object bundleContext);

  /**
   * A facility for accessing OSGi {@link DebugOptions debug options}, whether OSGi {@link OMPlatform#isOSGiRunning() is
   * running} or not.
   *
   * @author Eike Stepper
   */
  public interface DebugSupport
  {
    public boolean isDebugging();

    public void setDebugging(boolean debugging);

    public String getDebugOption(String option);

    public void setDebugOption(String option, String value);

    public String getDebugOption(String option, String defaultValue);

    public boolean getDebugOption(String option, boolean defaultValue);

    public void setDebugOption(String option, boolean value);

    public int getDebugOption(String option, int defaultValue);

    public void setDebugOption(String option, int value);
  }

  /**
   * A facility for accessing {@link ResourceBundle resource bundles}.
   *
   * @author Eike Stepper
   */
  public interface TranslationSupport
  {
    /**
     * Indicates whether strings should be translated by default.
     *
     * @return <code>true</code> if strings should be translated by default; <code>false</code> otherwise.
     */
    public boolean shouldTranslate();

    /**
     * Sets whether strings should be translated by default.
     *
     * @param shouldTranslate
     *          whether strings should be translated by default.
     */
    public void setShouldTranslate(boolean shouldTranslate);

    /**
     * Returns the string resource associated with the key.
     *
     * @param key
     *          the key of the string resource.
     * @return the string resource associated with the key.
     */
    String getString(String key);

    /**
     * Returns the string resource associated with the key.
     *
     * @param key
     *          the key of the string resource.
     * @param translate
     *          whether the result is to be translated to the current locale.
     * @return the string resource associated with the key.
     */
    String getString(String key, boolean translate);

    /**
     * Returns a string resource associated with the key, and performs substitutions.
     *
     * @param key
     *          the key of the string.
     * @param args
     *          the message substitutions.
     * @return a string resource associated with the key.
     * @see #getString(String)
     * @see java.text.MessageFormat#format(String, Object...)
     */
    String getString(String key, Object... args);

    /**
     * Returns a string resource associated with the key, and performs substitutions.
     *
     * @param key
     *          the key of the string.
     * @param translate
     *          whether the result is to be translated to the current locale.
     * @param args
     *          the message substitutions.
     * @return a string resource associated with the key.
     * @see #getString(String)
     * @see java.text.MessageFormat#format(String, Object[])
     */
    String getString(String key, boolean translate, Object... args);
  }
}
