/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.view.AbstractCDOViewProvider;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.util.container.FactoryNotFoundException;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.security.IPasswordCredentialsProvider;
import org.eclipse.net4j.util.security.PasswordCredentialsProvider;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Eike Stepper
 * @since 4.0
 */
public abstract class CDONet4jViewProvider extends AbstractCDOViewProvider
{
  private String transport;

  public CDONet4jViewProvider(String transport, int priority)
  {
    super("cdo\\.net4j\\." + transport + "://.*", priority);
    this.transport = transport;
  }

  public CDOView getView(URI uri, ResourceSet resourceSet)
  {
    CDOURIData data = new CDOURIData(uri);

    IConnector connector = getConnector(data.getAuthority());
    CDOSession session = getSession(connector, data.getUserName(), data.getPassWord(), data.getRepositoryName());

    String branchPath = data.getBranchPath().toPortableString();
    CDOBranch branch = session.getBranchManager().getBranch(branchPath);
    long timeStamp = data.getTimeStamp();

    if (data.isTransactional())
    {
      return session.openTransaction(branch, resourceSet);
    }

    return session.openView(branch, timeStamp, resourceSet);
  }

  @Override
  public URI getResourceURI(CDOView view, String path)
  {
    StringBuilder builder = new StringBuilder();
    builder.append("cdo.net4j.");
    builder.append(transport);
    builder.append("://");

    CDOSession session = (CDOSession)view.getSession();

    // CDOAuthenticator authenticator = ((InternalCDOSession)session).getAuthenticator();
    // IPasswordCredentialsProvider credentialsProvider = authenticator.getCredentialsProvider();
    // if (credentialsProvider != null)
    // {
    // IPasswordCredentials credentials = credentialsProvider.getCredentials();
    // builder.append(credentials.getUserID());
    //
    // char[] password = credentials.getPassword();
    // if (password != null)
    // {
    // builder.append(":");
    // builder.append(password);
    // }
    //
    // builder.append("@");
    // }

    IConnector connector = (IConnector)session.options().getProtocol().getChannel().getMultiplexer();
    String repositoryName = session.getRepositoryInfo().getName();
    append(builder, connector, repositoryName);

    if (!path.startsWith("/"))
    {
      builder.append("/");
    }

    builder.append(path);

    int params = 0;

    String branchPath = view.getBranch().getPathName();
    if (!CDOBranch.MAIN_BRANCH_NAME.equalsIgnoreCase(branchPath))
    {
      builder.append(params++ == 0 ? "?" : "&");
      builder.append(CDOURIData.BRANCH_PARAMETER);
      builder.append("=");
      builder.append(branchPath);
    }

    long timeStamp = view.getTimeStamp();
    if (timeStamp != CDOBranchPoint.UNSPECIFIED_DATE)
    {
      builder.append(params++ == 0 ? "?" : "&");
      builder.append(CDOURIData.TIME_PARAMETER);
      builder.append("=");
      builder.append(new SimpleDateFormat().format(new Date(timeStamp)));
    }

    if (!view.isReadOnly())
    {
      builder.append(params++ == 0 ? "?" : "&");
      builder.append(CDOURIData.TRANSACTIONAL_PARAMETER);
      builder.append("=true");
    }

    return URI.createURI(builder.toString());
  }

  protected String getURIAuthority(IConnector connector)
  {
    String url = connector.getURL().toString();
    return URI.createURI(url).authority();
  }

  protected CDOSession getSession(IConnector connector, String userName, String passWord, String repositoryName)
  {
    CDOSessionConfiguration configuration = getSessionConfiguration(connector, userName, passWord, repositoryName);
    return configuration.openSession();
  }

  protected CDOSessionConfiguration getSessionConfiguration(IConnector connector, String userName, String passWord,
      String repositoryName)
  {
    CDOSessionConfiguration configuration = CDONet4jUtil.createSessionConfiguration();
    configuration.setConnector(connector);
    configuration.setRepositoryName(repositoryName);

    IPasswordCredentialsProvider credentialsProvider = null;
    if (userName != null && passWord != null)
    {
      credentialsProvider = new PasswordCredentialsProvider(userName, passWord);
    }
    else
    {
      StringBuilder builder = new StringBuilder();
      append(builder, connector, repositoryName);
      String resource = builder.toString();

      try
      {
        credentialsProvider = (IPasswordCredentialsProvider)getContainer().getElement(
            "org.eclipse.net4j.util.credentialsProviders", "password", resource);
      }
      catch (FactoryNotFoundException ex)
      {
        // Ignore
      }
    }

    configuration.getAuthenticator().setCredentialsProvider(credentialsProvider);
    return configuration;
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

  private void append(StringBuilder builder, IConnector connector, String repositoryName)
  {
    String authority = getURIAuthority(connector);
    builder.append(authority);

    builder.append("/");
    builder.append(repositoryName);
  }

  /**
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
}
