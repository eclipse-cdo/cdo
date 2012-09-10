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
package org.eclipse.emf.cdo.internal.ui;

import org.eclipse.emf.cdo.common.lob.CDOBlob;
import org.eclipse.emf.cdo.common.lob.CDOClob;
import org.eclipse.emf.cdo.eresource.CDOBinaryResource;
import org.eclipse.emf.cdo.eresource.CDOFileResource;
import org.eclipse.emf.cdo.eresource.CDOTextResource;
import org.eclipse.emf.cdo.internal.ui.bundle.OM;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.io.IORuntimeException;
import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.provider.FileInfo;
import org.eclipse.core.filesystem.provider.FileStore;
import org.eclipse.core.filesystem.provider.FileSystem;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IURIEditorInput;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.WeakHashMap;

/**
 * @author Eike Stepper
 */
public class CDOLobEditorInput extends PlatformObject implements IURIEditorInput
{
  private static final String SCHEME = "cdo.lob";

  private static final Map<CDOLobEditorInput, LobFileStore> fileStores = new WeakHashMap<CDOLobEditorInput, LobFileStore>();

  private static int lastID;

  private CDOFileResource<?> resource;

  private URI uri;

  public CDOLobEditorInput(CDOFileResource<?> resource)
  {
    this.resource = resource;

    try
    {
      uri = new URI(SCHEME + "://" + ++lastID);
    }
    catch (URISyntaxException ex)
    {
      throw WrappedException.wrap(ex);
    }

    synchronized (fileStores)
    {
      fileStores.put(this, null);
    }
  }

  public CDOFileResource<?> getResource()
  {
    return resource;
  }

  public boolean exists()
  {
    return true;
  }

  public ImageDescriptor getImageDescriptor()
  {
    return null;
  }

  public String getName()
  {
    return resource.getName();
  }

  public IPersistableElement getPersistable()
  {
    return null;
  }

  public String getToolTipText()
  {
    return resource.getPath();
  }

  public URI getURI()
  {
    return uri;
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + (resource == null ? 0 : resource.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
    {
      return true;
    }

    if (obj == null)
    {
      return false;
    }

    if (!(obj instanceof CDOLobEditorInput))
    {
      return false;
    }

    CDOLobEditorInput other = (CDOLobEditorInput)obj;
    if (resource == null)
    {
      if (other.resource != null)
      {
        return false;
      }
    }
    else if (!resource.equals(other.resource))
    {
      return false;
    }

    return true;
  }

  /**
   * @author Eike Stepper
   */
  public static class LobFileSystem extends FileSystem
  {
    @Override
    public IFileStore getStore(URI uri)
    {
      synchronized (fileStores)
      {
        for (Entry<CDOLobEditorInput, LobFileStore> entry : fileStores.entrySet())
        {
          CDOLobEditorInput editorInput = entry.getKey();
          if (uri.equals(editorInput.getURI()))
          {
            LobFileStore store = entry.getValue();
            if (store == null)
            {
              store = createStore(uri, editorInput);
              fileStores.put(editorInput, store);
            }

            return store;
          }
        }
      }

      throw new IllegalStateException("No editor input is cached for " + uri);
    }

    protected LobFileStore createStore(URI uri, CDOLobEditorInput editorInput)
    {
      CDOFileResource<?> resource = editorInput.getResource();
      return new LobFileStore(resource, uri);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class LobFileStore extends FileStore
  {
    private static final String[] NO_CHILDREN = new String[0];

    private CDOFileResource<?> resource;

    private final URI uri;

    private FileInfo info;

    public LobFileStore(CDOFileResource<?> resource, URI uri)
    {
      this.resource = resource;
      this.uri = uri;
    }

    @Override
    public URI toURI()
    {
      return uri;
    }

    @Override
    public IFileInfo fetchInfo(int options, IProgressMonitor monitor) throws CoreException
    {
      if (info == null)
      {
        info = new FileInfo(getName());
        info.setLastModified(0L);
        info.setExists(true);
        info.setDirectory(false);
        info.setLength(EFS.NONE);
        info.setAttribute(EFS.ATTRIBUTE_READ_ONLY, false);
        info.setAttribute(EFS.ATTRIBUTE_HIDDEN, false);
      }

      return info;
    }

    @Override
    public InputStream openInputStream(int options, IProgressMonitor monitor) throws CoreException
    {
      try
      {
        if (resource instanceof CDOTextResource)
        {
          CDOTextResource textResource = (CDOTextResource)resource;
          CDOClob clob = textResource.getContents();
          if (clob == null)
          {
            return new ByteArrayInputStream(new byte[0]);
          }

          Reader reader = clob.getContents();
          CharArrayWriter writer = new CharArrayWriter();
          IOUtil.copyCharacter(reader, writer);

          String encoding = getEncoding(textResource);
          byte[] bytes = writer.toString().getBytes(encoding);
          return new ByteArrayInputStream(bytes);
        }

        CDOBlob blob = ((CDOBinaryResource)resource).getContents();
        if (blob == null)
        {
          return new ByteArrayInputStream(new byte[0]);
        }

        InputStream inputStream = blob.getContents();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        IOUtil.copy(inputStream, outputStream);

        byte[] bytes = outputStream.toByteArray();
        return new ByteArrayInputStream(bytes);
      }
      catch (IOException ex)
      {
        throw new IORuntimeException(ex);
      }
    }

    @Override
    public OutputStream openOutputStream(int options, IProgressMonitor monitor) throws CoreException
    {
      return new ByteArrayOutputStream()
      {
        @Override
        public void close() throws IOException
        {
          if (resource instanceof CDOTextResource)
          {
            CDOTextResource textResource = (CDOTextResource)resource;
            String encoding = getEncoding(textResource);

            String string = toString(encoding);
            CDOClob clob = new CDOClob(new CharArrayReader(string.toCharArray()));
            textResource.setContents(clob);
          }
          else
          {
            byte[] bytes = toByteArray();
            CDOBlob blob = new CDOBlob(new ByteArrayInputStream(bytes));
            ((CDOBinaryResource)resource).setContents(blob);
          }
        }
      };
    }

    @Override
    public String getName()
    {
      return resource.getName();
    }

    @Override
    public IFileStore getParent()
    {
      return null; // This is a flat file system
    }

    @Override
    public IFileStore getChild(String name)
    {
      return null; // This is a flat file system
    }

    @Override
    public String[] childNames(int options, IProgressMonitor monitor) throws CoreException
    {
      return NO_CHILDREN; // This is a flat file system
    }

    private String getEncoding(CDOTextResource textResource)
    {
      String encoding = textResource.getEncoding();
      if (encoding == null)
      {
        try
        {
          encoding = ResourcesPlugin.getWorkspace().getRoot().getDefaultCharset();
        }
        catch (CoreException ex)
        {
          OM.LOG.error(ex);
        }
      }

      return encoding;
    }
  }
}
