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

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.lm.Baseline;
import org.eclipse.emf.cdo.lm.Change;
import org.eclipse.emf.cdo.lm.Delivery;
import org.eclipse.emf.cdo.lm.Stream;
import org.eclipse.emf.cdo.lm.client.IAssemblyDescriptor;
import org.eclipse.emf.cdo.lm.client.IAssemblyManager;
import org.eclipse.emf.cdo.lm.client.ISystemDescriptor;
import org.eclipse.emf.cdo.lm.client.ISystemManager;
import org.eclipse.emf.cdo.lm.provider.LMEditPlugin;
import org.eclipse.emf.cdo.lm.ui.InteractiveDeliveryMerger;
import org.eclipse.emf.cdo.lm.ui.bundle.OM;
import org.eclipse.emf.cdo.lm.ui.widgets.BaselineComposite;
import org.eclipse.emf.cdo.lm.util.LMMerger2;
import org.eclipse.emf.cdo.lm.util.LMOperations;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.edit.ui.provider.ExtendedImageRegistry;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public class NewDeliveryAction extends LMAction.NewElement<Stream>
{
  private static final LMMerger2 MERGER = new InteractiveDeliveryMerger();

  private boolean changeWasNull;

  private ISystemDescriptor systemDescriptor;

  private EList<Baseline> possibleChanges;

  private Change change;

  private IAssemblyDescriptor[] assemblyDescriptors = {};

  private boolean deleteCheckouts;

  private Button deleteCheckoutsButton;

  public NewDeliveryAction(IWorkbenchPage page, StructuredViewer viewer, Stream stream, Change change)
  {
    super(page, viewer, //
        (change == null ? "New Delivery" : "Deliver to " + stream.getTypeAndName()) + INTERACTIVE, //
        "Add a new delivery to stream '" + stream.getName() + "'", //
        ExtendedImageRegistry.INSTANCE.getImageDescriptor(LMEditPlugin.INSTANCE.getImage("full/obj16/Delivery")), //
        "Add a new delivery to stream '" + stream.getName() + "'.", //
        "icons/wizban/NewDelivery.png", //
        stream);
    this.change = change;
    changeWasNull = change == null;
  }

  @Override
  public String getAuthorizableOperationID()
  {
    return LMOperations.CREATE_DELIVERY;
  }

  @Override
  protected void preRun() throws Exception
  {
    Stream stream = getContext();
    systemDescriptor = ISystemManager.INSTANCE.getDescriptor(stream.getSystem());

    possibleChanges = new BasicEList<>();

    if (changeWasNull)
    {
      stream.forEachBaseline(baseline -> {
        if (baseline instanceof Change)
        {
          Change change = (Change)baseline;

          if (change.getDeliveryPoint(stream) == null)
          {
            possibleChanges.add(change);
          }
        }
      });

      possibleChanges.sort(Baseline.COMPARATOR);
    }
    else
    {
      possibleChanges.add(change);
    }

    if (change == null && !possibleChanges.isEmpty())
    {
      change = (Change)possibleChanges.get(0);
    }

    assemblyDescriptors = IAssemblyManager.INSTANCE.getDescriptors(change);

    super.preRun();
  }

  @Override
  protected boolean isDialogNeeded()
  {
    return changeWasNull || assemblyDescriptors.length != 0;
  }

  @Override
  protected void fillDialogArea(LMDialog dialog, Composite parent)
  {
    {
      Label label = new Label(parent, SWT.NONE);
      label.setLayoutData(GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).create());
      label.setText("Change:");

      BaselineComposite baselineComposite = new BaselineComposite(parent, SWT.NONE, possibleChanges, getContext());
      baselineComposite.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).create());

      if (change != null)
      {
        baselineComposite.setBaseline(change);
      }

      baselineComposite.addModifyListener((control, baseline) -> {
        change = (Change)baseline;
        assemblyDescriptors = IAssemblyManager.INSTANCE.getDescriptors(change);
        deleteCheckoutsButton.setEnabled(assemblyDescriptors.length != 0);
        validateDialog();
      });

      if (!changeWasNull)
      {
        label.setEnabled(false);
        baselineComposite.setEnabled(false);
      }
    }

    {
      deleteCheckoutsButton = newCheckBox(parent, "Delete checkout(s)");
      deleteCheckoutsButton.addSelectionListener(SelectionListener.widgetSelectedAdapter(e -> {
        deleteCheckouts = deleteCheckoutsButton.getSelection();
        validateDialog();
      }));

      deleteCheckoutsButton.setSelection(deleteCheckouts);
      deleteCheckoutsButton.setEnabled(assemblyDescriptors.length != 0);

      if (!changeWasNull)
      {
        deleteCheckoutsButton.setFocus();
      }
    }
  }

  @Override
  protected String doValidate(LMDialog dialog)
  {
    if (change == null)
    {
      return "A change must be selected.";
    }

    return super.doValidate(dialog);
  }

  @Override
  protected CDOObject newElement(Stream stream, IProgressMonitor monitor) throws Exception
  {
    Delivery delivery = systemDescriptor.createDelivery(stream, change, MERGER, monitor);

    if (delivery != null && deleteCheckouts)
    {
      for (IAssemblyDescriptor assemblyDescriptor : assemblyDescriptors)
      {
        try
        {
          CDOCheckout checkout = assemblyDescriptor.getCheckout();
          checkout.delete(true);
        }
        catch (Exception ex)
        {
          OM.LOG.error(ex);
        }
      }
    }

    return delivery;
  }
}
