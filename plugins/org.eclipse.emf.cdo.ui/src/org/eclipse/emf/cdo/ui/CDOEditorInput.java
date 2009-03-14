/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Victor Roldan Betancort - maintenance
 */
package org.eclipse.emf.cdo.ui;

import org.eclipse.emf.cdo.internal.ui.SharedIcons;
import org.eclipse.emf.cdo.internal.ui.bundle.OM;
import org.eclipse.emf.cdo.internal.ui.editor.CDOEditor;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.view.CDOAudit;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.ObjectUtil;

import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class CDOEditorInput extends PlatformObject implements IEditorInput
{
  private CDOView view;

  private boolean viewOwned;

  private String resourcePath;

  public CDOEditorInput(CDOView view, String resourcePath)
  {
    this(view, resourcePath, false);
  }

  public CDOEditorInput(CDOView view, String resourcePath, boolean viewOwned)
  {
    this.view = view;
    this.viewOwned = viewOwned;
    this.resourcePath = resourcePath;
  }

  public CDOView getView()
  {
    return view;
  }

  public boolean isViewOwned()
  {
    return viewOwned;
  }

  public String getResourcePath()
  {
    return resourcePath;
  }

  public boolean exists()
  {
    return true;
  }

  public ImageDescriptor getImageDescriptor()
  {
    switch (view.getViewType())
    {
    case TRANSACTION:
      return SharedIcons.getDescriptor(SharedIcons.OBJ_EDITOR);
    case READONLY:
      return SharedIcons.getDescriptor(SharedIcons.OBJ_EDITOR_READONLY);
    case AUDIT:
      return SharedIcons.getDescriptor(SharedIcons.OBJ_EDITOR_HISTORICAL);
    }

    return null;
  }

  public String getName()
  {
    if (resourcePath != null)
    {
      return new Path(resourcePath).lastSegment();
    }

    return view.getSession().repository().getName();
  }

  public IPersistableElement getPersistable()
  {
    return null;
  }

  public String getToolTipText()
  {
    if (view.isClosed())
    {
      return "View closed";
    }

    CDOSession session = view.getSession();
    String repositoryName = session.repository().getName();

    StringBuilder builder = new StringBuilder();
    builder.append(repositoryName);
    if (resourcePath != null)
    {
      builder.append(resourcePath);
    }

    builder.append(" [");
    builder.append(session.getSessionID());
    builder.append(":");
    builder.append(view.getViewID());
    builder.append("]");

    if (view.getViewType() != CDOView.Type.TRANSACTION)
    {
      builder.append(" readonly");
    }

    if (view instanceof CDOAudit)
    {
      builder.append(MessageFormat.format(" {0,date} {0,time}", ((CDOAudit)view).getTimeStamp()));
    }

    return builder.toString();
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj == this)
    {
      return true;
    }

    if (obj instanceof CDOEditorInput)
    {
      CDOEditorInput that = (CDOEditorInput)obj;
      return ObjectUtil.equals(view, that.view) && ObjectUtil.equals(resourcePath, that.resourcePath);
    }

    return false;
  }

  /**
   * @ADDED
   */
  public static void openEditor(final IWorkbenchPage page, final CDOView view, final String resourcePath)
  {
    Display display = page.getWorkbenchWindow().getShell().getDisplay();
    display.asyncExec(new Runnable()
    {
      public void run()
      {
        try
        {
          IEditorReference[] references = findEditor(page, view, resourcePath);
          if (references.length != 0)
          {
            IEditorPart editor = references[0].getEditor(true);
            page.activate(editor);
          }
          else
          {
            IEditorInput input = new CDOEditorInput(view, resourcePath);
            page.openEditor(input, CDOEditor.EDITOR_ID);
          }
        }
        catch (Exception ex)
        {
          OM.LOG.error(ex);
        }
      }
    });
  }

  /**
   * @ADDED
   */
  public static IEditorReference[] findEditor(IWorkbenchPage page, CDOView view, String resourcePath)
  {
    List<IEditorReference> result = new ArrayList<IEditorReference>();
    IEditorReference[] editorReferences = page.getEditorReferences();
    for (IEditorReference editorReference : editorReferences)
    {
      try
      {
        if (ObjectUtil.equals(editorReference.getId(), CDOEditor.EDITOR_ID))
        {
          IEditorInput editorInput = editorReference.getEditorInput();
          if (editorInput instanceof CDOEditorInput)
          {
            CDOEditorInput cdoInput = (CDOEditorInput)editorInput;
            if (cdoInput.getView() == view)
            {
              if (resourcePath == null || ObjectUtil.equals(cdoInput.getResourcePath(), resourcePath))
              {
                result.add(editorReference);
              }
            }
          }
        }
      }
      catch (PartInitException ex)
      {
        OM.LOG.error(ex);
      }
    }

    return result.toArray(new IEditorReference[result.size()]);
  }

  /**
   * @ADDED
   */
  public static void refreshEditors(IWorkbenchPage page, CDOView view)
  {
    IEditorReference[] references = findEditor(page, view, null);
    for (IEditorReference reference : references)
    {
      CDOEditor editor = (CDOEditor)reference.getEditor(false);
      if (editor != null)
      {
        editor.refreshViewer(null);
      }
    }
  }
}
