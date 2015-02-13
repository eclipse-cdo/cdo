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
package org.eclipse.emf.cdo.explorer.ui;

import org.eclipse.emf.cdo.explorer.CDOExplorerUtil;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.explorer.ui.bundle.OM;
import org.eclipse.emf.cdo.explorer.ui.checkouts.CDOCheckoutLabelProvider;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.DecoratingStyledCellLabelProvider;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class DeleteObjectsDialog extends TitleAreaDialog
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
      String type = "Object";
      String types = "Objects";

      String title = "Delete " + (size == 1 ? type : types);
      getShell().setText(title);
      setTitle(title);
      setTitleImage(OM.getImage("icons/wiz/delete.gif"));
      setMessage("Are you sure you want to delete " + (size == 1 ? "this" : "these") + " " + size + " "
          + (size == 1 ? type : types).toLowerCase() + "?");

      Composite container = new Composite(area, SWT.NONE);
      container.setLayoutData(new GridData(GridData.FILL_BOTH));
      GridLayout containerGridLayout = new GridLayout();
      containerGridLayout.marginWidth = 10;
      containerGridLayout.marginHeight = 10;
      container.setLayout(containerGridLayout);

      final LabelProvider labelProvider = new LabelProvider();
      final ObjectToDelete[] input = new ObjectToDelete[objects.size()];

      for (int i = 0; i < input.length; i++)
      {
        input[i] = new ObjectToDelete(objects.get(i));
      }

      final CheckboxTreeViewer treeViewer = new CheckboxTreeViewer(container);
      treeViewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      treeViewer.setContentProvider(new ContentProvider());
      treeViewer.setLabelProvider(new DecoratingStyledCellLabelProvider(labelProvider, new LabelDecorator(), null));
      treeViewer.setInput(input);
      treeViewer.addCheckStateListener(new ICheckStateListener()
      {
        public void checkStateChanged(CheckStateChangedEvent event)
        {
          ObjectToDelete element = (ObjectToDelete)event.getElement();
          if (event.getChecked())
          {
            objects.add(element.getObject());
          }
          else
          {
            objects.remove(element.getObject());
          }

          int size = objects.size();
          if (size <= 1)
          {
            Button button = getButton(OK);
            button.setEnabled(size != 0);
          }
        }
      });

      parent.getDisplay().asyncExec(new Runnable()
      {
        @SuppressWarnings("deprecation")
        public void run()
        {
          treeViewer.setAllChecked(true);

          new Job("Compute paths")
          {
            @Override
            protected IStatus run(IProgressMonitor monitor)
            {
              for (ObjectToDelete element : input)
              {
                try
                {
                  if (element.computePath(checkout, labelProvider))
                  {
                    ViewerUtil.update(treeViewer, element);
                  }
                }
                catch (Exception ex)
                {
                  //$FALL-THROUGH$
                }
              }

              return Status.OK_STATUS;
            }
          }.schedule();
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

  /**
   * @author Eike Stepper
   */
  private static final class ObjectToDelete
  {
    private final EObject object;

    private String path;

    public ObjectToDelete(EObject object)
    {
      this.object = object;
    }

    public final EObject getObject()
    {
      return object;
    }

    public final String getPath()
    {
      return path;
    }

    public boolean computePath(CDOCheckout checkout, LabelProvider labelProvider)
    {
      LinkedList<EObject> nodes = CDOExplorerUtil.getPath(object);
      if (nodes != null)
      {
        nodes.removeLast();

        StringBuilder builder = new StringBuilder();
        boolean first = true;

        for (EObject node : nodes)
        {
          builder.append('/');

          if (first)
          {
            builder.append(checkout.getLabel());
            first = false;
          }
          else
          {
            builder.append(labelProvider.getText(node));
          }
        }

        path = builder.toString();
        return true;
      }

      return false;
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class ContentProvider implements ITreeContentProvider
  {
    private ObjectToDelete[] elements;

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
      elements = (ObjectToDelete[])newInput;
    }

    public void dispose()
    {
    }

    public Object[] getElements(Object element)
    {
      return elements;
    }

    public Object getParent(Object element)
    {
      return null;
    }

    public Object[] getChildren(Object element)
    {
      return getElements(element);
    }

    public boolean hasChildren(Object element)
    {
      return !(element instanceof ObjectToDelete);
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class LabelProvider extends CDOCheckoutLabelProvider
  {
    public LabelProvider()
    {
    }

    @Override
    public Image getImage(Object element)
    {
      if (element instanceof ObjectToDelete)
      {
        ObjectToDelete objectToDelete = (ObjectToDelete)element;
        return super.getImage(objectToDelete.getObject());
      }

      return super.getImage(element);
    }

    @Override
    public String getText(Object element)
    {
      if (element instanceof ObjectToDelete)
      {
        ObjectToDelete objectToDelete = (ObjectToDelete)element;
        return super.getText(objectToDelete.getObject());
      }

      return super.getText(element);
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class LabelDecorator extends BaseLabelDecorator
  {
    @Override
    public String decorateText(String text, Object element)
    {
      if (element instanceof ObjectToDelete)
      {
        ObjectToDelete objectToDelete = (ObjectToDelete)element;
        String path = objectToDelete.getPath();
        if (path != null)
        {
          text = path + "/" + text;
        }

        return text;
      }

      return super.decorateText(text, element);
    }
  }
}
