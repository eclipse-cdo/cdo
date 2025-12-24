/*
 * Copyright (c) 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.graphiti.wizards;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Martin Fluegge
 */
public abstract class AbstractDawnGraphitiWizardPage extends WizardPage
{
  public AbstractDawnGraphitiWizardPage(String pageName, String title, ImageDescriptor titleImage)
  {
    super(pageName, title, titleImage);
  }

  protected AbstractDawnGraphitiWizardPage(String pageName)
  {
    super(pageName);
  }

  @Override
  public void createControl(Composite parent)
  {
    Composite composite = new Composite(parent, SWT.NULL);
    composite.setFont(parent.getFont());
    composite.setLayout(new GridLayout());
    composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

    createWizardContents(composite);

    setPageComplete(true);

    // Show description on opening
    setErrorMessage(null);
    setMessage(null);
    setControl(composite);
  }

  abstract protected void createWizardContents(Composite parent);

}
