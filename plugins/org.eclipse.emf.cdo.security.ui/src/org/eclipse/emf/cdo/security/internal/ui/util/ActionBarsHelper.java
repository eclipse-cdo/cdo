/*
 * Copyright (c) 2013, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.emf.cdo.security.internal.ui.util;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;

import java.util.Map;

/**
 * A utility that manages the propagation of global actions from
 * some nested part of an editor to the editor's action bars.
 *
 * @author Christian W. Damus (CEA LIST)
 */
public class ActionBarsHelper
{
  private final IActionBars bars;

  private final Map<String, IAction> globalActions = new java.util.HashMap<String, IAction>();

  private final Map<String, IAction> previousActions = new java.util.HashMap<String, IAction>();

  public ActionBarsHelper(IActionBars actionBars)
  {
    bars = actionBars;
  }

  public ActionBarsHelper addGlobalAction(String actionID, IAction action)
  {
    globalActions.put(actionID, action);
    return this;
  }

  public ActionBarsHelper install(Viewer viewer)
  {
    return install(viewer.getControl(), viewer);
  }

  public ActionBarsHelper install(Control control)
  {
    return install(control, null);
  }

  private ActionBarsHelper install(Control control, final ISelectionProvider selectionProvider)
  {
    control.addFocusListener(new FocusListener()
    {
      @Override
      public void focusLost(FocusEvent e)
      {
        unsetGlobalActions();
        bars.updateActionBars();
      }

      @Override
      public void focusGained(FocusEvent e)
      {
        updateActions();
        setGlobalActions();
        bars.updateActionBars();
      }

      private void updateActions()
      {
        if (selectionProvider != null)
        {
          SelectionChangedEvent selectionEvent = null;

          for (IAction next : globalActions.values())
          {
            if (next instanceof ISelectionChangedListener)
            {
              if (selectionEvent == null)
              {
                selectionEvent = new SelectionChangedEvent(selectionProvider, selectionProvider.getSelection());
              }

              ((ISelectionChangedListener)next).selectionChanged(selectionEvent);
            }
          }
        }
      }
    });

    control.addDisposeListener(new DisposeListener()
    {
      @Override
      public void widgetDisposed(DisposeEvent e)
      {
        globalActions.clear();
        previousActions.clear();
      }
    });

    return this;
  }

  protected void setGlobalActions()
  {
    previousActions.clear();

    for (Map.Entry<String, IAction> next : globalActions.entrySet())
    {
      previousActions.put(next.getKey(), bars.getGlobalActionHandler(next.getKey()));
      bars.setGlobalActionHandler(next.getKey(), next.getValue());
    }
  }

  protected void unsetGlobalActions()
  {
    for (Map.Entry<String, IAction> next : previousActions.entrySet())
    {
      bars.setGlobalActionHandler(next.getKey(), next.getValue());
    }

    previousActions.clear();
  }
}
