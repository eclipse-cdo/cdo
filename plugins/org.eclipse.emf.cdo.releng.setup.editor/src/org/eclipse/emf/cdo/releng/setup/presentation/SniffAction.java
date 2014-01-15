/*
 * Copyright (c) 2013, 2014 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.setup.presentation;

import org.eclipse.emf.cdo.releng.internal.setup.Activator;
import org.eclipse.emf.cdo.releng.internal.setup.ui.AbstractContainerAction;
import org.eclipse.emf.cdo.releng.internal.setup.ui.AbstractSetupDialog;
import org.eclipse.emf.cdo.releng.setup.CompoundSetupTask;
import org.eclipse.emf.cdo.releng.setup.SetupFactory;
import org.eclipse.emf.cdo.releng.setup.SetupPackage;
import org.eclipse.emf.cdo.releng.setup.SetupTask;
import org.eclipse.emf.cdo.releng.setup.SetupTask.Sniffer;
import org.eclipse.emf.cdo.releng.setup.SetupTaskContainer;
import org.eclipse.emf.cdo.releng.setup.presentation.templates.AutomaticProjectTemplate;
import org.eclipse.emf.cdo.releng.setup.util.UIUtil;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.ui.provider.ExtendedImageRegistry;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class SniffAction extends AbstractContainerAction
{
  private static final String TITLE = "Import From IDE";

  private List<Sniffer> sniffers;

  public SniffAction(boolean withDialog)
  {
    super(TITLE);
    setToolTipText("Imports tasks from the running IDE into the selected setup task container");
    setImageDescriptor(Activator.imageDescriptorFromPlugin(SetupEditorPlugin.PLUGIN_ID, "icons/sniff.gif"));
  }

  @Override
  protected boolean runInit(SetupTaskContainer container)
  {
    sniffers = getSniffers();
    if (sniffers.isEmpty())
    {
      MessageDialog.openInformation(UIUtil.getShell(), AbstractSetupDialog.SHELL_TEXT, "No task importers available.");
      return false;
    }

    boolean ok = new SelectSniffersDialog(UIUtil.getShell()).open() == SelectSniffersDialog.OK;
    return ok && !sniffers.isEmpty();
  }

  @Override
  protected void runModify(final SetupTaskContainer container)
  {
    try
    {
      UIUtil.runInProgressDialog(UIUtil.getShell(), new IRunnableWithProgress()
      {
        public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
        {
          int totalWork = 0;
          for (Sniffer sniffer : sniffers)
          {
            int work = sniffer.getWork();
            totalWork += work;
          }

          monitor.beginTask("Importing from IDE", totalWork);

          try
          {
            CompoundSetupTask compound = SetupFactory.eINSTANCE.createCompoundSetupTask("Import from " + new Date());
            container.getSetupTasks().add(compound);

            for (Sniffer sniffer : sniffers)
            {
              monitor.subTask(sniffer.getLabel());

              try
              {
                int work = sniffer.getWork();
                sniffer.sniff(compound, new SubProgressMonitor(monitor, work));
              }
              catch (NoClassDefFoundError ex)
              {
                SetupEditorPlugin.getPlugin().log(ex);
              }
              catch (Exception ex)
              {
                SetupEditorPlugin.getPlugin().log(ex);
              }
            }
          }
          finally
          {
            monitor.done();
          }
        }
      });
    }
    catch (Exception ex)
    {
      SetupEditorPlugin.getPlugin().log(ex);
    }
  }

  @Override
  protected void runDone(SetupTaskContainer container)
  {
    sniffers = null;
    super.runDone(container);
  }

  private List<Sniffer> getSniffers()
  {
    List<Sniffer> sniffers = new ArrayList<Sniffer>();
    for (EClassifier eClassifier : SetupPackage.eINSTANCE.getEClassifiers())
    {
      if (eClassifier instanceof EClass)
      {
        EClass eClass = (EClass)eClassifier;
        if (!eClass.isAbstract() && SetupPackage.Literals.SETUP_TASK.isSuperTypeOf(eClass))
        {
          try
          {
            SetupTask task = (SetupTask)EcoreUtil.create(eClass);
            task.collectSniffers(sniffers);
          }
          catch (NoClassDefFoundError ex)
          {
            SetupEditorPlugin.getPlugin().log(ex);
          }
        }
      }
    }

    return sniffers;
  }

  /**
   * @author Eike Stepper
   */
  private final class SelectSniffersDialog extends AbstractSetupDialog
  {
    private static final String LAST_SNIFFERS = "lastSniffers";

    private CheckboxTableViewer viewer;

    public SelectSniffersDialog(Shell parentShell)
    {
      super(parentShell, TITLE, 500, 400);
    }

    @Override
    protected String getDefaultMessage()
    {
      return "Check the importers of your choice and click the Import button.";
    }

    @Override
    protected void createUI(Composite parent)
    {
      viewer = CheckboxTableViewer.newCheckList(parent, SWT.NONE);
      viewer.setContentProvider(new ArrayContentProvider());
      viewer.setLabelProvider(new SnifferLabelProvider());
      viewer.setInput(sniffers);

      Table table = viewer.getTable();
      table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      String[] labels = getSettings().getArray(LAST_SNIFFERS);
      if (labels != null)
      {
        HashSet<String> toCheck = new HashSet<String>(Arrays.asList(labels));
        for (Sniffer sniffer : sniffers)
        {
          if (toCheck.contains(sniffer.getLabel()))
          {
            viewer.setChecked(sniffer, true);
          }
        }
      }
    }

    @Override
    protected void okPressed()
    {
      List<Object> checkedElements = Arrays.asList(viewer.getCheckedElements());
      sniffers.retainAll(checkedElements);

      List<String> labels = new ArrayList<String>();
      for (Sniffer sniffer : sniffers)
      {
        labels.add(sniffer.getLabel());
      }

      getSettings().put(LAST_SNIFFERS, labels.toArray(new String[labels.size()]));
      super.okPressed();
    }

    private IDialogSettings getSettings()
    {
      IDialogSettings dialogSettings = SetupEditorPlugin.getPlugin().getDialogSettings();
      IDialogSettings section = dialogSettings.getSection(AutomaticProjectTemplate.class.getName());
      if (section == null)
      {
        section = dialogSettings.addNewSection(SelectSniffersDialog.class.getName());
      }

      return section;
    }

    /**
     * @author Eike Stepper
     */
    private final class SnifferLabelProvider extends LabelProvider
    {
      @Override
      public Image getImage(Object element)
      {
        if (element instanceof Sniffer)
        {
          Sniffer sniffer = (Sniffer)element;
          return ExtendedImageRegistry.INSTANCE.getImage(sniffer.getIcon());
        }

        return super.getImage(element);
      }
    }
  }
}
