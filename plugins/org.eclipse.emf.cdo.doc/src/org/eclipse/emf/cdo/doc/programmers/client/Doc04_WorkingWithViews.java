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
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.concurrent.CriticalSection;
import org.eclipse.net4j.util.concurrent.CriticalSection.LockedCriticalSection;
import org.eclipse.net4j.util.concurrent.DelegableReentrantLock;
import org.eclipse.net4j.util.concurrent.DelegableReentrantLock.DelegateDetector;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;

import org.eclipse.swt.widgets.Display;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
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
   * A view is associated with a {@code ResourceSet}; resources loaded through it contain objects owned by that view.
   * Keep the resource set with the view that created it and close the view when the application is done. Do not move
   * CDO resources or their objects to another resource set and assume their view context changes with them. A
   * {@code ResourceSet} can also contain ordinary EMF resources, but their persistence and lifecycle remain the
   * responsibility of their own resource implementation. See {@link Doc10_IntegratingWithEMFAndOtherFrameworks} for
   * URI-based loading and resource factories.
   */
  public class ResourceSetsAndTheirUsage
  {
  }

  /**
   * Navigating Models
   * <p>
   * Use ordinary EMF navigation for a known, bounded part of the graph. References may be proxies, so reading one can
   * trigger additional loading. A broad walk can therefore cause many round trips and retain many objects. When the
   * desired result is defined by a type or predicate over repository contents, prefer a repository query and load
   * only the returned objects. Choose deliberately between traversal and query; neither is always cheaper.
   */
  public class NavigatingModels
  {
  }

  /**
   * Waiting For Updates
   * <p>
   * Passive updates advance or invalidate a live head view according to its session update mode. An application that
   * needs to wait for such updates can use the view/session update-waiting API; a fixed-time historical view does not
   * move forward. Waiting should happen outside a view critical section, because update processing may need that same
   * synchronization boundary. Use view or session events for notification rather than polling model values.
   */
  public class WaitingForUpdates
  {
  }

  /**
   * Querying Resources
   * <p>
   * Queries are created from the view and executed by the repository. Use a query when selecting candidate resources
   * is cheaper than loading and traversing a broad resource tree; result limits and asynchronous consumption are
   * covered in {@link Doc11_AdvancedTopics.LargeScaleAccess}.
   */
  public class QueryingResources
  {
  }

  /**
   * Querying Model Objects
   * <p>
   * A query runs against the view's repository coordinate and returns objects associated with that view. Select a
   * language supported by both the client and server, bind parameters instead of building expressions from untrusted
   * strings, and close asynchronous result iterators. For a transaction, query options determine whether local dirty
   * state participates; this does not turn a repository query into arbitrary in-memory graph evaluation. OCL is an
   * optional integration, not a universally available language.
   */
  public class QueryingModelObjects
  {
  }

  /**
   * Querying Cross References
   * <p>
   * {@code queryXRefs} asks the repository for objects whose selected source references point at one or more target
   * objects. This is useful when reverse navigation is not already loaded locally. Restrict the source reference set
   * where possible, and use the asynchronous iterator for large results so the whole result list need not be retained
   * at once. The iterator is a resource and must be closed.
   */
  public class QueryingCrossReferences
  {
  }

  /**
   * Custom Queries
   * <p>
   * A custom query language is only usable when the server has a matching {@link IQueryHandler server query handler}
   * registered and the client supplies the language identifier and parameters that handler expects. It is not enough
   * to install a parser on the client. Handler registration and result delivery are covered in
   * {@link Doc08_SecurityQueriesAndSpecializedExtensions.QueryHandlers}.
   */
  public class CustomQueries
  {
  }

  /**
   * Units
   * <p>
   * A unit is a repository-defined subtree that the view can treat as a bounded working set. Units can help an
   * application load and manage a coherent part of a large model, but they do not make unrelated object access free
   * or replace transaction boundaries. Unit membership and lifecycle must follow the repository's unit-manager
   * support; close units and views according to the APIs that opened them.
   */
  public class Units
  {
  }

  /**
   * View Events
   * <p>
   * View events report lifecycle and target/update changes for that view. Register listeners only for the period in
   * which the application needs them and remove them when the owning component is disposed. Event callbacks may run
   * on a CDO-managed thread; capture the data needed by the UI and dispatch UI work to its thread instead of blocking
   * the callback. See {@link Doc09_NotificationsAndEventHandling} for event categories and threading guidance.
   */
  public class ViewEvents
  {
  }

  /**
   * View Options
   * <p>
   * View options control view-local behavior such as invalidation, object-cache references, adapters, and locking or
   * notification details. Configure options before relying on the resulting policy, and distinguish them from
   * session options that apply to all views opened by that session. A view option cannot enable a repository feature
   * that the server/store does not provide.
   */
  public class ViewOptions
  {
  }

  /**
   * View Properties
   * <p>
   * View properties are an application-owned key/value area associated with a view. They are useful for associating
   * client-side metadata with that view; they are not persisted model features, are not shared with the repository,
   * and should not be used to smuggle objects between views. Remove values that retain large application objects
   * when they are no longer needed.
   */
  public class ViewProperties
  {
  }

  /**
   * Example: Open a read-only view from a session
   * @snip
   * Opens a CDOView and prints its ID.
   * @param session the CDOSession
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
   * Example: Open a transaction, change a feature on a persistent object, and commit it.
   * @snip
   * @param session the CDOSession
   * @param objectID the persistent ID of the object to modify
   * @param feature a feature belonging to the object's EClass
   * @param value a value valid for the feature's type and multiplicity
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
