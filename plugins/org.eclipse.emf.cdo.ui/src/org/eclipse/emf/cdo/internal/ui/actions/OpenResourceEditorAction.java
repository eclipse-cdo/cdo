/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Victor Roldan Betancort - maintenance
 */
package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.internal.ui.messages.Messages;
import org.eclipse.emf.cdo.ui.CDOEditorUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public final class OpenResourceEditorAction extends ResourceNodeAction
{
  private static final String TITLE = Messages.getString("OpenResourceEditorAction.0"); //$NON-NLS-1$

  private static final String TOOL_TIP = Messages.getString("OpenResourceEditorAction.1"); //$NON-NLS-1$

  public OpenResourceEditorAction(IWorkbenchPage page, CDOResourceNode resourceNode)
  {
    super(page, TITLE, TOOL_TIP, null, resourceNode);
  }

  @Override
  protected void doRun(IProgressMonitor progressMonitor) throws Exception
  {
    CDOView view = getResourceNode().cdoView();
    String resourcePath = getResourceNode().getPath();
    CDOEditorUtil.openEditor(getPage(), view, resourcePath);
  }
}
