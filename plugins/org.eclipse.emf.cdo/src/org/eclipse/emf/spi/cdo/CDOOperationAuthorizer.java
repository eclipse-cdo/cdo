/*
 * Copyright (c) 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.spi.cdo;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.session.CDOSession;

import org.eclipse.net4j.util.collection.Entity;
import org.eclipse.net4j.util.container.ContainerElementList.Prioritized;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.factory.IFactory;
import org.eclipse.net4j.util.security.operations.AuthorizableOperation;

import java.io.ObjectOutputStream;
import java.util.function.Supplier;

/**
 * An operation authorizer for CDO sessions.
 * <p>
 * Implementations must register a {@link IFactory factory} with the {@link IManagedContainer container}
 * of the {@link CDOSession session} that is supposed to authorize {@link AuthorizableOperation operations}, e.g.:
 *
 * <pre>
 * &lt;simpleFactory
 *    productGroup="org.eclipse.emf.cdo.operationAuthorizers"
 *    type="test"
 *    productClass="x.y.z.TestOperationAuthorizer"/>
 * </pre>
 *
 * @author Eike Stepper
 * @since 4.28
 */
public interface CDOOperationAuthorizer extends Prioritized
{
  public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.operationAuthorizers";

  public static final Object GRANTED = new Object()
  {
    @Override
    public String toString()
    {
      return "Authorization granted";
    }
  };

  public static final String DENIED = "Authorization denied";

  public static final Object ABSTAINED = null;

  @Override
  public default int getPriority()
  {
    return 0;
  }

  /**
   * Authorizes the given {@link AuthorizableOperation operation} in the context of the given {@link CDOSession session}.
   *
   * @return One of the following values:
   * <ul>
   * <li> The special value {@link #ABSTAINED} (or <code>null</code>) to indicate that this authorizer abstained from voting.
   * <li> The special value {@link #GRANTED} to indicate that authorization is granted.
   * <li> Any non-<code>null</code> String value to indicate that authorization is vetoed.
   * The String value represents the reason for the veto.
   * <li> An {@link AuthorizableOperation authorizable operation} to indicate that the repository
   * shall be asked to authorize the returned operation on behalf of the operation that is passed to this method.
   * The returned operation can be identical to the passed operation, have different
   * {@link AuthorizableOperation#getParameters() parameters} than the passed operation, or an entirely different operation.
   * <b>Care should be taken</b> to ask for repository authorization of operations with complex parameter types,
   * as Object {@link ObjectOutputStream serialization} is used to send the parameters to the repository.
   * For example, a {@link CDOID} should be preferred over an entire {@link CDOObject}.
   * </ul>
   *
   * @see AuthorizableOperation#stripParameters()
   */
  public Object authorizeOperation(CDOSession session, Supplier<Entity> userInfoSupplier, AuthorizableOperation operation);
}
