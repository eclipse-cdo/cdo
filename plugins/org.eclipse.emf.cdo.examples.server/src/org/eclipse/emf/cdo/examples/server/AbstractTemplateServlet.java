/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *
 *  Initial Publication:
 *    Eclipse Magazin - http://www.eclipse-magazin.de
 */
package org.eclipse.emf.cdo.examples.server;

import org.eclipse.net4j.util.io.IOUtil;

import org.apache.commons.lang.StringEscapeUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.NumberFormat;

/**
 * @author Eike Stepper
 */
public abstract class AbstractTemplateServlet<ARGUMENT> extends HttpServlet
{
  private static final long serialVersionUID = 1L;

  private Object template;

  private Method method;

  public AbstractTemplateServlet(Class<?> templateClass) throws InstantiationException, IllegalAccessException,
      SecurityException, NoSuchMethodException
  {
    method = templateClass.getMethod("generate", Object.class);
    template = templateClass.newInstance();
  }

  @Override
  public final void init() throws ServletException
  {
    System.out.println("INIT " + getClass().getSimpleName());
    doInit();
    super.init();
  }

  @Override
  public final void destroy()
  {
    System.out.println("DESTROY " + getClass().getSimpleName());
    doDestroy();
    super.destroy();
  }

  @Override
  protected final void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
  {
    doPost(req, resp);
  }

  @Override
  protected final void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
  {
    PrintWriter writer = resp.getWriter();

    try
    {
      ARGUMENT argument = createTemplateArgument(req);
      String html = (String)method.invoke(template, argument);
      writer.print(html);
    }
    catch (IllegalArgumentException ex)
    {
      throw new ServletException(ex);
    }
    catch (IllegalAccessException ex)
    {
      throw new ServletException(ex);
    }
    catch (InvocationTargetException ex)
    {
      throw new ServletException(ex);
    }
    finally
    {
      IOUtil.close(writer);
    }
  }

  protected void doInit()
  {
  }

  protected void doDestroy()
  {
  }

  protected abstract ARGUMENT createTemplateArgument(HttpServletRequest req);

  public static String html(String value)
  {
    return StringEscapeUtils.escapeHtml(value);
  }

  public static String html(double value)
  {
    return html(NumberFormat.getCurrencyInstance().format(value));
  }
}
