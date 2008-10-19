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
package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.internal.ui.editor.CDOEditor;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public final class OpenResourceEditorAction extends ResourceNodeAction
{
  private static final String TITLE = "Open Editor";

  private static final String TOOL_TIP = "Open a CDO editor for this resource";

  public OpenResourceEditorAction(IWorkbenchPage page, String text, String toolTipText, ImageDescriptor image,
      CDOResourceNode resourceNode)
  {
    super(page, TITLE, TOOL_TIP, null, resourceNode);
  }

  @Override
  protected void doRun() throws Exception
  {
    CDOView view = getResourceNode().cdoView();
    String resourcePath = getResourceNode().getPath();
    CDOEditor.open(getPage(), view, resourcePath);
  }
}
