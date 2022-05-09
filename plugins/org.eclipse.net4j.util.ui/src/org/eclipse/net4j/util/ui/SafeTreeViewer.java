/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.ui;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.Widget;

import java.util.function.Consumer;

/**
 * @author Eike Stepper
 * @since 3.15
 */
public class SafeTreeViewer extends TreeViewer
{
  private final Consumer<Exception> exceptionHandler;

  public SafeTreeViewer(Tree tree, Consumer<Exception> exceptionHandler)
  {
    super(tree);
    this.exceptionHandler = exceptionHandler;
  }

  public SafeTreeViewer(Composite parent, int style, Consumer<Exception> exceptionHandler)
  {
    super(parent, style);
    this.exceptionHandler = exceptionHandler;
  }

  @Override
  protected void doUpdateItem(Widget widget, Object element, boolean fullMap)
  {
    try
    {
      super.doUpdateItem(widget, element, fullMap);
    }
    catch (Exception ex)
    {
      if (exceptionHandler != null)
      {
        exceptionHandler.accept(ex);
      }
    }
  }

  @Override
  protected void doUpdateItem(Item item, Object element)
  {
    try
    {
      super.doUpdateItem(item, element);
    }
    catch (Exception ex)
    {
      if (exceptionHandler != null)
      {
        exceptionHandler.accept(ex);
      }
    }
  }

  @Override
  public boolean isExpandable(Object element)
  {
    try
    {
      return super.isExpandable(element);
    }
    catch (Exception ex)
    {
      if (exceptionHandler != null)
      {
        exceptionHandler.accept(ex);
      }

      return false;
    }
  }
}
