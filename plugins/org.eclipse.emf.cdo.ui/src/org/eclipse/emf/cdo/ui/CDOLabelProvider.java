/*
 * Copyright (c) 2007-2013, 2015, 2016, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ui;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.security.CDOPermission;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.emf.spi.cdo.FSMUtil;
import org.eclipse.emf.spi.cdo.InternalCDOObject;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

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
  private static final Color COLOR_PERMISSION_NONE = UIUtil.getDisplay().getSystemColor(SWT.COLOR_GRAY);

  private static final Color COLOR_PERMISSION_READ = UIUtil.getDisplay().getSystemColor(SWT.COLOR_DARK_CYAN);

  private static final Color COLOR_CONFLICT = UIUtil.getDisplay().getSystemColor(SWT.COLOR_RED);

  private static final Image ERROR_IMAGE = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_ERROR_TSK);

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

    Control control = viewer.getControl();
    bold = UIUtil.getBoldFont(control);
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
        @Override
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
  public Image getImage(Object object)
  {
    try
    {
      return super.getImage(object);
    }
    catch (Exception ex)
    {
      return ERROR_IMAGE;
    }
  }

  @Override
  public String getText(Object object)
  {
    try
    {
      String text = super.getText(object);
      if (!StringUtil.isEmpty(text))
      {
        return text;
      }
    }
    catch (Exception ex)
    {
      //$FALL-THROUGH$
    }

    try
    {
      if (object instanceof EObject)
      {
        EObject eObject = (EObject)object;
        EClass eClass = eObject.eClass();
        String text = getText(eClass);
        if (!StringUtil.isEmpty(text))
        {
          return text;
        }
      }
    }
    catch (Exception ignore)
    {
      //$FALL-THROUGH$
    }

    return object.getClass().getSimpleName();
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
    return getColor(FSMUtil.adapt(object, view));
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

  /**
   * @since 4.3
   */
  public static Color getColor(CDOObject object)
  {
    try
    {
      if (object.cdoConflict())
      {
        return COLOR_CONFLICT;
      }

      CDOPermission permission = object.cdoPermission();
      switch (permission)
      {
      case NONE:
        return COLOR_PERMISSION_NONE;

      case READ:
        return COLOR_PERMISSION_READ;

      default:
        //$FALL-THROUGH$
      }
    }
    catch (RuntimeException ignore)
    {
    }

    // Use default
    return null;
  }
}
