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
package org.eclipse.emf.cdo.explorer.ui.checkouts.wizards;

import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.explorer.repositories.CDORepository;
import org.eclipse.emf.cdo.explorer.repositories.CDORepository.IDGeneration;
import org.eclipse.emf.cdo.explorer.ui.bundle.OM;

import org.eclipse.net4j.util.ObjectUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import java.util.Properties;

/**
 * @author Eike Stepper
 */
public class CheckoutTypePage extends CheckoutWizardPage
{
  private CDORepository repository;

  private String type = CDOCheckout.TYPE_ONLINE_TRANSACTIONAL;

  private Button transactionalButton;

  private Button historicalButton;

  private Button offlineButton;

  public CheckoutTypePage()
  {
    super("Checkout Type", "Select the type of the new checkout.");
  }

  public final String getType()
  {
    return type;
  }

  public final void setType(String type)
  {
    if (!ObjectUtil.equals(this.type, type))
    {
      log("Setting type to " + type);
      this.type = type;

      typeChanged(type);
    }
  }

  @Override
  protected GridData createCompositeGridData()
  {
    return new GridData(SWT.CENTER, SWT.CENTER, true, true);
  }

  @Override
  protected void createUI(Composite parent)
  {
    transactionalButton = addChoice(parent, "Online Transactional", "icons/transactional.gif",
        CDOCheckout.TYPE_ONLINE_TRANSACTIONAL);

    historicalButton = addChoice(parent, "Online Historical", "icons/historical.gif",
        CDOCheckout.TYPE_ONLINE_HISTORICAL);

    offlineButton = addChoice(parent, "Offline", "icons/disconnect.gif", CDOCheckout.TYPE_OFFLINE);
  }

  private Button addChoice(Composite composite, String text, String imagePath, final String type)
  {
    final SelectionListener listener = new SelectionListener()
    {
      public void widgetSelected(SelectionEvent e)
      {
        CheckoutTypePage.this.setType(type);
        validate();
      }

      public void widgetDefaultSelected(SelectionEvent e)
      {
        widgetSelected(e);
        showNextPage();
      }
    };

    Button button = new Button(composite, SWT.RADIO);
    button.setText(text);
    button.addSelectionListener(listener);
    button.addMouseListener(new MouseAdapter()
    {
      @Override
      public void mouseDoubleClick(MouseEvent e)
      {
        listener.widgetDefaultSelected(null);
      }
    });

    Label imageLabel = new Label(composite, SWT.NONE);
    imageLabel.setImage(OM.getImage(imagePath));

    new Label(composite, SWT.NONE);
    return button;
  }

  @Override
  protected void repositoryChanged(CDORepository repository)
  {
    this.repository = repository;

    Button button = getButton(type);
    if (button != null && !button.isEnabled())
    {
      setType(CDOCheckout.TYPE_ONLINE_TRANSACTIONAL);
    }

    super.repositoryChanged(repository);
  }

  private Button getButton(String type)
  {
    if (type == CDOCheckout.TYPE_ONLINE_TRANSACTIONAL)
    {
      return transactionalButton;
    }

    if (type == CDOCheckout.TYPE_ONLINE_HISTORICAL)
    {
      return historicalButton;
    }

    if (type == CDOCheckout.TYPE_OFFLINE)
    {
      return offlineButton;
    }

    return null;
  }

  @Override
  protected void pageActivated()
  {
    if (transactionalButton != null)
    {
      transactionalButton.setEnabled(false);
      historicalButton.setEnabled(false);
      offlineButton.setEnabled(false);

      if (repository != null)
      {
        transactionalButton.setEnabled(true);

        if (repository.getVersioningMode().isSupportingAudits())
        {
          historicalButton.setEnabled(true);
          if (repository.getIDGeneration() == IDGeneration.UUID)
          {
            offlineButton.setEnabled(true);
          }
        }

        if (historicalButton.getSelection() && !historicalButton.isEnabled())
        {
          setType(CDOCheckout.TYPE_ONLINE_TRANSACTIONAL);
        }
        else if (offlineButton.getSelection() && !offlineButton.isEnabled())
        {
          setType(CDOCheckout.TYPE_ONLINE_TRANSACTIONAL);
        }
      }

      transactionalButton.setSelection(type == CDOCheckout.TYPE_ONLINE_TRANSACTIONAL);
    }
    historicalButton.setSelection(type == CDOCheckout.TYPE_ONLINE_HISTORICAL);
    offlineButton.setSelection(type == CDOCheckout.TYPE_OFFLINE);
  }

  @Override
  protected boolean doValidate() throws ValidationProblem
  {
    return true;
  }

  @Override
  protected void fillProperties(Properties properties)
  {
    properties.setProperty("type", type);
  }
}
