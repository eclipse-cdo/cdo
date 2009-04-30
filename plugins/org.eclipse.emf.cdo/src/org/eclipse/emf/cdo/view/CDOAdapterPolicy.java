/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.view;

import org.eclipse.emf.cdo.CDOAdapter;
import org.eclipse.emf.cdo.CDOInvalidationNotification;
import org.eclipse.emf.cdo.messages.Messages;
import org.eclipse.emf.cdo.session.CDOSessionInvalidationEvent;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.ecore.EObject;

/**
 * Specifies an adapter policy.
 * <p>
 * To activate a policy, you must do the following: <br>
 * <code>view.setChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);</code>
 * <p>
 * To register an object, you must add an adapter to the object in which you are interested:<br>
 * <code>eObject.eAdapters().add(myAdapter);</code>
 * <p>
 * By activating this feature, each object having at least one adapter that matches the current policy will be
 * registered with the server and will be notified for each change occurring in the scope of any other transaction.
 * <p>
 * {@link CDOAdapterPolicy#NONE} - Disabled. <br>
 * {@link CDOAdapterPolicy#ALL} - Enabled for all adapters used.<br>
 * {@link CDOAdapterPolicy#CDO} - Enabled only for adapters that implement {@link CDOAdapter}. <br>
 * Any other class that implement {@link CDOAdapterPolicy} will enable for whatever rules defined in that class. <br>
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
public interface CDOAdapterPolicy
{
  /**
   * A default adapter policy that never triggers any special behaviour.
   */
  public static final CDOAdapterPolicy NONE = new CDOAdapterPolicy()
  {
    /**
     * Always returns <code>false</code>.
     */
    public boolean isValid(EObject eObject, Adapter adapter)
    {
      return false;
    }

    @Override
    public String toString()
    {
      return Messages.getString("CDOAdapterPolicy.1"); //$NON-NLS-1$
    }
  };

  /**
   * A default adapter policy that only triggers special behaviour if the adapter under test implements
   * {@link CDOAdapter}.
   */
  public static final CDOAdapterPolicy CDO = new CDOAdapterPolicy()
  {
    /**
     * Returns <code>true</code> if the given adapter implements {@link CDOAdapter}.
     */
    public boolean isValid(EObject eObject, Adapter adapter)
    {
      return adapter instanceof CDOAdapter;
    }

    @Override
    public String toString()
    {
      return Messages.getString("CDOAdapterPolicy.0"); //$NON-NLS-1$
    }
  };

  /**
   * A default adapter policy that always triggers special behaviour.
   */
  public static final CDOAdapterPolicy ALL = new CDOAdapterPolicy()
  {
    /**
     * Always returns <code>true</code>.
     */
    public boolean isValid(EObject eObject, Adapter adapter)
    {
      return true;
    }

    @Override
    public String toString()
    {
      return Messages.getString("CDOAdapterPolicy.2"); //$NON-NLS-1$
    }
  };

  /**
   * Returns <code>true</code> if the given adapter on the given object should trigger a certain operation or behaviour,
   * <code>false</code> otherwise.
   * 
   * @see CDOView.Options#addChangeSubscriptionPolicy(CDOAdapterPolicy)
   * @see CDOView.Options#setStrongReferencePolicy(CDOAdapterPolicy)
   */
  public boolean isValid(EObject eObject, Adapter adapter);
}
