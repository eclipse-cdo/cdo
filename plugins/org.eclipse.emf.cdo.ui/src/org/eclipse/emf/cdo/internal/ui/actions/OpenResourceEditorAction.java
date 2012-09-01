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

import org.eclipse.emf.cdo.common.lob.CDOClob;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceLeaf;
import org.eclipse.emf.cdo.eresource.CDOTextResource;
import org.eclipse.emf.cdo.internal.ui.bundle.OM;
import org.eclipse.emf.cdo.internal.ui.messages.Messages;
import org.eclipse.emf.cdo.ui.CDOEditorUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.io.IORuntimeException;

import org.eclipse.emf.ecore.resource.URIConverter;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.IWorkbenchPage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/**
 * @author Eike Stepper
 */
public final class OpenResourceEditorAction extends ResourceNodeAction
{
  private static final String TITLE = Messages.getString("OpenResourceEditorAction.0"); //$NON-NLS-1$

  private static final String TOOL_TIP = Messages.getString("OpenResourceEditorAction.1"); //$NON-NLS-1$

  private static final String FILE_TITLE = Messages.getString("OpenFileEditorAction.0"); //$NON-NLS-1$

  private static final String FILE_TOOL_TIP = Messages.getString("OpenFileEditorAction.1"); //$NON-NLS-1$

  public OpenResourceEditorAction(IWorkbenchPage page, CDOResourceLeaf resource)
  {
    super(page, resource instanceof CDOResource ? TITLE : FILE_TITLE, resource instanceof CDOResource ? TOOL_TIP
        : FILE_TOOL_TIP, null, resource);
  }

  @Override
  protected void doRun(IProgressMonitor progressMonitor) throws Exception
  {
    final CDOResourceLeaf resource = (CDOResourceLeaf)getResourceNode();
    final CDOView view = resource.cdoView();
    final String resourcePath = resource.getPath();
    final IWorkbenchPage page = getPage();

    if (resource instanceof CDOResource)
    {
      CDOEditorUtil.openEditor(page, view, resourcePath);
    }
    else if (resource instanceof CDOTextResource)
    {
      final IPath path = new Path(resourcePath);

      Display display = page.getWorkbenchWindow().getShell().getDisplay();
      display.asyncExec(new Runnable()
      {
        public void run()
        {
          try
          {
            IEditorInput input = new IStorageEditorInput()
            {
              public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter)
              {
                System.out.println("IStorageEditorInput: " + adapter);
                return Platform.getAdapterManager().getAdapter(this, adapter);
              }

              public String getToolTipText()
              {
                return path.toString();
              }

              public IPersistableElement getPersistable()
              {
                return null;
              }

              public String getName()
              {
                return path.lastSegment();
              }

              public ImageDescriptor getImageDescriptor()
              {
                return null;
              }

              public boolean exists()
              {
                return true;
              }

              public IStorage getStorage() throws CoreException
              {
                return new IStorage()
                {
                  public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter)
                  {
                    System.out.println("IStorage: " + adapter);
                    return Platform.getAdapterManager().getAdapter(this, adapter);
                  }

                  public boolean isReadOnly()
                  {
                    return false;
                  }

                  public String getName()
                  {
                    return path.lastSegment();
                  }

                  public IPath getFullPath()
                  {
                    return path;
                  }

                  public InputStream getContents() throws CoreException
                  {
                    try
                    {
                      CDOClob clob = ((CDOTextResource)resource).getContents();
                      if (clob == null)
                      {
                        return new ByteArrayInputStream(new byte[0]);
                      }

                      Reader reader = clob.getContents();
                      return new URIConverter.ReadableInputStream(reader);
                    }
                    catch (IOException ex)
                    {
                      throw new IORuntimeException(ex);
                    }
                  }
                };
              }
            };

            page.openEditor(input, "org.eclipse.ui.DefaultTextEditor");
          }
          catch (Exception ex)
          {
            OM.LOG.error(ex);
          }
        }
      });
    }
  }
}
