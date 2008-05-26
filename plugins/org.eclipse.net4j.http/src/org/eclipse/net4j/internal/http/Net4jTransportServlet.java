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
package org.eclipse.net4j.internal.http;

import org.eclipse.net4j.buffer.IBufferHandler;
import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.http.IHTTPConnector;
import org.eclipse.net4j.http.INet4jTransportServlet;
import org.eclipse.net4j.internal.http.bundle.OM;
import org.eclipse.net4j.protocol.IProtocol;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import org.eclipse.internal.net4j.acceptor.AcceptorFactory;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Eike Stepper
 */
public class Net4jTransportServlet extends HttpServlet implements INet4jTransportServlet
{
  private static final long serialVersionUID = 1L;

  private RequestHandler requestHandler;

  public Net4jTransportServlet()
  {
  }

  public RequestHandler getRequestHandler()
  {
    return requestHandler;
  }

  public void setRequestHandler(RequestHandler requestHandler)
  {
    this.requestHandler = requestHandler;
  }

  @Override
  protected final void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
  {
    doPost(req, resp);
  }

  @Override
  protected final void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
  {
    doRequest(req, resp);
  }

  protected void doRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
  {
    if (requestHandler == null)
    {
      throw new ServletException("No request handler installed");
    }

    String connectorID = req.getParameter("list");
    if (connectorID != null)
    {
      doList(connectorID, resp);
      return;
    }

    ServletInputStream servletInputStream = req.getInputStream();
    ExtendedDataInputStream in = new ExtendedDataInputStream(servletInputStream);

    ServletOutputStream servletOutputStream = resp.getOutputStream();
    ExtendedDataOutputStream out = new ExtendedDataOutputStream(servletOutputStream);

    int opcode = servletInputStream.read();
    switch (opcode)
    {
    case OPCODE_CONNECT:
      doConnect(in, out);
      break;

    case OPCODE_DISCONNECT:
      doDisconnect(in, out);
      break;

    case OPCODE_OPEN_CHANNEL:
      doOpenChannel(in, out);
      break;

    case OPCODE_BUFFERS:
      doBuffers(in, out);
      break;

    default:
      throw new IOException("Invalid opcaode: " + opcode);
    }

    out.flush();
  }

  protected void doList(String connectorID, HttpServletResponse resp) throws IOException
  {
    IHTTPConnector[] connectors = requestHandler.handleList(connectorID);
    PrintWriter writer = resp.getWriter();
    for (IHTTPConnector connector : connectors)
    {
      // TODO Security: Hide connectorID!
      writer.write(connector.getConnectorID());
      writer.write(":");

      String userID = connector.getUserID();
      if (userID != null)
      {
        writer.write(" userID=" + userID);
      }

      if (connector instanceof HTTPServerConnector)
      {
        long idleTime = System.currentTimeMillis() - ((HTTPServerConnector)connector).getLastTraffic();
        writer.write(" idleTime=" + idleTime);
      }

      writer.write("\n");

      for (IChannel channel : connector.getChannels())
      {
        writer.write("    ");
        writer.write(String.valueOf(channel.getChannelIndex()));
        writer.write(": ");
        IBufferHandler receiveHandler = channel.getReceiveHandler();
        if (receiveHandler instanceof IProtocol)
        {
          writer.write(((IProtocol)receiveHandler).getType());
        }
        else
        {
          String string = receiveHandler.toString();
          if (string.length() > 256)
          {
            string = string.substring(0, 256);
          }

          writer.write(string);
        }

        writer.write(" (");
        writer.write(String.valueOf(channel.getChannelID()));
        writer.write(")\n");
      }
    }
  }

  protected void doConnect(ExtendedDataInputStream in, ExtendedDataOutputStream out) throws ServletException,
      IOException
  {
    try
    {
      String userID = in.readString();
      IHTTPConnector connector = requestHandler.handleConnect(userID);
      out.writeString(connector.getConnectorID());
      out.writeInt(connector.getMaxIdleTime());
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
      out.writeString(null);
    }
  }

  protected void doDisconnect(ExtendedDataInputStream in, ExtendedDataOutputStream out) throws ServletException,
      IOException
  {
    try
    {
      String connectorID = in.readString();
      requestHandler.handleDisonnect(connectorID);
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
    }
  }

  protected void doOpenChannel(ExtendedDataInputStream in, ExtendedDataOutputStream out) throws ServletException,
      IOException
  {
    try
    {
      String connectorID = in.readString();
      int channelID = in.readInt();
      short channelIndex = in.readShort();
      String protocolType = in.readString();
      requestHandler.handleOpenChannel(connectorID, channelIndex, channelID, protocolType);
      out.writeBoolean(true);
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
      out.writeBoolean(false);
    }
  }

  protected void doBuffers(ExtendedDataInputStream in, ExtendedDataOutputStream out) throws ServletException,
      IOException
  {
    String connectorID = in.readString();
    requestHandler.handleBuffers(connectorID, in, out);
  }

  /**
   * @author Eike Stepper
   */
  public static class ContainerAware extends Net4jTransportServlet
  {
    private static final String ACCEPTORS_GROUP = AcceptorFactory.PRODUCT_GROUP;

    private static final String HTTP_TYPE = HTTPAcceptorFactory.TYPE;

    private static final long serialVersionUID = 1L;

    private HTTPAcceptor acceptor;

    public ContainerAware()
    {
    }

    @Override
    public void init() throws ServletException
    {
      super.init();

      acceptor = (HTTPAcceptor)IPluginContainer.INSTANCE.getElement(ACCEPTORS_GROUP, HTTP_TYPE, null);
      if (acceptor == null)
      {
        throw new ServletException("Acceptor not found");
      }

      acceptor.setServlet(this);
      setRequestHandler(acceptor);
    }

    @Override
    public void destroy()
    {
      setRequestHandler(null);
      acceptor.setServlet(null);
      acceptor = null;
      super.destroy();
    }
  }
}
