/*
 * Copyright (c) 2009-2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.checkouts;

import org.eclipse.emf.cdo.explorer.ui.bundle.OM;

import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;

import org.eclipse.emf.common.util.URI;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Eike Stepper
 */
public interface CDOCheckoutEditorOpener
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
  public static abstract class Default implements CDOCheckoutEditorOpener
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
}
