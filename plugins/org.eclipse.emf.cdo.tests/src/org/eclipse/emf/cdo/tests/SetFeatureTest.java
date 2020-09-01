/*
 * Copyright (c) 2010-2012, 2016, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.model1.VAT;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.ObjectUtil;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;

import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author Martin Fluegge
 */
public class SetFeatureTest extends AbstractCDOTest
{
  // Mysql TIMESTAMP values do not support milliseconds!
  private static final Date DATE = new GregorianCalendar(2020, 4, 2, 6, 45, 14).getTime();

  public void testUnsettableDateNoDefault_SetDefault() throws Exception
  {
    testIsSet(getModel2Factory().createUnsettable1(), getModel2Package().getUnsettable1_UnsettableDate(),
        getModel2Package().getUnsettable1_UnsettableDate().getDefaultValue());
  }

  public void testUnsettableStringNoDefault_SetDefault() throws Exception
  {
    testIsSet(getModel2Factory().createUnsettable1(), getModel2Package().getUnsettable1_UnsettableString(),
        getModel2Package().getUnsettable1_UnsettableString().getDefaultValue());
  }

  public void testNotUnsettableBooleanNoDefault() throws Exception
  {
    testIsSet(getModel2Factory().createNotUnsettable(), getModel2Package().getNotUnsettable_NotUnsettableBoolean(), true);
  }

  public void testNotUnsettableBooleanNoDefault2() throws Exception
  {
    testIsSet(getModel2Factory().createNotUnsettable(), getModel2Package().getNotUnsettable_NotUnsettableBoolean(), Boolean.valueOf(false));
  }

  public void testNotUnsettableBooleanNoDefault_SetDefault() throws Exception
  {
    testIsSet(getModel2Factory().createNotUnsettable(), getModel2Package().getNotUnsettable_NotUnsettableBoolean(),
        getModel2Package().getNotUnsettable_NotUnsettableBoolean().getDefaultValue());
  }

  public void testNotUnsettableByteNoDefault() throws Exception
  {
    testIsSet(getModel2Factory().createNotUnsettable(), getModel2Package().getNotUnsettable_NotUnsettableByte(), (byte)10);
  }

  public void testNotUnsettableByteNoDefault2() throws Exception
  {
    testIsSet(getModel2Factory().createNotUnsettable(), getModel2Package().getNotUnsettable_NotUnsettableByte(), Byte.valueOf((byte)25));
  }

  public void testNotUnsettableByteNoDefault_SetDefault() throws Exception
  {
    testIsSet(getModel2Factory().createNotUnsettable(), getModel2Package().getNotUnsettable_NotUnsettableByte(),
        getModel2Package().getNotUnsettable_NotUnsettableByte().getDefaultValue());
  }

  public void testNotUnsettableCharNoDefault() throws Exception
  {
    testIsSet(getModel2Factory().createNotUnsettable(), getModel2Package().getNotUnsettable_NotUnsettableChar(), 'c');
  }

  public void testNotUnsettableCharNoDefault_SetDefault() throws Exception
  {
    testIsSet(getModel2Factory().createNotUnsettable(), getModel2Package().getNotUnsettable_NotUnsettableChar(), Character.valueOf('c'));
  }

  public void testNotUnsettableCharNoDefault2() throws Exception
  {
    testIsSet(getModel2Factory().createNotUnsettable(), getModel2Package().getNotUnsettable_NotUnsettableChar(),
        getModel2Package().getNotUnsettable_NotUnsettableChar().getDefaultValue());
  }

  public void testNotUnsettableDateNoDefault() throws Exception
  {
    testIsSet(getModel2Factory().createNotUnsettable(), getModel2Package().getNotUnsettable_NotUnsettableDate(), DATE);
  }

  public void testNotUnsettableDateNoDefault_SetDefault() throws Exception
  {
    testIsSet(getModel2Factory().createNotUnsettable(), getModel2Package().getNotUnsettable_NotUnsettableDate(),
        getModel2Package().getNotUnsettable_NotUnsettableDate().getDefaultValue());
  }

  public void testNotUnsettableDoubleNoDefault() throws Exception
  {
    testIsSet(getModel2Factory().createNotUnsettable(), getModel2Package().getNotUnsettable_NotUnsettableDouble(), 15.03d);
  }

  public void testNotUnsettableDoubleNoDefault_SetDefault() throws Exception
  {
    testIsSet(getModel2Factory().createNotUnsettable(), getModel2Package().getNotUnsettable_NotUnsettableDouble(), Double.valueOf(19.79));
  }

  public void testNotUnsettableDoubleNoDefault2() throws Exception
  {
    testIsSet(getModel2Factory().createNotUnsettable(), getModel2Package().getNotUnsettable_NotUnsettableDouble(),
        getModel2Package().getNotUnsettable_NotUnsettableDouble().getDefaultValue());
  }

  public void testNotUnsettableFloatNoDefault() throws Exception
  {
    testIsSet(getModel2Factory().createNotUnsettable(), getModel2Package().getNotUnsettable_NotUnsettableFloat(), 15.03f);
  }

  public void testNotUnsettableFloatNoDefault2() throws Exception
  {
    testIsSet(getModel2Factory().createNotUnsettable(), getModel2Package().getNotUnsettable_NotUnsettableFloat(), Float.valueOf(19.79f));
  }

  public void testNotUnsettableFloatNoDefault_SetDefault() throws Exception
  {
    testIsSet(getModel2Factory().createNotUnsettable(), getModel2Package().getNotUnsettable_NotUnsettableFloat(),
        getModel2Package().getNotUnsettable_NotUnsettableFloat().getDefaultValue());
  }

  public void testNotUnsettableIntNoDefault() throws Exception
  {
    testIsSet(getModel2Factory().createNotUnsettable(), getModel2Package().getNotUnsettable_NotUnsettableInt(), 15);
  }

  public void testNotUnsettableIntNoDefault2() throws Exception
  {
    testIsSet(getModel2Factory().createNotUnsettable(), getModel2Package().getNotUnsettable_NotUnsettableInt(), Integer.valueOf(15));
  }

  public void testNotUnsettableIntNoDefault_SetDefault() throws Exception
  {
    testIsSet(getModel2Factory().createNotUnsettable(), getModel2Package().getNotUnsettable_NotUnsettableInt(),
        getModel2Package().getNotUnsettable_NotUnsettableInt().getDefaultValue());
  }

  public void testNotUnsettableLongNoDefault() throws Exception
  {
    testIsSet(getModel2Factory().createNotUnsettable(), getModel2Package().getNotUnsettable_NotUnsettableLong(), 15L);
  }

  public void testNotUnsettableLongNoDefault2() throws Exception
  {
    testIsSet(getModel2Factory().createNotUnsettable(), getModel2Package().getNotUnsettable_NotUnsettableLong(), Long.valueOf(15031979L));
  }

  public void testNotUnsettableLongNoDefault_SetDefault() throws Exception
  {
    testIsSet(getModel2Factory().createNotUnsettable(), getModel2Package().getNotUnsettable_NotUnsettableLong(),
        getModel2Package().getNotUnsettable_NotUnsettableLong().getDefaultValue());
  }

  public void testNotUnsettableShortNoDefault() throws Exception
  {
    testIsSet(getModel2Factory().createNotUnsettable(), getModel2Package().getNotUnsettable_NotUnsettableShort(), (short)15);
  }

  public void testNotUnsettableShortNoDefault2() throws Exception
  {
    testIsSet(getModel2Factory().createNotUnsettable(), getModel2Package().getNotUnsettable_NotUnsettableShort(), Short.valueOf((short)15));
  }

  public void testNotUnsettableShortNoDefault_SetDefault() throws Exception
  {
    testIsSet(getModel2Factory().createNotUnsettable(), getModel2Package().getNotUnsettable_NotUnsettableShort(),
        getModel2Package().getNotUnsettable_NotUnsettableShort().getDefaultValue());
  }

  public void testNotUnsettableStringNoDefault() throws Exception
  {
    testIsSet(getModel2Factory().createNotUnsettable(), getModel2Package().getNotUnsettable_NotUnsettableString(), "Martin");
  }

  public void testNotUnsettableStringNoDefault2() throws Exception
  {
    testIsSet(getModel2Factory().createNotUnsettable(), getModel2Package().getNotUnsettable_NotUnsettableString(), new String("Martin"));
  }

  public void testNotUnsettableStringNoDefault_SetDefault() throws Exception
  {
    testIsSet(getModel2Factory().createNotUnsettable(), getModel2Package().getNotUnsettable_NotUnsettableString(),
        getModel2Package().getNotUnsettable_NotUnsettableString().getDefaultValue());
  }

  public void testNotUnsettableVATNoDefault() throws Exception
  {
    testIsSet(getModel2Factory().createNotUnsettable(), getModel2Package().getNotUnsettable_NotUnsettableVAT(), VAT.VAT7);
  }

  public void testNotUnsettableVATNoDefault_SetDefault() throws Exception
  {
    testIsSet(getModel2Factory().createNotUnsettable(), getModel2Package().getNotUnsettable_NotUnsettableVAT(),
        getModel2Package().getNotUnsettable_NotUnsettableVAT().getDefaultValue());
  }

  public void testNotUnsettableWithDefaultBoolean() throws Exception
  {
    testIsSet(getModel2Factory().createNotUnsettableWithDefault(), getModel2Package().getNotUnsettableWithDefault_NotUnsettableBoolean(), true);
  }

  public void testNotUnsettableWithDefaultBoolean2() throws Exception
  {
    testIsSet(getModel2Factory().createNotUnsettableWithDefault(), getModel2Package().getNotUnsettableWithDefault_NotUnsettableBoolean(),
        Boolean.valueOf(false));
  }

  public void testNotUnsettableWithDefaultBoolean_SetDefault() throws Exception
  {
    testIsSet(getModel2Factory().createNotUnsettableWithDefault(), getModel2Package().getNotUnsettableWithDefault_NotUnsettableBoolean(),
        getModel2Package().getNotUnsettableWithDefault_NotUnsettableBoolean().getDefaultValue());
  }

  public void testNotUnsettableWithDefaultByte() throws Exception
  {
    testIsSet(getModel2Factory().createNotUnsettableWithDefault(), getModel2Package().getNotUnsettableWithDefault_NotUnsettableByte(), (byte)10);
  }

  public void testNotUnsettableWithDefaultByte2() throws Exception
  {
    testIsSet(getModel2Factory().createNotUnsettableWithDefault(), getModel2Package().getNotUnsettableWithDefault_NotUnsettableByte(), Byte.valueOf((byte)25));
  }

  public void testNotUnsettableWithDefaultByte_SetDefault() throws Exception
  {
    testIsSet(getModel2Factory().createNotUnsettableWithDefault(), getModel2Package().getNotUnsettableWithDefault_NotUnsettableByte(),
        getModel2Package().getNotUnsettableWithDefault_NotUnsettableByte().getDefaultValue());
  }

  public void testNotUnsettableWithDefaultChar() throws Exception
  {
    testIsSet(getModel2Factory().createNotUnsettableWithDefault(), getModel2Package().getNotUnsettableWithDefault_NotUnsettableChar(), 'c');
  }

  public void testNotUnsettableWithDefaultChart2() throws Exception
  {
    testIsSet(getModel2Factory().createNotUnsettableWithDefault(), getModel2Package().getNotUnsettableWithDefault_NotUnsettableChar(), Character.valueOf('c'));
  }

  public void testNotUnsettableWithDefaultChar_SetDefault() throws Exception
  {
    testIsSet(getModel2Factory().createNotUnsettableWithDefault(), getModel2Package().getNotUnsettableWithDefault_NotUnsettableChar(),
        getModel2Package().getNotUnsettableWithDefault_NotUnsettableChar().getDefaultValue());
  }

  public void testNotUnsettableWithDefaultDate() throws Exception
  {
    testIsSet(getModel2Factory().createNotUnsettableWithDefault(), getModel2Package().getNotUnsettableWithDefault_NotUnsettableDate(), DATE);
  }

  public void testNotUnsettableWithDefaultDate_SetDefault() throws Exception
  {
    testIsSet(getModel2Factory().createNotUnsettableWithDefault(), getModel2Package().getNotUnsettableWithDefault_NotUnsettableDate(),
        getModel2Package().getNotUnsettableWithDefault_NotUnsettableDate().getDefaultValue());
  }

  public void testNotUnsettableWithDefaultDouble() throws Exception
  {
    testIsSet(getModel2Factory().createNotUnsettableWithDefault(), getModel2Package().getNotUnsettableWithDefault_NotUnsettableDouble(), 15.03d);
  }

  public void testNotUnsettableWithDefaultDouble2() throws Exception
  {
    testIsSet(getModel2Factory().createNotUnsettableWithDefault(), getModel2Package().getNotUnsettableWithDefault_NotUnsettableDouble(),
        Double.valueOf(15.03d));
  }

  public void testNotUnsettableWithDefaultDouble_SetDefault() throws Exception
  {
    testIsSet(getModel2Factory().createNotUnsettableWithDefault(), getModel2Package().getNotUnsettableWithDefault_NotUnsettableDouble(),
        getModel2Package().getNotUnsettableWithDefault_NotUnsettableDouble().getDefaultValue());
  }

  public void testNotUnsettableWithDefaultVAT() throws Exception
  {
    testIsSet(getModel2Factory().createNotUnsettableWithDefault(), getModel2Package().getNotUnsettableWithDefault_NotUnsettableVAT(), VAT.VAT7);
  }

  public void testNotUnsettableWithDefaultVAT_SetDefault() throws Exception
  {
    testIsSet(getModel2Factory().createNotUnsettableWithDefault(), getModel2Package().getNotUnsettableWithDefault_NotUnsettableVAT(),
        getModel2Package().getNotUnsettableWithDefault_NotUnsettableVAT().getDefaultValue());
  }

  public void testUnsettableBooleanNoDefault() throws Exception
  {
    testIsSet(getModel2Factory().createUnsettable1(), getModel2Package().getUnsettable1_UnsettableBoolean(), true);
  }

  public void testUnsettableBooleanNoDefault2() throws Exception
  {
    testIsSet(getModel2Factory().createUnsettable1(), getModel2Package().getUnsettable1_UnsettableBoolean(), Boolean.valueOf(false));
  }

  public void testUnsettableBooleanNoDefault_SetDefault() throws Exception
  {
    testIsSet(getModel2Factory().createUnsettable1(), getModel2Package().getUnsettable1_UnsettableBoolean(),
        getModel2Package().getUnsettable1_UnsettableBoolean().getDefaultValue());
  }

  public void testUnsettableByteNoDefault() throws Exception
  {
    testIsSet(getModel2Factory().createUnsettable1(), getModel2Package().getUnsettable1_UnsettableByte(), (byte)10);
  }

  public void testUnsettableByteNoDefault2() throws Exception
  {
    testIsSet(getModel2Factory().createUnsettable1(), getModel2Package().getUnsettable1_UnsettableByte(), Byte.valueOf((byte)25));
  }

  public void testUnsettableByteNoDefault_SetDefault() throws Exception
  {
    testIsSet(getModel2Factory().createUnsettable1(), getModel2Package().getUnsettable1_UnsettableByte(),
        getModel2Package().getUnsettable1_UnsettableByte().getDefaultValue());
  }

  public void testUnsettableCharNoDefault() throws Exception
  {
    testIsSet(getModel2Factory().createUnsettable1(), getModel2Package().getUnsettable1_UnsettableChar(), 'c');
  }

  public void testUnsettableCharNoDefault2() throws Exception
  {
    testIsSet(getModel2Factory().createUnsettable1(), getModel2Package().getUnsettable1_UnsettableChar(), Character.valueOf('c'));
  }

  public void testUnsettableCharNoDefault_SetDefault() throws Exception
  {
    testIsSet(getModel2Factory().createUnsettable1(), getModel2Package().getUnsettable1_UnsettableChar(),
        getModel2Package().getUnsettable1_UnsettableChar().getDefaultValue());
  }

  public void testUnsettableDateNoDefault() throws Exception
  {
    testIsSet(getModel2Factory().createUnsettable1(), getModel2Package().getUnsettable1_UnsettableDate(), DATE);
  }

  public void testUnsettableDoubleNoDefault() throws Exception
  {
    testIsSet(getModel2Factory().createUnsettable1(), getModel2Package().getUnsettable1_UnsettableDouble(), 15.03d);
  }

  public void testUnsettableDoubleNoDefault2() throws Exception
  {
    testIsSet(getModel2Factory().createUnsettable1(), getModel2Package().getUnsettable1_UnsettableDouble(), Double.valueOf(19.79));
  }

  public void testUnsettableDoubleNoDefault_SetDefault() throws Exception
  {
    testIsSet(getModel2Factory().createUnsettable1(), getModel2Package().getUnsettable1_UnsettableDouble(),
        getModel2Package().getUnsettable1_UnsettableDouble().getDefaultValue());
  }

  public void testUnsettableFloatNoDefault() throws Exception
  {
    testIsSet(getModel2Factory().createUnsettable1(), getModel2Package().getUnsettable1_UnsettableFloat(), 15.03f);
  }

  public void testUnsettableFloatNoDefault2() throws Exception
  {
    testIsSet(getModel2Factory().createUnsettable1(), getModel2Package().getUnsettable1_UnsettableFloat(), Float.valueOf(19.79f));
  }

  public void testUnsettableFloatNoDefault_SetDefault() throws Exception
  {
    testIsSet(getModel2Factory().createUnsettable1(), getModel2Package().getUnsettable1_UnsettableFloat(),
        getModel2Package().getUnsettable1_UnsettableFloat().getDefaultValue());
  }

  public void testUnsettableIntNoDefault() throws Exception
  {
    testIsSet(getModel2Factory().createUnsettable1(), getModel2Package().getUnsettable1_UnsettableInt(), 15);
  }

  public void testUnsettableIntNoDefault2() throws Exception
  {
    testIsSet(getModel2Factory().createUnsettable1(), getModel2Package().getUnsettable1_UnsettableInt(), Integer.valueOf(15));
  }

  public void testUnsettableIntNoDefault_SetDefault() throws Exception
  {
    testIsSet(getModel2Factory().createUnsettable1(), getModel2Package().getUnsettable1_UnsettableInt(),
        getModel2Package().getUnsettable1_UnsettableInt().getDefaultValue());
  }

  public void testUnsettableLongNoDefault() throws Exception
  {
    testIsSet(getModel2Factory().createUnsettable1(), getModel2Package().getUnsettable1_UnsettableLong(), 15L);
  }

  public void testUnsettableShortNoDefault() throws Exception
  {
    testIsSet(getModel2Factory().createUnsettable1(), getModel2Package().getUnsettable1_UnsettableShort(), (short)15);
  }

  public void testUnsettableShortNoDefault2() throws Exception
  {
    testIsSet(getModel2Factory().createUnsettable1(), getModel2Package().getUnsettable1_UnsettableShort(), Short.valueOf((short)15));
  }

  public void testUnsettableShortNoDefault_SetDefault() throws Exception
  {
    testIsSet(getModel2Factory().createUnsettable1(), getModel2Package().getUnsettable1_UnsettableShort(),
        getModel2Package().getUnsettable1_UnsettableShort().getDefaultValue());
  }

  public void testUnsettableStringNoDefault() throws Exception
  {
    testIsSet(getModel2Factory().createUnsettable1(), getModel2Package().getUnsettable1_UnsettableString(), "Martin");
  }

  public void testUnsettableStringNoDefault2() throws Exception
  {
    testIsSet(getModel2Factory().createUnsettable1(), getModel2Package().getUnsettable1_UnsettableString(), new String("Martin"));
  }

  public void testUnsettableVATNoDefault() throws Exception
  {
    testIsSet(getModel2Factory().createUnsettable1(), getModel2Package().getUnsettable1_UnsettableVAT(), VAT.VAT7);
  }

  public void testUnsettableVATNoDefault_SetDefault() throws Exception
  {
    testIsSet(getModel2Factory().createUnsettable1(), getModel2Package().getUnsettable1_UnsettableVAT(),
        getModel2Package().getUnsettable1_UnsettableVAT().getDefaultValue());
  }

  public void testUnsettableWithDefaultBoolean() throws Exception
  {
    testIsSet(getModel2Factory().createUnsettable2WithDefault(), getModel2Package().getUnsettable2WithDefault_UnsettableBoolean(), true);
  }

  public void testUnsettableWithDefaultBoolean2() throws Exception
  {
    testIsSet(getModel2Factory().createUnsettable2WithDefault(), getModel2Package().getUnsettable2WithDefault_UnsettableBoolean(), Boolean.valueOf(false));
  }

  public void testUnsettableWithDefaultByte() throws Exception
  {
    testIsSet(getModel2Factory().createUnsettable2WithDefault(), getModel2Package().getUnsettable2WithDefault_UnsettableByte(), (byte)10);
  }

  public void testUnsettableWithDefaultByte2() throws Exception
  {
    testIsSet(getModel2Factory().createUnsettable2WithDefault(), getModel2Package().getUnsettable2WithDefault_UnsettableByte(), Byte.valueOf((byte)25));
  }

  public void testUnsettableWithDefaultByte_SetDefault() throws Exception
  {
    testIsSet(getModel2Factory().createUnsettable2WithDefault(), getModel2Package().getUnsettable2WithDefault_UnsettableByte(),
        getModel2Package().getUnsettable2WithDefault_UnsettableByte().getDefaultValue());
  }

  public void testUnsettableWithDefaultChar() throws Exception
  {
    testIsSet(getModel2Factory().createUnsettable2WithDefault(), getModel2Package().getUnsettable2WithDefault_UnsettableChar(), 'c');
  }

  public void testUnsettableWithDefaultCharNo() throws Exception
  {
    testIsSet(getModel2Factory().createUnsettable2WithDefault(), getModel2Package().getUnsettable2WithDefault_UnsettableChar(), Character.valueOf('c'));
  }

  public void testUnsettableWithDefaultChar_SetDefault() throws Exception
  {
    testIsSet(getModel2Factory().createUnsettable2WithDefault(), getModel2Package().getUnsettable2WithDefault_UnsettableChar(),
        getModel2Package().getUnsettable2WithDefault_UnsettableChar().getDefaultValue());
  }

  public void testUnsettableWithDefaultDate() throws Exception
  {
    testIsSet(getModel2Factory().createUnsettable2WithDefault(), getModel2Package().getUnsettable2WithDefault_UnsettableDate(), DATE);
  }

  public void testUnsettableWithDefaultDate_SetDefault() throws Exception
  {
    testIsSet(getModel2Factory().createUnsettable2WithDefault(), getModel2Package().getUnsettable2WithDefault_UnsettableDate(),
        getModel2Package().getUnsettable2WithDefault_UnsettableDate().getDefaultValue());
  }

  public void testUnsettableWithDefaultDouble() throws Exception
  {
    testIsSet(getModel2Factory().createUnsettable2WithDefault(), getModel2Package().getUnsettable2WithDefault_UnsettableDouble(), 15.03d);
  }

  public void testUnsettableWithDefaultDouble2() throws Exception
  {
    testIsSet(getModel2Factory().createUnsettable2WithDefault(), getModel2Package().getUnsettable2WithDefault_UnsettableDouble(), Double.valueOf(19.79));
  }

  public void testUnsettableWithDefaultDouble_SetDefault() throws Exception
  {
    testIsSet(getModel2Factory().createUnsettable2WithDefault(), getModel2Package().getUnsettable2WithDefault_UnsettableDouble(),
        getModel2Package().getUnsettable2WithDefault_UnsettableDouble().getDefaultValue());
  }

  public void testUnsettableWithDefaultFloat() throws Exception
  {
    testIsSet(getModel2Factory().createUnsettable2WithDefault(), getModel2Package().getUnsettable2WithDefault_UnsettableFloat(), 15.03f);
  }

  public void testUnsettableWithDefaultFloat2() throws Exception
  {
    testIsSet(getModel2Factory().createUnsettable2WithDefault(), getModel2Package().getUnsettable2WithDefault_UnsettableFloat(), Float.valueOf(19.79f));
  }

  public void testUnsettableWithDefaultFloat_SetDefault() throws Exception
  {
    testIsSet(getModel2Factory().createUnsettable2WithDefault(), getModel2Package().getUnsettable2WithDefault_UnsettableFloat(),
        getModel2Package().getUnsettable2WithDefault_UnsettableFloat().getDefaultValue());
  }

  public void testUnsettableWithDefaultInt() throws Exception
  {
    testIsSet(getModel2Factory().createUnsettable2WithDefault(), getModel2Package().getUnsettable2WithDefault_UnsettableInt(), 15);
  }

  public void testUnsettableWithDefaultInt2() throws Exception
  {
    testIsSet(getModel2Factory().createUnsettable2WithDefault(), getModel2Package().getUnsettable2WithDefault_UnsettableInt(), Integer.valueOf(15));
  }

  public void testUnsettableWithDefaultInt_SetDefault() throws Exception
  {
    testIsSet(getModel2Factory().createUnsettable2WithDefault(), getModel2Package().getUnsettable2WithDefault_UnsettableInt(),
        getModel2Package().getUnsettable2WithDefault_UnsettableInt().getDefaultValue());
  }

  public void testUnsettableWithDefaultLong() throws Exception
  {
    testIsSet(getModel2Factory().createUnsettable2WithDefault(), getModel2Package().getUnsettable2WithDefault_UnsettableLong(), 15L);
  }

  public void testUnsettableWithDefaultShort() throws Exception
  {
    testIsSet(getModel2Factory().createUnsettable2WithDefault(), getModel2Package().getUnsettable2WithDefault_UnsettableShort(), (short)15);
  }

  public void testUnsettableWithDefaultShort2() throws Exception
  {
    testIsSet(getModel2Factory().createUnsettable2WithDefault(), getModel2Package().getUnsettable2WithDefault_UnsettableShort(), Short.valueOf((short)15));
  }

  public void testUnsettableWithDefaultShort_SetDefault() throws Exception
  {
    testIsSet(getModel2Factory().createUnsettable2WithDefault(), getModel2Package().getUnsettable2WithDefault_UnsettableShort(),
        getModel2Package().getUnsettable2WithDefault_UnsettableShort().getDefaultValue());
  }

  public void testUnsettableWithDefaultString() throws Exception
  {
    testIsSet(getModel2Factory().createUnsettable2WithDefault(), getModel2Package().getUnsettable2WithDefault_UnsettableString(), "Martin");
  }

  public void testUnsettableWithDefaultString2() throws Exception
  {
    testIsSet(getModel2Factory().createUnsettable2WithDefault(), getModel2Package().getUnsettable2WithDefault_UnsettableString(), new String("Martin"));
  }

  public void testUnsettableWithDefaultString_SetDefault() throws Exception
  {
    testIsSet(getModel2Factory().createUnsettable2WithDefault(), getModel2Package().getUnsettable2WithDefault_UnsettableString(),
        getModel2Package().getUnsettable2WithDefault_UnsettableString().getDefaultValue());
  }

  public void testUnsettableWithDefaultVAT() throws Exception
  {
    testIsSet(getModel2Factory().createUnsettable2WithDefault(), getModel2Package().getUnsettable2WithDefault_UnsettableVAT(), VAT.VAT7);
  }

  public void testUnsettableWithDefaultVAT_SetDefault() throws Exception
  {
    testIsSet(getModel2Factory().createUnsettable2WithDefault(), getModel2Package().getUnsettable2WithDefault_UnsettableVAT(),
        getModel2Package().getUnsettable2WithDefault_UnsettableVAT().getDefaultValue());
  }

  private void testIsSet(EObject object, EStructuralFeature feature, Object value) throws Exception
  {
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));

      assertEquals(false, object.eIsSet(feature));

      object.eSet(feature, value);

      if (!feature.isUnsettable() && ObjectUtil.equals(feature.getDefaultValue(), value))
      {
        assertEquals(false, object.eIsSet(feature));
      }
      else
      {
        assertEquals(true, object.eIsSet(feature));
      }

      object.eUnset(feature);

      assertEquals(false, object.eIsSet(feature));
      assertEquals(true, ObjectUtil.equals(object.eGet(feature), feature.getDefaultValue()));

      object.eSet(feature, value);

      if (!feature.isUnsettable() && ObjectUtil.equals(feature.getDefaultValue(), value))
      {
        assertEquals(false, object.eIsSet(feature));
      }
      else
      {
        assertEquals(true, object.eIsSet(feature));
      }

      resource.getContents().add(object);

      transaction.commit();

      if (!feature.isUnsettable() && ObjectUtil.equals(feature.getDefaultValue(), value))
      {
        assertEquals(false, object.eIsSet(feature));
      }
      else
      {
        assertEquals(true, object.eIsSet(feature));
      }

      assertEquals(((InternalCDOTransaction)transaction).getStore().isSet((InternalEObject)object, feature), object.eIsSet(feature));

      session.close();

      // ---------- open new session --------------

      session = openSession();

      transaction = session.openTransaction();
      resource = transaction.getResource(getResourcePath("/test1"), true);

      object = resource.getContents().get(0);
      if (!feature.isUnsettable() && ObjectUtil.equals(feature.getDefaultValue(), value))
      {
        assertEquals(false, object.eIsSet(feature));
      }
      else
      {
        assertEquals(true, object.eIsSet(feature));
      }

      object.eUnset(feature);

      assertEquals(false, object.eIsSet(feature));
      assertEquals(true, ObjectUtil.equals(object.eGet(feature), feature.getDefaultValue()));
      assertEquals(((InternalCDOTransaction)transaction).getStore().isSet((InternalEObject)object, feature), object.eIsSet(feature));

      transaction.commit();

      assertEquals(false, object.eIsSet(feature));
      assertEquals(((InternalCDOTransaction)transaction).getStore().isSet((InternalEObject)object, feature), object.eIsSet(feature));

      session.close();

      // ---------- open new session --------------

      session = openSession();

      transaction = session.openTransaction();
      CDOView view = session.openView();
      resource = view.getResource(getResourcePath("/test1"));

      object = resource.getContents().get(0);
      assertEquals(false, object.eIsSet(feature));

      session.close();
    }
  }
}
