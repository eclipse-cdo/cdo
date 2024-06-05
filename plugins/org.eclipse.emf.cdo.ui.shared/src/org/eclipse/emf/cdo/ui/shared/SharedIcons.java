/*
 * Copyright (c) 2010-2013, 2015, 2019-2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Victor Roldan Betancort - maintenance
 */
package org.eclipse.emf.cdo.ui.shared;

import org.eclipse.emf.cdo.ui.internal.shared.bundle.OM;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * @author Eike Stepper
 * @noextend This class is not intended to be subclassed by clients.
 */
public class SharedIcons
{
  private static final String IMAGE_FORMAT_GIF = ".gif"; //$NON-NLS-1$

  private static final String IMAGE_FORMAT_PNG = ".png"; //$NON-NLS-1$

  private static final ImageRegistry REGISTRY = new ImageRegistry(getDisplay());

  private static final String ETOOL = "etool16/"; //$NON-NLS-1$

  public static final String ETOOL_OPEN_SESSION = ETOOL + "open_session" + IMAGE_FORMAT_GIF; //$NON-NLS-1$

  public static final String ETOOL_OPEN_EDITOR = ETOOL + "open_editor" + IMAGE_FORMAT_GIF; //$NON-NLS-1$

  /**
   * @since 4.1
   */
  public static final String ETOOL_NEW_RESOURCE = ETOOL + "NewCDOResource" + IMAGE_FORMAT_GIF; //$NON-NLS-1$

  /**
   * @since 4.1
   */
  public static final String ETOOL_NEW_TEXT_RESOURCE = ETOOL + "NewCDOTextResource" + IMAGE_FORMAT_GIF; //$NON-NLS-1$

  /**
   * @since 4.1
   */
  public static final String ETOOL_NEW_BINARY_RESOURCE = ETOOL + "NewCDOBinaryResource" + IMAGE_FORMAT_GIF; //$NON-NLS-1$

  /**
   * @since 4.1
   */
  public static final String ETOOL_NEW_RESOURCE_FOLDER = ETOOL + "NewCDOResourceFolder" + IMAGE_FORMAT_GIF; //$NON-NLS-1$

  /**
   * @since 4.1
   */
  public static final String ETOOL_CLOCK = ETOOL + "clock" + IMAGE_FORMAT_PNG; //$NON-NLS-1$

  /**
   * @deprecated As of 4.1 use {@link #ETOOL_CLOCK}
   */
  @Deprecated
  public static final String ETOOL_TIME_PICK_BUTTON_ICON = ETOOL_CLOCK;

  /**
   * @since 4.1
   */
  public static final String ETOOL_SLIDER = ETOOL + "slider" + IMAGE_FORMAT_PNG; //$NON-NLS-1$

  /**
   * @deprecated As of 4.1 use {@link #ETOOL_CLOCK}
   */
  @Deprecated
  public static final String ETOOL_SLIDER_ICON = ETOOL_SLIDER;

  /**
   * @since 4.1
   */
  public static final String ETOOL_SAVE = ETOOL + "save" + IMAGE_FORMAT_GIF; //$NON-NLS-1$

  /**
   * @since 4.1
   */
  public static final String ETOOL_IMPORT = ETOOL + "import" + IMAGE_FORMAT_GIF; //$NON-NLS-1$

  /**
   * @since 4.1
   */
  public static final String ETOOL_EXPORT = ETOOL + "export" + IMAGE_FORMAT_GIF; //$NON-NLS-1$

  private static final String OBJ = "obj16/"; //$NON-NLS-1$

  public static final String OBJ_SESSION = OBJ + "cdo_session" + IMAGE_FORMAT_GIF; //$NON-NLS-1$

  public static final String OBJ_SESSION_SYNCING = OBJ + "cdo_session_syncing" + IMAGE_FORMAT_GIF; //$NON-NLS-1$

  public static final String OBJ_SESSION_OFFLINE = OBJ + "cdo_session_offline" + IMAGE_FORMAT_GIF; //$NON-NLS-1$

  /**
   * @since 4.9
   */
  public static final String OBJ_VIEW_SET = OBJ + "cdo_view_set" + IMAGE_FORMAT_GIF; //$NON-NLS-1$

  public static final String OBJ_EDITOR = OBJ + "cdo_editor" + IMAGE_FORMAT_GIF; //$NON-NLS-1$

  public static final String OBJ_EDITOR_READWRITE = OBJ + "cdo_editor_readwrite" + IMAGE_FORMAT_GIF; //$NON-NLS-1$

  public static final String OBJ_EDITOR_READONLY = OBJ + "cdo_editor_readonly" + IMAGE_FORMAT_GIF; //$NON-NLS-1$

  public static final String OBJ_EDITOR_HISTORICAL = OBJ + "cdo_editor_historical" + IMAGE_FORMAT_GIF; //$NON-NLS-1$

  public static final String OBJ_EPACKAGE = OBJ + "EPackage" + IMAGE_FORMAT_GIF; //$NON-NLS-1$

  public static final String OBJ_RESOURCE = OBJ + "CDOResource" + IMAGE_FORMAT_GIF; //$NON-NLS-1$

  /**
   * @since 4.1
   */
  public static final String OBJ_FILE_RESOURCE = OBJ + "CDOFileResource" + IMAGE_FORMAT_GIF; //$NON-NLS-1$

  /**
   * @since 4.1
   */
  public static final String OBJ_TEXT_RESOURCE = OBJ + "CDOTextResource" + IMAGE_FORMAT_GIF; //$NON-NLS-1$

  /**
   * @since 4.1
   */
  public static final String OBJ_BINARY_RESOURCE = OBJ + "CDOBinaryResource" + IMAGE_FORMAT_GIF; //$NON-NLS-1$

  public static final String OBJ_RESOURCE_FOLDER = OBJ + "CDOResourceFolder" + IMAGE_FORMAT_GIF; //$NON-NLS-1$

  public static final String OBJ_EPACKAGE_CONVERTED = OBJ + "EPackageConverted" + IMAGE_FORMAT_GIF; //$NON-NLS-1$

  public static final String OBJ_EPACKAGE_LEGACY = OBJ + "EPackageLegacy" + IMAGE_FORMAT_GIF; //$NON-NLS-1$

  public static final String OBJ_EPACKAGE_NATIVE = OBJ + "EPackageNative" + IMAGE_FORMAT_GIF; //$NON-NLS-1$

  public static final String OBJ_EPACKAGE_DYNAMIC = OBJ + "EPackageDynamic" + IMAGE_FORMAT_GIF; //$NON-NLS-1$

  public static final String OBJ_EPACKAGE_UNKNOWN = OBJ + "EPackageUnknown" + IMAGE_FORMAT_GIF; //$NON-NLS-1$

  public static final String OBJ_ECLASS = OBJ + "EClass" + IMAGE_FORMAT_GIF; //$NON-NLS-1$

  public static final String OBJ_REPO = OBJ + "repo.gif"; //$NON-NLS-1$

  /**
   * @since 4.3
   */
  public static final String OBJ_BRANCH_POINT = OBJ + "branchpoint.gif"; //$NON-NLS-1$

  public static final String OBJ_BRANCH = OBJ + "branch.gif"; //$NON-NLS-1$

  /**
   * @since 4.1
   */
  public static final String OBJ_BRANCH_GRAY = OBJ + "branch-gray.gif"; //$NON-NLS-1$

  /**
   * @since 4.1
   */
  public static final String OBJ_COMMIT = OBJ + "commit.gif"; //$NON-NLS-1$

  /**
   * @since 4.1
   */
  public static final String OBJ_PERSON = OBJ + "person.gif"; //$NON-NLS-1$

  /**
   * @since 4.1
   */
  public static final String OBJ_PERSON_ME = OBJ + "person-me.gif"; //$NON-NLS-1$

  /**
   * @since 4.7
   */
  public static final String OBJ_TOPIC = OBJ + "topic.gif"; //$NON-NLS-1$

  private static final String OVR = "ovr16/"; //$NON-NLS-1$

  /**
   * @since 4.3
   */
  public static final String OVR_ERROR = OVR + "error" + IMAGE_FORMAT_GIF; //$NON-NLS-1$

  /**
   * @since 4.3
   */
  public static final String OVR_WARNING = OVR + "warning" + IMAGE_FORMAT_GIF; //$NON-NLS-1$

  /**
   * @since 4.3
   */
  public static final String OVR_LOCK = OVR + "lock" + IMAGE_FORMAT_GIF; //$NON-NLS-1$

  /**
   * @since 4.3
   */
  public static final String OVR_LOCK_SELF = OVR + "lock_self" + IMAGE_FORMAT_GIF; //$NON-NLS-1$

  /**
   * @since 4.8
   */
  public static final String OVR_SYSTEM = OVR + "system" + IMAGE_FORMAT_GIF; //$NON-NLS-1$

  /**
   * @since 4.8
   */
  public static final String OVR_MODULE = OVR + "module" + IMAGE_FORMAT_GIF; //$NON-NLS-1$

  /**
   * @since 4.6
   */
  public static final String OVR_SECURITY = OVR + "security" + IMAGE_FORMAT_GIF; //$NON-NLS-1$

  private static final String VIEW = "view16/"; //$NON-NLS-1$

  public static final String VIEW_SESSIONS = VIEW + "cdo_sessions" + IMAGE_FORMAT_GIF; //$NON-NLS-1$

  private static final String WIZBAN = "wizban/"; //$NON-NLS-1$

  /**
   * @since 4.2
   */
  public static final String WIZBAN_OPEN_SESSION = WIZBAN + "OpenSession" + IMAGE_FORMAT_GIF; //$NON-NLS-1$

  public static final String WIZBAN_PACKAGE_MANAGER = WIZBAN + "PackageManager" + IMAGE_FORMAT_GIF; //$NON-NLS-1$

  public static final String WIZBAN_PROTOCOL_PROBLEM = WIZBAN + "ProtocolProblem" + IMAGE_FORMAT_GIF; //$NON-NLS-1$

  /**
   * @deprecated As of 4.3 use {@link #WIZBAN_BRANCH_SELECTION}
   */
  @Deprecated
  public static final String WIZBAN_TARGET_SELECTION = WIZBAN + "BranchBanner" + IMAGE_FORMAT_GIF; //$NON-NLS-1$

  /**
   * @since 4.3
   */
  public static final String WIZBAN_BRANCH_SELECTION = WIZBAN + "BranchBanner" + IMAGE_FORMAT_GIF; //$NON-NLS-1$

  /**
   * @since 4.3
   */
  public static final String WIZBAN_BRANCH_POINT_SELECTION = WIZBAN + "BranchPointBanner" + IMAGE_FORMAT_GIF; //$NON-NLS-1$

  public static final String WIZBAN_TIME_SELECTION = WIZBAN + "TimeBanner" + IMAGE_FORMAT_GIF; //$NON-NLS-1$

  /**
   * @since 4.1
   */
  public static final String WIZBAN_TRANSFER = WIZBAN + "transfer_wiz" + IMAGE_FORMAT_PNG; //$NON-NLS-1$

  /**
   * @since 4.1
   */
  public static final String WIZBAN_IMPORT = WIZBAN + "import_wiz" + IMAGE_FORMAT_PNG; //$NON-NLS-1$

  /**
   * @since 4.3
   */
  public static final String WIZBAN_EXPORT = WIZBAN + "export_wiz" + IMAGE_FORMAT_PNG; //$NON-NLS-1$

  /**
   * @since 4.1
   * @deprecated As of 4.2 use {@link #WIZBAN_EXPORT}.
   */
  @Deprecated
  public static final String WIZBAN_EXPOR = WIZBAN + "export_wiz" + IMAGE_FORMAT_PNG; //$NON-NLS-1$

  /**
   * @since 4.3
   */
  public static final String WIZBAN_COMMIT = WIZBAN + "commit" + IMAGE_FORMAT_GIF; //$NON-NLS-1$

  /**
   * @since 4.3
   */
  public static final String WIZBAN_CONFLICT = WIZBAN + "conflict" + IMAGE_FORMAT_GIF; //$NON-NLS-1$

  /**
   * @since 4.3
   */
  public static final String WIZBAN_EDIT = WIZBAN + "edit" + IMAGE_FORMAT_GIF; //$NON-NLS-1$

  /**
   * @since 4.6
   */
  public static final String WIZBAN_DELETE = WIZBAN + "delete" + IMAGE_FORMAT_GIF; //$NON-NLS-1$

  public static Image getImage(String key)
  {
    key = mangleKey(key);
    Image image = REGISTRY.get(key);
    if (image == null)
    {
      createDescriptor(key);
      image = REGISTRY.get(key);
    }

    // For some reason, sometimes images get disposed.
    // In that case, create and put in registry again.
    if (image.isDisposed())
    {
      REGISTRY.remove(key);
      createDescriptor(key);
      image = REGISTRY.get(key);
    }

    return image;
  }

  public static ImageDescriptor getDescriptor(String key)
  {
    key = mangleKey(key);
    ImageDescriptor descriptor = REGISTRY.getDescriptor(key);
    if (descriptor == null)
    {
      descriptor = createDescriptor(key);
    }

    return descriptor;
  }

  private static ImageDescriptor createDescriptor(String key)
  {
    ImageDescriptor descriptor = AbstractUIPlugin.imageDescriptorFromPlugin(OM.BUNDLE_ID, key);
    if (descriptor != null)
    {
      REGISTRY.put(key, descriptor);
    }

    return descriptor;
  }

  private static Display getDisplay()
  {
    Display display = Display.getCurrent();
    if (display == null)
    {
      display = Display.getDefault();
    }

    if (display == null)
    {
      throw new IllegalStateException("display == null"); //$NON-NLS-1$
    }

    return display;
  }

  private static String mangleKey(String key)
  {
    return "icons/full/" + key; //$NON-NLS-1$
  }
}
