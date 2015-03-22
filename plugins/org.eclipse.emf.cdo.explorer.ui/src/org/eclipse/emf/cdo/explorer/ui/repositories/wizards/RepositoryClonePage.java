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

import org.eclipse.emf.cdo.internal.explorer.repositories.CloneCDORepository;
import org.eclipse.emf.cdo.server.IRepositorySynchronizer;

import org.eclipse.net4j.util.StringUtil;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import java.util.Properties;

/**
 * @author Eike Stepper
 */
public class RepositoryClonePage extends AbstractRepositoryPage
{
  private MasterRepositoryController controller;

  private Text reconnectSecondsText;

  private Text recommitSecondsText;

  private Text recommitAttemptsText;

  public RepositoryClonePage()
  {
    super("clone", "Clone Repository 1");
    setTitle("New Clone Repository");
    setMessage("Enter the label and the connection parameters of the new remote location.");
  }

  @Override
  protected void fillPage(Composite container)
  {
    // controller = new MasterRepositoryController(container)
    // {
    // @Override
    // protected void validateController()
    // {
    // super.validateController();
    // validate();
    // }
    // };

    createLabel(container, "Reconnect seconds:");
    reconnectSecondsText = createText(container, 50);
    reconnectSecondsText.setText(Integer.toString(IRepositorySynchronizer.DEFAULT_RETRY_INTERVAL));
    reconnectSecondsText.addModifyListener(this);

    createLabel(container, "Recommit seconds:");
    recommitSecondsText = createText(container, 50);
    recommitSecondsText.setText(Integer.toString(IRepositorySynchronizer.DEFAULT_RECOMMIT_INTERVAL));
    recommitSecondsText.addModifyListener(this);

    createLabel(container, "Recommit attempts:");
    recommitAttemptsText = createText(container, 50);
    recommitAttemptsText.setText(Integer.toString(IRepositorySynchronizer.DEFAULT_MAX_RECOMMITS));
    recommitAttemptsText.addModifyListener(this);
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

    String reconnectSeconds = reconnectSecondsText.getText();

    try
    {
      int value = Integer.parseInt(reconnectSeconds);
      if (value < 0)
      {
        throw new Exception();
      }
    }
    catch (Exception ex)
    {
      throw new Exception("Invalid reconnect seconds.");
    }

    String recommitSeconds = recommitSecondsText.getText();

    try
    {
      int value = Integer.parseInt(recommitSeconds);
      if (value < 0)
      {
        throw new Exception();
      }
    }
    catch (Exception ex)
    {
      throw new Exception("Invalid recommit seconds.");
    }

    String recommitAttempts = recommitAttemptsText.getText();

    try
    {
      int value = Integer.parseInt(recommitAttempts);
      if (value < 0)
      {
        throw new Exception();
      }
    }
    catch (Exception ex)
    {
      throw new Exception("Invalid recommit attempts.");
    }

    properties.setProperty(CloneCDORepository.PROP_CONNECTOR_TYPE, "tcp");
    properties.setProperty(CloneCDORepository.PROP_CONNECTOR_DESCRIPTION, connectorDescription);
    properties.setProperty(CloneCDORepository.PROP_NAME, repositoryName);
    properties.setProperty(CloneCDORepository.PROP_RECONNECT_SECONDS, reconnectSeconds);
    properties.setProperty(CloneCDORepository.PROP_RECOMMIT_SECONDS, recommitSeconds);
    properties.setProperty(CloneCDORepository.PROP_RECOMMIT_ATTEMPTS, recommitAttempts);
  }
}
