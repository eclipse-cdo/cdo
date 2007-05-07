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

import java.util.Map;

/**
 * @author Eike Stepper
 */
public abstract class Step
{
  private String label;

  private CompoundStep parent;

  private SteppingWizard wizard;

  private SteppingWizardPage wizardPage;

  public Step()
  {
  }

  public Step(String label)
  {
    this.label = label;
  }

  public String getLabel()
  {
    return label;
  }

  public final CompoundStep getParent()
  {
    return parent;
  }

  public final SteppingWizard getWizard()
  {
    return wizard;
  }

  public SteppingWizardPage getWizardPage()
  {
    return wizardPage;
  }

  public final Map<String, Object> getContext()
  {
    return wizard.getContext();
  }

  public final Object getContextValue(String key)
  {
    return wizard.getContextValue(key);
  }

  public final boolean hasContextValue(String key)
  {
    return wizard.hasContextValue(key);
  }

  public final Object setContextValue(String key, Object value)
  {
    return wizard.setContextValue(key, value);
  }

  public boolean isFirst()
  {
    if (parent == null)
    {
      return true;
    }

    return parent.indexOf(this) == 0;
  }

  public boolean isLast()
  {
    if (parent == null)
    {
      return true;
    }

    return parent.indexOf(this) == parent.size() - 1;
  }

  public Step getPrevious()
  {
    if (parent == null)
    {
      return null;
    }

    int index = parent.indexOf(this);
    if (index == 0)
    {
      return parent.getPrevious();
    }

    return parent.get(index - 1);
  }

  public Step getNext()
  {
    if (parent == null)
    {
      return null;
    }

    int index = parent.indexOf(this);
    if (index == parent.size() - 1)
    {
      return parent.getNext();
    }

    return parent.get(index + 1);
  }

  public void accept(IStepVisitor visitor)
  {
    visitor.visit(this);
  }

  public abstract boolean isReady();

  public abstract boolean isFinished();

  final void setParent(CompoundStep parent)
  {
    this.parent = parent;
  }

  void setWizard(SteppingWizard wizard)
  {
    this.wizard = wizard;
  }

  final void setWizardPage(SteppingWizardPage wizardPage)
  {
    this.wizardPage = wizardPage;
  }
}
