/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Andre Dietisheim - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.defs;

import org.eclipse.net4j.defs.ConnectorDef;
import org.eclipse.net4j.util.defs.Def;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>CDO Session Def</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.defs.CDOSessionDef#getConnectorDef <em>Connector Def</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.defs.CDOSessionDef#getRepositoryName <em>Repository Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.defs.CDOSessionDef#getCdoPackageRegistryDef <em>Cdo Package Registry Def</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.defs.CDOSessionDef#isLegacySupportEnabled <em>Legacy Support Enabled</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.defs.CDODefsPackage#getCDOSessionDef()
 * @model
 * @generated
 */
public interface CDOSessionDef extends Def
{
  /**
   * Returns the value of the '<em><b>Connector Def</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Connector Def</em>' reference isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Connector Def</em>' reference.
   * @see #isSetConnectorDef()
   * @see #unsetConnectorDef()
   * @see #setConnectorDef(ConnectorDef)
   * @see org.eclipse.emf.cdo.defs.CDODefsPackage#getCDOSessionDef_ConnectorDef()
   * @model unsettable="true" required="true"
   * @generated
   */
  ConnectorDef getConnectorDef();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.defs.CDOSessionDef#getConnectorDef <em>Connector Def</em>}' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Connector Def</em>' reference.
   * @see #isSetConnectorDef()
   * @see #unsetConnectorDef()
   * @see #getConnectorDef()
   * @generated
   */
  void setConnectorDef(ConnectorDef value);

  /**
   * Unsets the value of the '{@link org.eclipse.emf.cdo.defs.CDOSessionDef#getConnectorDef <em>Connector Def</em>}' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #isSetConnectorDef()
   * @see #getConnectorDef()
   * @see #setConnectorDef(ConnectorDef)
   * @generated
   */
  void unsetConnectorDef();

  /**
   * Returns whether the value of the '{@link org.eclipse.emf.cdo.defs.CDOSessionDef#getConnectorDef <em>Connector Def</em>}' reference is set.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return whether the value of the '<em>Connector Def</em>' reference is set.
   * @see #unsetConnectorDef()
   * @see #getConnectorDef()
   * @see #setConnectorDef(ConnectorDef)
   * @generated
   */
  boolean isSetConnectorDef();

  /**
   * Returns the value of the '<em><b>Repository Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>CDORepositoryInfo Name</em>' attribute isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Repository Name</em>' attribute.
   * @see #setRepositoryName(String)
   * @see org.eclipse.emf.cdo.defs.CDODefsPackage#getCDOSessionDef_RepositoryName()
   * @model unique="false" required="true" ordered="false"
   * @generated
   */
  String getRepositoryName();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.defs.CDOSessionDef#getRepositoryName <em>Repository Name</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Repository Name</em>' attribute.
   * @see #getRepositoryName()
   * @generated
   */
  void setRepositoryName(String value);

  /**
   * Returns the value of the '<em><b>Cdo Package Registry Def</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Cdo Package Registry Def</em>' reference isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Cdo Package Registry Def</em>' reference.
   * @see #setCdoPackageRegistryDef(CDOPackageRegistryDef)
   * @see org.eclipse.emf.cdo.defs.CDODefsPackage#getCDOSessionDef_CdoPackageRegistryDef()
   * @model required="true"
   * @generated
   */
  CDOPackageRegistryDef getCdoPackageRegistryDef();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.defs.CDOSessionDef#getCdoPackageRegistryDef <em>Cdo Package Registry Def</em>}' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Cdo Package Registry Def</em>' reference.
   * @see #getCdoPackageRegistryDef()
   * @generated
   */
  void setCdoPackageRegistryDef(CDOPackageRegistryDef value);

  /**
   * Returns the value of the '<em><b>Legacy Support Enabled</b></em>' attribute.
   * The default value is <code>"false"</code>.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Legacy Support Enabled</em>' attribute isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Legacy Support Enabled</em>' attribute.
   * @see #setLegacySupportEnabled(boolean)
   * @see org.eclipse.emf.cdo.defs.CDODefsPackage#getCDOSessionDef_LegacySupportEnabled()
   * @model default="false" required="true"
   * @generated
   */
  boolean isLegacySupportEnabled();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.defs.CDOSessionDef#isLegacySupportEnabled <em>Legacy Support Enabled</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Legacy Support Enabled</em>' attribute.
   * @see #isLegacySupportEnabled()
   * @generated
   */
  void setLegacySupportEnabled(boolean value);

} // CDOSessionDef
