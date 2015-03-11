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
import org.eclipse.emf.cdo.explorer.ui.bundle.OM;

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
  private String type = CDOCheckout.TYPE_ONLINE;

  private boolean readOnly;

  public CheckoutTypePage()
  {
    super("New Checkout", "Select the type of the new checkout.");
  }

  public final String getType()
  {
    return type;
  }

  public final void setType(String type)
  {
    this.type = type;
  }

  public final boolean isReadOnly()
  {
    return readOnly;
  }

  public final void setReadOnly(boolean readOnly)
  {
    this.readOnly = readOnly;
  }

  @Override
  protected GridData createCompositeGridData()
  {
    return new GridData(SWT.CENTER, SWT.CENTER, true, true);
  }

  @Override
  protected void createUI(Composite parent)
  {
    addChoice(parent, "Online Transactional", "icons/transactional.gif", CDOCheckout.TYPE_ONLINE, false);
    addChoice(parent, "Online Historical", "icons/historical.gif", CDOCheckout.TYPE_ONLINE, true);
    addChoice(parent, "Offline", "icons/disconnect.gif", CDOCheckout.TYPE_OFFLINE, false);
  }

  private Button addChoice(Composite composite, String text, String imagePath, final String type, final boolean readOnly)
  {
    final SelectionListener listener = new SelectionListener()
    {
      public void widgetSelected(SelectionEvent e)
      {
        CheckoutTypePage.this.type = type;
        CheckoutTypePage.this.readOnly = readOnly;
        validate();
      }

      public void widgetDefaultSelected(SelectionEvent e)
      {
        widgetSelected(e);
        getContainer().showPage(getNextPage());
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
    super.repositoryChanged(repository);
  }

  @Override
  protected boolean doValidate() throws Exception
  {
    return true;
  }

  @Override
  protected void fillProperties(Properties properties)
  {
    properties.setProperty("type", type);
    properties.setProperty("readOnly", Boolean.toString(readOnly));
  }
}
