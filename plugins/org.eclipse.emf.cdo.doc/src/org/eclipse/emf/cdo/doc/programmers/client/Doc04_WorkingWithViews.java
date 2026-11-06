/*
 * Copyright (c) 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.doc.programmers.client;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOObjectReference;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lob.CDOBlob;
import org.eclipse.emf.cdo.common.lob.CDOClob;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.doc.programmers.server.Doc08_SecurityQueriesAndSpecializedExtensions;
import org.eclipse.emf.cdo.eresource.CDOBinaryResource;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.eresource.CDOTextResource;
import org.eclipse.emf.cdo.server.IQueryHandler;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOAdapterPolicy;
import org.eclipse.emf.cdo.view.CDOFeatureAnalyzer;
import org.eclipse.emf.cdo.view.CDOQuery;
import org.eclipse.emf.cdo.view.CDOUnit;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewEvent;

import org.eclipse.net4j.util.collection.CloseableIterator;
import org.eclipse.net4j.util.concurrent.CriticalSection;
import org.eclipse.net4j.util.concurrent.CriticalSection.LockedCriticalSection;
import org.eclipse.net4j.util.concurrent.DelegableReentrantLock;
import org.eclipse.net4j.util.concurrent.DelegableReentrantLock.DelegateDetector;
import org.eclipse.net4j.util.event.IListener;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;

import org.eclipse.swt.widgets.Display;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Working with Views
 * <p>
 * This chapter covers view management, resource handling, querying, transactions, and related options in CDO client
 * applications. Views are central to accessing and interacting with model data in a CDO repository. Understanding how
 * to use views effectively is key to building responsive and scalable applications.
 * <p>
 * <b>Table of Contents</b> {@toc}
 *
 * @author Eike Stepper
 */
public class Doc04_WorkingWithViews
{
  /**
   * Understanding Views and Their Types
   * <p>
   * A view binds one client-side {@link CDOView} and its objects to a session, a resource set,
   * and a branch point. Use a read-only view for navigation, queries, or a stable historical target; use a
   * {@link CDOTransaction} when the operation must stage local edits and commit them. A normal head view can receive
   * remote invalidations as repository commits arrive, so it is not a frozen snapshot. Audit or historical views read
   * an explicit branch/time point and are read-only. Objects are view-scoped: reopen by persistent ID in another view
   * instead of carrying model objects across view lifetimes.
   * <p>
   * {@link #openReadOnlyView(CDOSession) OpenReadOnlyView.java} shows a short-lived read; the transaction example
   * {@link #modifyAndCommit(CDOSession, CDOID, EStructuralFeature, Object) ModifyAndCommit.java} shows an editable view and its commit boundary.
   */
  public class UnderstandingViewsAndTheirTypes
  {
  }

  /**
   * Opening and Closing Views
   * <p>
   * Open views from a session with {@link CDOSession#openView()} for the current branch head, or choose an explicit
   * branch point through the session's view-opener overloads. Open a transaction when the operation must modify data;
   * a historical view is read-only. The session tracks its open views, their resource sets, caches, listeners, and
   * remote view state, so close each view in a {@code finally} block or try-with-resources pattern when the work ends.
   * Closing a view releases those resources and ends the validity of its model-object context. It does not close the
   * session, which may create later views. Avoid sharing a single long-lived view across unrelated operations just to
   * avoid reopening it; view lifetime should match the consistency and object-identity scope the application needs.
   * <p>
   * {@link #openReadOnlyView(CDOSession) OpenReadOnlyView.java} and
   * {@link #modifyAndCommit(CDOSession, CDOID, EStructuralFeature, Object) ModifyAndCommit.java} shows the transaction cleanup pattern.
   */
  public class OpeningAndClosingViews
  {
  }

  /**
   * Thread Safety
   * <p>
   * Views in CDO are inherently thread-safe, but this guarantee applies only to <b>individual</b> method calls. When performing
   * <b>multiple</b> operations that need to be atomic or consistent, developers must use a {@link CriticalSection} to
   * synchronize access to the view. A CDO view provides its critical section via the {@link CDOView#sync()}.
   * <p>
   * Each individual access to a view or one of its objects is synchronized with the view's internal work. This does
   * not make a sequence of separate calls atomic: an invalidation may occur between calls. Use {@link CDOView#sync()}
   * around the smallest multi-call operation that must observe one coherent state. Keep that critical section short;
   * do not wait for network work, block on another thread, or call arbitrary application callbacks while holding it.
   */
  public class ThreadSafety
  {
    /**
     * Using Critical Sections
     * <p>
     * To ensure thread-safe access to a CDO view when performing multiple operations, use the view's critical section
     * object. It is returned by the {@link CDOView#sync()} method. The critical section provides methods to execute code blocks
     * safely, such as {@link CriticalSection#run(Runnable)} and {@link CriticalSection#call(Callable)}.
     * <p>
     * Here is an example of using a critical section with a callable to access multiple objects in a view atomically:
     * {@link #criticalSectionWithCallable(CDOView, CDOID, CDOID, CDOID) CriticalSectionWithCallable.java}
     * <p>
     * The {@link CriticalSection} interface provides the following methods:
     * <ul>
     * <li>{@link CriticalSection#run(Runnable) run(Runnable)} - Executes a Runnable within the critical section.
     * <li>{@link CriticalSection#call(Callable) call(Callable)} - Executes a Callable within the critical section and returns its result.
     * <li>{@link CriticalSection#call(Class, Callable) call(Class, Callable)} - Executes a Callable within the critical section, specifying the exception type it may throw.
     * <li>{@link CriticalSection#supply(java.util.function.Supplier) supply(Supplier)} - Executes a Supplier within the critical section and returns its result.
     * <li>{@link CriticalSection#supply(java.util.function.BooleanSupplier) supply(BooleanSupplier)} - Executes a BooleanSupplier within the critical section and returns its boolean result.
     * <li>{@link CriticalSection#supply(java.util.function.IntSupplier) supply(IntSupplier)} - Executes an IntSupplier within the critical section and returns its int result.
     * <li>{@link CriticalSection#supply(java.util.function.LongSupplier) supply(LongSupplier)} - Executes a LongSupplier within the critical section and returns its long result.
     * <li>{@link CriticalSection#supply(java.util.function.DoubleSupplier) supply(DoubleSupplier)} - Executes a DoubleSupplier within the critical section and returns its double result.
     * <li>{@link CriticalSection#newCondition() newCondition()} - Creates a new Condition associated with the critical section.
     * </ul>
     * <p>
     * A view's critical section uses a real reentrant lock; the current default is a non-fair reentrant lock. A lock supplied with
     * {@link CDOUtil#setNextViewLock(Lock)} or the session's delegable-lock option is used when configured. Synchronizing
     * directly on the view object is unsupported. By default, CDO detects this when a view lock is next entered and throws
     * {@link UnsupportedOperationException}, directing the caller to {@link CDOView#sync()}. Set
     * <code>-Dorg.eclipse.emf.cdo.view.DISABLE_INTRINSIC_MONITOR_CHECK=true</code> to disable that safety check.
     * <p>
     * Deprecated monitor and lock methods fail fast by default. Set
     * <code>-Dorg.eclipse.emf.cdo.view.ENABLE_LEGACY_LOCKING_API=true</code> to re-enable their best-effort behavior.
     * The compatibility monitor returned by <code>getViewMonitor()</code> coordinates only callers that synchronize on
     * that returned object; it does not coordinate by itself with CDO's internal view lock. The deprecated lock methods
     * use the real view lock when enabled.
     * <p>
     * Here's an example of setting a custom lock for the next view to be opened:
     * {@link #customLockForNextView(CDOSession) CustomLockForNextView.java}
     * <p>
     * A {@link DelegableReentrantLock} is useful when a framework synchronously hands work to another thread that must
     * access the same view, such as an SWT UI callback. It solves that specific lock-ownership handoff; it is not a
     * reason to hold the critical section across arbitrary blocking work. Register the relevant delegate detector and
     * enable the session option or install the lock before opening the view.
     */
    public class UsingCriticalSections
    {
      /**
       * @snip
       */
      @SuppressWarnings("unused")
      public void criticalSectionWithCallable(CDOView view, CDOID id1, CDOID id2, CDOID id3) throws Exception
      {
        CriticalSection sync = view.sync();

        MyResult result = sync.call(() -> {
          // Access the view and its objects safely here.
          CDOObject object1 = view.getObject(id1);
          CDOObject object2 = view.getObject(id2);
          CDOObject object3 = view.getObject(id3);

          // Return a result object.
          return new MyResult();
        });
      }

      class MyResult
      {
      }

      /**
       * @snip
       */
      public void customLockForNextView(CDOSession session) throws Exception
      {
        Lock customLock = new ReentrantLock();
        CDOUtil.setNextViewLock(customLock);
        CDOView view = null;
        try
        {
          view = session.openView();
          CriticalSection sync = view.sync();

          if (!(sync instanceof LockedCriticalSection) || ((LockedCriticalSection)sync).getLock() != customLock)
          {
            throw new IllegalStateException("The configured lock was not installed");
          }
        }
        finally
        {
          if (view != null)
          {
            view.close();
          }

          CDOUtil.setNextViewLock(null);
        }
      }
    }

    /**
     * Using a Delegable Lock
     * <p>
     * As an alternative to the default locking strategy of a view's critical section, you can use
     * a {@link DelegableReentrantLock}, which allows to delegate the lock ownership to a
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
     * Here is an example that illustrates this scenario:
     * {@link #deadlockExample(CDOView, CDOID) DeadlockExample.java}
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
     * <p>
     * <code>DelegableReentrantLock</code>
     * uses {@link DelegateDetector}s to determine whether the current thread is allowed to delegate the lock ownership
     * to a different thread. A <code>DelegateDetector</code> can be registered with the lock by calling
     * {@link DelegableReentrantLock#addDelegateDetector(DelegateDetector)}. The <code>org.eclipse.net4j.util.ui</code> plugin provides
     * a <code>DisplayDelegateDetector</code> for the SWT/JFace UI thread that detects calls to
     * <code>Display.syncExec()</code>.
     * <p>
     * There are two ways to use a <code>DelegableReentrantLock</code> with a CDO view:
     * <ol>
     * <li>Set the lock as the next view lock by calling {@link CDOUtil#setNextViewLock(Lock)} before opening the view.
     *    This way, the view will use the lock for its critical section. Here's an example:
     *    <p>
     *    {@link #individualViewLock(CDOSession) IndividualViewLock.java}
     *    <p>
     * <li>Set {@link CDOSession.Options#setDelegableViewLockEnabled(boolean) delegableViewLockEnabled} to <code>true</code> on the session.
     *    This way, all views opened from the session will use a <code>DelegableReentrantLock</code> for their critical section.
     *    The lock is created automatically and configured with all <code>DelegateDetector</code>s that are registered.
     *    Example:
     *    <p>
     *    {@link #setDelegableViewLockEnabled(CDOSession) SetDelegableViewLockEnabled.java}
     *    <p>
     * </ol>
     */
    public class UsingDelegableLock
    {
      /**
       * @snip
       */
      @SuppressWarnings("unused")
      public void deadlockExample(CDOView view, CDOID id)
      {
        CDOObject object = view.getObject(id);
        object.eAdapters().add(new AdapterImpl()
        {
          @Override
          public void notifyChanged(Notification msg)
          {
            // This code is executed in a non-UI thread and holds the view lock.

            // The following call to Display.syncExec() will execute the Runnable in the UI thread
            // and make the current thread wait for it to complete. During that time the view lock
            // is still held by the current thread.
            Display.getDefault().syncExec(() -> {
              // This code is executed in the UI thread.
              // It tries to access the view while the view lock is held by the non-UI thread.
              // The result is a deadlock.
              CDOResource resource = view.getResource("/my/resource");
            });
          }
        });
      }

      /**
       * @snip
       */
      public void individualViewLock(CDOSession session) throws Exception
      {
        CDOUtil.setNextViewLock(new DelegableReentrantLock());

        CDOView view = session.openView();
        CriticalSection sync = view.sync();

        // Acquire the view lock.
        sync.run(() -> {
          // This code is executed in a non-UI thread and holds the view lock.

          // The following call to Display.syncExec() will execute the Runnable in the UI thread
          Display.getDefault().syncExec(() -> {
            // This code is executed in the UI thread.
            // It can access the view because the lock ownership has been delegated to the UI thread.
            CDOResource resource = view.getResource("/my/resource");
            System.out.println("Resource: " + resource.getURI());
          });
        });
      }

      /**
       * @snip
       */
      public void setDelegableViewLockEnabled(CDOSession session) throws Exception
      {
        session.options().setDelegableViewLockEnabled(true);

        CDOView view = session.openView();
        CriticalSection sync = view.sync();

        // Acquire the view lock.
        sync.run(() -> {
          // This code is executed in a non-UI thread and holds the view lock.

          // The following call to Display.syncExec() will execute the Runnable in the UI thread
          Display.getDefault().syncExec(() -> {
            // This code is executed in the UI thread.
            // It can access the view because the lock ownership has been delegated to the UI thread.
            CDOResource resource = view.getResource("/my/resource");
            System.out.println("Resource: " + resource.getURI());
          });
        });
      }
    }
  }

  /**
   * Understanding the CDO File System
   * <p>
   * The CDO repository exposes a virtual file system for organizing model resources. This section describes the
   * structure and usage of the file system, including root resources, folders, and different resource types.
   * <p>
   * <img src="FileSystem.png"/>
   */
  public class UnderstandingTheCDOFileSystem
  {
    /**
     * The Root Resource
     * <p>
     * The root resource is the entry point to the CDO file system. It contains all top-level folders and resources,
     * providing a hierarchical view of the repository's contents.
     * <p>
     * Each CDO view or transaction can provide the root resource. Here is an example of how to access and list the
     * contents of the root resource:
     * {@link #getRootResource(CDOView) GetRootResource.java}
     * <p>
     * When you don't have a view or transaction available, you can ask a session for the root resource's ID as follows:
     * {@link #getRootResourceID(CDOSession) GetRootResourceID.java}
     * <p>
     * The root resource of a repository is created automatically when the repository is initialized for the first time.
     * It can not be deleted, but its contents can be modified like any other resource.
     */
    public class TheRootResource
    {
      /**
       * @snip
       */
      public void getRootResource(CDOView view) throws Exception
      {
        // Each CDO view or transaction can provide the root resource.
        CDOResource rootResource = view.getRootResource();

        for (EObject content : rootResource.getContents())
        {
          if (content instanceof CDOResourceFolder)
          {
            CDOResourceFolder folder = (CDOResourceFolder)content;
            System.out.println("Folder: " + folder.getName());
          }
          else if (content instanceof CDOResource)
          {
            CDOResource resource = (CDOResource)content;
            System.out.println("Model Resource: " + resource.getName());
          }
          else if (content instanceof CDOBinaryResource)
          {
            CDOBinaryResource binary = (CDOBinaryResource)content;
            System.out.println("Binary File: " + binary.getName());
          }
          else if (content instanceof CDOTextResource)
          {
            CDOTextResource text = (CDOTextResource)content;
            System.out.println("Text File: " + text.getName());
          }
        }
      }

      /**
       * @snip
       */
      public void getRootResourceID(CDOSession session) throws Exception
      {
        CDOID rootResourceID = session.getRepositoryInfo().getRootResourceID();
        CDOBranch mainBranch = session.getBranchManager().getMainBranch();

        CDORevision rootResourceRevision = session.getRevisionManager().request().getRevision(rootResourceID, mainBranch.getHead());
        System.out.println("Root Resource Revision: " + rootResourceRevision);
      }
    }

    /**
     * Resource Folders
     * <p>
     * The CDO resource hierarchy is a repository-side virtual file system. A {@link CDOResourceFolder} is a persistent
     * model object that contains resource nodes; those nodes may be folders, model resources, binary resources, or text
     * resources. Use folders when resource paths need meaningful grouping and use the node APIs to enumerate children
     * without loading every model's contents. Folder creation is a transaction change and becomes visible to other
     * views only after commit.
     * <p>
     * For creating resource folders you need a CDOTransaction. Here is an example that illustrates how to create
     * folders and subfolders:
     * {@link #createFolder(CDOTransaction) CreateFolder.java}
     * <p>
     * Listing subnodes (folders and resources) within a folder does not require a transaction and
     * can be done as follows:
     * {@link #listSubNodes(CDOResourceFolder) ListSubNodes.java}
     * <p>
     * Note that resource folders <b>are</b> model objects and therefore support EMF features such as adapters,
     * notifications, and so on.
     */
    public class ResourceFolders
    {
      /**
       * @snip
       */
      public void createFolder(CDOTransaction transaction) throws Exception
      {
        // Create a new folder at the specified path directly in the transaction.
        CDOResourceFolder folder = transaction.createResourceFolder("/my/new/folder");
        System.out.println("Created folder: " + folder.getPath()); // Outputs: /my/new/folder

        CDOResourceFolder parent = folder.getFolder();
        System.out.println("Parent folder: " + parent.getPath()); // Outputs: /my/new

        CDOResourceFolder parent2 = parent.getFolder();
        System.out.println("Parent parent folder: " + parent2.getPath()); // Outputs: /my

        // Create a subfolder within the newly created folder.
        CDOResourceFolder subfolder = folder.addResourceFolder("subfolder");
        System.out.println("Created subfolder: " + subfolder.getPath()); // Outputs: /my/new/folder/subfolder

        // None of the above changes are visible in other views/transactions.
        // They become visible only after committing the transaction.
        transaction.commit();
      }

      /**
       * @snip
       */
      public void listSubNodes(CDOResourceFolder folder) throws Exception
      {
        // List all nodes (folders and resources) within the folder.
        for (CDOResourceNode node : folder.getNodes())
        {
          System.out.println("Node: " + node.getName());
        }

        // Access a specific subnode by name.
        CDOResourceNode subnode = folder.getNode("subfolder"); // May return null.
        if (subnode instanceof CDOResourceFolder)
        {
          CDOResourceFolder subfolder = (CDOResourceFolder)subnode;
          System.out.println("Subfolder path: " + subfolder.getPath());
        }
      }
    }

    /**
     * Model Resources
     * <p>
     * A {@link CDOResource} is an EMF resource backed by a CDO view. Its root objects and resource metadata participate
     * in the view's model and notification behavior; creating a resource or changing its contents requires a
     * {@link CDOTransaction}. Loading it in a read-only view gives access to repository state through that view's
     * {@code ResourceSet}. Adding a resource to an ordinary EMF {@code ResourceSet} does not persist it in CDO; use the
     * CDO transaction's resource-creation APIs and commit the transaction to publish the change.
     * <p>
     * For creating model resources you need a CDOTransaction. Here is an example that illustrates how to create
     * model resources:
     * {@link #createResource(CDOTransaction) CreateResource.java}
     * <p>
     * Listing root objects and contained objects within a model resource does not require a transaction and
     * can be done as follows:
     * {@link #getContents(CDOResource) GetContents.java}
     * <p>
     * Note that model resources <b>are</b> model objects and therefore support EMF features such as adapters,
     * notifications, and so on.
     */
    public class ModelResources
    {
      /**
       * @snip
       */
      public void createResource(CDOTransaction transaction) throws Exception
      {
        // Create a new folder at the specified path directly in the transaction.
        CDOResource resource = transaction.createResource("/my/new/resource");
        System.out.println("Created resource: " + resource.getPath()); // Outputs: /my/new/resource

        // Your method to create the root object.
        EObject rootObject = createContentTree();

        // Add the root object to the resource's contents.
        resource.getContents().add(rootObject);

        // CDO resources support multiple root objects.
        resource.getContents().add(EcoreUtil.copy(rootObject));

        // None of the above changes are visible in other views/transactions.
        // They become visible only after committing the transaction.
        transaction.commit();
      }

      private EObject createContentTree()
      {
        return null;
      }

      /**
       * @snip
       */
      public void getContents(CDOResource resource) throws Exception
      {
        // List root objects in the resource.
        for (EObject rootObject : resource.getContents())
        {
          System.out.println("Root object: " + rootObject);
        }

        // Iterate over all contained objects in the resource. Normal EMF model object operation.
        TreeIterator<EObject> allContents = resource.eAllContents();
        allContents.forEachRemaining(eObject -> System.out.println("Contained object: " + eObject));
      }
    }

    /**
     * Binary Resources
     * <p>
     * Binary resources allow storage of non-model data, such as images or files, within the CDO repository.
     * They are based on CDO's special data type {@link CDOBlob}, which supports efficient handling of large binary objects.
     * Large object support is described in detail in the chapter {@link Doc07_LargeObjects}.
     * <p>
     * For creating binary resources you need a CDOTransaction. Here is an example that illustrates how to create
     * binary resources:
     * {@link #createBinaryFile(CDOTransaction) CreateBinaryFile.java}
     * <p>
     * Getting the binary contents of a binary resource does not require a transaction and
     * can be done as follows:
     * {@link #getContents(CDOBinaryResource) GetContents.java}
     * <p>
     * Note that binary resources <b>are</b> model objects and therefore support EMF features such as adapters,
     * notifications, and so on.
     */
    public class BinaryResources
    {
      /**
       * @snip
       */
      public void createBinaryFile(CDOTransaction transaction) throws Exception
      {
        // Create a new binary file at the specified path directly in the transaction.
        CDOBinaryResource binary = transaction.createBinaryResource("/my/new/file");
        System.out.println("Created binary file: " + binary.getPath()); // Outputs: /my/new/file

        CDOBlob blob = transaction.getSession().newBlob(new byte[] { 0, 1, 2, 3, 4, 5 });
        binary.setContents(blob);

        // None of the above changes are visible in other views/transactions.
        // They become visible only after committing the transaction.
        transaction.commit();
      }

      /**
       * @snip
       */
      public void getContents(CDOBinaryResource binary) throws Exception
      {
        // Get the binary contents.
        CDOBlob blob = binary.getContents();

        // Print some information about the binary contents.
        System.out.println("Binary contents ID: " + blob.getID());
        System.out.println("Binary contents size: " + blob.getSize());

        // For demonstration purposes, copy the binary contents to System.out.
        blob.copyTo(System.out);

        try (InputStream stream = blob.getContents())
        {
          // Or do something else with the InputStream...
        }
      }
    }

    /**
     * Text Resources
     * <p>
     * Text resources store textual data, such as configuration files or documentation, in the repository.
     * They are based on CDO's special data type {@link CDOClob}, which supports efficient handling of large text objects.
     * Large object support is described in detail in the chapter {@link Doc07_LargeObjects}.
     * <p>
     * For creating text resources you need a CDOTransaction. Here is an example that illustrates how to create
     * text resources:
     * {@link #createTextFile(CDOTransaction) CreateTextFile.java}
     * <p>
     * Getting the text contents of a text resource does not require a transaction and
     * can be done as follows:
     * {@link #getContents(CDOTextResource) GetContents.java}
     * <p>
     * Note that text resources <b>are</b> model objects and therefore support EMF features such as adapters,
     * notifications, and so on.
     */
    public class TextResources
    {
      /**
       * @snip
       */
      public void createTextFile(CDOTransaction transaction) throws Exception
      {
        // Create a new text file at the specified path directly in the transaction.
        CDOTextResource text = transaction.createTextResource("/my/new/text");
        System.out.println("Created text file: " + text.getPath()); // Outputs: /my/new/file

        CDOClob clob = transaction.getSession().newClob("Hello, CDO Text Resource!");
        text.setContents(clob);

        // None of the above changes are visible in other views/transactions.
        // They become visible only after committing the transaction.
        transaction.commit();
      }

      /**
       * @snip
       */
      public void getContents(CDOTextResource text) throws Exception
      {
        // Get the text contents.
        CDOClob clob = text.getContents();

        // Print some information about the binary contents.
        System.out.println("Text contents ID: " + clob.getID());
        System.out.println("Text contents size: " + clob.getSize());

        // For demonstration purposes, copy the binary contents to System.out.
        clob.copyTo(new OutputStreamWriter(System.out));

        try (Reader reader = clob.getContents())
        {
          // Or do something else with the Reader...
        }
      }
    }
  }

  /**
   * Resource Sets and Their Usage
   * <p>
   * A {@link CDOView} is the CDO access context; its {@link CDOView#getResourceSet() ResourceSet} is the EMF loading
   * context used to resolve CDO resource URIs and keep the resulting objects associated with that view. A session can
   * create a ResourceSet for a view or the caller can supply one through a {@link CDOSession#openView(ResourceSet)}
   * overload. In either case, use that set
   * with its view for the view's lifetime. CDO objects are view-scoped; moving a resource to another set does not
   * transfer its identity or make its objects usable in another view. Reopen objects there by {@link CDOObject#cdoID()}.
   * <p>
   * Use {@link CDOView#getResource(String) getResource(path)} when the repository path is already known. Use
   * {@link CDOView#createResourceURI(String)} with {@link ResourceSet#getResource(URI, boolean)} when integrating
   * with code that follows normal EMF URI loading. Proxy resolution then uses the resource set's URI handling and
   * resolves CDO targets in the owning view. Ordinary EMF resources may coexist in a ResourceSet when their resource
   * factories and URI handlers are registered; those resources retain their own persistence semantics and are not
   * made persistent by being placed beside a CDO resource. See {@link Doc10_IntegratingWithEMFAndOtherFrameworks} for
   * broader EMF integration.
   * <p>
   * The examples show direct path lookup and EMF URI loading from the same view ResourceSet:
   * {@link #loadResources(CDOView) LoadResources.java}
   */
  public class ResourceSetsAndTheirUsage
  {
    /**
     * @snip
     */
    public void loadResources(CDOView view)
    {
      CDOResource byPath = view.getResource("/library/books");
      ResourceSet resourceSet = view.getResourceSet();

      URI uri = view.createResourceURI("/library/authors");
      Resource byURI = resourceSet.getResource(uri, true);
      System.out.println(byPath.getContents().size() + ": " + byURI.getURI());
    }
  }

  /**
   * Navigating Models
   * <p>
   * Use ordinary EMF navigation for a known, bounded part of the graph: {@link EObject#eContainer()},
   * {@link EObject#eContents()}, {@link EObject#eGet(EStructuralFeature)}, and generated getters all preserve normal
   * EMF semantics. Containment navigation visits already-related objects, while non-containment references can be
   * represented by proxies. Resolving a proxy may fetch a target from the repository, so a broad traversal can produce
   * many remote loads and a large view cache. Keep navigation in one view; do not compare object identity across views.
   * <p>
   * For a known persistent identifier use {@link CDOView#getObject(CDOID)}; for several identifiers,
   * {@link CDOView#getObjects(java.util.Collection)} retrieves missing objects in one round trip. For repository-wide
   * selection by EClass use {@link CDOView#queryInstances(org.eclipse.emf.ecore.EClass)}. Prefer traversal for a
   * bounded local graph, bulk lookup for a known ID set, and a query when the repository should select candidates.
   * The example receives two persistent identifiers: {@code rootID} for the traversal root and {@code relatedID} for
   * the independent bulk lookup. It demonstrates graph walking and batched ID lookup:
   * {@link #navigateAndLoad(CDOView, CDOID, CDOID) NavigateAndLoad.java}
   */
  public class NavigatingModels
  {
    /**
     * @snip
     */
    public void navigateAndLoad(CDOView view, CDOID rootID, CDOID relatedID)
    {
      CDOObject root = view.getObject(rootID);
      TreeIterator<EObject> contents = root.eAllContents();

      while (contents.hasNext())
      {
        EObject child = contents.next();

        for (EReference reference : child.eClass().getEAllReferences())
        {
          child.eGet(reference);
        }
      }

      CDOObject related = view.getObjects(Collections.singleton(relatedID)).get(relatedID);
      System.out.println(related);
    }
  }

  /**
   * Waiting For Updates
   * <p>
   * {@link CDOView} implements {@link org.eclipse.emf.cdo.util.CDOUpdatable}. Its
   * {@link org.eclipse.emf.cdo.util.CDOUpdatable#getLastUpdateTime()} reports the timestamp of the last processed
   * update; this may be inaccurate when {@link CDOSession.Options#isPassiveUpdateEnabled() passive updates} are
   * disabled. {@link org.eclipse.emf.cdo.util.CDOUpdatable#waitForUpdate(long, long)} blocks until the view has
   * processed at least the requested commit timestamp or its timeout expires, returning {@code true} only when the
   * timestamp is reached. Pass {@link org.eclipse.emf.cdo.util.CDOUpdatable#NO_TIMEOUT} to wait indefinitely.
   * <p>
   * Waiting does not enable passive updates. That is session-wide configuration and must be enabled on
   * {@link CDOSession.Options}; it affects views from that session. Head views then process remote commits according
   * to their invalidation policy. A historical view remains at its selected branch point. For event-driven UI work,
   * observe view invalidation events instead of polling. Never wait while holding {@link CDOView#sync()}: update
   * processing may need the view's synchronization lock. Also avoid blocking a UI thread or a callback needed by the
   * update dispatcher.
   * <p>
   * This example waits for an expected commit timestamp with a bounded timeout. In a two-session application, obtain
   * the timestamp from the committing session's commit result or commit-info event and pass it to the observing view:
   * {@link #waitForRemoteCommit(CDOView, long) WaitForRemoteCommit.java}
   */
  public class WaitingForUpdates
  {
    /**
     * @snip
     */
    public void waitForRemoteCommit(CDOView view, long commitTime)
    {
      boolean received = view.waitForUpdate(commitTime, 10_000);
      if (received)
      {
        System.out.println("View reached commit time " + view.getLastUpdateTime());
      }
      else
      {
        System.out.println("Timed out; last processed update was " + view.getLastUpdateTime());
      }
    }
  }

  /**
   * Querying Resources
   * <p>
   * {@link CDOView#queryResources(CDOResourceFolder, String, boolean)} searches child resource nodes in one folder;
   * pass {@code null} for the folder to search at the repository root. With {@code exactMatch == true}, the name must
   * match exactly; otherwise the name is a prefix. The synchronous method returns a {@link java.util.List}. Its
   * asynchronous counterpart {@link CDOView#queryResourcesAsync(CDOResourceFolder, String, boolean)} streams through
   * a {@link org.eclipse.net4j.util.collection.CloseableIterator}, which must be closed even when iteration stops
   * early. A resource query does not recursively search every descendant folder. Use it to select nodes before loading
   * model contents, rather than walking a large folder tree and opening every resource.
   * <p>
   * This example contrasts exact matching with prefix matching and closes the asynchronous iterator:
   * {@link #findResources(CDOView, CDOResourceFolder) FindResources.java}
   */
  public class QueryingResources
  {
    /**
     * @snip
     */
    public void findResources(CDOView view, CDOResourceFolder folder)
    {
      List<CDOResourceNode> exact = view.queryResources(folder, "books", true);
      System.out.println("Exact matches: " + exact.size());

      CloseableIterator<CDOResourceNode> matches = view.queryResourcesAsync(folder, "book", false);

      try
      {
        while (matches.hasNext())
        {
          System.out.println(matches.next().getPath());
        }
      }
      finally
      {
        matches.close();
      }
    }
  }

  /**
   * Querying Model Objects
   * <p>
   * {@link CDOView#queryInstances(org.eclipse.emf.ecore.EClass)} selects instances of an {@link org.eclipse.emf.ecore.EClass};
   * use {@link CDOView#queryInstancesAsync(org.eclipse.emf.ecore.EClass, boolean)} when results should be consumed
   * incrementally. The {@code exact} flag controls whether only that EClass is matched or compatible subtypes are
   * included. Returned EObjects belong to the querying view and must not outlive or be passed to another view as if
   * their identity were shared. Asynchronous iterators are closeable and should be used in try-with-resources.
   * <p>
   * Repository selection is useful when a matching set is spread across resources. Avoid querying an overly broad
   * base type when the result is large; constrain the EClass or use a supported custom query when the predicate needs
   * server-side expression. Transaction queries have transaction-specific behavior for local changes; see
   * {@link CDOTransaction} and do not treat them as arbitrary in-memory graph scans. Here is a synchronous and
   * asynchronous pattern, where {@code type} is the desired package classifier:
   * {@link #findInstances(CDOView, EClass) FindInstances.java}
   */
  public class QueryingModelObjects
  {
    /**
     * @snip
     */
    public void findInstances(CDOView view, EClass type)
    {
      List<EObject> all = view.queryInstances(type);
      System.out.println("Instances: " + all.size());

      CloseableIterator<EObject> matches = view.queryInstancesAsync(type, true);

      try
      {
        while (matches.hasNext())
        {
          EObject object = matches.next();
          System.out.println(CDOUtil.getCDOObject(object).cdoID());
        }
      }
      finally
      {
        matches.close();
      }
    }
  }

  /**
   * Querying Cross References
   * <p>
   * {@link CDOView#queryXRefs(CDOObject, EReference...)} and its set overload ask the repository for incoming
   * references to one or several targets. An empty reference array means all source references; supplying features
   * narrows the search. Each {@link CDOObjectReference} describes the referencing
   * source object and the source reference (and, for many-valued features, the list index); it is not an inverse
   * reference value already loaded into the target EObject. This server-assisted lookup is useful when inverse edges
   * have not been loaded. If the local graph is already loaded and bounded, normal EMF traversal may be simpler.
   * <p>
   * The asynchronous form streams results and requires closure. The example filters by one EReference:
   * {@link #findIncomingReferences(CDOView, CDOObject, EReference) FindIncomingReferences.java}
   */
  public class QueryingCrossReferences
  {
    /**
     * @snip
     */
    public void findIncomingReferences(CDOView view, CDOObject target, EReference reference)
    {
      CloseableIterator<CDOObjectReference> refs = view.queryXRefsAsync(Collections.singleton(target), reference);

      try
      {
        while (refs.hasNext())
        {
          CDOObjectReference ref = refs.next();
          System.out.println(ref.getSourceObject() + " via " + ref.getSourceReference().getName());
        }
      }
      finally
      {
        refs.close();
      }
    }
  }

  /**
   * Custom Queries
   * <p>
   * A {@link org.eclipse.emf.cdo.view.CDOQuery} is a request associated with the creating view. Create it with
   * {@link CDOView#createQuery(String, String)} or its context overload, set named arguments with
   * {@link org.eclipse.emf.cdo.view.CDOQuery#setParameter(String, Object)}, and optionally cap transfer with
   * {@link org.eclipse.emf.cdo.view.CDOQuery#setMaxResults(int)}. Query language identifiers and expressions are
   * interpreted by a matching server {@link IQueryHandler}; availability depends on server registration and any
   * required integration bundles. A client-side language constant alone does not install server support.
   * <p>
   * {@link org.eclipse.emf.cdo.view.CDOQuery#getResult(Class)} waits for the complete typed result list;
   * {@link org.eclipse.emf.cdo.view.CDOQuery#getResultAsync(Class)} returns a streaming closeable iterator.
   * {@link org.eclipse.emf.cdo.view.CDOQuery#getResultValue(Class)} is for a scalar result produced by the handler.
   * Choose the result type expected by that handler. Async iterators must be closed, including on errors or early
   * termination. Invalid parameter names or value types can fail during binding; parsing and execution failures are
   * reported by query execution. For server handler implementation and registration, see
   * {@link Doc08_SecurityQueriesAndSpecializedExtensions.QueryHandlers}.
   * <p>
   * This example uses the optional OCL integration and therefore requires its server handler to be installed. The
   * supplied context object must have a {@code purchaseOrders} reference:
   * {@link #executeOCLQuery(CDOView, EObject) ExecuteOCLQuery.java}
   */
  public class CustomQueries
  {
    /**
     * @snip
     */
    public void executeOCLQuery(CDOView view, EObject company)
    {
      CDOQuery query = view.createQuery("ocl", "self.purchaseOrders->size()", company);
      Integer count = query.getResultValue(Integer.class);
      System.out.println("Purchase orders: " + count);
    }
  }

  /**
   * Units
   * <p>
   * A {@link org.eclipse.emf.cdo.view.CDOUnit} is a managed set of disjunct object subtrees rooted at CDO objects.
   * Unlike a resource, a unit can gather related subtrees without requiring one common containment root. The view's
   * {@link CDOView#getUnitManager() unit manager} discovers and opens units, creates them, and manages units opened
   * for the view. Unit APIs expose roots and loaded element counts; they do not change the transaction boundary or make
   * arbitrary objects outside the unit free to access. Unit availability and persistence behavior depend on the
   * repository/store implementation and its capabilities, so do not assume every store supports every operation.
   * <p>
   * Units belong to their view and must be closed before that view. Closing releases retained elements and
   * subscriptions. Open units can receive change notifications according to their subscription configuration.
   * Retaining a large unit trades fewer later loads for client memory. CDO also manages resource units for
   * resource-backed loading; these are distinct from explicitly created units. Create/open operations have explicit
   * lifetimes; use on-demand opening only when a missing unit should be created. Create through a transaction and
   * supply a root object from that same view. Repository configuration must support units. This example creates and
   * closes one:
   * {@link #createAndCloseUnit(CDOTransaction, CDOObject) CreateAndCloseUnit.java}
   * <p>
   * To open an existing unit, use {@link org.eclipse.emf.cdo.view.CDOUnitManager#openUnit(EObject, boolean, org.eclipse.core.runtime.IProgressMonitor)}.
   * The inspection example checks membership using {@link org.eclipse.emf.cdo.view.CDOUnitManager#getOpenUnit(EObject)}:
   * {@link #inspectUnit(CDOView, CDOObject) InspectUnit.java}
   */
  public class Units
  {
    /**
     * @snip
     */
    public void createAndCloseUnit(CDOTransaction transaction, CDOObject root)
    {
      CDOUnit unit = transaction.getUnitManager().createUnit(root, true, null);

      try
      {
        System.out.println("Unit root: " + unit.getRoot());
        System.out.println("Loaded elements: " + unit.getElements());
      }
      finally
      {
        unit.close();
      }
    }

    /**
     * @snip
     */
    public void inspectUnit(CDOView view, CDOObject root)
    {
      CDOUnit unit = view.getUnitManager().getOpenUnit(root);
      if (unit != null)
      {
        System.out.println("Unit root: " + unit.getRoot());
        System.out.println("Loaded elements: " + unit.getElements());
        unit.close();
      }
    }
  }

  /**
   * View Events
   * <p>
   * {@link org.eclipse.emf.cdo.view.CDOViewEvent} is the common view-scoped event contract; its source is the affected
   * view. Listen on the view's {@link org.eclipse.net4j.util.event.INotifier#addListener(org.eclipse.net4j.util.event.IListener)}
   * mechanism and remove the same listener when its owner is disposed. Events may be delivered on CDO-managed threads.
   * Copy the event data needed by the UI and dispatch UI work to the UI thread; do not block update processing or hold
   * a view critical section while calling application code. Session lifecycle events remain session events. EMF
   * notifications go to adapters attached to model objects; option-change events come from {@code view.options()};
   * and {@link org.eclipse.emf.cdo.view.CDOViewEventProducer} produces events for its specialized adapters. These
   * channels have different sources and purposes. See {@link Doc09_NotificationsAndEventHandling} for general guidance.
   * <p>
   * Registering and removing a listener:
   * {@link #observeViewEvents(CDOView) ObserveViewEvents.java}
   */
  public class ViewEvents
  {
    /**
     * @snip
     */
    public void observeViewEvents(CDOView view)
    {
      IListener listener = event -> {
        if (event instanceof CDOViewEvent)
        {
          System.out.println("View event: " + event.getClass().getSimpleName());
        }
      };

      view.addListener(listener);
      view.removeListener(listener);
    }

    /**
     * View Invalidation Events
     * <p>
     * {@link org.eclipse.emf.cdo.view.CDOViewInvalidationEvent} is emitted after a view processes a remote commit.
     * It exposes the commit timestamp, dirty objects, revision deltas, and detached objects. It reports view update
     * processing; adapters receive their own EMF notifications separately. Keep listeners fast and use the timestamp
     * to correlate with {@link org.eclipse.emf.cdo.view.CDOViewAdaptersNotifiedEvent}.
     */
    public class InvalidationEvents
    {
    }

    /**
     * View Lock Events
     * <p>
     * {@link org.eclipse.emf.cdo.view.CDOViewLocksChangedEvent} reports lock changes visible to the view, including
     * sender and affected objects. Use it to refresh lock decorations; it is distinct from a lock acquisition call
     * result. Delivery follows view event threading rules.
     */
    public class LockEvents
    {
    }

    /**
     * View Permission Events
     * <p>
     * {@link org.eclipse.emf.cdo.view.CDOViewPermissionsChangedEvent} carries a map of object IDs to old/new
     * permissions when permissions visible to the view change. Applications can refresh enabled actions from this
     * event. Permission state may also depend on server security configuration.
     */
    public class PermissionEvents
    {
    }

    /**
     * View Target Events
     * <p>
     * {@link org.eclipse.emf.cdo.view.CDOViewTargetChangedEvent} reports the old and new branch point when a
     * movable view changes target. It is useful for invalidating client presentations tied to a branch point. A
     * fixed historical view does not advance merely because a commit occurs.
     */
    public class TargetEvents
    {
    }

    /**
     * Adapter Notification Completion Events
     * <p>
     * {@link org.eclipse.emf.cdo.view.CDOViewAdaptersNotifiedEvent} indicates that the view has finished notifying
     * EMF adapters for a remote commit; its timestamp correlates with the invalidation event. This is a view event
     * about completed adapter delivery, not an individual {@link Notification} and not an adapter notification itself.
     */
    public class AdaptersNotifiedEvents
    {
    }

    /**
     * Durability Events
     * <p>
     * {@link org.eclipse.emf.cdo.view.CDOViewDurabilityChangedEvent} reports changes to a view's durable-locking
     * identifier, exposing the old and new IDs. Use it to track durability state; lock operations and lock-change
     * events remain separate APIs.
     */
    public class DurabilityEvents
    {
    }

    /**
     * Provider Events
     * <p>
     * {@link org.eclipse.emf.cdo.view.CDOViewProviderChangedEvent} reports the old and new provider associated with
     * a view. It is primarily useful to infrastructure managing view-provider selection; ordinary clients rarely
     * need it. Treat it as a view event and keep its callback short.
     */
    public class ProviderEvents
    {
    }
  }

  /**
   * View Options
   * <p>
   * {@link CDOView.Options} controls view-local notification, invalidation, cache, and loading behavior. These
   * settings differ from {@link CDOSession.Options}, which configures the session and can affect all its views (for
   * example passive updates). Option changes emit {@link org.eclipse.net4j.util.options.IOptionsEvent}s from the
   * options object, not {@link org.eclipse.emf.cdo.view.CDOViewEvent}s from the view. Defaults below refer to the
   * current API implementation; session configuration and repository capabilities can affect observed behavior.
   * <p>
   * A listener can observe option changes directly:
   * {@link #watchOptions(CDOView) WatchOptions.java}
   */
  public class ViewOptions
  {
    /**
     * @snip
     */
    public void watchOptions(CDOView view)
    {
      IListener listener = event -> System.out.println(event.getClass().getSimpleName());

      view.options().addListener(listener);
      view.options().setLoadNotificationEnabled(true);
      view.options().removeListener(listener);
    }

    /**
     * Load Notification Enabled
     * <p>
     * {@link CDOView.Options#isLoadNotificationEnabled()} and {@link CDOView.Options#setLoadNotificationEnabled(boolean)}
     * control EMF adapter notifications when objects load. The implementation default is {@code false}. Enable this
     * when adapters maintain derived or presentation state that must react to loading. It is separate from remote
     * invalidation and subscriptions; changes emit {@link CDOView.LoadNotificationEvent}.
     */
    public class LoadNotificationEnabled
    {
    }

    /**
     * Detachment Notification Enabled
     * <p>
     * {@link CDOView.Options#isDetachmentNotificationEnabled()} and {@link CDOView.Options#setDetachmentNotificationEnabled(boolean)}
     * control adapter notification when remote changes detach objects. The default is {@code false}. Enable it when
     * clients need to discard references to removed objects. It does not subscribe objects; changes emit
     * {@link CDOView.DetachmentNotificationEvent}.
     */
    public class DetachmentNotificationEnabled
    {
    }

    /**
     * Invalidation Notification Enabled
     * <p>
     * {@link CDOView.Options#isInvalidationNotificationEnabled()} and {@link CDOView.Options#setInvalidationNotificationEnabled(boolean)}
     * control object adapter invalidation notifications. Default is {@code false}. This controls EMF notifications,
     * not whether the view processes remote commits or emits its view-level invalidation event. Changes emit
     * {@link CDOView.InvalidationNotificationEvent}.
     */
    public class InvalidationNotificationEnabled
    {
    }

    /**
     * Invalidation Policy
     * <p>
     * {@link CDOView.Options#getInvalidationPolicy()} and {@link CDOView.Options#setInvalidationPolicy(org.eclipse.emf.cdo.view.CDOInvalidationPolicy)}
    * select how a view treats remotely changed objects. The default is
    * {@link org.eclipse.emf.cdo.view.CDOInvalidationPolicy#RELAXED}; {@link org.eclipse.emf.cdo.view.CDOInvalidationPolicy#STRICT}
    * clears the object's revision on invalidation and reports objects that have become invalid, while RELAXED leaves
    * that handling to later access. This is view-local and requires the session to receive passive updates. Changes
    * emit {@link CDOView.InvalidationPolicyEvent}.
     */
    public class InvalidationPolicy
    {
    }

    /**
     * Change Subscription Policies
     * <p>
     * {@link CDOView.Options#getChangeSubscriptionPolicies()}, {@link CDOView.Options#addChangeSubscriptionPolicy(CDOAdapterPolicy)},
     * and {@link CDOView.Options#removeChangeSubscriptionPolicy(CDOAdapterPolicy)} select adapted objects registered
     * for remote changes. Initially the policy set is empty. Common policies are {@link CDOAdapterPolicy#NONE},
     * {@link CDOAdapterPolicy#ALL}, and {@link CDOAdapterPolicy#CDO}; an object needs a matching adapter. Session
     * passive updates must also be enabled. Changes emit {@link CDOView.ChangeSubscriptionPoliciesEvent}.
     */
    public class ChangeSubscriptionPolicies
    {
    }

    /**
     * Cache Reference Type
     * <p>
     * {@link CDOView.Options#getCacheReferenceType()} and {@link CDOView.Options#setCacheReferenceType(org.eclipse.net4j.util.ref.ReferenceType)}
     * select strong, soft, or weak references in the view cache. The implementation default is
     * {@link org.eclipse.net4j.util.ref.ReferenceType#SOFT}. Strong references retain objects and increase memory;
     * soft and weak references permit collection subject to the strong-reference policy. Passing {@code null}
     * restores soft references. The setter reports whether it changed and emits
     * {@link CDOView.CacheReferenceTypeEvent}.
     */
    public class CacheReferenceType
    {
    }

    /**
     * Strong Reference Policy
     * <p>
     * {@link CDOView.Options#getStrongReferencePolicy()} and {@link CDOView.Options#setStrongReferencePolicy(CDOAdapterPolicy)}
     * choose which adapted objects remain strongly referenced when the cache reference type is non-strong. Default
     * is {@link CDOAdapterPolicy#ALL}; policies can be narrowed to {@code NONE}, {@code CDO}, or a custom policy.
     * Balance memory use against adapter/object continuity. Changes emit {@link CDOView.StrongReferencePolicyEvent}.
     */
    public class StrongReferencePolicy
    {
    }

    /**
     * Stale Reference Policy
     * <p>
     * {@link CDOView.Options#getStaleReferencePolicy()} and {@link CDOView.Options#setStaleReferencePolicy(org.eclipse.emf.cdo.view.CDOStaleReferencePolicy)}
    * control references to objects absent at the view target. The default is
    * {@link org.eclipse.emf.cdo.view.CDOStaleReferencePolicy#PROXY}, which supplies a dynamic proxy; the
    * {@link org.eclipse.emf.cdo.view.CDOStaleReferencePolicy#EXCEPTION} policy throws an
    * {@link org.eclipse.emf.cdo.util.ObjectNotFoundException}. The older Behaviour methods are deprecated. Changes
    * emit {@link CDOView.StaleReferencePolicyEvent}.
     */
    public class StaleReferencePolicy
    {
    }

    /**
     * Revision Prefetching Policy
     * <p>
     * {@link CDOView.Options#getRevisionPrefetchingPolicy()} and
     * {@link CDOView.Options#setRevisionPrefetchingPolicy(org.eclipse.emf.cdo.view.CDORevisionPrefetchingPolicy)}
     * control fetching revisions in advance to reduce later round trips. The current implementation initializes the
     * policy to {@code NO_REVISION_PREFETCHING}; {@code DEFAULT_REVISION_PREFETCHING} is a policy constant, not a
     * guarantee of every read's batch size. Profile before tuning because prefetching trades transfer and memory for
     * fewer later requests. Changes emit {@link CDOView.RevisionPrefetchingPolicyEvent}.
     */
    public class RevisionPrefetchingPolicy
    {
    }

    /**
     * Feature Analyzer
     * <p>
     * {@link CDOView.Options#getFeatureAnalyzer()} and {@link CDOView.Options#setFeatureAnalyzer(org.eclipse.emf.cdo.view.CDOFeatureAnalyzer)}
     * install analysis of feature access for loading strategies. Default is {@link CDOFeatureAnalyzer#NOOP}. It does
     * not change model semantics. Changes emit {@link CDOView.FeatureAnalyzerEvent}.
     */
    public class FeatureAnalyzer
    {
    }

    /**
     * Clear Adapter Policy
     * <p>
     * {@link CDOView.Options#getClearAdapterPolicy()} and {@link CDOView.Options#setClearAdapterPolicy(CDOAdapterPolicy)}
     * select which adapters CDO removes when clearing object adapters. Per-view default is {@code null}, so the view
     * set's default policy applies. Changes emit {@link CDOView.ClearAdapterPolicyEvent}.
     */
    public class ClearAdapterPolicy
    {
    }

    /**
     * Lock Notification Enabled
     * <p>
     * This inherited option is managed by {@link org.eclipse.emf.cdo.common.CDOCommonView.Options#isLockNotificationEnabled()}
     * and {@link org.eclipse.emf.cdo.common.CDOCommonView.Options#setLockNotificationEnabled(boolean)}. Default is
     * {@code false}; enabling it requests protocol lock-state updates for this view and requires appropriate server
    * support. The session's {@link org.eclipse.emf.cdo.common.CDOCommonSession.Options#setLockNotificationMode(org.eclipse.emf.cdo.common.CDOCommonSession.Options.LockNotificationMode)}
    * also controls notification delivery modes. Changes emit the inherited
    * {@link org.eclipse.emf.cdo.common.CDOCommonView.Options.LockNotificationEvent}; this is an option event, not a
    * view event.
     */
    public class LockNotificationEnabled
    {
    }
  }

  /**
   * View Properties
   * <p>
   * {@link CDOView#properties()} exposes the view's {@link org.eclipse.net4j.util.properties.IPropertiesContainer}
   * for application metadata. Properties are local to that view's lifetime, are not persisted in the repository, and
   * are distinct from session properties and persistent EStructuralFeatures. Use application-owned keys and remove
   * values when they are no longer needed, especially if they retain large UI or model graphs. Property access and
   * listener behavior follow the properties-container API; do not assume cross-thread safety beyond the guarantees
   * of the concrete container. The example sets, reads, and removes a value:
   * {@link #useViewProperties(CDOView) UseViewProperties.java}
   */
  public class ViewProperties
  {
    /**
     * @snip
     */
    public void useViewProperties(CDOView view)
    {
      String key = "com.example.selection";
      view.properties().put(key, "current-page");

      Object selection = view.properties().get(key);
      System.out.println(selection);

      view.properties().remove(key);
    }
  }

  /**
   * @snip
   */
  public void openReadOnlyView(CDOSession session)
  {
    CDOView view = session.openView();

    try
    {
      System.out.println("Opened view with ID: " + view.getViewID());
    }
    finally
    {
      view.close();
    }
  }

  /**
   * @snip
   */
  public void modifyAndCommit(CDOSession session, CDOID objectID, EStructuralFeature feature, Object value) throws CommitException
  {
    CDOTransaction transaction = session.openTransaction();

    try
    {
      CDOObject object = transaction.getObject(objectID);
      if (!object.eClass().getEAllStructuralFeatures().contains(feature))
      {
        throw new IllegalArgumentException("Feature does not belong to the object's EClass");
      }

      object.eSet(feature, value);
      transaction.commit();
    }
    catch (CommitException | RuntimeException ex)
    {
      transaction.rollback();
      throw ex;
    }
    finally
    {
      transaction.close();
    }
  }
}
