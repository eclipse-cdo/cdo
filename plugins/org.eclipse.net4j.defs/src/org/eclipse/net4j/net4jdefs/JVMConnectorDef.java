/**
 * <copyright>
 * </copyright>
 *
 * $Id: JVMConnectorDef.java,v 1.2 2008-12-30 08:43:13 estepper Exp $
 */
package org.eclipse.net4j.net4jdefs;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>JVM Connector Def</b></em>'. <!-- end-user-doc
 * -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.net4j.net4jdefs.JVMConnectorDef#getName <em>Name</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.net4j.net4jdefs.Net4jDefsPackage#getJVMConnectorDef()
 * @model
 * @generated
 */
public interface JVMConnectorDef extends ConnectorDef
{

  /**
   * Returns the value of the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Name</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Name</em>' attribute.
   * @see #setName(String)
   * @see org.eclipse.net4j.net4jdefs.Net4jDefsPackage#getJVMConnectorDef_Name()
   * @model
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.eclipse.net4j.net4jdefs.JVMConnectorDef#getName <em>Name</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);
} // JVMConnectorDef
