/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ui;

import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.emf.spi.cdo.FSMUtil;
import org.eclipse.emf.spi.cdo.InternalCDOObject;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;

/**
 * A {@link org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider AdapterFactoryLabelProvider} specialization
 * that decorates {@link org.eclipse.emf.cdo.CDOObject objects} with a color/font code according to their current
 * {@link org.eclipse.emf.cdo.CDOState state}.
 * 
 * @author Eike Stepper
 * @see org.eclipse.jface.viewers.IColorProvider
 * @see org.eclipse.jface.viewers.IFontProvider
 * @see org.eclipse.emf.cdo.CDOObject
 * @see org.eclipse.emf.cdo.CDOState
 */
public class CDOLabelProvider extends AdapterFactoryLabelProvider implements IColorProvider, IFontProvider
{
  private static final Color GRAY = UIUtil.getDisplay().getSystemColor(SWT.COLOR_GRAY);

  private static final Color RED = UIUtil.getDisplay().getSystemColor(SWT.COLOR_RED);

  private Font bold;

  private CDOView view;

  private TreeViewer viewer;

  /**
   * @since 2.0
   */
  public CDOLabelProvider(AdapterFactory adapterFactory, CDOView view, TreeViewer viewer)
  {
    super(adapterFactory);
    this.view = view;
    this.viewer = viewer;
    bold = UIUtil.getBoldFont(viewer.getControl());
  }

  /**
   * @since 2.0
   */
  public CDOView getView()
  {
    return view;
  }

  public TreeViewer getViewer()
  {
    return viewer;
  }

  @Override
  public void dispose()
  {
    bold.dispose();
    super.dispose();
  }

  @Override
  public void notifyChanged(final Notification notification)
  {
    super.notifyChanged(notification);

    try
    {
      viewer.getControl().getDisplay().asyncExec(new Runnable()
      {
        public void run()
        {
          try
          {
            viewer.refresh(notification.getNotifier(), true);
          }
          catch (Exception ignore)
          {
          }
        }
      });
    }
    catch (Exception ignore)
    {
    }
  }

  @Override
  public Color getBackground(Object object)
  {
    // Use default
    return null;
  }

  @Override
  public Color getForeground(Object object)
  {
    try
    {
      InternalCDOObject cdoObject = FSMUtil.adapt(object, view);
      switch (cdoObject.cdoState())
      {
      case PROXY:
        return GRAY;

      case CONFLICT:
        return RED;
      }
    }
    catch (RuntimeException ignore)
    {
    }

    // Use default
    return null;
  }

  @Override
  public Font getFont(Object object)
  {
    try
    {
      InternalCDOObject cdoObject = FSMUtil.adapt(object, view);
      switch (cdoObject.cdoState())
      {
      case NEW:
      case DIRTY:
      case CONFLICT:
        return bold;
      }
    }
    catch (RuntimeException ignore)
    {
    }

    // Use default
    return null;
  }
}
