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

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.lm.LMFactory;
import org.eclipse.emf.cdo.lm.Module;
import org.eclipse.emf.cdo.lm.ModuleType;
import org.eclipse.emf.cdo.lm.Process;
import org.eclipse.emf.cdo.lm.System;
import org.eclipse.emf.cdo.lm.client.ISystemDescriptor;
import org.eclipse.emf.cdo.lm.client.ISystemManager;
import org.eclipse.emf.cdo.lm.provider.LMEditPlugin;
import org.eclipse.emf.cdo.lm.ui.actions.NewStreamAction.CommonStreamParameters;

import org.eclipse.emf.edit.ui.provider.ExtendedImageRegistry;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.equinox.p2.metadata.Version;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class NewModuleAction extends LMAction.NewElement<System>
{
  private static final ModuleType NO_MODULE_TYPE = LMFactory.eINSTANCE.createModuleType("");

  private ISystemDescriptor systemDescriptor;

  private String name;

  private ModuleType type;

  private CommonStreamParameters<System> streamParameters;

  public NewModuleAction(IWorkbenchPage page, TreeViewer viewer, System system)
  {
    super(page, viewer, //
        "New Module" + INTERACTIVE, //
        "Add a new module to system '" + system.getName() + "'", //
        ExtendedImageRegistry.INSTANCE.getImageDescriptor(LMEditPlugin.INSTANCE.getImage("full/obj16/Module")), //
        "Add a new module to system '" + system.getName() + "'.", //
        "icons/NewModule.png", //
        system);
  }

  @Override
  protected void preRun() throws Exception
  {
    System system = getContext();
    systemDescriptor = ISystemManager.INSTANCE.getDescriptor(system);

    Process process = system.getProcess();
    Version initialModuleVersion = process.getInitialModuleVersion();
    if (initialModuleVersion == null)
    {
      streamParameters = new CommonStreamParameters<>(this, 0, 1);
    }
    else
    {
      streamParameters = new CommonStreamParameters<>(this, //
          (int)initialModuleVersion.getSegment(0), //
          (int)initialModuleVersion.getSegment(1));
    }

    super.preRun();
  }

  @Override
  protected void fillDialogArea(LMDialog dialog, Composite parent)
  {
    {
      Label label = new Label(parent, SWT.NONE);
      label.setLayoutData(GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).create());
      label.setText("Name:");

      Text text = new Text(parent, SWT.BORDER | SWT.SINGLE);
      text.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).create());
      text.addModifyListener(e -> {
        name = text.getText();
        validateDialog();
      });
    }

    List<ModuleType> moduleTypes = systemDescriptor.getSystem().getProcess().getModuleTypes();
    if (!moduleTypes.isEmpty())
    {
      moduleTypes = new ArrayList<>(moduleTypes);
      moduleTypes.add(NO_MODULE_TYPE);

      Label label = new Label(parent, SWT.NONE);
      label.setLayoutData(GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).create());
      label.setText("Type:");

      ComboViewer comboViewer = new ComboViewer(parent, SWT.READ_ONLY);
      comboViewer.setLabelProvider(new ModuleTypeLabelProvider());
      comboViewer.setContentProvider(new ArrayContentProvider());
      comboViewer.setInput(moduleTypes);
      comboViewer.addSelectionChangedListener(new ISelectionChangedListener()
      {
        @Override
        public void selectionChanged(SelectionChangedEvent event)
        {
          Object selection = event.getStructuredSelection().getFirstElement();
          type = selection == NO_MODULE_TYPE ? null : (ModuleType)selection;
          validateDialog();
        }
      });
    }

    {
      Composite composite = new Composite(parent, SWT.NONE);
      composite.setLayout(GridLayoutFactory.fillDefaults().numColumns(3).equalWidth(false).create());
      composite.setLayoutData(GridDataFactory.fillDefaults().span(2, 1).align(SWT.FILL, SWT.CENTER).grab(true, false).create());

      Label separator1 = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
      separator1.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).create());

      Label label = new Label(composite, SWT.NONE);
      label.setLayoutData(GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).create());
      label.setText("Initial Stream"); // TODO Move to NamingStrategy.

      Label separator2 = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
      separator2.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).create());
    }

    streamParameters.fillDialogArea(parent);
  }

  @Override
  protected String doValidate(LMDialog dialog)
  {
    if (name == null || name.isEmpty())
    {
      return "A valid name must be entered.";
    }

    String result = streamParameters.validate();
    if (result != null)
    {
      return result;
    }

    return super.doValidate(dialog);
  }

  @Override
  protected CDOObject newElement(System system, IProgressMonitor monitor) throws Exception
  {
    Module module = systemDescriptor.createModule(name, type, streamParameters, monitor);
    return module.getStreams().get(0);
  }

  /**
   * @author Eike Stepper
   */
  private static final class ModuleTypeLabelProvider extends LabelProvider
  {
    public ModuleTypeLabelProvider()
    {
    }

    @Override
    public String getText(Object element)
    {
      if (element instanceof ModuleType)
      {
        ModuleType type = (ModuleType)element;
        return type.getName();
      }

      return super.getText(element);
    }
  }
}
