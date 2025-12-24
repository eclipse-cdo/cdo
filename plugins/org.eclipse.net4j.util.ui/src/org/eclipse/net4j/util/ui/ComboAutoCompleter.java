/*
 * Copyright (c) 2007, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.ui;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;

/**
 * @author Eike Stepper
 */
public class ComboAutoCompleter implements ModifyListener
{
  private ComboViewer viewer;

  public ComboAutoCompleter(ComboViewer viewer)
  {
    this.viewer = viewer;
  }

  public final ComboViewer getViewer()
  {
    return viewer;
  }

  @Override
  public final void modifyText(ModifyEvent e)
  {
    String text = getText();
    Object foundElement = findShortestLabel(text);
    if (foundElement != null)
    {
      setSelection(new StructuredSelection(foundElement));
    }
    else
    {
      // setSelection(StructuredSelection.EMPTY);
    }
  }

  protected void setSelection(StructuredSelection selection)
  {
    viewer.setSelection(selection);
  }

  private Object findShortestLabel(String prefix)
  {
    Object foundElement = null;
    String shortestLabel = null;
    ILabelProvider labelProvider = (ILabelProvider)viewer.getLabelProvider();
    IStructuredContentProvider contentProvider = (IStructuredContentProvider)viewer.getContentProvider();
    for (Object element : contentProvider.getElements(viewer.getInput()))
    {
      String label = labelProvider.getText(element);
      if (label.startsWith(prefix))
      {
        if (shortestLabel == null || label.length() < shortestLabel.length())
        {
          shortestLabel = label;
          foundElement = element;
        }
      }
    }

    return foundElement;
  }

  private String getText()
  {
    return viewer.getCombo().getText();
  }

  public static void attach(ComboViewer viewer, ComboAutoCompleter completer)
  {
    viewer.getCombo().addModifyListener(completer);
  }

  public static void attach(ComboViewer viewer)
  {
    viewer.getCombo().addModifyListener(new ComboAutoCompleter(viewer));
  }
}
