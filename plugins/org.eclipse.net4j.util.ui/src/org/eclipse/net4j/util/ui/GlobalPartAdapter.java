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
package org.eclipse.net4j.util.ui;

import org.eclipse.net4j.util.collection.ConcurrentArray;
import org.eclipse.net4j.util.internal.ui.bundle.OM;

import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import java.util.function.Consumer;

/**
 * @author Eike Stepper
 * @since 3.10
 */
public class GlobalPartAdapter implements IWindowListener, IPageListener, IPartListener2
{
  private static final ConcurrentArray<GlobalPartAdapter> adapters = new ConcurrentArray<GlobalPartAdapter>()
  {
    @Override
    protected GlobalPartAdapter[] newArray(int length)
    {
      return new GlobalPartAdapter[length];
    }
  };

  public GlobalPartAdapter()
  {
    adapters.add(this);
  }

  public void dispose()
  {
    adapters.remove(this);
  }

  @Override
  public void windowActivated(IWorkbenchWindow window)
  {
    // Subclasses may override.
  }

  @Override
  public void windowDeactivated(IWorkbenchWindow window)
  {
    // Subclasses may override.
  }

  @Override
  public void windowClosed(IWorkbenchWindow window)
  {
    // Subclasses may override.
  }

  @Override
  public void windowOpened(IWorkbenchWindow window)
  {
    // Subclasses may override.
  }

  @Override
  public void pageActivated(IWorkbenchPage page)
  {
    // Subclasses may override.
  }

  @Override
  public void pageClosed(IWorkbenchPage page)
  {
    // Subclasses may override.
  }

  @Override
  public void pageOpened(IWorkbenchPage page)
  {
    // Subclasses may override.
  }

  @Override
  public void partActivated(IWorkbenchPartReference partRef)
  {
    // Subclasses may override.
  }

  @Override
  public void partBroughtToTop(IWorkbenchPartReference partRef)
  {
    // Subclasses may override.
  }

  @Override
  public void partClosed(IWorkbenchPartReference partRef)
  {
    // Subclasses may override.
  }

  @Override
  public void partDeactivated(IWorkbenchPartReference partRef)
  {
    // Subclasses may override.
  }

  @Override
  public void partOpened(IWorkbenchPartReference partRef)
  {
    // Subclasses may override.
  }

  @Override
  public void partHidden(IWorkbenchPartReference partRef)
  {
    // Subclasses may override.
  }

  @Override
  public void partVisible(IWorkbenchPartReference partRef)
  {
    // Subclasses may override.
  }

  @Override
  public void partInputChanged(IWorkbenchPartReference partRef)
  {
    // Subclasses may override.
  }

  private static void notifyAdapters(Consumer<GlobalPartAdapter> consumer)
  {
    GlobalPartAdapter[] array = adapters.get();
    if (array != null)
    {
      for (int i = 0; i < array.length; i++)
      {
        GlobalPartAdapter adapter = array[i];

        try
        {
          consumer.accept(adapter);
        }
        catch (Exception ex)
        {
          OM.LOG.error(ex);
        }
      }
    }
  }

  static
  {
    IPartListener2 partListener = new IPartListener2()
    {
      @Override
      public void partOpened(IWorkbenchPartReference partRef)
      {
        notifyAdapters(a -> a.partOpened(partRef));
      }

      @Override
      public void partClosed(IWorkbenchPartReference partRef)
      {
        notifyAdapters(a -> a.partClosed(partRef));
      }

      @Override
      public void partActivated(IWorkbenchPartReference partRef)
      {
        notifyAdapters(a -> a.partActivated(partRef));
      }

      @Override
      public void partDeactivated(IWorkbenchPartReference partRef)
      {
        notifyAdapters(a -> a.partDeactivated(partRef));
      }

      @Override
      public void partBroughtToTop(IWorkbenchPartReference partRef)
      {
        notifyAdapters(a -> a.partBroughtToTop(partRef));
      }

      @Override
      public void partHidden(IWorkbenchPartReference partRef)
      {
        notifyAdapters(a -> a.partHidden(partRef));
      }

      @Override
      public void partVisible(IWorkbenchPartReference partRef)
      {
        notifyAdapters(a -> a.partVisible(partRef));
      }

      @Override
      public void partInputChanged(IWorkbenchPartReference partRef)
      {
        notifyAdapters(a -> a.partInputChanged(partRef));
      }
    };

    IPageListener pageListener = new IPageListener()
    {
      @Override
      public void pageOpened(IWorkbenchPage page)
      {
        page.addPartListener(partListener);
        notifyAdapters(a -> a.pageOpened(page));
      }

      @Override
      public void pageClosed(IWorkbenchPage page)
      {
        page.removePartListener(partListener);
        notifyAdapters(a -> a.pageClosed(page));
      }

      @Override
      public void pageActivated(IWorkbenchPage page)
      {
        notifyAdapters(a -> a.pageActivated(page));
      }
    };

    IWindowListener windowListener = new IWindowListener()
    {
      @Override
      public void windowOpened(IWorkbenchWindow window)
      {
        window.addPageListener(pageListener);
        notifyAdapters(a -> a.windowOpened(window));
      }

      @Override
      public void windowClosed(IWorkbenchWindow window)
      {
        window.removePageListener(pageListener);
        notifyAdapters(a -> a.windowOpened(window));
      }

      @Override
      public void windowActivated(IWorkbenchWindow window)
      {
        notifyAdapters(a -> a.windowClosed(window));
      }

      @Override
      public void windowDeactivated(IWorkbenchWindow window)
      {
        notifyAdapters(a -> a.windowDeactivated(window));
      }
    };

    IWorkbench workbench = PlatformUI.getWorkbench();
    workbench.addWindowListener(windowListener);
  }
}
