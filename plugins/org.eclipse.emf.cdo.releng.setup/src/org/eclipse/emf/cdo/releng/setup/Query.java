/**
 */
package org.eclipse.emf.cdo.releng.setup;

import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Query</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.Query#getTask <em>Task</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.Query#getSummary <em>Summary</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.Query#getURL <em>URL</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.Query#getAttributes <em>Attributes</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getQuery()
 * @model
 * @generated
 */
public interface Query extends EObject
{
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
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getQuery_Task()
   * @see org.eclipse.emf.cdo.releng.setup.MylynQueriesTask#getQueries
   * @model opposite="queries" transient="false"
   * @generated
   */
  MylynQueriesTask getTask();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.setup.Query#getTask <em>Task</em>}' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Task</em>' container reference.
   * @see #getTask()
   * @generated
   */
  void setTask(MylynQueriesTask value);

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
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getQuery_Summary()
   * @model required="true"
   * @generated
   */
  String getSummary();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.setup.Query#getSummary <em>Summary</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Summary</em>' attribute.
   * @see #getSummary()
   * @generated
   */
  void setSummary(String value);

  /**
   * Returns the value of the '<em><b>URL</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>URL</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>URL</em>' attribute.
   * @see #setURL(String)
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getQuery_URL()
   * @model extendedMetaData="kind='attribute' name='url'"
   * @generated
   */
  String getURL();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.setup.Query#getURL <em>URL</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>URL</em>' attribute.
   * @see #getURL()
   * @generated
   */
  void setURL(String value);

  /**
   * Returns the value of the '<em><b>Attributes</b></em>' map.
   * The key is of type {@link java.lang.String},
   * and the value is of type {@link java.lang.String},
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Attributes</em>' map isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Attributes</em>' map.
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getQuery_Attributes()
   * @model mapType="org.eclipse.emf.cdo.releng.setup.QueryAttribute<org.eclipse.emf.ecore.EString, org.eclipse.emf.ecore.EString>"
   * @generated
   */
  EMap<String, String> getAttributes();

} // Query
