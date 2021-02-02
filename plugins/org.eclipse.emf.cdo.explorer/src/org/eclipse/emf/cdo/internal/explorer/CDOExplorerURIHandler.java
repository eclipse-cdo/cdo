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
package org.eclipse.emf.cdo.internal.explorer;

import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.lob.CDOBlob;
import org.eclipse.emf.cdo.common.lob.CDOClob;
import org.eclipse.emf.cdo.common.lob.CDOLobStore;
import org.eclipse.emf.cdo.common.util.CDOException;
import org.eclipse.emf.cdo.common.util.CDOResourceNodeNotFoundException;
import org.eclipse.emf.cdo.eresource.CDOBinaryResource;
import org.eclipse.emf.cdo.eresource.CDOResourceLeaf;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.eresource.CDOTextResource;
import org.eclipse.emf.cdo.explorer.CDOExplorerUtil;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.spi.common.util.URIHandlerFactory;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.internal.cdo.view.CDOURIHandler;

import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.om.OMPlatform;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.URIHandler;
import org.eclipse.emf.ecore.resource.impl.URIHandlerImpl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public abstract class CDOExplorerURIHandler<NODE extends CDOResourceNode> extends URIHandlerImpl
{
  private static final boolean OMIT_CHECKOUT_FILE_URI_HANDLERS = OMPlatform.INSTANCE.isProperty("org.eclipse.emf.cdo.explorer.omitCheckoutFileURIHandlers");

  private final String scheme;

  protected CDOExplorerURIHandler(String scheme)
  {
    this.scheme = scheme;
  }

  public final String getScheme()
  {
    return scheme;
  }

  @Override
  public boolean canHandle(URI uri)
  {
    return !OMIT_CHECKOUT_FILE_URI_HANDLERS;
  }

  @Override
  public boolean exists(URI uri, Map<?, ?> options)
  {
    CDOView view = getView(uri);
    NODE node = null;

    try
    {
      node = getNode(view, uri.path());
    }
    catch (CDOResourceNodeNotFoundException ex)
    {
      //$FALL-THROUGH$
    }

    return node != null;
  }

  @Override
  public Map<String, ?> getAttributes(URI uri, Map<?, ?> options)
  {
    CDOView view = getView(uri);
    Map<String, ?> attributes = CDOURIHandler.getAttributes(view, uri.path(), options);

    // The view of a checkout is always read-only, so remove this attribute.
    attributes.remove(URIConverter.ATTRIBUTE_READ_ONLY);

    return attributes;
  }

  @Override
  public InputStream createInputStream(URI uri, Map<?, ?> options) throws IOException
  {
    CDOView view = getView(uri);
    NODE node = getNode(view, uri.path());
    return createInputStream(node);
  }

  @Override
  public OutputStream createOutputStream(URI uri, Map<?, ?> options) throws IOException
  {
    return new ByteArrayOutputStream()
    {
      @Override
      public void close() throws IOException
      {
        super.close();
        byte[] bytes = toByteArray();

        CDOCheckout checkout = getCheckout(uri);
        CDOLobStore lobStore = checkout.getView().getSession().options().getLobCache();

        modify(checkout, uri.path(), true, node -> setContents(node, bytes, lobStore));
      }
    };
  }

  @Override
  public void delete(URI uri, Map<?, ?> options) throws IOException
  {
    CDOCheckout checkout = getCheckout(uri);
    modify(checkout, uri.path(), false, node -> node.delete(options));
  }

  protected abstract NODE getNode(CDOView view, String path) throws CDOResourceNodeNotFoundException;

  protected abstract NODE getOrCreateNode(CDOTransaction transaction, String path);

  protected abstract void setContents(NODE node, byte[] bytes, CDOLobStore lobStore) throws IOException;

  protected InputStream createInputStream(NODE node) throws IOException
  {
    if (node instanceof CDOResourceLeaf)
    {
      return CDOUtil.openInputStream((CDOResourceLeaf)node);
    }

    throw new IllegalArgumentException("Not a resource leaf: " + node);
  }

  private CDOCheckout getCheckout(URI uri)
  {
    String id = uri.authority();

    CDOCheckout checkout = CDOExplorerUtil.getCheckout(id);
    if (checkout == null)
    {
      throw new CDOException("No checkout exists for " + uri);
    }

    if (!checkout.isOpen())
    {
      throw new CDOException("Checkout '" + checkout.getLabel() + "' is not open");
    }

    return checkout;
  }

  private CDOView getView(URI uri)
  {
    CDOCheckout checkout = getCheckout(uri);
    return checkout.getView();
  }

  private void modify(CDOCheckout checkout, String path, boolean createOnDemand, ResourceNodeModifier<NODE> modifier) throws IOException
  {
    CDOTransaction transaction = null;

    try
    {
      transaction = checkout.openTransaction();

      NODE node = createOnDemand ? getOrCreateNode(transaction, path) : getNode(transaction, path);
      modifier.modify(node);

      CDOCommitInfo commitInfo = transaction.commit();
      if (commitInfo != null)
      {
        CDOView view = checkout.getView();
        view.waitForUpdate(commitInfo.getTimeStamp());
      }
    }
    catch (Exception ex)
    {
      throw IOUtil.ioException(ex);
    }
    finally
    {
      IOUtil.closeSilent(transaction);
    }
  }

  private static String getScheme(CDOResourceNode node)
  {
    if (node instanceof CDOTextResource)
    {
      return TextURIHandler.SCHEME;
    }

    if (node instanceof CDOBinaryResource)
    {
      return BinaryURIHandler.SCHEME;
    }

    return null;
  }

  public static URI createURI(CDOResourceNode node)
  {
    CDOCheckout checkout = CDOExplorerUtil.getCheckout(node);
    if (checkout == null)
    {
      return null;
    }

    return createURI(checkout, node);
  }

  public static URI createURI(CDOCheckout checkout, CDOResourceNode node)
  {
    String scheme = getScheme(node);
    if (scheme == null)
    {
      return null;
    }

    String authority = checkout.getID();
    String[] segments = node.getURI().segments();

    return URI.createHierarchicalURI(scheme, authority, null, segments, null, null);
  }

  /**
   * @author Eike Stepper
   */
  @FunctionalInterface
  private interface ResourceNodeModifier<NODE extends CDOResourceNode>
  {
    public void modify(NODE node) throws IOException;
  }

  /**
   * @author Eike Stepper
   */
  public static final class TextURIHandler extends CDOExplorerURIHandler<CDOTextResource>
  {
    public static final String SCHEME = CDOExplorerUtil.URI_SCHEME + ".text";

    public TextURIHandler()
    {
      super(SCHEME);
    }

    @Override
    protected CDOTextResource getNode(CDOView view, String path) throws CDOResourceNodeNotFoundException
    {
      return view.getTextResource(path);
    }

    @Override
    protected CDOTextResource getOrCreateNode(CDOTransaction transaction, String path)
    {
      return transaction.getOrCreateTextResource(path);
    }

    @Override
    protected void setContents(CDOTextResource node, byte[] bytes, CDOLobStore lobStore) throws IOException
    {
      String encoding = node.getEncoding();
      if (encoding == null)
      {
        encoding = StandardCharsets.UTF_8.toString();
      }

      CDOClob clob = new CDOClob(new StringReader(new String(bytes, encoding)), lobStore);
      node.setContents(clob);
    }

    /**
     * @author Eike Stepper
     */
    public static final class Factory extends URIHandlerFactory
    {
      public Factory()
      {
        super(SCHEME);
      }

      @Override
      public URIHandler create(String description) throws ProductCreationException
      {
        return new TextURIHandler();
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class BinaryURIHandler extends CDOExplorerURIHandler<CDOBinaryResource>
  {
    public static final String SCHEME = CDOExplorerUtil.URI_SCHEME + ".binary";

    public BinaryURIHandler()
    {
      super(SCHEME);
    }

    @Override
    protected CDOBinaryResource getNode(CDOView view, String path) throws CDOResourceNodeNotFoundException
    {
      return view.getBinaryResource(path);
    }

    @Override
    protected CDOBinaryResource getOrCreateNode(CDOTransaction transaction, String path)
    {
      return transaction.getOrCreateBinaryResource(path);
    }

    @Override
    protected void setContents(CDOBinaryResource node, byte[] bytes, CDOLobStore lobStore) throws IOException
    {
      CDOBlob blob = new CDOBlob(new ByteArrayInputStream(bytes), lobStore);
      node.setContents(blob);
    }

    /**
     * @author Eike Stepper
     */
    public static final class Factory extends URIHandlerFactory
    {
      public Factory()
      {
        super(SCHEME);
      }

      @Override
      public URIHandler create(String description) throws ProductCreationException
      {
        return new BinaryURIHandler();
      }
    }
  }
}
