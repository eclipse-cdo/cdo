/*
 * Copyright (c) 2008, 2009, 2011-2013, 2015, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.http.tests;

import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.io.IOUtil;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class Net4jEchoTestServlet extends HttpServlet
{
  private static final long serialVersionUID = 1L;

  public Net4jEchoTestServlet()
  {
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
    ServletInputStream servletInputStream = req.getInputStream();
    ExtendedDataInputStream in = new ExtendedDataInputStream(servletInputStream);

    ServletOutputStream servletOutputStream = resp.getOutputStream();
    ExtendedDataOutputStream out = new ExtendedDataOutputStream(servletOutputStream);

    long lastTime = System.currentTimeMillis();
    int count = in.readByte();
    out.writeInt(count);
    for (int i = 0; i < count; i++)
    {
      byte b = in.readByte();
      long now = System.currentTimeMillis();
      long gap = now - lastTime;
      lastTime = now;
      IOUtil.OUT().println("Gap: " + gap); //$NON-NLS-1$

      out.writeByte(b);
      out.writeLong(gap);
      out.flush();
    }
  }
}
