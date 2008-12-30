/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util.tests.defs;

import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleException;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.net4jutildefs.Def;
import org.eclipse.net4j.util.net4jutildefs.impl.DefImpl;
import org.eclipse.net4j.util.tests.AbstractOMTest;

import org.eclipse.emf.common.notify.Notifier;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Eike Stepper
 */
public class TestDefTest extends AbstractOMTest
{
  private Def def;

  public TestDefTest()
  {
  }

  @Override
  public void doSetUp()
  {
    def = new BlockingTokenDef();
  }

  public void testGetInstanceReturnsSameInstance()
  {
    Object thisInstance = def.getInstance();
    Object thatInstance = def.getInstance();
    assertTrue(thisInstance == thatInstance);
  }

  public void testInstanceCreatedIsActivated()
  {
    Object thisInstance = def.getInstance();
    assertTrue(LifecycleUtil.isActive(thisInstance));
  }

  public void testNewInstanceIsCreatedIfDefWasTouchedAfterwards() throws Exception
  {
    Def def = new DefImpl()
    {
      @Override
      public boolean isTouched()
      {
        return true;
      }

      @Override
      protected Object createInstance()
      {
        return new String("");
      }
    };

    Object thisInstance = def.getInstance();
    Object thatInstance = def.getInstance();
    assertTrue(thatInstance != thisInstance);
  }

  public void testSameInstanceIfDefWasntTouched() throws Exception
  {
    Def def = new DefImpl()
    {
      @Override
      public boolean isTouched()
      {
        return false;
      }

      @Override
      protected Object createInstance()
      {
        return new String("");
      }
    };

    Object thisInstance = def.getInstance();
    Object thatInstance = def.getInstance();
    assertTrue(thatInstance == thisInstance);
  }

  public void testSetAttributeTouches()
  {
    TestDef def = createTestDef();
    assertTrue(!def.isTouched());
    def.setAttribute("aValue");
    assertTrue(def.isTouched());
    def.getInstance(); // clears touched
    assertTrue(!def.isTouched());
  }

  public void testAddReferenceTouches()
  {
    TestDef def = createTestDef();
    assertTrue(!def.isTouched());
    def.getReferences().add(createTestDef());
    assertTrue(def.isTouched());
  }

  public void testSetInReferencedDefTouchesReferenchingDef()
  {
    TestDef def = createTestDef();
    assertTrue(!def.isTouched());

    TestDef referencedDef = createTestDef();
    def.getReferences().add(referencedDef);
    assertTrue(!referencedDef.isTouched());

    referencedDef.setAttribute("newValue");
    assertTrue(referencedDef.isTouched());
    assertTrue(def.isTouched());
  }

  public void testNewInstanceIsCreatedIfCurrentIsDeactivated() throws Exception
  {
    BlockingToken thisInstance = (BlockingToken)def.getInstance();
    LifecycleUtil.deactivate(thisInstance);

    // Wait until instance gets deactivated
    thisInstance.waitForDeactivation();
    BlockingToken thatInstance = (BlockingToken)def.getInstance();
    assertTrue(thatInstance != thisInstance);
  }

  public void testNewInstanceIsCreatedIfCurrentIsUnset() throws Exception
  {
    BlockingToken thisInstance = (BlockingToken)def.getInstance();
    def.unsetInstance();

    // Wait until instance gets deactivated
    thisInstance.waitForDeactivation();
    BlockingToken thatInstance = (BlockingToken)def.getInstance();
    assertTrue(thatInstance != thisInstance);
  }

  private TestDef createTestDef()
  {
    return DefsFactory.eINSTANCE.createTestDef();
  }

  /**
   * @author Eike Stepper
   */
  private static final class BlockingTokenDef extends DefImpl
  {
    public BlockingTokenDef()
    {
    }

    @Override
    protected Object createInstance()
    {
      return new BlockingToken();
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class BlockingToken extends Lifecycle
  {
    private static final long WAIT_TIMEOUT = 1000l;

    private volatile boolean isActive = false;

    private ReentrantLock reentrantLock = new ReentrantLock();

    private Condition lockReleaseCondition = reentrantLock.newCondition();

    public BlockingToken()
    {
    }

    /**
     * (Blocking) Wait for deactivation. The deactivation is triggered in a separate thread. This method allows you
     * blocking wait for deactivation
     * 
     * @throws InterruptedException
     *           the interrupted exception
     * @see Notifier#fireEvent
     */
    public void waitForDeactivation() throws InterruptedException
    {
      long startTime = System.currentTimeMillis();
      reentrantLock.lock();
      try
      {
        while (isActive)
        {
          if (isTimeout(startTime))
          {
            throw new IllegalStateException("was not deactivated while waiting for '" + WAIT_TIMEOUT + "'!");
          }

          reentrantLock.wait(WAIT_TIMEOUT);
        }
      }
      finally
      {
        reentrantLock.unlock();
      }
    }

    @Override
    protected void doActivate() throws LifecycleException
    {
      try
      {
        reentrantLock.lock();
        isActive = true;
      }
      finally
      {
        reentrantLock.unlock();
      }
    }

    @Override
    protected void doDeactivate()
    {
      try
      {
        reentrantLock.lock();
        isActive = false;
        lockReleaseCondition.signal();
      }
      finally
      {
        reentrantLock.unlock();
      }
    }

    private boolean isTimeout(long startTime)
    {
      return System.currentTimeMillis() - startTime > WAIT_TIMEOUT;
    }
  }
}
