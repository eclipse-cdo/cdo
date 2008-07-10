/**
 * <copyright>
 * </copyright>
 *
 * $Id: RefSingleContained.java,v 1.2 2008-07-10 15:57:40 estepper Exp $
 */
package org.eclipse.emf.cdo.tests.model4;

import org.eclipse.emf.cdo.CDOObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Ref Single Contained</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.model4.RefSingleContained#getElement <em>Element</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.emf.cdo.tests.model4.model4Package#getRefSingleContained()
 * @model
 * @extends CDOObject
 * @generated
 */
public interface RefSingleContained extends CDOObject
{
  /**
   * Returns the value of the '<em><b>Element</b></em>' containment reference. It is bidirectional and its opposite is '
   * {@link org.eclipse.emf.cdo.tests.model4.SingleContainedElement#getParent <em>Parent</em>}'. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Element</em>' containment reference isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Element</em>' containment reference.
   * @see #setElement(SingleContainedElement)
   * @see org.eclipse.emf.cdo.tests.model4.model4Package#getRefSingleContained_Element()
   * @see org.eclipse.emf.cdo.tests.model4.SingleContainedElement#getParent
   * @model opposite="parent" containment="true"
   * @generated
   */
  SingleContainedElement getElement();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model4.RefSingleContained#getElement <em>Element</em>}'
   * containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Element</em>' containment reference.
   * @see #getElement()
   * @generated
   */
  void setElement(SingleContainedElement value);

} // RefSingleContained
