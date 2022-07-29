/*
 * Copyright (c) 2011, 2012, 2015, 2016, 2018, 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Teerawat Chaiyakijpichet (No Magic Asia Ltd.) - initial API and implementation
 *    Caspar De Groot (No Magic Asia Ltd.) - initial API and implementation
 */
package org.eclipse.net4j.tcp.ssl;

import org.eclipse.net4j.internal.tcp.TCPAcceptorFactory;
import org.eclipse.net4j.internal.tcp.TCPConnectorFactory;
import org.eclipse.net4j.internal.tcp.ssl.SSLAcceptorFactory;
import org.eclipse.net4j.internal.tcp.ssl.SSLConnectorFactory;
import org.eclipse.net4j.tcp.ITCPAcceptor;
import org.eclipse.net4j.tcp.ITCPConnector;
import org.eclipse.net4j.tcp.TCPUtil;
import org.eclipse.net4j.util.container.IManagedContainer;

import javax.net.ssl.SSLEngine;

/**
 * A utility class with various static factory and convenience methods for SSL transport.
 *
 * @author Teerawat Chaiyakijpichet (No Magic Asia Ltd.)
 * @author Caspar De Groot (No Magic Asia Ltd.)
 * @since 4.0
 */
public class SSLUtil
{
  private static String configFile;

  private static String defaultProtocol = "TLSv1.3";

  private static String defaultKeyPath;

  private static String defaultTrustPath;

  private static String defaultPassPhrase;

  /**
   * Default value of handshake timeout is 12 times.
   */
  private static int defaultHandShakeTimeOut = 12;

  /**
   * Default value of handshake wait time is 60 milliseconds.
   */
  private static int defaultHandShakeWaitTime = 60;

  /**
   * @since 4.4
   */
  public static String getConfigFile()
  {
    return configFile;
  }

  /**
   * @since 4.4
   */
  public static void setConfigFile(String configFile)
  {
    SSLUtil.configFile = configFile;
  }

  /**
   * @since 4.4
   */
  public static String getDefaultProtocol()
  {
    return defaultProtocol;
  }

  /**
   * @since 4.4
   */
  public static void setDefaultProtocol(String defaultProtocol)
  {
    SSLUtil.defaultProtocol = defaultProtocol;
  }

  /**
   * @since 4.4
   */
  public static String getDefaultKeyPath()
  {
    return defaultKeyPath;
  }

  /**
   * @since 4.4
   */
  public static void setDefaultKeyPath(String defaultKeyPath)
  {
    SSLUtil.defaultKeyPath = defaultKeyPath;
  }

  /**
   * @since 4.4
   */
  public static String getDefaultTrustPath()
  {
    return defaultTrustPath;
  }

  /**
   * @since 4.4
   */
  public static void setDefaultTrustPath(String defaultTrustPath)
  {
    SSLUtil.defaultTrustPath = defaultTrustPath;
  }

  /**
   * @since 4.4
   */
  public static String getDefaultPassPhrase()
  {
    return defaultPassPhrase;
  }

  /**
   * @since 4.4
   */
  public static void setDefaultPassPhrase(String defaultPassPhrase)
  {
    SSLUtil.defaultPassPhrase = defaultPassPhrase;
  }

  /**
   * @since 4.4
   */
  public static int getDefaultHandShakeTimeOut()
  {
    return defaultHandShakeTimeOut;
  }

  /**
   * @since 4.4
   */
  public static void setDefaultHandShakeTimeOut(int defaultHandShakeTimeOut)
  {
    SSLUtil.defaultHandShakeTimeOut = defaultHandShakeTimeOut;
  }

  /**
   * @since 4.4
   */
  public static int getDefaultHandShakeWaitTime()
  {
    return defaultHandShakeWaitTime;
  }

  /**
   * @since 4.4
   */
  public static void setDefaultHandShakeWaitTime(int defaultHandShakeWaitTime)
  {
    SSLUtil.defaultHandShakeWaitTime = defaultHandShakeWaitTime;
  }

  public static void prepareContainer(IManagedContainer container)
  {
    TCPUtil.prepareContainer(container);

    // Prepare SSL
    container.registerFactory(new SSLAcceptorFactory());
    container.registerFactory(new SSLConnectorFactory());
  }

  public static ITCPAcceptor getAcceptor(IManagedContainer container, String description)
  {
    return (ITCPAcceptor)container.getElement(TCPAcceptorFactory.PRODUCT_GROUP, SSLAcceptorFactory.TYPE, description);
  }

  public static ITCPConnector getConnector(IManagedContainer container, String description)
  {
    return (ITCPConnector)container.getElement(TCPConnectorFactory.PRODUCT_GROUP, SSLConnectorFactory.TYPE, description);
  }

  /**
   * @deprecated As of 4.4 use {@link #setConfigFile(String)}.
   */
  @Deprecated
  public static void setSSLConfigurationFile(String file)
  {
    setConfigFile(file);
  }

  /**
   * @deprecated As of 4.4 use {@link #setDefaultKeyPath(String)}, {@link #setDefaultTrustPath(String)}, and {@link #setDefaultPassPhrase(String)}.
   */
  @Deprecated
  public static void setDefaultSSLConfiguration(String keyPath, String trustPath, String passPhrase)
  {
    defaultKeyPath = keyPath;
    defaultTrustPath = trustPath;
    defaultPassPhrase = passPhrase;
  }

  /**
   * @deprecated As of 4.4 use {@link #setDefaultKeyPath(String)}, {@link #setDefaultTrustPath(String)}, {@link #setDefaultPassPhrase(String)},
   * {@link #setDefaultHandShakeTimeOut(int)}, and {@link #setDefaultHandShakeWaitTime(int)}.
   */
  @Deprecated
  public static void setDefaultSSLConfiguration(String keyPath, String trustPath, String passPhrase, int handShakeTimeOut, int handShakeWaitTime)
  {
    setDefaultSSLConfiguration(keyPath, trustPath, passPhrase);

    defaultHandShakeTimeOut = handShakeTimeOut;
    defaultHandShakeWaitTime = handShakeWaitTime;
  }

  /**
   * @deprecated As of 4.4 no longer supported (was only used internally before).
   */
  @Deprecated
  public static SSLEngine createSSLEngine(boolean client, String host, int port) throws Exception
  {
    throw new UnsupportedOperationException();
  }

  /**
   * @deprecated As of 4.4 use {@link #getDefaultHandShakeTimeOut}.
   */
  @Deprecated
  public static int getHandShakeTimeOut()
  {
    return getDefaultHandShakeTimeOut();
  }

  /**
   * @deprecated As of 4.4 use {@link #getDefaultHandShakeWaitTime}.
   */
  @Deprecated
  public static int getHandShakeWaitTime()
  {
    return getDefaultHandShakeWaitTime();
  }
}
