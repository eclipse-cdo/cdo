/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm;

import org.eclipse.emf.cdo.common.branch.CDOBranchPointRef;
import org.eclipse.emf.cdo.common.branch.CDOBranchRef;

import org.eclipse.emf.common.util.EList;

import java.util.function.Consumer;

/**
 * <!-- begin-user-doc --> A representation of the model object
 * '<em><b>Stream</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.Stream#getModule <em>Module</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.Stream#getBase <em>Base</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.Stream#getStartTimeStamp <em>Start Time Stamp</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.Stream#getMajorVersion <em>Major Version</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.Stream#getMinorVersion <em>Minor Version</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.Stream#getCodeName <em>Code Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.Stream#getAllowedChanges <em>Allowed Changes</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.Stream#getMode <em>Mode</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.Stream#getDevelopmentBranch <em>Development Branch</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.Stream#getMaintenanceBranch <em>Maintenance Branch</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.Stream#getContents <em>Contents</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.Stream#getMaintenanceTimeStamp <em>Maintenance Time Stamp</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.lm.LMPackage#getStream()
 * @model
 * @generated
 */
public interface Stream extends FloatingBaseline
{
  /**
   * Returns the value of the '<em><b>Module</b></em>' container reference.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.lm.Module#getStreams <em>Streams</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the value of the '<em>Module</em>' container reference.
   * @see #setModule(org.eclipse.emf.cdo.lm.Module)
   * @see org.eclipse.emf.cdo.lm.LMPackage#getStream_Module()
   * @see org.eclipse.emf.cdo.lm.Module#getStreams
   * @model opposite="streams" required="true" transient="false"
   * @generated
   */
  @Override
  org.eclipse.emf.cdo.lm.Module getModule();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.Stream#getModule <em>Module</em>}' container reference.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @param value the new value of the '<em>Module</em>' container reference.
   * @see #getModule()
   * @generated
   */
  void setModule(org.eclipse.emf.cdo.lm.Module value);

  /**
   * Returns the value of the '<em><b>Base</b></em>' reference. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the value of the '<em>Base</em>' reference.
   * @see #setBase(Drop)
   * @see org.eclipse.emf.cdo.lm.LMPackage#getStream_Base()
   * @model
   * @generated
   */
  @Override
  Drop getBase();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.Stream#getBase <em>Base</em>}' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Base</em>' reference.
   * @see #getBase()
   * @generated
   */
  void setBase(Drop value);

  /**
   * Returns the value of the '<em><b>Start Time Stamp</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the value of the '<em>Start Time Stamp</em>' attribute.
   * @see #setStartTimeStamp(long)
   * @see org.eclipse.emf.cdo.lm.LMPackage#getStream_StartTimeStamp()
   * @model required="true"
   * @generated
   */
  long getStartTimeStamp();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.Stream#getStartTimeStamp <em>Start Time Stamp</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Start Time Stamp</em>' attribute.
   * @see #getStartTimeStamp()
   * @generated
   */
  void setStartTimeStamp(long value);

  /**
   * Returns the value of the '<em><b>Major Version</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the value of the '<em>Major Version</em>' attribute.
   * @see #setMajorVersion(int)
   * @see org.eclipse.emf.cdo.lm.LMPackage#getStream_MajorVersion()
   * @model required="true"
   * @generated
   */
  int getMajorVersion();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.Stream#getMajorVersion <em>Major Version</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Major Version</em>' attribute.
   * @see #getMajorVersion()
   * @generated
   */
  void setMajorVersion(int value);

  /**
   * Returns the value of the '<em><b>Minor Version</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the value of the '<em>Minor Version</em>' attribute.
   * @see #setMinorVersion(int)
   * @see org.eclipse.emf.cdo.lm.LMPackage#getStream_MinorVersion()
   * @model required="true"
   * @generated
   */
  int getMinorVersion();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.Stream#getMinorVersion <em>Minor Version</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Minor Version</em>' attribute.
   * @see #getMinorVersion()
   * @generated
   */
  void setMinorVersion(int value);

  /**
   * Returns the value of the '<em><b>Code Name</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the value of the '<em>Code Name</em>' attribute.
   * @see #setCodeName(String)
   * @see org.eclipse.emf.cdo.lm.LMPackage#getStream_CodeName()
   * @model
   * @generated
   */
  String getCodeName();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.Stream#getCodeName <em>Code Name</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Code Name</em>' attribute.
   * @see #getCodeName()
   * @generated
   */
  void setCodeName(String value);

  /**
   * Returns the value of the '<em><b>Allowed Changes</b></em>' attribute.
   * The default value is <code>"Minor"</code>.
   * The literals are from the enumeration {@link org.eclipse.emf.cdo.lm.Impact}.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @return the value of the '<em>Allowed Changes</em>' attribute.
   * @see org.eclipse.emf.cdo.lm.Impact
   * @see #setAllowedChanges(Impact)
   * @see org.eclipse.emf.cdo.lm.LMPackage#getStream_AllowedChanges()
   * @model default="Minor"
   * @generated
   */
  Impact getAllowedChanges();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.Stream#getAllowedChanges <em>Allowed Changes</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Allowed Changes</em>' attribute.
   * @see org.eclipse.emf.cdo.lm.Impact
   * @see #getAllowedChanges()
   * @generated
   */
  void setAllowedChanges(Impact value);

  /**
   * Returns the value of the '<em><b>Contents</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.lm.Baseline}.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.lm.Baseline#getStream <em>Stream</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the value of the '<em>Contents</em>' containment reference list.
   * @see org.eclipse.emf.cdo.lm.LMPackage#getStream_Contents()
   * @see org.eclipse.emf.cdo.lm.Baseline#getStream
   * @model opposite="stream" containment="true"
   * @generated
   */
  EList<Baseline> getContents();

  /**
   * Returns the value of the '<em><b>Maintenance Time Stamp</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the value of the '<em>Maintenance Time Stamp</em>' attribute.
   * @see #setMaintenanceTimeStamp(long)
   * @see org.eclipse.emf.cdo.lm.LMPackage#getStream_MaintenanceTimeStamp()
   * @model
   * @generated
   */
  long getMaintenanceTimeStamp();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.Stream#getMaintenanceTimeStamp <em>Maintenance Time Stamp</em>}' attribute.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @param value the new value of the '<em>Maintenance Time Stamp</em>' attribute.
   * @see #getMaintenanceTimeStamp()
   * @generated
   */
  void setMaintenanceTimeStamp(long value);

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @model baselineRequired="true"
   * @generated
   */
  int insertContent(Baseline baseline);

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @model dataType="org.eclipse.emf.cdo.etypes.BranchPointRef"
   * @generated
   */
  CDOBranchPointRef getBranchPoint(long timeStamp);

  /**
   * Returns the value of the '<em><b>Mode</b></em>' attribute. The literals are
   * from the enumeration {@link org.eclipse.emf.cdo.lm.StreamMode}. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the value of the '<em>Mode</em>' attribute.
   * @see org.eclipse.emf.cdo.lm.StreamMode
   * @see #setMode(StreamMode)
   * @see org.eclipse.emf.cdo.lm.LMPackage#getStream_Mode()
   * @model required="true"
   * @generated
   */
  @SuppressWarnings("javadoc")
  StreamMode getMode();

  /**
   * Returns the value of the '<em><b>Development Branch</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the value of the '<em>Development Branch</em>' attribute.
   * @see #setDevelopmentBranch(CDOBranchRef)
   * @see org.eclipse.emf.cdo.lm.LMPackage#getStream_DevelopmentBranch()
   * @model dataType="org.eclipse.emf.cdo.etypes.BranchRef" required="true"
   * @generated
   */
  CDOBranchRef getDevelopmentBranch();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.Stream#getDevelopmentBranch <em>Development Branch</em>}' attribute.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @param value the new value of the '<em>Development Branch</em>' attribute.
   * @see #getDevelopmentBranch()
   * @generated
   */
  void setDevelopmentBranch(CDOBranchRef value);

  /**
   * Returns the value of the '<em><b>Maintenance Branch</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the value of the '<em>Maintenance Branch</em>' attribute.
   * @see #setMaintenanceBranch(CDOBranchRef)
   * @see org.eclipse.emf.cdo.lm.LMPackage#getStream_MaintenanceBranch()
   * @model dataType="org.eclipse.emf.cdo.etypes.BranchRef"
   * @generated
   */
  CDOBranchRef getMaintenanceBranch();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.Stream#getMaintenanceBranch <em>Maintenance Branch</em>}' attribute.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @param value the new value of the '<em>Maintenance Branch</em>' attribute.
   * @see #getMaintenanceBranch()
   * @generated
   */
  void setMaintenanceBranch(CDOBranchRef value);

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @model kind="operation"
   * @generated
   */
  Drop getFirstRelease();

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @model kind="operation"
   * @generated
   */
  Drop getLastRelease();

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @model kind="operation"
   * @generated
   */
  EList<Drop> getReleases();

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @model kind="operation"
   * @generated
   */
  EList<Change> getBasedChanges();

  public void forEachBaseline(Consumer<Baseline> consumer);

  public Baseline getBaseline(String baselineName);

  public Delivery getDelivery(Change change);

} // Stream
