/**
 * <copyright>
 * </copyright>
 *
 * $Id: GenRefMultiNonContained.java,v 1.2 2008-07-10 15:57:40 estepper Exp $
 */
package org.eclipse.emf.cdo.tests.model4;

import org.eclipse.emf.cdo.CDOObject;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Gen Ref Multi Non Contained</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.model4.GenRefMultiNonContained#getElements <em>Elements</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.emf.cdo.tests.model4.model4Package#getGenRefMultiNonContained()
 * @model
 * @extends CDOObject
 * @generated
 */
public interface GenRefMultiNonContained extends CDOObject
{
  /**
   * Returns the value of the '<em><b>Elements</b></em>' reference list. The list contents are of type
   * {@link org.eclipse.emf.ecore.EObject}. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Elements</em>' reference list isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Elements</em>' reference list.
   * @see org.eclipse.emf.cdo.tests.model4.model4Package#getGenRefMultiNonContained_Elements()
   * @model
   * @generated
   */
  EList<EObject> getElements();

} // GenRefMultiNonContained
