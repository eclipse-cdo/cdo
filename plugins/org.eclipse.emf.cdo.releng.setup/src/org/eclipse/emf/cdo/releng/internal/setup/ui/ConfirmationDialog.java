/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.internal.setup.ui;

import org.eclipse.emf.cdo.releng.internal.setup.Activator;
import org.eclipse.emf.cdo.releng.internal.setup.util.EMFUtil;
import org.eclipse.emf.cdo.releng.setup.Branch;
import org.eclipse.emf.cdo.releng.setup.Project;
import org.eclipse.emf.cdo.releng.setup.ScopeRoot;
import org.eclipse.emf.cdo.releng.setup.Setup;
import org.eclipse.emf.cdo.releng.setup.SetupTask;
import org.eclipse.emf.cdo.releng.setup.util.UIUtil;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class ConfirmationDialog extends AbstractSetupDialog
{
  private final Map<ScopeRoot, List<SetupTask>> map = new HashMap<ScopeRoot, List<SetupTask>>();

  private final Setup setup;

  private final EList<SetupTask> neededSetupTasks;

  private CheckboxTreeViewer viewer;

  private TreeViewer childrenViewer;

  private PropertiesViewer propertiesViewer;

  private ComposedAdapterFactory adapterFactory = EMFUtil.createAdapterFactory();

  public ConfirmationDialog(Shell parentShell, Setup setup, EList<SetupTask> neededSetupTasks)
  {
    super(parentShell, "Confirm Setup Tasks", 800, 700, Activator.getDefault().getBundle());
    this.setup = setup;
    this.neededSetupTasks = neededSetupTasks;

    for (SetupTask task : neededSetupTasks)
    {
      ScopeRoot scope = getScope(task);

      List<SetupTask> list = map.get(scope);
      if (list == null)
      {
        list = new ArrayList<SetupTask>();
        map.put(scope, list);
      }

      list.add(task);
    }
  }

  @Override
  protected String getDefaultMessage()
  {
    return "Review the set of setup tasks to be performed.";
  }

  @Override
  protected void createUI(Composite parent)
  {
    SashForm horizontalSash = new SashForm(parent, SWT.HORIZONTAL);
    UIUtil.grabVertical(UIUtil.applyGridData(horizontalSash));

    fillCheckPane(horizontalSash);

    SashForm verticalSash = new SashForm(horizontalSash, SWT.VERTICAL);

    fillChildrenPane(verticalSash);

    propertiesViewer = new PropertiesViewer(verticalSash, SWT.NONE);

    connectMasterDetail(viewer, childrenViewer);
    connectMasterDetail(viewer, propertiesViewer);
    connectMasterDetail(childrenViewer, propertiesViewer);

    horizontalSash.setWeights(new int[] { 1, 2 });

    viewer.getControl().getDisplay().asyncExec(new Runnable()
    {
      public void run()
      {
        viewer.expandAll();
        for (ScopeRoot scope : map.keySet())
        {
          viewer.setSubtreeChecked(scope, true);
        }
      }
    });
  }

  @Override
  protected void okPressed()
  {
    Set<SetupTask> checkedTasks = getCheckedTasks();
    neededSetupTasks.retainAll(checkedTasks);
    super.okPressed();
  }

  private void fillCheckPane(Composite parent)
  {
    viewer = new CheckboxTreeViewer(parent, SWT.FULL_SELECTION | SWT.NO_SCROLL | SWT.V_SCROLL);

    final Tree tree = viewer.getTree();
    tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

    viewer.setContentProvider(new ITreeContentProvider()
    {
      public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
      {
      }

      public void dispose()
      {
      }

      public boolean hasChildren(Object element)
      {
        return !(element instanceof SetupTask);
      }

      public Object getParent(Object element)
      {
        if (element instanceof SetupTask)
        {
          SetupTask task = (SetupTask)element;
          return getScope(task);
        }

        return null;
      }

      public Object[] getElements(Object element)
      {
        return getChildren(element);
      }

      public Object[] getChildren(Object element)
      {
        if (element == map)
        {
          List<ScopeRoot> list = new ArrayList<ScopeRoot>(map.keySet());
          Collections.sort(list, new Comparator<ScopeRoot>()
          {
            public int compare(ScopeRoot o1, ScopeRoot o2)
            {
              return o1.getScope().compareTo(o2.getScope());
            }
          });

          return list.toArray();
        }

        if (element instanceof ScopeRoot)
        {
          ScopeRoot scope = (ScopeRoot)element;
          List<SetupTask> list = map.get(scope);
          return list.toArray();
        }

        return new Object[0];
      }
    });

    viewer.setLabelProvider(new AdapterFactoryLabelProvider(adapterFactory)
    {
      @Override
      public String getText(Object object)
      {
        if (object instanceof Branch)
        {
          return getText(((Branch)object).getProject()) + " " + super.getText(object);
        }

        return super.getText(object);
      }
    });

    viewer.setInput(map);
    viewer.addCheckStateListener(new ICheckStateListener()
    {
      public void checkStateChanged(CheckStateChangedEvent event)
      {
        final boolean checked = event.getChecked();
        final Object element = event.getElement();
        if (element instanceof ScopeRoot)
        {
          ScopeRoot scope = (ScopeRoot)element;
          for (SetupTask task : map.get(scope))
          {
            viewer.setChecked(task, checked);
          }

          viewer.expandToLevel(scope, 1);
        }
        else if (element instanceof SetupTask)
        {
          SetupTask task = (SetupTask)element;
          ScopeRoot scope = getScope(task);

          if (checked)
          {
            if (areAllTasksChecked(scope))
            {
              viewer.setChecked(scope, true);
            }
          }
          else
          {
            viewer.setChecked(scope, false);
          }
        }

        Button button = getButton(IDialogConstants.OK_ID);
        if (button != null)
        {
          button.setEnabled(!getCheckedTasks().isEmpty());
        }
      }
    });
  }

  private void fillChildrenPane(SashForm verticalSash)
  {
    childrenViewer = new TreeViewer(verticalSash, SWT.NO_SCROLL | SWT.V_SCROLL);
    childrenViewer.setLabelProvider(new AdapterFactoryLabelProvider(adapterFactory));
    childrenViewer.setContentProvider(new AdapterFactoryContentProvider(adapterFactory)
    {
      @Override
      public Object[] getElements(Object object)
      {
        List<Object> result = new ArrayList<Object>();
        for (Object child : super.getElements(object))
        {
          if (!(child instanceof SetupTask))
          {
            result.add(child);
          }
        }

        return result.toArray();
      }
    });

    final Tree tree = childrenViewer.getTree();
    tree.setHeaderVisible(true);

    final TreeColumn column = new TreeColumn(tree, SWT.NONE);
    column.setText("Nested Elements");
    column.setWidth(600);

    final ControlAdapter columnResizer = new ControlAdapter()
    {
      @Override
      public void controlResized(ControlEvent e)
      {
        Point size = tree.getSize();
        ScrollBar bar = tree.getVerticalBar();
        if (bar != null && bar.isVisible())
        {
          size.x -= bar.getSize().x;
        }

        column.setWidth(size.x);
      }
    };

    tree.addControlListener(columnResizer);
    tree.getDisplay().asyncExec(new Runnable()
    {
      public void run()
      {
        columnResizer.controlResized(null);
      }
    });

    childrenViewer.setInput(new Object());
  }

  private void connectMasterDetail(final TreeViewer master, final Viewer detail)
  {
    master.addSelectionChangedListener(new ISelectionChangedListener()
    {
      public void selectionChanged(SelectionChangedEvent event)
      {
        if (detail != null)
        {
          Object selection = ((IStructuredSelection)master.getSelection()).getFirstElement();
          detail.setInput(selection);
        }
      }
    });
  }

  private ScopeRoot getScope(SetupTask task)
  {
    ScopeRoot scope = task.getScopeRoot();
    if (scope instanceof Project)
    {
      scope = setup.getBranch();
    }

    return scope;
  }

  private Set<SetupTask> getCheckedTasks()
  {
    Set<SetupTask> tasks = new HashSet<SetupTask>();
    for (Object object : viewer.getCheckedElements())
    {
      if (object instanceof SetupTask)
      {
        SetupTask task = (SetupTask)object;
        tasks.add(task);
      }
    }

    return tasks;
  }

  private boolean areAllTasksChecked(ScopeRoot scope)
  {
    for (SetupTask t : map.get(scope))
    {
      if (!viewer.getChecked(t))
      {
        return false;
      }
    }

    return true;
  }
}
