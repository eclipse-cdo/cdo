/*
 * Copyright (c) 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui.views;

import org.eclipse.emf.cdo.CDOElement;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewInvalidationEvent;

import org.eclipse.emf.internal.cdo.view.CDOViewImpl;
import org.eclipse.emf.internal.cdo.view.CDOViewImpl.LockStatesChangedEvent;

import org.eclipse.net4j.util.AdapterUtil;
import org.eclipse.net4j.util.HexUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.lifecycle.ILifecycleEvent;
import org.eclipse.net4j.util.lifecycle.ILifecycleEvent.Kind;
import org.eclipse.net4j.util.ui.StructuredContentProvider;
import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.actions.SafeAction;
import org.eclipse.net4j.util.ui.views.ContainerView;
import org.eclipse.net4j.util.ui.views.ItemProvider;

import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class CDOLockStatesView extends ViewPart implements ISelectionListener
{
  public static final String ID = "org.eclipse.emf.cdo.ui.CDOLockStatesView"; //$NON-NLS-1$

  private TableViewer viewer;

  public CDOLockStatesView()
  {
  }

  @Override
  public void createPartControl(Composite parent)
  {
    TableColumnLayout tableLayout = new TableColumnLayout();

    Composite tableComposite = new Composite(parent, SWT.NONE);
    tableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    tableComposite.setLayout(tableLayout);

    viewer = new TableViewer(tableComposite, SWT.SINGLE | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
    viewer.setContentProvider(new ContentProvider());

    TableViewerColumn objectViewerColumn = new TableViewerColumn(viewer, SWT.NONE);
    TableColumn objectColumn = objectViewerColumn.getColumn();
    objectColumn.setText("Object");
    tableLayout.setColumnData(objectColumn, new ColumnWeightData(20, 100, true));
    objectViewerColumn.setLabelProvider(new ColumnLabelProvider()
    {
      @Override
      public String getText(Object element)
      {
        @SuppressWarnings("unchecked")
        CDOObject object = ((Map.Entry<CDOObject, CDOLockState>)element).getKey();
        CDORevision revision = object.cdoRevision(false);
        if (revision != null)
        {
          return StringUtil.safe(revision);
        }

        return StringUtil.safe(object);
      }
    });

    TableViewerColumn lockStateViewerColumn = new TableViewerColumn(viewer, SWT.NONE);
    TableColumn lockStateColumn = lockStateViewerColumn.getColumn();
    lockStateColumn.setText("Lock State");
    tableLayout.setColumnData(lockStateColumn, new ColumnWeightData(80, 150, true));
    lockStateViewerColumn.setLabelProvider(new ColumnLabelProvider()
    {
      @Override
      public String getText(Object element)
      {
        @SuppressWarnings("unchecked")
        CDOLockState lockState = ((Map.Entry<CDOObject, CDOLockState>)element).getValue();
        int XXX;
        return StringUtil.safe(lockState) + "@" + HexUtil.identityHashCode(lockState);
      }
    });

    IViewSite site = getViewSite();
    site.setSelectionProvider(viewer);

    IWorkbenchPage page = site.getPage();
    selectionChanged(null, page.getSelection());
    page.addSelectionListener(this);

    IActionBars bars = site.getActionBars();
    IToolBarManager toolBarManager = bars.getToolBarManager();
    toolBarManager.add(new SafeAction("Refresh", ContainerView.getRefreshImageDescriptor())
    {
      @Override
      protected void safeRun() throws Exception
      {
        viewer.refresh();
      }
    });
  }

  @Override
  public void dispose()
  {
    getSite().getPage().removeSelectionListener(this);
    super.dispose();
  }

  @Override
  public void setFocus()
  {
    viewer.getControl().setFocus();
  }

  @Override
  public void selectionChanged(IWorkbenchPart part, ISelection selection)
  {
    CDOViewImpl view = getView(selection);
    if (view != null && view.isActive())
    {
      viewer.setInput(view);
    }
  }

  private CDOViewImpl getView(ISelection selection)
  {
    if (selection instanceof IStructuredSelection)
    {
      IStructuredSelection ssel = (IStructuredSelection)selection;
      for (Iterator<?> it = ssel.iterator(); it.hasNext();)
      {
        Object element = it.next();
        if (element instanceof CDOElement)
        {
          element = ((CDOElement)element).getDelegate();
        }

        if (element instanceof Notifier)
        {
          Notifier notifier = (Notifier)element;

          CDOView view = CDOUtil.getView(notifier);
          if (view instanceof CDOViewImpl)
          {
            return (CDOViewImpl)view;
          }
        }

        CDOView view = AdapterUtil.adapt(element, CDOView.class);
        if (view instanceof CDOViewImpl)
        {
          return (CDOViewImpl)view;
        }
      }
    }

    return null;
  }

  /**
   * @author Eike Stepper
   */
  public class ContentProvider extends StructuredContentProvider<CDOViewImpl>
  {
    public ContentProvider()
    {
    }

    @Override
    protected void connectInput(CDOViewImpl view)
    {
      view.addListener(this);
    }

    @Override
    protected void disconnectInput(CDOViewImpl view)
    {
      view.removeListener(this);
    }

    @Override
    public void notifyEvent(IEvent event)
    {
      if (event instanceof ILifecycleEvent)
      {
        ILifecycleEvent e = (ILifecycleEvent)event;
        if (e.getKind() == Kind.DEACTIVATED)
        {
          UIUtil.asyncExec(() -> getViewer().setInput(null));
        }
      }
      else if (event instanceof CDOViewInvalidationEvent)
      {
        refreshViewer(true);
      }
      else if (event instanceof LockStatesChangedEvent)
      {
        refreshViewer(true);
      }
    }

    @Override
    public Object[] getElements(Object inputElement)
    {
      CDOViewImpl view = getInput();
      if (view == null)
      {
        return ItemProvider.NO_ELEMENTS;
      }

      Set<Map.Entry<CDOObject, CDOLockState>> entrySet = view.getLockStates().entrySet();

      @SuppressWarnings("unchecked")
      Map.Entry<CDOObject, CDOLockState>[] elements = entrySet.toArray(new Map.Entry[entrySet.size()]);
      Arrays.sort(elements, Comparator.comparing(Map.Entry::getKey, Comparator.comparing(CDOObject::cdoID)));
      return elements;
    }
  }
}
