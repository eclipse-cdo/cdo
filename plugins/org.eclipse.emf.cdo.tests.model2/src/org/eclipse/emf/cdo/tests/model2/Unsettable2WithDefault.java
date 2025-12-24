/*
 * Copyright (c) 2009-2013, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model2;

import org.eclipse.emf.cdo.tests.model1.VAT;

import org.eclipse.emf.ecore.EObject;

import java.util.Date;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Unsettable2 With Default</b></em>'. <!--
 * end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault#isUnsettableBoolean <em>Unsettable Boolean</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault#getUnsettableByte <em>Unsettable Byte</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault#getUnsettableChar <em>Unsettable Char</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault#getUnsettableDate <em>Unsettable Date</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault#getUnsettableDouble <em>Unsettable Double</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault#getUnsettableFloat <em>Unsettable Float</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault#getUnsettableInt <em>Unsettable Int</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault#getUnsettableLong <em>Unsettable Long</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault#getUnsettableShort <em>Unsettable Short</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault#getUnsettableString <em>Unsettable String</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault#getUnsettableVAT <em>Unsettable VAT</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.tests.model2.Model2Package#getUnsettable2WithDefault()
 * @model
 * @generated
 */
public interface Unsettable2WithDefault extends EObject
{
  /**
   * Returns the value of the '<em><b>Unsettable Boolean</b></em>' attribute.
   * The default value is <code>"true"</code>.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Unsettable Boolean</em>' attribute isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Unsettable Boolean</em>' attribute.
   * @see #isSetUnsettableBoolean()
   * @see #unsetUnsettableBoolean()
   * @see #setUnsettableBoolean(boolean)
   * @see org.eclipse.emf.cdo.tests.model2.Model2Package#getUnsettable2WithDefault_UnsettableBoolean()
   * @model default="true" unsettable="true"
   * @generated
   */
  boolean isUnsettableBoolean();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault#isUnsettableBoolean <em>Unsettable Boolean</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Unsettable Boolean</em>' attribute.
   * @see #isSetUnsettableBoolean()
   * @see #unsetUnsettableBoolean()
   * @see #isUnsettableBoolean()
   * @generated
   */
  void setUnsettableBoolean(boolean value);

  /**
   * Unsets the value of the '{@link org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault#isUnsettableBoolean <em>Unsettable Boolean</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #isSetUnsettableBoolean()
   * @see #isUnsettableBoolean()
   * @see #setUnsettableBoolean(boolean)
   * @generated
   */
  void unsetUnsettableBoolean();

  /**
   * Returns whether the value of the '{@link org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault#isUnsettableBoolean <em>Unsettable Boolean</em>}' attribute is set.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return whether the value of the '<em>Unsettable Boolean</em>' attribute is set.
   * @see #unsetUnsettableBoolean()
   * @see #isUnsettableBoolean()
   * @see #setUnsettableBoolean(boolean)
   * @generated
   */
  boolean isSetUnsettableBoolean();

  /**
   * Returns the value of the '<em><b>Unsettable Byte</b></em>' attribute. The default value is <code>"3"</code>. <!--
   * begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Unsettable Byte</em>' attribute isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Unsettable Byte</em>' attribute.
   * @see #isSetUnsettableByte()
   * @see #unsetUnsettableByte()
   * @see #setUnsettableByte(byte)
   * @see org.eclipse.emf.cdo.tests.model2.Model2Package#getUnsettable2WithDefault_UnsettableByte()
   * @model default="3" unsettable="true"
   * @generated
   */
  byte getUnsettableByte();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault#getUnsettableByte <em>Unsettable Byte</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Unsettable Byte</em>' attribute.
   * @see #isSetUnsettableByte()
   * @see #unsetUnsettableByte()
   * @see #getUnsettableByte()
   * @generated
   */
  void setUnsettableByte(byte value);

  /**
   * Unsets the value of the '{@link org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault#getUnsettableByte <em>Unsettable Byte</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #isSetUnsettableByte()
   * @see #getUnsettableByte()
   * @see #setUnsettableByte(byte)
   * @generated
   */
  void unsetUnsettableByte();

  /**
   * Returns whether the value of the '{@link org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault#getUnsettableByte <em>Unsettable Byte</em>}' attribute is set.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return whether the value of the '<em>Unsettable Byte</em>' attribute is set.
   * @see #unsetUnsettableByte()
   * @see #getUnsettableByte()
   * @see #setUnsettableByte(byte)
   * @generated
   */
  boolean isSetUnsettableByte();

  /**
   * Returns the value of the '<em><b>Unsettable Char</b></em>' attribute.
   * The default value is <code>"\'x\'"</code>.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Unsettable Char</em>' attribute isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Unsettable Char</em>' attribute.
   * @see #isSetUnsettableChar()
   * @see #unsetUnsettableChar()
   * @see #setUnsettableChar(char)
   * @see org.eclipse.emf.cdo.tests.model2.Model2Package#getUnsettable2WithDefault_UnsettableChar()
   * @model default="\'x\'" unsettable="true"
   * @generated
   */
  char getUnsettableChar();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault#getUnsettableChar <em>Unsettable Char</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Unsettable Char</em>' attribute.
   * @see #isSetUnsettableChar()
   * @see #unsetUnsettableChar()
   * @see #getUnsettableChar()
   * @generated
   */
  void setUnsettableChar(char value);

  /**
   * Unsets the value of the '{@link org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault#getUnsettableChar <em>Unsettable Char</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #isSetUnsettableChar()
   * @see #getUnsettableChar()
   * @see #setUnsettableChar(char)
   * @generated
   */
  void unsetUnsettableChar();

  /**
   * Returns whether the value of the '{@link org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault#getUnsettableChar <em>Unsettable Char</em>}' attribute is set.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return whether the value of the '<em>Unsettable Char</em>' attribute is set.
   * @see #unsetUnsettableChar()
   * @see #getUnsettableChar()
   * @see #setUnsettableChar(char)
   * @generated
   */
  boolean isSetUnsettableChar();

  /**
   * Returns the value of the '<em><b>Unsettable Date</b></em>' attribute.
   * The default value is <code>"2009-12-21T15:12:59"</code>.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Unsettable Date</em>' attribute isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Unsettable Date</em>' attribute.
   * @see #isSetUnsettableDate()
   * @see #unsetUnsettableDate()
   * @see #setUnsettableDate(Date)
   * @see org.eclipse.emf.cdo.tests.model2.Model2Package#getUnsettable2WithDefault_UnsettableDate()
   * @model default="2009-12-21T15:12:59" unsettable="true"
   * @generated
   */
  Date getUnsettableDate();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault#getUnsettableDate <em>Unsettable Date</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Unsettable Date</em>' attribute.
   * @see #isSetUnsettableDate()
   * @see #unsetUnsettableDate()
   * @see #getUnsettableDate()
   * @generated
   */
  void setUnsettableDate(Date value);

  /**
   * Unsets the value of the '{@link org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault#getUnsettableDate <em>Unsettable Date</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #isSetUnsettableDate()
   * @see #getUnsettableDate()
   * @see #setUnsettableDate(Date)
   * @generated
   */
  void unsetUnsettableDate();

  /**
   * Returns whether the value of the '{@link org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault#getUnsettableDate <em>Unsettable Date</em>}' attribute is set.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return whether the value of the '<em>Unsettable Date</em>' attribute is set.
   * @see #unsetUnsettableDate()
   * @see #getUnsettableDate()
   * @see #setUnsettableDate(Date)
   * @generated
   */
  boolean isSetUnsettableDate();

  /**
   * Returns the value of the '<em><b>Unsettable Double</b></em>' attribute.
   * The default value is <code>"3.3"</code>.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Unsettable Double</em>' attribute isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Unsettable Double</em>' attribute.
   * @see #isSetUnsettableDouble()
   * @see #unsetUnsettableDouble()
   * @see #setUnsettableDouble(double)
   * @see org.eclipse.emf.cdo.tests.model2.Model2Package#getUnsettable2WithDefault_UnsettableDouble()
   * @model default="3.3" unsettable="true"
   * @generated
   */
  double getUnsettableDouble();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault#getUnsettableDouble <em>Unsettable Double</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Unsettable Double</em>' attribute.
   * @see #isSetUnsettableDouble()
   * @see #unsetUnsettableDouble()
   * @see #getUnsettableDouble()
   * @generated
   */
  void setUnsettableDouble(double value);

  /**
   * Unsets the value of the '{@link org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault#getUnsettableDouble <em>Unsettable Double</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #isSetUnsettableDouble()
   * @see #getUnsettableDouble()
   * @see #setUnsettableDouble(double)
   * @generated
   */
  void unsetUnsettableDouble();

  /**
   * Returns whether the value of the '{@link org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault#getUnsettableDouble <em>Unsettable Double</em>}' attribute is set.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return whether the value of the '<em>Unsettable Double</em>' attribute is set.
   * @see #unsetUnsettableDouble()
   * @see #getUnsettableDouble()
   * @see #setUnsettableDouble(double)
   * @generated
   */
  boolean isSetUnsettableDouble();

  /**
   * Returns the value of the '<em><b>Unsettable Float</b></em>' attribute.
   * The default value is <code>"4.4"</code>.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Unsettable Float</em>' attribute isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Unsettable Float</em>' attribute.
   * @see #isSetUnsettableFloat()
   * @see #unsetUnsettableFloat()
   * @see #setUnsettableFloat(float)
   * @see org.eclipse.emf.cdo.tests.model2.Model2Package#getUnsettable2WithDefault_UnsettableFloat()
   * @model default="4.4" unsettable="true"
   * @generated
   */
  float getUnsettableFloat();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault#getUnsettableFloat <em>Unsettable Float</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Unsettable Float</em>' attribute.
   * @see #isSetUnsettableFloat()
   * @see #unsetUnsettableFloat()
   * @see #getUnsettableFloat()
   * @generated
   */
  void setUnsettableFloat(float value);

  /**
   * Unsets the value of the '{@link org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault#getUnsettableFloat <em>Unsettable Float</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #isSetUnsettableFloat()
   * @see #getUnsettableFloat()
   * @see #setUnsettableFloat(float)
   * @generated
   */
  void unsetUnsettableFloat();

  /**
   * Returns whether the value of the '{@link org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault#getUnsettableFloat <em>Unsettable Float</em>}' attribute is set.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return whether the value of the '<em>Unsettable Float</em>' attribute is set.
   * @see #unsetUnsettableFloat()
   * @see #getUnsettableFloat()
   * @see #setUnsettableFloat(float)
   * @generated
   */
  boolean isSetUnsettableFloat();

  /**
   * Returns the value of the '<em><b>Unsettable Int</b></em>' attribute. The default value is <code>"5"</code>. <!--
   * begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Unsettable Int</em>' attribute isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Unsettable Int</em>' attribute.
   * @see #isSetUnsettableInt()
   * @see #unsetUnsettableInt()
   * @see #setUnsettableInt(int)
   * @see org.eclipse.emf.cdo.tests.model2.Model2Package#getUnsettable2WithDefault_UnsettableInt()
   * @model default="5" unsettable="true"
   * @generated
   */
  int getUnsettableInt();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault#getUnsettableInt <em>Unsettable Int</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Unsettable Int</em>' attribute.
   * @see #isSetUnsettableInt()
   * @see #unsetUnsettableInt()
   * @see #getUnsettableInt()
   * @generated
   */
  void setUnsettableInt(int value);

  /**
   * Unsets the value of the '{@link org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault#getUnsettableInt <em>Unsettable Int</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #isSetUnsettableInt()
   * @see #getUnsettableInt()
   * @see #setUnsettableInt(int)
   * @generated
   */
  void unsetUnsettableInt();

  /**
   * Returns whether the value of the '{@link org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault#getUnsettableInt <em>Unsettable Int</em>}' attribute is set.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return whether the value of the '<em>Unsettable Int</em>' attribute is set.
   * @see #unsetUnsettableInt()
   * @see #getUnsettableInt()
   * @see #setUnsettableInt(int)
   * @generated
   */
  boolean isSetUnsettableInt();

  /**
   * Returns the value of the '<em><b>Unsettable Long</b></em>' attribute. The default value is <code>"6"</code>. <!--
   * begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Unsettable Long</em>' attribute isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Unsettable Long</em>' attribute.
   * @see #isSetUnsettableLong()
   * @see #unsetUnsettableLong()
   * @see #setUnsettableLong(long)
   * @see org.eclipse.emf.cdo.tests.model2.Model2Package#getUnsettable2WithDefault_UnsettableLong()
   * @model default="6" unsettable="true"
   * @generated
   */
  long getUnsettableLong();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault#getUnsettableLong <em>Unsettable Long</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Unsettable Long</em>' attribute.
   * @see #isSetUnsettableLong()
   * @see #unsetUnsettableLong()
   * @see #getUnsettableLong()
   * @generated
   */
  void setUnsettableLong(long value);

  /**
   * Unsets the value of the '{@link org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault#getUnsettableLong <em>Unsettable Long</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #isSetUnsettableLong()
   * @see #getUnsettableLong()
   * @see #setUnsettableLong(long)
   * @generated
   */
  void unsetUnsettableLong();

  /**
   * Returns whether the value of the '{@link org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault#getUnsettableLong <em>Unsettable Long</em>}' attribute is set.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return whether the value of the '<em>Unsettable Long</em>' attribute is set.
   * @see #unsetUnsettableLong()
   * @see #getUnsettableLong()
   * @see #setUnsettableLong(long)
   * @generated
   */
  boolean isSetUnsettableLong();

  /**
   * Returns the value of the '<em><b>Unsettable Short</b></em>' attribute. The default value is <code>"7"</code>. <!--
   * begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Unsettable Short</em>' attribute isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Unsettable Short</em>' attribute.
   * @see #isSetUnsettableShort()
   * @see #unsetUnsettableShort()
   * @see #setUnsettableShort(short)
   * @see org.eclipse.emf.cdo.tests.model2.Model2Package#getUnsettable2WithDefault_UnsettableShort()
   * @model default="7" unsettable="true"
   * @generated
   */
  short getUnsettableShort();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault#getUnsettableShort <em>Unsettable Short</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Unsettable Short</em>' attribute.
   * @see #isSetUnsettableShort()
   * @see #unsetUnsettableShort()
   * @see #getUnsettableShort()
   * @generated
   */
  void setUnsettableShort(short value);

  /**
   * Unsets the value of the '{@link org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault#getUnsettableShort <em>Unsettable Short</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #isSetUnsettableShort()
   * @see #getUnsettableShort()
   * @see #setUnsettableShort(short)
   * @generated
   */
  void unsetUnsettableShort();

  /**
   * Returns whether the value of the '{@link org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault#getUnsettableShort <em>Unsettable Short</em>}' attribute is set.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return whether the value of the '<em>Unsettable Short</em>' attribute is set.
   * @see #unsetUnsettableShort()
   * @see #getUnsettableShort()
   * @see #setUnsettableShort(short)
   * @generated
   */
  boolean isSetUnsettableShort();

  /**
   * Returns the value of the '<em><b>Unsettable String</b></em>' attribute.
   * The default value is <code>"\"eike\""</code>.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Unsettable String</em>' attribute isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Unsettable String</em>' attribute.
   * @see #isSetUnsettableString()
   * @see #unsetUnsettableString()
   * @see #setUnsettableString(String)
   * @see org.eclipse.emf.cdo.tests.model2.Model2Package#getUnsettable2WithDefault_UnsettableString()
   * @model default="\"eike\"" unsettable="true"
   * @generated
   */
  String getUnsettableString();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault#getUnsettableString <em>Unsettable String</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Unsettable String</em>' attribute.
   * @see #isSetUnsettableString()
   * @see #unsetUnsettableString()
   * @see #getUnsettableString()
   * @generated
   */
  void setUnsettableString(String value);

  /**
   * Unsets the value of the '{@link org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault#getUnsettableString <em>Unsettable String</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #isSetUnsettableString()
   * @see #getUnsettableString()
   * @see #setUnsettableString(String)
   * @generated
   */
  void unsetUnsettableString();

  /**
   * Returns whether the value of the '{@link org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault#getUnsettableString <em>Unsettable String</em>}' attribute is set.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return whether the value of the '<em>Unsettable String</em>' attribute is set.
   * @see #unsetUnsettableString()
   * @see #getUnsettableString()
   * @see #setUnsettableString(String)
   * @generated
   */
  boolean isSetUnsettableString();

  /**
   * Returns the value of the '<em><b>Unsettable VAT</b></em>' attribute.
   * The default value is <code>"vat15"</code>.
   * The literals are from the enumeration {@link org.eclipse.emf.cdo.tests.model1.VAT}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Unsettable VAT</em>' attribute isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Unsettable VAT</em>' attribute.
   * @see org.eclipse.emf.cdo.tests.model1.VAT
   * @see #isSetUnsettableVAT()
   * @see #unsetUnsettableVAT()
   * @see #setUnsettableVAT(VAT)
   * @see org.eclipse.emf.cdo.tests.model2.Model2Package#getUnsettable2WithDefault_UnsettableVAT()
   * @model default="vat15" unsettable="true"
   * @generated
   */
  VAT getUnsettableVAT();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault#getUnsettableVAT <em>Unsettable VAT</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Unsettable VAT</em>' attribute.
   * @see org.eclipse.emf.cdo.tests.model1.VAT
   * @see #isSetUnsettableVAT()
   * @see #unsetUnsettableVAT()
   * @see #getUnsettableVAT()
   * @generated
   */
  void setUnsettableVAT(VAT value);

  /**
   * Unsets the value of the '{@link org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault#getUnsettableVAT <em>Unsettable VAT</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #isSetUnsettableVAT()
   * @see #getUnsettableVAT()
   * @see #setUnsettableVAT(VAT)
   * @generated
   */
  void unsetUnsettableVAT();

  /**
   * Returns whether the value of the '{@link org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault#getUnsettableVAT <em>Unsettable VAT</em>}' attribute is set.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return whether the value of the '<em>Unsettable VAT</em>' attribute is set.
   * @see #unsetUnsettableVAT()
   * @see #getUnsettableVAT()
   * @see #setUnsettableVAT(VAT)
   * @generated
   */
  boolean isSetUnsettableVAT();

} // Unsettable2WithDefault
