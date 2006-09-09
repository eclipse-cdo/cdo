/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server;


import org.eclipse.emf.cdo.core.CDOResProtocol;
import org.eclipse.emf.cdo.core.OID;
import org.eclipse.emf.cdo.core.RID;
import org.eclipse.emf.cdo.core.protocol.ResourceChangeInfo;

import org.springframework.transaction.support.TransactionTemplate;

import java.util.Collection;
import java.util.List;


public interface ServerCDOResProtocol extends CDOResProtocol
{
  public Mapper getMapper();

  public TransactionTemplate getTransactionTemplate();

  public void fireResourcesChangedNotification(List<ResourceChangeInfo> resourceChanges);

  public void fireInvalidationNotification(Collection<Long> modifiedOIDs);

  public void fireRemovalNotification(Collection<Integer> rids);

  /**
   * Adds a {@link Listener} to the list of listeners to be notified about 
   * removed resources and invalidated obejcts in the scope of this 
   * {@link ServerCDOResProtocol}.<p>
   *
   * @param listener The {@link Listener} to be added.<p>
   */
  public void addListener(Listener listener);

  /**
   * Removes a {@link Listener} from the list of listeners to be notified about
   * removed resources and invalidated obejcts in the scope of this 
   * {@link ServerCDOResProtocol}.<p>
   *
   * @param listener The {@link Listener} to be removed.<p>
   */
  public void removeListener(Listener listener);


  /**
   * Can be registered with a {@link ServerCDOResProtocol} to be subsequently 
   * notified about removed resources and invalidated objects.<p>
   *
   * @author Eike Stepper
   */
  public interface Listener
  {
    /**
     * Called by the {@link ServerCDOResProtocol} this {@link Listener} is 
     * registered with to notify about removed resources.<p>
     * 
     * @param protocol The {@link ServerCDOResProtocol} this {@link Listener} is 
     *        registered with.<p>
     * @param rids A {@link Collection} of {@link RID}s which have been removed.<p>
     */
    public void notifyRemoval(ServerCDOResProtocol protocol, Collection<Integer> rids);

    /**
     * Called by the {@link ServerCDOResProtocol} this {@link Listener} is 
     * registered with to notify about invalidated objects.<p>
     * 
     * @param protocol The {@link ServerCDOResProtocol} this {@link Listener} is 
     *        registered with.<p>
     * @param modifiedOIDs A {@link Collection} of {@link OID}s which have been 
     *        invalidated.<p>
     */
    public void notifyInvalidation(ServerCDOResProtocol protocol, Collection<Long> modifiedOIDs);
  }
}
