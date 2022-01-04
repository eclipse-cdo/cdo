/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ecore.dependencies;

import org.eclipse.emf.cdo.ecore.dependencies.util.Graph;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;

import org.eclipse.core.resources.IFile;

import java.util.Comparator;
import java.util.List;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Model</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.ecore.dependencies.Model#getContainer <em>Container</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.ecore.dependencies.Model#getFile <em>File</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.ecore.dependencies.Model#isWorkspace <em>Workspace</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.ecore.dependencies.Model#isExists <em>Exists</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.ecore.dependencies.Model#getNsURI <em>Ns URI</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.ecore.dependencies.Model#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.ecore.dependencies.Model#getElements <em>Elements</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.ecore.dependencies.Model#getOutgoingLinks <em>Outgoing Links</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.ecore.dependencies.Model#getIncomingLinks <em>Incoming Links</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.ecore.dependencies.Model#getBrokenLinks <em>Broken Links</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.ecore.dependencies.Model#getDependencies <em>Dependencies</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.ecore.dependencies.Model#getDependingModels <em>Depending Models</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.ecore.dependencies.Model#getFlatDependencies <em>Flat Dependencies</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.ecore.dependencies.Model#getFlatDependingModels <em>Flat Depending Models</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.ecore.dependencies.DependenciesPackage#getModel()
 * @model
 * @generated
 */
public interface Model extends Addressable
{
  public static final Comparator<Model> TOPOLOGICAL_COMPARATOR = new Comparator<>()
  {
    @Override
    public int compare(Model m1, Model m2)
    {
      if (m1.dependsUpon(m2))
      {
        return -1;
      }

      if (m2.dependsUpon(m1))
      {
        return 1;
      }

      return ALPHABETICAL_COMPARATOR.compare(m1, m2);
    }
  };

  /**
   * Returns the value of the '<em><b>Container</b></em>' container reference.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.ecore.dependencies.ModelContainer#getModels <em>Models</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Container</em>' container reference.
   * @see #setContainer(ModelContainer)
   * @see org.eclipse.emf.cdo.ecore.dependencies.DependenciesPackage#getModel_Container()
   * @see org.eclipse.emf.cdo.ecore.dependencies.ModelContainer#getModels
   * @model opposite="models" required="true" transient="false"
   * @generated
   */
  ModelContainer getContainer();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.ecore.dependencies.Model#getContainer <em>Container</em>}' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Container</em>' container reference.
   * @see #getContainer()
   * @generated
   */
  void setContainer(ModelContainer value);

  /**
   * Returns the value of the '<em><b>File</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>File</em>' attribute.
   * @see org.eclipse.emf.cdo.ecore.dependencies.DependenciesPackage#getModel_File()
   * @model dataType="org.eclipse.emf.cdo.ecore.dependencies.File" transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  IFile getFile();

  /**
   * Returns the value of the '<em><b>Workspace</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Workspace</em>' attribute.
   * @see org.eclipse.emf.cdo.ecore.dependencies.DependenciesPackage#getModel_Workspace()
   * @model transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  boolean isWorkspace();

  /**
   * Returns the value of the '<em><b>Exists</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Exists</em>' attribute.
   * @see #setExists(boolean)
   * @see org.eclipse.emf.cdo.ecore.dependencies.DependenciesPackage#getModel_Exists()
   * @model
   * @generated
   */
  boolean isExists();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.ecore.dependencies.Model#isExists <em>Exists</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Exists</em>' attribute.
   * @see #isExists()
   * @generated
   */
  void setExists(boolean value);

  /**
   * Returns the value of the '<em><b>Ns URI</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Ns URI</em>' attribute.
   * @see #setNsURI(String)
   * @see org.eclipse.emf.cdo.ecore.dependencies.DependenciesPackage#getModel_NsURI()
   * @model required="true"
   * @generated
   */
  String getNsURI();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.ecore.dependencies.Model#getNsURI <em>Ns URI</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Ns URI</em>' attribute.
   * @see #getNsURI()
   * @generated
   */
  void setNsURI(String value);

  /**
   * Returns the value of the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Name</em>' attribute.
   * @see #setName(String)
   * @see org.eclipse.emf.cdo.ecore.dependencies.DependenciesPackage#getModel_Name()
   * @model required="true"
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.ecore.dependencies.Model#getName <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>Elements</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.ecore.dependencies.Element}.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.ecore.dependencies.Element#getModel <em>Model</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Elements</em>' containment reference list.
   * @see org.eclipse.emf.cdo.ecore.dependencies.DependenciesPackage#getModel_Elements()
   * @see org.eclipse.emf.cdo.ecore.dependencies.Element#getModel
   * @model opposite="model" containment="true"
   * @generated
   */
  EList<Element> getElements();

  /**
   * Returns the value of the '<em><b>Outgoing Links</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.ecore.dependencies.Link}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Outgoing Links</em>' reference list.
   * @see org.eclipse.emf.cdo.ecore.dependencies.DependenciesPackage#getModel_OutgoingLinks()
   * @model resolveProxies="false" transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  EList<Link> getOutgoingLinks();

  /**
   * Returns the value of the '<em><b>Incoming Links</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.ecore.dependencies.Link}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Incoming Links</em>' reference list.
   * @see org.eclipse.emf.cdo.ecore.dependencies.DependenciesPackage#getModel_IncomingLinks()
   * @model resolveProxies="false" transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  EList<Link> getIncomingLinks();

  /**
   * Returns the value of the '<em><b>Broken Links</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.ecore.dependencies.Link}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Broken Links</em>' reference list.
   * @see org.eclipse.emf.cdo.ecore.dependencies.DependenciesPackage#getModel_BrokenLinks()
   * @model resolveProxies="false" transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  EList<Link> getBrokenLinks();

  /**
   * Returns the value of the '<em><b>Dependencies</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.ecore.dependencies.Model}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Dependencies</em>' reference list.
   * @see org.eclipse.emf.cdo.ecore.dependencies.DependenciesPackage#getModel_Dependencies()
   * @model resolveProxies="false" transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  EList<Model> getDependencies();

  /**
   * Returns the value of the '<em><b>Depending Models</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.ecore.dependencies.Model}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Depending Models</em>' reference list.
   * @see org.eclipse.emf.cdo.ecore.dependencies.DependenciesPackage#getModel_DependingModels()
   * @model resolveProxies="false" transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  EList<Model> getDependingModels();

  /**
   * Returns the value of the '<em><b>Flat Dependencies</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.ecore.dependencies.Model}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Flat Dependencies</em>' reference list.
   * @see org.eclipse.emf.cdo.ecore.dependencies.DependenciesPackage#getModel_FlatDependencies()
   * @model resolveProxies="false" transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  EList<Model> getFlatDependencies();

  /**
   * Returns the value of the '<em><b>Flat Depending Models</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.ecore.dependencies.Model}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Flat Depending Models</em>' reference list.
   * @see org.eclipse.emf.cdo.ecore.dependencies.DependenciesPackage#getModel_FlatDependingModels()
   * @model resolveProxies="false" transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  EList<Model> getFlatDependingModels();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model targetRequired="true"
   * @generated
   */
  boolean dependsUpon(Model target);

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model targetRequired="true"
   * @generated
   */
  void addDependency(Model target);

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model
   * @generated
   */
  boolean hasBrokenLinks();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model uriDataType="org.eclipse.emf.cdo.ecore.dependencies.URI" uriRequired="true"
   * @generated
   */
  Element getElement(URI uri);

  public static List<Model> sortModels(List<Model> models, boolean byDependencies)
  {
    if (byDependencies)
    {
      models = Graph.topologicalSort(models, Model::getDependencies);
    }
    else
    {
      models.sort(ALPHABETICAL_COMPARATOR);
    }

    return models;
  }

} // Model
