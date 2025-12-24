/*
 * Copyright (c) 2009-2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *
 *  Initial Publication:
 *    Eclipse Magazin - http://www.eclipse-magazin.de
 */
package org.gastro.rcp.internal.department;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.ref.ReferenceValueMap;

import org.eclipse.nebula.widgets.gallery.DefaultGalleryItemRenderer;
import org.eclipse.nebula.widgets.gallery.Gallery;
import org.eclipse.nebula.widgets.gallery.GalleryItem;
import org.eclipse.nebula.widgets.gallery.NoGroupRenderer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import com.swtdesigner.ResourceManager;
import com.swtdesigner.SWTResourceManager;

import org.gastro.inventory.Department;
import org.gastro.inventory.Employee;
import org.gastro.rcp.IModel;

import java.util.Map;

/**
 * @author Eike Stepper
 */
public class EmployeesView extends ViewPart
{
  public static final String ID = "org.gastro.rcp.employees.view";

  private static final Map<String, String> fakeImages = new ReferenceValueMap.Soft<>();

  private static int fakeImageID;

  public EmployeesView()
  {
  }

  /**
   * Passing the focus request to the viewer's control.
   */
  @Override
  public void setFocus()
  {
  }

  /**
   * This is a callback that will allow us to create the viewer and initialize it.
   */
  @Override
  public void createPartControl(Composite parent)
  {
    Department department = (Department)IModel.INSTANCE.getStation();

    DefaultGalleryItemRenderer ir = new DefaultGalleryItemRenderer();
    ir.setShowRoundedSelectionCorners(true);

    Gallery gallery = new Gallery(parent, SWT.NONE);
    gallery.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
    gallery.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_BLUE));
    gallery.setFont(SWTResourceManager.getFont("Segoe UI", 16, SWT.BOLD));
    gallery.setGroupRenderer(new NoGroupRenderer());
    gallery.setItemRenderer(ir);

    GalleryItem group = new GalleryItem(gallery, SWT.NONE);
    for (Employee employee : department.getEmployees())
    {
      GalleryItem item = new GalleryItem(group, SWT.NONE);
      String imageName = getImageName(employee.getName());
      item.setImage(getCachedImage(imageName));
      item.setText(StringUtil.safe(employee.getName()));
    }
  }

  private String getImageName(String employee)
  {
    if (employee == null)
    {
      return null;
    }

    String name = fakeImages.get(employee);
    if (name != null)
    {
      return name;
    }

    for (;;)
    {
      name = "employee-" + ++fakeImageID;
      Image image = getCachedImage(name);
      if (image != null)
      {
        fakeImages.put(employee, name);
        return name;
      }

      fakeImageID = 0;
    }
  }

  private Image getCachedImage(String name)
  {
    return ResourceManager.getPluginImage("org.gastro.rcp.department", "images/" + name + ".png");
  }
}
