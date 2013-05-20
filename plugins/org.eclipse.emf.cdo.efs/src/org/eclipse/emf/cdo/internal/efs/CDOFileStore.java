/*
 * Copyright (c) 2010-2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.efs;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.internal.efs.bundle.OM;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.provider.FileInfo;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;

import org.xml.sax.InputSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public final class CDOFileStore extends AbstractFileStore
{
  private static final Path PROJECT_DESCRIPTION_PATH = new Path(IProjectDescription.DESCRIPTION_FILE_NAME);

  private CDOFileRoot root;

  private IPath path;

  public CDOFileStore(CDOFileRoot root, IPath path)
  {
    this.root = root;
    this.path = path;
  }

  @Override
  public CDOFileSystem getFileSystem()
  {
    return root.getFileSystem();
  }

  public CDOFileRoot getRoot()
  {
    return root;
  }

  @Override
  public CDOView getView(IProgressMonitor monitor)
  {
    return root.getView(monitor);
  }

  @Override
  protected CDOResourceNode doGetResourceNode(IProgressMonitor monitor)
  {
    return getView(monitor).getResourceNode(path.toPortableString());
  }

  @Override
  public IPath getPath()
  {
    return path;
  }

  @Override
  public IFileStore getParent()
  {
    if (path.segmentCount() == 1)
    {
      return root;
    }

    return new CDOFileStore(root, path.removeLastSegments(1));
  }

  @Override
  public String getName()
  {
    return path.lastSegment();
  }

  @Override
  public IFileInfo fetchInfo(int options, IProgressMonitor monitor)
  {
    FileInfo info = new FileInfo(getName());
    info.setLastModified(EFS.NONE);
    info.setLength(EFS.NONE);
    info.setAttribute(EFS.ATTRIBUTE_READ_ONLY, false);

    if (isProjectDescription())
    {
      info.setExists(getProjectName() != null);
      info.setDirectory(false);
    }
    else
    {
      try
      {
        CDOResourceNode resourceNode = getResourceNode(monitor);
        info.setExists(true);
        info.setDirectory(resourceNode instanceof CDOResourceFolder);
      }
      catch (Exception ex)
      {
        info.setExists(false);
      }
    }

    return info;
  }

  @Deprecated
  @Override
  public IFileStore getChild(IPath path)
  {
    return new CDOFileStore(root, this.path.append(path));
  }

  @Override
  public IFileStore getChild(String name)
  {
    return new CDOFileStore(root, path.append(name));
  }

  @Override
  public String[] childNames(int options, IProgressMonitor monitor) throws CoreException
  {
    List<String> result = new ArrayList<String>();
    CDOResourceNode resourceNode = getResourceNode(monitor);
    if (resourceNode instanceof CDOResourceFolder)
    {
      CDOResourceFolder resourceFolder = (CDOResourceFolder)resourceNode;
      for (CDOResourceNode node : resourceFolder.getNodes())
      {
        result.add(node.getName());
      }
    }

    return result.toArray(new String[result.size()]);
  }

  @Override
  public OutputStream openOutputStream(int options, IProgressMonitor monitor) throws CoreException
  {
    return new ByteArrayOutputStream()
    {
      @SuppressWarnings("restriction")
      @Override
      public void close() throws IOException
      {
        try
        {
          ByteArrayInputStream bais = new ByteArrayInputStream(toByteArray());
          IProjectDescription description = new org.eclipse.core.internal.resources.ProjectDescriptionReader()
              .read(new InputSource(bais));
          OM.associateProjectName(root.toURI(), description.getName());
        }
        catch (RuntimeException ex)
        {
          ex.printStackTrace();
          throw ex;
        }
      }
    };
  }

  @Override
  public InputStream openInputStream(int options, IProgressMonitor monitor) throws CoreException
  {
    if (monitor == null)
    {
      monitor = new NullProgressMonitor();
    }

    try
    {
      monitor.beginTask("", 1); //$NON-NLS-1$
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      if (isProjectDescription())
      {
        writeProjectDescription(baos, monitor);
      }
      else
      {
        writeResource(baos, monitor);
      }

      return new ByteArrayInputStream(baos.toByteArray());
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      throw new CoreException(new Status(IStatus.ERROR, OM.BUNDLE_ID, ex.getLocalizedMessage(), ex));
    }
    finally
    {
      monitor.done();
    }
  }

  private String getProjectName()
  {
    return OM.getProjectName(root.toURI());
  }

  private boolean isProjectDescription()
  {
    return path.equals(PROJECT_DESCRIPTION_PATH);
  }

  private void writeProjectDescription(ByteArrayOutputStream baos, IProgressMonitor monitor)
  {
    PrintStream out = new PrintStream(baos);
    out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    out.println("<projectDescription>");
    out.println("  <name>" + OM.getProjectName(root.toURI()) + "</name>");
    out.println("  <comment></comment>");
    out.println("  <projects>");
    out.println("  </projects>");
    out.println("  <buildSpec>");
    out.println("  </buildSpec>");
    out.println("  <natures>");
    out.println("  </natures>");
    out.println("</projectDescription>");
    out.flush();
  }

  private void writeResource(ByteArrayOutputStream baos, IProgressMonitor monitor) throws IOException
  {
    CDOResource resource = (CDOResource)getResourceNode(monitor);
    // resource.cdoPrefetch(CDORevision.DEPTH_INFINITE);
    resource.save(baos, null);
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj == this)
    {
      return true;
    }

    if (obj.getClass() == CDOFileStore.class)
    {
      CDOFileStore that = (CDOFileStore)obj;
      return root == that.root && path.equals(that.path);
    }

    return false;
  }

  @Override
  protected int createHashCode()
  {
    return root.hashCode() ^ path.hashCode();
  }

  @Override
  public void appendURI(StringBuilder builder)
  {
    root.appendURI(builder);
    builder.append("/");
    builder.append(path.toPortableString());
  }
}
