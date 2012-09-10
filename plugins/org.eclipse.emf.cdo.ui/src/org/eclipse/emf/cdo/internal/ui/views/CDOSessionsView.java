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

import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceLeaf;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.internal.ui.actions.OpenResourceEditorAction;
import org.eclipse.emf.cdo.internal.ui.actions.OpenSessionAction;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.ui.CDOItemProvider;

import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.ui.dnd.DNDDropAdapter;
import org.eclipse.net4j.util.ui.views.ContainerItemProvider;
import org.eclipse.net4j.util.ui.views.ContainerView;
import org.eclipse.net4j.util.ui.views.IElementFilter;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.part.ResourceTransfer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
    Control control = super.createUI(parent);

    TreeViewer viewer = getViewer();
    CDOResourceDropAdapter.support(viewer);

    return control;
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
    IWorkbenchPage page = getSite().getPage();

    if (object instanceof CDOResourceLeaf)
    {
      CDOResourceLeaf resource = (CDOResourceLeaf)object;
      String path = resource.getPath();

      String extension = new Path(path).getFileExtension();
      ResourceOpener opener = resourceOpeners.get(extension);
      if (opener != null)
      {
        opener.openResource(page, resource);
      }
      else
      {
        OpenResourceEditorAction action = new OpenResourceEditorAction(page, resource);
        action.run();
      }
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
    public void openResource(IWorkbenchPage page, CDOResourceLeaf resource);
  }

  /**
   * @author Eike Stepper
   */
  public static class CDOResourceDropAdapter extends DNDDropAdapter<Object>
  {
    public static final Transfer[] TRANSFERS = { ResourceTransfer.getInstance(), FileTransfer.getInstance() };

    protected CDOResourceDropAdapter(StructuredViewer viewer)
    {
      super(TRANSFERS, viewer);
    }

    @Override
    protected boolean validateTarget(Object target, int operation)
    {
      if (getTargetResourceNode(target) != null)
      {
        overrideOperation(DND.DROP_COPY);
        return true;
      }

      return false;
    }

    @Override
    protected boolean performDrop(Object data, Object target)
    {
      CDOResourceNode container = getTargetResourceNode(target);
      String containerPath = container.getPath();

      List<Source> sources = Source.getSources((Object[])data);
      for (Source source : sources)
      {
        // System.out.println(source.getName() + " --> " + originalOperation);
      }

      return true;
    }

    /**
     * @author Eike Stepper
     */
    public static abstract class Source
    {
      public abstract String getName();

      public abstract String getPath();

      public abstract boolean isDirectory();

      public final InputStream getInputStream() throws IOException
      {
        checkNotDirectory();
        return openInputStream();
      }

      public final OutputStream getOutputStream() throws IOException
      {
        checkNotDirectory();
        return openOutputStream();
      }

      protected abstract InputStream openInputStream() throws IOException;

      protected abstract OutputStream openOutputStream() throws IOException;

      private void checkNotDirectory() throws IOException
      {
        if (isDirectory())
        {
          throw new IOException("Not supported for directories");
        }
      }

      public static Source getSource(Object data)
      {
        if (data instanceof IResource)
        {
          IResource resource = (IResource)data;
          return new ResourceSource(resource);
        }

        if (data instanceof String)
        {
          File file = new File((String)data);
          return new FileSource(file);
        }

        return null;
      }

      public static List<Source> getSources(Object[] data)
      {
        List<Source> sources = new ArrayList<Source>();

        String commonPath = null;
        for (int i = 0; i < data.length; i++)
        {
          Object element = data[i];
          Source source = getSource(element);
          if (source != null && !source.isDirectory())
          {
            String path = source.getPath();
            if (commonPath == null)
            {
              commonPath = path;
            }
            else
            {
              if (!commonPath.equals(path))
              {
                return new ArrayList<Source>();
              }
            }

            sources.add(source);
          }
        }

        return sources;
      }

      /**
       * @author Eike Stepper
       */
      private static class ResourceSource extends Source
      {
        private IResource resource;

        public ResourceSource(IResource resource)
        {
          this.resource = resource;
        }

        @Override
        public String getName()
        {
          return resource.getName();
        }

        @Override
        public String getPath()
        {
          return resource.getParent().getFullPath().toString();
        }

        @Override
        public boolean isDirectory()
        {
          return resource instanceof org.eclipse.core.resources.IContainer;
        }

        @Override
        protected InputStream openInputStream() throws IOException
        {
          try
          {
            return ((IFile)resource).getContents();
          }
          catch (CoreException ex)
          {
            throw new IOException(ex);
          }
        }

        @Override
        protected OutputStream openOutputStream() throws IOException
        {
          return new ByteArrayOutputStream()
          {
            @Override
            public void close() throws IOException
            {
              if (resource.exists())
              {
                try
                {
                  ((IFile)resource).setContents(new ByteArrayInputStream(toByteArray()), true, true, null);
                }
                catch (CoreException ex)
                {
                  throw new IOException(ex);
                }
              }
            }
          };
        }
      }

      /**
       * @author Eike Stepper
       */
      private static class FileSource extends Source
      {
        private File file;

        public FileSource(File file)
        {
          this.file = file;
        }

        @Override
        public String getName()
        {
          return file.getName();
        }

        @Override
        public String getPath()
        {
          return file.getParentFile().getPath();
        }

        @Override
        public boolean isDirectory()
        {
          return file.isDirectory();
        }

        @Override
        protected InputStream openInputStream() throws IOException
        {
          return new FileInputStream(file);
        }

        @Override
        protected OutputStream openOutputStream() throws IOException
        {
          return new FileOutputStream(file);
        }
      }
    }

    protected CDOResourceNode getTargetResourceNode(Object target)
    {
      if (target instanceof CDOTransaction)
      {
        return ((CDOTransaction)target).getRootResource();
      }

      if (target instanceof CDOResourceFolder)
      {
        return (CDOResourceFolder)target;
      }

      return null;
    }

    public static CDOResourceDropAdapter support(StructuredViewer viewer)
    {
      CDOResourceDropAdapter dropAdapter = new CDOResourceDropAdapter(viewer);
      viewer.addDropSupport(DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_DEFAULT, TRANSFERS, dropAdapter);
      return dropAdapter;
    }
  }
}
