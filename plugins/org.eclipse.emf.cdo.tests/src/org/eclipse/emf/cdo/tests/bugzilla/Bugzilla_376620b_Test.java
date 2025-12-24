/*
 * Copyright (c) 2013 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Christian W. Damus (CEA) - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.CDOAdapter;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model2.NotUnsettable;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOAdapterPolicy;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Bug 376620: Tests that we get the appropriate notifications for primitive-valued attributes in view invalidations.
 *
 * @author Christian W. Damus (CEA)
 */
public class Bugzilla_376620b_Test extends AbstractCDOTest
{
  public void testBoolean() throws Exception
  {
    NotUnsettable object = createAndCommitTestObject();
    TestAdapter adapter = poke(object, getModel2Package().getNotUnsettable_NotUnsettableBoolean(), true);
    adapter.awaitAssertion();
  }

  public void testByte() throws Exception
  {
    NotUnsettable object = createAndCommitTestObject();
    TestAdapter adapter = poke(object, getModel2Package().getNotUnsettable_NotUnsettableByte(), (byte)42);
    adapter.awaitAssertion();
  }

  public void testChar() throws Exception
  {
    NotUnsettable object = createAndCommitTestObject();
    TestAdapter adapter = poke(object, getModel2Package().getNotUnsettable_NotUnsettableChar(), 'a');
    adapter.awaitAssertion();
  }

  public void testShort() throws Exception
  {
    NotUnsettable object = createAndCommitTestObject();
    TestAdapter adapter = poke(object, getModel2Package().getNotUnsettable_NotUnsettableShort(), (short)42);
    adapter.awaitAssertion();
  }

  public void testInt() throws Exception
  {
    NotUnsettable object = createAndCommitTestObject();
    TestAdapter adapter = poke(object, getModel2Package().getNotUnsettable_NotUnsettableInt(), 42);
    adapter.awaitAssertion();
  }

  public void testLong() throws Exception
  {
    NotUnsettable object = createAndCommitTestObject();
    TestAdapter adapter = poke(object, getModel2Package().getNotUnsettable_NotUnsettableLong(), 42L);
    adapter.awaitAssertion();
  }

  public void testFloat() throws Exception
  {
    NotUnsettable object = createAndCommitTestObject();
    TestAdapter adapter = poke(object, getModel2Package().getNotUnsettable_NotUnsettableFloat(), 42.0f);
    adapter.awaitAssertion();
  }

  public void testDouble() throws Exception
  {
    NotUnsettable object = createAndCommitTestObject();
    TestAdapter adapter = poke(object, getModel2Package().getNotUnsettable_NotUnsettableDouble(), 42.0);
    adapter.awaitAssertion();
  }

  //
  // Test framework
  //

  protected NotUnsettable createAndCommitTestObject() throws CommitException
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    transaction.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.CDO);

    Resource res = transaction.createResource(getResourcePath("test"));
    NotUnsettable result = getModel2Factory().createNotUnsettable();
    res.getContents().add(result);

    transaction.commit();
    return result;
  }

  protected TestAdapter poke(EObject object, EAttribute attribute, Object value) throws CommitException
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    transaction.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.CDO);

    final Object oldValue = object.eGet(attribute);
    final TestAdapter result = TestAdapter.assertFuture(object, attribute, oldValue, value);

    EObject other = transaction.getObject(object);

    other.eSet(attribute, value);
    transaction.commit();
    return result;
  }

  /**
   * @author Christian W. Damus (CEA)
   */
  private static class TestAdapter extends AdapterImpl implements CDOAdapter
  {
    private final Semaphore semaphore = new Semaphore(0);

    private final EAttribute attribute;

    private final Object expectedOldValue;

    private final Object expectedNewValue;

    private volatile boolean received;

    public TestAdapter(EAttribute attribute, Object expectedOldValue, Object expectedNewValue)
    {
      this.attribute = attribute;
      this.expectedOldValue = expectedOldValue;
      this.expectedNewValue = expectedNewValue;
    }

    public static TestAdapter assertFuture(EObject notifier, EAttribute attribute, Object oldValue, Object newValue)
    {
      TestAdapter result = new TestAdapter(attribute, oldValue, newValue);
      notifier.eAdapters().add(result);
      return result;
    }

    public void awaitAssertion()
    {
      try
      {
        semaphore.tryAcquire(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
      }
      catch (InterruptedException e)
      {
        fail("Interrupted while awaiting assertion.");
      }

      assertEquals(true, received);
    }

    @Override
    public void notifyChanged(Notification msg)
    {
      if (msg.getFeature() == attribute)
      {
        try
        {
          Class<?> type = attribute.getEType().getInstanceClass();
          if (type == boolean.class)
          {
            update(asBoolean(expectedOldValue) == msg.getOldBooleanValue() //
                && asBoolean(expectedNewValue) == msg.getNewBooleanValue());
          }
          else if (type == byte.class)
          {
            update(asByte(expectedOldValue) == msg.getOldByteValue() //
                && asByte(expectedNewValue) == msg.getNewByteValue());
          }
          else if (type == char.class)
          {
            update(asChar(expectedOldValue) == msg.getOldCharValue() //
                && asChar(expectedNewValue) == msg.getNewCharValue());
          }
          else if (type == short.class)
          {
            update(asShort(expectedOldValue) == msg.getOldShortValue() //
                && asShort(expectedNewValue) == msg.getNewShortValue());
          }
          else if (type == int.class)
          {
            update(asInt(expectedOldValue) == msg.getOldIntValue() //
                && asInt(expectedNewValue) == msg.getNewIntValue());
          }
          else if (type == long.class)
          {
            update(asLong(expectedOldValue) == msg.getOldLongValue() //
                && asLong(expectedNewValue) == msg.getNewLongValue());
          }
          else if (type == float.class)
          {
            update(asFloat(expectedOldValue) == msg.getOldFloatValue() //
                && asFloat(expectedNewValue) == msg.getNewFloatValue());
          }
          else if (type == double.class)
          {
            update(asDouble(expectedOldValue) == msg.getOldDoubleValue() //
                && asDouble(expectedNewValue) == msg.getNewDoubleValue());
          }
        }
        finally
        {
          semaphore.release();
        }
      }
    }

    private void update(boolean received)
    {
      if (received)
      {
        this.received = received;
      }
    }

    private boolean asBoolean(Object value)
    {
      return ((Boolean)value).booleanValue();
    }

    private byte asByte(Object value)
    {
      return ((Number)value).byteValue();
    }

    private char asChar(Object value)
    {
      return ((Character)value).charValue();
    }

    private short asShort(Object value)
    {
      return ((Number)value).shortValue();
    }

    private int asInt(Object value)
    {
      return ((Number)value).intValue();
    }

    private long asLong(Object value)
    {
      return ((Number)value).longValue();
    }

    private float asFloat(Object value)
    {
      return ((Number)value).floatValue();
    }

    private double asDouble(Object value)
    {
      return ((Number)value).doubleValue();
    }
  }
}
