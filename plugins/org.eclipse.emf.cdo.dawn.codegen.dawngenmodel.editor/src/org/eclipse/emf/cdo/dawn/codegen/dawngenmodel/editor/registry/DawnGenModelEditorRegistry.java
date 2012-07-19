/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.editor.registry;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IEditorPart;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Martin Fluegge
 * @since 1.0
 */
public class DawnGenModelEditorRegistry
{
  private static final String EXTENSION_POINT_ID = "org.eclipse.emf.cdo.dawn.genmodel.ui.editors";

  public static DawnGenModelEditorRegistry instance = new DawnGenModelEditorRegistry();

  public List<EditorExtension> getRegisteredEditors() throws CoreException
  {
    List<EditorExtension> editors = new ArrayList<EditorExtension>();
    IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(EXTENSION_POINT_ID);
    for (IConfigurationElement e : config)
    {
      IEditorPart editorPart = (IEditorPart)e.createExecutableExtension("editor");
      String fileExtension = e.getAttribute("file_extension");
      editors.add(new EditorExtension(editorPart, fileExtension));
    }
    return editors;
  }

  /**
   * @author Martin Fluegge
   */
  public static class EditorExtension
  {
    private IEditorPart editorPart;

    private String fileExtension;

    public EditorExtension(IEditorPart editorPart, String fileExtension)
    {
      this.editorPart = editorPart;
      this.fileExtension = fileExtension;
    }

    public IEditorPart getEditorPart()
    {
      return editorPart;
    }

    public String getFileExtension()
    {
      return fileExtension;
    }
  }
}
