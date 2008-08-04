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
package org.eclipse.net4j.util.ui.views;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.internal.ui.bundle.OM;
import org.eclipse.net4j.util.ui.widgets.SashComposite;

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

  private int currentIndex;

  public MasterDetailsView()
  {
  }

  public StructuredViewer getMaster()
  {
    return master;
  }

  @Override
  protected Control createUI(Composite parent)
  {
    SashComposite sash = new SashComposite(parent, SWT.NONE, 10, 50, false)
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
        // Styles: CLOSE, TOP, BOTTOM, FLAT, BORDER, SINGLE, MULTI
        detailsFolder = new CTabFolder(parent, SWT.BOTTOM | SWT.FLAT);
        adjustDetails(null);
        detailsFolder.addSelectionListener(new SelectionAdapter()
        {
          @Override
          public void widgetSelected(SelectionEvent e)
          {
            String title = detailsFolder.getSelection().getText();
            currentIndex = indexOf(detailItems, title);
          }
        });

        return detailsFolder;
      }
    };

    setCurrentViewer(master);
    master.addSelectionChangedListener(new ISelectionChangedListener()
    {
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
    String oldDetailTitle = detailItems == null || currentIndex < 0 ? null : detailItems[currentIndex].getText();
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

    currentIndex = indexOf(detailItems, oldDetailTitle);
    if (currentIndex == -1 && details.length > 0)
    {
      currentIndex = 0;
    }

    if (currentIndex != -1)
    {
      detailsFolder.setSelection(currentIndex);
    }
  }

  protected void setDetailInput(StructuredViewer viewer, Object input)
  {
    if (input != viewer.getInput())
    {
      viewer.setInput(input);
    }
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
}
