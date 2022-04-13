/*
 * Copyright (c) 2011, 2012, 2015, 2016, 2018 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.net4j.internal.tcp.ssl.SSLProperties;
import org.eclipse.net4j.tcp.ITCPAcceptor;
import org.eclipse.net4j.tcp.ITCPConnector;
import org.eclipse.net4j.tcp.TCPUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.om.OMPlatform;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

/**
 * A utility class with various static factory and convenience methods for SSL transport.
 *
 * @author Teerawat Chaiyakijpichet (No Magic Asia Ltd.)
 * @author Caspar De Groot (No Magic Asia Ltd.)
 * @since 4.0
 */
public class SSLUtil
{
  /**
   * The variable for SSL Engine
   */
  private static final String PROTOCOL = "TLSv1.2";

  private static String configFile;

  private static String keyPathVar;

  private static String trustPathVar;

  private static String passPhraseVar;

  /**
   * Default value of handshake timeout is 12 times.
   */
  private static int handShakeTimeOutVar = 12;

  /**
   * Default value of handshake wait time is 60 milliseconds.
   */
  private static int handShakeWaitTimeVar = 60;

  public static synchronized void setDefaultSSLConfiguration(String keyPath, String trustPath, String passPhrase)
  {
    keyPathVar = keyPath;
    trustPathVar = trustPath;
    passPhraseVar = passPhrase;
  }

  public static synchronized void setDefaultSSLConfiguration(String keyPath, String trustPath, String passPhrase, int handShakeTimeOut, int handShakeWaitTime)
  {
    setDefaultSSLConfiguration(keyPath, trustPath, passPhrase);

    handShakeTimeOutVar = handShakeTimeOut;
    handShakeWaitTimeVar = handShakeWaitTime;
  }

  public static synchronized void setSSLConfigurationFile(String file)
  {
    configFile = file;
  }

  public static synchronized void prepareContainer(IManagedContainer container)
  {
    TCPUtil.prepareContainer(container);

    // Prepare SSL
    container.registerFactory(new SSLAcceptorFactory());
    container.registerFactory(new SSLConnectorFactory());
  }

  public static synchronized ITCPAcceptor getAcceptor(IManagedContainer container, String description)
  {
    return (ITCPAcceptor)container.getElement(TCPAcceptorFactory.PRODUCT_GROUP, SSLAcceptorFactory.TYPE, description);
  }

  public static synchronized ITCPConnector getConnector(IManagedContainer container, String description)
  {
    return (ITCPConnector)container.getElement(TCPConnectorFactory.PRODUCT_GROUP, SSLConnectorFactory.TYPE, description);
  }

  public static synchronized SSLEngine createSSLEngine(boolean client, String host, int port) throws Exception
  {
    // Get values from the system properties.
    SSLProperties sslProperties = new SSLProperties();
    String keyPath = sslProperties.getKeyPath();
    String trustPath = sslProperties.getTrustPath();
    String passPhrase = sslProperties.getPassPhrase();

    if ((keyPath == null || trustPath == null || passPhrase == null) && configFile != null)
    {
      sslProperties.load(configFile);
    }

    // In case, the system properties does not have the key path property. It will load from local config file.
    if (keyPath == null)
    {
      keyPath = sslProperties.getKeyPath();
      if (keyPath == null)
      {
        keyPath = keyPathVar;
      }
    }

    // In case, the system properties does not have the trust path property. It will load from local config file.
    if (trustPath == null)
    {
      trustPath = sslProperties.getTrustPath();
      if (trustPath == null)
      {
        trustPath = trustPathVar;
      }
    }

    // In case, the system properties does not have the passphrase property. It will load from local config file.
    if (passPhrase == null)
    {
      passPhrase = sslProperties.getPassPhrase();
      if (passPhrase == null)
      {
        passPhrase = passPhraseVar;
      }
    }

    // Handle assign the value of handshake timeout and handshake timewait from local properties or system properties by
    // giving the value form system properties is high priority.
    String value = sslProperties.getHandShakeTimeOut();
    if (value != null)
    {
      handShakeTimeOutVar = Integer.parseInt(value);
    }

    value = sslProperties.getHandShakeWaitTime();
    if (value != null)
    {
      handShakeWaitTimeVar = Integer.parseInt(value);
    }

    if (keyPath == null && !client || trustPath == null && client || passPhrase == null)
    {
      if (client)
      {
        throw new KeyStoreException(
            "Trust Store[" + (trustPath != null) + "] or Pass Phrase[" + (passPhrase != null) + "] is not provided. [false] means it does not exist.");
      }

      throw new KeyStoreException(
          "Key Store[" + (keyPath != null) + "] or Pass Phrase[" + (passPhrase != null) + "] is not provided. [false] means it does not exist.");
    }

    char[] pass = passPhrase.toCharArray();

    KeyManager[] keyManagers = null;
    TrustManager[] trustManagers = null;
    boolean checkValidtyStatus = OMPlatform.INSTANCE.isProperty(SSLProperties.CHECK_VALIDITY_CERTIFICATE, true);
    if (client)
    {
      // Initial key material(private key) for the client.
      KeyStore ksTrust = createKeyStore(trustPath, pass, checkValidtyStatus);
      // Initial the trust manager factory
      TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      tmf.init(ksTrust);

      trustManagers = tmf.getTrustManagers();
    }
    else
    {
      // Initial key material (private key) for the server.
      KeyStore ksKeys = createKeyStore(keyPath, pass, checkValidtyStatus);

      // Initial the key manager factory.
      KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
      kmf.init(ksKeys, pass);

      keyManagers = kmf.getKeyManagers();
    }

    SSLContext sslContext = SSLContext.getInstance(PROTOCOL);
    sslContext.init(keyManagers, trustManagers, null);

    SSLEngine sslEngine = sslContext.createSSLEngine(host, port);
    sslEngine.setUseClientMode(client);
    return sslEngine;
  }

  private static KeyStore createKeyStore(String path, char[] password, boolean checkValidity) throws Exception
  {
    // Initial key material
    KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());

    InputStream in = null;

    try
    {
      in = new URL(path).openStream();
      keyStore.load(in, password);

      if (checkValidity)
      {
        // Check validity license key
        Enumeration<String> aliasesIter = keyStore.aliases();
        while (aliasesIter.hasMoreElements())
        {
          String alias = aliasesIter.nextElement();
          Certificate cert = keyStore.getCertificate(alias);
          if (cert instanceof X509Certificate)
          {
            ((X509Certificate)cert).checkValidity();
          }
        }
      }
    }
    finally
    {
      IOUtil.close(in);
    }

    return keyStore;
  }

  public static synchronized int getHandShakeTimeOut()
  {
    return handShakeTimeOutVar;
  }

  public static synchronized int getHandShakeWaitTime()
  {
    return handShakeWaitTimeVar;
  }
}
