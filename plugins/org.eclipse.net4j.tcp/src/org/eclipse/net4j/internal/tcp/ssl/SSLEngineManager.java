/*
 * Copyright (c) 2011, 2012, 2015, 2016 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.net4j.internal.tcp.bundle.OM;
import org.eclipse.net4j.tcp.ssl.SSLUtil;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;
import javax.net.ssl.SSLEngineResult.Status;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.concurrent.Executor;

/**
 * @author Teerawat Chaiyakijpichet (No Magic Asia Ltd.)
 * @author Caspar De Groot (No Magic Asia Ltd.)
 * @since 4.0
 */
public class SSLEngineManager
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, SSLEngineManager.class);

  /**
   * An optional executor to be used this class.
   */
  private Executor executor;

  private SSLEngine sslEngine;

  private int handShakeTimeOut;

  private int handShakeWaitTime;

  private boolean handShakeComplete;

  private SSLEngineResult.HandshakeStatus handShakeStatus;

  private boolean needRehandShake;

  private ByteBuffer appSendBuf;

  private ByteBuffer appRecvBuf;

  private ByteBuffer packetSendBuf;

  private ByteBuffer packetRecvBuf;

  private SSLEngineResult engineResult;

  private Object readLock = new ReadLock();

  private Object writeLock = new WriteLock();

  public SSLEngineManager(boolean client, String host, int port, Executor executor) throws Exception
  {
    this.executor = executor;

    SSLProperties sslProperties = new SSLProperties(SSLUtil.getConfigFile());
    String protocol = sslProperties.getProtocol(SSLUtil.getDefaultProtocol());
    String keyPath = sslProperties.getKeyPath(SSLUtil.getDefaultKeyPath());
    String trustPath = sslProperties.getTrustPath(SSLUtil.getDefaultTrustPath());
    String passPhrase = sslProperties.getPassPhrase(SSLUtil.getDefaultPassPhrase());
    handShakeTimeOut = sslProperties.getHandShakeTimeOut(SSLUtil.getDefaultHandShakeTimeOut());
    handShakeWaitTime = sslProperties.getHandShakeWaitTime(SSLUtil.getDefaultHandShakeWaitTime());

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

    SSLContext sslContext = SSLContext.getInstance(protocol);
    sslContext.init(keyManagers, trustManagers, null);

    sslEngine = sslContext.createSSLEngine(host, port);
    sslEngine.setUseClientMode(client);
    sslEngine.beginHandshake();

    SSLSession session = sslEngine.getSession();

    int applicationBufferSize = session.getApplicationBufferSize();
    appSendBuf = ByteBuffer.allocate(applicationBufferSize);
    appRecvBuf = ByteBuffer.allocate(applicationBufferSize);

    int packetBufferSize = session.getPacketBufferSize();
    packetSendBuf = ByteBuffer.allocate(packetBufferSize);
    packetRecvBuf = ByteBuffer.allocate(packetBufferSize);
  }

  public Executor getExecutor()
  {
    return executor;
  }

  public int getHandShakeWaitTime()
  {
    return handShakeWaitTime;
  }

  public ByteBuffer getAppSendBuf()
  {
    return appSendBuf;
  }

  public void setAppSendBuf(ByteBuffer appSendBuf)
  {
    this.appSendBuf = appSendBuf;
  }

  public ByteBuffer getAppRecvBuf()
  {
    return appRecvBuf;
  }

  public void setAppRecvBuf(ByteBuffer appRecvBuf)
  {
    this.appRecvBuf = appRecvBuf;
  }

  public ByteBuffer getPacketSendBuf()
  {
    return packetSendBuf;
  }

  public void setPacketSendBuf(ByteBuffer packetSendBuf)
  {
    this.packetSendBuf = packetSendBuf;
  }

  public ByteBuffer getPacketRecvBuf()
  {
    return packetRecvBuf;
  }

  public void setPacketRecvBuf(ByteBuffer packetRecvBuf)
  {
    this.packetRecvBuf = packetRecvBuf;
  }

  public synchronized void checkInitialHandshake(SocketChannel socketChannel) throws Exception
  {
    if (!handShakeComplete)
    {
      try
      {
        int counter = 0;
        while (!isHandshakeFinished() && counter <= handShakeTimeOut)
        {
          performHandshake(socketChannel, needRehandShake);
          counter = handShakeStatus == HandshakeStatus.NEED_UNWRAP ? counter++ : 0;
        }

        if (!isHandshakeFinished() && counter == handShakeTimeOut)
        {
          throw new SSLException("SSL handshake timeout");
        }
      }
      catch (Exception ex)
      {
        if (TRACER.isEnabled())
        {
          TRACER.trace("SSL handshake incomplete.", ex); //$NON-NLS-1$
        }

        try
        {
          // Clean the SSLEngine.
          close();
        }
        catch (IOException ioex)
        {
          OM.LOG.warn(ioex);
        }

        throw ex;
      }

      handShakeComplete = true;
    }
  }

  public int read(SocketChannel socketChannel) throws IOException
  {
    if (!handShakeComplete)
    {
      throw new SSLException("Handshake still not completed");
    }

    synchronized (readLock)
    {
      int readTextCount = appRecvBuf.position();

      do
      {
        if (sslEngine.isInboundDone())
        {
          return readTextCount > 0 ? readTextCount : -1;
        }

        int count = handleRead(socketChannel);
        if (count <= 0 && packetRecvBuf.position() == 0)
        {
          return count;
        }

        packetRecvBuf.flip();
        engineResult = sslEngine.unwrap(packetRecvBuf, appRecvBuf);
        packetRecvBuf.compact();

        switch (engineResult.getStatus())
        {
        case BUFFER_UNDERFLOW:
          continue;

        case BUFFER_OVERFLOW:
          if (TRACER.isEnabled())
          {
            TRACER.trace("Buffer overflow on read method."); //$NON-NLS-1$
          }

          return 0;

        case CLOSED:
          throw new ClosedChannelException();

        default:
          // OK
        }

        readTextCount = appRecvBuf.position();
      } while (engineResult.getStatus() != Status.OK);

      if (sslEngine.isInboundDone())
      {
        return -1;
      }

      return readTextCount;
    }
  }

  public int write(SocketChannel socketChannel) throws IOException
  {
    if (!handShakeComplete)
    {
      throw new SSLException("Handshake still not completed");
    }

    synchronized (writeLock)
    {
      int writeCount = 0;
      int count = 0;
      int appSendBufSize = appSendBuf.position();

      do
      {
        appSendBuf.flip();
        engineResult = sslEngine.wrap(appSendBuf, packetSendBuf);
        appSendBuf.compact();
        switch (engineResult.getStatus())
        {
        case BUFFER_UNDERFLOW:
          if (TRACER.isEnabled())
          {
            TRACER.trace("Buffer Underflow happen on write method"); //$NON-NLS-1$
          }

          return -1;

        case BUFFER_OVERFLOW:
          count = handleWrite(socketChannel);
          writeCount += count;
          continue;

        case CLOSED:
          throw new ClosedChannelException();

        case OK:
          int bytesComsumed = engineResult.bytesConsumed();
          appSendBufSize = appSendBufSize - bytesComsumed;
          count = handleWrite(socketChannel);
          writeCount += count;
          break;
        }
      } while (engineResult.getStatus() != Status.OK);

      return writeCount;
    }
  }

  /**
   * Write the contents of {@link #packetSendBuf} to the given {@link SocketChannel}
   *
   * @return the number of bytes actually written
   */
  public int handleWrite(SocketChannel socketChannel) throws IOException
  {
    try
    {
      packetSendBuf.flip();
      int count = socketChannel.write(packetSendBuf);
      packetSendBuf.compact();

      if (count == -1)
      {
        throw new ClosedChannelException();
      }

      return count;
    }
    catch (ClosedChannelException ex)
    {
      throw ex;
    }
    catch (IOException ex)
    {
      throw new ClosedChannelException();
    }
  }

  /**
   * Read the contents from the given {@link SocketChannel} to {@link #packetSendBuf}
   *
   * @return the number of bytes actually read
   */
  public int handleRead(SocketChannel socketChannel) throws IOException
  {
    try
    {
      int count = socketChannel.read(packetRecvBuf);
      if (count == -1)
      {
        throw new ClosedChannelException();
      }

      return count;
    }
    catch (ClosedChannelException ex)
    {
      throw ex;
    }
    catch (IOException ex)
    {
      throw new ClosedChannelException();
    }
  }

  public void close() throws IOException
  {
    if (sslEngine != null)
    {
      sslEngine.closeOutbound();
    }
  }

  public void checkRehandShake(SocketChannel socket) throws Exception
  {
    handShakeStatus = engineResult.getHandshakeStatus();
    needRehandShake = handShakeStatus == SSLEngineResult.HandshakeStatus.NEED_TASK || handShakeStatus == SSLEngineResult.HandshakeStatus.NEED_UNWRAP
        || handShakeStatus == SSLEngineResult.HandshakeStatus.NEED_WRAP;

    if (needRehandShake)
    {
      handShakeComplete = false;
    }

    checkInitialHandshake(socket);
    return;
  }

  public boolean isHandshakeComplete()
  {
    return handShakeComplete;
  }

  private boolean isHandshakeFinished()
  {
    return handShakeStatus != null && handShakeStatus == SSLEngineResult.HandshakeStatus.FINISHED;
  }

  private boolean performHandshake(SocketChannel socketChannel, boolean rehandShake) throws IOException
  {
    int nBytes = 0;
    if (!rehandShake)
    {
      handShakeStatus = sslEngine.getHandshakeStatus();
    }

    switch (handShakeStatus)
    {
    case NEED_WRAP:
      appSendBuf.flip();
      engineResult = sslEngine.wrap(appSendBuf, packetSendBuf);
      handShakeStatus = engineResult.getHandshakeStatus();
      appSendBuf.compact();

      switch (engineResult.getStatus())
      {
      case BUFFER_OVERFLOW:
        nBytes = handleWrite(socketChannel);
        break;

      case OK:
        while (packetSendBuf.position() > 0)
        {
          nBytes = handleWrite(socketChannel);
          if (nBytes == 0)
          {
            // Prevent spinning if the channel refused the write
            break;
          }
        }

        break;

      default:
        if (TRACER.isEnabled())
        {
          TRACER.trace("Need Wrap Operation: cannot handle ssl result status [" + engineResult.getStatus() + "]"); //$NON-NLS-1$
        }
      }

      return true;

    case NEED_UNWRAP:
      nBytes = handleRead(socketChannel);

      packetRecvBuf.flip();
      engineResult = sslEngine.unwrap(packetRecvBuf, appRecvBuf);
      handShakeStatus = engineResult.getHandshakeStatus();
      packetRecvBuf.compact();

      if (engineResult.getStatus() == SSLEngineResult.Status.BUFFER_UNDERFLOW && sslEngine.isInboundDone())
      {
        return false;
      }

      return engineResult.getStatus() != SSLEngineResult.Status.BUFFER_OVERFLOW;

    case NEED_TASK:
      executeTasks();
      return true;

    case NOT_HANDSHAKING:
      if (TRACER.isEnabled())
      {
        TRACER.trace("Not handshaking status occurs."); //$NON-NLS-1$
      }

      throw new ClosedChannelException();

    case FINISHED:
      return false;
    }

    return true;
  }

  private void executeTasks()
  {
    Runnable task;
    while ((task = sslEngine.getDelegatedTask()) != null)
    {
      executor.execute(task);

      if (TRACER.isEnabled())
      {
        TRACER.trace("Scheduled task: " + task); //$NON-NLS-1$
      }
    }
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

  /**
   * A separate class for better monitor debugging.
   *
   * @author Eike Stepper
   */
  private static final class ReadLock
  {
  }

  /**
   * A separate class for better monitor debugging.
   *
   * @author Eike Stepper
   */
  private static final class WriteLock
  {
  }
}
