/*
 * Copyright (c) 2013 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Steve Monnier - initial API and implementation
 *    Eike Stepper - adapted for more correct test model definition
 *    Christian W. Damus (CEA) - adapted for new test model with unsettable attribute
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model6.EmptyStringDefaultUnsettable;
import org.eclipse.emf.cdo.tests.model6.UnsettableAttributes;
import org.eclipse.emf.cdo.tests.model6.legacy.Model6Factory;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Date;

/**
 * Bug 405191.
 *
 * @author Steve Monnier
 */
public class Bugzilla_405191_Test extends AbstractCDOTest
{
  /**
   * This scenario validates that null can be set on a String feature with an empty string has default value has.
   */
  public void testSetNonDefaultNullString() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/res"));

    // Create an object and set is a string attribute to the empty string
    EmptyStringDefaultUnsettable localObject = getModel6Factory().createEmptyStringDefaultUnsettable();
    localObject.setAttribute("");
    resource.getContents().add(localObject);
    transaction.commit();

    CDOTransaction remoteTransaction = openSession().openTransaction();
    EmptyStringDefaultUnsettable remoteObject = remoteTransaction.getObject(localObject);

    // Validate that for another user (another transaction) the value is an empty string
    assertNotNull("Attribute should not be null", remoteObject.getAttribute());

    // Change attribute value from empty string to null
    assertNotNull("Attribute should not be be null", localObject.getAttribute());
    localObject.setAttribute(null);
    assertNull("Attribute should be null", localObject.getAttribute());

    // Validate that for another user (another transaction) the value is null
    commitAndSync(transaction, remoteTransaction);
    assertNull(remoteObject.getAttribute());
  }

  public void testAllTypesSetAndUnset() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/res"));

    // Create an object and set all attributes to non-default values
    UnsettableAttributes localObject = getModel6Factory().createUnsettableAttributes();
    localObject.setAttrBigDecimal(new BigDecimal("4711.1234"));
    localObject.setAttrBigInteger(new BigInteger("4711"));
    localObject.setAttrBoolean(true);
    localObject.setAttrBooleanObject(true);
    localObject.setAttrByte((byte)47);
    localObject.setAttrByteObject((byte)47);
    localObject.setAttrByteArray(new byte[] { 4, 7, 1, 1 });
    localObject.setAttrChar('4');
    localObject.setAttrCharacterObject('4');
    localObject.setAttrDate(new Date(4711));
    localObject.setAttrDouble(4711.1234);
    localObject.setAttrDoubleObject(4711.1234);
    localObject.setAttrFloat(4711.1234f);
    localObject.setAttrFloatObject(4711.1234f);
    localObject.setAttrInt(4711);
    localObject.setAttrIntegerObject(4711);
    localObject.setAttrJavaClass(getClass());
    localObject.setAttrJavaObject(new int[] { 4, 7, 1, 1 });
    localObject.setAttrLong(4711L);
    localObject.setAttrLongObject(4711L);
    localObject.setAttrShort((short)4711);
    localObject.setAttrShortObject((short)4711);
    localObject.setAttrString("4711");
    resource.getContents().add(localObject);
    transaction.commit();

    CDOTransaction remoteTransaction = openSession().openTransaction();
    UnsettableAttributes remoteObject = remoteTransaction.getObject(localObject);
    assertEquals(localObject.getAttrBigDecimal(), remoteObject.getAttrBigDecimal());
    assertEquals(localObject.getAttrBigInteger(), remoteObject.getAttrBigInteger());
    assertEquals(localObject.isAttrBoolean(), remoteObject.isAttrBoolean());
    assertEquals(localObject.getAttrBooleanObject(), remoteObject.getAttrBooleanObject());
    assertEquals(localObject.getAttrByte(), remoteObject.getAttrByte());
    assertEquals(localObject.getAttrByteObject(), remoteObject.getAttrByteObject());
    assertEquals(true, Arrays.equals(localObject.getAttrByteArray(), remoteObject.getAttrByteArray()));
    assertEquals(localObject.getAttrChar(), remoteObject.getAttrChar());
    assertEquals(localObject.getAttrCharacterObject(), remoteObject.getAttrCharacterObject());
    assertEquals(localObject.getAttrDate(), remoteObject.getAttrDate());
    assertEquals(localObject.getAttrDouble(), remoteObject.getAttrDouble());
    assertEquals(localObject.getAttrDoubleObject(), remoteObject.getAttrDoubleObject());
    assertEquals(localObject.getAttrFloat(), remoteObject.getAttrFloat());
    assertEquals(localObject.getAttrFloatObject(), remoteObject.getAttrFloatObject());
    assertEquals(localObject.getAttrInt(), remoteObject.getAttrInt());
    assertEquals(localObject.getAttrIntegerObject(), remoteObject.getAttrIntegerObject());
    assertEquals(localObject.getAttrJavaClass(), remoteObject.getAttrJavaClass());
    assertEquals(true, Arrays.equals((int[])localObject.getAttrJavaObject(), (int[])remoteObject.getAttrJavaObject()));
    assertEquals(localObject.getAttrLong(), remoteObject.getAttrLong());
    assertEquals(localObject.getAttrLongObject(), remoteObject.getAttrLongObject());
    assertEquals(localObject.getAttrShort(), remoteObject.getAttrShort());
    assertEquals(localObject.getAttrShortObject(), remoteObject.getAttrShortObject());
    assertEquals(localObject.getAttrString(), remoteObject.getAttrString());

    assertEquals(localObject.isSetAttrBigDecimal(), remoteObject.isSetAttrBigDecimal());
    assertEquals(localObject.isSetAttrBigInteger(), remoteObject.isSetAttrBigInteger());
    assertEquals(localObject.isSetAttrBoolean(), remoteObject.isSetAttrBoolean());
    assertEquals(localObject.isSetAttrBooleanObject(), remoteObject.isSetAttrBooleanObject());
    assertEquals(localObject.isSetAttrByte(), remoteObject.isSetAttrByte());
    assertEquals(localObject.isSetAttrByteObject(), remoteObject.isSetAttrByteObject());
    assertEquals(localObject.isSetAttrByteArray(), remoteObject.isSetAttrByteArray());
    assertEquals(localObject.isSetAttrChar(), remoteObject.isSetAttrChar());
    assertEquals(localObject.isSetAttrCharacterObject(), remoteObject.isSetAttrCharacterObject());
    assertEquals(localObject.isSetAttrDate(), remoteObject.isSetAttrDate());
    assertEquals(localObject.isSetAttrDouble(), remoteObject.isSetAttrDouble());
    assertEquals(localObject.isSetAttrDoubleObject(), remoteObject.isSetAttrDoubleObject());
    assertEquals(localObject.isSetAttrFloat(), remoteObject.isSetAttrFloat());
    assertEquals(localObject.isSetAttrFloatObject(), remoteObject.isSetAttrFloatObject());
    assertEquals(localObject.isSetAttrInt(), remoteObject.isSetAttrInt());
    assertEquals(localObject.isSetAttrIntegerObject(), remoteObject.isSetAttrIntegerObject());
    assertEquals(localObject.isSetAttrJavaClass(), remoteObject.isSetAttrJavaClass());
    assertEquals(localObject.isSetAttrJavaObject(), remoteObject.isSetAttrJavaObject());
    assertEquals(localObject.isSetAttrLong(), remoteObject.isSetAttrLong());
    assertEquals(localObject.isSetAttrLongObject(), remoteObject.isSetAttrLongObject());
    assertEquals(localObject.isSetAttrShort(), remoteObject.isSetAttrShort());
    assertEquals(localObject.isSetAttrShortObject(), remoteObject.isSetAttrShortObject());
    assertEquals(localObject.isSetAttrString(), remoteObject.isSetAttrString());

    assertEquals(true, remoteObject.isSetAttrBigDecimal());
    assertEquals(true, remoteObject.isSetAttrBigInteger());
    assertEquals(true, remoteObject.isSetAttrBoolean());
    assertEquals(true, remoteObject.isSetAttrBooleanObject());
    assertEquals(true, remoteObject.isSetAttrByte());
    assertEquals(true, remoteObject.isSetAttrByteObject());
    assertEquals(true, remoteObject.isSetAttrByteArray());
    assertEquals(true, remoteObject.isSetAttrChar());
    assertEquals(true, remoteObject.isSetAttrCharacterObject());
    assertEquals(true, remoteObject.isSetAttrDate());
    assertEquals(true, remoteObject.isSetAttrDouble());
    assertEquals(true, remoteObject.isSetAttrDoubleObject());
    assertEquals(true, remoteObject.isSetAttrFloat());
    assertEquals(true, remoteObject.isSetAttrFloatObject());
    assertEquals(true, remoteObject.isSetAttrInt());
    assertEquals(true, remoteObject.isSetAttrIntegerObject());
    assertEquals(true, remoteObject.isSetAttrJavaClass());
    assertEquals(true, remoteObject.isSetAttrJavaObject());
    assertEquals(true, remoteObject.isSetAttrLong());
    assertEquals(true, remoteObject.isSetAttrLongObject());
    assertEquals(true, remoteObject.isSetAttrShort());
    assertEquals(true, remoteObject.isSetAttrShortObject());
    assertEquals(true, remoteObject.isSetAttrString());

    // Unset all attributes
    localObject.unsetAttrBigDecimal();
    localObject.unsetAttrBigInteger();
    localObject.unsetAttrBoolean();
    localObject.unsetAttrBooleanObject();
    localObject.unsetAttrByte();
    localObject.unsetAttrByteObject();
    localObject.unsetAttrByteArray();
    localObject.unsetAttrChar();
    localObject.unsetAttrCharacterObject();
    localObject.unsetAttrDate();
    localObject.unsetAttrDouble();
    localObject.unsetAttrDoubleObject();
    localObject.unsetAttrFloat();
    localObject.unsetAttrFloatObject();
    localObject.unsetAttrInt();
    localObject.unsetAttrIntegerObject();
    localObject.unsetAttrJavaClass();
    localObject.unsetAttrJavaObject();
    localObject.unsetAttrLong();
    localObject.unsetAttrLongObject();
    localObject.unsetAttrShort();
    localObject.unsetAttrShortObject();
    localObject.unsetAttrString();

    commitAndSync(transaction, remoteTransaction);
    assertEquals(localObject.getAttrBigDecimal(), remoteObject.getAttrBigDecimal());
    assertEquals(localObject.getAttrBigInteger(), remoteObject.getAttrBigInteger());
    assertEquals(localObject.isAttrBoolean(), remoteObject.isAttrBoolean());
    assertEquals(localObject.getAttrBooleanObject(), remoteObject.getAttrBooleanObject());
    assertEquals(localObject.getAttrByte(), remoteObject.getAttrByte());
    assertEquals(localObject.getAttrByteObject(), remoteObject.getAttrByteObject());
    assertEquals(true, Arrays.equals(localObject.getAttrByteArray(), remoteObject.getAttrByteArray()));
    assertEquals(localObject.getAttrChar(), remoteObject.getAttrChar());
    assertEquals(localObject.getAttrCharacterObject(), remoteObject.getAttrCharacterObject());
    assertEquals(localObject.getAttrDate(), remoteObject.getAttrDate());
    assertEquals(localObject.getAttrDouble(), remoteObject.getAttrDouble());
    assertEquals(localObject.getAttrDoubleObject(), remoteObject.getAttrDoubleObject());
    assertEquals(localObject.getAttrFloat(), remoteObject.getAttrFloat());
    assertEquals(localObject.getAttrFloatObject(), remoteObject.getAttrFloatObject());
    assertEquals(localObject.getAttrInt(), remoteObject.getAttrInt());
    assertEquals(localObject.getAttrIntegerObject(), remoteObject.getAttrIntegerObject());
    assertEquals(localObject.getAttrJavaClass(), remoteObject.getAttrJavaClass());
    assertEquals(true, Arrays.equals((int[])localObject.getAttrJavaObject(), (int[])remoteObject.getAttrJavaObject()));
    assertEquals(localObject.getAttrLong(), remoteObject.getAttrLong());
    assertEquals(localObject.getAttrLongObject(), remoteObject.getAttrLongObject());
    assertEquals(localObject.getAttrShort(), remoteObject.getAttrShort());
    assertEquals(localObject.getAttrShortObject(), remoteObject.getAttrShortObject());
    assertEquals(localObject.getAttrString(), remoteObject.getAttrString());

    assertEquals(localObject.isSetAttrBigDecimal(), remoteObject.isSetAttrBigDecimal());
    assertEquals(localObject.isSetAttrBigInteger(), remoteObject.isSetAttrBigInteger());
    assertEquals(localObject.isSetAttrBoolean(), remoteObject.isSetAttrBoolean());
    assertEquals(localObject.isSetAttrBooleanObject(), remoteObject.isSetAttrBooleanObject());
    assertEquals(localObject.isSetAttrByte(), remoteObject.isSetAttrByte());
    assertEquals(localObject.isSetAttrByteObject(), remoteObject.isSetAttrByteObject());
    assertEquals(localObject.isSetAttrByteArray(), remoteObject.isSetAttrByteArray());
    assertEquals(localObject.isSetAttrChar(), remoteObject.isSetAttrChar());
    assertEquals(localObject.isSetAttrCharacterObject(), remoteObject.isSetAttrCharacterObject());
    assertEquals(localObject.isSetAttrDate(), remoteObject.isSetAttrDate());
    assertEquals(localObject.isSetAttrDouble(), remoteObject.isSetAttrDouble());
    assertEquals(localObject.isSetAttrDoubleObject(), remoteObject.isSetAttrDoubleObject());
    assertEquals(localObject.isSetAttrFloat(), remoteObject.isSetAttrFloat());
    assertEquals(localObject.isSetAttrFloatObject(), remoteObject.isSetAttrFloatObject());
    assertEquals(localObject.isSetAttrInt(), remoteObject.isSetAttrInt());
    assertEquals(localObject.isSetAttrIntegerObject(), remoteObject.isSetAttrIntegerObject());
    assertEquals(localObject.isSetAttrJavaClass(), remoteObject.isSetAttrJavaClass());
    assertEquals(localObject.isSetAttrJavaObject(), remoteObject.isSetAttrJavaObject());
    assertEquals(localObject.isSetAttrLong(), remoteObject.isSetAttrLong());
    assertEquals(localObject.isSetAttrLongObject(), remoteObject.isSetAttrLongObject());
    assertEquals(localObject.isSetAttrShort(), remoteObject.isSetAttrShort());
    assertEquals(localObject.isSetAttrShortObject(), remoteObject.isSetAttrShortObject());
    assertEquals(localObject.isSetAttrString(), remoteObject.isSetAttrString());
  }

  public void testAllTypesSetAndDefault() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/res"));

    // Create an object and set all attributes to non-default values
    UnsettableAttributes localObject = getModel6Factory().createUnsettableAttributes();
    localObject.setAttrBigDecimal(new BigDecimal("4711.1234"));
    localObject.setAttrBigInteger(new BigInteger("4711"));
    localObject.setAttrBoolean(true);
    localObject.setAttrBooleanObject(true);
    localObject.setAttrByte((byte)47);
    localObject.setAttrByteObject((byte)47);
    localObject.setAttrByteArray(new byte[] { 4, 7, 1, 1 });
    localObject.setAttrChar('4');
    localObject.setAttrCharacterObject('4');
    localObject.setAttrDate(new Date(4711));
    localObject.setAttrDouble(4711.1234);
    localObject.setAttrDoubleObject(4711.1234);
    localObject.setAttrFloat(4711.1234f);
    localObject.setAttrFloatObject(4711.1234f);
    localObject.setAttrInt(4711);
    localObject.setAttrIntegerObject(4711);
    localObject.setAttrJavaClass(getClass());
    localObject.setAttrJavaObject(new int[] { 4, 7, 1, 1 });
    localObject.setAttrLong(4711L);
    localObject.setAttrLongObject(4711L);
    localObject.setAttrShort((short)4711);
    localObject.setAttrShortObject((short)4711);
    localObject.setAttrString("4711");
    resource.getContents().add(localObject);
    transaction.commit();

    CDOTransaction remoteTransaction = openSession().openTransaction();
    UnsettableAttributes remoteObject = remoteTransaction.getObject(localObject);
    assertEquals(localObject.getAttrBigDecimal(), remoteObject.getAttrBigDecimal());
    assertEquals(localObject.getAttrBigInteger(), remoteObject.getAttrBigInteger());
    assertEquals(localObject.isAttrBoolean(), remoteObject.isAttrBoolean());
    assertEquals(localObject.getAttrBooleanObject(), remoteObject.getAttrBooleanObject());
    assertEquals(localObject.getAttrByte(), remoteObject.getAttrByte());
    assertEquals(localObject.getAttrByteObject(), remoteObject.getAttrByteObject());
    assertEquals(true, Arrays.equals(localObject.getAttrByteArray(), remoteObject.getAttrByteArray()));
    assertEquals(localObject.getAttrChar(), remoteObject.getAttrChar());
    assertEquals(localObject.getAttrCharacterObject(), remoteObject.getAttrCharacterObject());
    assertEquals(localObject.getAttrDate(), remoteObject.getAttrDate());
    assertEquals(localObject.getAttrDouble(), remoteObject.getAttrDouble());
    assertEquals(localObject.getAttrDoubleObject(), remoteObject.getAttrDoubleObject());
    assertEquals(localObject.getAttrFloat(), remoteObject.getAttrFloat());
    assertEquals(localObject.getAttrFloatObject(), remoteObject.getAttrFloatObject());
    assertEquals(localObject.getAttrInt(), remoteObject.getAttrInt());
    assertEquals(localObject.getAttrIntegerObject(), remoteObject.getAttrIntegerObject());
    assertEquals(localObject.getAttrJavaClass(), remoteObject.getAttrJavaClass());
    assertEquals(true, Arrays.equals((int[])localObject.getAttrJavaObject(), (int[])remoteObject.getAttrJavaObject()));
    assertEquals(localObject.getAttrLong(), remoteObject.getAttrLong());
    assertEquals(localObject.getAttrLongObject(), remoteObject.getAttrLongObject());
    assertEquals(localObject.getAttrShort(), remoteObject.getAttrShort());
    assertEquals(localObject.getAttrShortObject(), remoteObject.getAttrShortObject());
    assertEquals(localObject.getAttrString(), remoteObject.getAttrString());

    assertEquals(localObject.isSetAttrBigDecimal(), remoteObject.isSetAttrBigDecimal());
    assertEquals(localObject.isSetAttrBigInteger(), remoteObject.isSetAttrBigInteger());
    assertEquals(localObject.isSetAttrBoolean(), remoteObject.isSetAttrBoolean());
    assertEquals(localObject.isSetAttrBooleanObject(), remoteObject.isSetAttrBooleanObject());
    assertEquals(localObject.isSetAttrByte(), remoteObject.isSetAttrByte());
    assertEquals(localObject.isSetAttrByteObject(), remoteObject.isSetAttrByteObject());
    assertEquals(localObject.isSetAttrByteArray(), remoteObject.isSetAttrByteArray());
    assertEquals(localObject.isSetAttrChar(), remoteObject.isSetAttrChar());
    assertEquals(localObject.isSetAttrCharacterObject(), remoteObject.isSetAttrCharacterObject());
    assertEquals(localObject.isSetAttrDate(), remoteObject.isSetAttrDate());
    assertEquals(localObject.isSetAttrDouble(), remoteObject.isSetAttrDouble());
    assertEquals(localObject.isSetAttrDoubleObject(), remoteObject.isSetAttrDoubleObject());
    assertEquals(localObject.isSetAttrFloat(), remoteObject.isSetAttrFloat());
    assertEquals(localObject.isSetAttrFloatObject(), remoteObject.isSetAttrFloatObject());
    assertEquals(localObject.isSetAttrInt(), remoteObject.isSetAttrInt());
    assertEquals(localObject.isSetAttrIntegerObject(), remoteObject.isSetAttrIntegerObject());
    assertEquals(localObject.isSetAttrJavaClass(), remoteObject.isSetAttrJavaClass());
    assertEquals(localObject.isSetAttrJavaObject(), remoteObject.isSetAttrJavaObject());
    assertEquals(localObject.isSetAttrLong(), remoteObject.isSetAttrLong());
    assertEquals(localObject.isSetAttrLongObject(), remoteObject.isSetAttrLongObject());
    assertEquals(localObject.isSetAttrShort(), remoteObject.isSetAttrShort());
    assertEquals(localObject.isSetAttrShortObject(), remoteObject.isSetAttrShortObject());
    assertEquals(localObject.isSetAttrString(), remoteObject.isSetAttrString());

    // Set all attributes to default values
    localObject.setAttrBigDecimal(null);
    localObject.setAttrBigInteger(null);
    localObject.setAttrBoolean(false);
    localObject.setAttrBooleanObject(false);
    localObject.setAttrByte((byte)0);
    localObject.setAttrByteObject(null);
    localObject.setAttrByteArray(null);
    localObject.setAttrChar('0');
    localObject.setAttrCharacterObject(null);
    localObject.setAttrDate(null);
    localObject.setAttrDouble(0.0);
    localObject.setAttrDoubleObject(null);
    localObject.setAttrFloat(0.0f);
    localObject.setAttrFloatObject(null);
    localObject.setAttrInt(0);
    localObject.setAttrIntegerObject(null);
    localObject.setAttrJavaClass(null);
    localObject.setAttrJavaObject(null);
    localObject.setAttrLong(0L);
    localObject.setAttrLongObject(null);
    localObject.setAttrShort((short)0);
    localObject.setAttrShortObject(null);
    localObject.setAttrString(null);

    commitAndSync(transaction, remoteTransaction);
    assertEquals(localObject.getAttrBigDecimal(), remoteObject.getAttrBigDecimal());
    assertEquals(localObject.getAttrBigInteger(), remoteObject.getAttrBigInteger());
    assertEquals(localObject.isAttrBoolean(), remoteObject.isAttrBoolean());
    assertEquals(localObject.getAttrBooleanObject(), remoteObject.getAttrBooleanObject());
    assertEquals(localObject.getAttrByte(), remoteObject.getAttrByte());
    assertEquals(localObject.getAttrByteObject(), remoteObject.getAttrByteObject());
    assertEquals(true, Arrays.equals(localObject.getAttrByteArray(), remoteObject.getAttrByteArray()));
    assertEquals(localObject.getAttrChar(), remoteObject.getAttrChar());
    assertEquals(localObject.getAttrCharacterObject(), remoteObject.getAttrCharacterObject());
    assertEquals(localObject.getAttrDate(), remoteObject.getAttrDate());
    assertEquals(localObject.getAttrDouble(), remoteObject.getAttrDouble());
    assertEquals(localObject.getAttrDoubleObject(), remoteObject.getAttrDoubleObject());
    assertEquals(localObject.getAttrFloat(), remoteObject.getAttrFloat());
    assertEquals(localObject.getAttrFloatObject(), remoteObject.getAttrFloatObject());
    assertEquals(localObject.getAttrInt(), remoteObject.getAttrInt());
    assertEquals(localObject.getAttrIntegerObject(), remoteObject.getAttrIntegerObject());
    assertEquals(localObject.getAttrJavaClass(), remoteObject.getAttrJavaClass());
    assertEquals(true, Arrays.equals((int[])localObject.getAttrJavaObject(), (int[])remoteObject.getAttrJavaObject()));
    assertEquals(localObject.getAttrLong(), remoteObject.getAttrLong());
    assertEquals(localObject.getAttrLongObject(), remoteObject.getAttrLongObject());
    assertEquals(localObject.getAttrShort(), remoteObject.getAttrShort());
    assertEquals(localObject.getAttrShortObject(), remoteObject.getAttrShortObject());
    assertEquals(localObject.getAttrString(), remoteObject.getAttrString());

    assertEquals(localObject.isSetAttrBigDecimal(), remoteObject.isSetAttrBigDecimal());
    assertEquals(localObject.isSetAttrBigInteger(), remoteObject.isSetAttrBigInteger());
    assertEquals(localObject.isSetAttrBoolean(), remoteObject.isSetAttrBoolean());
    assertEquals(localObject.isSetAttrBooleanObject(), remoteObject.isSetAttrBooleanObject());
    assertEquals(localObject.isSetAttrByte(), remoteObject.isSetAttrByte());
    assertEquals(localObject.isSetAttrByteObject(), remoteObject.isSetAttrByteObject());
    assertEquals(localObject.isSetAttrByteArray(), remoteObject.isSetAttrByteArray());
    assertEquals(localObject.isSetAttrChar(), remoteObject.isSetAttrChar());
    assertEquals(localObject.isSetAttrCharacterObject(), remoteObject.isSetAttrCharacterObject());
    assertEquals(localObject.isSetAttrDate(), remoteObject.isSetAttrDate());
    assertEquals(localObject.isSetAttrDouble(), remoteObject.isSetAttrDouble());
    assertEquals(localObject.isSetAttrDoubleObject(), remoteObject.isSetAttrDoubleObject());
    assertEquals(localObject.isSetAttrFloat(), remoteObject.isSetAttrFloat());
    assertEquals(localObject.isSetAttrFloatObject(), remoteObject.isSetAttrFloatObject());
    assertEquals(localObject.isSetAttrInt(), remoteObject.isSetAttrInt());
    assertEquals(localObject.isSetAttrIntegerObject(), remoteObject.isSetAttrIntegerObject());
    assertEquals(localObject.isSetAttrJavaClass(), remoteObject.isSetAttrJavaClass());
    assertEquals(localObject.isSetAttrJavaObject(), remoteObject.isSetAttrJavaObject());
    assertEquals(localObject.isSetAttrLong(), remoteObject.isSetAttrLong());
    assertEquals(localObject.isSetAttrLongObject(), remoteObject.isSetAttrLongObject());
    assertEquals(localObject.isSetAttrShort(), remoteObject.isSetAttrShort());
    assertEquals(localObject.isSetAttrShortObject(), remoteObject.isSetAttrShortObject());
    assertEquals(localObject.isSetAttrString(), remoteObject.isSetAttrString());

    assertEquals(true, remoteObject.isSetAttrBigDecimal());
    assertEquals(true, remoteObject.isSetAttrBigInteger());
    assertEquals(true, remoteObject.isSetAttrBoolean());
    assertEquals(true, remoteObject.isSetAttrBooleanObject());
    assertEquals(true, remoteObject.isSetAttrByte());
    assertEquals(true, remoteObject.isSetAttrByteObject());
    assertEquals(true, remoteObject.isSetAttrByteArray());
    assertEquals(true, remoteObject.isSetAttrChar());
    assertEquals(true, remoteObject.isSetAttrCharacterObject());
    assertEquals(true, remoteObject.isSetAttrDate());
    assertEquals(true, remoteObject.isSetAttrDouble());
    assertEquals(true, remoteObject.isSetAttrDoubleObject());
    assertEquals(true, remoteObject.isSetAttrFloat());
    assertEquals(true, remoteObject.isSetAttrFloatObject());
    assertEquals(true, remoteObject.isSetAttrInt());
    assertEquals(true, remoteObject.isSetAttrIntegerObject());
    assertEquals(true, remoteObject.isSetAttrJavaClass());
    assertEquals(true, remoteObject.isSetAttrJavaObject());
    assertEquals(true, remoteObject.isSetAttrLong());
    assertEquals(true, remoteObject.isSetAttrLongObject());
    assertEquals(true, remoteObject.isSetAttrShort());
    assertEquals(true, remoteObject.isSetAttrShortObject());
    assertEquals(true, remoteObject.isSetAttrString());
  }

  public void testAllTypesSetAndNull() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/res"));

    // Create an object and set all attributes to non-default values
    UnsettableAttributes localObject = getModel6Factory().createUnsettableAttributes();
    localObject.setAttrBigDecimal(new BigDecimal("4711.1234"));
    localObject.setAttrBigInteger(new BigInteger("4711"));
    localObject.setAttrBooleanObject(true);
    localObject.setAttrByteObject((byte)47);
    localObject.setAttrByteArray(new byte[] { 4, 7, 1, 1 });
    localObject.setAttrCharacterObject('4');
    localObject.setAttrDate(new Date(4711));
    localObject.setAttrDoubleObject(4711.1234);
    localObject.setAttrFloatObject(4711.1234f);
    localObject.setAttrIntegerObject(4711);
    localObject.setAttrJavaClass(getClass());
    localObject.setAttrJavaObject(new int[] { 4, 7, 1, 1 });
    localObject.setAttrLongObject(4711L);
    localObject.setAttrShortObject((short)4711);
    localObject.setAttrString("4711");
    resource.getContents().add(localObject);
    transaction.commit();

    CDOTransaction remoteTransaction = openSession().openTransaction();
    UnsettableAttributes remoteObject = remoteTransaction.getObject(localObject);

    // Set all attributes to default values
    localObject.setAttrBigDecimal(null);
    localObject.setAttrBigInteger(null);
    localObject.setAttrBooleanObject(null);
    localObject.setAttrByteObject(null);
    localObject.setAttrByteArray(null);
    localObject.setAttrCharacterObject(null);
    localObject.setAttrDate(null);
    localObject.setAttrDoubleObject(null);
    localObject.setAttrFloatObject(null);
    localObject.setAttrIntegerObject(null);
    localObject.setAttrJavaClass(null);
    localObject.setAttrJavaObject(null);
    localObject.setAttrLongObject(null);
    localObject.setAttrShortObject(null);
    localObject.setAttrString(null);

    commitAndSync(transaction, remoteTransaction);
    assertEquals(localObject.getAttrBigDecimal(), remoteObject.getAttrBigDecimal());
    assertEquals(localObject.getAttrBigInteger(), remoteObject.getAttrBigInteger());
    assertEquals(localObject.getAttrBooleanObject(), remoteObject.getAttrBooleanObject());
    assertEquals(localObject.getAttrByteObject(), remoteObject.getAttrByteObject());
    assertEquals(true, Arrays.equals(localObject.getAttrByteArray(), remoteObject.getAttrByteArray()));
    assertEquals(localObject.getAttrCharacterObject(), remoteObject.getAttrCharacterObject());
    assertEquals(localObject.getAttrDate(), remoteObject.getAttrDate());
    assertEquals(localObject.getAttrDoubleObject(), remoteObject.getAttrDoubleObject());
    assertEquals(localObject.getAttrFloatObject(), remoteObject.getAttrFloatObject());
    assertEquals(localObject.getAttrIntegerObject(), remoteObject.getAttrIntegerObject());
    assertEquals(localObject.getAttrJavaClass(), remoteObject.getAttrJavaClass());
    assertEquals(true, Arrays.equals((int[])localObject.getAttrJavaObject(), (int[])remoteObject.getAttrJavaObject()));
    assertEquals(localObject.getAttrLongObject(), remoteObject.getAttrLongObject());
    assertEquals(localObject.getAttrShortObject(), remoteObject.getAttrShortObject());
    assertEquals(localObject.getAttrString(), remoteObject.getAttrString());

    assertEquals(localObject.isSetAttrBigDecimal(), remoteObject.isSetAttrBigDecimal());
    assertEquals(localObject.isSetAttrBigInteger(), remoteObject.isSetAttrBigInteger());
    assertEquals(localObject.isSetAttrBooleanObject(), remoteObject.isSetAttrBooleanObject());
    assertEquals(localObject.isSetAttrByteObject(), remoteObject.isSetAttrByteObject());
    assertEquals(localObject.isSetAttrByteArray(), remoteObject.isSetAttrByteArray());
    assertEquals(localObject.isSetAttrCharacterObject(), remoteObject.isSetAttrCharacterObject());
    assertEquals(localObject.isSetAttrDate(), remoteObject.isSetAttrDate());
    assertEquals(localObject.isSetAttrDoubleObject(), remoteObject.isSetAttrDoubleObject());
    assertEquals(localObject.isSetAttrFloatObject(), remoteObject.isSetAttrFloatObject());
    assertEquals(localObject.isSetAttrIntegerObject(), remoteObject.isSetAttrIntegerObject());
    assertEquals(localObject.isSetAttrJavaClass(), remoteObject.isSetAttrJavaClass());
    assertEquals(localObject.isSetAttrJavaObject(), remoteObject.isSetAttrJavaObject());
    assertEquals(localObject.isSetAttrLongObject(), remoteObject.isSetAttrLongObject());
    assertEquals(localObject.isSetAttrShortObject(), remoteObject.isSetAttrShortObject());
    assertEquals(localObject.isSetAttrString(), remoteObject.isSetAttrString());

    assertEquals(true, remoteObject.isSetAttrBigDecimal());
    assertEquals(true, remoteObject.isSetAttrBigInteger());
    assertEquals(true, remoteObject.isSetAttrBooleanObject());
    assertEquals(true, remoteObject.isSetAttrByteObject());
    assertEquals(true, remoteObject.isSetAttrByteArray());
    assertEquals(true, remoteObject.isSetAttrCharacterObject());
    assertEquals(true, remoteObject.isSetAttrDate());
    assertEquals(true, remoteObject.isSetAttrDoubleObject());
    assertEquals(true, remoteObject.isSetAttrFloatObject());
    assertEquals(true, remoteObject.isSetAttrIntegerObject());
    assertEquals(true, remoteObject.isSetAttrJavaClass());
    assertEquals(true, remoteObject.isSetAttrJavaObject());
    assertEquals(true, remoteObject.isSetAttrLongObject());
    assertEquals(true, remoteObject.isSetAttrShortObject());
    assertEquals(true, remoteObject.isSetAttrString());
  }

  public void testAllTypesSetAndDefaultEMF() throws Exception
  {
    // Create an object and set all attributes to non-default values
    UnsettableAttributes localObject = Model6Factory.eINSTANCE.createUnsettableAttributes();
    localObject.setAttrBigDecimal(new BigDecimal("4711.1234"));
    localObject.setAttrBigInteger(new BigInteger("4711"));
    localObject.setAttrBoolean(true);
    localObject.setAttrBooleanObject(true);
    localObject.setAttrByte((byte)47);
    localObject.setAttrByteObject((byte)47);
    localObject.setAttrByteArray(new byte[] { 4, 7, 1, 1 });
    localObject.setAttrChar('4');
    localObject.setAttrCharacterObject('4');
    localObject.setAttrDate(new Date(4711));
    localObject.setAttrDouble(4711.1234);
    localObject.setAttrDoubleObject(4711.1234);
    localObject.setAttrFloat(4711.1234f);
    localObject.setAttrFloatObject(4711.1234f);
    localObject.setAttrInt(4711);
    localObject.setAttrIntegerObject(4711);
    localObject.setAttrJavaClass(getClass());
    localObject.setAttrJavaObject(new int[] { 4, 7, 1, 1 });
    localObject.setAttrLong(4711L);
    localObject.setAttrLongObject(4711L);
    localObject.setAttrShort((short)4711);
    localObject.setAttrShortObject((short)4711);
    localObject.setAttrString("4711");

    assertEquals(true, localObject.isSetAttrBigDecimal());
    assertEquals(true, localObject.isSetAttrBigInteger());
    assertEquals(true, localObject.isSetAttrBoolean());
    assertEquals(true, localObject.isSetAttrBooleanObject());
    assertEquals(true, localObject.isSetAttrByte());
    assertEquals(true, localObject.isSetAttrByteObject());
    assertEquals(true, localObject.isSetAttrByteArray());
    assertEquals(true, localObject.isSetAttrChar());
    assertEquals(true, localObject.isSetAttrCharacterObject());
    assertEquals(true, localObject.isSetAttrDate());
    assertEquals(true, localObject.isSetAttrDouble());
    assertEquals(true, localObject.isSetAttrDoubleObject());
    assertEquals(true, localObject.isSetAttrFloat());
    assertEquals(true, localObject.isSetAttrFloatObject());
    assertEquals(true, localObject.isSetAttrInt());
    assertEquals(true, localObject.isSetAttrIntegerObject());
    assertEquals(true, localObject.isSetAttrJavaClass());
    assertEquals(true, localObject.isSetAttrJavaObject());
    assertEquals(true, localObject.isSetAttrLong());
    assertEquals(true, localObject.isSetAttrLongObject());
    assertEquals(true, localObject.isSetAttrShort());
    assertEquals(true, localObject.isSetAttrShortObject());
    assertEquals(true, localObject.isSetAttrString());

    // Set all attributes to default values
    localObject.setAttrBigDecimal(null);
    localObject.setAttrBigInteger(null);
    localObject.setAttrBoolean(false);
    localObject.setAttrBooleanObject(false);
    localObject.setAttrByte((byte)0);
    localObject.setAttrByteObject(null);
    localObject.setAttrByteArray(null);
    localObject.setAttrChar('0');
    localObject.setAttrCharacterObject(null);
    localObject.setAttrDate(null);
    localObject.setAttrDouble(0.0);
    localObject.setAttrDoubleObject(null);
    localObject.setAttrFloat(0.0f);
    localObject.setAttrFloatObject(null);
    localObject.setAttrInt(0);
    localObject.setAttrIntegerObject(null);
    localObject.setAttrJavaClass(null);
    localObject.setAttrJavaObject(null);
    localObject.setAttrLong(0L);
    localObject.setAttrLongObject(null);
    localObject.setAttrShort((short)0);
    localObject.setAttrShortObject(null);
    localObject.setAttrString(null);

    assertEquals(true, localObject.isSetAttrBigDecimal());
    assertEquals(true, localObject.isSetAttrBigInteger());
    assertEquals(true, localObject.isSetAttrBoolean());
    assertEquals(true, localObject.isSetAttrBooleanObject());
    assertEquals(true, localObject.isSetAttrByte());
    assertEquals(true, localObject.isSetAttrByteObject());
    assertEquals(true, localObject.isSetAttrByteArray());
    assertEquals(true, localObject.isSetAttrChar());
    assertEquals(true, localObject.isSetAttrCharacterObject());
    assertEquals(true, localObject.isSetAttrDate());
    assertEquals(true, localObject.isSetAttrDouble());
    assertEquals(true, localObject.isSetAttrDoubleObject());
    assertEquals(true, localObject.isSetAttrFloat());
    assertEquals(true, localObject.isSetAttrFloatObject());
    assertEquals(true, localObject.isSetAttrInt());
    assertEquals(true, localObject.isSetAttrIntegerObject());
    assertEquals(true, localObject.isSetAttrJavaClass());
    assertEquals(true, localObject.isSetAttrJavaObject());
    assertEquals(true, localObject.isSetAttrLong());
    assertEquals(true, localObject.isSetAttrLongObject());
    assertEquals(true, localObject.isSetAttrShort());
    assertEquals(true, localObject.isSetAttrShortObject());
    assertEquals(true, localObject.isSetAttrString());
  }
}
