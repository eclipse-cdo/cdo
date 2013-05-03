/*
 * Copyright (c) 2010-2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.ui.internal.ide.wizards;

import org.eclipse.emf.cdo.ui.internal.ide.messages.Messages;
import org.eclipse.emf.cdo.ui.widgets.SessionComposite;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * @author Victor Roldan Betancort
 */
public class CDOShareProjectWizardPage extends WizardPage
{
  private SessionComposite sessionComposite;

  private Listener modifyListener = new Listener()
  {
    public void handleEvent(Event e)
    {
      boolean valid = isPageComplete();
      setPageComplete(valid);
    }
  };

  public CDOShareProjectWizardPage(String id)
  {
    super(id);
  }

  public SessionComposite getSessionComposite()
  {
    return sessionComposite;
  }

  public void createControl(Composite parent)
  {
    sessionComposite = new SessionComposite(parent, SWT.NONE);
    setControl(sessionComposite);
    sessionComposite.addListener(SWT.Modify, modifyListener);
  }

  public String getSessionDescription()
  {
    return sessionComposite != null ? sessionComposite.getSessionDescription() : null;
  }

  public boolean isDescriptionValid()
  {
    return sessionComposite.isDescriptionValid();
  }

  @Override
  public boolean isPageComplete()
  {
    if (isDescriptionValid())
    {
      CDOShareProjectWizardPage.this.setErrorMessage(null);
      return true;
    }

    CDOShareProjectWizardPage.this.setErrorMessage(Messages.getString("CDOShareProjectWizardPage_0")); //$NON-NLS-1$
    return false;
  }
}
