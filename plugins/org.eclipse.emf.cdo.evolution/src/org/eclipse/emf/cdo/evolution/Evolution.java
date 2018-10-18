/**
 */
package org.eclipse.emf.cdo.evolution;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;

import java.util.Map;
import java.util.Set;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Evolution</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.evolution.Evolution#isUseEcorePackage <em>Use Ecore Package</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.evolution.Evolution#isUseEresourcePackage <em>Use Eresource Package</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.evolution.Evolution#isUseEtypesPackage <em>Use Etypes Package</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.evolution.Evolution#isUniqueNamespaces <em>Unique Namespaces</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.evolution.Evolution#getModels <em>Models</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.evolution.Evolution#getRootPackages <em>Root Packages</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.evolution.Evolution#getAllPackages <em>All Packages</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.evolution.Evolution#getMissingPackages <em>Missing Packages</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.evolution.Evolution#getReleases <em>Releases</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.evolution.Evolution#getOrderedReleases <em>Ordered Releases</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.evolution.Evolution#getInitialRelease <em>Initial Release</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.evolution.Evolution#getLatestRelease <em>Latest Release</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.evolution.Evolution#getNextReleaseVersion <em>Next Release Version</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.evolution.EvolutionPackage#getEvolution()
 * @model annotation="http://www.eclipse.org/emf/2002/Ecore constraints='NotEmpty PackagesUnique NoMissingPackages IDsUnique'"
 * @generated
 */
public interface Evolution extends ModelSet
{
  /**
   * Returns the value of the '<em><b>Models</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.evolution.Model}.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.evolution.Model#getEvolution <em>Evolution</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Models</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Models</em>' containment reference list.
   * @see org.eclipse.emf.cdo.evolution.EvolutionPackage#getEvolution_Models()
   * @see org.eclipse.emf.cdo.evolution.Model#getEvolution
   * @model opposite="evolution" containment="true" resolveProxies="true"
   * @generated
   */
  EList<Model> getModels();

  /**
   * Returns the value of the '<em><b>Use Ecore Package</b></em>' attribute.
   * The default value is <code>"true"</code>.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Use Ecore Package</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Use Ecore Package</em>' attribute.
   * @see #setUseEcorePackage(boolean)
   * @see org.eclipse.emf.cdo.evolution.EvolutionPackage#getEvolution_UseEcorePackage()
   * @model default="true"
   * @generated
   */
  boolean isUseEcorePackage();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.evolution.Evolution#isUseEcorePackage <em>Use Ecore Package</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Use Ecore Package</em>' attribute.
   * @see #isUseEcorePackage()
   * @generated
   */
  void setUseEcorePackage(boolean value);

  /**
   * Returns the value of the '<em><b>Use Eresource Package</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Use Eresource Package</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Use Eresource Package</em>' attribute.
   * @see #setUseEresourcePackage(boolean)
   * @see org.eclipse.emf.cdo.evolution.EvolutionPackage#getEvolution_UseEresourcePackage()
   * @model
   * @generated
   */
  boolean isUseEresourcePackage();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.evolution.Evolution#isUseEresourcePackage <em>Use Eresource Package</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Use Eresource Package</em>' attribute.
   * @see #isUseEresourcePackage()
   * @generated
   */
  void setUseEresourcePackage(boolean value);

  /**
   * Returns the value of the '<em><b>Use Etypes Package</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Use Etypes Package</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Use Etypes Package</em>' attribute.
   * @see #setUseEtypesPackage(boolean)
   * @see org.eclipse.emf.cdo.evolution.EvolutionPackage#getEvolution_UseEtypesPackage()
   * @model
   * @generated
   */
  boolean isUseEtypesPackage();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.evolution.Evolution#isUseEtypesPackage <em>Use Etypes Package</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Use Etypes Package</em>' attribute.
   * @see #isUseEtypesPackage()
   * @generated
   */
  void setUseEtypesPackage(boolean value);

  /**
   * Returns the value of the '<em><b>Unique Namespaces</b></em>' attribute.
   * The default value is <code>"true"</code>.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Unique Namespaces</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Unique Namespaces</em>' attribute.
   * @see #setUniqueNamespaces(boolean)
   * @see org.eclipse.emf.cdo.evolution.EvolutionPackage#getEvolution_UniqueNamespaces()
   * @model default="true"
   * @generated
   */
  boolean isUniqueNamespaces();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.evolution.Evolution#isUniqueNamespaces <em>Unique Namespaces</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Unique Namespaces</em>' attribute.
   * @see #isUniqueNamespaces()
   * @generated
   */
  void setUniqueNamespaces(boolean value);

  /**
   * Returns the value of the '<em><b>Root Packages</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.ecore.EPackage}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Root Packages</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Root Packages</em>' reference list.
   * @see org.eclipse.emf.cdo.evolution.EvolutionPackage#getEvolution_RootPackages()
   * @model transient="true" changeable="false" volatile="true" derived="true"
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
   * @see org.eclipse.emf.cdo.evolution.EvolutionPackage#getEvolution_AllPackages()
   * @model transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  EList<EPackage> getAllPackages();

  public Map<String, Set<EPackage>> getReleasedPackages();

  /**
   * Returns the value of the '<em><b>Releases</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.evolution.Release}.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.evolution.Release#getEvolution <em>Evolution</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Releases</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Releases</em>' containment reference list.
   * @see org.eclipse.emf.cdo.evolution.EvolutionPackage#getEvolution_Releases()
   * @see org.eclipse.emf.cdo.evolution.Release#getEvolution
   * @model opposite="evolution" containment="true" resolveProxies="true"
   * @generated
   */
  EList<Release> getReleases();

  /**
   * Returns the value of the '<em><b>Ordered Releases</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.evolution.Release}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Ordered Releases</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Ordered Releases</em>' reference list.
   * @see org.eclipse.emf.cdo.evolution.EvolutionPackage#getEvolution_OrderedReleases()
   * @model transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  EList<Release> getOrderedReleases();

  /**
   * Returns the value of the '<em><b>Initial Release</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Initial Release</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Initial Release</em>' reference.
   * @see org.eclipse.emf.cdo.evolution.EvolutionPackage#getEvolution_InitialRelease()
   * @model transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  Release getInitialRelease();

  /**
   * Returns the value of the '<em><b>Latest Release</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Latest Release</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Latest Release</em>' reference.
   * @see org.eclipse.emf.cdo.evolution.EvolutionPackage#getEvolution_LatestRelease()
   * @model transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  Release getLatestRelease();

  /**
   * Returns the value of the '<em><b>Next Release Version</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Next Release Version</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Next Release Version</em>' attribute.
   * @see org.eclipse.emf.cdo.evolution.EvolutionPackage#getEvolution_NextReleaseVersion()
   * @model transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  int getNextReleaseVersion();

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
  Model getModel(String nsURI);

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model uriDataType="org.eclipse.emf.cdo.evolution.URI"
   * @generated
   */
  Model addModel(URI uri);

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
   * @see org.eclipse.emf.cdo.evolution.EvolutionPackage#getEvolution_MissingPackages()
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
  Release getRelease(int version);

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model
   * @generated
   */
  Release createRelease();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model
   * @generated
   */
  void save();

} // Evolution
