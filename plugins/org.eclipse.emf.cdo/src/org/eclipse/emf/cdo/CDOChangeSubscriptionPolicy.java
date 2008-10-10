/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 **************************************************************************/
package org.eclipse.emf.cdo;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.ecore.EObject;

/**
 * Specifies a change subscription policy.
 * <p>
 * To activate a policy, you must do the following: <br>
 * <code>view.setChangeSubscriptionPolicy(CDOChangeSubscriptionPolicy.ALL);</code>
 * <p>
 * To register an object, you must add an adapter to the object in which you are interested:<br>
 * <code>eObject.eAdapters().add(myAdapter);</code>
 * <p>
 * By activating this feature, each object having at least one adapter that matches the current policy will be
 * registered with the server and will be notified for each change occurring in the scope of any other transaction.
 * <p>
 * {@link CDOChangeSubscriptionPolicy#NONE} - Disabled. <br>
 * {@link CDOChangeSubscriptionPolicy#ALL} - Enabled for all adapters used.<br>
 * {@link CDOChangeSubscriptionPolicy#ONLY_CDO_ADAPTER} - Enabled only for adapters that implement {@link CDOAdapter}. <br>
 * Any other class that implement {@link CDOChangeSubscriptionPolicy} will enable for whatever rules defined in that
 * class. <br>
 * <p>
 * If <code>myAdapter</code> in the above example matches the current policy, <code>eObject</code> will be registered
 * with the server and you will receive all changes from other transaction.
 * <p>
 * When the policy is changed all objects in the cache will automatically be recalculated.
 * <p>
 * You can subscribe to temporary objects. Even if you cannot receive notifications from other {@link CDOTransaction}
 * for these because they are only local to you, at commit time these objects will be registered automatically.
 * <p>
 * <b>Note:</b> It can be used with <code>CDOSession.setPassiveUpdate(false)</code>. In this case, it will receive
 * changes without having the objects changed.
 * 
 * @author Simon McDuff
 * @see CDOSessionInvalidationEvent
 * @see CDOInvalidationNotification
 * @since 2.0
 */
public interface CDOChangeSubscriptionPolicy
{
  public static final CDOChangeSubscriptionPolicy NONE = new CDOChangeSubscriptionPolicy()
  {
    public boolean shouldSubscribe(EObject eObject, Adapter adapter)
    {
      return false;
    }
  };

  public static final CDOChangeSubscriptionPolicy ONLY_CDO_ADAPTER = new CDOChangeSubscriptionPolicy()
  {
    public boolean shouldSubscribe(EObject eObject, Adapter adapter)
    {
      return adapter instanceof CDOAdapter;
    }
  };

  public static final CDOChangeSubscriptionPolicy ALL = new CDOChangeSubscriptionPolicy()
  {
    public boolean shouldSubscribe(EObject eObject, Adapter adapter)
    {
      return true;
    }
  };

  public boolean shouldSubscribe(EObject eObject, Adapter adapter);
}
