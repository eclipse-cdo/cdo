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

import org.eclipse.net4j.internal.ui.wizards.steps.NewAcceptorStep;
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
public class NewAcceptorWizard extends SteppingNewWizard
{
  public NewAcceptorWizard(Map<String, Object> context)
  {
    super(context);
  }

  public NewAcceptorWizard(String type, String description)
  {
    super(createContext(type, description));
  }

  public NewAcceptorWizard()
  {
  }

  @Override
  protected Step createRootStep()
  {
    return new NewAcceptorStep(getTransportContainer());
  }

  @Override
  protected void doFinish(IProgressMonitor monitor) throws Exception
  {
    NewAcceptorStep step = (NewAcceptorStep)getRootStep();
    ITransportContainer transportContainer = getTransportContainer();
    transportContainer.getAcceptor(step.getAcceptorType(), step.getAcceptorDescription());
  }

  protected ITransportContainer getTransportContainer()
  {
    return IPluginTransportContainer.INSTANCE;
  }

  private static Map<String, Object> createContext(String type, String description)
  {
    Map<String, Object> context = new HashMap();
    if (type != null)
    {
      context.put(NewAcceptorStep.KEY_TYPE, type);
    }

    if (description != null)
    {
      context.put(NewAcceptorStep.KEY_DESCRIPTION, description);
    }

    return context;
  }
}
