/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public License 2.0.
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.doc.programmers.server;

import org.eclipse.emf.cdo.common.util.CDOQueryInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.doc.operators.Doc01_ConfiguringRepositories;
import org.eclipse.emf.cdo.doc.operators.Doc03_ManagingSecurity;
import org.eclipse.emf.cdo.server.IPermissionManager;
import org.eclipse.emf.cdo.server.IQueryContext;
import org.eclipse.emf.cdo.server.IQueryHandler;
import org.eclipse.emf.cdo.server.IRepositoryProtector;
import org.eclipse.emf.cdo.server.IRepositoryProtector.UserAuthenticator;
import org.eclipse.emf.cdo.spi.server.QueryHandlerFactory;
import org.eclipse.emf.cdo.view.CDOQuery;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.container.IManagedContainer;

import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * Security, Queries, and Specialized Extensions
 * <p>
 * Security first authenticates a repository user and then authorizes that identity's operations. Query handling is a
 * separate extension domain with a language/factory contract. Use these supported seams instead of general handlers
 * for authentication, authorization, or a custom query language. Deployment
 * credentials, password files, TLS, and directory-server configuration remain
 * {@link Doc03_ManagingSecurity Operator's Guide topics}.
 * {@toc}
 *
 * @author Eike Stepper
 */
public class Doc08_SecurityQueriesAndSpecializedExtensions
{
  /**
   * Repository Protection
   * <p>
   * {@link IRepositoryProtector} protects a repository by combining a {@link UserAuthenticator}, authorization
   * strategy, revision authorizers, and commit handlers. Authentication answers “which repository user presented
   * these credentials?” Returning {@code null} rejects session opening; a returned identity becomes the user ID in the
   * server session. Authorization answers “what may this identity do?” and is evaluated on repository reads and
   * commits; authentication alone grants no access. Delegate credential verification to application policy rather
   * than embedding password storage in this integration.
   * <p>
   * The authenticator establishes repository user identity;
   * {@link IRepositoryProtector.AuthorizationStrategy} combines permissions; a
   * {@link IRepositoryProtector.RevisionAuthorizer} decides revision permissions; and a
   * {@link IRepositoryProtector.CommitHandler} runs before security checking and after a successful protected commit.
   * In server XML, the repository's {@code <protector type="default">} extension selects a product from
   * {@link IRepositoryProtector#PRODUCT_GROUP}; its child elements select authenticator, authorization-strategy,
   * revision-authorizer, and commit-handler products. This core repository-configurator extension is distinct from
   * the optional security module. When {@code org.eclipse.emf.cdo.server.security} is installed, its application
   * extension recognizes a separate {@code <securityManager type="default" description="...">} element and creates
   * the module's realm-backed security manager; an optional {@code <permissionCache>} child selects its cache creator.
   * See {@link Doc01_ConfiguringRepositories.Element_securityManager} for the canonical manager and cache XML, and
   * {@link Doc01_ConfiguringRepositories#cdoServerXML()} for the surrounding repository configuration. A custom
   * protector product must be registered in its product group; applications do not implement
   * {@code IRepositoryProtector} directly.
   * <p>
   * {@link IPermissionManager} is the legacy permission-management API used by the security model. The optional
   * {@code org.eclipse.emf.cdo.server.security} module supplies its own {@code ISecurityManager}; use its public API
   * when that module is intentionally part of an application, without coupling to its internal realm implementation.
   */
  public class SecurityModel
  {
  }

  /**
   * Permission Cache SPI
   * <p>
   * The optional {@code org.eclipse.emf.cdo.server.security} module provides the {@code PermissionCache} and
   * {@code PermissionCacheFactory} SPIs for applications that need an alternative resource-permission cache. Register
   * a {@code PermissionCacheFactory} product in its managed-container product group, then select its type and optional description with
   * repository properties {@code security.permissionCache.type} and
   * {@code security.permissionCache.description}. The built-in factory type is {@code default}; its description can
   * set creator capacity (for example {@code |capacity=50000}). The optional repository property
   * {@code security.permissionCache.default.capacity} also supplies that capacity when no description suffix is
   * present. Invalid or nonpositive capacity fails factory creation and therefore repository startup. The security extension also accepts one {@code permissionCache} child under
   * {@code securityManager}; omitting its type selects the default cache. The default implementation has a configurable
   * capacity, whose documented default is 100,000 entries. The internal security-manager setter is not an application
   * configuration API.
   * <p>
   * Authorization evaluates resource, class, object, and global policy. The cache stores only a resource node's
   * permission and the baseline inherited by ordinary contained objects; other policy checks remain separate. The
   * security manager caches a resource baseline in two separate slots for each resource-node ID: permission on the
   * resource node itself, and the baseline inherited by ordinary objects in that resource. The built-in resource
   * permissions and resource filters can contribute to this baseline. Composed filters are cacheable only when their
   * operands are cacheable; arbitrary class and package filters are not automatically treated as resource-stable.
   * Custom {@code Permission} and {@code PermissionFilter} implementations are dynamic by default. The implementation
   * has protected resource-cache classification hooks, but they are not a public application extension point; do not
   * subclass an internal security-manager implementation to reach them. Keep custom policy dynamic, especially when
   * it depends on request-local state, transactions, object contents, or {@code AuthorizationContext}.
   * <p>
   * Cached authorization applies only to reads at the current branch head. Historical reads and commit authorization
   * remain uncached. Cache generations are isolated by repository, user, and branch, and relevant realm or
   * resource-tree changes invalidate affected generations. A custom implementation must ensure that writes racing with
   * invalidation cannot populate the replacement generation. This also applies independently to secondary repositories.
   * A dynamic permission that depends on {@code AuthorizationContext} is evaluated outside the cached resource
   * baseline on each authorization.
   * <p>
   * A custom {@code PermissionCache.Creator} must return a fresh logical generation for each repository/user/branch
   * scope and keep late writes to an obsolete generation from becoming visible through its replacement. The cache
   * implementation must support concurrent authorization access. The built-in cache's 100,000-entry capacity is shared
   * by its creator's backing store; it is not a separate quota for every user or branch. Invalid capacity
   * configuration prevents creator creation and therefore repository startup.
   */
  public class PermissionCacheSPI
  {
  }

  /**
   * Authentication Example
   * <p>
   * An authenticator must delegate credential validation to application-owned policy and return {@code null} when the
   * policy rejects the credentials. This example shows the integration seam without embedding credentials or choosing
   * a password-storage scheme.
   * {@link #createAuthenticator(BiPredicate) CreateAuthenticator.java}
   */
  public class AuthenticationExample
  {
  }

  /**
   * Query Handlers
   * <p>
   * The client creates a query with a language identifier, query string, and named parameters. The server provider
   * selects a factory by language; that factory creates an {@link IQueryHandler}, which receives
   * {@link CDOQueryInfo query info} and an {@link IQueryContext} carrying server view/session and branch-point context.
   * Results flow through {@link IQueryContext#addResult(Object)}; the handler stops when that method returns
   * {@code false} or the context is cancelled. Register handler factories in
   * {@link QueryHandlerFactory#PRODUCT_GROUP}; the factory type is the query language, so container configuration can
   * select the handler. Implement {@link IQueryHandler.PotentiallySlow} when a handler can identify slow query forms.
   * OCL is a supplied optional handler integration; this guide does not teach the OCL language itself. A custom
   * language becomes reachable only after its factory is registered in the server container; the client's language
   * string must match the factory type.
   * A useful custom handler often adapts an application-owned, repository-aware index: parse a query string or
   * parameter, find matching persistent IDs, and pass them to the context. The index must honor the query's branch and
   * time semantics; a global current-state index is not correct for historical views. The handler must check
   * cancellation during long scans and stop when {@link IQueryContext#addResult(Object)} returns {@code false}.
   * {@link #createProductLookupHandler(Function) CreateProductLookupHandler.java}
   * <p>
   * {@link #registerProductLookupFactory(IManagedContainer, Function) RegisterProductLookupFactory.java}
   * <p>
   * {@link #queryProduct(CDOView, String) QueryProduct.java}
   */
  public class QueryHandlers
  {
  }

  /**
   * Registers an application-owned product lookup function as the {@code product_lookup} query language. In OSGi,
   * factory classes are commonly contributed declaratively; this programmatic form is useful when the application
   * owns a standalone container and can supply the index dependency directly.
   *
   * @param container the active server container
   * @param findProductID repository-aware lookup by product key
   * @snip
   */
  public void registerProductLookupFactory(IManagedContainer container, Function<String, CDOID> findProductID)
  {
    container.registerFactory(new QueryHandlerFactory("product_lookup")
    {
      @Override
      public IQueryHandler create(String description)
      {
        return createProductLookupHandler(findProductID);
      }
    });
  }

  /**
   * Sends the {@code product_lookup} query and reads at most one persistent object ID.
   *
   * @param view an open client view
   * @param productKey the named parameter read by the server handler
   * @return the matching ID, or {@code null} if the index found no product
   * @snip
   */
  public CDOID queryProduct(CDOView view, String productKey)
  {
    CDOQuery query = view.createQuery("product_lookup", "byKey");
    query.setParameter("key", productKey);
    query.setMaxResults(1);
    return query.getResultValue(CDOID.class);
  }

  /**
   * Extension Boundaries
   * <p>
   * Repository factories, query handler factories, repository-protector elements, DB adapters, and mapping
   * strategies are container/factory extension mechanisms. They serve different responsibilities and should not be
   * confused with application extensions. Use a supported factory SPI only when the application must supply that
   * product; otherwise configure an existing product.
   */
  public class FactoryExtensions
  {
  }

  /**
   * Creates an authenticator that delegates credential validation to the supplied application policy.
   *
   * @snip
   */
  public UserAuthenticator createAuthenticator(BiPredicate<String, char[]> credentials)
  {
    return new UserAuthenticator()
    {
      @Override
      public IRepositoryProtector.UserInfo authenticateUser(String userID, char[] password)
      {
        if (credentials.test(userID, password))
        {
          return new IRepositoryProtector.UserInfo(userID);
        }

        return null;
      }
    };
  }

  /**
   * Creates a query handler that looks up a product ID by product key in an application-owned index.
   * <p>
   * The index is supplied by the application and must be scoped to, or otherwise validate against, the query's
   * repository branch and time point. This example returns at most one persistent object ID.
   *
   * @param findProductID looks up the ID for a product key, or {@code null} when no product matches
   *
   * @snip
   */
  public IQueryHandler createProductLookupHandler(Function<String, CDOID> findProductID)
  {
    return (info, context) -> {
      if (context.isCancelled())
      {
        return;
      }

      String productKey = info.getParameter("key");
      CDOID productID = findProductID.apply(productKey);
      if (productID != null && !context.isCancelled())
      {
        context.addResult(productID);
      }
    };
  }
}
