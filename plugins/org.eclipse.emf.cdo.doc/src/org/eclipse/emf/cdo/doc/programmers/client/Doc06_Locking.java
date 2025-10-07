package org.eclipse.emf.cdo.doc.programmers.client;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;

import java.util.Collections;

/**
 * Locking
 * <p>
 * This chapter describes the locking mechanisms available in CDO client applications, including lock types and
 * strategies for managing concurrent access to model data. Locking is essential for ensuring data integrity and
 * consistency in collaborative environments.
 *
 * @author Eike Stepper
 */
public class Doc06_Locking
{
  /**
   * Lock Types
   * <p>
   * CDO supports several types of locks to control access to model objects. This section explains the differences
   * between read locks, write locks, and write option locks, and how to use them to prevent conflicts and ensure safe
   * modifications.
   */
  public class LockTypes
  {
    /**
     * Read Locks
     * <p>
     * Read locks allow multiple clients to access model objects concurrently for reading, while preventing
     * modifications. Learn how to acquire and release read locks to enable safe, concurrent navigation of repository
     * data.
     */
    public class ReadLocks
    {
      /**
       * @snip
       */
      public void acquireReadLock(CDOTransaction transaction, CDOObject object) throws InterruptedException
      {
        transaction.lockObjects(Collections.singleton(object), LockType.READ, 1000L);
        System.out.println("Read lock acquired for object: " + object);

        transaction.unlockObjects(Collections.singleton(object), LockType.READ);
        System.out.println("Read lock released for object: " + object);
      }
    }

    /**
     * Write Locks
     * <p>
     * Write locks provide exclusive access to model objects for modification. This section covers how to obtain write
     * locks, handle lock contention, and release locks after changes are committed.
     */
    public class WriteLocks
    {
      /**
       * @snip
       */
      public void acquireWriteLock(CDOTransaction transaction, CDOObject object) throws InterruptedException
      {
        transaction.lockObjects(Collections.singleton(object), LockType.WRITE, 1000L);
        System.out.println("Write lock acquired for object: " + object);

        transaction.unlockObjects(Collections.singleton(object), LockType.WRITE);
        System.out.println("Write lock released for object: " + object);
      }
    }

    /**
     * Write Option Locks
     * <p>
     * Write option locks are a specialized form of locking that allow clients to prepare for future write operations.
     * Learn how to use write option locks to optimize transaction workflows and reduce lock contention.
     */
    public class WriteOptionLocks
    {
      /**
       * @snip
       */
      public void acquireWriteOptionLock(CDOTransaction transaction, CDOObject object) throws InterruptedException
      {
        transaction.lockObjects(Collections.singleton(object), LockType.OPTION, 1000L);
        System.out.println("Write option lock acquired for object: " + object);

        transaction.unlockObjects(Collections.singleton(object), LockType.OPTION);
        System.out.println("Write option lock released for object: " + object);
      }
    }
  }

  /**
   * Optimistic Locking
   * <p>
   * Optimistic locking assumes that conflicts are rare and allows clients to proceed with changes without acquiring
   * locks up front. This section explains how optimistic locking works in CDO, how to detect and resolve conflicts at
   * commit time, and best practices for using optimistic strategies.
   */
  public class OptimisticLocking
  {
  }

  /**
   * Pessimistic Locking
   * <p>
   * Pessimistic locking requires clients to acquire locks before making changes, preventing other clients from
   * modifying the same objects concurrently. Learn how to implement pessimistic locking in CDO, manage lock lifecycles,
   * and avoid deadlocks.
   */
  public class PessimisticLocking
  {
  }

  /**
   * Durable Locking
   * <p>
   * Durable locks persist across client disconnects and server restarts, ensuring long-term protection of model
   * objects. This section covers how to create, manage, and release durable locks, and when to use them in your
   * application.
   */
  public class DurableLocking
  {
  }
}
