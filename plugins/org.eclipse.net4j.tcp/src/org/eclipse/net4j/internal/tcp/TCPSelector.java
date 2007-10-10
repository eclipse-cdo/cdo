/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.internal.tcp;

import org.eclipse.net4j.internal.tcp.bundle.OM;
import org.eclipse.net4j.internal.util.lifecycle.Lifecycle;
import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.tcp.ITCPSelector;
import org.eclipse.net4j.tcp.ITCPSelectorListener;
import org.eclipse.net4j.tcp.ITCPSelectorListener.Active;
import org.eclipse.net4j.tcp.ITCPSelectorListener.Passive;

import java.io.IOException;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedChannelException;
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

  private transient Queue<Runnable> clientOperations = new ConcurrentLinkedQueue<Runnable>();

  private transient Queue<Runnable> serverOperations = new ConcurrentLinkedQueue<Runnable>();

  private transient Thread thread;

  private transient boolean running;

  public TCPSelector()
  {
  }

  public void register(final ServerSocketChannel channel, final Passive listener)
  {
    assertValidListener(listener);
    order(false, new Runnable()
    {
      public void run()
      {
        registerAcceptor(channel, listener);
      }

      @Override
      public String toString()
      {
        return "REGISTER " + channel;
      }
    });
  }

  public void register(final SocketChannel channel, final boolean client, final Active listener)
  {
    assertValidListener(listener);
    order(client, new Runnable()
    {
      public void run()
      {
        registerConnector(channel, listener, client);
      }

      @Override
      public String toString()
      {
        return "REGISTER " + channel;
      }
    });
  }

  public void setConnectInterest(final SelectionKey selectionKey, boolean client, final boolean on)
  {
    order(client, new Runnable()
    {
      public void run()
      {
        SelectorUtil.setConnectInterest(selectionKey, on);
      }

      @Override
      public String toString()
      {
        return "INTEREST CONNECT " + selectionKey.channel() + " = " + on;
      }
    });
  }

  public void setReadInterest(final SelectionKey selectionKey, boolean client, final boolean on)
  {
    order(client, new Runnable()
    {
      public void run()
      {
        SelectorUtil.setReadInterest(selectionKey, on);
      }

      @Override
      public String toString()
      {
        return "INTEREST READ " + selectionKey.channel() + " = " + on;
      }
    });
  }

  public void setWriteInterest(final SelectionKey selectionKey, boolean client, final boolean on)
  {
    order(client, new Runnable()
    {
      public void run()
      {
        SelectorUtil.setWriteInterest(selectionKey, on);
      }

      @Override
      public String toString()
      {
        return "INTEREST WRITE " + selectionKey.channel() + " = " + on;
      }

    });
  }

  public void run()
  {
    while (running && !Thread.interrupted())
    {
      try
      {
        execute(true);
        execute(false);
        if (selector != null && selector.select() > 0)
        {
          Iterator<SelectionKey> it = selector.selectedKeys().iterator();
          while (it.hasNext())
          {
            SelectionKey selKey = it.next();
            it.remove();

            try
            {
              handleSelection(selKey);
            }
            catch (CancelledKeyException ignore)
            {
              ; // Do nothing
            }
            catch (NullPointerException ignore)
            {
              ; // Do nothing
            }
            catch (Exception ex)
            {
              OM.LOG.error(ex);
              selKey.cancel();
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

  @Override
  public String toString()
  {
    return "TCPSelector"; //$NON-NLS-1$
  }

  protected void handleSelection(SelectionKey selKey) throws IOException
  {
    SelectableChannel channel = selKey.channel();
    if (channel instanceof ServerSocketChannel)
    {
      ServerSocketChannel ssChannel = (ServerSocketChannel)selKey.channel();
      if (ssChannel.isOpen())
      {
        ITCPSelectorListener.Passive listener = (ITCPSelectorListener.Passive)selKey.attachment();

        if (selKey.isAcceptable())
        {
          if (TRACER.isEnabled())
          {
            TRACER.trace("Accepting " + ssChannel); //$NON-NLS-1$
          }

          listener.handleAccept(this, ssChannel);
        }
      }
    }
    else if (channel instanceof SocketChannel)
    {
      SocketChannel sChannel = (SocketChannel)channel;
      ITCPSelectorListener.Active listener = (ITCPSelectorListener.Active)selKey.attachment();

      if (selKey.isConnectable())
      {
        if (TRACER.isEnabled())
        {
          TRACER.trace("Connecting " + sChannel); //$NON-NLS-1$
        }

        listener.handleConnect(this, sChannel);
      }

      if (selKey.isReadable())
      {
        if (TRACER.isEnabled())
        {
          TRACER.trace("Reading " + sChannel); //$NON-NLS-1$
        }

        listener.handleRead(this, sChannel);
      }

      if (selKey.isWritable())
      {
        if (TRACER.isEnabled())
        {
          TRACER.trace("Writing " + sChannel); //$NON-NLS-1$
        }

        listener.handleWrite(this, sChannel);
      }
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    running = true;
    selector = Selector.open();
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
      if (exception == null)
      {
        exception = ex;
      }
    }
    finally
    {
      thread = null;
    }

    try
    {
      selector.close();
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
      TRACER.format("Ordering {0} operation {1}", client, operation);
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

  private void execute(boolean client)
  {
    Runnable operation;
    Queue<Runnable> operations = client ? clientOperations : serverOperations;
    while ((operation = operations.poll()) != null)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Ordering {0} operation {1}", client, operation);
      }

      operation.run();
    }
  }

  private void registerAcceptor(final ServerSocketChannel channel, final ITCPSelectorListener.Passive listener)
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Registering " + channel); //$NON-NLS-1$
    }

    try
    {
      int interest = SelectionKey.OP_ACCEPT;
      SelectionKey selectionKey = channel.register(selector, interest, listener);
      listener.handleRegistration(selectionKey);
    }
    catch (ClosedChannelException ignore)
    {
    }
  }

  private void registerConnector(final SocketChannel channel, final ITCPSelectorListener.Active listener, boolean client)
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Registering " + channel); //$NON-NLS-1$
    }

    try
    {
      // int interest = SelectionKey.OP_READ;
      // if (connect)
      // {
      // interest |= SelectionKey.OP_CONNECT;
      // }

      int interest = client ? SelectionKey.OP_CONNECT : SelectionKey.OP_READ;
      SelectionKey selectionKey = channel.register(selector, interest, listener);
      listener.handleRegistration(selectionKey);
    }
    catch (ClosedChannelException ignore)
    {
    }
  }
}
