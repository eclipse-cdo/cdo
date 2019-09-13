/*
 * Copyright (c) 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ui;

import org.eclipse.emf.cdo.internal.ui.bundle.OM;

import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.container.Container;
import org.eclipse.net4j.util.om.OMPlatform;

import org.eclipse.emf.common.util.URI;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Eike Stepper
 * @since 4.4
 */
public interface CDOEditorOpener
{
  public static final int DEFAULT_PRIORITY = 500;

  public String getID();

  public String getName();

  public ImageDescriptor getIcon();

  /**
   * Returns the priority of this editor opener. Usually used to choose between several editor openers that
   * match the same repository URI.
   */
  public int getPriority();

  /**
   * Returns the regular expression that determines if the editor opener can open a certain URI.
   */
  public String getRegex();

  /**
   * Checks if the URI matches the regular expression of this editor opener.
   */
  public boolean matchesRegex(URI uri);

  public IEditorPart openEditor(IWorkbenchPage page, URI uri);

  /**
   * @author Eike Stepper
   */
  public static abstract class Default implements CDOEditorOpener
  {
    private String id;

    private String name;

    private ImageDescriptor icon;

    private String regex;

    private int priority = DEFAULT_PRIORITY;

    @ExcludeFromDump
    private transient Pattern pattern;

    public Default()
    {
    }

    public Default(String id, String name, ImageDescriptor icon, String regex, int priority)
    {
      this.id = id;
      this.name = name;
      this.icon = icon;
      this.regex = regex;
      this.priority = priority;
    }

    public String getID()
    {
      return id;
    }

    public String getName()
    {
      return name;
    }

    public ImageDescriptor getIcon()
    {
      return icon;
    }

    public final int getPriority()
    {
      return priority;
    }

    public final String getRegex()
    {
      return regex;
    }

    public final boolean matchesRegex(URI uri)
    {
      synchronized (regex)
      {
        if (pattern == null)
        {
          pattern = Pattern.compile(regex);
        }
      }

      Matcher matcher = pattern.matcher(uri.toString());
      return matcher.matches();
    }

    public IEditorPart openEditor(final IWorkbenchPage page, URI uri)
    {
      final Set<IEditorPart> editors = new HashSet<IEditorPart>();
      final IEditorPart[] editor = { null };

      IPartListener partListener = new IPartListener()
      {
        public void partClosed(IWorkbenchPart part)
        {
          if (part == editor[0])
          {
            try
            {
              // view.close();
            }
            catch (Exception ex)
            {
              OM.LOG.error(ex);
            }
            finally
            {
              page.removePartListener(this);
            }
          }
        }

        public void partOpened(IWorkbenchPart part)
        {
          // Do nothing.
        }

        public void partDeactivated(IWorkbenchPart part)
        {
          // Do nothing.
        }

        public void partBroughtToTop(IWorkbenchPart part)
        {
          // Do nothing.
        }

        public void partActivated(IWorkbenchPart part)
        {
          // Do nothing.
        }
      };

      page.addPartListener(partListener);
      editor[0] = doOpenEditor(page, uri);

      if (!editors.contains(editor))
      {
        // The editor must have been open already and someone else will handle close.
        page.removePartListener(partListener);
      }

      return editor[0];
    }

    protected abstract IEditorPart doOpenEditor(IWorkbenchPage page, URI uri);

    @Override
    public String toString()
    {
      return id + "[" + regex + "]";
    }
  }

  /**
   * @author Eike Stepper
   * @since 4.4
   * @noextend This class is not intended to be subclassed by clients.
   * @noinstantiate This class is not intended to be instantiated by clients.
   */
  public static class Registry extends Container<CDOEditorOpener>
  {
    public static final Registry INSTANCE = new Registry();

    private static final String EXT_POINT = "editorOpeners"; //$NON-NLS-1$

    private final Map<String, CDOEditorOpener> editorOpeners = new HashMap<String, CDOEditorOpener>();

    public Registry()
    {
    }

    public IEditorPart openEditor(IWorkbenchPage page, URI uri)
    {
      if (uri == null)
      {
        return null;
      }

      for (CDOEditorOpener editorOpener : getEditorOpeners(uri))
      {
        IEditorPart editor = editorOpener.openEditor(page, uri);
        if (editor != null)
        {
          return editor;
        }
      }

      return null;
    }

    public CDOEditorOpener getEditorOpener(String id)
    {
      synchronized (editorOpeners)
      {
        return editorOpeners.get(id);
      }
    }

    public CDOEditorOpener[] getEditorOpeners(URI uri)
    {
      List<CDOEditorOpener> result = new ArrayList<CDOEditorOpener>();

      synchronized (editorOpeners)
      {
        for (CDOEditorOpener editorOpener : editorOpeners.values())
        {
          if (editorOpener.matchesRegex(uri))
          {
            result.add(editorOpener);
          }
        }
      }

      // Sort highest priority first
      Collections.sort(result, new Comparator<CDOEditorOpener>()
      {
        public int compare(CDOEditorOpener o1, CDOEditorOpener o2)
        {
          return -Integer.valueOf(o1.getPriority()).compareTo(o2.getPriority());
        }
      });

      return result.toArray(new CDOEditorOpener[result.size()]);
    }

    public void addEditorOpener(CDOEditorOpener editorOpener)
    {
      boolean added;
      synchronized (editorOpeners)
      {
        String id = editorOpener.getID();
        added = !editorOpeners.containsKey(id);
        if (added)
        {
          editorOpeners.put(id, editorOpener);
        }
      }

      if (added)
      {
        fireElementAddedEvent(editorOpener);
      }
    }

    public void removeEditorOpener(CDOEditorOpener editorOpener)
    {
      boolean removed;
      synchronized (editorOpeners)
      {
        String id = editorOpener.getID();
        removed = editorOpeners.remove(id) != null;
      }

      if (removed)
      {
        fireElementRemovedEvent(editorOpener);
      }
    }

    public CDOEditorOpener[] getElements()
    {
      synchronized (editorOpeners)
      {
        return editorOpeners.values().toArray(new CDOEditorOpener[editorOpeners.size()]);
      }
    }

    @Override
    public boolean isEmpty()
    {
      synchronized (editorOpeners)
      {
        return editorOpeners.isEmpty();
      }
    }

    @Override
    protected void doActivate() throws Exception
    {
      super.doActivate();
      if (OMPlatform.INSTANCE.isOSGiRunning())
      {
        try
        {
          readExtensions();
        }
        catch (Throwable t)
        {
          OM.LOG.error(t);
        }
      }
    }

    public void readExtensions()
    {
      IExtensionRegistry registry = Platform.getExtensionRegistry();
      IConfigurationElement[] configurationElements = registry.getConfigurationElementsFor(OM.BUNDLE_ID, EXT_POINT);
      for (IConfigurationElement element : configurationElements)
      {
        try
        {
          EditorOpenerDescriptor descriptor = new EditorOpenerDescriptor(element);
          addEditorOpener(descriptor);
        }
        catch (Exception ex)
        {
          OM.LOG.error(ex);
        }
      }
    }

    /**
     * @author Eike Stepper
     */
    public static final class EditorOpenerDescriptor extends CDOEditorOpener.Default
    {
      private IConfigurationElement element;

      public EditorOpenerDescriptor(IConfigurationElement element)
      {
        super(getID(element), getName(element), getIcon(element), getRegex(element), getPriority(element));
        this.element = element;

        if (StringUtil.isEmpty(element.getAttribute("class"))) //$NON-NLS-1$
        {
          throw new IllegalArgumentException(MessageFormat.format("Class not defined for extension {0}", element)); //$NON-NLS-1$
        }
      }

      @Override
      protected IEditorPart doOpenEditor(IWorkbenchPage page, URI uri)
      {
        return getEditorOpener().openEditor(page, uri);
      }

      private CDOEditorOpener getEditorOpener()
      {
        try
        {
          return (CDOEditorOpener)element.createExecutableExtension("class"); //$NON-NLS-1$
        }
        catch (CoreException ex)
        {
          throw WrappedException.wrap(ex);
        }
      }

      private static String getID(IConfigurationElement element)
      {
        String value = element.getAttribute("id"); //$NON-NLS-1$
        if (StringUtil.isEmpty(value))
        {
          throw new IllegalArgumentException(MessageFormat.format("ID not defined for extension {0}", element)); //$NON-NLS-1$
        }

        return value;
      }

      private static String getName(IConfigurationElement element)
      {
        String value = element.getAttribute("name"); //$NON-NLS-1$
        if (StringUtil.isEmpty(value))
        {
          throw new IllegalArgumentException(MessageFormat.format("Name not defined for extension {0}", element)); //$NON-NLS-1$
        }

        return value;
      }

      @SuppressWarnings("deprecation")
      private static ImageDescriptor getIcon(IConfigurationElement element)
      {
        String icon = element.getAttribute("icon"); //$NON-NLS-1$
        if (icon != null)
        {
          try
          {
            return AbstractUIPlugin.imageDescriptorFromPlugin(element.getNamespaceIdentifier(), icon);
          }
          catch (Exception ex)
          {
            //$FALL-THROUGH$
          }
        }

        return null;
      }

      private static String getRegex(IConfigurationElement element)
      {
        String value = element.getAttribute("regex"); //$NON-NLS-1$
        if (StringUtil.isEmpty(value))
        {
          throw new IllegalArgumentException(MessageFormat.format("Regex not defined for extension {0}", element)); //$NON-NLS-1$
        }

        return value;
      }

      private static int getPriority(IConfigurationElement element)
      {
        try
        {
          String value = element.getAttribute("priority"); //$NON-NLS-1$
          return Integer.parseInt(value);
        }
        catch (Exception ex)
        {
          return DEFAULT_PRIORITY;
        }
      }
    }
  }
}
