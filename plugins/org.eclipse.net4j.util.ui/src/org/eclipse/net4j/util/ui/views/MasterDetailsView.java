/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.ui.views;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.internal.ui.bundle.OM;
import org.eclipse.net4j.util.internal.ui.messages.Messages;
import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.actions.SafeAction;
import org.eclipse.net4j.util.ui.actions.SashLayoutAction;
import org.eclipse.net4j.util.ui.widgets.CoolBarComposite;
import org.eclipse.net4j.util.ui.widgets.SashComposite;

import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public abstract class MasterDetailsView extends MultiViewersView
{
  private StructuredViewer master;

  private CTabFolder detailsFolder;

  private Object currentMasterElement = new Object();

  private String[] detailTitles;

  private CTabItem[] detailItems;

  private StructuredViewer[] details;

  private int currentDetailIndex;

  private SashComposite sash;

  public MasterDetailsView()
  {
  }

  public StructuredViewer getMaster()
  {
    return master;
  }

  public StructuredViewer[] getDetails()
  {
    return details;
  }

  public String[] getDetailTitles()
  {
    return detailTitles;
  }

  public Object getCurrentMasterElement()
  {
    return currentMasterElement;
  }

  public int getCurrentDetailIndex()
  {
    return currentDetailIndex;
  }

  public String getCurrentDetailTitle()
  {
    if (detailTitles == null || currentDetailIndex == -1)
    {
      return null;
    }

    return detailTitles[currentDetailIndex];
  }

  @Override
  protected Control createUI(Composite parent)
  {
    sash = new SashComposite(parent, SWT.NONE, 10, 50, true)
    {
      @Override
      protected Control createControl1(Composite parent)
      {
        master = createMaster(parent);
        return master.getControl();
      }

      @Override
      protected Control createControl2(Composite parent)
      {
        return new CoolBarComposite(parent, SWT.NONE)
        {
          @Override
          protected Control createUI(Composite parent)
          {
            // Styles: CLOSE, TOP, BOTTOM, FLAT, BORDER, SINGLE, MULTI
            detailsFolder = new CTabFolder(parent, SWT.BOTTOM | SWT.FLAT);
            detailsFolder.setLayoutData(UIUtil.createGridData());
            adjustDetails(null);
            detailsFolder.addSelectionListener(new SelectionAdapter()
            {
              @Override
              public void widgetSelected(SelectionEvent e)
              {
                String title = detailsFolder.getSelection().getText();
                int detailIndex = indexOf(detailItems, title);
                if (detailIndex != currentDetailIndex)
                {
                  currentDetailIndex = detailIndex;
                  updateCoolBar();
                }
              }
            });

            return detailsFolder;
          }

          @Override
          protected void fillCoolBar(IContributionManager manager)
          {
            MasterDetailsView.this.fillCoolBar(manager);
            manager.add(new RefreshAction());
          }
        };
      }
    };

    sash.setVertical(true);
    setCurrentViewer(master);
    master.addSelectionChangedListener(new ISelectionChangedListener()
    {
      @Override
      public void selectionChanged(SelectionChangedEvent event)
      {
        try
        {
          masterSelectionChanged(event);
        }
        catch (Error ex)
        {
          OM.LOG.error(ex);
          throw ex;
        }
        catch (RuntimeException ex)
        {
          OM.LOG.error(ex);
          throw ex;
        }
      }
    });

    return sash;
  }

  @Override
  protected void fillLocalPullDown(IMenuManager manager)
  {
    super.fillLocalPullDown(manager);
    manager.add(new Separator("group1")); //$NON-NLS-1$
    manager.add(new SashLayoutAction.LayoutMenu(sash));
    manager.add(new Separator("group2")); //$NON-NLS-1$
    manager.add(new RefreshAction());
  }

  protected void masterSelectionChanged(SelectionChangedEvent event)
  {
    IStructuredSelection selection = (IStructuredSelection)master.getSelection();
    Object masterElement = selection.getFirstElement();
    adjustDetails(masterElement);
  }

  protected void adjustDetails(Object masterElement)
  {
    if (ObjectUtil.equals(masterElement, currentMasterElement))
    {
      return;
    }

    // Temporarily remember old values
    String oldDetailTitle = detailItems == null || currentDetailIndex < 0 ? null : detailItems[currentDetailIndex].getText();
    StructuredViewer[] oldDetails = details;
    CTabItem[] oldDetailItems = detailItems;

    // Initialize new values
    detailTitles = getDetailTitles(masterElement);
    details = new StructuredViewer[detailTitles.length];
    detailItems = new CTabItem[detailTitles.length];

    for (int i = 0; i < detailTitles.length; i++)
    {
      String title = detailTitles[i];
      int oldIndex = indexOf(oldDetailItems, title);
      if (oldIndex != -1)
      {
        details[i] = oldDetails[oldIndex];
        oldDetailItems[oldIndex].setControl(null);
        oldDetailItems[oldIndex].dispose();
        oldDetailItems[oldIndex] = null;
      }
      else
      {
        details[i] = createDetail(detailsFolder, title);
      }

      detailItems[i] = new CTabItem(detailsFolder, SWT.NONE, i);
      detailItems[i].setText(title);
      detailItems[i].setControl(details[i].getControl());
    }

    // Cleanup
    if (oldDetailItems != null)
    {
      for (CTabItem item : oldDetailItems)
      {
        if (item != null)
        {
          item.dispose();
        }
      }
    }

    currentMasterElement = masterElement;
    for (StructuredViewer viewer : details)
    {
      setDetailInput(viewer, masterElement);
    }

    currentDetailIndex = indexOf(detailItems, oldDetailTitle);
    if (currentDetailIndex == -1 && details.length > 0)
    {
      currentDetailIndex = 0;
    }

    if (currentDetailIndex != -1)
    {
      detailsFolder.setSelection(currentDetailIndex);
    }
  }

  protected void setDetailInput(StructuredViewer viewer, Object input)
  {
    if (input != viewer.getInput())
    {
      viewer.setInput(input);
    }
  }

  protected void fillCoolBar(IContributionManager manager)
  {
  }

  protected abstract StructuredViewer createMaster(Composite parent);

  protected abstract StructuredViewer createDetail(Composite parent, String title);

  protected abstract String[] getDetailTitles(Object masterElement);

  public static int indexOf(CTabItem[] items, String title)
  {
    if (items != null)
    {
      for (int i = 0; i < items.length; i++)
      {
        CTabItem item = items[i];
        if (item != null && ObjectUtil.equals(item.getText(), title))
        {
          return i;
        }
      }
    }

    return -1;
  }

  /**
   * @author Eike Stepper
   */
  protected final class RefreshAction extends SafeAction
  {
    public RefreshAction()
    {
      super(Messages.getString("MasterDetailsView_2"), Messages.getString("MasterDetailsView_3"), //$NON-NLS-1$ //$NON-NLS-2$
          OM.getImageDescriptor("icons/full/etool16/refresh.gif")); //$NON-NLS-1$
    }

    @Override
    protected void safeRun() throws Exception
    {
      StructuredViewer viewer = getCurrentViewer();
      if (viewer != null)
      {
        viewer.refresh();
      }
    }
  }
}
