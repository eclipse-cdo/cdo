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
package org.eclipse.net4j.internal.ui.wizards;

import org.eclipse.net4j.internal.ui.wizards.steps.NewConnectorStep;
import org.eclipse.net4j.transport.IPluginTransportContainer;
import org.eclipse.net4j.transport.ITransportContainer;
import org.eclipse.net4j.ui.wizards.Step;
import org.eclipse.net4j.ui.wizards.SteppingNewWizard;

import org.eclipse.core.runtime.IProgressMonitor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class NewConnectorWizard extends SteppingNewWizard
{
  public NewConnectorWizard(Map<String, Object> context)
  {
    super(context);
  }

  public NewConnectorWizard(String connectorType, String description)
  {
    super(createContext(connectorType, description));
  }

  public NewConnectorWizard()
  {
  }

  @Override
  protected Step createRootStep()
  {
    return new NewConnectorStep(getTransportContainer());
  }

  @Override
  protected void doFinish(IProgressMonitor monitor) throws Exception
  {
    NewConnectorStep step = (NewConnectorStep)getRootStep();
    ITransportContainer transportContainer = getTransportContainer();
    transportContainer.getConnector(step.getConnectorType(), step.getConnectorDescription());
  }

  protected ITransportContainer getTransportContainer()
  {
    return IPluginTransportContainer.INSTANCE;
  }

  private static Map<String, Object> createContext(String connectorType, String description)
  {
    Map<String, Object> context = new HashMap();
    if (connectorType != null)
    {
      context.put(NewConnectorStep.KEY_TYPE, connectorType);
    }

    if (description != null)
    {
      context.put(NewConnectorStep.KEY_DESCRIPTION, description);
    }

    return context;
  }
}
