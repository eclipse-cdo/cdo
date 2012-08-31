/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui.views;

import org.eclipse.emf.cdo.common.lob.CDOClob;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOTextResource;
import org.eclipse.emf.cdo.internal.ui.actions.OpenSessionAction;
import org.eclipse.emf.cdo.internal.ui.bundle.OM;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.ui.CDOEditorUtil;
import org.eclipse.emf.cdo.ui.CDOItemProvider;

import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.io.IORuntimeException;
import org.eclipse.net4j.util.ui.views.ContainerItemProvider;
import org.eclipse.net4j.util.ui.views.ContainerView;
import org.eclipse.net4j.util.ui.views.IElementFilter;

import org.eclipse.emf.ecore.resource.URIConverter;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.IWorkbenchPage;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class CDOSessionsView extends ContainerView
{
  public final static String ID = "org.eclipse.emf.cdo.ui.CDOSessionsView"; //$NON-NLS-1$

  private static Map<String, ResourceOpener> resourceOpeners = Collections
      .synchronizedMap(new HashMap<String, ResourceOpener>());

  private OpenSessionAction openSessionAction;

  public CDOSessionsView()
  {
  }

  @Override
  protected Control createUI(Composite parent)
  {
    openSessionAction = new OpenSessionAction(getViewSite().getPage());
    return super.createUI(parent);
  }

  @Override
  protected IManagedContainer getContainer()
  {
    return IPluginContainer.INSTANCE;
  }

  @Override
  protected ContainerItemProvider<IContainer<Object>> createContainerItemProvider()
  {
    return new CDOItemProvider(getSite().getPage(), new IElementFilter()
    {
      public boolean filter(Object element)
      {
        return element instanceof CDOSession;
      }
    });
  }

  @Override
  protected void fillLocalToolBar(IToolBarManager manager)
  {
    manager.add(openSessionAction);
    super.fillLocalToolBar(manager);
  }

  @Override
  protected void doubleClicked(Object object)
  {
    final IWorkbenchPage page = getSite().getPage();

    if (object instanceof CDOResource)
    {
      CDOResource resource = (CDOResource)object;
      String path = resource.getPath();

      String extension = new Path(path).getFileExtension();
      ResourceOpener opener = resourceOpeners.get(extension);
      if (opener != null)
      {
        opener.openResource(page, resource);
      }
      else
      {
        CDOEditorUtil.openEditor(page, resource.cdoView(), path);
      }
    }
    else if (object instanceof CDOTextResource)
    {
      final CDOTextResource resource = (CDOTextResource)object;
      final IPath path = new Path(resource.getPath());

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
                      CDOClob clob = resource.getContents();
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

            page.openEditor(input, CDOEditorUtil.getEditorID());
          }
          catch (Exception ex)
          {
            OM.LOG.error(ex);
          }
        }
      });
    }
    else
    {
      super.doubleClicked(object);
    }
  }

  public static ResourceOpener registerResourceOpener(String resourceExtension, ResourceOpener opener)
  {
    return resourceOpeners.put(resourceExtension, opener);
  }

  public static ResourceOpener unregisterResourceOpener(String resourceExtension)
  {
    return resourceOpeners.remove(resourceExtension);
  }

  /**
   * @author Eike Stepper
   */
  public interface ResourceOpener
  {
    public void openResource(IWorkbenchPage page, CDOResource resource);
  }
}
