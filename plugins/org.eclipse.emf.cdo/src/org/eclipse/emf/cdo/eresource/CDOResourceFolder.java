/**
 * <copyright>
 * </copyright>
 *
 * $Id: CDOResourceFolder.java,v 1.2 2008-10-19 01:28:51 smcduff Exp $
 */
package org.eclipse.emf.cdo.eresource;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>CDO Resource Folder</b></em>'. <!-- end-user-doc
 * -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.eresource.CDOResourceFolder#getContents <em>Contents</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.emf.cdo.eresource.EresourcePackage#getCDOResourceFolder()
 * @model
 * @generated
 * @since 2.0
 */
public interface CDOResourceFolder extends CDOResourceNode
{
  /**
   * Returns the value of the '<em><b>Nodes</b></em>' containment reference list. The list contents are of type
   * {@link org.eclipse.emf.cdo.eresource.CDOResourceNode}. It is bidirectional and its opposite is '
   * {@link org.eclipse.emf.cdo.eresource.CDOResourceNode#getFolder <em>Folder</em>}'. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Nodes</em>' containment reference list isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Nodes</em>' containment reference list.
   * @see org.eclipse.emf.cdo.eresource.EresourcePackage#getCDOResourceFolder_Nodes()
   * @see org.eclipse.emf.cdo.eresource.CDOResourceNode#getFolder
   * @model opposite="folder" containment="true"
   * @generated
   */
  EList<CDOResourceNode> getNodes();

} // CDOResourceFolder
