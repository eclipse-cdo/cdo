/*
 * Copyright (c) 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.explorer.repositories.CDORepository.VersioningMode;
import org.eclipse.emf.cdo.explorer.ui.bundle.OM;
import org.eclipse.emf.cdo.internal.explorer.checkouts.CDOCheckoutImpl;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
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
    transactionalButton = addChoice(parent, "Online Transactional", "icons/transactional.gif", "Creates a remote connection for online editing.",
        CDOCheckout.TYPE_ONLINE_TRANSACTIONAL);

    historicalButton = addChoice(parent, "Online Historical", "icons/historical.gif", "Creates a remote connection for online auditing.",
        CDOCheckout.TYPE_ONLINE_HISTORICAL);

    offlineButton = addChoice(parent, "Offline", "icons/disconnect.gif", "Creates a local checkout for offline editing.", CDOCheckout.TYPE_OFFLINE);
  }

  private Button addChoice(Composite composite, String text, String imagePath, String description, final String type)
  {
    final SelectionListener listener = new SelectionListener()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        CheckoutTypePage.this.setType(type);
        validate();
      }

      @Override
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

    GridLayout descriptionLayout = UIUtil.createGridLayout(2);
    descriptionLayout.marginLeft = 20;
    descriptionLayout.horizontalSpacing = 5;

    Composite descriptionComposite = new Composite(composite, SWT.NONE);
    descriptionComposite.setLayout(descriptionLayout);

    Label imageLabel = new Label(descriptionComposite, SWT.NONE);
    imageLabel.setImage(OM.getImage(imagePath));

    Label descriptionLabel = new Label(descriptionComposite, SWT.NONE);
    descriptionLabel.setText(description);

    new Label(composite, SWT.NONE);
    return button;
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

        VersioningMode versioningMode = repository.getVersioningMode();
        if (versioningMode == null || versioningMode.isSupportingAudits())
        {
          historicalButton.setEnabled(true);
          IDGeneration idGeneration = repository.getIDGeneration();
          if (idGeneration == null || idGeneration == IDGeneration.UUID)
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

      transactionalButton.setSelection(CDOCheckout.TYPE_ONLINE_TRANSACTIONAL.equals(type));
    }

    historicalButton.setSelection(CDOCheckout.TYPE_ONLINE_HISTORICAL.equals(type));
    offlineButton.setSelection(CDOCheckout.TYPE_OFFLINE.equals(type));
  }

  @Override
  protected boolean doValidate() throws ValidationProblem
  {
    return true;
  }

  @Override
  protected void fillProperties(Properties properties)
  {
    properties.setProperty(CDOCheckoutImpl.PROP_TYPE, type);
    properties.setProperty(CDOCheckoutImpl.PROP_READ_ONLY, Boolean.toString(CDOCheckout.TYPE_ONLINE_HISTORICAL.equals(type)));
  }
}
