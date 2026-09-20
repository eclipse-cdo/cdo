/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public License 2.0.
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.doc.programmers.server;

import org.eclipse.emf.cdo.doc.operators.Doc03_ManagingSecurity;
import org.eclipse.emf.cdo.common.util.CDOQueryInfo;
import org.eclipse.emf.cdo.server.IQueryContext;
import org.eclipse.emf.cdo.server.IQueryHandler;
import org.eclipse.emf.cdo.server.IPermissionManager;
import org.eclipse.emf.cdo.server.IRepositoryProtector;
import org.eclipse.emf.cdo.server.IRepositoryProtector.UserAuthenticator;
import org.eclipse.emf.cdo.spi.server.QueryHandlerFactory;

import java.util.function.BiPredicate;

/**
 * Security, Queries, and Specialized Extensions
 * <p>
 * Security and query handling are specialized extension domains: use their deliberately supported contracts instead
 * of general read/write handlers when the problem is authentication, authorization, or a query language. Deployment
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
   * strategy, revision authorizers, and commit handlers. The authenticator establishes repository user identity;
   * {@link IRepositoryProtector.AuthorizationStrategy} combines permissions; a
   * {@link IRepositoryProtector.RevisionAuthorizer} decides revision permissions; and a
   * {@link IRepositoryProtector.CommitHandler} runs before security checking and after a successful protected commit.
   * The protector's product groups make those elements container-configurable.
   * <p>
   * {@link IPermissionManager} is the legacy permission-management API used by the security model. The optional
   * {@code org.eclipse.emf.cdo.server.security} module supplies its own {@code ISecurityManager}; use its public API
   * when that module is intentionally part of an application, without coupling to its internal realm implementation.
   */
  public class SecurityModel
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
   * An {@link IQueryHandler} executes the {@link CDOQueryInfo query info} selected by a repository's query-handler
   * provider. It submits results through {@link IQueryContext#addResult(Object)} and stops when that method returns
   * {@code false} or the context is cancelled. Register handler factories in
   * {@link QueryHandlerFactory#PRODUCT_GROUP}; the factory type is the query language, so container configuration can
   * select the handler. Implement {@link IQueryHandler.PotentiallySlow} when a handler can identify slow query forms.
   * OCL is a supplied optional handler integration; this guide does not teach the OCL language itself.
   * {@link #createViewIdentifierHandler() CreateViewIdentifierHandler.java}
   */
  public class QueryHandlers
  {
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
   * Creates a query handler that returns the executing server view identifier.
   *
   * @snip
   */
  public IQueryHandler createViewIdentifierHandler()
  {
    return (info, context) -> {
      if (!context.isCancelled())
      {
        context.addResult(context.getView().getViewID());
      }
    };
  }
}
