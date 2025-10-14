/*
 * Copyright (c) 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 */
package org.eclipse.emf.cdo.doc.programmers.client;

import org.eclipse.emf.cdo.eresource.CDOResourceFactory;
import org.eclipse.emf.cdo.net4j.CDONet4jSessionConfiguration;
import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.AbstractCDOViewProvider;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewProvider;
import org.eclipse.emf.cdo.view.CDOViewProviderRegistry;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.tcp.TCPUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.ManagedContainer;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * View Providers
 * <p>
 * A {@link CDOViewProvider view provider} allows clients to inject custom logic into the {@link CDOResourceFactory
 * resource factory} mechansim, capable of handling the whole {@link CDOSession session} and {@link CDOView view}
 * instantiation process. This permits to obtain {@link Resource resources} through the {@link ResourceSet resource set}
 * API transparently, without any prior CDO client API code. The view provider automatically kicks in the middle of the
 * {@link ResourceSet#getResource(org.eclipse.emf.common.util.URI, boolean) ResourceSet.getResource()} call, forgetting
 * about the whole openning session / openning transaction process, which happens behind the scenes.
 * <p>
 * This is quite useful when integrating CDO with EMF-based frameworks and tools that are not prepared for a CDO
 * scenario themselves.
 * <p>
 * <b>Table of Contents</b> {@toc}
 *
 * @author Victor Roldan Betancort
 * @ignore
 */
public class Doc99_ViewProviders
{
  /**
   * Implementing a View Provider
   * <p>
   * Clients should implement the {@link CDOViewProvider} interface, or sub class the {@link AbstractCDOViewProvider}
   * class, which provides common functionality.
   * <p>
   * The example below shows a simple implementation that opens a <b>new</b> {@link CDOSession session} to a local
   * server and a <b>new</b> {@link CDOTransaction transaction} on that session.
   * <p>
   * {@link #example() ExampleViewProvider.java}
   */
  public class ProviderImplementation
  {
    /**
     * @snip
     * @callout The example provider catches all URIs with shape "cdo.local:<repoName>".
     * @callout Register the provider with {@link CDOViewProviderRegistry}.
     */
    public void example()
    {
      /* callout */new AbstractCDOViewProvider("cdo\\.local:.*", 100)
      {
        private IManagedContainer container;

        /* callout */@Override
        public CDOView getView(URI uri, ResourceSet resourceSet)
        {
          if (container == null)
          {
            container = new ManagedContainer();
            Net4jUtil.prepareContainer(container);
            TCPUtil.prepareContainer(container);
            container.activate();
          }

          int startIndex = uri.toString().indexOf(':');
          String repoName = uri.toString().substring(startIndex);

          IConnector connector = (IConnector)container.getElement("org.eclipse.net4j.connectors", "tcp", "localhost");

          CDONet4jSessionConfiguration config = CDONet4jUtil.createNet4jSessionConfiguration();
          config.setConnector(connector);
          config.setRepositoryName(repoName);

          CDOSession session = config.openNet4jSession();
          return session.openTransaction();
        }
      };
    }
  }

  /**
   * Contributing View Providers Programmatically
   * <p>
   * A client's view provider implementation can be contributed programmatically to the {@link CDOViewProviderRegistry},
   * as the following example suggests:
   * <p>
   * {@link #snippet2() ProviderContribution.java}
   */
  public class ContributeProviderProgrammatically
  {
    /**
     * @snip
     * @callout Get the target {@link CDOViewProvider} implementation.
     * @callout Add the provider instance to {@link CDOViewProviderRegistry}.
     */
    @SuppressWarnings("restriction")
    public void snippet2()
    {
      // Instantiate your view provider
      CDOViewProvider viewProvider = /* callout */org.eclipse.emf.internal.cdo.view.PluginContainerViewProvider.INSTANCE;

      // Add the instance to the registry
      /* callout */CDOViewProviderRegistry.INSTANCE.addViewProvider(viewProvider);
    }
  }

  /**
   * Contributing View Providers Using Extension Points
   * <p>
   * A specific {@link CDOViewProvider} implementation can also be contributed using the
   * <code>org.eclipse.emf.cdo.viewProviders</code> extension point. Clients specify:
   * <p>
   * <ul>
   * <li>A mandatory <b><code>class</code></b> implementing the {@link CDOViewProvider} interface.
   * <li>A mandatory <b>regular expression</b> string indicating the shape of {@link org.eclipse.emf.common.util.URI
   * URIs} that should match with the contributed provider.
   * <li>An optional <b><code>priority</code></b> integer value, to indicate preference over other implementations
   * matching the same regular expression. A higher value indicates a higher priority, {@link Integer#MAX_VALUE} being
   * the maximum priority value and {@link Integer#MIN_VALUE} the minimum.
   * </ul>
   */
  public class ContributeProviderUsingExtensionPoint
  {
  }
}
