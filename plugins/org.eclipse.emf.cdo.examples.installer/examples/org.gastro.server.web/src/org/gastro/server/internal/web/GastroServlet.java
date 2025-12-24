/*
 * Copyright (c) 2009-2012, 2015, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *
 *  Initial Publication:
 *    Eclipse Magazin - http://www.eclipse-magazin.de
 */
package org.gastro.server.internal.web;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.net4j.CDONet4jSessionConfiguration;
import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.om.OMPlatform;

import org.apache.commons.text.StringEscapeUtils;
import org.gastro.business.BusinessDay;
import org.gastro.inventory.MenuCard;
import org.gastro.server.GastroServer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import templates.MenuCardTemplate;

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

  private IRepository repository;

  public GastroServlet()
  {
  }

  @Override
  public void init() throws ServletException
  {
    repository = GastroServer.getRepository();
    if (repository == null)
    {
      return;
    }

    OM.LOG.info("Gastro servlet initializing");
    String repositoryName = repository.getName();
    restaurantName = getRestaurantName();

    acceptor = Net4jUtil.getAcceptor(IPluginContainer.INSTANCE, "jvm", repositoryName);
    connector = Net4jUtil.getConnector(IPluginContainer.INSTANCE, "jvm", repositoryName);

    CDONet4jSessionConfiguration config = CDONet4jUtil.createNet4jSessionConfiguration();
    config.setConnector(connector);
    config.setRepositoryName(repositoryName);

    CDONet4jSession session = config.openNet4jSession();
    view = session.openView();
    super.init();
    OM.LOG.info("Gastro servlet initialized");
  }

  @Override
  public void destroy()
  {
    if (repository == null)
    {
      return;
    }

    OM.LOG.info("Gastro servlet destroying");
    if (view != null)
    {
      CDONet4jSession session = (CDONet4jSession)view.getSession();
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
    OM.LOG.info("Gastro servlet destroyed");
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
      String configPath = OMPlatform.INSTANCE.getProperty("servlet.config", "config/gastro.properties");
      // InputStream stream = new FileInputStream(configPath);
      InputStream stream = OM.BUNDLE.getInputStream(configPath);

      try
      {
        Properties properties = new Properties();
        properties.load(stream);
        return properties.getProperty("restaurant");
      }
      finally
      {
        stream.close();
      }
    }
    catch (IOException ex)
    {
      throw new ServletException(ex);
    }
  }

  public static String html(String value)
  {
    return StringEscapeUtils.escapeHtml3(value);
  }

  public static String html(double value)
  {
    return html(NumberFormat.getCurrencyInstance().format(value));
  }
}
