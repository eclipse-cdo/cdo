/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - maintenance
 *    Victor Roldan Betancort - http://bugs.eclipse.org/208689
 **************************************************************************/
package org.eclipse.emf.cdo;

import org.eclipse.emf.cdo.common.CDOProtocolView;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.query.CDOQuery;
import org.eclipse.emf.cdo.util.ReadOnlyException;

import org.eclipse.net4j.util.collection.CloseableIterator;
import org.eclipse.net4j.util.event.INotifier;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIHandler;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A read-only view to the <em>current</em> (i.e. latest) state of the object graph in the repository of the underlying
 * {@link CDOSession session}.
 * <p>
 * Objects that are accessed through this view are unchangeable for the client. Each attempt to call a mutator on one of
 * these objects or one of their reference collections will result in a {@link ReadOnlyException} being thrown
 * immediately.
 * <p>
 * A view is opened through API of the underlying session like this:
 * 
 * <pre>
 *   CDOSession session = ...
 *   CDOView view = session.openView();
 *   ...
 * </pre>
 * 
 * @author Eike Stepper
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOView extends CDOProtocolView, INotifier
{
  /**
   * @since 2.0
   */
  public static final int NO_REVISION_PREFETCHING = 1;

  public CDOSession getSession();

  /**
   * @since 2.0
   */
  public CDOViewSet getViewSet();

  public ResourceSet getResourceSet();

  /**
   * @since 2.0
   */
  public URIHandler getURIHandler();

  /**
   * Returns a reentrant lock that can be used to prevent the framework from writing to any object in this view (caused,
   * for example, by passive updates).
   * <p>
   * Acquiring this lock provides a means to safely iterate over multiple model elements without being affected by
   * unanticipated remote updates, like in the following example:
   * 
   * <pre>
   *    CDOResource resource = view.getResource(&quot;/orders/order-4711&quot;);
   *    PurchaseOrder order = (PurchaseOrder)resource.getContents().get(0);
   * 
   *    ReentrantLock lock = view.getLock();
   *    if (!lock.tryLock(5L, TimeUnit.SECONDS))
   *    {
   *      throw new TimeoutException();
   *    }
   * 
   *    try
   *    {
   *      float sum = 0;
   *      for (OrderDetail detail : order.getOrderDetails())
   *      {
   *        sum += detail.getPrice();
   *      }
   * 
   *      System.out.println(&quot;Sum: &quot; + sum);
   *    }
   *    finally
   *    {
   *      lock.unlock();
   *    }
   *  }
   * </pre>
   * 
   * Note that this method really just returns the lock instance but does <b>not</b> acquire the lock! The above example
   * acquires the lock with a timeout that expires after five seconds.
   * 
   * @since 2.0
   */
  public ReentrantLock getLock();

  public boolean isDirty();

  public boolean hasConflict();

  /**
   * @see #setUniqueResourceContents(boolean)
   * @deprecated This performance tweak is not necessary with EMF 2.5 anymore and is likely to be removed when CDO
   *             reaches 2.0. In the meantime it can be safely used.
   */
  @Deprecated
  public boolean hasUniqueResourceContents();

  /**
   * Specifies whether the contents list of resources will be unique or not.
   * <p>
   * This property is transient in that it does not stick with resources outside of the scope of this view. Especially
   * it will not be persisted with resources in the repository. Each new view will start with <code>true</code> as a
   * default value. Changing to <code>false</code> will subsequently apply to all resources being loaded or created.
   * <p>
   * Notice that the resource contents is a containment list and as such <b>must be</b> unique. Setting this property to
   * <code>false</code> is only recommended for performance optimization when uniqueness is granted by other means.
   * Violating the uniqueness constraint will result in unpredictable behaviour and possible corruption of the
   * repository!
   * 
   * @deprecated This performance tweak is not necessary with EMF 2.5 anymore and is likely to be removed when CDO
   *             reaches 2.0. In the meantime it can be safely used.
   */
  @Deprecated
  public void setUniqueResourceContents(boolean uniqueResourceContents);

  /**
   * @since 2.0
   */
  public boolean isInvalidationNotificationEnabled();

  /**
   * @since 2.0
   */
  public void setInvalidationNotificationEnabled(boolean enabled);

  /**
   * @since 2.0
   */
  public CDOChangeSubscriptionPolicy getChangeSubscriptionPolicy();

  /**
   * Specifies the change subscription policy. By default, the value is set to {@link CDOChangeSubscriptionPolicy#NONE}.
   * <p>
   * To activate the policy, you must do the following: <br>
   * <code>transaction.setChangeSubscriptionPolicy(CDOChangeSubscriptionPolicy.ALL);</code>
   * <p>
   * To register an object, you must add an adapter to the object in which you are interested:<br>
   * <code>eObject.eAdapters().add(myAdapter);</code>
   * <p>
   * By activating this feature, each object having at least one adapter that matches the current policy will be
   * registered with the server and will be notified for each change occurring in the scope of any other transaction.
   * <p>
   * {@link CDOChangeSubscriptionPolicy#NONE} - Disabled. <br>
   * {@link CDOChangeSubscriptionPolicy#ALL} - Enabled for all adapters used.<br>
   * {@link CDOChangeSubscriptionPolicy#ONLY_CDO_ADAPTER} - Enabled only for adapters that implement {@link CDOAdapter}.
   * <br>
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
   * @since 2.0
   */
  public void setChangeSubscriptionPolicy(CDOChangeSubscriptionPolicy policy);

  /**
   * @since 2.0
   */
  public CDORevisionPrefetchingPolicy getRevisionPrefetchingPolicy();

  /**
   * @since 2.0
   */
  public void setRevisionPrefetchingPolicy(CDORevisionPrefetchingPolicy prefetchingPolicy);

  public boolean hasResource(String path);

  /**
   * @see ResourceSet#getResource(URI, boolean)
   * @since 2.0
   */
  public CDOResource getResource(String path, boolean loadInDemand);

  /**
   * @see ResourceSet#getResource(URI, boolean)
   */
  public CDOResource getResource(String path);

  /**
   * @since 2.0
   */
  public CDOResource getRootResource();

  /**
   * Returns a list of the resources in the given folder with a name equal to or starting with the value of the name
   * parameter.
   * 
   * @param folder
   *          The folder to search in, or <code>null</code> for top level resource nodes.
   * @param name
   *          the name or prefix of the resource nodes to return.
   * @param exactMatch
   *          <code>true</code> if the complete name of the resource must match, <code>false</code> if only a common
   *          prefix of the name must match.
   * @since 2.0
   */
  public List<CDOResourceNode> queryResources(CDOResourceFolder folder, String name, boolean exactMatch);

  /**
   * Returns an iterator over the resources in the given folder with a name equal to or starting with the value of the
   * name parameter. The underlying query will be executed asynchronously.
   * 
   * @param folder
   *          The folder to search in, or <code>null</code> for top level resource nodes.
   * @param name
   *          the name or prefix of the resource nodes to return.
   * @param exactMatch
   *          <code>true</code> if the complete name of the resource must match, <code>false</code> if only a common
   *          prefix of the name must match.
   * @since 2.0
   */
  public CloseableIterator<CDOResourceNode> queryResourcesAsync(CDOResourceFolder folder, String name,
      boolean exactMatch);

  /**
   * Returns the object for the given CDOID.
   * 
   * @param loadOnDemand
   *          whether to create and load the object, if it doesn't already exist.
   * @return the object resolved by the CDOID if the id is not <code>null</code>, or <code>null</code> if there isn't
   *         one and loadOnDemand is <code>false</code>.
   */
  public CDOObject getObject(CDOID id, boolean loadOnDemand);

  /**
   * Returns the object for the given CDOID.
   * <p>
   * Same as <code>getObject(id, true)</code>.
   */
  public CDOObject getObject(CDOID id);

  /**
   * Takes an object from a (possibly) different view and <em>contextifies</em> it for the usage with this view.
   * <ul>
   * <li>If the given object is contained in this view it is returned unmodified.
   * <li>If the given object can not be cast to {@link CDOObject} it is returned unmodified.
   * <li>If the view of the given object is contained in a different session an <code>IllegalArgumentException</code> is
   * thrown.
   * <li>If <code>null</code> is passed <code>null
   * </code> is returned.
   * </ul>
   * 
   * @since 2.0
   */
  public <T extends EObject> T getObject(T objectFromDifferentView);

  public boolean isObjectRegistered(CDOID id);

  public int reload(CDOObject... objects);

  /**
   * @since 2.0
   */
  public CDOQuery createQuery(String language, String queryString);
}
