/*
 * Copyright (c) 2007-2013, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.tcp;

import org.eclipse.net4j.internal.tcp.bundle.OM;
import org.eclipse.net4j.tcp.ITCPActiveSelectorListener;
import org.eclipse.net4j.tcp.ITCPPassiveSelectorListener;
import org.eclipse.net4j.tcp.ITCPSelector;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Eike Stepper
 */
public class TCPSelector extends Lifecycle implements ITCPSelector, Runnable
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, TCPSelector.class);

  private Selector selector;

  /**
   * Always processed <b>after</b> {@link #serverOperations}.
   */
  private transient Queue<Runnable> clientOperations = new ConcurrentLinkedQueue<Runnable>();

  /**
   * Always processed <b>before</b> {@link #clientOperations}.
   */
  private transient Queue<Runnable> serverOperations = new ConcurrentLinkedQueue<Runnable>();

  private transient Thread thread;

  private transient boolean running;

  public TCPSelector()
  {
  }

  @Override
  public Selector getSocketSelector()
  {
    return selector;
  }

  @Override
  public String toString()
  {
    return "TCPSelector"; //$NON-NLS-1$
  }

  @Override
  public void orderRegistration(final ServerSocketChannel channel, final ITCPPassiveSelectorListener listener)
  {
    assertValidListener(listener);
    order(false, new Runnable()
    {
      @Override
      public void run()
      {
        executeRegistration(channel, listener);
      }

      @Override
      public String toString()
      {
        return "REGISTER " + channel; //$NON-NLS-1$
      }
    });
  }

  @Override
  public void orderRegistration(final SocketChannel channel, final boolean client, final ITCPActiveSelectorListener listener)
  {
    assertValidListener(listener);
    order(client, new Runnable()
    {
      @Override
      public void run()
      {
        executeRegistration(channel, listener, client);
      }

      @Override
      public String toString()
      {
        return "REGISTER " + channel; //$NON-NLS-1$
      }
    });
  }

  @Override
  public void orderConnectInterest(final SelectionKey selectionKey, boolean client, final boolean on)
  {
    order(client, new Runnable()
    {
      @Override
      public void run()
      {
        SelectorUtil.setConnectInterest(selectionKey, on);
      }

      @Override
      public String toString()
      {
        return "INTEREST CONNECT " + selectionKey.channel() + " = " + on; //$NON-NLS-1$ //$NON-NLS-2$
      }
    });
  }

  @Override
  public void orderReadInterest(final SelectionKey selectionKey, boolean client, final boolean on)
  {
    order(client, new Runnable()
    {
      @Override
      public void run()
      {
        SelectorUtil.setReadInterest(selectionKey, on);
      }

      @Override
      public String toString()
      {
        return "INTEREST READ " + selectionKey.channel() + " = " + on; //$NON-NLS-1$ //$NON-NLS-2$
      }
    });
  }

  @Override
  public void orderWriteInterest(final SelectionKey selectionKey, boolean client, final boolean on)
  {
    order(client, new Runnable()
    {
      @Override
      public void run()
      {
        SelectorUtil.setWriteInterest(selectionKey, on);
      }

      @Override
      public String toString()
      {
        return "INTEREST WRITE " + selectionKey.channel() + " = " + on; //$NON-NLS-1$ //$NON-NLS-2$
      }
    });
  }

  @Override
  public void run()
  {
    while (running && !Thread.interrupted())
    {
      try
      {
        processOperations(false);
        processOperations(true);
        if (selector != null && selector.select() > 0)
        {
          Iterator<SelectionKey> it = selector.selectedKeys().iterator();
          while (it.hasNext())
          {
            SelectionKey selKey = it.next();
            it.remove();

            SelectableChannel channel = selKey.channel();
            if (channel.isOpen())
            {
              try
              {
                handleSelection(selKey);
              }
              catch (CancelledKeyException ignore)
              {
                // Do nothing
              }
              catch (NullPointerException ignore)
              {
                // Do nothing
              }
              catch (Exception ex)
              {
                OM.LOG.info(ex.getMessage());
                selKey.cancel();
              }
            }
          }
        }
      }
      catch (NullPointerException ex)
      {
        break;
      }
      catch (ClosedSelectorException ex)
      {
        break;
      }
      catch (Exception ex)
      {
        OM.LOG.error(ex);
        break;
      }
    }

    deactivate();
  }

  protected void handleSelection(SelectionKey selKey) throws IOException
  {
    SelectableChannel channel = selKey.channel();
    if (channel instanceof ServerSocketChannel)
    {
      ITCPPassiveSelectorListener listener = (ITCPPassiveSelectorListener)selKey.attachment();

      if (selKey.isAcceptable())
      {
        if (TRACER.isEnabled())
        {
          TRACER.trace("Accepting " + channel); //$NON-NLS-1$
        }

        listener.handleAccept(this, (ServerSocketChannel)channel);
      }
    }
    else if (channel instanceof SocketChannel)
    {
      ITCPActiveSelectorListener listener = (ITCPActiveSelectorListener)selKey.attachment();

      if (selKey.isConnectable())
      {
        if (TRACER.isEnabled())
        {
          TRACER.trace("Connecting " + channel); //$NON-NLS-1$
        }

        listener.handleConnect(this, (SocketChannel)channel);
      }

      if (selKey.isReadable())
      {
        if (TRACER.isEnabled())
        {
          TRACER.trace("Reading " + channel); //$NON-NLS-1$
        }

        listener.handleRead(this, (SocketChannel)channel);
      }

      if (selKey.isWritable())
      {
        if (TRACER.isEnabled())
        {
          TRACER.trace("Writing " + channel); //$NON-NLS-1$
        }

        listener.handleWrite(this, (SocketChannel)channel);
      }
    }
  }

  protected Selector openSelector() throws IOException
  {
    return Selector.open();
  }

  protected void closeSelector() throws IOException
  {
    selector.close();
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    running = true;
    selector = openSelector();

    thread = new Thread(this, "TCPSelector"); //$NON-NLS-1$
    thread.setDaemon(true);
    thread.start();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    running = false;
    selector.wakeup();
    Exception exception = null;

    try
    {
      thread.join(200);
    }
    catch (RuntimeException ex)
    {
      exception = ex;
    }
    finally
    {
      thread = null;
    }

    try
    {
      closeSelector();
    }
    catch (Exception ex)
    {
      if (exception == null)
      {
        exception = ex;
      }
    }
    finally
    {
      selector = null;
    }

    super.doDeactivate();
    if (exception != null)
    {
      throw exception;
    }
  }

  private void assertValidListener(Object listener)
  {
    if (listener == null)
    {
      throw new IllegalArgumentException("listener == null"); //$NON-NLS-1$
    }
  }

  private void order(boolean client, Runnable operation)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Ordering {0} operation {1}", client ? "client" : "server", operation); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    if (client)
    {
      clientOperations.add(operation);
    }
    else
    {
      serverOperations.add(operation);
    }

    if (selector != null)
    {
      selector.wakeup();
    }
  }

  private void processOperations(boolean client)
  {
    Runnable operation;
    Queue<Runnable> operations = client ? clientOperations : serverOperations;
    while ((operation = operations.poll()) != null)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Executing {0} operation {1}", client ? "client" : "server", operation); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      }

      operation.run();
    }
  }

  private void executeRegistration(final ServerSocketChannel channel, final ITCPPassiveSelectorListener listener)
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Registering " + channel); //$NON-NLS-1$
    }

    try
    {
      listener.handleRegistration(this, channel);
    }
    catch (Exception ex)
    {
      OM.LOG.debug(ex);
    }
  }

  private void executeRegistration(final SocketChannel channel, final ITCPActiveSelectorListener listener, boolean client)
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Registering " + channel); //$NON-NLS-1$
    }

    try
    {
      listener.handleRegistration(this, channel);
    }
    catch (Exception ex)
    {
      OM.LOG.debug(ex);
    }
  }
}
