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

    String repositoryName = controller.getRepositoryName();
    if (StringUtil.isEmpty(repositoryName))
    {
      throw new Exception("Repository name is empty.");
    }

    properties.put("connectorType", "tcp");
    properties.put("connectorDescription", connectorDescription);
    properties.put("repositoryName", repositoryName);
  }
}
