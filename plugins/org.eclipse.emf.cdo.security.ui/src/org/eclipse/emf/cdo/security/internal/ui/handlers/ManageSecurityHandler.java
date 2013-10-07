/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.emf.cdo.security.internal.ui.handlers;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.security.internal.ui.editor.CDOSecurityFormEditor;
import org.eclipse.emf.cdo.security.internal.ui.messages.Messages;
import org.eclipse.emf.cdo.security.ui.ISecurityManagementContext;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.ui.CDOEditorInput;
import org.eclipse.emf.cdo.ui.CDOEditorUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.statushandlers.StatusManager;

import java.util.Set;

/**
 * "Manage Security" command handler, which opens the Security Manager editor
 * in the context of the currently selected {@link CDOSession}, with the help
 * of an optional {@link ISecurityManagementContext} adapter.
 * 
 * @author Christian W. Damus (CEA LIST)
 */
public class ManageSecurityHandler extends AbstractHandler
{

  public ManageSecurityHandler()
  {
  }

  public Object execute(ExecutionEvent event) throws ExecutionException
  {
    ISelection selection = HandlerUtil.getCurrentSelection(event);
    CDOSession session = UIUtil.adaptElement(selection, CDOSession.class);
    if (session != null && !session.isClosed())
    {
      IWorkbenchPart part = HandlerUtil.getActivePart(event);
      if (part != null)
      {
        final IWorkbenchPage page = part.getSite().getPage();

        IEditorPart existing = findEditor(page, session);
        if (existing != null)
        {
          // Activate this editor
          page.activate(existing);
        }
        else
        {
          // Open a new security editor
          ISecurityManagementContext context = getContext(event);
          CDOView view = context.connect(session);
          if (view == null || view.isClosed())
          {
            MessageDialog.openWarning(HandlerUtil.getActiveShell(event), Messages.ManageSecurityHandler_0,
                Messages.ManageSecurityHandler_1);
          }
          else
          {
            try
            {
              CDOResource resource = context.getSecurityResource(view);
              if (resource == null)
              {
                MessageDialog.openWarning(HandlerUtil.getActiveShell(event), Messages.ManageSecurityHandler_0,
                    Messages.ManageSecurityHandler_2);
              }
              else
              {
                IEditorInput input = CDOEditorUtil.createCDOEditorInput(view, resource.getPath(), false);

                try
                {
                  IEditorPart editor = page.openEditor(input, CDOSecurityFormEditor.ID);
                  if (editor != null)
                  {
                    hookCloseListener(editor, context, view);
                    view = null; // Don't disconnect it until later
                  }
                }
                catch (PartInitException e)
                {
                  StatusManager.getManager().handle(e.getStatus(), StatusManager.SHOW);
                }
              }
            }
            finally
            {
              if (view != null)
              {
                context.disconnect(view);
              }
            }
          }
        }
      }
    }

    return null;
  }

  IEditorPart findEditor(IWorkbenchPage page, CDOSession session)
  {
    IEditorPart result = null;

    for (IEditorReference next : page.getEditorReferences())
    {
      if (CDOSecurityFormEditor.ID.equals(next.getId()))
      {
        IEditorPart candidate = next.getEditor(false);

        if (candidate != null)
        {
          IEditorInput input = candidate.getEditorInput();

          if (input instanceof CDOEditorInput)
          {
            CDOView view = ((CDOEditorInput)input).getView();

            if (view != null && !view.isClosed() && session.equals(view.getSession()))
            {
              result = candidate;
              break;
            }
          }
        }
      }
    }

    return result;
  }

  ISecurityManagementContext getContext(ExecutionEvent event)
  {
    ISecurityManagementContext result = null;

    IWorkbenchPart part = HandlerUtil.getActivePart(event);
    if (part != null)
    {
      result = (ISecurityManagementContext)part.getAdapter(ISecurityManagementContext.class);
    }

    if (result == null)
    {
      result = ISecurityManagementContext.DEFAULT;
    }

    return result;
  }

  private void hookCloseListener(final IEditorPart editor, final ISecurityManagementContext context, final CDOView view)
  {
    final IWorkbenchPage page = editor.getSite().getPage();

    page.addPartListener(new IPartListener()
    {

      private final IEditorInput input = editor.getEditorInput();

      private final Set<IEditorPart> openEditors = new java.util.HashSet<IEditorPart>();

      {
        openEditors.add(editor);
      }

      public void partClosed(IWorkbenchPart part)
      {
        openEditors.remove(part);
        if (openEditors.isEmpty())
        {
          // No more editors using this view
          context.disconnect(view);
          page.removePartListener(this);
        }
      }

      public void partOpened(IWorkbenchPart part)
      {
        if (part instanceof IEditorPart)
        {
          IEditorPart editor = (IEditorPart)part;
          if (input.equals(editor.getEditorInput()))
          {
            // The user opened the advanced-mode editor from the form editor
            openEditors.add(editor);
          }
        }
      }

      public void partDeactivated(IWorkbenchPart part)
      {
        // Pass
      }

      public void partBroughtToTop(IWorkbenchPart part)
      {
        // Pass
      }

      public void partActivated(IWorkbenchPart part)
      {
        // Pass
      }
    });
  }
}
