/**
 */
package org.eclipse.emf.cdo.tests.model3;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Diagram</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.Diagram#getEdges <em>Edges</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.Diagram#getEdgeTargets <em>Edge Targets</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.tests.model3.Model3Package#getDiagram()
 * @model
 * @generated
 */
public interface Diagram extends EObject
{
  /**
   * Returns the value of the '<em><b>Edges</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.tests.model3.Edge}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Edges</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Edges</em>' containment reference list.
   * @see org.eclipse.emf.cdo.tests.model3.Model3Package#getDiagram_Edges()
   * @model containment="true"
   * @generated
   */
  EList<Edge> getEdges();

  /**
   * Returns the value of the '<em><b>Edge Targets</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.tests.model3.EdgeTarget}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Edge Targets</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Edge Targets</em>' containment reference list.
   * @see org.eclipse.emf.cdo.tests.model3.Model3Package#getDiagram_EdgeTargets()
   * @model containment="true"
   * @generated
   */
  EList<EdgeTarget> getEdgeTargets();

} // Diagram
