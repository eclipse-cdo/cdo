/*
 * Copyright (c) 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui;

import org.eclipse.emf.cdo.eresource.CDOResourceLeaf;

import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * @author Eike Stepper
 */
public class CDOLobEditorInput extends PlatformObject implements IEditorInput
{
  // private static final String SCHEME = "cdo.lob";
  //
  // private static final Map<CDOLobEditorInput, LobFileStore> fileStores = new WeakHashMap<CDOLobEditorInput,
  // LobFileStore>();
  //
  // private URI uri;
  //
  // public URI getURI()
  // {
  // return uri;
  // }

  private CDOResourceLeaf resource;

  public CDOLobEditorInput(CDOResourceLeaf resource)
  {
    this.resource = resource;

    // try
    // {
    // CDOView view = resource.cdoView();
    // org.eclipse.emf.common.util.URI resourceURI = resource.getURI();
    // String path = resourceURI.authority() + "/" + resourceURI.path() + "?session=" + view.getSessionID() + "&view="
    // + view.getViewID();
    //
    // uri = new URI(SCHEME + "://" + path);
    // }
    // catch (URISyntaxException ex)
    // {
    // throw WrappedException.wrap(ex);
    // }
    //
    // synchronized (fileStores)
    // {
    // fileStores.remove(this);
    // fileStores.put(this, null);
    // }
  }

  public CDOResourceLeaf getResource()
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
    return resource.getURI().toString();
  }

  // @Override
  // public int hashCode()
  // {
  // final int prime = 31;
  // int result = 1;
  // result = prime * result + (uri == null ? 0 : uri.hashCode());
  // return result;
  // }
  //
  // @Override
  // public boolean equals(Object obj)
  // {
  // if (this == obj)
  // {
  // return true;
  // }
  //
  // if (obj == null)
  // {
  // return false;
  // }
  //
  // if (!(obj instanceof CDOLobEditorInput))
  // {
  // return false;
  // }
  //
  // CDOLobEditorInput other = (CDOLobEditorInput)obj;
  // if (uri == null)
  // {
  // if (other.uri != null)
  // {
  // return false;
  // }
  // }
  // else if (!uri.equals(other.uri))
  // {
  // return false;
  // }
  //
  // return true;
  // }
  //
  // /**
  // * @author Eike Stepper
  // */
  // public static class LobFileSystem extends FileSystem
  // {
  // @Override
  // public IFileStore getStore(URI uri)
  // {
  // synchronized (fileStores)
  // {
  // for (Entry<CDOLobEditorInput, LobFileStore> entry : fileStores.entrySet())
  // {
  // CDOLobEditorInput editorInput = entry.getKey();
  // if (uri.equals(editorInput.getURI()))
  // {
  // LobFileStore store = entry.getValue();
  // if (store == null)
  // {
  // store = createStore(uri, editorInput);
  // fileStores.put(editorInput, store);
  // }
  //
  // return store;
  // }
  // }
  // }
  //
  // throw new IllegalStateException("No editor input is cached for " + uri);
  // }
  //
  // protected LobFileStore createStore(URI uri, CDOLobEditorInput editorInput)
  // {
  // CDOResourceLeaf resource = editorInput.getResource();
  // return new LobFileStore(resource, uri);
  // }
  // }
  //
  // /**
  // * @author Eike Stepper
  // */
  // public static class LobFileStore extends FileStore
  // {
  // private static final String[] NO_CHILDREN = new String[0];
  //
  // private CDOResourceLeaf resource;
  //
  // private final URI uri;
  //
  // private FileInfo info;
  //
  // public LobFileStore(CDOResourceLeaf resource, URI uri)
  // {
  // this.resource = resource;
  // this.uri = uri;
  // }
  //
  // @Override
  // public URI toURI()
  // {
  // return uri;
  // }
  //
  // @Override
  // public IFileInfo fetchInfo(int options, IProgressMonitor monitor) throws CoreException
  // {
  // if (info == null)
  // {
  // info = new FileInfo(getName());
  // info.setLastModified(0L);
  // info.setExists(true);
  // info.setDirectory(false);
  // info.setLength(EFS.NONE);
  // info.setAttribute(EFS.ATTRIBUTE_READ_ONLY, false);
  // info.setAttribute(EFS.ATTRIBUTE_HIDDEN, false);
  // }
  //
  // return info;
  // }
  //
  // @Override
  // public InputStream openInputStream(int options, IProgressMonitor monitor) throws CoreException
  // {
  // try
  // {
  // if (resource instanceof CDOTextResource)
  // {
  // CDOTextResource textResource = (CDOTextResource)resource;
  // CDOClob clob = textResource.getContents();
  // if (clob == null)
  // {
  // return new ByteArrayInputStream(new byte[0]);
  // }
  //
  // Reader reader = clob.getContents();
  // CharArrayWriter writer = new CharArrayWriter();
  // IOUtil.copyCharacter(reader, writer);
  //
  // String encoding = getEncoding(textResource);
  // byte[] bytes = writer.toString().getBytes(encoding);
  // return new ByteArrayInputStream(bytes);
  // }
  //
  // if (resource instanceof CDOBinaryResource)
  // {
  // CDOBlob blob = ((CDOBinaryResource)resource).getContents();
  // if (blob == null)
  // {
  // return new ByteArrayInputStream(new byte[0]);
  // }
  //
  // InputStream inputStream = blob.getContents();
  // ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
  // IOUtil.copy(inputStream, outputStream);
  //
  // byte[] bytes = outputStream.toByteArray();
  // return new ByteArrayInputStream(bytes);
  // }
  //
  // ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
  // ((CDOResource)resource).save(outputStream, null);
  // byte[] bytes = outputStream.toByteArray();
  // return new ByteArrayInputStream(bytes);
  // }
  // catch (IOException ex)
  // {
  // throw new IORuntimeException(ex);
  // }
  // }
  //
  // @Override
  // public OutputStream openOutputStream(int options, IProgressMonitor monitor) throws CoreException
  // {
  // return new ByteArrayOutputStream()
  // {
  // @Override
  // public void close() throws IOException
  // {
  // if (resource instanceof CDOTextResource)
  // {
  // CDOTextResource textResource = (CDOTextResource)resource;
  // String encoding = getEncoding(textResource);
  //
  // String string = toString(encoding);
  // CDOClob clob = new CDOClob(new CharArrayReader(string.toCharArray()));
  // textResource.setContents(clob);
  // }
  // else if (resource instanceof CDOBinaryResource)
  // {
  // byte[] bytes = toByteArray();
  // CDOBlob blob = new CDOBlob(new ByteArrayInputStream(bytes));
  // ((CDOBinaryResource)resource).setContents(blob);
  // }
  // }
  // };
  // }
  //
  // @Override
  // public String getName()
  // {
  // return resource.getName();
  // }
  //
  // @Override
  // public IFileStore getParent()
  // {
  // return null; // This is a flat file system
  // }
  //
  // @Override
  // public IFileStore getChild(String name)
  // {
  // return null; // This is a flat file system
  // }
  //
  // @Override
  // public String[] childNames(int options, IProgressMonitor monitor) throws CoreException
  // {
  // return NO_CHILDREN; // This is a flat file system
  // }
  //
  // private String getEncoding(CDOTextResource textResource)
  // {
  // String encoding = textResource.getEncoding();
  // if (encoding == null)
  // {
  // try
  // {
  // encoding = ResourcesPlugin.getWorkspace().getRoot().getDefaultCharset();
  // }
  // catch (CoreException ex)
  // {
  // OM.LOG.error(ex);
  // }
  // }
  //
  // return encoding;
  // }
  // }
}
