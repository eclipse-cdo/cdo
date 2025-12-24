/*
 * Copyright (c) 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.repositories.wizards;

import org.eclipse.emf.cdo.explorer.CDOExplorerUtil;
import org.eclipse.emf.cdo.explorer.ui.bundle.OM;
import org.eclipse.emf.cdo.explorer.ui.checkouts.wizards.CheckoutWizardPage.ValidationProblem;
import org.eclipse.emf.cdo.internal.explorer.AbstractElement;
import org.eclipse.emf.cdo.internal.explorer.repositories.CDORepositoryImpl;

import org.eclipse.net4j.util.security.IPasswordCredentials;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import java.util.Properties;

/**
 * @author Eike Stepper
 */
public abstract class AbstractRepositoryPage extends WizardPage implements SelectionListener, ModifyListener
{
  private final String defaultLabel;

  private Text labelText;

  private Properties properties;

  public AbstractRepositoryPage(String pageName, String defaultLabel)
  {
    super(pageName);
    setImageDescriptor(OM.getImageDescriptor("icons/wiz/new_repo.gif"));
    this.defaultLabel = CDOExplorerUtil.getRepositoryManager().getUniqueLabel(defaultLabel);
  }

  @Override
  public NewRepositoryWizard getWizard()
  {
    return (NewRepositoryWizard)super.getWizard();
  }

  @Override
  public IWizardPage getNextPage()
  {
    return null;
  }

  public Properties getProperties()
  {
    return properties;
  }

  public IPasswordCredentials getCredentials()
  {
    return null;
  }

  @Override
  public void createControl(Composite parent)
  {
    Composite container = new Composite(parent, SWT.NULL);
    GridLayout containerGridLayout = new GridLayout();
    containerGridLayout.numColumns = 2;
    container.setLayout(containerGridLayout);
    setControl(container);

    AbstractRepositoryPage.createLabel(container, "Label:");
    labelText = new Text(container, SWT.BORDER);
    labelText.setText(defaultLabel);
    labelText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    labelText.addModifyListener(new ModifyListener()
    {
      @Override
      public void modifyText(ModifyEvent e)
      {
        validate();
      }
    });

    fillPage(container);

    setPreviousPage(getWizard().getTypePage());
    validate();
  }

  @Override
  public void modifyText(ModifyEvent e)
  {
    validate();
  }

  @Override
  public void widgetSelected(SelectionEvent e)
  {
    validate();
  }

  @Override
  public void widgetDefaultSelected(SelectionEvent e)
  {
    validate();
  }

  protected final void validate()
  {
    properties = new Properties();
    properties.setProperty(CDORepositoryImpl.PROP_TYPE, getName());

    try
    {
      doValidate(properties);
      setErrorMessage(null);
      setPageComplete(true);
    }
    catch (Exception ex)
    {
      properties = null;
      setErrorMessage(ex.getMessage());
      setPageComplete(false);
    }
  }

  protected void doValidate(Properties properties) throws Exception
  {
    String label = labelText.getText();
    if (label.length() == 0)
    {
      throw new ValidationProblem("Label is empty.");
    }

    for (int i = 0; i < label.length(); i++)
    {
      char c = label.charAt(i);
      for (int j = 0; j < AbstractElement.ILLEGAL_LABEL_CHARACTERS.length(); j++)
      {
        if (c == AbstractElement.ILLEGAL_LABEL_CHARACTERS.charAt(j))
        {
          throw new ValidationProblem("Invalid character: " + AbstractElement.ILLEGAL_LABEL_CHARACTERS.substring(j, 1));
        }
      }
    }

    if (CDOExplorerUtil.getRepositoryManager().getRepositoryByLabel(label) != null)
    {
      throw new ValidationProblem("Label is not unique.");
    }

    properties.setProperty(CDORepositoryImpl.PROP_LABEL, label);
  }

  protected abstract void fillPage(Composite container);

  public static Label createLabel(Composite parent, String text)
  {
    Label label = new Label(parent, SWT.NONE);
    label.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
    label.setText(text);
    return label;
  }

  public static Text createText(Composite container, int widthHint)
  {
    GridData gridData = new GridData(SWT.LEFT, SWT.CENTER, false, false);
    gridData.widthHint = widthHint;

    Text text = new Text(container, SWT.BORDER);
    text.setLayoutData(gridData);
    return text;
  }
}
