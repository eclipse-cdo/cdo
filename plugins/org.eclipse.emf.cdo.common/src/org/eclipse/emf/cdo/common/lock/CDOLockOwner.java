/*
 * Copyright (c) 2011, 2012, 2015, 2021, 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.common.lock;

import org.eclipse.net4j.util.ref.Interner;

/**
 * A client-side representation of a view owning locks.
 * <p>
 * Implementation note: All lock owners are {@link Interner interned}. That means they can safely be compared via "==".
 *
 * @author Caspar De Groot
 * @since 4.1
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOLockOwner
{
  /**
   * @return the ID identifying the session that owns the view
   */
  public int getSessionID();

  /**
   * @return the ID identifying the view within the session
   */
  public int getViewID();

  public String getDurableLockingID();

  /**
   * Returns <code>true</code> if this view is <b>purely</b> durable,
   * i.e., currently not open and active on the client-side, <code>false</code> otherwise.
   */
  public boolean isDurableView();
}
