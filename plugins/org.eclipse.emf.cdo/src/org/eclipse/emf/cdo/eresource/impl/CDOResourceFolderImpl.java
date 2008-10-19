/**
 * <copyright>
 * </copyright>
 *
 * $Id: CDOResourceFolderImpl.java,v 1.2 2008-10-19 01:28:53 smcduff Exp $
 */
package org.eclipse.emf.cdo.eresource.impl;

import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.eresource.EresourcePackage;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>CDO Resource Folder</b></em>'.
 * 
 * @since 2.0<!-- end-user-doc -->
 *        <p>
 *        The following features are implemented:
 *        <ul>
 *        <li>{@link org.eclipse.emf.cdo.eresource.impl.CDOResourceFolderImpl#getNodes <em>Nodes</em>}</li>
 *        </ul>
 *        </p>
 * @generated
 */
public class CDOResourceFolderImpl extends CDOResourceNodeImpl implements CDOResourceFolder
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected CDOResourceFolderImpl()
  {
    super();
  }

  /**
   * @ADDED
   */
  public boolean isRoot()
  {
    return false;
  }

  // @Override
  // public Resource.Internal eDirectResource()
  // {
  // if (FSMUtil.isTransient(this))
  // {
  // return super.eDirectResource();
  // }
  //
  // if (eStore().getContainer(this) == null)
  // {
  // return cdoView().getRootResource();
  // }
  //
  // return null;
  // }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return EresourcePackage.Literals.CDO_RESOURCE_FOLDER;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @SuppressWarnings("unchecked")
  public EList<CDOResourceNode> getNodes()
  {
    return (EList<CDOResourceNode>)eGet(EresourcePackage.Literals.CDO_RESOURCE_FOLDER__NODES, true);
  }

} // CDOResourceFolderImpl
