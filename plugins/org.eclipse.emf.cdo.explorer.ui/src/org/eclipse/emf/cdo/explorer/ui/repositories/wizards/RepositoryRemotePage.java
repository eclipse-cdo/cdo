/*
 * Copyright (c) 2015, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.repositories.wizards;

import org.eclipse.emf.cdo.explorer.repositories.CDORepository.IDGeneration;
import org.eclipse.emf.cdo.explorer.repositories.CDORepository.VersioningMode;
import org.eclipse.emf.cdo.explorer.ui.checkouts.wizards.CheckoutWizardPage.ValidationProblem;
import org.eclipse.emf.cdo.internal.explorer.repositories.RemoteCDORepository;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.security.IPasswordCredentials;
import org.eclipse.net4j.util.ui.UIUtil;

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
    super("remote", "Remote Repository");
    setTitle("New Remote Repository");
    setMessage("Enter label and connection parameters of the new remote location.");
  }

  @Override
  public IPasswordCredentials getCredentials()
  {
    return controller.getCredentials();
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

        if (controller != null)
        {
          validate();
        }
        else
        {
          UIUtil.getDisplay().asyncExec(new Runnable()
          {
            @Override
            public void run()
            {
              validate();
            }
          });
        }
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
      throw new ValidationProblem("Host or port are invalid.");
    }

    String repositoryName = controller.getRepositoryName();
    if (StringUtil.isEmpty(repositoryName))
    {
      throw new ValidationProblem("Repository name is empty.");
    }

    properties.setProperty(RemoteCDORepository.PROP_CONNECTOR_TYPE, "tcp");
    properties.setProperty(RemoteCDORepository.PROP_CONNECTOR_DESCRIPTION, connectorDescription);
    properties.setProperty(RemoteCDORepository.PROP_AUTHENTICATING, Boolean.toString(controller.isAuthenticating()));
    properties.setProperty(RemoteCDORepository.PROP_NAME, repositoryName);

    VersioningMode versioningMode = controller.getVersioningMode();
    if (versioningMode != null)
    {
      properties.setProperty(RemoteCDORepository.PROP_VERSIONING_MODE, versioningMode.toString());
    }

    IDGeneration idGeneration = controller.getIDGeneration();
    if (idGeneration != null)
    {
      properties.setProperty(RemoteCDORepository.PROP_ID_GENERATION, idGeneration.toString());
    }
  }
}
