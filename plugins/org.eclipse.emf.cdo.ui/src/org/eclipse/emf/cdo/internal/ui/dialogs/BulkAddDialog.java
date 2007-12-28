/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.ui.dialogs;

import org.eclipse.net4j.util.ui.StaticContentProvider;
import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.emf.ecore.EReference;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class BulkAddDialog extends TitleAreaDialog
{
  public static final String TITLE = "Bulk Add";

  private IWorkbenchPage page;

  private List<EReference> features = new ArrayList<EReference>();

  private EReference feature;

  private int instances = 100;

  public BulkAddDialog(IWorkbenchPage page, List<EReference> features)
  {
    super(new Shell(page.getWorkbenchWindow().getShell()));
    setShellStyle(getShellStyle() | SWT.APPLICATION_MODAL | SWT.MAX | SWT.TITLE | SWT.RESIZE);
    this.page = page;
    this.features = features;
  }

  public IWorkbenchPage getPage()
  {
    return page;
  }

  public List<EReference> getFeatures()
  {
    return features;
  }

  public EReference getFeature()
  {
    return feature;
  }

  public int getInstances()
  {
    return instances;
  }

  @Override
  protected void configureShell(Shell newShell)
  {
    super.configureShell(newShell);
    newShell.setText(TITLE);
  }

  @Override
  protected Control createDialogArea(Composite parent)
  {
    Composite composite = new Composite((Composite)super.createDialogArea(parent), SWT.NONE);
    composite.setLayoutData(UIUtil.createGridData());
    composite.setLayout(new GridLayout(2, false));

    setTitle("Select a child type and number of child instances");

    Label label = new Label(composite, SWT.NONE);
    label.setText("Type:");
    label.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));

    final TableViewer featureViewer = new TableViewer(composite, SWT.BORDER | SWT.SINGLE);
    featureViewer.getTable().setLayoutData(UIUtil.createGridData(true, true));
    featureViewer.setContentProvider(new StaticContentProvider(features));
    featureViewer.setLabelProvider(new LabelProvider()
    {
      @Override
      public String getText(Object element)
      {
        return ((EReference)element).getName();
      }
    });

    featureViewer.setInput(features);
    featureViewer.addSelectionChangedListener(new ISelectionChangedListener()
    {
      public void selectionChanged(SelectionChangedEvent event)
      {
        IStructuredSelection selection = (IStructuredSelection)featureViewer.getSelection();
        feature = selection.isEmpty() ? null : (EReference)selection.getFirstElement();
        dialogChanged();
      }
    });

    new Label(composite, SWT.NONE).setText("Instances:");
    final Text instancesText = new Text(composite, SWT.BORDER);
    instancesText.setLayoutData(UIUtil.createGridData(true, false));
    instancesText.setText(String.valueOf(instances));
    instancesText.addModifyListener(new ModifyListener()
    {
      public void modifyText(ModifyEvent e)
      {
        try
        {
          String text = instancesText.getText();
          instances = Integer.parseInt(text);
        }
        catch (NumberFormatException ex)
        {
          instances = 0;
        }

        dialogChanged();
      }
    });

    return composite;
  }

  @Override
  protected Control createButtonBar(Composite parent)
  {
    try
    {
      return super.createButtonBar(parent);
    }
    finally
    {
      dialogChanged();
    }
  }

  protected void dialogChanged()
  {
    getButton(IDialogConstants.OK_ID).setEnabled(false);
    if (feature == null)
    {
      setErrorMessage("Select a feature");
      return;
    }

    if (instances == 0)
    {
      setErrorMessage("Enter a valid number of instances (>0)");
      return;
    }

    getButton(IDialogConstants.OK_ID).setEnabled(true);
    setErrorMessage(null);
  }
}
