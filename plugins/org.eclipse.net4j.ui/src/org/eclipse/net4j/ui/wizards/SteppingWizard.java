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
package org.eclipse.net4j.ui.wizards;

import org.eclipse.net4j.internal.ui.bundle.Net4jUI;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public abstract class SteppingWizard extends Wizard
{
  private Map<String, Object> context;

  private Step rootStep;

  private Step currentStep;

  public SteppingWizard(Map<String, Object> context)
  {
    this.context = context;
  }

  public SteppingWizard()
  {
    this(new HashMap());
  }

  @Override
  public void dispose()
  {
    super.dispose();
  }

  public final Map<String, Object> getContext()
  {
    return context;
  }

  public final boolean hasContextValue(String key)
  {
    return getContext().containsKey(key);
  }

  public final Object getSingleContextValue(String key)
  {
    Object value = getContextValue(key);
    if (value instanceof Collection)
    {
      Collection collection = (Collection)value;
      if (collection.isEmpty())
      {
        return null;
      }
      else
      {
        return collection.iterator().next();
      }
    }

    return value;
  }

  public final Object getContextValue(String key)
  {
    return getContext().get(key);
  }

  public final Object setSingleContextValue(String key, Object value)
  {
    return setContextValue(key, Collections.singletonList(value));
  }

  public final Object setContextValue(String key, Object value)
  {
    Object old = getContext().put(key, value);
    if (rootStep != null)
    {
      for (IWizardPage page : getPages())
      {
        if (page instanceof SteppingWizardPage)
        {
          ((SteppingWizardPage)page).validate();
        }
      }
    }

    return old;
  }

  public Step getRootStep()
  {
    return rootStep;
  }

  public Step getCurrentStep()
  {
    return currentStep;
  }

  public Step getPreviousStep()
  {
    return currentStep.getPrevious();
  }

  public Step getNextStep()
  {
    return currentStep.getNext();
  }

  public PagingStrategy getPagingStrategy()
  {
    return new PagingStrategy();
  }

  @Override
  public final void addPages()
  {
    rootStep = createRootStep();
    rootStep.setWizard(this);

    PagingStrategy pagingStrategy = getPagingStrategy();
    rootStep.accept(pagingStrategy);
    List<SteppingWizardPage> pages = pagingStrategy.getPages();
    for (SteppingWizardPage wizardPage : pages)
    {
      addPage(wizardPage);
    }
  }

  @Override
  public final boolean performFinish()
  {
    try
    {
      if (isLongRunningFinish())
      {
        new Job("")
        {
          @Override
          protected IStatus run(IProgressMonitor monitor)
          {
            try
            {
              doFinish(monitor);
            }
            catch (Exception ex)
            {
              handleFinishException(ex);
            }

            return Status.OK_STATUS;
          }

        }.schedule();
        return true;
      }

      doFinish(new NullProgressMonitor());
      return true;
    }
    catch (Exception ex)
    {
      handleFinishException(ex);
      return false;
    }
  }

  protected boolean isLongRunningFinish()
  {
    return false;
  }

  protected abstract void doFinish(IProgressMonitor monitor) throws Exception;

  protected abstract Step createRootStep();

  private void handleFinishException(Exception ex)
  {
    Net4jUI.LOG.error(ex);
    MessageDialog.openError(getShell(), getWindowTitle(), ex.getMessage());
  }
}
