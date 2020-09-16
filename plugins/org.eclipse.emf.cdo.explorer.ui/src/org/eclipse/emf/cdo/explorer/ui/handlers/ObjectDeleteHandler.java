/*
 * Copyright (c) 2015, 2016, 2019, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.handlers;

import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.explorer.ui.ObjectListController;
import org.eclipse.emf.cdo.explorer.ui.bundle.OM;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import java.util.List;

/**
 * @author Eike Stepper
 */
public class ObjectDeleteHandler extends AbstractObjectHandler
{
  public ObjectDeleteHandler()
  {
    super(null);
  }

  @Override
  protected void preRun(ExecutionEvent event) throws Exception
  {
    Shell shell = HandlerUtil.getActiveShell(event);
    DeleteObjectsDialog dialog = new DeleteObjectsDialog(shell, getCheckout(), elements);
    if (dialog.open() != DeleteObjectsDialog.OK)
    {
      cancel();
    }
  }

  @Override
  protected boolean doExecute(ExecutionEvent event, List<EObject> transactionalElements, IProgressMonitor monitor)
  {
    try
    {
      for (EObject eObject : transactionalElements)
      {
        EcoreUtil.remove(eObject);
      }
    }
    catch (Throwable ex)
    {
      // TODO Exceptions due to deleting children of already deleted parents are expected, but hard to detect.
      // It would be better to determine the set of top-most objects plus all their transitive children.
      // This should happen in a ProgressRunnable before the DeleteObjectsDialog is even shown.

      Shell shell = HandlerUtil.getActiveShell(event);
      if (!shell.isDisposed())
      {
        shell.getDisplay().asyncExec(new Runnable()
        {
          @Override
          public void run()
          {
            try
            {
              if (!shell.isDisposed())
              {
                MessageDialog.openError(shell, "Error", ex.getMessage());
              }
            }
            catch (Exception ex)
            {
              OM.LOG.error(ex);
            }
          }
        });
      }

      return false;
    }

    return true;
  }

  /**
   * @author Eike Stepper
   */
  private static class DeleteObjectsDialog extends TitleAreaDialog
  {
    private final CDOCheckout checkout;

    private final List<EObject> objects;

    public DeleteObjectsDialog(Shell parentShell, CDOCheckout checkout, List<EObject> objects)
    {
      super(parentShell);
      this.checkout = checkout;
      this.objects = objects;

      setShellStyle(SWT.CLOSE | SWT.RESIZE | SWT.TITLE | SWT.APPLICATION_MODAL);
    }

    @Override
    protected Control createDialogArea(Composite parent)
    {
      Composite area = (Composite)super.createDialogArea(parent);

      int size = objects.size();
      if (size != 0)
      {
        String title = "Delete " + (size == 1 ? "Object" : "Objects");
        getShell().setText(title);
        setTitle(title);
        setTitleImage(OM.getImage("icons/wiz/delete.gif"));
        updateMessage(size);

        Composite container = new Composite(area, SWT.NONE);
        container.setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout containerGridLayout = new GridLayout();
        containerGridLayout.marginWidth = 10;
        containerGridLayout.marginHeight = 10;
        container.setLayout(containerGridLayout);

        final ObjectListController objectListController = new ObjectListController(checkout);
        for (EObject object : objects)
        {
          objectListController.addObject(object, true);
        }

        final CheckboxTreeViewer treeViewer = new CheckboxTreeViewer(container);
        treeViewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        objectListController.configure(treeViewer);

        treeViewer.addCheckStateListener(new ICheckStateListener()
        {
          @Override
          public void checkStateChanged(CheckStateChangedEvent event)
          {
            EObject object = objectListController.getObject(event.getElement());
            if (event.getChecked())
            {
              objects.add(object);
            }
            else
            {
              objects.remove(object);
            }

            int size = objects.size();
            updateMessage(size);

            if (size <= 1)
            {
              Button button = getButton(OK);
              button.setEnabled(size != 0);
            }
          }
        });
      }

      return area;
    }

    @Override
    protected Point getInitialSize()
    {
      return new Point(500, 400);
    }

    private void updateMessage(int size)
    {
      if (size == 0)
      {
        setMessage("No objects to delete.");
      }
      else
      {
        setMessage("Are you sure you want to delete " + (size == 1 ? "this" : "these") + " " + size + " " + (size == 1 ? "object" : "objects") + "?");
      }
    }
  }
}
