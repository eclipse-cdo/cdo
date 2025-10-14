/*
 * Copyright (c) 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.doc.programmers.client;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.util.ConcurrentAccessException;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.concurrent.CriticalSection;
import org.eclipse.net4j.util.concurrent.CriticalSection.LockedCriticalSection;
import org.eclipse.net4j.util.concurrent.DelegableReentrantLock;
import org.eclipse.net4j.util.concurrent.DelegableReentrantLock.DelegateDetector;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;

import org.eclipse.swt.widgets.Display;

import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Working with Views
 * <p>
 * This chapter covers view management, resource handling, querying, transactions, and related options in CDO client
 * applications. Views are central to accessing and interacting with model data in a CDO repository. Understanding how
 * to use views effectively is key to building responsive and scalable applications.
 *
 * @author Eike Stepper
 */
public class Doc04_WorkingWithViews
{
  /**
   * Understanding Views and Their Types
   * <p>
   * CDO provides several types of views, including read-only views and transactional views. Read-only views allow safe
   * navigation of repository data without risk of modification, while transactional views enable changes and commits.
   * This section explains the differences, use cases, and lifecycle of each view type.
   */
  public class UnderstandingViewsAndTheirTypes
  {
  }

  /**
   * Opening and Closing Views
   * <p>
   * Learn how to open views to access repository data and close them to release resources. Proper management of view
   * lifecycles helps prevent memory leaks and ensures efficient resource usage.
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
   * Thread safety of CDO views is absolutely essential, even in single-threaded applications. The reason is that CDO
   * views are accessed by background threads for tasks such as asynchronous updates, notifications, and
   * event handling. If a view were not thread-safe, these background operations could lead to data corruption,
   * inconsistent states, and unpredictable behavior in the application. By ensuring that views are thread-safe,
   * CDO allows developers to build robust applications that can safely handle concurrent operations without risking
   * data integrity.
   * <p>
   * This section discusses best practices for managing concurrent access,
   * synchronizing operations, and avoiding race conditions when working with views in multi-threaded environments.
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
     * {@link #crititicalSectionWithCallable(CDOView, CDOID, CDOID, CDOID) CrititicalSectionWithCallable.java}
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
     * By default the critical section of a view uses the monitor lock of that view to synchronize. If you need a different locking
     * strategy you can override this by calling {@link CDOUtil#setNextViewLock(Lock)} before opening the view.
     * <p>
     * Here's an example of setting a custom lock for the next view to be opened:
     * {@link #customLockForNextView(CDOSession) CustomLockForNextView.java}
     * <p>
     * The following chapter describes how to use a special kind of lock, a {@link DelegableReentrantLock}, that
     * allows to delegate the lock ownership to a different thread.
     */
    public class UsingCriticalSections
    {
      /**
       * @snip
       */
      @SuppressWarnings("unused")
      public void crititicalSectionWithCallable(CDOView view, CDOID id1, CDOID id2, CDOID id3) throws Exception
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

        CDOView view = session.openView();
        CriticalSection sync = view.sync();

        if (sync instanceof LockedCriticalSection)
        {
          Lock lock = ((LockedCriticalSection)sync).getLock();
          assert lock == customLock;
        }
        else
        {
          throw new IllegalStateException();
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
     */
    public class TheRootResource
    {
    }

    /**
     * Resource Folders
     * <p>
     * Resource folders organize model resources into logical groups. Learn how to create, navigate, and manage folders
     * to structure your repository effectively.
     */
    public class ResourceFolders
    {
    }

    /**
     * Model Resources
     * <p>
     * Model resources store EMF model data in the repository. This section explains how to load, save, and query model
     * resources, and how they relate to EMF ResourceSet.
     */
    public class ModelResources
    {
    }

    /**
     * Binary Resources
     * <p>
     * Binary resources allow storage of non-model data, such as images or files, within the CDO repository. Learn how
     * to manage binary resources and integrate them with your application.
     */
    public class BinaryResources
    {
    }

    /**
     * Text Resources
     * <p>
     * Text resources store textual data, such as configuration files or documentation, in the repository. This section
     * covers reading, writing, and organizing text resources.
     */
    public class TextResources
    {
    }
  }

  /**
   * Resource Sets and Their Usage
   * <p>
   * Resource sets are collections of resources managed together. Learn how to use EMF ResourceSet with CDO, manage
   * resource lifecycles, and optimize performance for large models.
   */
  public class ResourceSetsAndTheirUsage
  {
  }

  /**
   * Navigating Models
   * <p>
   * This section provides techniques for traversing and querying model objects within views, including use of EMF APIs
   * and CDO-specific features for efficient navigation.
   */
  public class NavigatingModels
  {
  }

  /**
   * Waiting For Updates
   * <p>
   * Learn how to synchronize your client with repository changes, block for updates, and react to notifications to keep
   * your application state current.
   */
  public class WaitingForUpdates
  {
  }

  /**
   * Querying Resources
   * <p>
   * CDO supports querying resources using various criteria. This section explains how to construct and execute queries
   * to locate resources and model objects efficiently.
   */
  public class QueryingResources
  {
  }

  /**
   * Querying Model Objects
   * <p>
   * Learn how to query model objects using CDO's query APIs, including support for OCL, custom queries, and
   * cross-references.
   */
  public class QueryingModelObjects
  {
  }

  /**
   * Querying Cross References
   * <p>
   * Cross references allow navigation between related model objects. This section covers techniques for querying and
   * resolving cross references in CDO models.
   */
  public class QueryingCrossReferences
  {
  }

  /**
   * Custom Queries
   * <p>
   * Extend CDO's querying capabilities with custom query implementations. Learn how to define, register, and execute
   * custom queries for advanced use cases.
   */
  public class CustomQueries
  {
  }

  /**
   * Units
   * <p>
   * Units are disjunct subtrees of model objects that can be managed independently. This section explains how to
   * create, open, and close units, and how they can improve performance and consistency in your application.
   */
  public class Units
  {
  }

  /**
   * View Events
   * <p>
   * Views emit events for changes, updates, and errors. This section explains how to listen for and handle view events
   * to build responsive applications.
   */
  public class ViewEvents
  {
  }

  /**
   * View Options
   * <p>
   * Configure view options to customize behavior, such as passive updates, notification handling, and more.
   */
  public class ViewOptions
  {
  }

  /**
   * View Properties
   * <p>
   * Access and modify view properties to store custom metadata and configuration values.
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
    org.eclipse.emf.cdo.view.CDOView view = session.openView();
    System.out.println("Opened view with ID: " + view.getViewID());
    view.close();
  }

  /**
   * Example: Open a transactional view and commit changes
   * @snip
   * Opens a CDOTransaction, modifies a model object, and commits the transaction.
   * @param session the CDOSession
   * @param object the CDOObject to modify
   */
  public void modifyAndCommit(CDOSession session, CDOObject object)
  {
    CDOTransaction transaction = session.openTransaction();

    try
    {
      object.eSet(object.eClass().getEStructuralFeature(0), "exampleValue");
      transaction.commit();
    }
    catch (ConcurrentAccessException e)
    {
      System.err.println("Commit failed: " + e.getMessage());
      transaction.rollback();
    }
    catch (CommitException e)
    {
      System.err.println("Commit failed: " + e.getMessage());
      transaction.rollback();
    }
    finally
    {
      transaction.close();
    }
  }
}
