/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.ui.actions;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.lm.Baseline;
import org.eclipse.emf.cdo.lm.Change;
import org.eclipse.emf.cdo.lm.FixedBaseline;
import org.eclipse.emf.cdo.lm.Stream;
import org.eclipse.emf.cdo.lm.client.ISystemDescriptor;
import org.eclipse.emf.cdo.lm.client.ISystemManager;
import org.eclipse.emf.cdo.lm.internal.client.LMNamingStrategy;
import org.eclipse.emf.cdo.lm.provider.LMEditPlugin;
import org.eclipse.emf.cdo.lm.ui.bundle.OM;
import org.eclipse.emf.cdo.lm.ui.widgets.BaselineComposite;

import org.eclipse.net4j.util.StringUtil;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.edit.ui.provider.ExtendedImageRegistry;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public class NewChangeAction extends LMAction<Stream>
{
  private ISystemDescriptor systemDescriptor;

  private EList<Baseline> possibleBases;

  private FixedBaseline base;

  private String labelString;

  private Text labelText;

  private boolean checkout = true;

  private Button checkoutButton;

  private String checkoutLabelString;

  private Text checkoutLabelText;

  public NewChangeAction(IWorkbenchPage page, Stream stream, FixedBaseline base)
  {
    super(page, //
        "New Change" + INTERACTIVE, //
        "Add a new change to stream '" + stream.getName() + "'", //
        ExtendedImageRegistry.INSTANCE.getImageDescriptor(LMEditPlugin.INSTANCE.getImage("full/obj16/Change")), //
        "Add a new change to stream '" + stream.getName() + "'.", //
        "icons/NewChange.png", //
        stream);
    this.base = base;
  }

  @Override
  protected void preRun() throws Exception
  {
    Stream stream = getContext();
    systemDescriptor = ISystemManager.INSTANCE.getDescriptor(stream.getSystem());

    possibleBases = new BasicEList<>();

    stream.forEachBaseline(baseline -> {
      if (baseline instanceof FixedBaseline)
      {
        possibleBases.add(baseline);
      }
    });

    possibleBases.sort(Baseline.COMPARATOR);

    if (base == null && !possibleBases.isEmpty())
    {
      base = (FixedBaseline)possibleBases.get(0);
    }

    super.preRun();
  }

  @Override
  protected void fillDialogArea(LMDialog dialog, Composite parent)
  {
    {
      Label label = new Label(parent, SWT.NONE);
      label.setLayoutData(GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).create());
      label.setText("Base:");

      BaselineComposite baselineComposite = new BaselineComposite(parent, SWT.NONE, possibleBases, getContext());
      baselineComposite.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).create());

      if (base != null)
      {
        baselineComposite.setBaseline(base);
      }

      baselineComposite.addModifyListener((control, baseline) -> {
        base = (FixedBaseline)baseline;
        validateDialog();
      });

      if (possibleBases.isEmpty())
      {
        label.setEnabled(false);
        baselineComposite.setEnabled(false);
      }
    }

    {
      Label label = new Label(parent, SWT.NONE);
      label.setLayoutData(GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).create());
      label.setText("Label:");

      labelText = new Text(parent, SWT.BORDER | SWT.SINGLE);
      labelText.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).create());
      labelText.addModifyListener(e -> {
        labelString = labelText.getText();

        if (checkout)
        {
          checkoutLabelText.setText(getCheckoutLabel());
        }

        validateDialog();
      });
      labelText.setFocus();
      labelText.selectAll();
    }

    {
      new Label(parent, SWT.NONE);

      Composite composite = new Composite(parent, SWT.NONE);
      composite.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).create());
      composite.setLayout(GridLayoutFactory.fillDefaults().numColumns(2).create());

      checkoutButton = new Button(composite, SWT.CHECK);
      checkoutButton.setText("Checkout as:");
      checkoutButton.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).create());
      checkoutButton.addSelectionListener(SelectionListener.widgetSelectedAdapter(e -> {
        checkout = checkoutButton.getSelection();
        checkoutLabelText.setEnabled(checkout);
        checkoutLabelText.setText(checkout ? getCheckoutLabel() : "");
        validateDialog();
      }));

      checkoutLabelText = new Text(composite, SWT.BORDER | SWT.SINGLE);
      checkoutLabelText.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).create());
      checkoutLabelText.addModifyListener(e -> {
        checkoutLabelString = checkoutLabelText.getText();
        validateDialog();
      });
    }

    checkoutButton.setSelection(checkout);
  }

  @Override
  protected String doValidate(LMDialog dialog)
  {
    if (!possibleBases.isEmpty() && base == null)
    {
      return "A base must be selected.";
    }

    if (StringUtil.isEmpty(labelString))
    {
      return "A label must be entered.";
    }

    if (checkout)
    {
      if (checkoutLabelString == null || checkoutLabelString.isEmpty())
      {
        return "A checkout label must be entered.";
      }
    }

    Stream stream = getContext();
    String moduleName = stream.getModule().getName();

    boolean[] branchExists = { false };
    systemDescriptor.withModuleSession(moduleName, moduleSession -> {
      CDOBranchPoint baseBranchPoint = (base == null ? stream.getBranch().getPointRef(java.lang.System.currentTimeMillis()) : base.getBranchPoint())
          .resolve(moduleSession.getBranchManager());

      String branchName = LMNamingStrategy.getChangeBranchName(labelString);
      branchExists[0] = baseBranchPoint.getBranch().getBranch(branchName) != null;

    });

    if (branchExists[0])
    {
      return "A change with the same label already exists.";
    }

    return super.doValidate(dialog);
  }

  @Override
  protected void doRun(Stream stream, IProgressMonitor monitor) throws Exception
  {
    monitor.beginTask("", checkout ? 2 : 1);

    Change change = systemDescriptor.createChange(stream, base, labelString, SubMonitor.convert(monitor, 1));

    if (checkout)
    {
      if (change != null)
      {
        try
        {
          CheckoutAction.checkout(change, SubMonitor.convert(monitor, 1));
        }
        catch (Exception ex)
        {
          OM.LOG.error(ex);
        }
      }
      else
      {
        monitor.worked(1);
      }
    }

    monitor.done();
  }

  private String getCheckoutLabel()
  {
    // TODO Move to NamingStrategy.
    if (StringUtil.isEmpty(labelString))
    {
      return "";
    }

    return "Module " + getContext().getModule().getName() + " - Change " + labelString;
  }
}
