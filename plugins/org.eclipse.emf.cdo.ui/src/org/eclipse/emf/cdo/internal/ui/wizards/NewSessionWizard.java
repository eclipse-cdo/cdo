/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.ui.wizards;

import org.eclipse.emf.cdo.internal.ui.wizards.steps.RepoNameStep;
import org.eclipse.emf.cdo.protocol.CDOProtocolConstants;

import org.eclipse.net4j.internal.ui.wizards.steps.ProvideConnectorStep;
import org.eclipse.net4j.internal.ui.wizards.steps.SelectConnectorStep;
import org.eclipse.net4j.transport.IConnector;
import org.eclipse.net4j.transport.IPluginTransportContainer;
import org.eclipse.net4j.transport.ITransportContainer;
import org.eclipse.net4j.ui.wizards.ParallelStep;
import org.eclipse.net4j.ui.wizards.Step;
import org.eclipse.net4j.ui.wizards.SteppingNewWizard;

import org.eclipse.emf.internal.cdo.CDOSessionFactory;

import org.eclipse.core.runtime.IProgressMonitor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class NewSessionWizard extends SteppingNewWizard
{
  private ProvideConnectorStep connectorStep;

  private RepoNameStep repoNameStep;

  public NewSessionWizard(Map<String, Object> context)
  {
    super(context);
  }

  public NewSessionWizard(IConnector connector, String repoName)
  {
    super(createContext(connector, repoName));
  }

  public NewSessionWizard()
  {
  }

  @Override
  protected Step createRootStep()
  {
    ParallelStep root = new ParallelStep();
    root.add(connectorStep = new ProvideConnectorStep(getTransportContainer()));
    root.add(repoNameStep = new RepoNameStep());
    return root;
  }

  @Override
  protected void doFinish(IProgressMonitor monitor) throws Exception
  {
    IConnector connector = connectorStep.getConnector();
    if (connector == null)
    {
      throw new IllegalStateException("connector == null");
    }

    String repoName = repoNameStep.getRepoName();
    if (repoName == null)
    {
      throw new IllegalStateException("repoName == null");
    }

    ITransportContainer transportContainer = getTransportContainer();
    String[] key = transportContainer.getElementKey(connector);

    String description = key[1] + "://" + key[2] + "/" + repoName;
    transportContainer.getElement(CDOSessionFactory.SESSION_GROUP, CDOProtocolConstants.PROTOCOL_NAME, description);
  }

  protected ITransportContainer getTransportContainer()
  {
    return IPluginTransportContainer.INSTANCE;
  }

  private static Map<String, Object> createContext(IConnector connector, String repoName)
  {
    Map<String, Object> context = new HashMap();
    if (connector != null)
    {
      context.put(SelectConnectorStep.KEY_CONNECTOR, Collections.singleton(connector));
    }

    if (repoName != null)
    {
      context.put(RepoNameStep.KEY_REPO_NAME, repoName);
    }

    return context;
  }
}
