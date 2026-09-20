/*
 * Copyright (c) 2025-2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.doc.programmers.client;

import org.eclipse.emf.cdo.CDOAdapter;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.util.CDOURIData;
import org.eclipse.emf.cdo.util.CDOURIUtil;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.AbstractCDOViewProvider;
import org.eclipse.emf.cdo.view.CDOAdapterPolicy;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewProvider;
import org.eclipse.emf.cdo.view.CDOViewProviderRegistry;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

/**
 * Integrating with EMF and Other Frameworks
 * <p>
 * CDO is an EMF persistence and collaboration layer. CDO model objects are still EMF {@link EObject EObjects}, and CDO
 * resources still participate in EMF {@link ResourceSet resource sets}. The CDO-specific part is the context around
 * those objects: a {@link CDOSession session} connects to a repository, a {@link CDOView view} defines the branch and
 * repository time that is visible, and a transaction supplies the writable view.
 * <p>
 * This chapter concentrates on integration mechanisms: resource sets, CDO URIs, view providers, EMF adapters and
 * commands, and supported extension points. Session configuration, view semantics, transactions and notification
 * details belong to {@link Doc03_WorkingWithSessions}, {@link Doc04_WorkingWithViews},
 * {@link Doc05_WorkingWithTransactions}, and {@link Doc09_NotificationsAndEventHandling}.
 * <p>
 * <b>Table of Contents</b> {@toc}
 *
 * @author Eike Stepper
 */
public class Doc10_IntegratingWithEMFAndOtherFrameworks
{
  /**
   * CDO in the EMF Programming Model
   * <p>
   * Use generated EMF interfaces, {@link EObject}, {@link Resource}, {@link ResourceSet}, and
   * {@link EcoreUtil} with CDO objects where their contracts apply. A CDO object is not a
   * second, unrelated model type. It is an EMF object whose state is managed by a CDO view and backed by repository
   * revisions.
   * <p>
   * The important difference is context. A read-only view rejects mutations, a transaction records mutations until
   * commit or rollback, and a historical view exposes a different revision from a floating view. Objects can be loaded
   * lazily and can be invalidated by repository changes. Code that needs CDO-specific state can use
   * {@link CDOUtil#getCDOObject(EObject)} or {@link CDOUtil#getView(Notifier)}; code that
   * only needs EMF behavior should remain expressed in EMF terms.
   */
  public class CDOAndEMF
  {
  }

  /**
   * ResourceSet Integration
   * <p>
   * A view owns a CDO view set, and the view set owns exactly one {@link ResourceSet}. Obtain that resource set from
   * {@link CDOView#getResourceSet()} or pass an application-created resource set when opening the view. The same set
   * can contain ordinary EMF resources as well as CDO resources when the application has configured the corresponding
   * resource factories.
   * <p>
   * Loading through {@link ResourceSet#getResource(URI, boolean)} is the normal EMF integration point. For a resource
   * already associated with a view, {@link CDOView#getResource(String)} is also convenient. Do not mistake a resource
   * factory registration for opening a session: a connection, view, and repository context must still be supplied by a
   * directly opened view or by a matching view provider.
   * <p>
   * The resource set is an ownership boundary. Keep it alive while its CDO resources and objects are in use, and close
   * the associated view or transaction before disposing the application component that owns the set. A resource that
   * remains referenced after its view is closed is not a substitute for a live CDO view.
   * <p>
   * {@link #loadResourceFromView(CDOView, String) LoadResourceFromView.java} shows the direct pattern.
   */
  public class ResourceSets
  {
    /**
     * Loads a CDO resource through the resource set associated with a view.
     *
     * @param view the view that owns the resource set
     * @param path the repository resource path, for example {@code /models/example.ecore}
     * @return the loaded CDO resource
     * @snip
     */
    public CDOResource loadResourceFromView(CDOView view, String path)
    {
      ResourceSet resourceSet = view.getResourceSet();
      URI uri = view.createResourceURI(path);
      Resource resource = resourceSet.getResource(uri, true);
      return (CDOResource)resource;
    }

    /**
     * Creates a resource set for an explicitly opened view.
     *
     * @return a new EMF resource set
     * @snip
     */
    public ResourceSet createResourceSet()
    {
      return new ResourceSetImpl();
    }
  }

  /**
   * CDO URIs
   * <p>
   * CDO has a canonical {@code cdo://repositoryUUID/resource/path} form and connection-aware forms such as
   * {@code cdo.net4j.tcp://host:2036/repository/resource/path}. The canonical form identifies a repository by UUID and
   * requires the resource set to be associated with a view that can resolve that repository. A connection-aware URI
   * contains transport information and a repository name, so a matching built-in provider can open the session and view.
   * <p>
   * Connection-aware URIs can carry {@code branch}, {@code time}, {@code transactional}, and {@code prefetch} query
   * parameters. A branch path is relative to the branch tree; {@code time=HEAD} denotes the floating current state;
   * {@code transactional=true} requests a transaction and is not valid with a historical time. The current Net4j
   * providers use the {@code cdo.net4j.jvm}, {@code cdo.net4j.tcp}, {@code cdo.net4j.ssl}, {@code cdo.net4j.ws}, and
   * {@code cdo.net4j.wss} schemes.
   * <p>
   * Use {@link CDOView#createResourceURI(String)} when a view already exists. Use {@link CDOURIData} to inspect a
   * connection-aware URI and {@link CDOURIUtil#extractResourcePath(URI)} or {@link CDOURIUtil#analyzePath(URI)} to
   * normalize its resource path. Avoid hand-parsing authority and query strings, and do not introduce the deprecated
   * {@link CDOURIUtil#createResourceURI(String, String)} helpers into new code.
   * <p>
   * {@link #inspectURI(URI) InspectURI.java} illustrates the public parser.
   */
  public class CDOURIs
  {
    /**
     * Reads the repository and resource portions of a connection-aware URI.
     *
     * @param uri a CDO URI such as {@code cdo.net4j.tcp://host:2036/repository/models/example.ecore?branch=MAIN}
     * @return a concise description of the parsed URI
     * @snip
     */
    public String inspectURI(URI uri)
    {
      CDOURIData data = new CDOURIData(uri);
      return data.getScheme() + ":" + data.getRepositoryName() + CDOURIUtil.SEGMENT_SEPARATOR
          + data.getResourcePath().toPortableString();
    }
  }

  /**
   * View Providers
   * <p>
   * A {@link CDOViewProvider} adapts URI-driven EMF loading to CDO. When {@link ResourceSet#getResource(URI, boolean)}
   * encounters a URI, the {@link CDOViewProviderRegistry} selects matching providers by regular expression and priority.
   * The same selection can be requested explicitly with {@link CDOViewProviderRegistry#provideView(URI, ResourceSet)}.
   * The selected provider returns a view associated with that resource set; the CDO resource factory then serves the
   * resource transparently to the EMF caller.
   * <p>
   * Built-in Net4j providers are contributed by the {@code org.eclipse.emf.cdo.net4j} plug-in. Applications should use
   * them for the standard transport schemes and use a custom provider only when an application-specific URI scheme or
   * repository selection policy is needed. A provider is an SPI implementation of a public interface, not a replacement
   * for ordinary direct session and view APIs.
   * <p>
   * The registry reuses a view already present in the resource set's view set when possible. Otherwise the provider
   * opens a view and the view set associates it with the resource set; the registry does not provide a general provider
   * cache or transfer ownership of the session. The component that owns the view and session must close them, and must
   * not keep using CDO resources after that lifecycle has ended.
   * <p>
   * {@link #createProvider(CDOSession, String) CreateProvider.java} uses an injected session so that the provider does
   * not hide connector and session configuration. The repository name is read from {@link URI#authority()}, without
   * retaining the leading colon. This corrects the former legacy provider pattern without reactivating that excluded
   * material.
   */
  public class ViewProviders
  {
    /**
     * Creates a provider for a preconfigured session and an application-specific URI scheme.
     *
     * @param session the session owned by the application
     * @param repositoryName the repository selected by the application
     * @return a provider that maps {@code cdo.local://repository/path} URIs to the session
     * @snip
     */
    public CDOViewProvider createProvider(CDOSession session, String repositoryName)
    {
      return new AbstractCDOViewProvider("cdo\\.local://.*", 1000)
      {
        @Override
        public CDOView getView(URI uri, ResourceSet resourceSet)
        {
          if (!repositoryName.equals(uri.authority()))
          {
            return null;
          }

          return session.openTransaction(resourceSet);
        }

        @Override
        public URI getResourceURI(CDOView view, String path)
        {
          URI uri = URI.createHierarchicalURI("cdo.local", repositoryName, null, null, null);
          return CDOURIUtil.appendResourcePath(uri, path);
        }
      };
    }

    /**
     * Adds a provider to the global registry.
     *
     * @param provider the provider to register
     * @snip
     */
    public void registerProvider(CDOViewProvider provider)
    {
      CDOViewProviderRegistry.INSTANCE.addViewProvider(provider);
    }

    /**
     * Removes a provider previously added by the application.
     *
     * @param provider the provider to remove
     * @snip
     */
    public void unregisterProvider(CDOViewProvider provider)
    {
      CDOViewProviderRegistry.INSTANCE.removeViewProvider(provider);
    }
  }

  /**
   * View-Provider Extension Point
   * <p>
   * Plug-in applications can contribute providers with the {@code org.eclipse.emf.cdo.viewProviders} extension point.
   * Each {@code viewProvider} specifies a public implementation class, a URI regular expression, and an optional
   * integer priority (default {@code 500}); higher priorities win when several providers match. The implementation
   * must have a usable no-argument construction path for the extension mechanism. Programmatic registration is more
   * suitable when the provider needs application-owned state such as an already configured session.
   * <p>
   * The extension point is declared by the CDO plug-in and its schema is {@code schema/viewProviders.exsd}. Keep the
   * regular expression narrow enough not to capture another provider's URI space. Remove programmatically registered
   * providers when the contributing component is disposed. Provider registration does not transfer ownership of a
   * session, connector, view, or resource set.
   */
  public class ExtensionPoints
  {
  }

  /**
   * Standard EMF Tools and Editing
   * <p>
   * Generated model APIs, EMF traversal, adapters, and utilities such as {@link EcoreUtil}
   * remain useful with CDO-backed objects. EMF Edit item-provider and command infrastructure can be used when the
   * relevant edit plug-ins and adapter factories are present. A command that mutates a CDO object must execute against
   * a writable CDO transaction, and the command stack's undo/redo history must not outlive the transaction context it
   * records.
   * <p>
   * CDO transactions are not EMF Transaction framework transactions. CDO does not require a special
   * {@code EditingDomain} for normal client access, and an application must not assume that an EMF transactional editing
   * domain supplies CDO commit, locking, conflict, or branch semantics. If an application uses a transactional editing
   * framework, treat it as an additional coordination layer and verify its command and notification assumptions.
   * <p>
   * Serialization is also contextual: saving a CDO resource through its CDO resource implementation is not the same
   * as serializing a detached object graph to an ordinary file. Use a CDO resource URI and its owning view when the
   * intent is repository access; use ordinary EMF resources when the intent is a standalone interchange file.
   */
  public class EMFToolsAndEditing
  {
  }

  /**
   * Adapters and Remote Changes
   * <p>
   * Ordinary EMF {@link Adapter adapters} can be attached to CDO objects. CDO-specific {@link CDOAdapter adapters}
   * and {@link CDOAdapterPolicy} change-subscription policies are available when an application needs selected remote
   * changes delivered to adapters. A policy controls subscription and delivery; it does not replace passive updates or
   * view invalidation.
   * <p>
   * Remove adapters and policies when the observing component is disposed. Do not infer that the repository is unchanged
   * merely because an adapter did not receive a notification: objects may not be loaded or subscribed. For invalidation,
   * subscription, and event ordering details, see {@link Doc09_NotificationsAndEventHandling}.
   */
  public class AdapterIntegration
  {
    /**
     * Installs a CDO adapter and enables the matching CDO policy.
     *
     * @param object the observed object
     * @param view the view in which the object is observed
     * @param adapter the adapter to install
     * @snip
     */
    public void installAdapter(CDOObject object, CDOView view, CDOAdapter adapter)
    {
      object.eAdapters().add(adapter);
      view.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.CDO);
    }
  }

  /**
   * UI and Other Frameworks
   * <p>
   * An Eclipse editor or viewer can consume a CDO resource through the same EMF resource and adapter-factory
   * contracts as other EMF resources. UI plug-ins may add selection, navigation, and editor integrations, but those
   * facilities remain UI concerns and should not be confused with the core URI/provider mechanism described here.
   * <p>
   * CDO currently has no general-purpose adapter layer for arbitrary third-party frameworks. Integrate such a framework
   * through its documented EMF APIs, provide the required adapter factories, and make its lifecycle follow the CDO view
   * and resource-set lifecycle. Do not assume that a framework that accepts EMF objects also understands lazy loading,
   * read-only views, invalidation, or CDO transactions without an integration-specific adapter.
   */
  public class UIAndOtherFrameworks
  {
  }

  /**
   * Lifecycle and Choosing an Integration
   * <p>
   * Direct session/view APIs are the clearest choice when the application owns connection setup, view reuse, commits,
   * and shutdown. Use a view's resource set when an EMF-based component needs CDO resources while the application still
   * controls the view. Use URI and view-provider integration when a component naturally calls
   * {@link ResourceSet#getResource(URI, boolean)} and cannot be taught CDO session APIs; make the provider's session and
   * view ownership explicit.
   * <p>
   * Use adapters and listeners for observation, with the cleanup rules in {@link Doc09_NotificationsAndEventHandling}.
   * Use the extension point for plug-in-discoverable provider implementations and programmatic registration for
   * application-scoped providers. In every case, close the views and sessions owned by the integration component, remove
   * providers owned by that component, and keep resource sets and command stacks within the lifetime of their CDO view
   * or transaction.
   */
  public class ChoosingAnIntegration
  {
  }
}
