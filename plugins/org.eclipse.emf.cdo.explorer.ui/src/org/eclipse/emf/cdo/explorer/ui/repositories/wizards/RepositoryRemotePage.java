/*
 * Copyright (c) 2004-2014 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.repositories.wizards;

import org.eclipse.emf.cdo.internal.explorer.repositories.RemoteCDORepository;

import org.eclipse.net4j.util.StringUtil;

import org.eclipse.swt.widgets.Composite;

import java.util.Properties;

/**
 * @author Eike Stepper
 */
public class RepositoryRemotePage extends AbstractRepositoryPage
{
  private MasterRepositoryController controller;

  public RepositoryRemotePage()
  {
    super("remote", "Remote Repository 1");
    setTitle("New Remote Repository");
    setMessage("Enter the label and the connection parameters of the new remote location.");
  }

  @Override
  protected void fillPage(Composite container)
  {
    controller = new MasterRepositoryController(container)
    {
      @Override
      protected void validateController()
      {
        super.validateController();
        validate();
      }
    };
  }

  @Override
  protected void doValidate(Properties properties) throws Exception
  {
    super.doValidate(properties);

    String connectorDescription = controller.getConnectorDescription();
    if (StringUtil.isEmpty(connectorDescription))
    {
      // TODO Port could be empty/invalid!
      throw new Exception("Host is empty.");
    }

    String name = controller.getName();
    if (StringUtil.isEmpty(name))
    {
      throw new Exception("Name is empty.");
    }

    properties.setProperty(RemoteCDORepository.PROP_CONNECTOR_TYPE, "tcp");
    properties.setProperty(RemoteCDORepository.PROP_CONNECTOR_DESCRIPTION, connectorDescription);
    properties.setProperty(RemoteCDORepository.PROP_NAME, name);
  }
}
