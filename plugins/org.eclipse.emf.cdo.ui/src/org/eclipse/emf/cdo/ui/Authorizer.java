/*
 * Copyright (c) 2021, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ui;

import org.eclipse.emf.cdo.session.CDOSession;

import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.security.operations.AuthorizableOperation;
import org.eclipse.net4j.util.ui.UIUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Asynchronously authorizes {@link AuthorizableOperation authorizable operations} and caches the authorization results.
 *
 * @author Eike Stepper
 * @since 4.11
 * @see AuthorizerContext
 * @see AbstractAuthorizingDialog
 */
public class Authorizer<E>
{
  public static final String AUTHORIZATION_PENDING = "Authorization pending...";

  public static final String AUTHORIZED = "Authorized";

  private final Authorizer.AuthorizerContext<E> context;

  private final CDOSession session;

  private final boolean authorizing;

  private volatile boolean authorizationFinished;

  private volatile boolean authorizationDenied;

  private final Map<E, String> authorizations = Collections.synchronizedMap(new HashMap<>());

  public Authorizer(Authorizer.AuthorizerContext<E> context, CDOSession session)
  {
    this.context = context;
    this.session = session;
    authorizing = session.getRepositoryInfo().isAuthorizingOperations();
  }

  public final Authorizer.AuthorizerContext<E> getContext()
  {
    return context;
  }

  public final CDOSession getSession()
  {
    return session;
  }

  public final boolean isAuthorizing()
  {
    return authorizing;
  }

  public final boolean isAuthorizationFinished()
  {
    return authorizationFinished;
  }

  public final boolean isAuthorizationDenied()
  {
    return authorizationDenied;
  }

  public final String getAuthorization(E element)
  {
    return authorizations.get(element);
  }

  protected void authorize()
  {
    if (authorizing)
    {
      ConcurrencyUtil.execute(session, () -> {
        List<Map.Entry<E, AuthorizableOperation>> elementOperations = new ArrayList<>();
        context.collectElementOperations(elementOperations);

        AuthorizableOperation[] operations = elementOperations.stream().map(Map.Entry::getValue).toArray(AuthorizableOperation[]::new);
        String[] results = session.authorizeOperations(operations);

        for (int i = 0; i < operations.length; i++)
        {
          E element = elementOperations.get(i).getKey();
          if (results[i] == null)
          {
            authorizations.put(element, AUTHORIZED);
          }
          else
          {
            authorizations.put(element, results[i]);
            authorizationDenied = true;
          }
        }

        authorizationFinished = true;

        UIUtil.asyncExec(() -> {
          context.updateUIAfterAuthorization();
          context.validate();
        });
      });
    }
  }

  protected void validate() throws Exception
  {
    if (authorizing)
    {
      if (!authorizationFinished)
      {
        throw new Exception(AUTHORIZATION_PENDING);
      }

      if (authorizationDenied)
      {
        throw new Exception(context.getAuthorizationDeniedMessage());
      }
    }
  }

  /**
   * An {@link Authorizer authorizer's} context.
   *
   * @author Eike Stepper
   * @see AbstractAuthorizingDialog
   */
  public interface AuthorizerContext<E>
  {
    public void collectElementOperations(List<Map.Entry<E, AuthorizableOperation>> operations);

    public void updateUIAfterAuthorization();

    public String getAuthorizationDeniedMessage();

    public boolean validate();
  }
}
