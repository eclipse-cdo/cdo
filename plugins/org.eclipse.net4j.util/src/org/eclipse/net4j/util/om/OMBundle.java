/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util.om;

import org.eclipse.net4j.util.om.log.OMLogger;
import org.eclipse.net4j.util.om.pref.OMPreferences;
import org.eclipse.net4j.util.om.trace.OMTracer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * @author Eike Stepper
 */
public interface OMBundle
{
  public OMPlatform getPlatform();

  public String getBundleID();

  public URL getBaseURL();

  public void setBundleContext(Object bundleContext);

  public OMTracer tracer(String name);

  public OMLogger logger();

  public OMPreferences preferences();

  public File getConfigFile();

  public Properties getConfigProperties();

  public String getStateLocation();

  public InputStream getInputStream(String path) throws IOException;

  public DebugSupport getDebugSupport();

  public TranslationSupport getTranslationSupport();

  /**
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
