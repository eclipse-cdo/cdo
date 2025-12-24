/*
 * Copyright (c) 2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.transfer.spi.workspace;

import org.eclipse.emf.cdo.transfer.CDOTransferElement;
import org.eclipse.emf.cdo.transfer.CDOTransferSystem;
import org.eclipse.emf.cdo.transfer.CDOTransferType;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.io.IORuntimeException;

import org.eclipse.emf.common.util.URI;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;

import java.io.InputStream;

/**
 * An Eclipse {@link IWorkspace workspace}-based implementation of a {@link CDOTransferSystem transfer system}.
 *
 * @author Eike Stepper
 * @since 4.2
 */
public class WorkspaceTransferSystem extends CDOTransferSystem
{
  public static final WorkspaceTransferSystem INSTANCE = new WorkspaceTransferSystem();

  public static final String TYPE = "workspace";

  private static final IWorkspaceRoot ROOT = ResourcesPlugin.getWorkspace().getRoot();

  public WorkspaceTransferSystem()
  {
    super(false);
  }

  @Override
  public String getType()
  {
    return TYPE;
  }

  public String getDefaultEncoding()
  {
    try
    {
      return ROOT.getDefaultCharset();
    }
    catch (CoreException ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  protected String getEncoding(IFile file)
  {
    try
    {
      return file.getCharset();
    }
    catch (CoreException ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  @Override
  public CDOTransferType getDefaultTransferType(CDOTransferElement element)
  {
    if (element instanceof Element)
    {
      Element node = (Element)element;
      if (node.isDirectory())
      {
        return CDOTransferType.FOLDER;
      }

      IFile file = (IFile)node.getNativeObject();

      // TODO Team is optional, handle its absence.
      int type = org.eclipse.team.core.Team.getFileContentManager().getType(file);
      switch (type)
      {
      case org.eclipse.team.core.Team.BINARY:
        return CDOTransferType.BINARY;
      case org.eclipse.team.core.Team.TEXT:
        String encoding = getEncoding(file);
        return CDOTransferType.text(encoding);
      }
    }

    return super.getDefaultTransferType(element);
  }

  @Override
  public URI getURI(IPath path)
  {
    return URI.createPlatformResourceURI(path.toString(), true);
  }

  @Override
  public CDOTransferElement getElement(IPath path)
  {
    IResource resource = ROOT.findMember(path);
    if (resource != null && resource.exists())
    {
      return new Element(this, resource);
    }

    return null;
  }

  @Override
  public CDOTransferElement getElement(URI uri)
  {
    if (uri.isPlatformResource())
    {
      IPath path = new Path(uri.path()).removeFirstSegments(1).makeAbsolute();
      return getElement(path);
    }

    return null;
  }

  @Override
  public void createFolder(IPath path)
  {
    try
    {
      IFolder folder = ROOT.getFolder(path);
      folder.create(true, true, null);
    }
    catch (CoreException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  @Override
  public void createBinary(IPath path, InputStream source, IProgressMonitor monitor)
  {
    try
    {
      IFile file = ROOT.getFile(path);
      file.create(source, true, monitor);
    }
    catch (CoreException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  @Override
  public void createText(IPath path, InputStream source, String encoding, IProgressMonitor monitor)
  {
    try
    {
      IFile file = ROOT.getFile(path);
      file.create(source, true, monitor);
      file.setCharset(encoding, null);
    }
    catch (CoreException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  @Override
  public String toString()
  {
    return "Workspace";
  }

  /**
   * @author Eike Stepper
   */
  private static class Element extends CDOTransferElement
  {
    private IResource resource;

    public Element(CDOTransferSystem system, IResource resource)
    {
      super(system);
      this.resource = resource;
    }

    @Override
    public Object getNativeObject()
    {
      return resource;
    }

    @Override
    public IPath getPath()
    {
      return resource.getFullPath();
    }

    @Override
    public boolean isDirectory()
    {
      return resource instanceof IContainer;
    }

    @Override
    protected CDOTransferElement[] doGetChildren()
    {
      try
      {
        IResource[] children = ((IContainer)resource).members();
        CDOTransferElement[] result = new Element[children.length];

        for (int i = 0; i < children.length; i++)
        {
          IResource child = children[i];
          result[i] = new Element(getSystem(), child);
        }

        return result;
      }
      catch (CoreException ex)
      {
        throw new IORuntimeException(ex);
      }
    }

    @Override
    protected InputStream doOpenInputStream()
    {
      try
      {
        return ((IFile)resource).getContents();
      }
      catch (CoreException ex)
      {
        throw new IORuntimeException(ex);
      }
    }

    // @Override
    // protected OutputStream doOpenOutputStream()
    // {
    // return new ByteArrayOutputStream()
    // {
    // @Override
    // public void close() throws IOException
    // {
    // if (resource.exists())
    // {
    // try
    // {
    // ((IFile)resource).setContents(new ByteArrayInputStream(toByteArray()), true, true, null);
    // }
    // catch (CoreException ex)
    // {
    // throw new IOException(ex);
    // }
    // }
    // }
    // };
    // }
  }
}
