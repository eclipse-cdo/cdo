/**
 */
package org.eclipse.emf.cdo.releng.setup;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

import java.util.Set;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Task</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.SetupTask#getRequirements <em>Requirements</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.SetupTask#getRestrictions <em>Restrictions</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.SetupTask#getScope <em>Scope</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.SetupTask#getExcludedTriggers <em>Excluded Triggers</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getSetupTask()
 * @model abstract="true"
 * @generated
 */
public interface SetupTask extends EObject
{
  /**
   * Returns the value of the '<em><b>Requirements</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.releng.setup.SetupTask}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Requirements</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Requirements</em>' reference list.
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getSetupTask_Requirements()
   * @model
   * @generated
   */
  EList<SetupTask> getRequirements();

  /**
   * Returns the value of the '<em><b>Restrictions</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.releng.setup.ConfigurableItem}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Restrictions</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Restrictions</em>' reference list.
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getSetupTask_Restrictions()
   * @model
   * @generated
   */
  EList<ConfigurableItem> getRestrictions();

  /**
   * Returns the value of the '<em><b>Scope</b></em>' attribute.
   * The literals are from the enumeration {@link org.eclipse.emf.cdo.releng.setup.SetupTaskScope}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Scope</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Scope</em>' attribute.
   * @see org.eclipse.emf.cdo.releng.setup.SetupTaskScope
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getSetupTask_Scope()
   * @model transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  SetupTaskScope getScope();

  /**
   * Returns the value of the '<em><b>Excluded Triggers</b></em>' attribute.
   * The default value is <code>""</code>.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Excluded Triggers</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Excluded Triggers</em>' attribute.
   * @see #setExcludedTriggers(Set)
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getSetupTask_ExcludedTriggers()
   * @model default="" dataType="org.eclipse.emf.cdo.releng.setup.TriggerSet" required="true"
   * @generated
   */
  Set<Trigger> getExcludedTriggers();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.setup.SetupTask#getExcludedTriggers <em>Excluded Triggers</em>}' attribute.
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @param value the new value of the '<em>Excluded Triggers</em>' attribute.
   * @see #getExcludedTriggers()
   * @generated
   */
  void setExcludedTriggers(Set<Trigger> value);

  /**
   * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
   * @model
   * @generated
   */
  boolean requires(SetupTask setupTask);

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model kind="operation" dataType="org.eclipse.emf.cdo.releng.setup.TriggerSet" required="true"
   * @generated
   */
  Set<Trigger> getValidTriggers();

  /**
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @model kind="operation" dataType="org.eclipse.emf.cdo.releng.setup.TriggerSet" required="true"
   * @generated
   */
  Set<Trigger> getTriggers();

  Object getOverrideToken();

  boolean isNeeded(SetupTaskContext context) throws Exception;

  void perform(SetupTaskContext context) throws Exception;

  void dispose();

} // SetupTask
