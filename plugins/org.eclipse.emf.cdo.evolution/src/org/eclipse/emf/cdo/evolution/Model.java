/**
 */
package org.eclipse.emf.cdo.evolution;

import org.eclipse.emf.cdo.CDOObject;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Model</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.evolution.Model#getEvolution <em>Evolution</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.evolution.Model#getURI <em>URI</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.evolution.Model#getRootPackage <em>Root Package</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.evolution.Model#getAllPackages <em>All Packages</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.evolution.Model#getReferencedPackages <em>Referenced Packages</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.evolution.Model#getMissingPackages <em>Missing Packages</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.evolution.EvolutionPackage#getModel()
 * @model annotation="http://www.eclipse.org/emf/2002/Ecore constraints='ModelLoaded IDs_Exist NamespaceReflectsChange'"
 * @extends CDOObject
 * @generated
 */
public interface Model extends CDOObject
{
  /**
   * Returns the value of the '<em><b>Evolution</b></em>' container reference.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.evolution.Evolution#getModels <em>Models</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Evolution</em>' container reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Evolution</em>' container reference.
   * @see #setEvolution(Evolution)
   * @see org.eclipse.emf.cdo.evolution.EvolutionPackage#getModel_Evolution()
   * @see org.eclipse.emf.cdo.evolution.Evolution#getModels
   * @model opposite="models" required="true" transient="false"
   * @generated
   */
  Evolution getEvolution();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.evolution.Model#getEvolution <em>Evolution</em>}' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Evolution</em>' container reference.
   * @see #getEvolution()
   * @generated
   */
  void setEvolution(Evolution value);

  /**
   * Returns the value of the '<em><b>URI</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>URI</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>URI</em>' attribute.
   * @see #setURI(URI)
   * @see org.eclipse.emf.cdo.evolution.EvolutionPackage#getModel_URI()
   * @model dataType="org.eclipse.emf.cdo.evolution.URI"
   *        extendedMetaData="kind='attribute' name='uri'"
   * @generated
   */
  URI getURI();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.evolution.Model#getURI <em>URI</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>URI</em>' attribute.
   * @see #getURI()
   * @generated
   */
  void setURI(URI value);

  /**
   * Returns the value of the '<em><b>Root Package</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Root Package</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Root Package</em>' reference.
   * @see org.eclipse.emf.cdo.evolution.EvolutionPackage#getModel_RootPackage()
   * @model transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  EPackage getRootPackage();

  /**
   * Returns the value of the '<em><b>All Packages</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.ecore.EPackage}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>All Packages</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>All Packages</em>' reference list.
   * @see org.eclipse.emf.cdo.evolution.EvolutionPackage#getModel_AllPackages()
   * @model transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  EList<EPackage> getAllPackages();

  /**
   * Returns the value of the '<em><b>Referenced Packages</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.ecore.EPackage}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Referenced Packages</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Referenced Packages</em>' reference list.
   * @see org.eclipse.emf.cdo.evolution.EvolutionPackage#getModel_ReferencedPackages()
   * @model transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  EList<EPackage> getReferencedPackages();

  /**
   * Returns the value of the '<em><b>Missing Packages</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.ecore.EPackage}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Missing Packages</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Missing Packages</em>' reference list.
   * @see org.eclipse.emf.cdo.evolution.EvolutionPackage#getModel_MissingPackages()
   * @model transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  EList<EPackage> getMissingPackages();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model
   * @generated
   */
  boolean ensureIDs();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model
   * @generated
   */
  EPackage getPackage(String nsURI);

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model
   * @generated
   */
  void save();

  public ModelStatus getStatus();

  /**
   * @author Eike Stepper
   */
  public enum ModelStatus
  {
    NO_URI, NO_RESOURCE_SET, RESOURCE_NOT_FOUND, LOAD_PROBLEM, CONTENT_PROBLEM, OK
  }

} // Model
