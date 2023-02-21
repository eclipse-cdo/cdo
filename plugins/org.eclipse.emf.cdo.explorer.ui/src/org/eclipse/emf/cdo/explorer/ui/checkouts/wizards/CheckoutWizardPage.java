/*
 * Copyright (c) 2015, 2019, 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.checkouts.wizards;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.explorer.repositories.CDORepository;
import org.eclipse.emf.cdo.explorer.ui.bundle.OM;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;

import java.util.Properties;

/**
 * @author Eike Stepper
 */
public abstract class CheckoutWizardPage extends WizardPage
{
  public CheckoutWizardPage(String title, String message)
  {
    super("wizardPage");
    setImageDescriptor(OM.getImageDescriptor("icons/wiz/new_checkout.gif"));
    setTitle(title);
    setMessage(message);
  }

  @Override
  public final CheckoutWizard getWizard()
  {
    return (CheckoutWizard)super.getWizard();
  }

  @Override
  public final void createControl(Composite parent)
  {
    Composite container = new Composite(parent, SWT.NULL);
    GridLayout containerGridLayout = new GridLayout();
    container.setLayout(containerGridLayout);
    setControl(container);

    Composite composite = new Composite(container, SWT.NONE);
    composite.setLayoutData(createCompositeGridData());
    composite.setLayout(createCompositeLayout());

    createUI(composite);
    validate();
  }

  protected GridData createCompositeGridData()
  {
    return new GridData(SWT.FILL, SWT.FILL, true, true);
  }

  protected Layout createCompositeLayout()
  {
    GridLayout gridLayout = new GridLayout(1, false);
    gridLayout.marginWidth = 0;
    gridLayout.marginHeight = 0;
    return gridLayout;
  }

  protected abstract void createUI(Composite parent);

  protected void showNextPage()
  {
    if (isPageComplete())
    {
      getContainer().showPage(getNextPage());
    }
  }

  protected void repositoryChanged(CDORepository repository)
  {
    CheckoutWizardPage nextPage = (CheckoutWizardPage)getNextPage();
    if (nextPage != null)
    {
      nextPage.repositoryChanged(repository);
    }
  }

  protected void typeChanged(String type)
  {
    CheckoutWizardPage nextPage = (CheckoutWizardPage)getNextPage();
    if (nextPage != null)
    {
      nextPage.typeChanged(type);
    }
  }

  protected void branchPointChanged(int branchID, long timeStamp)
  {
    CheckoutWizardPage nextPage = (CheckoutWizardPage)getNextPage();
    if (nextPage != null)
    {
      nextPage.branchPointChanged(branchID, timeStamp);
    }
  }

  protected void rootObjectChanged(CDOID rootID)
  {
    CheckoutWizardPage nextPage = (CheckoutWizardPage)getNextPage();
    if (nextPage != null)
    {
      nextPage.rootObjectChanged(rootID);
    }
  }

  protected boolean pageAboutToDeactivate(Object targetPage)
  {
    return true;
  }

  protected void pageActivated()
  {
  }

  protected final void validate()
  {
    try
    {
      boolean valid = doValidate();
      setErrorMessage(null);
      setPageComplete(valid);
    }
    catch (ValidationProblem ex)
    {
      String message = ex.getMessage();
      setErrorMessage(message);
      setPageComplete(false);
    }
  }

  protected abstract boolean doValidate() throws ValidationProblem;

  protected abstract void fillProperties(Properties properties);

  protected static void log(String message)
  {
    // System.out.println(message);
  }

  /**
   * @author Eike Stepper
   */
  public static final class ValidationProblem extends Exception
  {
    private static final long serialVersionUID = 1L;

    public ValidationProblem()
    {
    }

    public ValidationProblem(String message)
    {
      super(message);
    }
  }
}
