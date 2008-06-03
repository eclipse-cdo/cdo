/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util.om.trace;

import org.eclipse.net4j.internal.util.bundle.OM;
import org.eclipse.net4j.util.io.IOUtil;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.EventObject;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Eike Stepper
 */
public class RemoteTraceServer
{
  public static final String DEFAULT_ADDRESS = "0.0.0.0"; //$NON-NLS-1$

  public static final int DEFAULT_PORT = 2035;

  public static final int ANY_PORT = 0;

  private static long lastEventID = 0;

  private int port;

  private String address;

  private ServerSocket serverSocket;

  private Queue<Listener> listeners = new ConcurrentLinkedQueue<Listener>();

  public RemoteTraceServer() throws IOException
  {
    this(DEFAULT_PORT);
  }

  public RemoteTraceServer(int port) throws IOException
  {
    this(port, DEFAULT_ADDRESS);
  }

  public RemoteTraceServer(int port, String address) throws IOException
  {
    this.port = port;
    this.address = address;
    serverSocket = bind();
    new Thread("RemoteTraceServer")
    {
      @Override
      public void run()
      {
        handleConnections();
      }
    }.start();
  }

  public void addListener(Listener listener)
  {
    if (!listeners.contains(listener))
    {
      listeners.add(listener);
    }
  }

  public void removeListener(Listener listener)
  {
    listeners.remove(listener);
  }

  public Exception close()
  {
    try
    {
      serverSocket.close();
      return null;
    }
    catch (IOException ex)
    {
      OM.LOG.error(ex);
      return ex;
    }
  }

  protected ServerSocket bind() throws IOException
  {
    InetAddress addr = InetAddress.getByName(this.address);
    return new ServerSocket(port, 5, addr);
  }

  protected void handleConnections()
  {
    for (;;)
    {
      try
      {
        final Socket socket = serverSocket.accept();
        new Thread()
        {
          @Override
          public void run()
          {
            handleSession(socket);
          }
        }.start();
      }
      catch (IOException ex)
      {
        if (!serverSocket.isClosed())
        {
          IOUtil.print(ex);
        }

        break;
      }
    }
  }

  protected void handleSession(Socket socket)
  {
    try
    {
      InputStream inputStream = socket.getInputStream();
      DataInputStream in = new DataInputStream(inputStream);

      for (;;)
      {
        handleTrace(in);
      }
    }
    catch (IOException ex)
    {
      IOUtil.print(ex);
    }
  }

  protected void handleTrace(DataInputStream in) throws IOException
  {
    Event event = new Event();
    event.timeStamp = in.readLong();
    event.agentID = in.readUTF();
    event.bundleID = in.readUTF();
    event.tracerName = in.readUTF();
    event.context = in.readUTF();
    event.message = in.readUTF();
    if (in.readBoolean())
    {
      event.throwable = in.readUTF();
      int size = in.readInt();
      event.stackTrace = new StackTraceElement[size];
      for (int i = 0; i < size; i++)
      {
        String className = in.readUTF();
        String methodName = in.readUTF();
        String fileName = in.readUTF();
        int lineNumber = in.readInt();
        event.stackTrace[i] = new StackTraceElement(className, methodName, fileName, lineNumber);
      }
    }

    fireEvent(event);
  }

  protected void fireEvent(Event event)
  {
    for (Listener listener : listeners)
    {
      try
      {
        listener.notifyRemoteTrace(event);
      }
      catch (RuntimeException ex)
      {
        IOUtil.print(ex);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public class Event extends EventObject
  {
    private static final long serialVersionUID = 1L;

    private long id;

    long timeStamp;

    String agentID;

    String bundleID;

    String tracerName;

    String context;

    String message;

    String throwable;

    StackTraceElement[] stackTrace;

    Event()
    {
      super(RemoteTraceServer.this);
      id = ++lastEventID;
    }

    public RemoteTraceServer getRemoteTraceServer()
    {
      return (RemoteTraceServer)source;
    }

    public long getID()
    {
      return id;
    }

    public long getTimeStamp()
    {
      return timeStamp;
    }

    public String getAgentID()
    {
      return agentID;
    }

    public String getBundleID()
    {
      return bundleID;
    }

    public String getContext()
    {
      return context;
    }

    public String getMessage()
    {
      return message;
    }

    public StackTraceElement[] getStackTrace()
    {
      return stackTrace;
    }

    public String getThrowable()
    {
      return throwable;
    }

    public String getTracerName()
    {
      return tracerName;
    }

    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append("TraceEvent[agentID=");
      builder.append(agentID);

      builder.append(", bundleID=");
      builder.append(bundleID);

      builder.append(", tracerName=");
      builder.append(tracerName);

      builder.append(", context=");
      builder.append(context);

      builder.append(", message=");
      builder.append(message);

      builder.append(", throwable=");
      builder.append(throwable);

      builder.append(", stackTrace=");
      builder.append(stackTrace);

      builder.append("]");
      return builder.toString();
    }

    public String getText(int index)
    {
      switch (index)
      {
      case 0:
        return Long.toString(id);
      case 1:
        return new Date(timeStamp).toString();
      case 2:
        return agentID;
      case 3:
        return bundleID;
      case 4:
        return tracerName;
      case 5:
        return context;
      case 6:
        return message;
      case 7:
        return throwable;
      }

      throw new IllegalArgumentException("Invalid index: " + index);
    }

    public boolean hasError()
    {
      return throwable != null && throwable.length() != 0 //
          || stackTrace != null && stackTrace.length != 0;
    }

  }

  /**
   * @author Eike Stepper
   */
  public interface Listener
  {
    public void notifyRemoteTrace(Event event);
  }

  public static class PrintListener implements Listener
  {
    public static final PrintListener CONSOLE = new PrintListener();

    private PrintStream stream;

    public PrintListener(PrintStream stream)
    {
      this.stream = stream;
    }

    protected PrintListener()
    {
      this(IOUtil.OUT());
    }

    public void notifyRemoteTrace(Event event)
    {
      stream.println("[TRACE] " + event.getAgentID());
      stream.println(event.getBundleID());
      stream.println(event.getTracerName());
      stream.println(event.getContext());
      stream.println(event.getMessage());

      String throwable = event.getThrowable();
      if (throwable != null && throwable.length() != 0)
      {
        stream.println(throwable);
      }

      StackTraceElement[] stackTrace = event.getStackTrace();
      if (stackTrace != null) for (StackTraceElement element : stackTrace)
      {
        stream.print(element.getClassName());
        stream.print("." + element.getMethodName());
        stream.print("(" + element.getFileName());
        stream.print(":" + element.getLineNumber());
        stream.println(")");
      }

      stream.println();
    }
  }
}
