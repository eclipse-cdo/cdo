/*
 * Copyright (c) 2010-2012, 2015, 2019-2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.ui.shared;

import org.eclipse.net4j.ui.internal.shared.bundle.OM;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * @author Eike Stepper
 */
public final class SharedIcons
{
  private static final ImageRegistry REGISTRY = new ImageRegistry(getDisplay());

  private static final String OBJ = "obj16/"; //$NON-NLS-1$

  public static final String OBJ_ACCEPTOR = OBJ + "acceptor"; //$NON-NLS-1$

  public static final String OBJ_ADAPTER = OBJ + "adapter"; //$NON-NLS-1$

  public static final String OBJ_CHANNEL = OBJ + "channel"; //$NON-NLS-1$

  /**
   * @since 4.6
   */
  public static final String OBJ_SIGNAL = OBJ + "signal"; //$NON-NLS-1$

  /**
   * @since 4.6
   */
  public static final String OBJ_REQUEST_SYNC = OBJ + "request_sync"; //$NON-NLS-1$

  /**
   * @since 4.6
   */
  public static final String OBJ_REQUEST_ASYNC = OBJ + "request_async"; //$NON-NLS-1$

  /**
   * @since 4.6
   */
  public static final String OBJ_INDICATION_SYNC = OBJ + "indication_sync"; //$NON-NLS-1$

  /**
   * @since 4.6
   */
  public static final String OBJ_INDICATION_ASYNC = OBJ + "indication_async"; //$NON-NLS-1$

  public static final String OBJ_CONNECTOR = OBJ + "connector"; //$NON-NLS-1$

  public static final String OBJ_FACTORY = OBJ + "factory"; //$NON-NLS-1$

  /**
   * @since 4.8
   */
  public static final String OBJ_PROJECT = OBJ + "project.png"; //$NON-NLS-1$

  public static final String OBJ_FOLDER = OBJ + "folder.png"; //$NON-NLS-1$

  public static final String OBJ_BEAN = OBJ + "javabean"; //$NON-NLS-1$

  public static final String OBJ_ERROR = OBJ + "error"; //$NON-NLS-1$

  public static final String OBJ_PENDING = OBJ + "pending"; //$NON-NLS-1$

  public static final String OBJ_COLLABORATION = OBJ + "collaboration"; //$NON-NLS-1$

  public static final String OBJ_BUDDY = OBJ + "buddy"; //$NON-NLS-1$

  public static final String OBJ_BUDDY_LONESOME = OBJ + "buddy_lonesome"; //$NON-NLS-1$

  public static final String OBJ_BUDDY_AWAY = OBJ + "buddy_away"; //$NON-NLS-1$

  public static final String OBJ_BUDDY_DO_NOT_DISTURB = OBJ + "buddy_do_not_disturb"; //$NON-NLS-1$

  public static final String OBJ_CHAT = OBJ + "chat"; //$NON-NLS-1$

  /**
   * @since 4.3
   */
  public static final String OBJ_INFO = OBJ + "information"; //$NON-NLS-1$

  private static final String ETOOL = "etool16/"; //$NON-NLS-1$

  public static final String ETOOL_ADD_ACCEPTOR = ETOOL + "add_acceptor"; //$NON-NLS-1$

  public static final String ETOOL_ADD_CONNECTOR = ETOOL + "add_connector"; //$NON-NLS-1$

  public static final String ETOOL_ADD = ETOOL + "add"; //$NON-NLS-1$

  public static final String ETOOL_DELETE = ETOOL + "delete"; //$NON-NLS-1$

  public static final String ETOOL_REFRESH = ETOOL + "refresh"; //$NON-NLS-1$

  /**
   * @since 4.1
   */
  public static final String ETOOL_PART_MODE = ETOOL + "part_mode"; //$NON-NLS-1$

  public static final String ETOOL_HORIZONTAL = ETOOL + "horizontal_layout"; //$NON-NLS-1$

  public static final String ETOOL_VERTICAL = ETOOL + "vertical_layout"; //$NON-NLS-1$

  /**
   * @since 4.7
   */
  public static final String ETOOL_LINK_WITH_EDITOR = ETOOL + "link_with_editor"; //$NON-NLS-1$

  /**
   * @since 4.7
   */
  public static final String ETOOL_EXPAND_ALL = ETOOL + "expand_all"; //$NON-NLS-1$

  /**
   * @since 4.2
   */
  public static final String ETOOL_COLLAPSE_ALL = ETOOL + "collapse_all"; //$NON-NLS-1$

  /**
   * @since 4.6
   */
  public static final String ETOOL_CLEAR_LOG = ETOOL + "clear_log.png"; //$NON-NLS-1$

  /**
   * @since 4.9
   */
  public static final String ETOOL_COPY = ETOOL + "copy.png"; //$NON-NLS-1$

  private static final String VIEW = "view16/"; //$NON-NLS-1$

  public static final String VIEW_CONTAINER = VIEW + "container"; //$NON-NLS-1$

  @SuppressWarnings("unused")
  private static final String WIZBAN = "wizban/"; //$NON-NLS-1$

  /**
   * @deprecated As of 4.2 use {@link #ETOOL_ADD}
   */
  @Deprecated
  public static final String TOOL_ADD = ETOOL_ADD;

  /**
   * @deprecated As of 4.2 use {@link #ETOOL_DELETE}
   */
  @Deprecated
  public static final String TOOL_DELETE = ETOOL_DELETE;

  /**
   * @deprecated As of 4.2 use {@link #ETOOL_REFRESH}
   */
  @Deprecated
  public static final String TOOL_REFRESH = ETOOL_REFRESH;

  public static Image getImage(String key)
  {
    key = mangleKey(key);
    Image image = REGISTRY.get(key);
    if (image == null)
    {
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
    key = "icons/full/" + key;
    if (key.endsWith(".png"))
    {
      return key;
    }

    return key + ".gif"; //$NON-NLS-1$
  }
}
