/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
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

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.io.IOUtil;
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
  public static final String KEY_PATH = "org.eclipse.net4j.tcp.ssl.key";

  public static final String TRUST_PATH = "org.eclipse.net4j.tcp.ssl.trust";

  public static final String PASS_PHRASE = "org.eclipse.net4j.tcp.ssl.passphrase";

  public static final String HANDSHAKE_TIMEOUT = "org.eclipse.net4j.tcp.ssl.handshake.timeout";

  public static final String HANDSHAKE_WAITTIME = "org.eclipse.net4j.tcp.ssl.handshake.waittime";

  private Properties localProperties;

  public SSLProperties()
  {
  }

  public void load(String localConfigPath) throws IOException
  {
    // Loading SSL config from local property is optional.
    localProperties = new Properties();

    InputStream in = null;

    try
    {
      in = new URL(localConfigPath).openStream();
      localProperties.load(in);
    }
    catch (IOException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex, "SSL config cannot be loaded");
    }
    finally
    {
      IOUtil.close(in);
    }
  }

  public String getKeyPath()
  {
    String keyPath = OMPlatform.INSTANCE.getProperty(KEY_PATH);
    if (keyPath == null && localProperties != null)
    {
      keyPath = localProperties.getProperty(KEY_PATH);
    }

    return keyPath;
  }

  public String getTrustPath()
  {
    String trustPath = OMPlatform.INSTANCE.getProperty(TRUST_PATH);
    if (trustPath == null && localProperties != null)
    {
      trustPath = localProperties.getProperty(TRUST_PATH);
    }

    return trustPath;
  }

  public String getPassPhrase()
  {
    String passPhrase = OMPlatform.INSTANCE.getProperty(PASS_PHRASE);
    if (passPhrase == null && localProperties != null)
    {
      passPhrase = localProperties.getProperty(PASS_PHRASE);
    }

    return passPhrase;
  }

  public String getHandShakeTimeOut()
  {
    String hsTimeOut = OMPlatform.INSTANCE.getProperty(HANDSHAKE_TIMEOUT);
    if (hsTimeOut == null && localProperties != null)
    {
      hsTimeOut = localProperties.getProperty(HANDSHAKE_TIMEOUT);
    }

    return hsTimeOut;
  }

  public String getHandShakeWaitTime()
  {
    String waitTime = OMPlatform.INSTANCE.getProperty(HANDSHAKE_WAITTIME);
    if (waitTime == null && localProperties != null)
    {
      waitTime = localProperties.getProperty(HANDSHAKE_WAITTIME);
    }

    return waitTime;
  }
}
