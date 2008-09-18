/**
 * <copyright>
 * </copyright>
 *
 * $Id: RefSingleContainedNPL.java,v 1.3 2008-09-18 12:56:50 estepper Exp $
 */
package org.eclipse.emf.cdo.tests.model4;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Ref Single Contained NPL</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.model4.RefSingleContainedNPL#getElement <em>Element</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.emf.cdo.tests.model4.model4Package#getRefSingleContainedNPL()
 * @model
 * @generated
 */
public interface RefSingleContainedNPL extends EObject
{
  /**
   * Returns the value of the '<em><b>Element</b></em>' containment reference. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Element</em>' containment reference isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Element</em>' containment reference.
   * @see #setElement(ContainedElementNoOpposite)
   * @see org.eclipse.emf.cdo.tests.model4.model4Package#getRefSingleContainedNPL_Element()
   * @model containment="true"
   * @generated
   */
  ContainedElementNoOpposite getElement();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model4.RefSingleContainedNPL#getElement <em>Element</em>}'
   * containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Element</em>' containment reference.
   * @see #getElement()
   * @generated
   */
  void setElement(ContainedElementNoOpposite value);

} // RefSingleContainedNPL
