/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.common.lock;

/**
 * A client-side representation of a view owning locks.
 * <p>
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

  /**
   * A constant to represent on the client-side that a lock's owner cannot be represented as a viewID-sessionID pair.
   */
  public static final CDOLockOwner UNKNOWN = new CDOLockOwner()
  {
    public int getViewID()
    {
      return 0;
    }

    public int getSessionID()
    {
      return 0;
    }

    @Override
    public String toString()
    {
      return CDOLockOwner.class.getSimpleName() + ".UNKNOWN";
    }
  };
}
