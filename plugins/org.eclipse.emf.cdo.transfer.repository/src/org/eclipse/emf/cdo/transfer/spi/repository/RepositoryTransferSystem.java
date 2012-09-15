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
package org.eclipse.emf.cdo.transfer.spi.repository;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.lob.CDOBlob;
import org.eclipse.emf.cdo.common.lob.CDOClob;
import org.eclipse.emf.cdo.eresource.CDOBinaryResource;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.eresource.CDOTextResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.transfer.CDOTransferElement;
import org.eclipse.emf.cdo.transfer.CDOTransferSystem;
import org.eclipse.emf.cdo.transfer.CDOTransferType;
import org.eclipse.emf.cdo.util.CDOURIUtil;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewInvalidationEvent;

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.io.IORuntimeException;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.ref.ReferenceValueMap;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import java.io.ByteArrayInputStream;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * @author Eike Stepper
 * @since 4.2
 */
public class RepositoryTransferSystem extends CDOTransferSystem
{
  public static final String TYPE = "repository";

  private static final Map<CDOView, RepositoryTransferSystem> INSTANCES = new WeakHashMap<CDOView, RepositoryTransferSystem>();

  private static final IListener VIEW_LISTENER = new ViewListener();

  private final CDOView view;

  private final Map<IPath, CDOResourceNode> resourceNodeCache = new ReferenceValueMap.Soft<IPath, CDOResourceNode>();

  private RepositoryTransferSystem(CDOView view)
  {
    super(view.isReadOnly());
    this.view = view;
  }

  @Override
  public String getType()
  {
    return TYPE;
  }

  public CDOView getView()
  {
    return view;
  }

  public RepositoryTransferSystem makeWriteable()
  {
    if (isReadOnly())
    {
      CDOBranch branch = view.getBranch();
      CDOSession session = view.getSession();
      CDOTransaction transaction = session.openTransaction(branch);
      return getInstance(transaction);
    }

    return this;
  }

  @Override
  public CDOTransferType getDefaultTransferType(CDOTransferElement element)
  {
    if (element instanceof Element)
    {
      CDOResourceNode node = ((Element)element).node;
      if (node instanceof CDOResource)
      {
        return CDOTransferType.MODEL;
      }

      if (node instanceof CDOTextResource)
      {
        String encoding = ((CDOTextResource)node).getEncoding();
        return CDOTransferType.text(encoding);
      }

      if (node instanceof CDOBinaryResource)
      {
        return CDOTransferType.BINARY;
      }
    }

    return super.getDefaultTransferType(element);
  }

  @Override
  protected ResourceSet provideResourceSet()
  {
    return view.getResourceSet();
  }

  @Override
  public URI getURI(IPath path)
  {
    return CDOURIUtil.createResourceURI(view, path.toString());
  }

  @Override
  public CDOTransferElement getElement(IPath path)
  {
    System.out.println("Get element for: " + path);
    if (path.isEmpty())
    {
      return new RootElement(this, view.getRootResource());
    }

    try
    {
      CDOResourceNode node = view.getResourceNode(path.toString());
      if (node != null)
      {
        return new Element(this, node);
      }
    }
    catch (Exception ex)
    {
      //$FALL-THROUGH$
    }

    return null;
  }

  @Override
  public CDOTransferElement getElement(URI uri)
  {
    if (CDOURIUtil.PROTOCOL_NAME.equals(uri.scheme()))
    {
      return getElement(uri.path());
    }

    return null;
  }

  @Override
  public void createFolder(IPath path)
  {
    ((CDOTransaction)view).createResourceFolder(path.toString());
  }

  @Override
  public void createBinary(IPath path, InputStream source)
  {
    try
    {
      CDOBlob blob = new CDOBlob(source);
      CDOBinaryResource resource = ((CDOTransaction)view).createBinaryResource(path.toString());
      resource.setContents(blob);
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  @Override
  public void createText(IPath path, InputStream source, String encoding)
  {
    try
    {
      CDOClob clob = new CDOClob(new InputStreamReader(source, encoding));
      CDOTextResource resource = ((CDOTransaction)view).createTextResource(path.toString());
      resource.setContents(clob);
      resource.setEncoding(encoding);
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  @Override
  public String toString()
  {
    return "Repository " + view.getSession().getRepositoryInfo().getName();
  }

  protected void handleViewInvalidation(CDOViewInvalidationEvent event)
  {
    Set<CDOObject> dirtyObjects = event.getDirtyObjects();
  }

  public static RepositoryTransferSystem getInstance(CDOView view)
  {
    synchronized (INSTANCES)
    {
      RepositoryTransferSystem instance = INSTANCES.get(view);
      if (instance == null)
      {
        instance = new RepositoryTransferSystem(view);
        view.addListener(VIEW_LISTENER);
        INSTANCES.put(view, instance);
      }

      return instance;
    }
  }

  /**
   * @author Eike Stepper
   */
  private static class ViewListener extends LifecycleEventAdapter
  {
    @Override
    protected void onDeactivated(ILifecycle view)
    {
      synchronized (INSTANCES)
      {
        INSTANCES.remove(view);
      }
    }

    @Override
    protected void notifyOtherEvent(IEvent event)
    {
      if (event instanceof CDOViewInvalidationEvent)
      {
        CDOViewInvalidationEvent e = (CDOViewInvalidationEvent)event;
        CDOView view = e.getSource();

        RepositoryTransferSystem instance;
        synchronized (INSTANCES)
        {
          instance = INSTANCES.get(view);
        }

        if (instance != null)
        {
          instance.handleViewInvalidation(e);
        }
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private static class RootElement extends CDOTransferElement
  {
    private CDOResource rootResource;

    public RootElement(CDOTransferSystem system, CDOResource rootResource)
    {
      super(system);
      this.rootResource = rootResource;
    }

    @Override
    public Object getNativeObject()
    {
      return rootResource;
    }

    @Override
    public IPath getPath()
    {
      return Path.EMPTY;
    }

    @Override
    public boolean isDirectory()
    {
      return true;
    }

    @Override
    protected CDOTransferElement[] doGetChildren()
    {
      EList<EObject> children = rootResource.getContents();
      int size = children.size();
      CDOTransferElement[] result = new Element[size];

      for (int i = 0; i < size; i++)
      {
        CDOResourceNode child = (CDOResourceNode)children.get(i);
        result[i] = new Element(getSystem(), child);
      }

      return result;
    }

    @Override
    protected InputStream doOpenInputStream()
    {
      throw new UnsupportedOperationException();
    }
  }

  /**
   * @author Eike Stepper
   */
  private static class Element extends CDOTransferElement
  {
    private CDOResourceNode node;

    public Element(CDOTransferSystem system, CDOResourceNode node)
    {
      super(system);
      this.node = node;
    }

    @Override
    public Object getNativeObject()
    {
      return node;
    }

    @Override
    public IPath getPath()
    {
      return new Path(node.getPath());
    }

    @Override
    public boolean isDirectory()
    {
      return node instanceof CDOResourceFolder;
    }

    @Override
    protected CDOTransferElement[] doGetChildren()
    {
      EList<CDOResourceNode> children = ((CDOResourceFolder)node).getNodes();
      int size = children.size();
      CDOTransferElement[] result = new Element[size];

      for (int i = 0; i < size; i++)
      {
        CDOResourceNode child = children.get(i);
        result[i] = new Element(getSystem(), child);
      }

      return result;
    }

    @Override
    protected InputStream doOpenInputStream()
    {
      try
      {
        if (node instanceof CDOBinaryResource)
        {
          CDOBinaryResource resource = (CDOBinaryResource)node;
          return resource.getContents().getContents();
        }

        if (node instanceof CDOTextResource)
        {
          CDOTextResource resource = (CDOTextResource)node;
          Reader reader = resource.getContents().getContents();
          CharArrayWriter buffer = new CharArrayWriter(); // TODO Make more scalable

          try
          {
            IOUtil.copyCharacter(reader, buffer);
          }
          finally
          {
            IOUtil.close(reader);
          }

          String encoding = resource.getEncoding();
          byte[] bytes = buffer.toString().getBytes(encoding);
          return new ByteArrayInputStream(bytes);
        }
      }
      catch (IOException ex)
      {
        throw new IORuntimeException(ex);
      }

      throw new IllegalStateException("Not a file resource: " + node);
    }
  }
}
