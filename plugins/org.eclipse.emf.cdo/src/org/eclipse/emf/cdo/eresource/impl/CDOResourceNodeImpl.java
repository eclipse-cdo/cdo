/*
 * Copyright (c) 2008-2013, 2015, 2016, 2018, 2019, 2021, 2023, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Christian W. Damus (CEA LIST) - bug 416298: CDOResourceNodes do not support reflective access to derived path attribute
 *    Thorsten Schlathoelter - bug 494954: Name change does not trigger recache of URI
 */
package org.eclipse.emf.cdo.eresource.impl;

import org.eclipse.emf.cdo.common.util.CDODuplicateResourceException;
import org.eclipse.emf.cdo.common.util.CDOException;
import org.eclipse.emf.cdo.common.util.CDOResourceNodeNotFoundException;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.util.CDOURIUtil;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;
import org.eclipse.emf.internal.cdo.messages.Messages;

import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.concurrent.CriticalSection;
import org.eclipse.net4j.util.om.OMPlatform;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;
import org.eclipse.emf.spi.cdo.InternalCDOView;

import java.text.MessageFormat;
import java.util.List;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>CDO Resource Node</b></em>'.
 *
 * @noextend This interface is not intended to be extended by clients. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.eresource.impl.CDOResourceNodeImpl#getFolder <em>Folder</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.eresource.impl.CDOResourceNodeImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.eresource.impl.CDOResourceNodeImpl#getPath <em>Path</em>}</li>
 * </ul>
 *
 * @generated
 */
public abstract class CDOResourceNodeImpl extends CDOObjectImpl implements CDOResourceNode
{
  private static final boolean disableNameChecks = OMPlatform.INSTANCE.isProperty("org.eclipse.emf.cdo.CDOResourceNode.disableNameChecks");

  private static final boolean singleExtensions = OMPlatform.INSTANCE.isProperty("org.eclipse.emf.cdo.CDOResourceNode.singleExtensions");

  private static final ExtensionFinder EXTENSION_FINDER = singleExtensions ? new ExtensionFinder.Single() : new ExtensionFinder.Multi();

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected CDOResourceNodeImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return EresourcePackage.Literals.CDO_RESOURCE_NODE;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected int eStaticFeatureCount()
  {
    return 0;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public CDOResourceFolder getFolder()
  {
    return (CDOResourceFolder)eGet(EresourcePackage.Literals.CDO_RESOURCE_NODE__FOLDER, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public void setFolderGen(CDOResourceFolder newFolder)
  {
    eSet(EresourcePackage.Literals.CDO_RESOURCE_NODE__FOLDER, newFolder);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  @Override
  public void setFolder(CDOResourceFolder newFolder)
  {
    basicSetFolder(newFolder, true);
  }

  /**
   * @ADDED
   */
  public void basicSetFolder(CDOResourceFolder newFolder, boolean checkDuplicates)
  {
    CDOResourceFolder oldFolder = getFolder();
    if (!ObjectUtil.equals(oldFolder, newFolder))
    {
      if (checkDuplicates)
      {
        String name = getName();
        if (name != null)
        {
          String newPath = (newFolder == null ? "" : newFolder.getPath()) + CDOURIUtil.SEGMENT_SEPARATOR + name; // $NON-NLS-1$
          checkDuplicates(newPath);
        }
      }

      setFolderGen(newFolder);
    }
  }

  /**
   * @since 4.5
   */
  public void recacheURIs()
  {
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getName()
  {
    return (String)eGet(EresourcePackage.Literals.CDO_RESOURCE_NODE__NAME, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public void setNameGen(String newName)
  {
    eSet(EresourcePackage.Literals.CDO_RESOURCE_NODE__NAME, newName);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  @Override
  public void setName(String newName)
  {
    basicSetName(newName, true);
  }

  /**
   * @ADDED
   */
  public void basicSetName(String newName, boolean checkDuplicates)
  {
    if (!disableNameChecks)
    {
      CheckUtil.checkArg(newName, "Name is null"); //$NON-NLS-1$
      CheckUtil.checkArg(newName.length() != 0, "Name is empty"); //$NON-NLS-1$
      CheckUtil.checkArg(!".".equals(newName), "Name is a dot"); //$NON-NLS-1$
      CheckUtil.checkArg(newName.indexOf(CDOURIUtil.SEGMENT_SEPARATOR_CHAR) == -1, "Name contains a path separator"); //$NON-NLS-1$
    }

    String oldName = getName();
    if (!ObjectUtil.equals(oldName, newName))
    {
      if (checkDuplicates)
      {
        CDOResourceFolder parent = getFolder();
        if (parent != null)
        {
          String newPath = parent.getPath() + CDOURIUtil.SEGMENT_SEPARATOR + newName;
          checkDuplicates(newPath);
        }
      }

      setNameGen(newName);
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  @Override
  public String getPath()
  {
    if (isRoot())
    {
      return CDOResourceNode.ROOT_PATH;
    }

    CDOResourceFolder folder = getFolder();
    if (folder == null)
    {
      return CDOResourceNode.ROOT_PATH + getName();
    }

    return folder.getPath() + CDOResourceNode.ROOT_PATH + getName();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  @Override
  public void setPath(String newPath)
  {
    InternalCDOTransaction transaction = cdoView().toTransaction();
    if (newPath == null)
    {
      throw new CDOException(Messages.getString("CDOResourceNodeImpl.3")); //$NON-NLS-1$
    }

    String oldPath = getPath();
    if (!ObjectUtil.equals(oldPath, newPath))
    {
      checkDuplicates(newPath);

      List<String> names = CDOURIUtil.analyzePath(newPath);
      if (names.isEmpty())
      {
        throw new CDOException(Messages.getString("CDOResourceNodeImpl.4")); //$NON-NLS-1$
      }

      String newName = names.remove(names.size() - 1);
      CDOResourceFolder newFolder = transaction.getOrCreateResourceFolder(names);
      if (newFolder == null)
      {
        transaction.getRootResource().getContents().add(this);
      }

      basicSetFolder(newFolder, false);
      basicSetName(newName, false);
    }
  }

  /**
   * @since 4.4
   */
  @Override
  public String getExtension()
  {
    String name = getName();
    if (name != null)
    {
      int dot = EXTENSION_FINDER.findExtension(name);
      if (dot != -1)
      {
        return name.substring(dot + 1);
      }
    }

    return "";
  }

  /**
   * @since 4.7
   */
  @Override
  public void setExtension(String extension)
  {
    sync().run(() -> setExtensionUnsynced(extension));
  }

  private void setExtensionUnsynced(String extension)
  {
    if (StringUtil.isEmpty(extension))
    {
      setName(getBasename());
    }
    else
    {
      if (singleExtensions)
      {
        CheckUtil.checkArg(extension.indexOf(ExtensionFinder.DOT) == -1, "Extension contains a dot"); //$NON-NLS-1$
      }

      setName(getBasename() + ExtensionFinder.DOT + extension);
    }
  }

  /**
   * @since 4.4
   */
  @Override
  public String trimExtension()
  {
    return getBasename();
  }

  /**
   * @since 4.7
   */
  @Override
  public String getBasename()
  {
    String name = getName();
    if (name != null)
    {
      int dot = EXTENSION_FINDER.findExtension(name);
      if (dot != -1)
      {
        return name.substring(0, dot);
      }
    }

    return name;
  }

  /**
   * @since 4.7
   */
  @Override
  public void setBasename(String basename)
  {
    sync().run(() -> setBasenameUnsynced(basename));
  }

  private void setBasenameUnsynced(String basename)
  {
    if (basename == null)
    {
      basename = StringUtil.EMPTY;
    }

    if (!singleExtensions)
    {
      CheckUtil.checkArg(basename.indexOf(ExtensionFinder.DOT) == -1, "Basename contains a dot"); //$NON-NLS-1$
    }

    String extension = getExtension();
    if (StringUtil.isEmpty(extension))
    {
      setName(basename);
    }
    else
    {
      setName(basename + ExtensionFinder.DOT + extension);
    }
  }

  /**
   * @ADDED
   */
  @Override
  public URI getURI()
  {
    InternalCDOView view = cdoView();
    String path = getPath();

    URI uri = CDOURIUtil.createResourceURI(view, path);
    if (uri != null)
    {
      URIConverter uriConverter = view.getResourceSet().getURIConverter();
      uri = uriConverter.normalize(uri);
    }

    return uri;
  }

  /**
   * @ADDED
   * @since 4.8
   */
  protected void checkDuplicates(String newPath) throws CDODuplicateResourceException
  {
    InternalCDOView view = cdoView();
    if (view != null)
    {
      view.clearResourcePathCacheIfNecessary(null);

      try
      {
        view.getResourceNodeID(newPath);
        throw new CDODuplicateResourceException(MessageFormat.format(Messages.getString("CDOResourceNodeImpl.5"), newPath)); //$NON-NLS-1$
      }
      catch (CDOResourceNodeNotFoundException success)
      {
        //$FALL-THROUGH$
      }
    }
  }

  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
    case EresourcePackage.CDO_RESOURCE_NODE__PATH:
      return getPath();

    default:
      return super.eGet(featureID, resolve, coreType);
    }
  }

  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case EresourcePackage.CDO_RESOURCE_NODE__PATH:
      setPath((String)newValue);
      break;

    default:
      super.eSet(featureID, newValue);
    }

    // Bug 494954: Update the URI if name the changes
    if (featureID == EresourcePackage.CDO_RESOURCE_NODE__NAME)
    {
      recacheURIs();
    }
  }

  @Override
  public String toString()
  {
    String string = super.toString();
    return toString(string);
  }

  /**
   * @since 4.4
   */
  protected String toString(String string)
  {
    InternalCDORevision revision = cdoRevision();
    if (revision != null)
    {
      String name = revision.getResourceNodeName();
      if (name == null)
      {
        name = "/";
      }

      string += "(\"" + name + "\")";
    }

    return string;
  }

  private CriticalSection sync()
  {
    InternalCDOView view = cdoView();
    if (view == null)
    {
      return CriticalSection.UNSYNCHRONIZED;
    }

    return view.sync();
  }

  /**
   * @author Eike Stepper
   */
  private interface ExtensionFinder
  {
    public static final char DOT = '.';

    public int findExtension(String name);

    /**
     * @author Eike Stepper
     */
    public static final class Single implements ExtensionFinder
    {
      @Override
      public int findExtension(String name)
      {
        return name.lastIndexOf(DOT);
      }
    }

    /**
     * @author Eike Stepper
     */
    public static final class Multi implements ExtensionFinder
    {
      @Override
      public int findExtension(String name)
      {
        return name.indexOf(DOT);
      }
    }
  }
} // CDOResourceNodeImpl
