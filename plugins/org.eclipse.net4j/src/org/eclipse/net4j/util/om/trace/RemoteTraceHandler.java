/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util.om.trace;

import org.eclipse.net4j.util.IOUtil;
import org.eclipse.net4j.util.ReflectUtil;
import org.eclipse.net4j.util.om.OMTraceHandler;
import org.eclipse.net4j.util.om.OMTracer;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.UUID;

/**
 * @author Eike Stepper
 */
public class RemoteTraceHandler implements OMTraceHandler
{
  public static final String DEFAULT_HOST = "localhost"; //$NON-NLS-1$

  public static final int DEFAULT_PORT = RemoteTraceServer.DEFAULT_PORT;

  private static int uniqueCounter;

  private String agentID;

  private String host;

  private int port;

  private Socket socket;

  public RemoteTraceHandler() throws IOException
  {
    this(uniqueAgentID());
  }

  public RemoteTraceHandler(String agentID) throws IOException
  {
    this(agentID, DEFAULT_HOST);
  }

  public RemoteTraceHandler(String agentID, String host) throws IOException
  {
    this(agentID, host, DEFAULT_PORT);
  }

  public RemoteTraceHandler(String agentID, String host, int port) throws IOException
  {
    this.agentID = agentID;
    this.host = host;
    this.port = port;
    socket = connect();
  }

  public Exception close()
  {
    try
    {
      socket.close();
      return null;
    }
    catch (IOException ex)
    {
      return ex;
    }
  }

  public void traced(OMTracer tracer, Class context, Object instance, String msg, Throwable t)
  {
    try
    {
      OutputStream outputStream = socket.getOutputStream();
      DataOutputStream out = new DataOutputStream(outputStream);

      writeUTF(out, agentID);
      writeUTF(out, tracer.getBundle().getBundleID());
      writeUTF(out, tracer.getFullName());
      writeUTF(out, context);
      writeUTF(out, ReflectUtil.getLabel(instance));
      writeUTF(out, msg);
      if (t == null)
      {
        out.writeBoolean(false);
      }
      else
      {
        out.writeBoolean(true);
        String message = t.getMessage();
        writeUTF(out, message);

        StackTraceElement[] stackTrace = t.getStackTrace();
        int size = stackTrace == null ? 0 : stackTrace.length;
        out.writeInt(size);

        for (int i = 0; i < size; i++)
        {
          StackTraceElement element = stackTrace[i];
          writeUTF(out, element.getClassName());
          writeUTF(out, element.getMethodName());
          writeUTF(out, element.getFileName());
          out.writeInt(element.getLineNumber());
        }
      }

      out.flush();
    }
    catch (IOException ex)
    {
      IOUtil.print(ex);
    }
  }

  protected Socket connect() throws IOException
  {
    return new Socket(host, port);
  }

  protected void writeUTF(DataOutputStream out, String str) throws IOException
  {
    out.writeUTF(str == null ? "" : str);
  }

  private void writeUTF(DataOutputStream out, Object object) throws IOException
  {
    writeUTF(out, object == null ? "" : object.toString());
  }

  private void writeUTF(DataOutputStream out, Class clazz) throws IOException
  {
    writeUTF(out, clazz == null ? "" : clazz.getName());
  }

  public static String uniqueAgentID()
  {
    try
    {
      InetAddress localMachine = InetAddress.getLocalHost();
      return localMachine.getHostName() + "#" + (++uniqueCounter);
    }
    catch (Exception ex)
    {
      UUID uuid = UUID.randomUUID();
      return uuid.toString();
    }
  }
}
