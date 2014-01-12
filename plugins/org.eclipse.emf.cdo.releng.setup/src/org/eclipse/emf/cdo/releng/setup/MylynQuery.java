/**
 */
package org.eclipse.emf.cdo.releng.setup;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Mylyn Query</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.MylynQuery#getTask <em>Task</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.MylynQuery#getSummary <em>Summary</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getMylynQuery()
 * @model abstract="true"
 * @generated
 */
public interface MylynQuery extends EObject
{
  /**
   * Returns the value of the '<em><b>Summary</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Summary</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Summary</em>' attribute.
   * @see #setSummary(String)
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getMylynQuery_Summary()
   * @model required="true"
   * @generated
   */
  String getSummary();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.setup.MylynQuery#getSummary <em>Summary</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Summary</em>' attribute.
   * @see #getSummary()
   * @generated
   */
  void setSummary(String value);

  /**
   * Returns the value of the '<em><b>Task</b></em>' container reference.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.releng.setup.MylynQueriesTask#getQueries <em>Queries</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Task</em>' container reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Task</em>' container reference.
   * @see #setTask(MylynQueriesTask)
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getMylynQuery_Task()
   * @see org.eclipse.emf.cdo.releng.setup.MylynQueriesTask#getQueries
   * @model opposite="queries" transient="false"
   * @generated
   */
  MylynQueriesTask getTask();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.setup.MylynQuery#getTask <em>Task</em>}' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Task</em>' container reference.
   * @see #getTask()
   * @generated
   */
  void setTask(MylynQueriesTask value);

  boolean isNeeded(SetupTaskContext context, String connectorKind, String repositoryURL) throws Exception;

  void perform(SetupTaskContext context, String connectorKind, String repositoryURL) throws Exception;

} // MylynQuery
