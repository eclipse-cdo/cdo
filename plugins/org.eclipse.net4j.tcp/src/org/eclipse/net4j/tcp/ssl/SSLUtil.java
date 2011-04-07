/**
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
package org.eclipse.net4j.tcp.ssl;

import org.eclipse.net4j.internal.tcp.TCPAcceptorFactory;
import org.eclipse.net4j.internal.tcp.TCPConnectorFactory;
import org.eclipse.net4j.internal.tcp.bundle.OM;
import org.eclipse.net4j.internal.tcp.ssl.SSLAcceptorFactory;
import org.eclipse.net4j.internal.tcp.ssl.SSLConnectorFactory;
import org.eclipse.net4j.internal.tcp.ssl.SSLProperties;
import org.eclipse.net4j.tcp.ITCPAcceptor;
import org.eclipse.net4j.tcp.ITCPConnector;
import org.eclipse.net4j.tcp.TCPUtil;
import org.eclipse.net4j.util.container.IManagedContainer;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

/**
 * @author Teerawat Chaiyakijpichet (No Magic Asia Ltd.)
 * @author Caspar De Groot (No Magic Asia Ltd.)
 * @since 4.0
 */
public class SSLUtil
{
  // The variable for SSL Engine
  private static final String protocol = "TLS";

  private static String configFile;

  private static String keyPathVar;

  private static String trustPathVar;

  private static String passPhraseVar;

  // default value of handshake timeout is 12 times.
  private static int handShakeTimeOutVar = 12;

  // default value of handshake wait time is 60 milliseconds.
  private static int handShakeWaitTimeVar = 60;

  public static synchronized void setDefaultSSLConfiguration(String keyPath, String trustPath, String passPhrase)
  {
    keyPathVar = keyPath;
    trustPathVar = trustPath;
    passPhraseVar = passPhrase;
  }

  public static synchronized void setDefaultSSLConfiguration(String keyPath, String trustPath, String passPhrase,
      int handShakeTimeOut, int handShakeWaitTime)
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

    // prepare SSL
    container.registerFactory(new SSLAcceptorFactory());
    container.registerFactory(new SSLConnectorFactory());
  }

  public static synchronized ITCPAcceptor getAcceptor(IManagedContainer container, String description)
  {
    return (ITCPAcceptor)container.getElement(TCPAcceptorFactory.PRODUCT_GROUP, SSLAcceptorFactory.TYPE, description);
  }

  public static synchronized ITCPConnector getConnector(IManagedContainer container, String description)
  {
    return (ITCPConnector)container
        .getElement(TCPConnectorFactory.PRODUCT_GROUP, SSLConnectorFactory.TYPE, description);
  }

  public static synchronized SSLEngine createSSLEngine(boolean client, String host, int port)
      throws NoSuchAlgorithmException, KeyStoreException, CertificateException, FileNotFoundException, IOException,
      UnrecoverableKeyException, KeyManagementException
  {
    // get values from the system properties.
    SSLProperties sslProperties = new SSLProperties();
    String keyPath = sslProperties.getKeyPath();
    String trustPath = sslProperties.getTrustPath();
    String passPhrase = sslProperties.getPassPhrase();

    if ((keyPath == null || trustPath == null || passPhrase == null) && configFile != null)
    {
      sslProperties.load(configFile);
    }

    // in case, the system properties does not have the key path property. it will load from local config file.
    if (keyPath == null)
    {
      keyPath = sslProperties.getKeyPath();
      if (keyPath == null)
      {
        keyPath = keyPathVar;
      }
    }

    // in case, the system properties does not have the trust path property. it will load from local config file.
    if (trustPath == null)
    {
      trustPath = sslProperties.getTrustPath();
      if (trustPath == null)
      {
        trustPath = trustPathVar;
      }
    }

    // in case, the system properties does not have the passphrase property. it will load from local config file.
    if (passPhrase == null)
    {
      passPhrase = sslProperties.getPassPhrase();
      if (passPhrase == null)
      {
        passPhrase = passPhraseVar;
      }
    }

    // handle assign the value of handshake timeout and handshake timewait from local properties or system properties by
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
        throw new KeyStoreException("Trust Store[" + (trustPath != null) + "] or Pass Phrase[" + (passPhrase != null)
            + "] is not provided. [false] means it does not exist.");
      }

      throw new KeyStoreException("Key Store[" + (keyPath != null) + "] or Pass Phrase[" + (passPhrase != null)
          + "] is not provided. [false] means it does not exist.");
    }

    char[] pass = passPhrase.toCharArray();

    KeyManager[] keyManagers = null;
    TrustManager[] trustManagers = null;

    if (client)
    {
      // initial key material(private key) for the client.
      KeyStore ksTrust = KeyStore.getInstance(KeyStore.getDefaultType());

      File ksTrustFile = null;
      FileInputStream ksTrustInputStream = null;

      try
      {
        ksTrustFile = new File(new URL(trustPath).toURI());
      }
      catch (Exception ex)
      {
        OM.LOG.warn(ex.getMessage() + ",so try to load with the normal path", ex);
        ksTrustFile = new File(trustPath);
      }

      if (ksTrustFile.exists())
      {
        try
        {
          ksTrustInputStream = new FileInputStream(ksTrustFile);
        }
        catch (FileNotFoundException ex)
        {
          throw ex;
        }
        finally
        {
          if (ksTrustInputStream != null)
          {
            ksTrust.load(ksTrustInputStream, pass);
            ksTrustInputStream.close();
          }
        }

        // initial the trust manager factory
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(ksTrust);

        trustManagers = tmf.getTrustManagers();
      }
      else
      {
        throw new KeyStoreException("Trust Store cannot be loaded.");
      }
    }
    else
    {
      // initial key material(private key) for the server.
      KeyStore ksKeys = KeyStore.getInstance(KeyStore.getDefaultType());

      File ksKeysFile = null;
      FileInputStream ksKeysInputStream = null;

      try
      {
        ksKeysFile = new File(new URL(keyPath).toURI());
      }
      catch (Exception ex)
      {
        OM.LOG.warn(ex.getMessage() + ",so try to load with the normal path", ex);
        ksKeysFile = new File(keyPath);
      }

      if (ksKeysFile.exists())
      {
        try
        {
          ksKeysInputStream = new FileInputStream(ksKeysFile);
        }
        catch (FileNotFoundException ex)
        {
          throw ex;
        }
        finally
        {
          if (ksKeysInputStream != null)
          {
            ksKeys.load(ksKeysInputStream, pass);
            ksKeysInputStream.close();
          }
        }

      }
      else
      {
        throw new KeyStoreException("Key Store cannot be loaded.");
      }

      // initial the key manager factory.
      KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
      kmf.init(ksKeys, pass);

      keyManagers = kmf.getKeyManagers();
    }

    SSLContext sslContext = SSLContext.getInstance(protocol);
    sslContext.init(keyManagers, trustManagers, null);

    SSLEngine sslEngine = sslContext.createSSLEngine(host, port);
    sslEngine.setUseClientMode(client);
    return sslEngine;
  }

  public static int getHandShakeTimeOut()
  {
    return handShakeTimeOutVar;
  }

  public static int getHandShakeWaitTime()
  {
    return handShakeWaitTimeVar;
  }

}
