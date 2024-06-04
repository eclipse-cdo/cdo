/*
 * Copyright (c) 2011, 2012, 2015, 2019, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Victor Roldan Betancort - maintenance
 */
package org.eclipse.emf.internal.cdo.view;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOURIUtil;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewProvider;
import org.eclipse.emf.cdo.view.CDOViewProvider.CDOViewProvider2;
import org.eclipse.emf.cdo.view.CDOViewProviderRegistry;

import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ContentHandler;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.URIHandler;
import org.eclipse.emf.spi.cdo.InternalCDOView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class CDOURIHandler implements URIHandler
{
  private static final String CDO_URI_SCHEME = CDOURIUtil.PROTOCOL_NAME;

  private InternalCDOView view;

  private CDOViewProvider2 viewProvider2;

  public CDOURIHandler(InternalCDOView view)
  {
    this.view = view;
  }

  public InternalCDOView getView()
  {
    return view;
  }

  @Override
  public boolean canHandle(URI uri)
  {
    if (CDO_URI_SCHEME.equals(uri.scheme()))
    {
      String repositoryUUID = PluginContainerViewProvider.getRepositoryUUID(uri);
      return Objects.equals(repositoryUUID, view.getSession().getRepositoryInfo().getUUID());
    }

    URI viewURI = view.getURI();
    if (viewURI != null)
    {
      URI uri2 = viewProvider2.getViewURI(uri);
      return viewURI == uri2;
    }

    CDOViewProvider provider = view.getProvider();
    if (provider != null)
    {
      CDOViewProvider[] viewProviders = CDOViewProviderRegistry.INSTANCE.getViewProviders(uri);
      for (CDOViewProvider viewProvider : viewProviders)
      {
        if (viewProvider == provider)
        {
          return true;
        }
      }
    }

    return false;
  }

  @Override
  public boolean exists(URI uri, Map<?, ?> options)
  {
    String path = getPath(uri);
    return view.hasResource(path);
  }

  @Override
  public void delete(URI uri, Map<?, ?> options) throws IOException
  {
    modify(uri, node -> node.delete(options));
  }

  @Override
  public InputStream createInputStream(URI uri, Map<?, ?> options) throws IOException
  {
    throw new IOException("CDOURIHandler.createInputStream() not implemented"); //$NON-NLS-1$
  }

  @Override
  public OutputStream createOutputStream(URI uri, Map<?, ?> options) throws IOException
  {
    throw new IOException("CDOURIHandler.createOutputStream() not implemented"); //$NON-NLS-1$
  }

  @Override
  public Map<String, ?> contentDescription(URI uri, Map<?, ?> options) throws IOException
  {
    // ViK: I hardly find this useful for CDO. ContentHandler defines, for instance, VALIDITY_PROPERTY
    // It might make sense in CDO... We could also introduce some CDO-Specific information here...
    return ContentHandler.INVALID_CONTENT_DESCRIPTION;
  }

  @Override
  public Map<String, ?> getAttributes(URI uri, Map<?, ?> options)
  {
    String path = getPath(uri);
    return getAttributes(view, path, options);
  }

  @Override
  public void setAttributes(URI uri, Map<String, ?> attributes, Map<?, ?> options) throws IOException
  {
    // ViK: We can't change any of the proposed attributes. Only TIME_STAMP, and I believe we are not
    // storing that attribute in the server. Due to CDOResouce distributed nature, changing it wouldn't make much sense.
  }

  void setViewProvider2(CDOViewProvider2 viewProvider2)
  {
    this.viewProvider2 = viewProvider2;
  }

  private String getPath(URI uri)
  {
    if (CDO_URI_SCHEME.equals(uri.scheme()))
    {
      return CDOURIUtil.extractResourcePath(uri);
    }

    for (CDOViewProvider viewProvider : CDOViewProviderRegistry.INSTANCE.getViewProviders(uri))
    {
      if (viewProvider instanceof CDOViewProvider2)
      {
        return ((CDOViewProvider2)viewProvider).getPath(uri);
      }
    }

    return null;
  }

  private void modify(URI uri, ResourceNodeModifier modifier) throws IOException
  {
    if (view.isHistorical())
    {
      throw new IOException("View is historical: " + view);
    }

    String path = getPath(uri);
    CDOTransaction transaction = null;

    try
    {
      CDOSession session = view.getSession();
      CDOBranch branch = view.getBranch();
      transaction = session.openTransaction(branch);

      CDOResourceNode node = transaction.getResourceNode(path);
      modifier.modify(node);

      CDOCommitInfo commitInfo = transaction.commit();
      if (commitInfo != null)
      {
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

  @SuppressWarnings("unchecked")
  public static Map<String, ?> getAttributes(CDOView view, String path, Map<?, ?> options)
  {
    Map<String, Object> result = new HashMap<>();

    CDOResourceNode node = view.getResourceNode(path);
    if (node != null)
    {
      Set<String> requestedAttributes = (Set<String>)options.get(URIConverter.OPTION_REQUESTED_ATTRIBUTES);
      if (requestedAttributes == null || requestedAttributes.contains(URIConverter.ATTRIBUTE_TIME_STAMP))
      {
        long stamp = node instanceof CDOResource ? ((CDOResource)node).getTimeStamp() : URIConverter.NULL_TIME_STAMP;
        result.put(URIConverter.ATTRIBUTE_TIME_STAMP, stamp);
      }

      if (requestedAttributes == null || requestedAttributes.contains(URIConverter.ATTRIBUTE_DIRECTORY))
      {
        result.put(URIConverter.ATTRIBUTE_DIRECTORY, node instanceof CDOResourceFolder);
      }

      if (requestedAttributes == null || requestedAttributes.contains(URIConverter.ATTRIBUTE_READ_ONLY))
      {
        result.put(URIConverter.ATTRIBUTE_READ_ONLY, view.isReadOnly());
      }
    }

    return result;
  }

  /**
   * @author Eike Stepper
   */
  @FunctionalInterface
  private interface ResourceNodeModifier
  {
    public void modify(CDOResourceNode node) throws IOException;
  }
}
