/*
 * Copyright (c) 2009-2016, 2018-2020, 2022, 2024, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - maintenance
 *    Victor Roldan Betancort - maintenance
 */
package org.eclipse.emf.cdo.view;

import org.eclipse.emf.cdo.CDOAdapter;
import org.eclipse.emf.cdo.CDOInvalidationNotification;
import org.eclipse.emf.cdo.CDONotification;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOObjectHistory;
import org.eclipse.emf.cdo.CDOObjectReference;
import org.eclipse.emf.cdo.common.CDOCommonView;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.commit.CDOCommitHistory;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.util.CDOException;
import org.eclipse.emf.cdo.common.util.CDOResourceNodeNotFoundException;
import org.eclipse.emf.cdo.eresource.CDOBinaryResource;
import org.eclipse.emf.cdo.eresource.CDOFileResource;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.eresource.CDOTextResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUpdatable;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.ReadOnlyException;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.net4j.util.collection.CloseableIterator;
import org.eclipse.net4j.util.concurrent.CriticalSection;
import org.eclipse.net4j.util.concurrent.DelegableReentrantLock;
import org.eclipse.net4j.util.concurrent.DelegableReentrantLock.DelegateDetector;
import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.options.IOptionsEvent;
import org.eclipse.net4j.util.ref.ReferenceType;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIHandler;

import org.eclipse.core.runtime.IProgressMonitor;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A read-only view to the state of the object graph in the repository of the underlying {@link CDOSession session} at a
 * specific {@link #getTimeStamp() time} at a specific {@link #getBranch() branch}.
 * <p>
 * If the {@link #getTimeStamp() time} of a view is {@link CDOBranchPoint#UNSPECIFIED_DATE unspecified} the objects
 * provided and managed by that view always show the latest state the repository graph.
 * <p>
 * Objects that are accessed through this view are immutable for the client. Each attempt to call a mutator on one of
 * these objects or one of their feature lists will result in a {@link ReadOnlyException} being thrown immediately.
 * Mutable objects can be provided by a {@link CDOTransaction transaction}.
 * <p>
 * A view is opened through API of the underlying session like this:
 *
 * <pre>
 *   CDOSession session = ...
 *   CDOView view = session.openView();
 *   ...
 * </pre>
 * <p>
 * Views and their model objects are thread-safe. They can be used concurrently from different threads without
 * additional synchronization. Note that this guarantee only applies to single accesses to the view or its objects. If
 * multiple accesses need to be guarded against invalidation or other internal operations of the view, the
 * {@link #sync() critical section} of the view must be used.
 *
 * @author Eike Stepper
 * @since 2.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOView extends CDOCommonView, CDOUpdatable, CDOCommitHistory.Provider<CDOObject, CDOObjectHistory>, IContainer<CDOResourceNode>
{
  /**
   * @since 4.5
   */
  public static String PROP_TIME_MACHINE_DISABLED = "timeMachineDisabled";

  /**
   * @since 4.19
   */
  public static String PROP_LIFECYCLE_EXCEPTION_HANDLER = "lifecycleExceptionHandler";

  /**
   * Returns the {@link CDOSession session} this view was opened by.
   *
   * @return The session this view was opened by, or <code>null</code> if this view is closed.
   * @see #close()
   * @see #isClosed()
   * @see CDOSession#openView()
   * @see CDOSession#openTransaction()
   */
  @Override
  public CDOSession getSession();

  /**
   * Returns the {@link CDOViewProvider provider} that has opened this view.
   *
   * @since 4.4
   */
  public CDOViewProvider getProvider();

  /**
   * @since 4.24
   */
  public URI getURI();

  /**
   * Returns the {@link CDOViewSet view set} this view is associated with.
   *
   * @return The view set this view is associated with, never <code>null</code>.
   * @see CDOViewSet#getViews()
   */
  public CDOViewSet getViewSet();

  /**
   * Returns a {@link CriticalSection critical section} that can be used to execute a block of code that
   * needs to be synchronized with the internal operations of this view and its {@link CDOObject objects}.
   * <p>
   * Here is an example:
   * <pre>
   * CDOView view = ...;
   * CriticalSection sync = view.sync();
   *
   * MyResult result = sync.call(() -&gt; {
   *   // Access the view and its objects safely here.
   *   CDOObject object1 = view.getObject(id1);
   *   CDOObject object2 = view.getObject(id2);
   *   CDOObject object3 = view.getObject(id3);
   *   ...
   *   return new MyResult(...);
   * });
   * </pre>
   * The critical section ensures that no invalidation or other internal operation of the view can occur while
   * the block of code is executed. This is particularly important if the block of code accesses the view or
   * {@link CDOObject objects} of the view <b>multiple times</b>, because these objects can be
   * {@link #isInvalidating() invalidated} by network threads at any time in between these accesses otherwise.
   * This can lead to wrong computation results in the block of code being executed.
   * <p>
   * Note that single accesses to the view or its objects are always safe, even without using the critical section.
   * <p>
   * There are some other useful methods in CriticalSection that can be used to execute a block of code,
   * such as {@link CriticalSection#run(Runnable) run(Runnable)} or {@link CriticalSection#supply(Supplier) supply(Supplier)}.
   * <p>
   * By default the critical section uses the monitor lock of this view to synchronize. If you need a different locking
   * strategy you can override this by calling {@link CDOUtil#setNextViewLock(Lock)} before opening the view.
   * <p>
   * In particular you can use a {@link DelegableReentrantLock}, which allows to delegate the lock ownership to a
   * different thread. This is useful in scenarios where you need to hold the lock while waiting for an
   * asynchronous operation to complete in a different thread.
   * <p>
   * A typical example is the Display.syncExec() method in SWT/JFace UI applications. With the default locking
   * strategy this can lead to deadlocks:
   * <ol>
   * <li>Thread A (not the UI thread) holds the view lock and calls <code>Display.syncExec()</code> to execute some code in the UI
   *    thread.
   * <li>The Runnable passed to syncExec() is scheduled for execution in the UI thread. Thread A waits for the Runnable to
   *    complete.
   * <li>The UI thread executes the Runnable, which tries to access the view or an object of the view. This requires
   *    the view lock, which is already held by thread A.
   * <li>Deadlock: Thread A waits for the Runnable to complete and the UI thread waits for the view lock to be released.
   * </ol>
   * <p>
   * Note that, in this scenario, Thread A is holding the view lock while waiting for the Runnable to complete. This is kind
   * of an anti-pattern, because it blocks other threads from accessing the view for an indeterminate amount of time. In
   * addition, it is not necessary to hold the view lock while waiting for the Runnable to complete, because Thread A
   * can not access the view in that time.
   * <p>
   * A {@link DelegableReentrantLock} can be used to avoid the deadlock. It uses so called <em>lock delegation</em> to
   * temporarily transfer the ownership of the lock to a different thread. In the scenario described above,
   * Thread A can delegate the lock ownership to the UI thread while waiting for the Runnable to complete.
   * When the Runnable completes, the lock ownership is transferred back to Thread A. This way, the UI thread can
   * access the view while executing the Runnable and no deadlock occurs.
   * <p><code>DelegableReentrantLock</code>
   * uses {@link DelegateDetector}s to determine whether the current thread is allowed to delegate the lock ownership
   * to a different thread. A <code>DelegateDetector</code> can be registered with the lock by calling
   * {@link DelegableReentrantLock#addDelegateDetector(DelegateDetector)}. The <code>org.eclipse.net4j.util.ui</code> plugin provides
   * a <code>DisplayDelegateDetector</code> for the SWT/JFace UI thread that detects calls to
   * <code>Display.syncExec()</code>.
   *
   * @see CDOUtil#sync(CDOView)
   * @see CDOUtil#sync(Notifier)
   * @since 4.29
   */
  public CriticalSection sync();

  /**
   * @since 4.5
   * @deprecated As of 4.29 use {@link #sync()}.
   */
  @Deprecated
  public Lock getViewLock();

  /**
   * @since 4.5
   * @deprecated As of 4.29 use {@link #sync()}.
   */
  @Deprecated
  public void syncExec(Runnable runnable);

  /**
   * @since 4.5
   * @deprecated As of 4.29 use {@link #sync()}.
   */
  @Deprecated
  public <V> V syncExec(Callable<V> callable) throws Exception;

  /**
   * Returns the {@link ResourceSet resource set} this view is associated with.
   * <p>
   * Same as calling <tt>getViewSet().getResourceSet()</tt>.
   *
   * @see CDOViewSet#getResourceSet()
   */
  public ResourceSet getResourceSet();

  public URIHandler getURIHandler();

  /**
   * @since 4.5
   */
  public CDOUnitManager getUnitManager();

  /**
   * Sets the {@link CDOBranch branch} and the point in (repository) time this view should refer to. {@link CDOObject
   * Objects} provided by this view will be {@link CDORevision#isValid(long) valid} at this time. The special value
   * {@link CDOCommonView#UNSPECIFIED_DATE UNSPECIFIED_DATE} denotes a "floating view" that always shows the latest
   * state of the repository.
   *
   * @return <code>true</code> if the branch point was changed, <code>false</code> otherwise.
   * @since 3.0
   */
  public boolean setBranchPoint(CDOBranch branch, long timeStamp);

  /**
   * Same as {@link CDOView#setBranchPoint(CDOBranch, long)} with a {@link IProgressMonitor}.
   *
   * @since 4.4
   */
  public boolean setBranchPoint(CDOBranch branch, long timeStamp, IProgressMonitor monitor);

  /**
   * Same as calling {@link #setBranchPoint(CDOBranch, long) setBranchPoint(branchPoint.getBranch(),
   * branchPoint.getTimeStamp())}.
   *
   * @since 3.0
   */
  public boolean setBranchPoint(CDOBranchPoint branchPoint);

  /**
   * Same as calling {@link #setBranchPoint(CDOBranchPoint)} with a {@link IProgressMonitor} .
   *
   * @since 4.4
   */
  public boolean setBranchPoint(CDOBranchPoint branchPoint, IProgressMonitor monitor);

  /**
   * Same as calling {@link #setBranchPoint(CDOBranch, long) setBranchPoint(branch, getTimeStamp())}.
   *
   * @since 3.0
   */
  public boolean setBranch(CDOBranch branch);

  /**
   * Same as {@link CDOView#setBranch(CDOBranch)} with {@link IProgressMonitor}.
   *
   * @since 4.4
   */
  public boolean setBranch(CDOBranch branch, IProgressMonitor monitor);

  /**
   * Same as calling {@link #setBranchPoint(CDOBranch, long) setBranchPoint(getBranch(), timeStamp)}.
   *
   * @since 3.0
   */
  public boolean setTimeStamp(long timeStamp);

  /**
   * Same as {@link CDOView#setTimeStamp(long)} with {@link IProgressMonitor}.
   *
   * @since 4.4
   */
  public boolean setTimeStamp(long timeStamp, IProgressMonitor monitor);

  /**
   * @since 4.8
   */
  public boolean isInvalidating();

  /**
   * @since 4.0
   * @deprecated As of 4.7 use {@link #isInvalidating()}.
   */
  @Deprecated
  public boolean isInvalidationRunnerActive();

  /**
   * @see CDOUtil#setLegacyModeDefault(boolean)
   * @since 3.0
   * @deprecated As of 4.2 the legacy mode is always enabled.
   */
  @Deprecated
  public boolean isLegacyModeEnabled();

  /**
   * Returns always <code>false</code>.
   * <p>
   * This method has a special implementation in {@link CDOTransaction} as well.
   *
   * @see CDOTransaction#isDirty()
   */
  public boolean isDirty();

  /**
   * Returns always <code>false</code>.
   * <p>
   * This method has a special implementation in {@link CDOTransaction} as well.
   *
   * @see CDOTransaction#hasConflict()
   */
  public boolean hasConflict();

  /**
   * Returns <code>true</code> if a resource with the given path exists in the repository, <code>false</code>.
   * Applies to {@link CDOFileResource file resources}, as well.
   *
   * @see #getResource(String, boolean)
   */
  public boolean hasResource(String path);

  /**
   * @see ResourceSet#getResource(URI, boolean)
   */
  public CDOResource getResource(String path, boolean loadOnDemand) throws CDOResourceNodeNotFoundException;

  /**
   * Same as {@link #getResource(String, boolean) getResource(String, true)}.
   *
   * @see ResourceSet#getResource(URI, boolean)
   */
  public CDOResource getResource(String path) throws CDOResourceNodeNotFoundException;

  /**
   * @since 4.2
   */
  public CDOTextResource getTextResource(String path) throws CDOResourceNodeNotFoundException;

  /**
   * @since 4.2
   */
  public CDOBinaryResource getBinaryResource(String path) throws CDOResourceNodeNotFoundException;

  /**
   * Returns the resource node with the given path.
   *
   * @return never <code>null</code>.
   * @throws CDOException
   *           if no such resource node exists.
   */
  public CDOResourceNode getResourceNode(String path) throws CDOResourceNodeNotFoundException;

  /**
   * @since 4.2
   */
  public CDOResourceFolder getResourceFolder(String path) throws CDOResourceNodeNotFoundException;

  /**
   * Returns the root resource of the repository.
   * <p>
   * The root resource is a special resource with only {@link CDOResourceNode CDOResourceNodes} in its contents list.
   * You can use it as the main entry into the new resource and folder structure.
   */
  public CDOResource getRootResource();

  /**
   * Sets the new {@link Map map} to be used as a cache for various <code>get*Resource*(String path)</code> methods.
   * <p>
   * Can be used to reset/clear the resource path cache by passing a <code>new HashMap&lt;String, CDOID&gt;()</code>.
   * Smarter maps could implement a LRU eviction policy to limit the map capacity.
   * Passing <code>null</code> disables resource path caching.
   * <p>
   * The default value is <code>new HashMap&lt;String, CDOID&gt;()</code>.
   *
   * @see #getResourceNode(String)
   * @see #getResource(String)
   * @see #getResource(String, boolean)
   * @see #getBinaryResource(String)
   * @see #getTextResource(String)
   * @see #getResourceFolder(String)
   * @see #hasResource(String)
   * @since 4.2
   */
  public void setResourcePathCache(Map<String, CDOID> resourcePathCache);

  /**
   * Returns a {@link URI} that can be used in {@link ResourceSet#getResource(URI, boolean)} to load the resource with the specified path.
   *
   * @since 4.4
   */
  public URI createResourceURI(String path);

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
   */
  public CloseableIterator<CDOResourceNode> queryResourcesAsync(CDOResourceFolder folder, String name, boolean exactMatch);

  /**
   * Returns a list of the instances of the given type.
   *
   * @since 4.3
   */
  public <T extends EObject> List<T> queryInstances(EClass type);

  /**
   * Returns an iterator over the instances of the given type. The underlying query will be executed asynchronously.
   *
   * @since 4.3
   */
  public <T extends EObject> CloseableIterator<T> queryInstancesAsync(EClass type);

  /**
   * Returns an iterator over the instances of the given type. The underlying query will be executed asynchronously.
   *
   * @since 4.6
   */
  public <T extends EObject> CloseableIterator<T> queryInstancesAsync(EClass type, boolean exact);

  /**
   * Returns a list of {@link CDOObjectReference object references} that represent the cross references to the specified
   * target object.
   *
   * @param targetObject
   *          The target object that referencing objects are requested for.
   *          An external target object can be used with the help of {@link CDOUtil#wrapExternalObject(EObject, CDOView) CDOUtil.wrapExternalObject()}.
   * @param sourceReferences
   *          The reference features that referencing objects are requested for, or an empty array if all reference
   *          features are to be used in the request.
   *
   * @since 4.0
   * @see CDOView#queryXRefs(Set, EReference...)
   * @see CDOView#queryXRefsAsync(Set, EReference...)
   * @see CDOUtil#wrapExternalObject(EObject, CDOView)
   */
  public List<CDOObjectReference> queryXRefs(CDOObject targetObject, EReference... sourceReferences);

  /**
   * Returns a list of {@link CDOObjectReference object references} that represent the cross references to the specified
   * target objects.
   *
   * @param targetObjects
   *          The set of target objects that referencing objects are requested for.
   *          External target objects can be used with the help of {@link CDOUtil#wrapExternalObject(EObject, CDOView) CDOUtil.wrapExternalObject()}.
   * @param sourceReferences
   *          The reference features that referencing objects are requested for, or an empty array if all reference
   *          features are to be used in the request.
   * @since 3.0
   * @see CDOView#queryXRefs(CDOObject, EReference...)
   * @see CDOView#queryXRefsAsync(Set, EReference...)
   * @see CDOUtil#wrapExternalObject(EObject, CDOView)
   */
  public List<CDOObjectReference> queryXRefs(Set<CDOObject> targetObjects, EReference... sourceReferences);

  /**
   * Returns an iterator over the {@link CDOObjectReference object references} that represent the cross references to
   * the specified target objects. The underlying query will be executed asynchronously.
   *
   * @param targetObjects
   *          The set of target objects that referencing objects are requested for.
   *          External target objects can be used with the help of {@link CDOUtil#wrapExternalObject(EObject, CDOView) CDOUtil.wrapExternalObject()}.
   * @param sourceReferences
   *          The reference features that referencing objects are requested for, or an empty array if all reference
   *          features are to be used in the request.
   * @since 3.0
   * @see CDOView#queryXRefs(CDOObject, EReference...)
   * @see CDOView#queryXRefs(Set, EReference...)
   * @see CDOUtil#wrapExternalObject(EObject, CDOView)
   */
  public CloseableIterator<CDOObjectReference> queryXRefsAsync(Set<CDOObject> targetObjects, EReference... sourceReferences);

  /**
   * Returns the objects with the given CDOIDs.
   * <p>
   * If objects are missing from the local cache they are loaded from the server in one round-trip.
   *
   * @param ids
   *          the collection of CDOIDs that identify the CDOObjects to return.
   * @return a map that contains the CDOObjects with the given CDOIDs.
   *
   * @since 4.13
   */
  public Map<CDOID, CDOObject> getObjects(Collection<CDOID> ids);

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
   *
   * @see #getObject(CDOID, boolean)
   */
  public CDOObject getObject(CDOID id);

  /**
   * Takes an object from a (possibly) different view and <em>contextifies</em> it for the usage with this view.
   * <ul>
   * <li>If the given object is contained in this view it is returned unmodified.
   * <li>If the given object can not be cast to {@link CDOObject} it is returned unmodified.
   * <li>If the view of the given object is contained in a different session an <code>IllegalArgumentException</code> is
   * thrown.
   * <li>If <code>null</code> is passed <code>null</code> is returned.
   * </ul>
   */
  public <T extends EObject> T getObject(T objectFromDifferentView);

  /**
   * Returns <code>true</code> if an {@link CDOObject object} with the given {@link CDOID id} is currently registered in
   * this view, <code>false</code> otherwise.
   */
  public boolean isObjectRegistered(CDOID id);

  /**
   * Reloads the given {@link CDOObject objects} from the repository.
   *
   * @deprecated As of 4.3 no longer supported because it is unsafe to reload single objects.
   */
  @Deprecated
  public int reload(CDOObject... objects);

  /**
   * Refreshes the {@link #getLockStates(Collection) lock states} of this {@link CDOView view} with the latest states from the repository.
   *
   * If a lock state consumer is passed it is called for each resulting new lock state.
   *
   * @since 4.12
   */
  public void refreshLockStates(Consumer<CDOLockState> consumer);

  /**
   * Get an array of {@link CDOLockState lock states} corresponding to the specified collection of {@link CDOID ids}.
   *
   * If the collection of {@link CDOID ids} is empty, {@link CDOLockState lock states} of all objects are returned.
   *
   * @since 4.6
   */
  public CDOLockState[] getLockStates(Collection<CDOID> ids);

  /**
   * Get an array of {@link CDOLockState lock states} corresponding to the specified collection of {@link CDOID ids}.
   *
   * If the collection of {@link CDOID ids} is empty, {@link CDOLockState lock states} of all objects are returned.
   *
   * @since 4.19
   */
  public CDOLockState[] getLockStates(Collection<CDOID> ids, boolean loadOnDemand);

  /**
   * Get an array of {@link CDOLockState lock states} corresponding to the specified collection of {@link CDOObject objects}.
   *
   * If the collection of {@link CDOObject objects} is empty, {@link CDOLockState lock states} of all locked objects are returned.
   *
   * @since 4.6
   */
  public CDOLockState[] getLockStatesOfObjects(Collection<? extends CDOObject> objects);

  /**
   * Locks the given objects. Once the objects are locked, they will not be changed remotely or go in conflict state.
   *
   * @since 3.0
   */
  public void lockObjects(Collection<? extends CDOObject> objects, LockType lockType, long timeout) throws InterruptedException;

  public void lockObjects(Collection<? extends CDOObject> objects, LockType lockType, long timeout, boolean recursive) throws InterruptedException;

  /**
   * Unlocks the given locked objects of this view.
   */
  public void unlockObjects(Collection<? extends CDOObject> objects, LockType lockType);

  public void unlockObjects(Collection<? extends CDOObject> objects, LockType lockType, boolean recursive);

  /**
   * Unlocks all locked objects of this view.
   *
   * @since 2.0
   */
  public void unlockObjects();

  /**
   * @since 4.0
   * @deprecated Use {@link #enableDurableLocking()} instead or {@link #disableDurableLocking(boolean)}, respectively.
   */
  @Deprecated
  public String enableDurableLocking(boolean enable);

  /**
   * Enables the storage of all information that's needed to {@link CDOSession#openView(String) reopen} this view at a
   * later point in time. This information includes the {@link CDOBranchPoint branch point}, the user ID of the
   * {@link CDOSession session}, whether it's a read-only view or a {@link CDOTransaction transaction} and all the locks
   * that are acquired or will be acquired while durable locking is enabled.
   *
   * @see CDOSession#openView(String)
   * @see CDOSession#openView(String, ResourceSet)
   * @see CDOSession#openTransaction(String)
   * @see CDOSession#openTransaction(String, ResourceSet)
   * @see #disableDurableLocking(boolean)
   * @since 4.1
   */
  public String enableDurableLocking();

  /**
   * Disables the storage of all information that's needed to {@link CDOSession#openView(String) reopen} this view at a
   * later point in time. If such information is stored when this method is called it is removed. Note that locks
   * acquired by this view are only released if <code>true</code> is passed to the <code>releaseLocks</code> parameter.
   *
   * @see #enableDurableLocking()
   * @since 4.1
   */
  public void disableDurableLocking(boolean releaseLocks);

  /**
   * @since 3.0
   */
  public void addObjectHandler(CDOObjectHandler handler);

  /**
   * @since 3.0
   */
  public void removeObjectHandler(CDOObjectHandler handler);

  /**
   * @since 3.0
   */
  public CDOObjectHandler[] getObjectHandlers();

  /**
   * @since 4.6
   */
  public void addRegistrationHandler(CDORegistrationHandler handler);

  /**
   * @since 4.6
   */
  public void removeRegistrationHandler(CDORegistrationHandler handler);

  /**
   * @since 4.6
   */
  public CDORegistrationHandler[] getRegistrationHandlers();

  /**
   * Same as <code>createQuery(language, queryString, null)</code>.
   *
   * @see #createQuery(String, String, Object)
   * @since 2.0
   */
  public CDOQuery createQuery(String language, String queryString);

  /**
   * @since 4.0
   */
  public CDOQuery createQuery(String language, String queryString, Object context);

  /**
   * @since 4.0
   */
  public CDOChangeSetData compareRevisions(CDOBranchPoint source);

  /**
   * @since 2.0
   */
  @Override
  public Options options();

  /**
   * Encapsulates a set of notifying {@link CDOView view} configuration options.
   *
   * @author Simon McDuff
   * @noextend This interface is not intended to be extended by clients.
   * @noimplement This interface is not intended to be implemented by clients.
   */
  public interface Options extends org.eclipse.emf.cdo.common.CDOCommonView.Options
  {
    /**
     * Returns the {@link CDOView view} of this options object.
     *
     * @since 4.0
     */
    @Override
    public CDOView getContainer();

    /**
     * @since 3.0
     */
    public static final int DEFAULT_REVISION_PREFETCHING = 100;

    public static final int NO_REVISION_PREFETCHING = 1;

    /**
     * Returns <code>true</code> if the {@link CDOObject objects} in this view will notify their
     * {@link org.eclipse.emf.common.notify.Adapter adapters} about the fact that they are <em>loaded</em>,
     * <code>false</code> otherwise.
     *
     * @since 4.1
     */
    public boolean isLoadNotificationEnabled();

    /**
     * Specifies whether the {@link CDOObject objects} in this view will notify their
     * {@link org.eclipse.emf.common.notify.Adapter adapters} about the fact that they are <em>loaded</em> or not.
     *
     * @since 4.1
     */
    public void setLoadNotificationEnabled(boolean enabled);

    /**
     * Returns <code>true</code> if the {@link CDOObject objects} in this view will notify their
     * {@link org.eclipse.emf.common.notify.Adapter adapters} about the fact that they are <em>detached</em> (due to
     * remote changes), <code>false</code> otherwise.
     *
     * @see CDONotification#DETACH_OBJECT
     * @since 4.1
     */
    public boolean isDetachmentNotificationEnabled();

    /**
     * Specifies whether the {@link CDOObject objects} in this view will notify their
     * {@link org.eclipse.emf.common.notify.Adapter adapters} about the fact that they are <em>detached</em> (due to
     * remote changes) or not.
     *
     * @see CDONotification#DETACH_OBJECT
     * @since 4.1
     */
    public void setDetachmentNotificationEnabled(boolean enabled);

    /**
     * Returns <code>true</code> if the {@link CDOObject objects} in this view will notify their
     * {@link org.eclipse.emf.common.notify.Adapter adapters} about the fact that they are <em>invalidated</em> (due to
     * remote changes), <code>false</code> otherwise.
     *
     * @see CDOInvalidationNotification
     */
    public boolean isInvalidationNotificationEnabled();

    /**
     * Specifies whether the {@link CDOObject objects} in this view will notify their
     * {@link org.eclipse.emf.common.notify.Adapter adapters} about the fact that they are <em>invalidated</em> (due to
     * remote changes) or not.
     *
     * @see CDOInvalidationNotification
     */
    public void setInvalidationNotificationEnabled(boolean enabled);

    /**
     * @since 3.0
     */
    public CDOInvalidationPolicy getInvalidationPolicy();

    /**
     * @since 3.0
     */
    public void setInvalidationPolicy(CDOInvalidationPolicy policy);

    /**
     * Returns the current set of {@link CDOAdapterPolicy change subscription policies}.
     *
     * @return The current set of change subscription policies, never <code>null</code>.
     * @see #addChangeSubscriptionPolicy(CDOAdapterPolicy)
     */
    public CDOAdapterPolicy[] getChangeSubscriptionPolicies();

    /**
     * Adds a change subscription policy to this view.
     * <p>
     * To activate a policy, you must do the following: <br>
     * <code>view.options().addChangeSubscriptionPolicy(CDOChangeSubscriptionPolicy.ALL);</code>
     * <p>
     * To register an object, you must add an adapter to the object in which you are interested:<br>
     * <code>eObject.eAdapters().add(myAdapter);</code>
     * <p>
     * By activating this feature, each object having at least one adapter that matches the current policy will be
     * registered with the server and will be notified for each change occurring in the scope of any other transaction.
     * <p>
     * <ul>
     * <li>{@link CDOAdapterPolicy#NONE} - Ignored.
     * <li>{@link CDOAdapterPolicy#ALL} - Enabled for all adapters used.
     * <li>{@link CDOAdapterPolicy#CDO} - Enabled only for adapters that implement {@link CDOAdapter}.
     * <li>Any other class that implements {@link CDOAdapterPolicy} will enable for whatever rules defined in that class.
     * </ul>
     * <p>
     * If <code>myAdapter</code> in the above example matches the current policy, <code>eObject</code> will be
     * registered with the server and you will receive all changes from other transaction.
     * <p>
     * When the policy is changed all objects in the cache will automatically be recalculated.
     * <p>
     * You can subscribe to temporary objects. Even if you cannot receive notifications from other
     * {@link CDOTransaction} for these because they are only local to you, at commit time these objects will be
     * registered automatically.
     *
     * @see #removeChangeSubscriptionPolicy(CDOAdapterPolicy)
     * @see #getChangeSubscriptionPolicies()
     */
    public void addChangeSubscriptionPolicy(CDOAdapterPolicy policy);

    /**
     * Removes a change subscription policy from this view.
     *
     * @see #addChangeSubscriptionPolicy(CDOAdapterPolicy)
     * @see #getChangeSubscriptionPolicies()
     */
    public void removeChangeSubscriptionPolicy(CDOAdapterPolicy policy);

    /**
     * Returns the reference type to be used in the internal object cache.
     *
     * @return Either {@link ReferenceType#STRONG STRONG}, {@link ReferenceType#SOFT SOFT} or {@link ReferenceType#WEAK
     *         WEAK}.
     */
    public ReferenceType getCacheReferenceType();

    /**
     * Sets the reference type to be used in the internal object cache to either {@link ReferenceType#STRONG STRONG},
     * {@link ReferenceType#SOFT SOFT} or {@link ReferenceType#WEAK WEAK}. If <code>null</code> is passed the default
     * reference type {@link ReferenceType#SOFT SOFT} is set. If the given reference type does not differ from the one
     * being currently set the new value is ignored and <code>false</code> is returned. Otherwise existing object
     * references are converted to the new type and <code>true</code> is returned.
     */
    public boolean setCacheReferenceType(ReferenceType referenceType);

    /**
     * Returns the strong reference policy in use.
     *
     * @see #setStrongReferencePolicy(CDOAdapterPolicy)
     */
    public CDOAdapterPolicy getStrongReferencePolicy();

    /**
     * Sets the policy that determines what object/adapter pairs of this {@link CDOView view}
     * are supposed to be protected against garbage collection.
     * <p>
     * A view uses references of the type determined by {@link #getCacheReferenceType()} to hold on to loaded objects.
     * If this type is <b>not</b> {@link ReferenceType#STRONG STRONG} and the application does <b>not</b> hold other strong
     * references to an object then this object and possibly any adapters attached to this object are subject to garbage collection.
     * <p>
     * To avoid automatic garbage collection while specific adapters are attached to an object this view calls the
     * {@link CDOAdapterPolicy#isValid(EObject, org.eclipse.emf.common.notify.Adapter) CDOAdapterPolicy.isValid()}
     * method for all adapters that are attached to this object.
     * An extra strong reference to this object is maintained if any of these calls return <code>true</code>.
     * <p>
     * The following adapter policies can be used as strong reference policies:
     * <ul>
     * <li>{@link CDOAdapterPolicy#NONE} - No adapter will prevent GC.
     * <li>{@link CDOAdapterPolicy#ALL} - Any adapter prevent GC.
     * <li>{@link CDOAdapterPolicy#CDO} - Only adapters that implement {@link CDOAdapter} will prevent GC.
     * <li>Any other class that implements {@link CDOAdapterPolicy} will prevent GC according to whatever rules defined
     *     in that class.
     * </ul>
     * <p>
     * The default strong reference policy is {@link CDOAdapterPolicy#ALL}, preventing garbage collection for all adapted objects.
     *
     * @see #getStrongReferencePolicy()
     */
    public void setStrongReferencePolicy(CDOAdapterPolicy policy);

    /**
     * Returns the CDOStaleReferencePolicy in use.
     *
     * @since 3.0
     * @deprecated Use {@link #getStaleReferencePolicy()}
     */
    @Deprecated
    public CDOStaleReferencePolicy getStaleReferenceBehaviour();

    /**
     * Sets a policy on how to deal with stale references.
     *
     * @since 3.0
     * @deprecated Use {@link #setStaleReferencePolicy(CDOStaleReferencePolicy)}
     */
    @Deprecated
    public void setStaleReferenceBehaviour(CDOStaleReferencePolicy policy);

    /**
     * Returns the CDOStaleReferencePolicy in use.
     *
     * @since 4.1
     */
    public CDOStaleReferencePolicy getStaleReferencePolicy();

    /**
     * Sets a policy on how to deal with stale references.
     *
     * @since 4.1
     */
    public void setStaleReferencePolicy(CDOStaleReferencePolicy policy);

    /**
     * Returns the CDORevisionPrefetchingPolicy in use.
     */
    public CDORevisionPrefetchingPolicy getRevisionPrefetchingPolicy();

    /**
     * The CDORevisionPrefetchingPolicy feature of the CDOView allows CDO users to fetch many objects at a time.
     * <p>
     * The difference between the CDOCollectionLoadingPolicy feature and the CDORevisionPrefetchingPolicy feature is
     * subtle. The CDOCollectionLoadingPolicy feature determines how and when to fetch CDOIDs, while the
     * CDORevisionPrefetchingPolicy feature determines how and when to resolve CDOIDs (i.e. fetch the target objects).
     * <p>
     * <code>view.options().setRevisionPrefetchingPolicy (CDONet4jUtil.createRevisionPrefetchingPolicy(10));</code>
     * <p>
     * The end-user could provide its own implementation of the CDORevisionPrefetchingPolicy interface.
     */
    public void setRevisionPrefetchingPolicy(CDORevisionPrefetchingPolicy prefetchingPolicy);

    /**
     * @since 4.1
     */
    public CDOFeatureAnalyzer getFeatureAnalyzer();

    /**
     * @since 4.1
     */
    public void setFeatureAnalyzer(CDOFeatureAnalyzer featureAnalyzer);

    /**
     * The returned adapter policy may optionally implement {@link CDOListWrapper}, see {@link CDOObjectImpl#eAdapters()}.
     *
     * @since 4.12
     */
    public CDOAdapterPolicy getClearAdapterPolicy();

    /**
     * @since 4.12
     */
    public void setClearAdapterPolicy(CDOAdapterPolicy policy);

    /**
     * An {@link IOptionsEvent options event} fired from view {@link CDOView#options() options} when the
     * {@link Options#setLoadNotificationEnabled(boolean) load notification enabled} option has changed.
     *
     * @author Eike Stepper
     * @noextend This interface is not intended to be extended by clients.
     * @noimplement This interface is not intended to be implemented by clients.
     * @since 4.1
     */
    public interface LoadNotificationEvent extends IOptionsEvent
    {
    }

    /**
     * An {@link IOptionsEvent options event} fired from view {@link CDOView#options() options} when the
     * {@link Options#setDetachmentNotificationEnabled(boolean) detachment notification enabled} option has changed.
     *
     * @author Eike Stepper
     * @noextend This interface is not intended to be extended by clients.
     * @noimplement This interface is not intended to be implemented by clients.
     * @since 4.1
     */
    public interface DetachmentNotificationEvent extends IOptionsEvent
    {
    }

    /**
     * An {@link IOptionsEvent options event} fired from view {@link CDOView#options() options} when the
     * {@link Options#setInvalidationNotificationEnabled(boolean) invalidation notification enabled} option has changed.
     *
     * @author Eike Stepper
     * @noextend This interface is not intended to be extended by clients.
     * @noimplement This interface is not intended to be implemented by clients.
     */
    public interface InvalidationNotificationEvent extends IOptionsEvent
    {
    }

    /**
     * An {@link IOptionsEvent options event} fired from view {@link CDOView#options() options} when the
     * {@link Options#setInvalidationPolicy(CDOInvalidationPolicy) invalidation policy} option has changed.
     *
     * @author Eike Stepper
     * @since 3.0
     * @noextend This interface is not intended to be extended by clients.
     * @noimplement This interface is not intended to be implemented by clients.
     */
    public interface InvalidationPolicyEvent extends IOptionsEvent
    {
    }

    /**
     * An {@link IOptionsEvent options event} fired from view {@link CDOView#options() options} when the
     * {@link Options#addChangeSubscriptionPolicy(CDOAdapterPolicy) change subscription policies} option has changed.
     *
     * @author Eike Stepper
     * @noextend This interface is not intended to be extended by clients.
     * @noimplement This interface is not intended to be implemented by clients.
     */
    public interface ChangeSubscriptionPoliciesEvent extends IOptionsEvent
    {
    }

    /**
     * An {@link IOptionsEvent options event} fired from view {@link CDOView#options() options} when the
     * {@link Options#setCacheReferenceType(ReferenceType) cache reference type} option has changed.
     *
     * @author Eike Stepper
     * @noextend This interface is not intended to be extended by clients.
     * @noimplement This interface is not intended to be implemented by clients.
     */
    public interface CacheReferenceTypeEvent extends IOptionsEvent
    {
    }

    /**
     * An {@link IOptionsEvent options event} fired from view {@link CDOView#options() options} when the
     * {@link Options#setStrongReferencePolicy(CDOAdapterPolicy) strong reference policy} option has changed.
     *
     * @author Eike Stepper
     * @noextend This interface is not intended to be extended by clients.
     * @noimplement This interface is not intended to be implemented by clients.
     * @since 4.0
     */
    public interface StrongReferencePolicyEvent extends IOptionsEvent
    {
    }

    /**
     * An {@link IOptionsEvent options event} fired from view {@link CDOView#options() options} when the
     * {@link Options#setStrongReferencePolicy(CDOAdapterPolicy) strong reference policy} option has changed.
     *
     * @author Eike Stepper
     * @noextend This interface is not intended to be extended by clients.
     * @noimplement This interface is not intended to be implemented by clients.
     * @deprecated Use {@link StrongReferencePolicyEvent} instead.
     */
    @Deprecated
    public interface ReferencePolicyEvent extends StrongReferencePolicyEvent
    {
    }

    /**
     * An {@link IOptionsEvent options event} fired from view {@link CDOView#options() options} when the
     * {@link Options#setStaleReferenceBehaviour(CDOStaleReferencePolicy) stale reference type} option has changed.
     *
     * @since 3.0
     * @noextend This interface is not intended to be extended by clients.
     * @noimplement This interface is not intended to be implemented by clients.
     */
    public interface StaleReferencePolicyEvent extends IOptionsEvent
    {
    }

    /**
     * An {@link IOptionsEvent options event} fired from view {@link CDOView#options() options} when the
     * {@link Options#setRevisionPrefetchingPolicy(CDORevisionPrefetchingPolicy) revision prefetching policy} option has
     * changed.
     *
     * @author Eike Stepper
     * @noextend This interface is not intended to be extended by clients.
     * @noimplement This interface is not intended to be implemented by clients.
     */
    public interface RevisionPrefetchingPolicyEvent extends IOptionsEvent
    {
    }

    /**
     * An {@link IOptionsEvent options event} fired from view {@link CDOView#options() options} when the
     * {@link Options#setFeatureAnalyzer(CDOFeatureAnalyzer) feature analyzer} option has
     * changed.
     *
     * @author Eike Stepper
     * @noextend This interface is not intended to be extended by clients.
     * @noimplement This interface is not intended to be implemented by clients.
     * @since 4.1
     */
    public interface FeatureAnalyzerEvent extends IOptionsEvent
    {
    }

    /**
     * An {@link IOptionsEvent options event} fired from view {@link CDOView#options() options} when the
     * {@link Options#setClearAdapterPolicy(CDOAdapterPolicy) clear adapter policy} option has
     * changed.
     *
     * @author Eike Stepper
     * @noextend This interface is not intended to be extended by clients.
     * @noimplement This interface is not intended to be implemented by clients.
     * @since 4.12
     */
    public interface ClearAdapterPolicyEvent extends IOptionsEvent
    {
    }
  }
}
