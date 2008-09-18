/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/201266
 *    Simon McDuff - http://bugs.eclipse.org/201997
 *    Simon McDuff - http://bugs.eclipse.org/233490
 *    Simon McDuff - http://bugs.eclipse.org/213402
 *    Victor Roldan Betancort - http://bugs.eclipse.org/208689
 **************************************************************************/
package org.eclipse.emf.cdo;

import org.eclipse.emf.cdo.common.CDOProtocolView;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.query.CDOQuery;

import org.eclipse.net4j.util.event.INotifier;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;

import java.util.List;

/**
 * @author Eike Stepper
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOView extends CDOProtocolView, INotifier
{
  public static final long UNSPECIFIED_DATE = CDORevision.UNSPECIFIED_DATE;

  public static final int NO_PRELOAD = 1;

  public CDOSession getSession();

  /**
   * @since 2.0
   */
  public CDOViewSet getViewSet();

  public ResourceSet getResourceSet();

  public boolean isDirty();

  public boolean hasConflict();

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
   */
  public void setUniqueResourceContents(boolean uniqueResourceContents);

  public boolean isInvalidationNotificationsEnabled();

  public void setInvalidationNotificationsEnabled(boolean invalidationNotificationsEnabled);

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
  public void setChangeSubscriptionPolicy(CDOChangeSubscriptionPolicy changeSubscriptionPolicy);

  public int getLoadRevisionCollectionChunkSize();

  public void setLoadRevisionCollectionChunkSize(int loadRevisionCollectionChunkSize);

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
   * Returns the object for the given CDOID.
   * 
   * @param loadOnDemand
   *          whether to create and load the object, if it doesn't already exist.
   * @return the object resolved by the CDOID, or <code>null</code> if there isn't one.
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

  /**
   * Returns a list of those resources whose path starts with the value of the pathPrefix parameter.
   * 
   * @param pathPrefix
   *          the prefix of the resource's path
   * @since 2.0
   */
  public List<CDOResource> queryResources(String pathPrefix);

  public void close();
}
