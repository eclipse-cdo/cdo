/*
 * Copyright (c) 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.checkouts.actions;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.explorer.ui.bundle.OM;
import org.eclipse.emf.cdo.ui.shared.SharedIcons;
import org.eclipse.emf.cdo.ui.widgets.TimeSlider;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.AdapterUtil;
import org.eclipse.net4j.util.ui.widgets.ImageButton;
import org.eclipse.net4j.util.ui.widgets.StackComposite;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;

import java.util.Iterator;

/**
 * @author Eike Stepper
 */
public class CDOCheckoutDashboard extends Composite implements ISelectionListener
{
  private final ISelectionService selectionService;

  private final Label iconLabel;

  private final Label titleLabel;

  private final ImageButton closeButton;

  private final StackComposite stackComposite;

  private final InfoPage infoPage;

  private final ViewPage viewPage;

  private final TransactionPage transactionPage;

  private DashboardPage currentPage;

  private CDOView currentView;

  public CDOCheckoutDashboard(Composite parent, ISelectionService selectionService)
  {
    super(parent, SWT.NONE);
    this.selectionService = selectionService;

    GridLayout gridLayout = new GridLayout(4, false);
    gridLayout.marginWidth = 0;
    gridLayout.marginHeight = 0;
    setLayout(gridLayout);

    GridData gridData = new GridData(SWT.LEFT, SWT.BOTTOM, false, false);
    gridData.heightHint = 21;

    iconLabel = new Label(this, SWT.NONE);
    iconLabel.setLayoutData(gridData);

    titleLabel = new Label(this, SWT.NONE);
    titleLabel.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false));

    closeButton = new ImageButton(this, OM.getImage("icons/close_hover.gif"), OM.getImage("icons/close.gif"))
    {
      @Override
      protected void widgetSelected()
      {
        OM.PREF_DASHBOARD_HEIGHT.setValue(-OM.PREF_DASHBOARD_HEIGHT.getValue());
        CDOCheckoutDashboard.this.dispose();
      }
    };

    closeButton.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false, false));
    closeButton.setToolTipText("Close");

    Label space = new Label(this, SWT.NONE);
    space.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));

    stackComposite = new StackComposite(this, SWT.NONE);
    stackComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));

    infoPage = new InfoPage(this);
    viewPage = new ViewPage(this);
    transactionPage = new TransactionPage(this);
    stackComposite.setTopControl(infoPage);

    setSelection(selectionService.getSelection());
    selectionService.addSelectionListener(this);
  }

  @Override
  public void dispose()
  {
    selectionService.removeSelectionListener(this);
    super.dispose();
  }

  @Override
  public void selectionChanged(IWorkbenchPart part, ISelection selection)
  {
    setSelection(selection);
  }

  public void setSelection(ISelection selection)
  {
    DashboardPage oldPage = currentPage;

    CDOView view = getView(selection);
    if (view != currentView)
    {
      currentView = view;

      if (currentView != null)
      {
        if (isReadOnly(currentView))
        {
          transactionPage.setView(null);
          currentPage = viewPage;
        }
        else
        {
          viewPage.setView(null);
          currentPage = transactionPage;
        }

        currentPage.setView(currentView);
      }
      else
      {
        viewPage.setView(null);
        transactionPage.setView(null);
        currentPage = infoPage;
      }
    }

    if (currentPage != oldPage)
    {
      updateTitleArea(currentPage);
      stackComposite.setTopControl(currentPage);
    }
  }

  private boolean isReadOnly(CDOView view)
  {
    // Object checkout = view.properties().get(CDOCheckout.class.getName());
    // if (checkout instanceof CDOCheckout)
    // {
    // return ((CDOCheckout)checkout).isReadOnly();
    // }

    return view.isReadOnly();
  }

  private CDOView getView(ISelection selection)
  {
    if (selection instanceof IStructuredSelection)
    {
      IStructuredSelection ssel = (IStructuredSelection)selection;
      if (!ssel.isEmpty())
      {
        CDOView firstView = null;
        for (Iterator<?> it = ssel.iterator(); it.hasNext();)
        {
          Object element = it.next();

          CDOCheckout checkout = AdapterUtil.adapt(element, CDOCheckout.class);
          if (checkout != null)
          {
            CDOView view = checkout.getView();
            if (view != null)
            {
              element = view;
            }
          }

          EObject eObject = AdapterUtil.adapt(element, EObject.class);
          if (eObject != null)
          {
            CDOObject cdoObject = CDOUtil.getCDOObject(eObject, false);
            if (cdoObject != null)
            {
              CDOView view = cdoObject.cdoView();
              if (view != null)
              {
                element = view;
              }
            }
          }

          CDOView view = AdapterUtil.adapt(element, CDOView.class);
          if (view != null)
          {
            if (firstView == null)
            {
              firstView = view;
            }
            else
            {
              if (firstView != view)
              {
                return null;
              }
            }
          }
        }

        return firstView;
      }
    }

    return null;
  }

  private StackComposite getStackComposite()
  {
    return stackComposite;
  }

  private void updateTitleArea(DashboardPage page)
  {
    if (page == currentPage)
    {
      iconLabel.setImage(page.getIcon());
      titleLabel.setText(page.getTitle());
    }
  }

  /**
   * @author Eike Stepper
   */
  public static abstract class DashboardPage extends Composite
  {
    private final CDOCheckoutDashboard dashboard;

    private Image icon;

    private String title;

    private CDOView view;

    public DashboardPage(CDOCheckoutDashboard dashboard)
    {
      super(dashboard.getStackComposite(), SWT.NONE);
      this.dashboard = dashboard;
    }

    public final Image getIcon()
    {
      return icon;
    }

    public final void setIcon(Image icon)
    {
      if (this.icon != icon)
      {
        this.icon = icon;
        dashboard.updateTitleArea(this);
      }
    }

    public final String getTitle()
    {
      return title;
    }

    public final void setTitle(String title)
    {
      if (this.title != title)
      {
        this.title = title;
        dashboard.updateTitleArea(this);
      }
    }

    public final CDOView getView()
    {
      return view;
    }

    public final void setView(CDOView view)
    {
      if (view != this.view)
      {
        CDOView oldView = this.view;
        this.view = view;
        viewChanged(oldView, view);
      }
    }

    protected void viewChanged(CDOView oldView, CDOView newView)
    {
    }
  }

  /**
   * @author Eike Stepper
   */
  private static class InfoPage extends DashboardPage
  {
    public InfoPage(CDOCheckoutDashboard dashboard)
    {
      super(dashboard);
      setIcon(org.eclipse.net4j.ui.shared.SharedIcons.getImage(org.eclipse.net4j.ui.shared.SharedIcons.OBJ_INFO));
      setTitle("CDO Dashboard");
      setLayout(new FillLayout());

      Label label = new Label(this, SWT.WRAP);
      label.setForeground(getDisplay().getSystemColor(SWT.COLOR_DARK_BLUE));
      label.setText("This dashboard is context-sensitive. " + "Select checkouts or models to show view and transaction controls.");
    }
  }

  /**
   * @author Eike Stepper
   */
  private static class ViewPage extends DashboardPage
  {
    private final TimeSlider timeSlider;

    public ViewPage(CDOCheckoutDashboard dashboard)
    {
      super(dashboard);
      setIcon(SharedIcons.getImage(SharedIcons.OBJ_EDITOR_HISTORICAL));
      setTitle("View time:");
      setLayout(new FillLayout());

      timeSlider = new TimeSlider(this, SWT.HORIZONTAL);
    }

    @Override
    protected void viewChanged(CDOView oldView, CDOView newView)
    {
      super.viewChanged(oldView, newView);
      timeSlider.connect(newView, null);
    }
  }

  /**
   * @author Eike Stepper
   */
  private static class TransactionPage extends DashboardPage
  {
    private final Text commentText;

    private final Button promptButton;

    private final Button locksButton;

    public TransactionPage(CDOCheckoutDashboard dashboard)
    {
      super(dashboard);
      setIcon(SharedIcons.getImage(SharedIcons.OBJ_EDITOR));
      setTitle("Transaction commit comment:");

      GridLayout gridLayout = new GridLayout(2, false);
      gridLayout.marginWidth = 0;
      gridLayout.marginHeight = 0;
      setLayout(gridLayout);

      commentText = new Text(this, SWT.BORDER | SWT.WRAP | SWT.MULTI);
      commentText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

      promptButton = new Button(this, SWT.CHECK);
      promptButton.setText("Prompt");
      promptButton.setToolTipText("Prompt on commit");

      locksButton = new Button(this, SWT.CHECK);
      locksButton.setText("Release locks");
      locksButton.setToolTipText("Release locks on commit");
    }
  }
}
