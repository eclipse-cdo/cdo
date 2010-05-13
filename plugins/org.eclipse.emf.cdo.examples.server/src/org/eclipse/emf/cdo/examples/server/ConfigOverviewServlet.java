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

import org.eclipse.emf.cdo.examples.server.DemoConfiguration.Mode;

import templates.ConfigOverview;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Eike Stepper
 */
public class ConfigOverviewServlet extends AbstractTemplateServlet<DemoConfiguration>
{
  private static final long serialVersionUID = 1L;

  public ConfigOverviewServlet() throws InstantiationException, IllegalAccessException, SecurityException,
      NoSuchMethodException
  {
    super(ConfigOverview.class);
  }

  @Override
  protected DemoConfiguration createTemplateArgument(HttpServletRequest req)
  {
    String name = req.getParameter("name");
    if (name != null)
    {
      return getDemoConfiguration(name);
    }

    String mode = req.getParameter("mode");
    String userIDs = req.getParameter("userIDs");
    return createDemoConfiguration(mode, userIDs);
  }

  protected DemoConfiguration createDemoConfiguration(String mode, String userIDs)
  {
    DemoConfiguration config = new DemoConfiguration();
    config.setMode(Mode.valueOf(mode));
    if (userIDs != null)
    {
      config.setUserIDs(userIDs.split(","));
    }

    config.activate();
    DemoServer.INSTANCE.getConfigs().put(config.getName(), config);
    return config;
  }

  protected DemoConfiguration getDemoConfiguration(String name)
  {
    DemoConfiguration config = DemoServer.INSTANCE.getConfigs().get(name);
    if (config == null)
    {
      throw new IllegalStateException("No demo configuration available for " + name);
    }

    return config;
  }
}
