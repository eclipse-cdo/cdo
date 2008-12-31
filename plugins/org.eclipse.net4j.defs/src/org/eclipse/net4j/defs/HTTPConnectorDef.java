/**
 * Copyright (c) 2004 - 2008 André Dietisheim, Switzerland.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    André Dietisheim - initial API and implementation
 *
 * $Id: HTTPConnectorDef.java,v 1.1 2008-12-31 14:43:20 estepper Exp $
 */
package org.eclipse.net4j.defs;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>HTTP Connector Def</b></em>'. <!-- end-user-doc
 * -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.net4j.defs.HTTPConnectorDef#getUrl <em>Url</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.net4j.defs.Net4jDefsPackage#getHTTPConnectorDef()
 * @model
 * @generated
 */
public interface HTTPConnectorDef extends ConnectorDef
{
  /**
   * Returns the value of the '<em><b>Url</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Url</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Url</em>' attribute.
   * @see #setUrl(String)
   * @see org.eclipse.net4j.defs.Net4jDefsPackage#getHTTPConnectorDef_Url()
   * @model
   * @generated
   */
  String getUrl();

  /**
   * Sets the value of the '{@link org.eclipse.net4j.defs.HTTPConnectorDef#getUrl <em>Url</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Url</em>' attribute.
   * @see #getUrl()
   * @generated
   */
  void setUrl(String value);

} // HTTPConnectorDef
