/*
 * Copyright (c) 2010-2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.ui.helper;

import org.eclipse.emf.cdo.dawn.spi.IDawnUIElement;

import org.eclipse.emf.ecore.resource.Resource;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.registry.EditorDescriptor;

/**
 * @author Martin Fluegge
 */
@SuppressWarnings("restriction")
public class EditorDescriptionHelper
{
  public static String getEditorIdForDawnEditor(String resourceName)
  {
    IEditorDescriptor editorDescriptor = getEditorDescriptorForDawnEditor(resourceName);
    if (editorDescriptor != null)
    {
      return editorDescriptor.getId();
    }
    return null;
  }

  public static IEditorDescriptor getEditorDescriptorForDawnEditor(String resourceName)
  {
    IEditorDescriptor[] editors = PlatformUI.getWorkbench().getEditorRegistry().getEditors(resourceName);

    for (IEditorDescriptor editorDescriptor : editors)
    {
      EditorDescriptor des = (EditorDescriptor)editorDescriptor;

      try
      {
        IEditorPart editor = des.createEditor();
        if (editor instanceof IDawnUIElement)
        {
          return editorDescriptor;
        }
      }
      catch (CoreException ex)
      {
        throw new RuntimeException(ex);
      }
    }
    return null;
  }

  public static IEditorDescriptor getEditorDescriptorFromFirstEditor(String resourceName)
  {
    IEditorDescriptor[] editors = PlatformUI.getWorkbench().getEditorRegistry().getEditors(resourceName);
    if (editors.length > 0)
    {
      return editors[0];
    }

    return null;
  }

  public static IEditorDescriptor getEditorDescriptorForDawnEditor(Resource resource)
  {
    return getEditorDescriptorForDawnEditor(resource.getURI().lastSegment());
  }

  public static Image getImageForEditor(String resourceName)
  {
    IEditorDescriptor editorDescriptor = getEditorDescriptorForDawnEditor(resourceName);

    if (editorDescriptor == null)
    {
      editorDescriptor = getEditorDescriptorFromFirstEditor(resourceName);
    }

    if (editorDescriptor != null)
    {
      return editorDescriptor.getImageDescriptor().createImage();
    }

    return null;
  }
}
