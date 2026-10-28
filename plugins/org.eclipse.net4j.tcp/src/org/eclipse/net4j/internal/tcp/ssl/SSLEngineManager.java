/*
 * Copyright (c) 2011, 2012, 2015, 2016, 2022, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
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

  private volatile boolean handShakeComplete;

  private SSLEngineResult.HandshakeStatus handShakeStatus;

  private boolean needRehandShake;

  private ByteBuffer appSendBuf;

  private ByteBuffer appRecvBuf;

  private ByteBuffer packetSendBuf;

  private ByteBuffer packetRecvBuf;

  private SSLEngineResult engineResult;

  private boolean readNeedsMoreNetworkData;

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

        while ((!isHandshakeFinished() || packetSendBuf.position() > 0) && counter < handShakeTimeOut)
        {
          boolean progressed = false;

          if (packetSendBuf.position() > 0)
          {
            int written = handleWrite(socketChannel);
            progressed = written > 0;

            if (packetSendBuf.position() > 0)
            {
              if (progressed)
              {
                counter = 0;
              }
              else
              {
                ++counter;
              }

              if (!progressed && counter < handShakeTimeOut)
              {
                Thread.sleep(Math.max(1, handShakeWaitTime));
              }

              continue;
            }
          }

          if (progressed && isHandshakeFinished() && packetSendBuf.position() == 0)
          {
            break;
          }

          HandshakeStatus previousStatus = handShakeStatus;

          progressed |= performHandshake(socketChannel, needRehandShake);
          progressed |= handShakeStatus != previousStatus;
          if (progressed)
          {
            counter = 0;
          }
          else
          {
            ++counter;

            if (counter < handShakeTimeOut)
            {
              Thread.sleep(Math.max(1, handShakeWaitTime));
            }
          }
        }

        if ((!isHandshakeFinished() || packetSendBuf.position() > 0) && counter >= handShakeTimeOut)
        {
          throw new SSLException("SSL handshake timeout");
        }
      }
      catch (Exception ex)
      {
        if (ex instanceof InterruptedException)
        {
          Thread.currentThread().interrupt();
        }

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

  public synchronized int read(SocketChannel socketChannel) throws IOException
  {
    if (!handShakeComplete)
    {
      throw new SSLException("Handshake still not completed");
    }

    synchronized (readLock)
    {
      int initialTextCount = appRecvBuf.position();
      readNeedsMoreNetworkData = false;

      while (true)
      {
        if (sslEngine.isInboundDone())
        {
          return appRecvBuf.position() > initialTextCount ? appRecvBuf.position() : -1;
        }

        int count = 0;

        if (packetRecvBuf.position() == 0)
        {
          count = handleRead(socketChannel);
          if (count <= 0)
          {
            return appRecvBuf.position() > initialTextCount ? appRecvBuf.position() : count;
          }
        }

        packetRecvBuf.flip();
        engineResult = sslEngine.unwrap(packetRecvBuf, appRecvBuf);
        packetRecvBuf.compact();

        switch (engineResult.getStatus())
        {
        case BUFFER_UNDERFLOW:
          if (appRecvBuf.position() > initialTextCount)
          {
            readNeedsMoreNetworkData = true;
            return appRecvBuf.position();
          }

          if (packetRecvBuf.position() == packetRecvBuf.capacity())
          {
            ByteBuffer largerBuffer = ByteBuffer.allocate(packetRecvBuf.capacity() * 2);
            packetRecvBuf.flip();
            largerBuffer.put(packetRecvBuf);
            packetRecvBuf = largerBuffer;
          }

          count = handleRead(socketChannel);
          if (count <= 0)
          {
            readNeedsMoreNetworkData = true;
            return appRecvBuf.position() > initialTextCount ? appRecvBuf.position() : 0;
          }

          continue;

        case BUFFER_OVERFLOW:
          if (TRACER.isEnabled())
          {
            TRACER.trace("Buffer overflow on read method."); //$NON-NLS-1$
          }

          ByteBuffer largerAppBuffer = ByteBuffer.allocate(appRecvBuf.capacity() * 2);
          appRecvBuf.flip();
          largerAppBuffer.put(appRecvBuf);
          appRecvBuf = largerAppBuffer;
          continue;

        case CLOSED:
          throw new ClosedChannelException();

        default:
          // OK
        }

        if (appRecvBuf.position() > initialTextCount)
        {
          return appRecvBuf.position();
        }

        if (engineResult.bytesConsumed() == 0 && packetRecvBuf.position() > 0)
        {
          readNeedsMoreNetworkData = true;
          return 0;
        }
      }
    }
  }

  public synchronized boolean hasBufferedReceiveData()
  {
    return appRecvBuf.position() > 0 || packetRecvBuf.position() > 0;
  }

  public synchronized boolean readNeedsMoreNetworkData()
  {
    return readNeedsMoreNetworkData;
  }

  public synchronized int write(SocketChannel socketChannel) throws IOException
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

      if (packetSendBuf.position() > 0)
      {
        count = handleWrite(socketChannel);
        writeCount += count;

        if (packetSendBuf.position() > 0)
        {
          return writeCount;
        }
      }

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
          resizePacketSendBuf();
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
  public synchronized int handleWrite(SocketChannel socketChannel) throws IOException
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

  public synchronized void close() throws IOException
  {
    if (sslEngine != null)
    {
      sslEngine.closeOutbound();
    }
  }

  public synchronized void checkRehandShake(SocketChannel socket) throws Exception
  {
    handShakeStatus = engineResult.getHandshakeStatus();

    needRehandShake = handShakeStatus == SSLEngineResult.HandshakeStatus.NEED_TASK //
        || handShakeStatus == SSLEngineResult.HandshakeStatus.NEED_UNWRAP //
        || handShakeStatus == SSLEngineResult.HandshakeStatus.NEED_WRAP;
    if (needRehandShake)
    {
      handShakeComplete = false;
    }
  }

  public boolean isHandshakeComplete()
  {
    return handShakeComplete;
  }

  private boolean isHandshakeFinished()
  {
    return handShakeStatus == HandshakeStatus.FINISHED || handShakeStatus == HandshakeStatus.NOT_HANDSHAKING;
  }

  private boolean performHandshake(SocketChannel socketChannel, boolean rehandShake) throws IOException
  {
    HandshakeStatus previousStatus = handShakeStatus;

    if (!rehandShake)
    {
      handShakeStatus = sslEngine.getHandshakeStatus();
    }

    boolean progressed = handShakeStatus != previousStatus;

    switch (handShakeStatus)
    {
    case NEED_WRAP:
      appSendBuf.flip();
      engineResult = sslEngine.wrap(appSendBuf, packetSendBuf);
      handShakeStatus = engineResult.getHandshakeStatus();
      appSendBuf.compact();
      progressed |= handShakeStatus != previousStatus || engineResult.bytesConsumed() > 0 || engineResult.bytesProduced() > 0;

      switch (engineResult.getStatus())
      {
      case BUFFER_OVERFLOW:
        resizePacketSendBuf();
        return true;

      case OK:
        while (packetSendBuf.position() > 0)
        {
          int written = handleWrite(socketChannel);
          progressed |= written > 0;

          if (written == 0)
          {
            break;
          }
        }

        return progressed;

      case CLOSED:
        throw new ClosedChannelException();

      default:
        throw new SSLException("Unexpected result from TLS wrap: " + engineResult.getStatus()); //$NON-NLS-1$
      }

    case NEED_UNWRAP:
      int bytesRead = handleRead(socketChannel);

      packetRecvBuf.flip();
      engineResult = sslEngine.unwrap(packetRecvBuf, appRecvBuf);
      handShakeStatus = engineResult.getHandshakeStatus();
      packetRecvBuf.compact();
      progressed |= handShakeStatus != previousStatus || bytesRead > 0 || engineResult.bytesConsumed() > 0 || engineResult.bytesProduced() > 0;

      switch (engineResult.getStatus())
      {
      case BUFFER_UNDERFLOW:
        if (sslEngine.isInboundDone())
        {
          throw new ClosedChannelException();
        }

        if (packetRecvBuf.position() == packetRecvBuf.capacity())
        {
          resizePacketRecvBuf();
          return true;
        }

        return progressed;

      case BUFFER_OVERFLOW:
        resizeAppRecvBuf();
        return true;

      case CLOSED:
        throw new ClosedChannelException();

      default:
        return progressed;
      }

    case NEED_TASK:
      progressed |= executeTasks();
      handShakeStatus = sslEngine.getHandshakeStatus();
      return progressed || handShakeStatus != previousStatus;

    case NOT_HANDSHAKING:
      return progressed;

    case FINISHED:
      return progressed;
    }

    return progressed;
  }

  private boolean executeTasks()
  {
    boolean executedTask = false;
    Runnable task;

    while ((task = sslEngine.getDelegatedTask()) != null)
    {
      task.run();
      executedTask = true;

      if (TRACER.isEnabled())
      {
        TRACER.trace("Scheduled task: " + task); //$NON-NLS-1$
      }
    }

    return executedTask;
  }

  private void resizePacketSendBuf() throws SSLException
  {
    int currentCapacity = packetSendBuf.capacity();
    int packetBufferSize = sslEngine.getSession().getPacketBufferSize();

    int newCapacity = Math.max(currentCapacity * 2, packetBufferSize);
    if (newCapacity <= currentCapacity)
    {
      throw new SSLException("Unable to grow TLS packet send buffer"); //$NON-NLS-1$
    }

    ByteBuffer largerBuffer = ByteBuffer.allocate(newCapacity);
    packetSendBuf.flip();
    largerBuffer.put(packetSendBuf);
    packetSendBuf = largerBuffer;
  }

  private void resizeAppRecvBuf() throws SSLException
  {
    int currentCapacity = appRecvBuf.capacity();
    int applicationBufferSize = sslEngine.getSession().getApplicationBufferSize();

    int newCapacity = Math.max(currentCapacity * 2, applicationBufferSize);
    if (newCapacity <= currentCapacity)
    {
      throw new SSLException("Unable to grow TLS application receive buffer"); //$NON-NLS-1$
    }

    ByteBuffer largerBuffer = ByteBuffer.allocate(newCapacity);
    appRecvBuf.flip();
    largerBuffer.put(appRecvBuf);
    appRecvBuf = largerBuffer;
  }

  private void resizePacketRecvBuf() throws SSLException
  {
    int currentCapacity = packetRecvBuf.capacity();
    int packetBufferSize = sslEngine.getSession().getPacketBufferSize();

    int newCapacity = Math.max(currentCapacity * 2, packetBufferSize);
    if (newCapacity <= currentCapacity)
    {
      throw new SSLException("Unable to grow TLS packet receive buffer"); //$NON-NLS-1$
    }

    ByteBuffer largerBuffer = ByteBuffer.allocate(newCapacity);
    packetRecvBuf.flip();
    largerBuffer.put(packetRecvBuf);
    packetRecvBuf = largerBuffer;
  }

  private static KeyStore createKeyStore(String path, char[] password, boolean checkValidity) throws Exception
  {
    // Initial key material
    KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());

    InputStream in = null;

    try
    {
      in = IOUtil.newURL(path).openStream();
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
