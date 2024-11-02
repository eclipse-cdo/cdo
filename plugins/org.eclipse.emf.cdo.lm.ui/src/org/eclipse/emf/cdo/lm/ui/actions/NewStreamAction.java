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
import org.eclipse.emf.cdo.lm.Baseline;
import org.eclipse.emf.cdo.lm.Drop;
import org.eclipse.emf.cdo.lm.Module;
import org.eclipse.emf.cdo.lm.Stream;
import org.eclipse.emf.cdo.lm.StreamSpec;
import org.eclipse.emf.cdo.lm.client.ISystemDescriptor;
import org.eclipse.emf.cdo.lm.client.ISystemManager;
import org.eclipse.emf.cdo.lm.provider.LMEditPlugin;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.emf.edit.ui.provider.ExtendedImageRegistry;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public class NewStreamAction extends LMAction.NewElement<Module>
{
  private static final int INVALID_VERSION = -1;

  private final AdapterFactory adapterFactory;

  private ISystemDescriptor systemDescriptor;

  private EList<Drop> possibleBases;

  private Drop lastBase;

  private Stream lastStream;

  private Drop base;

  private CommonStreamParameters<Module> streamParameters;

  public NewStreamAction(IWorkbenchPage page, StructuredViewer viewer, AdapterFactory adapterFactory, Module module)
  {
    super(page, viewer, //
        "New Stream" + INTERACTIVE, //
        "Add a new stream to module '" + module.getName() + "'", //
        ExtendedImageRegistry.INSTANCE.getImageDescriptor(LMEditPlugin.INSTANCE.getImage("full/obj16/Stream")), //
        "Add a new stream to module '" + module.getName() + "'.", //
        "icons/wizban/NewStream.png", //
        module);
    this.adapterFactory = adapterFactory;
  }

  @Override
  protected void preRun() throws Exception
  {
    Module module = getContext();
    systemDescriptor = ISystemManager.INSTANCE.getDescriptor(module.getSystem());

    possibleBases = new BasicEList<>();
    lastBase = null;
    lastStream = null;

    for (Stream stream : module.getStreams())
    {
      for (Baseline baseline : stream.getContents())
      {
        if (baseline instanceof Drop)
        {
          Drop drop = (Drop)baseline;
          if (drop.isRelease())
          {
            possibleBases.add(drop);
            lastBase = drop;
          }
        }
      }

      lastStream = stream;
    }

    if (lastStream != null && lastBase == null)
    {
      openError("No existing stream contains a release that allows for a new stream.");
      cancel();
      return;
    }

    if (lastStream == null)
    {
      streamParameters = new CommonStreamParameters<>(this, 1, 0);
    }
    else
    {
      streamParameters = new CommonStreamParameters<>(this, //
          lastStream.getMajorVersion(), //
          lastStream.getMinorVersion() + 1);
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
      label.setEnabled(lastStream != null);

      ComboViewer viewer = new ComboViewer(parent, SWT.BORDER | SWT.SINGLE);
      viewer.getControl().setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).create());
      viewer.getControl().setEnabled(lastStream != null);
      viewer.setLabelProvider(new AdapterFactoryLabelProvider(adapterFactory));
      viewer.setContentProvider(new ArrayContentProvider());
      viewer.setInput(possibleBases);

      if (lastBase != null)
      {
        base = lastBase;
        viewer.setSelection(new StructuredSelection(base));
      }

      viewer.addSelectionChangedListener(e -> {
        base = (Drop)e.getStructuredSelection().getFirstElement();
        validateDialog();
      });
    }

    streamParameters.fillDialogArea(parent);
  }

  @Override
  protected String doValidate(LMDialog dialog)
  {
    if (base == null && lastStream != null)
    {
      return "A base drop must be selected.";
    }

    String result = streamParameters.validate();
    if (result != null)
    {
      return result;
    }

    return super.doValidate(dialog);
  }

  @Override
  protected CDOObject newElement(Module module, IProgressMonitor monitor) throws Exception
  {
    return systemDescriptor.createStream(module, base, streamParameters, monitor);
  }

  /**
   * @author Eike Stepper
   */
  public static final class CommonStreamParameters<CONTEXT extends CDOObject> extends StreamSpec
  {
    private LMAction<CONTEXT> lmAction;

    public CommonStreamParameters(LMAction<CONTEXT> lmAction, int majorVersion, int minorVersion)
    {
      super(majorVersion, minorVersion, null);
      this.lmAction = lmAction;
    }

    public void fillDialogArea(Composite parent)
    {
      {
        Label label = new Label(parent, SWT.NONE);
        label.setLayoutData(GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).create());
        label.setText("Major version:");

        Text text = new Text(parent, SWT.BORDER | SWT.SINGLE);
        text.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).create());
        text.setText(Integer.toString(majorVersion));
        text.addModifyListener(e -> {
          try
          {
            majorVersion = Integer.parseInt(text.getText());

            if (majorVersion < 0)
            {
              majorVersion = INVALID_VERSION;
            }
          }
          catch (NumberFormatException ex)
          {
            majorVersion = INVALID_VERSION;
          }

          lmAction.validateDialog();
        });
      }

      {
        Label label = new Label(parent, SWT.NONE);
        label.setLayoutData(GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).create());
        label.setText("Minor version:");

        Text text = new Text(parent, SWT.BORDER | SWT.SINGLE);
        text.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).create());
        text.setText(Integer.toString(minorVersion));
        text.addModifyListener(e -> {
          try
          {
            minorVersion = Integer.parseInt(text.getText());

            if (minorVersion < 0)
            {
              minorVersion = INVALID_VERSION;
            }
          }
          catch (NumberFormatException ex)
          {
            minorVersion = INVALID_VERSION;
          }

          lmAction.validateDialog();
        });
      }

      {
        Label label = new Label(parent, SWT.NONE);
        label.setLayoutData(GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).create());
        label.setText("Code name:");

        Text text = new Text(parent, SWT.BORDER | SWT.SINGLE);
        text.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).create());
        text.addModifyListener(e -> {
          codeName = text.getText();
          lmAction.validateDialog();
        });
      }
    }

    public String validate()
    {
      if (majorVersion == INVALID_VERSION)
      {
        return "A valid major version must be entered.";
      }

      if (minorVersion == INVALID_VERSION)
      {
        return "A valid minor version must be entered.";
      }

      return null;
    }
  }
}
