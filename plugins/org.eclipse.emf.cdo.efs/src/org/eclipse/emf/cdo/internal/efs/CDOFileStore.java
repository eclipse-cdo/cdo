/*******************************************************************************
 * Copyright (c) 2005, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 * Martin Oberhuber (Wind River) - [294429] Avoid substring baggage in FileInfo
 *******************************************************************************/
package org.eclipse.emf.cdo.internal.efs;

import org.eclipse.emf.cdo.internal.efs.bundle.OM;

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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Eike Stepper
 */
public final class CDOFileStore extends AbstractFileStore
{
  private static final Path PROJECT_DESCRIPTION_PATH = new Path(IProjectDescription.DESCRIPTION_FILE_NAME);

  private CDORootStore rootStore;

  private IPath path;

  public CDOFileStore(CDORootStore rootStore, IPath path)
  {
    this.rootStore = rootStore;
    this.path = path;
  }

  @Override
  public CDOFileSystem getFileSystem()
  {
    return rootStore.getFileSystem();
  }

  public CDORootStore getRootStore()
  {
    return rootStore;
  }

  public IPath getPath()
  {
    return path;
  }

  @Override
  public IFileStore getParent()
  {
    if (path.segmentCount() == 1)
    {
      return rootStore;
    }

    return new CDOFileStore(rootStore, path.removeLastSegments(1));
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
    if (isProjectDescription())
    {
      info.setExists(getProjectDescription() != null);
    }
    else
    {
      info.setExists(true);
    }

    info.setLength(EFS.NONE);
    info.setDirectory(!isProjectDescription());
    info.setAttribute(EFS.ATTRIBUTE_READ_ONLY, false);
    return info;
  }

  @Override
  public IFileStore getChild(IPath path)
  {
    return new CDOFileStore(rootStore, this.path.append(path));
  }

  @Override
  public IFileStore getChild(String name)
  {
    return new CDOFileStore(rootStore, path.append(name));
  }

  @Override
  public String[] childNames(int options, IProgressMonitor monitor) throws CoreException
  {
    if (path.segmentCount() == 4)
    {
      return new String[0];
    }

    return new String[] { "a", "b", "c" };
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
          getFileSystem().putProjectDescription(rootStore, description);
        }
        catch (RuntimeException ex)
        {
          ex.printStackTrace();
          throw ex;
        }
      }
    };
  }

  @SuppressWarnings("restriction")
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
      if (isProjectDescription())
      {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        IProjectDescription description = getProjectDescription();
        new org.eclipse.core.internal.resources.ModelObjectWriter().write(description, baos);

        byte[] buf = baos.toByteArray();
        return new ByteArrayInputStream(buf);
      }

      return new FileInputStream(path.toPortableString());
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

  private IProjectDescription getProjectDescription()
  {
    return getFileSystem().getProjectDescription(rootStore);
  }

  private boolean isProjectDescription()
  {
    return path.equals(PROJECT_DESCRIPTION_PATH);
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
      return rootStore == that.rootStore && path.equals(that.path);
    }

    return false;
  }

  @Override
  protected int createHashCode()
  {
    return rootStore.hashCode() ^ path.hashCode();
  }

  @Override
  public void appendURI(StringBuilder builder)
  {
    rootStore.appendURI(builder);
    builder.append("/");
    builder.append(path.toPortableString());
  }
}
