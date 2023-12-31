/*
 * Copyright (c) 2015, 2016, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.ui.widgets;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.ReflectUtil;
import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.views.ItemProvider;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

import java.lang.reflect.Field;

/**
 * @author Eike Stepper
 * @since 3.6
 */
public class SearchField extends Composite
{
  private static final Field patternFilterField = ReflectUtil.getField(FilteredTree.class, "patternFilter");

  private static final Field refreshJobField = ReflectUtil.getField(FilteredTree.class, "refreshJob");

  private final InternalTree filteredTree;

  public SearchField(Composite parent, final FilterHandler filterHandler)
  {
    super(parent, SWT.NONE);
    setLayout(new FirstChildLayout());

    final PatternFilter patternFilter = new PatternFilter()
    {
      @Override
      public Object[] filter(Viewer viewer, Object parent, Object[] elements)
      {
        if (filteredTree != null)
        {
          Text filterControl = filteredTree.getFilterControl();
          String filter = filterControl.getText();
          if (ObjectUtil.equals(filter, filteredTree.getInitialText()))
          {
            filter = null;
          }

          filterHandler.handleFilter(filter);
        }

        return ItemProvider.NO_ELEMENTS;
      }
    };

    filteredTree = new InternalTree(this, SWT.NONE, patternFilter, true, patternFilter);

    final Text filterControl = filteredTree.getFilterControl();
    filterControl.addTraverseListener(new TraverseListener()
    {
      @Override
      public void keyTraversed(TraverseEvent e)
      {
        if (e.keyCode == SWT.ESC)
        {
          if (!"".equals(filterControl.getText()))
          {
            filterControl.setText("");
            e.doit = false;
          }
        }
      }
    });

    filterControl.addKeyListener(new KeyAdapter()
    {
      @Override
      public void keyPressed(KeyEvent e)
      {
        if (e.keyCode == SWT.CR || e.keyCode == SWT.ARROW_DOWN)
        {
          finishFilter();
          e.doit = false;
        }
      }
    });
  }

  public final PatternFilter getPatternFilter()
  {
    return filteredTree.getPatternFilter();
  }

  public final Text getFilterControl()
  {
    return filteredTree.getFilterControl();
  }

  public final void setInitialText(String text)
  {
    filteredTree.setInitialText(text);
  }

  @Override
  public final boolean getEnabled()
  {
    return filteredTree.getEnabled();
  }

  @Override
  public void setEnabled(boolean enabled)
  {
    filteredTree.setEnabled(enabled);
  }

  @Override
  public boolean setFocus()
  {
    return getFilterControl().setFocus();
  }

  @Override
  protected void checkSubclass()
  {
    // Do nothing.
  }

  /**
   * Subclasses may override.
   */
  protected void finishFilter()
  {
    // Do nothing.
  }

  /**
   * @author Eike Stepper
   */
  private final class InternalTree extends FilteredTree
  {
    private final PatternFilter patternFilter;

    @SuppressWarnings("deprecation")
    private InternalTree(Composite parent, int treeStyle, PatternFilter filter, boolean useNewLook, PatternFilter patternFilter)
    {
      super(parent, treeStyle, filter, useNewLook);
      this.patternFilter = patternFilter;
    }

    @Override
    @SuppressWarnings("restriction")
    protected void init(int treeStyle, PatternFilter filter)
    {
      ReflectUtil.setValue(patternFilterField, this, filter);

      showFilterControls = PlatformUI.getPreferenceStore().getBoolean(IWorkbenchPreferenceConstants.SHOW_FILTERED_TEXTS);
      createControl(SearchField.this, treeStyle);

      Job refreshJob = new Job("Refresh Filter")
      {
        @Override
        protected IStatus run(IProgressMonitor monitor)
        {
          UIUtil.syncExec(new Runnable()
          {
            @Override
            public void run()
            {
              patternFilter.filter(treeViewer, (Object)null, null);
            }
          });

          return Status.OK_STATUS;
        }
      };

      refreshJob.setSystem(true);
      ReflectUtil.setValue(refreshJobField, this, refreshJob);

      setInitialText(org.eclipse.ui.internal.WorkbenchMessages.FilteredTree_FilterMessage);
      setFont(SearchField.this.getFont());
    }

    @Override
    protected void createControl(Composite xxx, int treeStyle)
    {
      super.createControl(SearchField.this, treeStyle);

      Tree tree = treeViewer.getTree();
      tree.setParent(SearchField.this);
      tree.setLayoutData(new GridData(0, 0));

      treeComposite.dispose();
      treeComposite = null;
    }

    @Override
    public String getInitialText()
    {
      return super.getInitialText();
    }
  }

  /**
   * @author Eike Stepper
   */
  public interface FilterHandler
  {
    public void handleFilter(String filter);
  }
}
