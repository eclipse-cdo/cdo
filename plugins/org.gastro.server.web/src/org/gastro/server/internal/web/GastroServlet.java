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
package org.gastro.server.internal.web;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.net4j.CDOSession;
import org.eclipse.emf.cdo.net4j.CDOSessionConfiguration;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.io.IOUtil;

import org.apache.commons.lang.StringEscapeUtils;
import org.gastro.business.BusinessDay;
import org.gastro.inventory.MenuCard;
import org.gastro.server.GastroServer;

import templates.MenuCardTemplate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * @author Eike Stepper
 */
public class GastroServlet extends HttpServlet
{
  private static final long serialVersionUID = 1L;

  private String restaurantName;

  private IAcceptor acceptor;

  private IConnector connector;

  private CDOView view;

  private MenuCard menuCard;

  private MenuCardTemplate template = MenuCardTemplate.create(StringUtil.NL);

  public GastroServlet()
  {
  }

  @Override
  public void init() throws ServletException
  {
    System.out.println("INIT GastroServlet");
    String repositoryName = GastroServer.getRepository().getName();
    restaurantName = getRestaurantName();

    acceptor = (IAcceptor)IPluginContainer.INSTANCE.getElement("org.eclipse.net4j.acceptors", "jvm", repositoryName);
    connector = Net4jUtil.getConnector(IPluginContainer.INSTANCE, "jvm", repositoryName);

    CDOSessionConfiguration config = CDONet4jUtil.createSessionConfiguration();
    config.setConnector(connector);
    config.setRepositoryName(repositoryName);

    CDOSession session = config.openSession();
    view = session.openView();
    super.init();
  }

  @Override
  public void destroy()
  {
    System.out.println("DESTROY GastroServlet");
    if (view != null)
    {
      CDOSession session = (CDOSession)view.getSession();
      if (session != null)
      {
        session.close();
      }
    }

    if (connector != null)
    {
      connector.close();
    }

    if (acceptor != null)
    {
      acceptor.close();
    }

    super.destroy();
  }

  public synchronized MenuCard getMenuCard()
  {
    if (menuCard == null)
    {
      SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
      String path = restaurantName + "/" + formatter.format(new Date());
      CDOResource resource = view.getResource(path);
      BusinessDay businessDay = (BusinessDay)resource.getContents().get(0);
      menuCard = businessDay.getMenuCard();
    }

    return menuCard;
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
  {
    doPost(req, resp);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
  {
    PrintWriter writer = resp.getWriter();

    try
    {
      String html = template.generate(getMenuCard());
      writer.print(html);
    }
    finally
    {
      IOUtil.close(writer);
    }
  }

  public static String getRestaurantName() throws ServletException
  {
    try
    {
      String configName = System.getProperty("servlet.config", "gastro.properties");
      InputStream fis = new FileInputStream(configName);

      try
      {
        Properties properties = new Properties();
        properties.load(fis);
        return properties.getProperty("restaurant");
      }
      finally
      {
        fis.close();
      }
    }
    catch (IOException ex)
    {
      throw new ServletException(ex);
    }
  }

  public static String html(String value)
  {
    return StringEscapeUtils.escapeHtml(value);
  }

  public static String html(double value)
  {
    return html(NumberFormat.getCurrencyInstance().format(value));
  }
}
