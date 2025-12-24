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
package org.eclipse.net4j.util.concurrent;

import org.eclipse.net4j.util.WrappedException;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

/**
 * A critical section is a block of code that must be executed by only one thread at a time. This interface provides methods
 * to run code blocks or callables within such a critical section. Implementations of this interface are expected to
 * provide the necessary synchronization mechanisms to ensure thread safety.
 * <p>
 * Example usage:
 * <pre>
 * CriticalSection criticalSection = ...; // Obtain an implementation
 * criticalSection.run(() -> {
 *   // Code to be executed in the critical section
 *   });
 *
 * int result = criticalSection.supply(() -> {
 *   // Code that returns a value
 *   return 42;
 * });
 *
 * boolean flag = criticalSection.supply(() -> {
 *   // Code that returns a boolean
 *   return true;
 * });
 *
 * double value = criticalSection.supply(() -> {
 *   // Code that returns a double
 *   return 3.14;
 * });
 *
 * long count = criticalSection.supply(() -> {
 *   // Code that returns a long
 *   return 100L;
 * });
 * </pre>
 *
 * @author Eike Stepper
 * @since 3.29
 */
public interface CriticalSection
{
  public static final CriticalSection UNSYNCHRONIZED = new UnsynchronizedCriticalSection();

  public default void run(Runnable runnable)
  {
    try
    {
      call(() -> {
        runnable.run();
        return null;
      });
    }
    catch (RuntimeException | Error ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  public default <V> V supply(Supplier<V> supplier)
  {
    try
    {
      return call(() -> supplier.get());
    }
    catch (Exception ex)
    {
      // Make the compiler happy, even though Supplier can not throw checked exceptions.
      throw WrappedException.wrap(ex);
    }
  }

  public default boolean supply(BooleanSupplier supplier)
  {
    try
    {
      return call(() -> supplier.getAsBoolean());
    }
    catch (Exception ex)
    {
      // Make the compiler happy, even though BooleanSupplier can not throw checked exceptions.
      throw WrappedException.wrap(ex);
    }
  }

  public default int supply(IntSupplier supplier)
  {
    try
    {
      return call(() -> supplier.getAsInt());
    }
    catch (Exception ex)
    {
      // Make the compiler happy, even though IntSupplier can not throw checked exceptions.
      throw WrappedException.wrap(ex);
    }
  }

  public default long supply(LongSupplier supplier)
  {
    try
    {
      return call(() -> supplier.getAsLong());
    }
    catch (Exception ex)
    {
      // Make the compiler happy, even though LongSupplier can not throw checked exceptions.
      throw WrappedException.wrap(ex);
    }
  }

  public default double supply(DoubleSupplier supplier)
  {
    try
    {
      return call(() -> supplier.getAsDouble());
    }
    catch (Exception ex)
    {
      // Make the compiler happy, even though DoubleSupplier can not throw checked exceptions.
      throw WrappedException.wrap(ex);
    }
  }

  public default <V, E extends Throwable> V call(Class<E> exceptionType, Callable<V> callable) throws E
  {
    try
    {
      return call(callable);
    }
    catch (Exception ex)
    {
      if (exceptionType.isInstance(ex))
      {
        throw exceptionType.cast(ex);
      }

      throw WrappedException.wrap(ex);
    }
  }

  public <V> V call(Callable<V> callable) throws Exception;

  public Condition newCondition();

  /**
   * A {@link CriticalSection} implementation that uses a given {@link Lock} to synchronize access to the critical
   * section.
   *
   * @author Eike Stepper
   */
  public static class LockedCriticalSection implements CriticalSection
  {
    private final Lock lock;

    public LockedCriticalSection(Lock lock)
    {
      this.lock = Objects.requireNonNull(lock);
    }

    public final Lock getLock()
    {
      return lock;
    }

    @Override
    public <V> V call(Callable<V> callable) throws Exception
    {
      lock.lock();

      try
      {
        return callable.call();
      }
      finally
      {
        lock.unlock();
      }
    }

    @Override
    public void run(Runnable runnable)
    {
      lock.lock();

      try
      {
        runnable.run();
      }
      finally
      {
        lock.unlock();
      }
    }

    @Override
    public <V> V supply(Supplier<V> supplier)
    {
      lock.lock();

      try
      {
        return supplier.get();
      }
      finally
      {
        lock.unlock();
      }
    }

    @Override
    public boolean supply(BooleanSupplier supplier)
    {
      lock.lock();

      try
      {
        return supplier.getAsBoolean();
      }
      finally
      {
        lock.unlock();
      }
    }

    @Override
    public int supply(IntSupplier supplier)
    {
      lock.lock();

      try
      {
        return supplier.getAsInt();
      }
      finally
      {
        lock.unlock();
      }
    }

    @Override
    public long supply(LongSupplier supplier)
    {
      lock.lock();

      try
      {
        return supplier.getAsLong();
      }
      finally
      {
        lock.unlock();
      }
    }

    @Override
    public double supply(DoubleSupplier supplier)
    {
      lock.lock();

      try
      {
        return supplier.getAsDouble();
      }
      finally
      {
        lock.unlock();
      }
    }

    @Override
    public Condition newCondition()
    {
      return lock.newCondition();
    }
  }

  /**
   * A {@link CriticalSection} implementation that uses a given mutex object to synchronize access to the critical
   * section.
   *
   * @author Eike Stepper
   */
  public static class SynchronizedCriticalSection implements CriticalSection
  {
    private final Object mutex;

    public SynchronizedCriticalSection(Object mutex)
    {
      this.mutex = Objects.requireNonNull(mutex);
    }

    public final Object getMutex()
    {
      return mutex;
    }

    @Override
    public <V> V call(Callable<V> callable) throws Exception
    {
      synchronized (mutex)
      {
        return callable.call();
      }
    }

    @Override
    public void run(Runnable runnable)
    {
      synchronized (mutex)
      {
        runnable.run();
      }
    }

    @Override
    public <V> V supply(Supplier<V> supplier)
    {
      synchronized (mutex)
      {
        return supplier.get();
      }
    }

    @Override
    public boolean supply(BooleanSupplier supplier)
    {
      synchronized (mutex)
      {
        return supplier.getAsBoolean();
      }
    }

    @Override
    public int supply(IntSupplier supplier)
    {
      synchronized (mutex)
      {
        return supplier.getAsInt();
      }
    }

    @Override
    public long supply(LongSupplier supplier)
    {
      synchronized (mutex)
      {
        return supplier.getAsLong();
      }
    }

    @Override
    public double supply(DoubleSupplier supplier)
    {
      synchronized (mutex)
      {
        return supplier.getAsDouble();
      }
    }

    @Override
    public Condition newCondition()
    {
      return new Condition()
      {
        @Override
        public void await() throws InterruptedException
        {
          mutex.wait();
        }

        @Override
        public void awaitUninterruptibly()
        {
          throw new UnsupportedOperationException();
        }

        @Override
        public long awaitNanos(long nanosTimeout) throws InterruptedException
        {
          throw new UnsupportedOperationException();
        }

        @Override
        public boolean await(long time, TimeUnit unit) throws InterruptedException
        {
          mutex.wait(unit.toMillis(time));
          return false;
        }

        @Override
        public boolean awaitUntil(Date deadline) throws InterruptedException
        {
          mutex.wait(deadline.getTime() - System.currentTimeMillis());
          return false;
        }

        @Override
        public void signal()
        {
          mutex.notify();
        }

        @Override
        public void signalAll()
        {
          mutex.notifyAll();
        }
      };
    }
  }

  /**
   * A {@link CriticalSection} implementation that does not perform any synchronization. This implementation is useful
   * for scenarios where synchronization is not required, such as single-threaded applications or testing environments.
   *
   * @author Eike Stepper
   */
  public static class UnsynchronizedCriticalSection implements CriticalSection
  {
    public UnsynchronizedCriticalSection()
    {
    }

    @Override
    public <V> V call(Callable<V> callable) throws Exception
    {
      return callable.call();
    }

    @Override
    public void run(Runnable runnable)
    {
      runnable.run();
    }

    @Override
    public <V> V supply(Supplier<V> supplier)
    {
      return supplier.get();
    }

    @Override
    public boolean supply(BooleanSupplier supplier)
    {
      return supplier.getAsBoolean();
    }

    @Override
    public int supply(IntSupplier supplier)
    {
      return supplier.getAsInt();
    }

    @Override
    public long supply(LongSupplier supplier)
    {
      return supplier.getAsLong();
    }

    @Override
    public double supply(DoubleSupplier supplier)
    {
      return supplier.getAsDouble();
    }

    @Override
    public Condition newCondition()
    {
      return new Condition()
      {
        @Override
        public void await() throws InterruptedException
        {
          // Do nothing.
        }

        @Override
        public void awaitUninterruptibly()
        {
          // Do nothing.
        }

        @Override
        public long awaitNanos(long nanosTimeout) throws InterruptedException
        {
          // Do nothing.
          return 0L;
        }

        @Override
        public boolean await(long time, TimeUnit unit) throws InterruptedException
        {
          return false;
        }

        @Override
        public boolean awaitUntil(Date deadline) throws InterruptedException
        {
          return false;
        }

        @Override
        public void signal()
        {
          // Do nothing.
        }

        @Override
        public void signalAll()
        {
          // Do nothing.
        }
      };
    }
  }
}
