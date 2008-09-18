/**
 * <copyright>
 * </copyright>
 *
 * $Id: RefMultiContainedNPL.java,v 1.3 2008-09-18 12:56:50 estepper Exp $
 */
package org.eclipse.emf.cdo.tests.model4;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Ref Multi Contained NPL</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.model4.RefMultiContainedNPL#getElements <em>Elements</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.emf.cdo.tests.model4.model4Package#getRefMultiContainedNPL()
 * @model
 * @generated
 */
public interface RefMultiContainedNPL extends EObject
{
  /**
   * Returns the value of the '<em><b>Elements</b></em>' containment reference list. The list contents are of type
   * {@link org.eclipse.emf.cdo.tests.model4.ContainedElementNoOpposite}. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Elements</em>' containment reference list isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Elements</em>' containment reference list.
   * @see org.eclipse.emf.cdo.tests.model4.model4Package#getRefMultiContainedNPL_Elements()
   * @model containment="true"
   * @generated
   */
  EList<ContainedElementNoOpposite> getElements();

} // RefMultiContainedNPL
