/*
 * Copyright (c) 2010-2012, 2015, 2016, 2019, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.net4j;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.util.CDOURIData;
import org.eclipse.emf.cdo.util.CDOURIUtil;
import org.eclipse.emf.cdo.util.InvalidURIException;
import org.eclipse.emf.cdo.view.AbstractCDOViewProvider;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewProvider;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.util.container.FactoryNotFoundException;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.security.CredentialsProviderFactory;
import org.eclipse.net4j.util.security.IPasswordCredentialsProvider;
import org.eclipse.net4j.util.security.PasswordCredentialsProvider;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * A {@link CDOViewProvider view provider} that uses Net4j-specific CDO {@link CDONet4jSession sessions} to open views.
 *
 * @author Eike Stepper
 * @since 4.0
 */
public abstract class CDONet4jViewProvider extends AbstractCDOViewProvider
{
  /**
   * @since 4.3
   */
  protected final String transport;

  private final String protocol;

  public CDONet4jViewProvider(String transport, int priority)
  {
    super("cdo\\.net4j\\." + transport + "://.*", priority);
    this.transport = transport;
    protocol = "cdo.net4j." + transport;
  }

  @Override
  public String getPath(URI uri)
  {
    return new Path(uri.path()).makeAbsolute().removeFirstSegments(1).toString();
  }

  @Override
  public CDOView getView(URI uri, ResourceSet resourceSet)
  {
    CDOURIData data = createURIData(uri);
    String authority = data.getAuthority();
    String userName = data.getUserName();
    String passWord = data.getPassWord();
    String repositoryName = data.getRepositoryName();

    IConnector connector = getConnector(authority);
    CDONet4jSession session = getNet4jSession(connector, userName, passWord, repositoryName);

    String viewID = data.getViewID();
    if (viewID != null)
    {
      if (data.isTransactional())
      {
        return session.openTransaction(viewID, resourceSet);
      }

      return session.openView(viewID, resourceSet);
    }

    String branchPath = data.getBranchPath().toPortableString();
    CDOBranch branch = session.getBranchManager().getBranch(branchPath);

    if (data.isTransactional())
    {
      return session.openTransaction(branch, resourceSet);
    }

    long timeStamp = data.getTimeStamp();
    return session.openView(branch, timeStamp, resourceSet);
  }

  @Override
  public URI getViewURI(URI uri)
  {
    if (uri == null)
    {
      return null;
    }

    String scheme = uri.scheme();
    if (scheme == null)
    {
      return null;
    }

    if (!scheme.equals(protocol))
    {
      return null;
    }

    // Remove all (resource path) segments and leave only the repository name segment.
    return uri.trimSegments(uri.segmentCount() - 1);
  }

  @Override
  public URI getResourceURI(CDOView view, String path)
  {
    String branchPath = view.getBranch().getPathName();
    long timeStamp = view.getTimeStamp();
    boolean readOnly = view.isReadOnly();

    CDONet4jSession session = (CDONet4jSession)view.getSession();
    IChannel channel = session.options().getNet4jProtocol().getChannel();
    if (channel == null)
    {
      return null;
    }

    IConnector connector = (IConnector)channel.getMultiplexer();
    String authority = getURIAuthority(connector);
    String repositoryName = session.getRepositoryInfo().getName();

    return getResourceURI(transport, authority, repositoryName, path, branchPath, timeStamp, readOnly);
  }

  /**
   * @since 4.4
   */
  public URI getResourceURI(String transport, String authority, String repositoryName, String resourcePath, String branchPath, long timeStamp, boolean readOnly)
  {
    StringBuilder query = new StringBuilder();

    if (!CDOBranch.MAIN_BRANCH_NAME.equalsIgnoreCase(branchPath))
    {
      CDOURIUtil.appendQueryParameter(query, CDOURIData.BRANCH_PARAMETER, branchPath);
    }

    if (timeStamp != CDOBranchPoint.UNSPECIFIED_DATE)
    {
      CDOURIUtil.appendQueryParameter(query, CDOURIData.TIME_PARAMETER, Long.toString(timeStamp));
    }

    if (!readOnly)
    {
      CDOURIUtil.appendQueryParameter(query, CDOURIData.TRANSACTIONAL_PARAMETER, "true");
    }

    String[] servicePathSegments = null;
    if (authority.contains(CDOURIUtil.SEGMENT_SEPARATOR))
    {
      URI uri = URI.createURI(transport + "://" + authority);
      authority = uri.authority();
      servicePathSegments = uri.segments();
    }

    URI uri = URI.createHierarchicalURI("cdo.net4j." + transport, authority, null, query.toString(), null);

    if (servicePathSegments != null && servicePathSegments.length > 0)
    {
      uri = uri.appendSegments(servicePathSegments);
    }
    uri = uri.appendSegment(repositoryName);
    return CDOURIUtil.appendResourcePath(uri, resourcePath);
  }

  /**
   * @since 4.3
   */
  protected CDOURIData createURIData(URI uri)
  {
    return new CDOURIData(uri);
  }

  protected String getURIAuthority(IConnector connector)
  {
    String url = connector.getURL().toString();
    return URI.createURI(url).authority();
  }

  /**
   * @since 4.1
   */
  protected CDONet4jSession getNet4jSession(IConnector connector, String userName, String passWord, String repositoryName)
  {
    CDONet4jSessionConfiguration configuration = getNet4jSessionConfiguration(connector, userName, passWord, repositoryName);
    return configuration.openNet4jSession();
  }

  /**
   * @since 4.1
   */
  protected CDONet4jSessionConfiguration getNet4jSessionConfiguration(IConnector connector, String userName, String passWord, String repositoryName)
  {
    CDONet4jSessionConfiguration configuration = CDONet4jUtil.createNet4jSessionConfiguration();
    configuration.setConnector(connector);
    configuration.setRepositoryName(repositoryName);

    IPasswordCredentialsProvider credentialsProvider = null;
    if (userName != null && passWord != null)
    {
      credentialsProvider = new PasswordCredentialsProvider(userName, passWord);
    }
    else
    {
      String authority = getURIAuthority(connector);
      String resource = authority + CDOURIUtil.SEGMENT_SEPARATOR + repositoryName;

      try
      {
        credentialsProvider = (IPasswordCredentialsProvider)getContainer().getElement(CredentialsProviderFactory.PRODUCT_GROUP, "password", resource);
      }
      catch (FactoryNotFoundException ex)
      {
        // Ignore
      }

      // The following is to stay compatible with the formerly wrong product group (".security" was missing).
      if (credentialsProvider == null)
      {
        try
        {
          credentialsProvider = (IPasswordCredentialsProvider)getContainer().getElement("org.eclipse.net4j.util.credentialsProviders", "password", resource);
        }
        catch (FactoryNotFoundException ex)
        {
          // Ignore
        }
      }
    }

    configuration.setCredentialsProvider(credentialsProvider);
    return configuration;
  }

  /**
   * @deprecated Use {@link #getNet4jSession(IConnector, String, String, String) getNet4jSession()}.
   */
  @Deprecated
  protected CDOSession getSession(IConnector connector, String userName, String passWord, String repositoryName)
  {
    return (CDOSession)getNet4jSession(connector, userName, passWord, repositoryName);
  }

  /**
   * @deprecated Use {@link #getNet4jSessionConfiguration(IConnector, String, String, String)
   *             getNet4jSessionConfiguration()}.
   */
  @Deprecated
  protected CDOSessionConfiguration getSessionConfiguration(IConnector connector, String userName, String passWord, String repositoryName)
  {
    return (CDOSessionConfiguration)getNet4jSessionConfiguration(connector, userName, passWord, repositoryName);
  }

  protected IManagedContainer getContainer()
  {
    return IPluginContainer.INSTANCE;
  }

  protected IConnector getConnector(String authority)
  {
    IManagedContainer container = getContainer();
    String description = getConnectorDescription(authority);
    return Net4jUtil.getConnector(container, transport, description);
  }

  protected String getConnectorDescription(String authority)
  {
    return authority;
  }

  /**
   * A JVM-based {@link CDONet4jViewProvider view provider}.
   *
   * @author Eike Stepper
   */
  public static class JVM extends CDONet4jViewProvider
  {
    public JVM(int priority)
    {
      super("jvm", priority);
    }

    public JVM()
    {
      this(DEFAULT_PRIORITY);
    }
  }

  /**
   * A TCP-based {@link CDONet4jViewProvider view provider}.
   *
   * @author Eike Stepper
   */
  public static class TCP extends CDONet4jViewProvider
  {
    public TCP(int priority)
    {
      super("tcp", priority);
    }

    public TCP()
    {
      this(DEFAULT_PRIORITY);
    }
  }

  /**
   * An SSL-based {@link CDONet4jViewProvider view provider}.
   *
   * @author Teerawat Chaiyakijpichet (No Magic Asia Ltd.)
   */
  public static class SSL extends CDONet4jViewProvider
  {
    public SSL(int priority)
    {
      super("ssl", priority);
    }

    public SSL()
    {
      this(DEFAULT_PRIORITY);
    }
  }

  /**
   * A WS-based {@link CDONet4jViewProvider view provider}.
   *
   * @author Eike Stepper
   * @since 4.3
   */
  public static class WS extends CDONet4jViewProvider
  {
    public static final String ACCEPTOR_NAME_PREFIX = "@";

    public WS(int priority)
    {
      super("ws", priority);
    }

    public WS()
    {
      this(DEFAULT_PRIORITY);
    }

    @Override
    public String getPath(URI uri)
    {
      IPath path = new Path(uri.path());

      int index = getAcceptorSegmentIndex(path);
      if (index == -1)
      {
        throw new InvalidURIException(uri);
      }

      return path.makeAbsolute().removeFirstSegments(index + 2).toString();
    }

    @Override
    protected String getURIAuthority(IConnector connector)
    {
      String url = connector.getURL().toString();

      String prefix = transport + "://";
      if (url.startsWith(prefix))
      {
        url = url.substring(prefix.length());
      }

      return url;
    }

    @Override
    protected CDOURIData createURIData(URI uri)
    {
      CDOURIData data = super.createURIData(uri);
      IPath path = new Path(data.getRepositoryName()).append(data.getResourcePath());

      int index = getAcceptorSegmentIndex(path);
      if (index == -1)
      {
        throw new InvalidURIException(uri);
      }

      String authority = data.getAuthority();
      for (int j = 0; j <= index; j++)
      {
        authority += "/" + path.segment(j);
      }

      data.setAuthority(authority);
      data.setRepositoryName(path.segment(index + 1));
      data.setResourcePath(path.removeFirstSegments(index + 2));
      return data;
    }

    protected int getAcceptorSegmentIndex(IPath path)
    {
      for (int i = 0; i < path.segmentCount(); i++)
      {
        if (path.segment(i).startsWith(ACCEPTOR_NAME_PREFIX))
        {
          return i;
        }
      }

      return -1;
    }
  }
}
