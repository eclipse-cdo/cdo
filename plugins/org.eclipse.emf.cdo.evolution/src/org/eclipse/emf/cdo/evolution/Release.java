/**
 */
package org.eclipse.emf.cdo.evolution;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EPackage;

import java.util.Date;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Release</b></em>'.
 * @extends Comparable<Release>
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.evolution.Release#getEvolution <em>Evolution</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.evolution.Release#getVersion <em>Version</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.evolution.Release#getDate <em>Date</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.evolution.Release#getNextRelease <em>Next Release</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.evolution.Release#getPreviousRelease <em>Previous Release</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.evolution.Release#getRootPackages <em>Root Packages</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.evolution.Release#getAllPackages <em>All Packages</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.evolution.EvolutionPackage#getRelease()
 * @model
 * @generated
 */
public interface Release extends ModelSet, Comparable<Release>
{
  /**
   * Returns the value of the '<em><b>Evolution</b></em>' container reference.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.evolution.Evolution#getReleases <em>Releases</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Evolution</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Evolution</em>' container reference.
   * @see #setEvolution(Evolution)
   * @see org.eclipse.emf.cdo.evolution.EvolutionPackage#getRelease_Evolution()
   * @see org.eclipse.emf.cdo.evolution.Evolution#getReleases
   * @model opposite="releases" required="true" transient="false"
   * @generated
   */
  Evolution getEvolution();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.evolution.Release#getEvolution <em>Evolution</em>}' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Evolution</em>' container reference.
   * @see #getEvolution()
   * @generated
   */
  void setEvolution(Evolution value);

  /**
   * Returns the value of the '<em><b>Date</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Date</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Date</em>' attribute.
   * @see #setDate(Date)
   * @see org.eclipse.emf.cdo.evolution.EvolutionPackage#getRelease_Date()
   * @model required="true"
   * @generated
   */
  Date getDate();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.evolution.Release#getDate <em>Date</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Date</em>' attribute.
   * @see #getDate()
   * @generated
   */
  void setDate(Date value);

  /**
   * Returns the value of the '<em><b>Next Release</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Next Release</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Next Release</em>' reference.
   * @see org.eclipse.emf.cdo.evolution.EvolutionPackage#getRelease_NextRelease()
   * @model transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  Release getNextRelease();

  /**
   * Returns the value of the '<em><b>Previous Release</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Previous Release</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Previous Release</em>' reference.
   * @see org.eclipse.emf.cdo.evolution.EvolutionPackage#getRelease_PreviousRelease()
   * @model transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  Release getPreviousRelease();

  /**
   * Returns the value of the '<em><b>Version</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Version</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Version</em>' attribute.
   * @see #setVersion(int)
   * @see org.eclipse.emf.cdo.evolution.EvolutionPackage#getRelease_Version()
   * @model required="true"
   * @generated
   */
  int getVersion();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.evolution.Release#getVersion <em>Version</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Version</em>' attribute.
   * @see #getVersion()
   * @generated
   */
  void setVersion(int value);

  /**
   * Returns the value of the '<em><b>Root Packages</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.ecore.EPackage}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Root Packages</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Root Packages</em>' containment reference list.
   * @see org.eclipse.emf.cdo.evolution.EvolutionPackage#getRelease_RootPackages()
   * @model containment="true"
   * @generated
   */
  EList<EPackage> getRootPackages();

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
   * @see org.eclipse.emf.cdo.evolution.EvolutionPackage#getRelease_AllPackages()
   * @model transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  EList<EPackage> getAllPackages();

} // Release
