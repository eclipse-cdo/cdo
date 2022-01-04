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
package org.eclipse.emf.cdo.ecore.dependencies.impl;

import org.eclipse.emf.cdo.ecore.dependencies.DependenciesPackage;
import org.eclipse.emf.cdo.ecore.dependencies.Element;
import org.eclipse.emf.cdo.ecore.dependencies.Link;
import org.eclipse.emf.cdo.ecore.dependencies.Model;
import org.eclipse.emf.cdo.ecore.dependencies.ModelContainer;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.EObjectEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Model</b></em>'.
 * @extends IAdaptable
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.ecore.dependencies.impl.ModelImpl#getUri <em>Uri</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.ecore.dependencies.impl.ModelImpl#getContainer <em>Container</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.ecore.dependencies.impl.ModelImpl#getFile <em>File</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.ecore.dependencies.impl.ModelImpl#isWorkspace <em>Workspace</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.ecore.dependencies.impl.ModelImpl#isExists <em>Exists</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.ecore.dependencies.impl.ModelImpl#getNsURI <em>Ns URI</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.ecore.dependencies.impl.ModelImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.ecore.dependencies.impl.ModelImpl#getElements <em>Elements</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.ecore.dependencies.impl.ModelImpl#getOutgoingLinks <em>Outgoing Links</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.ecore.dependencies.impl.ModelImpl#getIncomingLinks <em>Incoming Links</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.ecore.dependencies.impl.ModelImpl#getBrokenLinks <em>Broken Links</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.ecore.dependencies.impl.ModelImpl#getDependencies <em>Dependencies</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.ecore.dependencies.impl.ModelImpl#getDependingModels <em>Depending Models</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.ecore.dependencies.impl.ModelImpl#getFlatDependencies <em>Flat Dependencies</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.ecore.dependencies.impl.ModelImpl#getFlatDependingModels <em>Flat Depending Models</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ModelImpl extends MinimalEObjectImpl.Container implements Model, IAdaptable
{
  /**
   * The default value of the '{@link #getUri() <em>Uri</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getUri()
   * @generated
   * @ordered
   */
  protected static final URI URI_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getUri() <em>Uri</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getUri()
   * @generated
   * @ordered
   */
  protected URI uri = URI_EDEFAULT;

  /**
   * The default value of the '{@link #getFile() <em>File</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getFile()
   * @generated
   * @ordered
   */
  protected static final IFile FILE_EDEFAULT = null;

  /**
   * The default value of the '{@link #isWorkspace() <em>Workspace</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isWorkspace()
   * @generated
   * @ordered
   */
  protected static final boolean WORKSPACE_EDEFAULT = false;

  /**
   * The default value of the '{@link #isExists() <em>Exists</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isExists()
   * @generated
   * @ordered
   */
  protected static final boolean EXISTS_EDEFAULT = false;

  /**
   * The cached value of the '{@link #isExists() <em>Exists</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isExists()
   * @generated
   * @ordered
   */
  protected boolean exists = EXISTS_EDEFAULT;

  /**
   * The default value of the '{@link #getNsURI() <em>Ns URI</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getNsURI()
   * @generated
   * @ordered
   */
  protected static final String NS_URI_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getNsURI() <em>Ns URI</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getNsURI()
   * @generated
   * @ordered
   */
  protected String nsURI = NS_URI_EDEFAULT;

  /**
   * The default value of the '{@link #getName() <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getName()
   * @generated
   * @ordered
   */
  protected static final String NAME_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getName()
   * @generated
   * @ordered
   */
  protected String name = NAME_EDEFAULT;

  /**
   * The cached value of the '{@link #getElements() <em>Elements</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getElements()
   * @generated
   * @ordered
   */
  protected EList<Element> elements;

  private Boolean brokenLinks;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ModelImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return DependenciesPackage.Literals.MODEL;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public URI getUri()
  {
    return uri;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setUri(URI newUri)
  {
    URI oldUri = uri;
    uri = newUri;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, DependenciesPackage.MODEL__URI, oldUri, uri));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ModelContainer getContainer()
  {
    if (eContainerFeatureID() != DependenciesPackage.MODEL__CONTAINER)
    {
      return null;
    }
    return (ModelContainer)eInternalContainer();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetContainer(ModelContainer newContainer, NotificationChain msgs)
  {
    msgs = eBasicSetContainer((InternalEObject)newContainer, DependenciesPackage.MODEL__CONTAINER, msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setContainer(ModelContainer newContainer)
  {
    if (newContainer != eInternalContainer() || eContainerFeatureID() != DependenciesPackage.MODEL__CONTAINER && newContainer != null)
    {
      if (EcoreUtil.isAncestor(this, newContainer))
      {
        throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
      }
      NotificationChain msgs = null;
      if (eInternalContainer() != null)
      {
        msgs = eBasicRemoveFromContainer(msgs);
      }
      if (newContainer != null)
      {
        msgs = ((InternalEObject)newContainer).eInverseAdd(this, DependenciesPackage.MODEL_CONTAINER__MODELS, ModelContainer.class, msgs);
      }
      msgs = basicSetContainer(newContainer, msgs);
      if (msgs != null)
      {
        msgs.dispatch();
      }
    }
    else if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, DependenciesPackage.MODEL__CONTAINER, newContainer, newContainer));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public IFile getFile()
  {
    URI uri = getUri();
    if (uri.isPlatformResource())
    {
      IPath path = Path.fromPortableString(uri.path()).removeFirstSegments(1);
      IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
      if (file.exists())
      {
        return file;
      }
    }

    return null;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public boolean isWorkspace()
  {
    return getFile() != null;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isExists()
  {
    return exists;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setExists(boolean newExists)
  {
    boolean oldExists = exists;
    exists = newExists;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, DependenciesPackage.MODEL__EXISTS, oldExists, exists));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getNsURI()
  {
    return nsURI;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setNsURI(String newNsURI)
  {
    String oldNsURI = nsURI;
    nsURI = newNsURI;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, DependenciesPackage.MODEL__NS_URI, oldNsURI, nsURI));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getName()
  {
    return name;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setName(String newName)
  {
    String oldName = name;
    name = newName;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, DependenciesPackage.MODEL__NAME, oldName, name));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList<Element> getElements()
  {
    if (elements == null)
    {
      elements = new EObjectContainmentWithInverseEList<>(Element.class, this, DependenciesPackage.MODEL__ELEMENTS, DependenciesPackage.ELEMENT__MODEL);
    }
    return elements;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public EList<Link> getOutgoingLinks()
  {
    EList<Link> result = new EObjectEList<>(Link.class, this, DependenciesPackage.MODEL__OUTGOING_LINKS);
    for (Element element : getElements())
    {
      result.addAll(element.getOutgoingLinks());
    }

    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public EList<Link> getIncomingLinks()
  {
    EList<Link> result = new EObjectEList<>(Link.class, this, DependenciesPackage.MODEL__INCOMING_LINKS);
    for (Element element : getElements())
    {
      result.addAll(element.getIncomingLinks());
    }

    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public EList<Link> getBrokenLinks()
  {
    EList<Link> result = new EObjectEList<>(Link.class, this, DependenciesPackage.ELEMENT__BROKEN_LINKS);
    for (Element element : getElements())
    {
      result.addAll(element.getBrokenLinks());
    }

    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public EList<Model> getDependencies()
  {
    Set<Model> models = new HashSet<>();
    for (Element element : getElements())
    {
      for (Link link : element.getOutgoingLinks())
      {
        Element target = link.getTarget();
        if (target != null)
        {
          models.add(target.getModel());
        }
      }
    }

    EList<Model> result = new EObjectEList<>(Model.class, this, DependenciesPackage.MODEL__DEPENDENCIES);
    result.addAll(models);
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public EList<Model> getDependingModels()
  {
    Set<Model> models = new HashSet<>();
    for (Element element : getElements())
    {
      for (Link link : element.getIncomingLinks())
      {
        models.add(link.getSource().getModel());
      }

    }

    EList<Model> result = new EObjectEList<>(Model.class, this, DependenciesPackage.MODEL__DEPENDING_MODELS);
    result.addAll(models);
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public EList<Model> getFlatDependencies()
  {
    return getFlat(DependenciesPackage.Literals.MODEL__DEPENDENCIES, DependenciesPackage.MODEL__FLAT_DEPENDENCIES);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public EList<Model> getFlatDependingModels()
  {
    return getFlat(DependenciesPackage.Literals.MODEL__DEPENDING_MODELS, DependenciesPackage.MODEL__FLAT_DEPENDING_MODELS);
  }

  private EList<Model> getFlat(EReference sourceReference, int targetReferenceID)
  {
    Set<Model> set = new HashSet<>();
    fill(this, sourceReference, set);

    EList<Model> result = new EObjectEList<>(Model.class, this, targetReferenceID);
    result.addAll(set);
    return result;
  }

  private void fill(Model model, EReference reference, Set<Model> result)
  {
    @SuppressWarnings("unchecked")
    EList<Model> list = (EList<Model>)model.eGet(reference);

    for (Model element : list)
    {
      if (result.add(element))
      {
        fill(element, reference, result);
      }
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public boolean dependsUpon(Model target)
  {
    return dependsUpon(this, target);
  }

  private boolean dependsUpon(ModelImpl source, Model target)
  {
    if (source != target)
    {
      for (Model dependency : getDependencies())
      {
        if (dependency == target || ((ModelImpl)dependency).dependsUpon(source, target))
        {
          return true;
        }
      }
    }

    return false;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public void addDependency(Model target)
  {
    EList<Model> dependencies = getDependencies();
    for (Model dependency : dependencies)
    {
      if (dependency == target)
      {
        return;
      }
    }

    dependencies.add(target);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public boolean hasBrokenLinks()
  {
    if (brokenLinks == null)
    {
      brokenLinks = false;

      for (Element element : getElements())
      {
        if (element.hasBrokenLinks())
        {
          brokenLinks = true;
          break;
        }
      }
    }

    return brokenLinks;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public Element getElement(URI uri)
  {
    URI modelURI = uri.trimFragment();
    if (modelURI == getUri())
    {
      for (Element element : getElements())
      {
        if (element.getUri() == uri)
        {
          return element;
        }
      }
    }

    return null;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case DependenciesPackage.MODEL__CONTAINER:
      if (eInternalContainer() != null)
      {
        msgs = eBasicRemoveFromContainer(msgs);
      }
      return basicSetContainer((ModelContainer)otherEnd, msgs);
    case DependenciesPackage.MODEL__ELEMENTS:
      return ((InternalEList<InternalEObject>)(InternalEList<?>)getElements()).basicAdd(otherEnd, msgs);
    }
    return super.eInverseAdd(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case DependenciesPackage.MODEL__CONTAINER:
      return basicSetContainer(null, msgs);
    case DependenciesPackage.MODEL__ELEMENTS:
      return ((InternalEList<?>)getElements()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs)
  {
    switch (eContainerFeatureID())
    {
    case DependenciesPackage.MODEL__CONTAINER:
      return eInternalContainer().eInverseRemove(this, DependenciesPackage.MODEL_CONTAINER__MODELS, ModelContainer.class, msgs);
    }
    return super.eBasicRemoveFromContainerFeature(msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
    case DependenciesPackage.MODEL__URI:
      return getUri();
    case DependenciesPackage.MODEL__CONTAINER:
      return getContainer();
    case DependenciesPackage.MODEL__FILE:
      return getFile();
    case DependenciesPackage.MODEL__WORKSPACE:
      return isWorkspace();
    case DependenciesPackage.MODEL__EXISTS:
      return isExists();
    case DependenciesPackage.MODEL__NS_URI:
      return getNsURI();
    case DependenciesPackage.MODEL__NAME:
      return getName();
    case DependenciesPackage.MODEL__ELEMENTS:
      return getElements();
    case DependenciesPackage.MODEL__OUTGOING_LINKS:
      return getOutgoingLinks();
    case DependenciesPackage.MODEL__INCOMING_LINKS:
      return getIncomingLinks();
    case DependenciesPackage.MODEL__BROKEN_LINKS:
      return getBrokenLinks();
    case DependenciesPackage.MODEL__DEPENDENCIES:
      return getDependencies();
    case DependenciesPackage.MODEL__DEPENDING_MODELS:
      return getDependingModels();
    case DependenciesPackage.MODEL__FLAT_DEPENDENCIES:
      return getFlatDependencies();
    case DependenciesPackage.MODEL__FLAT_DEPENDING_MODELS:
      return getFlatDependingModels();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case DependenciesPackage.MODEL__URI:
      setUri((URI)newValue);
      return;
    case DependenciesPackage.MODEL__CONTAINER:
      setContainer((ModelContainer)newValue);
      return;
    case DependenciesPackage.MODEL__EXISTS:
      setExists((Boolean)newValue);
      return;
    case DependenciesPackage.MODEL__NS_URI:
      setNsURI((String)newValue);
      return;
    case DependenciesPackage.MODEL__NAME:
      setName((String)newValue);
      return;
    case DependenciesPackage.MODEL__ELEMENTS:
      getElements().clear();
      getElements().addAll((Collection<? extends Element>)newValue);
      return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
    case DependenciesPackage.MODEL__URI:
      setUri(URI_EDEFAULT);
      return;
    case DependenciesPackage.MODEL__CONTAINER:
      setContainer((ModelContainer)null);
      return;
    case DependenciesPackage.MODEL__EXISTS:
      setExists(EXISTS_EDEFAULT);
      return;
    case DependenciesPackage.MODEL__NS_URI:
      setNsURI(NS_URI_EDEFAULT);
      return;
    case DependenciesPackage.MODEL__NAME:
      setName(NAME_EDEFAULT);
      return;
    case DependenciesPackage.MODEL__ELEMENTS:
      getElements().clear();
      return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case DependenciesPackage.MODEL__URI:
      return URI_EDEFAULT == null ? uri != null : !URI_EDEFAULT.equals(uri);
    case DependenciesPackage.MODEL__CONTAINER:
      return getContainer() != null;
    case DependenciesPackage.MODEL__FILE:
      return FILE_EDEFAULT == null ? getFile() != null : !FILE_EDEFAULT.equals(getFile());
    case DependenciesPackage.MODEL__WORKSPACE:
      return isWorkspace() != WORKSPACE_EDEFAULT;
    case DependenciesPackage.MODEL__EXISTS:
      return exists != EXISTS_EDEFAULT;
    case DependenciesPackage.MODEL__NS_URI:
      return NS_URI_EDEFAULT == null ? nsURI != null : !NS_URI_EDEFAULT.equals(nsURI);
    case DependenciesPackage.MODEL__NAME:
      return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
    case DependenciesPackage.MODEL__ELEMENTS:
      return elements != null && !elements.isEmpty();
    case DependenciesPackage.MODEL__OUTGOING_LINKS:
      return !getOutgoingLinks().isEmpty();
    case DependenciesPackage.MODEL__INCOMING_LINKS:
      return !getIncomingLinks().isEmpty();
    case DependenciesPackage.MODEL__BROKEN_LINKS:
      return !getBrokenLinks().isEmpty();
    case DependenciesPackage.MODEL__DEPENDENCIES:
      return !getDependencies().isEmpty();
    case DependenciesPackage.MODEL__DEPENDING_MODELS:
      return !getDependingModels().isEmpty();
    case DependenciesPackage.MODEL__FLAT_DEPENDENCIES:
      return !getFlatDependencies().isEmpty();
    case DependenciesPackage.MODEL__FLAT_DEPENDING_MODELS:
      return !getFlatDependingModels().isEmpty();
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eInvoke(int operationID, EList<?> arguments) throws InvocationTargetException
  {
    switch (operationID)
    {
    case DependenciesPackage.MODEL___DEPENDS_UPON__MODEL:
      return dependsUpon((Model)arguments.get(0));
    case DependenciesPackage.MODEL___ADD_DEPENDENCY__MODEL:
      addDependency((Model)arguments.get(0));
      return null;
    case DependenciesPackage.MODEL___HAS_BROKEN_LINKS:
      return hasBrokenLinks();
    case DependenciesPackage.MODEL___GET_ELEMENT__URI:
      return getElement((URI)arguments.get(0));
    }
    return super.eInvoke(operationID, arguments);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString()
  {
    if (eIsProxy())
    {
      return super.toString();
    }

    StringBuilder result = new StringBuilder(super.toString());
    result.append(" (uri: ");
    result.append(uri);
    result.append(", exists: ");
    result.append(exists);
    result.append(", nsURI: ");
    result.append(nsURI);
    result.append(", name: ");
    result.append(name);
    result.append(')');
    return result.toString();
  }

  @Override
  public <T> T getAdapter(Class<T> adapter)
  {
    if (adapter == IResource.class || adapter == IFile.class)
    {
      @SuppressWarnings("unchecked")
      T file = (T)getFile();
      return file;
    }

    return null;
  }

} // ModelImpl
