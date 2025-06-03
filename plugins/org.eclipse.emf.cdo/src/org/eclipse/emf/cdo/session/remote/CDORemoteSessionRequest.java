/*
 * Copyright (c) 2022, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.session.remote;

import org.eclipse.emf.cdo.common.util.TransportException;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOSessionRegistry;

import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.container.ContainerEventAdapter;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.IDeactivateable;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.om.OMPlatform;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * An {@link #send(CDORemoteSession) asynchronous} or {@link #sendSynchronous(CDORemoteSession) synchronous} request
 * to a {@link CDORemoteSession remote session}.
 *
 * @author Eike Stepper
 * @since 4.17
 */
public final class CDORemoteSessionRequest
{
  public static final long DEFAULT_TIMEOUT = OMPlatform.INSTANCE.getProperty("org.eclipse.emf.cdo.RemoteSessionRequest.DEFAULT_TIMEOUT", 5000L);

  private static final boolean DEBUG = OMPlatform.INSTANCE.isProperty("org.eclipse.emf.cdo.RemoteSessionRequest.DEBUG");

  private static final String NOTIFICATION_PREFIX = "org.eclipse.emf.cdo.NOTIFICATION/";

  private static final String REQUEST_PREFIX = "org.eclipse.emf.cdo.REQUEST/";

  private static final String RESPONSE_PREFIX = "org.eclipse.emf.cdo.RESPONSE/";

  private static final AtomicLong lastMessageID = new AtomicLong();

  private static Consumer<String> defaultTimeoutHandler = DEBUG ? type -> OM.LOG.warn("Timeout: " + type) : null;

  private static BiConsumer<String, Throwable> defaultExceptionHandler = DEBUG ? (type, ex) -> OM.LOG.error("Exception: " + type, ex) : null;

  private final String type;

  private final byte[] data;

  private long timeout = DEFAULT_TIMEOUT;

  private Consumer<String> timeoutHandler = defaultTimeoutHandler;

  private BiConsumer<String, Throwable> exceptionHandler = defaultExceptionHandler;

  private Consumer<byte[]> responseConsumer;

  public CDORemoteSessionRequest(String type, byte[] data)
  {
    this.type = type;
    this.data = data;
  }

  public CDORemoteSessionRequest(String type)
  {
    this(type, null);
  }

  public long timeout()
  {
    return timeout;
  }

  public CDORemoteSessionRequest timeout(long timeout)
  {
    this.timeout = timeout;
    return this;
  }

  public CDORemoteSessionRequest onTimeout(Consumer<String> timeoutHandler)
  {
    this.timeoutHandler = timeoutHandler;
    return this;
  }

  public CDORemoteSessionRequest onException(BiConsumer<String, Throwable> exceptionHandler)
  {
    this.exceptionHandler = exceptionHandler;
    return this;
  }

  public CDORemoteSessionRequest onResponse(Consumer<byte[]> responseConsumer)
  {
    this.responseConsumer = responseConsumer;
    return this;
  }

  public void send(CDORemoteSession recipient)
  {
    ConcurrencyUtil.execute(recipient.getManager(), () -> sendSynchronous(recipient));
  }

  public void sendSynchronous(CDORemoteSession recipient)
  {
    CDORemoteSessionManager manager = recipient.getManager();
    String messageDescriptor = type + "/" + lastMessageID.incrementAndGet();
    String requestType = (responseConsumer != null ? REQUEST_PREFIX : NOTIFICATION_PREFIX) + messageDescriptor;

    if (DEBUG)
    {
      System.out.println("Sending " + requestType);
    }

    try
    {
      if (responseConsumer != null)
      {
        String responseType = RESPONSE_PREFIX + messageDescriptor;

        AtomicBoolean responseReceived = new AtomicBoolean();
        CountDownLatch finished = new CountDownLatch(1);

        IListener responseListener = new CDORemoteSessionManager.EventAdapter()
        {
          @Override
          protected void onMessageReceived(CDORemoteSession sender, CDORemoteSessionMessage message)
          {
            if (sender == recipient && message.getType().equals(responseType))
            {
              if (DEBUG)
              {
                System.out.println("Received " + responseType);
              }
              try
              {
                responseReceived.set(true);
                responseConsumer.accept(message.getData());
              }
              catch (Throwable ex)
              {
                if (exceptionHandler != null)
                {
                  exceptionHandler.accept(requestType, ex);
                }
              }
              finally
              {
                finished.countDown();
              }
            }
          }

          @Override
          protected void onClosed(CDORemoteSession sender)
          {
            if (sender == recipient)
            {
              finished.countDown();
            }
          }
        };

        try
        {
          manager.addListener(responseListener);
          long deadline = DEBUG ? Long.MAX_VALUE : System.currentTimeMillis() + timeout;

          // Send two-way request.
          doSend(recipient, requestType);

          while (System.currentTimeMillis() < deadline)
          {
            if (finished.await(100, TimeUnit.MILLISECONDS))
            {
              if (responseReceived.get())
              {
                // Count has reached zero with a response. Exit sendSynchronous().
                return;
              }

              // Count has reached zero without a response. Proceed with timeout handling.
              break;
            }
          }

          if (timeoutHandler != null)
          {
            timeoutHandler.accept(requestType);
          }
        }
        finally
        {
          manager.removeListener(responseListener);
        }
      }
      else
      {
        // Send one-way notification.
        doSend(recipient, requestType);
      }
    }
    catch (Throwable ex)
    {
      if (exceptionHandler != null)
      {
        exceptionHandler.accept(requestType, ex);
      }
    }
  }

  private void doSend(CDORemoteSession recipient, String requestType)
  {
    CDORemoteSessionMessage message = new CDORemoteSessionMessage(requestType, data);
    if (!recipient.sendMessage(message))
    {
      throw new TransportException("Message could not be delivered to the server: " + message);
    }
  }

  public static Consumer<String> getDefaultTimeoutHandler()
  {
    return defaultTimeoutHandler;
  }

  public static void setDefaultTimeoutHandler(Consumer<String> defaultTimeoutHandler)
  {
    CDORemoteSessionRequest.defaultTimeoutHandler = defaultTimeoutHandler;
  }

  public static BiConsumer<String, Throwable> getDefaultExceptionHandler()
  {
    return defaultExceptionHandler;
  }

  public static void setDefaultExceptionHandler(BiConsumer<String, Throwable> defaultExceptionHandler)
  {
    CDORemoteSessionRequest.defaultExceptionHandler = defaultExceptionHandler;
  }

  /**
   * Handles incoming {@link CDORemoteSessionRequest requests} in the context of a single {@link CDOSession#getRemoteSessionManager() session}
   * and optionally sends back response {@link CDORemoteSessionMessage messages}.
   *
   * @author Eike Stepper
   */
  public static abstract class RequestHandler implements IDeactivateable
  {
    private final IListener managerListener = new CDORemoteSessionManager.EventAdapter()
    {
      @Override
      protected void onMessageReceived(CDORemoteSession sender, CDORemoteSessionMessage message)
      {
        String type = message.getType();

        if (type.startsWith(notificationType))
        {
          if (DEBUG)
          {
            System.out.println("Received " + type);
          }

          byte[] request = message.getData();
          handleRequest(sender, request);
        }
        else if (type.startsWith(requestType))
        {
          if (DEBUG)
          {
            System.out.println("Received " + type);
          }

          byte[] request = message.getData();
          byte[] response = handleRequest(sender, request);

          String messageDescriptor = type.substring(REQUEST_PREFIX.length());
          String responseType = RESPONSE_PREFIX + messageDescriptor;
          sender.sendMessage(new CDORemoteSessionMessage(responseType, response));
        }
      }

      @Override
      protected void onDeactivated(ILifecycle lifecycle)
      {
        deactivate();
      }
    };

    private final String notificationType;

    private final String requestType;

    private final CDORemoteSessionManager manager;

    private boolean deactivated;

    public RequestHandler(CDORemoteSessionManager manager, String type)
    {
      notificationType = NOTIFICATION_PREFIX + type;
      requestType = REQUEST_PREFIX + type;

      this.manager = manager;
      manager.addListener(managerListener);
    }

    @Override
    public Exception deactivate()
    {
      if (!deactivated)
      {
        manager.removeListener(managerListener);
        deactivated = true;
      }

      return null;
    }

    protected abstract byte[] handleRequest(CDORemoteSession sender, byte[] request);
  }

  /**
   * Handles incoming {@link CDORemoteSessionRequest requests} in the context of all {@link CDOSessionRegistry#INSTANCE globally registered} sessions
   * and optionally sends back response {@link CDORemoteSessionMessage messages}.
   *
   * @author Eike Stepper
   */
  public static abstract class GlobalRequestHandler implements IDeactivateable
  {
    private final IListener sessionRegistryListener = new ContainerEventAdapter<CDOSessionRegistry.Registration>()
    {
      @Override
      protected void onAdded(IContainer<CDOSessionRegistry.Registration> container, CDOSessionRegistry.Registration registration)
      {
        addSession(registration.getSession());
      }

      @Override
      protected void onRemoved(IContainer<CDOSessionRegistry.Registration> container, CDOSessionRegistry.Registration registration)
      {
        removeSession(registration.getSession());
      }
    };

    private final Map<CDOSession, RequestHandler> requestHandlers = new HashMap<>();

    private final String type;

    private boolean deactivated;

    public GlobalRequestHandler(String type)
    {
      this.type = type;

      for (CDOSession session : CDOSessionRegistry.INSTANCE.getSessions())
      {
        addSession(session);
      }

      CDOSessionRegistry.INSTANCE.addListener(sessionRegistryListener);
    }

    @Override
    public Exception deactivate()
    {
      if (!deactivated)
      {
        CDOSessionRegistry.INSTANCE.removeListener(sessionRegistryListener);

        for (RequestHandler requestHandler : requestHandlers.values())
        {
          requestHandler.deactivate();
        }

        requestHandlers.clear();
        deactivated = true;
      }

      return null;
    }

    protected abstract byte[] createResponse(CDORemoteSession sender, byte[] request);

    private void addSession(CDOSession session)
    {
      CDORemoteSessionManager manager = session.getRemoteSessionManager();
      RequestHandler requestHandler = new RequestHandler(manager, type)
      {
        @Override
        protected byte[] handleRequest(CDORemoteSession sender, byte[] request)
        {
          return GlobalRequestHandler.this.createResponse(sender, request);
        }
      };

      synchronized (requestHandlers)
      {
        requestHandlers.put(session, requestHandler);
      }
    }

    private void removeSession(CDOSession session)
    {
      RequestHandler requestHandler;
      synchronized (requestHandlers)
      {
        requestHandler = requestHandlers.remove(session);
      }

      if (requestHandler != null)
      {
        requestHandler.deactivate();
      }
    }
  }
}
