/**
 */
package org.eclipse.emf.cdo.releng.setup.impl;

import org.eclipse.emf.cdo.releng.setup.AutomaticSourceLocator;
import org.eclipse.emf.cdo.releng.setup.InstallableUnit;
import org.eclipse.emf.cdo.releng.setup.P2Repository;
import org.eclipse.emf.cdo.releng.setup.RepositoryList;
import org.eclipse.emf.cdo.releng.setup.SetupPackage;
import org.eclipse.emf.cdo.releng.setup.Targlet;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EObjectEList;
import org.eclipse.emf.ecore.util.InternalEList;

import java.util.Collection;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Targlet</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.TargletImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.TargletImpl#getRoots <em>Roots</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.TargletImpl#getSourceLocators <em>Source Locators</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.TargletImpl#getRepositoryLists <em>Repository Lists</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.TargletImpl#getActiveRepositoryList <em>Active Repository List</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.TargletImpl#getActiveP2Repositories <em>Active P2 Repositories</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class TargletImpl extends MinimalEObjectImpl.Container implements Targlet
{
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
   * The cached value of the '{@link #getRoots() <em>Roots</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getRoots()
   * @generated
   * @ordered
   */
  protected EList<InstallableUnit> roots;

  /**
   * The cached value of the '{@link #getSourceLocators() <em>Source Locators</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getSourceLocators()
   * @generated
   * @ordered
   */
  protected EList<AutomaticSourceLocator> sourceLocators;

  /**
   * The cached value of the '{@link #getRepositoryLists() <em>Repository Lists</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getRepositoryLists()
   * @generated
   * @ordered
   */
  protected EList<RepositoryList> repositoryLists;

  /**
   * The default value of the '{@link #getActiveRepositoryList() <em>Active Repository List</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getActiveRepositoryList()
   * @generated
   * @ordered
   */
  protected static final String ACTIVE_REPOSITORY_LIST_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getActiveRepositoryList() <em>Active Repository List</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getActiveRepositoryList()
   * @generated
   * @ordered
   */
  protected String activeRepositoryList = ACTIVE_REPOSITORY_LIST_EDEFAULT;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected TargletImpl()
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
    return SetupPackage.Literals.TARGLET;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getName()
  {
    return name;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setName(String newName)
  {
    String oldName = name;
    name = newName;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.TARGLET__NAME, oldName, name));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<InstallableUnit> getRoots()
  {
    if (roots == null)
    {
      roots = new EObjectContainmentEList.Resolving<InstallableUnit>(InstallableUnit.class, this,
          SetupPackage.TARGLET__ROOTS);
    }
    return roots;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<AutomaticSourceLocator> getSourceLocators()
  {
    if (sourceLocators == null)
    {
      sourceLocators = new EObjectContainmentEList.Resolving<AutomaticSourceLocator>(AutomaticSourceLocator.class,
          this, SetupPackage.TARGLET__SOURCE_LOCATORS);
    }
    return sourceLocators;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<RepositoryList> getRepositoryLists()
  {
    if (repositoryLists == null)
    {
      repositoryLists = new EObjectContainmentEList.Resolving<RepositoryList>(RepositoryList.class, this,
          SetupPackage.TARGLET__REPOSITORY_LISTS);
    }
    return repositoryLists;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getActiveRepositoryList()
  {
    return activeRepositoryList;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setActiveRepositoryList(String newActiveRepositoryList)
  {
    String oldActiveRepositoryList = activeRepositoryList;
    activeRepositoryList = newActiveRepositoryList;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.TARGLET__ACTIVE_REPOSITORY_LIST,
          oldActiveRepositoryList, activeRepositoryList));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public EList<P2Repository> getActiveP2Repositories()
  {
    EList<P2Repository> result = new EObjectEList<P2Repository>(P2Repository.class, this,
        SetupPackage.TARGLET__ACTIVE_P2_REPOSITORIES);

    String name = getActiveRepositoryList();
    if (name != null)
    {
      for (RepositoryList repositoryList : getRepositoryLists())
      {
        if (name.equals(repositoryList.getName()))
        {
          result.addAll(repositoryList.getP2Repositories());
          break;
        }
      }
    }

    return result;
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
    case SetupPackage.TARGLET__ROOTS:
      return ((InternalEList<?>)getRoots()).basicRemove(otherEnd, msgs);
    case SetupPackage.TARGLET__SOURCE_LOCATORS:
      return ((InternalEList<?>)getSourceLocators()).basicRemove(otherEnd, msgs);
    case SetupPackage.TARGLET__REPOSITORY_LISTS:
      return ((InternalEList<?>)getRepositoryLists()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
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
    case SetupPackage.TARGLET__NAME:
      return getName();
    case SetupPackage.TARGLET__ROOTS:
      return getRoots();
    case SetupPackage.TARGLET__SOURCE_LOCATORS:
      return getSourceLocators();
    case SetupPackage.TARGLET__REPOSITORY_LISTS:
      return getRepositoryLists();
    case SetupPackage.TARGLET__ACTIVE_REPOSITORY_LIST:
      return getActiveRepositoryList();
    case SetupPackage.TARGLET__ACTIVE_P2_REPOSITORIES:
      return getActiveP2Repositories();
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
    case SetupPackage.TARGLET__NAME:
      setName((String)newValue);
      return;
    case SetupPackage.TARGLET__ROOTS:
      getRoots().clear();
      getRoots().addAll((Collection<? extends InstallableUnit>)newValue);
      return;
    case SetupPackage.TARGLET__SOURCE_LOCATORS:
      getSourceLocators().clear();
      getSourceLocators().addAll((Collection<? extends AutomaticSourceLocator>)newValue);
      return;
    case SetupPackage.TARGLET__REPOSITORY_LISTS:
      getRepositoryLists().clear();
      getRepositoryLists().addAll((Collection<? extends RepositoryList>)newValue);
      return;
    case SetupPackage.TARGLET__ACTIVE_REPOSITORY_LIST:
      setActiveRepositoryList((String)newValue);
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
    case SetupPackage.TARGLET__NAME:
      setName(NAME_EDEFAULT);
      return;
    case SetupPackage.TARGLET__ROOTS:
      getRoots().clear();
      return;
    case SetupPackage.TARGLET__SOURCE_LOCATORS:
      getSourceLocators().clear();
      return;
    case SetupPackage.TARGLET__REPOSITORY_LISTS:
      getRepositoryLists().clear();
      return;
    case SetupPackage.TARGLET__ACTIVE_REPOSITORY_LIST:
      setActiveRepositoryList(ACTIVE_REPOSITORY_LIST_EDEFAULT);
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
    case SetupPackage.TARGLET__NAME:
      return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
    case SetupPackage.TARGLET__ROOTS:
      return roots != null && !roots.isEmpty();
    case SetupPackage.TARGLET__SOURCE_LOCATORS:
      return sourceLocators != null && !sourceLocators.isEmpty();
    case SetupPackage.TARGLET__REPOSITORY_LISTS:
      return repositoryLists != null && !repositoryLists.isEmpty();
    case SetupPackage.TARGLET__ACTIVE_REPOSITORY_LIST:
      return ACTIVE_REPOSITORY_LIST_EDEFAULT == null ? activeRepositoryList != null : !ACTIVE_REPOSITORY_LIST_EDEFAULT
      .equals(activeRepositoryList);
    case SetupPackage.TARGLET__ACTIVE_P2_REPOSITORIES:
      return !getActiveP2Repositories().isEmpty();
    }
    return super.eIsSet(featureID);
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

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (name: ");
    result.append(name);
    result.append(", activeRepositoryList: ");
    result.append(activeRepositoryList);
    result.append(')');
    return result.toString();
  }

} // TargletImpl
