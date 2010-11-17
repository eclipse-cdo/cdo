/**
 * <copyright>
 * </copyright>
 *
 * $Id: ModelElement.java,v 1.1 2010-11-17 06:17:26 estepper Exp $
 */
package org.eclipse.emf.cdo.etypes;

import org.eclipse.emf.cdo.CDOObject;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Model Element</b></em>'.
 * 
 * @since 4.0 <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.etypes.ModelElement#getAnnotations <em>Annotations</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.etypes.EtypesPackage#getModelElement()
 * @model abstract="true"
 * @extends CDOObject
 * @generated
 */
public interface ModelElement extends CDOObject
{
  /**
   * Returns the value of the '<em><b>Annotations</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.etypes.Annotation}.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.etypes.Annotation#getModelElement <em>Model Element</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Annotations</em>' containment reference list isn't clear, there really should be more of
   * a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Annotations</em>' containment reference list.
   * @see org.eclipse.emf.cdo.etypes.EtypesPackage#getModelElement_Annotations()
   * @see org.eclipse.emf.cdo.etypes.Annotation#getModelElement
   * @model opposite="modelElement" containment="true"
   * @generated
   */
  EList<Annotation> getAnnotations();

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @model
   * @generated
   */
  Annotation getAnnotation(String source);

} // ModelElement
