/*
 * Copyright (c) 2011, 2012, 2015, 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Teerawat Chaiyakijpichet (No Magic Asia Ltd.) - initial API and implementation
 *    Caspar De Groot (No Magic Asia Ltd.) - initial API and implementation
 */
package org.eclipse.net4j.internal.tcp.ssl;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.om.OMPlatform;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * @author Teerawat Chaiyakijpichet (No Magic Asia Ltd.)
 * @author Caspar De Groot (No Magic Asia Ltd.)
 * @since 4.0
 */
public class SSLProperties
{
  public static final String PROTOCOL = "org.eclipse.net4j.tcp.ssl.protocol";

  public static final String KEY_PATH = "org.eclipse.net4j.tcp.ssl.key";

  public static final String TRUST_PATH = "org.eclipse.net4j.tcp.ssl.trust";

  public static final String PASS_PHRASE = "org.eclipse.net4j.tcp.ssl.passphrase";

  public static final String HANDSHAKE_TIMEOUT = "org.eclipse.net4j.tcp.ssl.handshake.timeout";

  public static final String HANDSHAKE_WAITTIME = "org.eclipse.net4j.tcp.ssl.handshake.waittime";

  public static final String CHECK_VALIDITY_CERTIFICATE = "check.validity.certificate";

  private Properties localProperties;

  public SSLProperties(String localConfigUrl) throws IOException
  {
    // Loading SSL config from local property is optional.
    if (!StringUtil.isEmpty(localConfigUrl))
    {
      try (InputStream in = new URL(localConfigUrl).openStream())
      {
        localProperties = new Properties();
        localProperties.load(in);
      }
      catch (IOException ex)
      {
        throw ex;
      }
      catch (Exception ex)
      {
        throw new IOException("SSL config cannot be loaded", ex);
      }
    }
  }

  public String getProtocol(String defaultValue)
  {
    return getValue(PROTOCOL, defaultValue);
  }

  public String getKeyPath(String defaultValue)
  {
    return getValue(KEY_PATH, defaultValue);
  }

  public String getTrustPath(String defaultValue)
  {
    return getValue(TRUST_PATH, defaultValue);
  }

  public String getPassPhrase(String defaultValue)
  {
    return getValue(PASS_PHRASE, defaultValue);
  }

  public int getHandShakeTimeOut(int defaultValue)
  {
    return getValue(HANDSHAKE_TIMEOUT, defaultValue);
  }

  public int getHandShakeWaitTime(int defaultValue)
  {
    return getValue(HANDSHAKE_WAITTIME, defaultValue);
  }

  private String getValue(String key, String defaultValue)
  {
    String value = OMPlatform.INSTANCE.getProperty(key);
    if (StringUtil.isEmpty(value) && localProperties != null)
    {
      value = localProperties.getProperty(key);
    }

    if (StringUtil.isEmpty(value))
    {
      return defaultValue;
    }

    return value;
  }

  private int getValue(String key, int defaultValue)
  {
    String value = OMPlatform.INSTANCE.getProperty(key);
    if (StringUtil.isEmpty(value) && localProperties != null)
    {
      value = localProperties.getProperty(key);
    }

    if (StringUtil.isEmpty(value))
    {
      return defaultValue;
    }

    if (value != null)
    {
      return Integer.parseInt(value);
    }

    return 0;
  }
}
