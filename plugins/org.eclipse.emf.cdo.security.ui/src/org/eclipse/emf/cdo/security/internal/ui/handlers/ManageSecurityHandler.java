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
import org.eclipse.net4j.util.ui.handlers.LongRunningHandler;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
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
public class ManageSecurityHandler extends LongRunningHandler
{
  private IWorkbenchPart part;

  private ISecurityManagementContext context;

  private CDOSession session;

  public ManageSecurityHandler()
  {
  }

  @Override
  protected void extractEventDetails(ExecutionEvent event)
  {
    super.extractEventDetails(event);

    part = HandlerUtil.getActivePart(event);
    context = getContext(event);
  }

  @Override
  protected void preRun() throws Exception
  {
    if (part == null)
    {
      // No workbench page available in which to open the editor
      cancel();
      return;
    }

    session = getSession();
    if (session != null && !session.isClosed())
    {
      final IWorkbenchPage page = part.getSite().getPage();

      IEditorPart existing = findEditor(page, session);
      if (existing != null)
      {
        // Activate this editor and we're done
        cancel();
        page.activate(existing);
      }
    }
  }

  protected CDOSession getSession()
  {
    return UIUtil.adaptElement(getSelection(), CDOSession.class);
  }

  @Override
  protected void doExecute(IProgressMonitor progressMonitor) throws Exception
  {
    // Open a new security editor
    final CDOView[] view = new CDOView[] { context.connect(session) };
    if (view[0] == null || view[0].isClosed())
    {
      showWarning(Messages.ManageSecurityHandler_0, Messages.ManageSecurityHandler_1);
      return;
    }

    try
    {
      final CDOResource resource = context.getSecurityResource(view[0]);
      if (resource == null)
      {
        showWarning(Messages.ManageSecurityHandler_0, Messages.ManageSecurityHandler_2);
      }
      else
      {
        UIUtil.getDisplay().syncExec(new Runnable()
        {

          public void run()
          {
            IEditorInput input = CDOEditorUtil.createCDOEditorInput(view[0], resource.getPath(), false);

            try
            {
              IEditorPart editor = part.getSite().getPage().openEditor(input, CDOSecurityFormEditor.ID);
              if (editor != null)
              {
                hookCloseListener(editor, context, view[0]);
                view[0] = null; // Don't disconnect it until later
              }
            }
            catch (PartInitException e)
            {
              StatusManager.getManager().handle(e.getStatus(), StatusManager.SHOW);
            }
          }
        });
      }
    }
    finally
    {
      if (view[0] != null)
      {
        context.disconnect(view[0]);
      }
    }
  }

  protected void showWarning(final String title, final String message)
  {
    UIUtil.getDisplay().syncExec(new Runnable()
    {

      public void run()
      {
        MessageDialog.openWarning(part.getSite().getShell(), title, message);
      }
    });
  }

  IEditorPart findEditor(IWorkbenchPage page, CDOSession session)
  {
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
              return candidate;
            }
          }
        }
      }
    }

    return null;
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
