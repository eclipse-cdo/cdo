/*
 * Copyright (c) 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.explorer.resources;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.lob.CDOBlob;
import org.eclipse.emf.cdo.common.lob.CDOClob;
import org.eclipse.emf.cdo.common.lob.CDOLob;
import org.eclipse.emf.cdo.common.util.CDOResourceNodeNotFoundException;
import org.eclipse.emf.cdo.eresource.CDOBinaryResource;
import org.eclipse.emf.cdo.eresource.CDOFileResource;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.eresource.CDOTextResource;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.internal.explorer.bundle.OM;
import org.eclipse.emf.cdo.util.CDOURIUtil;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.io.ReaderInputStream;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.provider.FileInfo;
import org.eclipse.core.filesystem.provider.FileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * @author Eike Stepper
 */
public class CDOCheckoutFileStore extends FileStore
{
  private static final String SLASH = CDOURIUtil.SEGMENT_SEPARATOR;

  private static final String[] NO_CHILDREN = {};

  private final CDOCheckout checkout;

  private final String path;

  private final boolean shallowTimeStamp;

  public CDOCheckoutFileStore(CDOCheckout checkout, String path, boolean shallowTimeStamp)
  {
    this.checkout = checkout;
    this.path = CDOURIUtil.sanitizePath(path);
    this.shallowTimeStamp = shallowTimeStamp;
  }

  public final CDOCheckout getCheckout()
  {
    return checkout;
  }

  public final String getPath()
  {
    return path;
  }

  @Override
  public IFileStore getParent()
  {
    if (isRoot())
    {
      return null;
    }

    int lastSlash = path.lastIndexOf(SLASH);
    if (lastSlash != -1)
    {
      return new CDOCheckoutFileStore(checkout, path.substring(0, lastSlash), shallowTimeStamp);
    }

    return new CDOCheckoutFileStore(checkout, SLASH, shallowTimeStamp);
  }

  @Override
  public String getName()
  {
    if (isRoot())
    {
      return StringUtil.EMPTY;
    }

    int lastSlash = path.lastIndexOf(SLASH);
    if (lastSlash != -1)
    {
      return path.substring(lastSlash + 1);
    }

    return path;
  }

  @Override
  public URI toURI()
  {
    return CDOCheckoutFileSystem.createURI(checkout, path, shallowTimeStamp);
  }

  @Override
  public IFileInfo fetchInfo(int options, IProgressMonitor monitor) throws CoreException
  {
    FileInfo info = new FileInfo(getName());
    info.setLength(EFS.NONE);
    info.setAttribute(EFS.ATTRIBUTE_OWNER_READ, true);
    info.setAttribute(EFS.ATTRIBUTE_GROUP_READ, true);
    info.setAttribute(EFS.ATTRIBUTE_OTHER_READ, true);

    info.setAttribute(EFS.ATTRIBUTE_OWNER_WRITE, false);
    info.setAttribute(EFS.ATTRIBUTE_GROUP_WRITE, false);
    info.setAttribute(EFS.ATTRIBUTE_OTHER_WRITE, false);

    info.setAttribute(EFS.ATTRIBUTE_OWNER_EXECUTE, false);
    info.setAttribute(EFS.ATTRIBUTE_GROUP_EXECUTE, false);
    info.setAttribute(EFS.ATTRIBUTE_OTHER_EXECUTE, false);

    CDOResourceNode node = getNode();
    if (node == null)
    {
      info.setExists(false);
    }
    else
    {
      info.setExists(true);

      boolean root = node.isRoot();
      boolean folder = root || node instanceof CDOResourceFolder;
      boolean model = !root && node instanceof CDOResource;

      info.setDirectory(folder);
      info.setLastModified(getTimeStamp(node, model));

      if (node instanceof CDOFileResource)
      {
        CDOFileResource<?> file = (CDOFileResource<?>)node;
        CDOLob<?> contents = file.getContents();
        info.setLength(contents == null ? 0L : contents.getSize());
      }
    }

    return info;
  }

  @Override
  public String[] childNames(int options, IProgressMonitor monitor) throws CoreException
  {
    CDOResourceNode node = getNode();

    EList<? extends EObject> children = getChildren(node);
    if (children != null)
    {
      int size = children.size();
      String[] names = new String[size];

      for (int i = 0; i < size; i++)
      {
        CDOResourceNode child = (CDOResourceNode)children.get(i);
        names[i] = child.getName();
      }

      return names;
    }

    return NO_CHILDREN;
  }

  @Override
  public IFileStore getChild(String name)
  {
    String childPath = path + (isRoot() ? name : SLASH + name);
    return new CDOCheckoutFileStore(checkout, childPath, shallowTimeStamp);
  }

  @Override
  public InputStream openInputStream(int options, IProgressMonitor monitor) throws CoreException
  {
    CDOResourceNode node = getNode();
    if (node == null)
    {
      throw new CoreException(new Status(IStatus.ERROR, OM.BUNDLE_ID, EFS.ERROR_NOT_EXISTS, "Resource does not exist: " + path, null));
    }

    if (node instanceof CDOResourceFolder)
    {
      throw new CoreException(new Status(IStatus.ERROR, OM.BUNDLE_ID, EFS.ERROR_WRONG_TYPE, "Resource is a folder: " + path, null));
    }

    if (node instanceof CDOBinaryResource)
    {
      CDOBinaryResource binary = (CDOBinaryResource)node;
      CDOBlob blob = binary.getContents();
      if (blob == null)
      {
        return createEmptyInputStream();
      }

      try
      {
        return blob.getContents();
      }
      catch (IOException ex)
      {
        OM.BUNDLE.coreException(ex);
      }
    }
    else if (node instanceof CDOTextResource)
    {
      CDOTextResource text = (CDOTextResource)node;
      CDOClob clob = text.getContents();
      if (clob == null)
      {
        return createEmptyInputStream();
      }

      try
      {
        return new ReaderInputStream(clob.getContents(), text.getEncoding());
      }
      catch (IOException ex)
      {
        OM.BUNDLE.coreException(ex);
      }
    }
    else // Model resource
    {
      CDOResource resource = (CDOResource)node;
      ByteArrayOutputStream baos = new ByteArrayOutputStream();

      try
      {
        resource.save(baos, null);
        return new ByteArrayInputStream(baos.toByteArray());
      }
      catch (IOException ex)
      {
        OM.BUNDLE.coreException(ex);
      }
    }

    // Can't realistically happen.
    return null;
  }

  private InputStream createEmptyInputStream()
  {
    return new InputStream()
    {
      @Override
      public int read() throws IOException
      {
        return IOUtil.EOF;
      }
    };
  }

  private boolean isRoot()
  {
    return SLASH.equals(path);
  }

  private CDOResourceNode getNode()
  {
    CDOView view = checkout.getView();
    if (view != null)
    {
      try
      {
        if (StringUtil.isEmpty(path))
        {
          return view.getRootResource();
        }

        return view.getResourceNode(path);
      }
      catch (CDOResourceNodeNotFoundException ex)
      {
        //$FALL-THROUGH$
      }
    }

    return null;
  }

  private EList<? extends EObject> getChildren(CDOResourceNode node)
  {
    if (node instanceof CDOResourceFolder)
    {
      CDOResourceFolder folder = (CDOResourceFolder)node;
      return folder.getNodes();
    }

    if (node != null && node.isRoot())
    {
      return ((CDOResource)node).getContents();
    }

    return null;
  }

  private long getTimeStamp(CDOResourceNode node, boolean model)
  {
    long timeStamp = node.cdoRevision(true).getTimeStamp();

    if (model && !shallowTimeStamp)
    {
      Resource resource = (Resource)node;
      for (TreeIterator<EObject> it = EcoreUtil.getAllProperContents(resource, false); it.hasNext();)
      {
        CDOObject object = CDOUtil.getCDOObject(it.next());
        timeStamp = Math.max(timeStamp, object.cdoRevision(true).getTimeStamp());
      }
    }

    return timeStamp;
  }
}
