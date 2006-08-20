/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package testmodel1;


import org.eclipse.emf.common.util.EList;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Empty Ref Node</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link testmodel1.EmptyRefNode#getMoreReferences <em>More References</em>}</li>
 * </ul>
 * </p>
 *
 * @see testmodel1.TestModel1Package#getEmptyRefNode()
 * @model
 * @generated
 */
public interface EmptyRefNode extends TreeNode
{
  /**
   * Returns the value of the '<em><b>More References</b></em>' reference list.
   * The list contents are of type {@link testmodel1.TreeNode}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>More References</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>More References</em>' reference list.
   * @see testmodel1.TestModel1Package#getEmptyRefNode_MoreReferences()
   * @model type="testmodel1.TreeNode"
   * @generated
   */
  EList getMoreReferences();

} // EmptyRefNode