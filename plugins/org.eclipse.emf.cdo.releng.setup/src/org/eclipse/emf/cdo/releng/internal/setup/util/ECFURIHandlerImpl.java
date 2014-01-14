/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.internal.setup.util;

import org.eclipse.emf.cdo.releng.setup.SetupConstants;
import org.eclipse.emf.cdo.releng.setup.util.FileUtil;

import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.impl.URIHandlerImpl;

import org.eclipse.ecf.core.security.ConnectContextFactory;
import org.eclipse.ecf.core.util.Proxy;
import org.eclipse.ecf.filetransfer.IFileTransferListener;
import org.eclipse.ecf.filetransfer.IRetrieveFileTransferOptions;
import org.eclipse.ecf.filetransfer.IncomingFileTransferException;
import org.eclipse.ecf.filetransfer.events.IFileTransferEvent;
import org.eclipse.ecf.filetransfer.events.IIncomingFileTransferReceiveDoneEvent;
import org.eclipse.ecf.filetransfer.events.IIncomingFileTransferReceiveStartEvent;
import org.eclipse.ecf.provider.filetransfer.identity.FileTransferID;
import org.eclipse.ecf.provider.filetransfer.identity.FileTransferNamespace;
import org.eclipse.ecf.provider.filetransfer.retrieve.UrlConnectionRetrieveFileTransfer;
import org.eclipse.ecf.provider.filetransfer.util.ProxySetupHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Eike Stepper
 */
public class ECFURIHandlerImpl extends URIHandlerImpl
{
  @Override
  public boolean exists(URI uri, Map<?, ?> options)
  {
    return super.exists(uri, options);
  }

  @Override
  public InputStream createInputStream(URI uri, Map<?, ?> options) throws IOException
  {
    String uriString = uri.toString();
    Proxy proxy = ProxySetupHelper.getProxy(uriString);
    UrlConnectionRetrieveFileTransfer fileTransfer = new UrlConnectionRetrieveFileTransfer();
    if (proxy != null)
    {
      fileTransfer.setProxy(proxy);

      String username = proxy.getUsername();
      String password = proxy.getPassword();
      if (username != null)
      {
        fileTransfer.setConnectContextForAuthentication(ConnectContextFactory.createUsernamePasswordConnectContext(
            username, password));
      }
      else if (password != null)
      {
        fileTransfer.setConnectContextForAuthentication(ConnectContextFactory.createPasswordConnectContext(password));
      }
    }

    final CountDownLatch receiveDoneLatch = new CountDownLatch(1);
    final AtomicReference<Exception> exception = new AtomicReference<Exception>();

    class FileTransferListener implements IFileTransferListener
    {
      public ByteArrayOutputStream out;

      public void handleTransferEvent(IFileTransferEvent event)
      {
        if (event instanceof IIncomingFileTransferReceiveStartEvent)
        {
          out = new ByteArrayOutputStream();

          try
          {
            ((IIncomingFileTransferReceiveStartEvent)event).receive(out);
          }
          catch (IOException ex)
          {
            exception.set(ex);
          }
        }
        else if (event instanceof IIncomingFileTransferReceiveDoneEvent)
        {
          IIncomingFileTransferReceiveDoneEvent done = (IIncomingFileTransferReceiveDoneEvent)event;
          Exception ex = done.getException();
          if (ex != null)
          {
            exception.set(ex);
          }

          receiveDoneLatch.countDown();
        }
      }
    }

    File cacheFile = new File(SetupConstants.CACHE_FOLDER, FileUtil.encodeFileName(uri.toString()));

    for (int i = 0;; ++i)
    {
      FileTransferListener transferListener = new FileTransferListener();

      try
      {
        FileTransferID fileTransferID = new FileTransferID(new FileTransferNamespace(), new java.net.URI(uriString));
        Map<Object, Object> requestOptions = new HashMap<Object, Object>();
        requestOptions.put(IRetrieveFileTransferOptions.CONNECT_TIMEOUT, 10000);
        requestOptions.put(IRetrieveFileTransferOptions.READ_TIMEOUT, 10000);
        fileTransfer.sendRetrieveRequest(fileTransferID, transferListener, Collections.emptyMap());
      }
      catch (URISyntaxException ex)
      {
        throw new IOException(ex);
      }
      catch (IncomingFileTransferException ex)
      {
        throw new IOException(ex);
      }

      try
      {
        receiveDoneLatch.await(60, TimeUnit.SECONDS);
      }
      catch (InterruptedException ex)
      {
        throw new IOException(ex);
      }

      if (exception.get() != null)
      {
        if (!(exception.get().getCause() instanceof SocketTimeoutException) || i > 2)
        {
          if (cacheFile.isFile())
          {
            return new FileInputStream(cacheFile);
          }

          throw new IOException(exception.get());
        }

        continue;
      }

      byte[] bytes = transferListener.out.toByteArray();

      SetupConstants.CACHE_FOLDER.mkdirs();
      IOUtil.writeFile(cacheFile, bytes);

      return new ByteArrayInputStream(bytes);
    }
  }
}
