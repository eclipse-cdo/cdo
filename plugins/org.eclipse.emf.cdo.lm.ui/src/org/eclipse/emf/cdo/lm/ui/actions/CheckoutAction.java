/*
 * Copyright (c) 2022, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.ui.actions;

import org.eclipse.emf.cdo.lm.Baseline;
import org.eclipse.emf.cdo.lm.client.IAssemblyManager;
import org.eclipse.emf.cdo.lm.provider.LMEditPlugin;
import org.eclipse.emf.cdo.lm.ui.bundle.OM;

import org.eclipse.net4j.util.StringUtil;

import org.eclipse.emf.common.util.ResourceLocator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public class CheckoutAction extends LMAction<Baseline>
{
  private String checkoutLabel;

  public CheckoutAction(IWorkbenchPage page, ResourceLocator resourceLocator, String text, Baseline baseline)
  {
    super(page, //
        text == null ? "Checkout " + baseline.getTypeAndName() + INTERACTIVE : text, //
        "Create a checkout for the selected " + resourceLocator.getString("_UI_" + baseline.eClass().getName() + "_type").toLowerCase(), //
        OM.getImageDescriptor("icons/checkout.gif"), //
        "Create a checkout for the selected " + resourceLocator.getString("_UI_" + baseline.eClass().getName() + "_type").toLowerCase() + ".", //
        "icons/Checkout.png", //
        baseline);
  }

  public CheckoutAction(IWorkbenchPage page, String text, Baseline baseline)
  {
    this(page, LMEditPlugin.INSTANCE, text, baseline);
  }

  public CheckoutAction(IWorkbenchPage page, Baseline baseline)
  {
    this(page, null, baseline);
  }

  @Override
  protected void preRun() throws Exception
  {
    Baseline baseline = getContext();
    checkoutLabel = getCheckoutLabel(baseline);

    super.preRun();
  }

  @Override
  protected void fillDialogArea(LMDialog dialog, Composite parent)
  {
    {
      Label label = new Label(parent, SWT.NONE);
      label.setLayoutData(GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).create());
      label.setText("Checkout label:");

      Text checkoutLabelText = new Text(parent, SWT.BORDER | SWT.SINGLE);
      checkoutLabelText.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).create());
      checkoutLabelText.setText(checkoutLabel);
      checkoutLabelText.selectAll();
      checkoutLabelText.addModifyListener(e -> {
        checkoutLabel = checkoutLabelText.getText();
        validateDialog();
      });

      checkoutLabelText.setFocus();
    }
  }

  @Override
  protected String doValidate(LMDialog dialog)
  {
    if (checkoutLabel == null || checkoutLabel.isEmpty())
    {
      return "A checkout label must be entered.";
    }

    return super.doValidate(dialog);
  }

  @Override
  protected void doRun(Baseline baseline, IProgressMonitor progressMonitor) throws Exception
  {
    checkout(baseline, checkoutLabel, progressMonitor);
  }

  public static String getCheckoutLabel(Baseline baseline)
  {
    return "Module " + baseline.getModule().getName() + " - " + baseline.getTypeAndName();
  }

  public static void checkout(Baseline baseline, String checkoutLabel, IProgressMonitor monitor) throws Exception
  {
    if (StringUtil.isEmpty(checkoutLabel))
    {
      checkoutLabel = getCheckoutLabel(baseline);
    }

    IAssemblyManager.INSTANCE.createDescriptor(checkoutLabel, baseline, monitor);
  }
}
