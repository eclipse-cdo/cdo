/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.internal.tcp;

import org.eclipse.net4j.tcp.TCPSelector;
import org.eclipse.net4j.tcp.TCPSelectorListener;
import org.eclipse.net4j.tcp.TCPSelectorListener.Active;
import org.eclipse.net4j.tcp.TCPSelectorListener.Passive;
import org.eclipse.net4j.util.lifecycle.LifecycleImpl;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.internal.net4j.bundle.Net4j;

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
public class TCPSelectorImpl extends LifecycleImpl implements TCPSelector, Runnable
{
  private static final ContextTracer TRACER = new ContextTracer(Net4j.DEBUG_SELECTOR, TCPSelectorImpl.class);

  private Selector selector;

  private Queue<Runnable> pendingOperations = new ConcurrentLinkedQueue();

  private Thread thread;

  public TCPSelectorImpl()
  {
  }

  public void invokeAsync(final Runnable operation)
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Pending operation " + operation);
    }

    pendingOperations.add(operation);
    selector.wakeup();
  }

  public void registerAsync(final ServerSocketChannel channel, final Passive listener)
  {
    assertValidListener(listener);
    invokeAsync(new Runnable()
    {
      public void run()
      {
        doRegister(channel, listener);
      }

      @Override
      public String toString()
      {
        return "REGISTER " + channel;
      }
    });
  }

  public void registerAsync(final SocketChannel channel, final Active listener)
  {
    assertValidListener(listener);
    invokeAsync(new Runnable()
    {
      public void run()
      {
        doRegister(channel, listener);
      }

      @Override
      public String toString()
      {
        return "REGISTER " + channel;
      }
    });
  }

  public void setConnectInterest(final SelectionKey selectionKey, final boolean on)
  {
    invokeAsync(new Runnable()
    {
      public void run()
      {
        SelectorUtil.setConnectInterest(selectionKey, on);
      }

      @Override
      public String toString()
      {
        return "INTEREST CONNECT " + selectionKey + " = " + on;
      }
    });
  }

  public void setReadInterest(final SelectionKey selectionKey, final boolean on)
  {
    invokeAsync(new Runnable()
    {
      public void run()
      {
        SelectorUtil.setReadInterest(selectionKey, on);
      }

      @Override
      public String toString()
      {
        return "INTEREST READ " + selectionKey + " = " + on;
      }
    });
  }

  public void setWriteInterest(final SelectionKey selectionKey, final boolean on)
  {
    invokeAsync(new Runnable()
    {
      public void run()
      {
        SelectorUtil.setWriteInterest(selectionKey, on);
      }

      @Override
      public String toString()
      {
        return "INTEREST WRITE " + selectionKey + " = " + on;
      }

    });
  }

  public void run()
  {
    while (isActive())
    {
      if (Thread.interrupted())
      {
        deactivate();
        break;
      }

      try
      {
        Runnable operation;
        while ((operation = pendingOperations.poll()) != null)
        {
          if (TRACER.isEnabled())
          {
            TRACER.trace("Executing operation " + operation);
          }

          operation.run();
        }

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
              Net4j.LOG.error(ex);
              selKey.cancel();
            }
          }
        }
      }
      catch (ClosedSelectorException ex)
      {
        break;
      }
      catch (Exception ex)
      {
        Net4j.LOG.error(ex);
        deactivate();
        break;
      }
    }
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
      TCPSelectorListener.Passive listener = (TCPSelectorListener.Passive)selKey.attachment();

      if (selKey.isAcceptable())
      {
        if (TRACER.isEnabled())
        {
          TRACER.trace("Accepting " + ssChannel); //$NON-NLS-1$
        }

        listener.handleAccept(this, ssChannel);
      }
    }
    else if (channel instanceof SocketChannel)
    {
      SocketChannel sChannel = (SocketChannel)channel;
      TCPSelectorListener.Active listener = (TCPSelectorListener.Active)selKey.attachment();

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
  protected void onActivate() throws Exception
  {
    selector = Selector.open();
    thread = new Thread(this, "selector"); //$NON-NLS-1$
    thread.setDaemon(true);
    thread.start();
  }

  @Override
  protected void onDeactivate() throws Exception
  {
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

  private void doRegister(final ServerSocketChannel channel, final TCPSelectorListener.Passive listener)
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Registering " + channel); //$NON-NLS-1$
    }

    try
    {
      channel.register(selector, SelectionKey.OP_ACCEPT, listener);
    }
    catch (ClosedChannelException ignore)
    {
      ;
    }
  }

  private void doRegister(final SocketChannel channel, final TCPSelectorListener.Active listener)
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Registering " + channel); //$NON-NLS-1$
    }

    try
    {
      int interest = SelectionKey.OP_CONNECT | SelectionKey.OP_READ;
      SelectionKey selectionKey = channel.register(selector, interest, listener);
      listener.registered(selectionKey);
    }
    catch (ClosedChannelException ignore)
    {
      ;
    }
  }
}
