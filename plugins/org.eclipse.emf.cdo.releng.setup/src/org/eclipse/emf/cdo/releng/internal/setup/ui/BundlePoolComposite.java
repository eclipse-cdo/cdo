/*
 * Copyright (c) 2004-2014 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.internal.setup.ui;

import org.eclipse.emf.cdo.releng.internal.setup.Activator;
import org.eclipse.emf.cdo.releng.internal.setup.targlets.P2;
import org.eclipse.emf.cdo.releng.internal.setup.ui.BundlePoolComposite.Agent.AgentListener;
import org.eclipse.emf.cdo.releng.internal.setup.ui.BundlePoolComposite.Agent.BundlePool;
import org.eclipse.emf.cdo.releng.internal.setup.ui.BundlePoolComposite.Agent.BundlePool.Artifact;
import org.eclipse.emf.cdo.releng.internal.setup.ui.BundlePoolComposite.Agent.BundlePool.Profile;
import org.eclipse.emf.cdo.releng.internal.setup.util.UpdateUtil;
import org.eclipse.emf.cdo.releng.setup.util.UIUtil;

import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.om.monitor.SubMonitor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.engine.IProfile;
import org.eclipse.equinox.p2.engine.IProfileRegistry;
import org.eclipse.equinox.p2.metadata.IArtifactKey;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.query.QueryUtil;
import org.eclipse.equinox.p2.repository.IRepositoryManager;
import org.eclipse.equinox.p2.repository.artifact.ArtifactKeyQuery;
import org.eclipse.equinox.p2.repository.artifact.IArtifactDescriptor;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepository;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepositoryManager;
import org.eclipse.equinox.p2.repository.artifact.IFileArtifactRepository;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * @author Eike Stepper
 */
public class BundlePoolComposite extends Composite
{
  private static final int TABLE_STYLE = SWT.BORDER | SWT.FULL_SELECTION | SWT.NO_SCROLL | SWT.V_SCROLL;

  private final Set<ISelectionProvider> changingSelection = new HashSet<ISelectionProvider>();

  private TableViewer bundlePoolsViewer;

  private TableViewer unusedArtifactsViewer;

  private TableViewer damagedArchivesViewer;

  private TableViewer profilesViewer;

  private Button deleteUnusedArtifactButton;

  private Button deleteAllUnusedArtifactsButton;

  private Button repairDamagedArchiveButton;

  private Button repairAllDamagedArchivesButton;

  // private Button deleteProfileButton;

  private Agent agent;

  public BundlePoolComposite(final Composite parent, int style)
  {
    super(parent, style);
    final Display display = getDisplay();

    GridLayout gridLayout = new GridLayout(1, false);
    gridLayout.marginWidth = 10;
    gridLayout.marginHeight = 10;
    setLayout(gridLayout);

    Composite composite_3 = new Composite(this, SWT.NONE);
    GridLayout gl_composite_3 = new GridLayout(1, false);
    gl_composite_3.marginWidth = 0;
    gl_composite_3.marginHeight = 0;
    composite_3.setLayout(gl_composite_3);
    composite_3.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    bundlePoolsViewer = new TableViewer(composite_3, TABLE_STYLE);
    Table bundlePoolsTable = bundlePoolsViewer.getTable();
    bundlePoolsTable.setHeaderVisible(true);
    GridData gd_bundlePoolsTable = new GridData(SWT.FILL, SWT.FILL, true, false);
    gd_bundlePoolsTable.heightHint = 84;
    bundlePoolsTable.setLayoutData(gd_bundlePoolsTable);

    TableColumn bundlePoolColumn = new TableViewerColumn(bundlePoolsViewer, SWT.NONE).getColumn();
    bundlePoolColumn.setText("Bundle Pool");
    bundlePoolColumn.setWidth(411);
    bundlePoolColumn.setResizable(false);

    TableColumn bundlePoolArtifactsColumn = new TableViewerColumn(bundlePoolsViewer, SWT.NONE).getColumn();
    bundlePoolArtifactsColumn.setText("Artifacts");
    bundlePoolArtifactsColumn.setAlignment(SWT.RIGHT);
    bundlePoolArtifactsColumn.setWidth(68);

    TableColumn bundlePoolUnusedArtifactsColumn = new TableViewerColumn(bundlePoolsViewer, SWT.NONE).getColumn();
    bundlePoolUnusedArtifactsColumn.setText("Unused Artifacts");
    bundlePoolUnusedArtifactsColumn.setAlignment(SWT.RIGHT);
    bundlePoolUnusedArtifactsColumn.setWidth(108);

    TableColumn bundlePoolDamagedArchivesColumn = new TableViewerColumn(bundlePoolsViewer, SWT.NONE).getColumn();
    bundlePoolDamagedArchivesColumn.setText("Damaged Archives");
    bundlePoolDamagedArchivesColumn.setAlignment(SWT.RIGHT);
    bundlePoolDamagedArchivesColumn.setWidth(114);

    TableColumn bundlePoolProfilesColumn = new TableViewerColumn(bundlePoolsViewer, SWT.NONE).getColumn();
    bundlePoolProfilesColumn.setText("Profiles");
    bundlePoolProfilesColumn.setAlignment(SWT.RIGHT);
    bundlePoolProfilesColumn.setWidth(72);

    bundlePoolsViewer.setLabelProvider(new TableLabelProvider(display));
    bundlePoolsViewer.setContentProvider(new BundlePoolsContentProvider());

    // Composite composite = new Composite(composite_3, SWT.NONE);
    // composite.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, false));
    // GridLayout gl_composite = new GridLayout(2, false);
    // gl_composite.marginWidth = 0;
    // gl_composite.marginHeight = 0;
    // composite.setLayout(gl_composite);
    //
    // final Button bundlePoolMoveButton = new Button(composite, SWT.NONE);
    // bundlePoolMoveButton.setText("Move...");
    // bundlePoolMoveButton.setEnabled(false);
    // bundlePoolMoveButton.addSelectionListener(new SelectionAdapter()
    // {
    // @Override
    // public void widgetSelected(SelectionEvent e)
    // {
    // moveBundlePool(getCurrentBundlePool());
    // }
    // });
    //
    // final Button bundlePoolDeleteButton = new Button(composite, SWT.NONE);
    // bundlePoolDeleteButton.setText("Delete All Artifacts...");
    // bundlePoolDeleteButton.setEnabled(false);
    // bundlePoolDeleteButton.addSelectionListener(new SelectionAdapter()
    // {
    // @Override
    // public void widgetSelected(SelectionEvent e)
    // {
    // deleteBundlePool(getCurrentBundlePool());
    // }
    // });

    new Label(composite_3, SWT.NONE);

    SashForm verticalSashForm = new SashForm(this, SWT.VERTICAL);
    verticalSashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

    SashForm horizontalSashForm = new SashForm(verticalSashForm, SWT.HORIZONTAL);

    Composite unusedArtifactsComposite = new Composite(horizontalSashForm, SWT.NONE);
    GridLayout unusedArtifactsLayout = new GridLayout(1, false);
    unusedArtifactsLayout.marginWidth = 0;
    unusedArtifactsLayout.marginHeight = 0;
    unusedArtifactsComposite.setLayout(unusedArtifactsLayout);

    unusedArtifactsViewer = new TableViewer(unusedArtifactsComposite, TABLE_STYLE | SWT.MULTI);
    Table unusedArtifactsTable = unusedArtifactsViewer.getTable();
    unusedArtifactsTable.setHeaderVisible(true);
    unusedArtifactsTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    new SelectAllAdapter(unusedArtifactsViewer);

    TableColumn unusedArtifactsColumn = new TableViewerColumn(unusedArtifactsViewer, SWT.NONE).getColumn();
    unusedArtifactsColumn.setText("Unused Artifact");
    unusedArtifactsColumn.setWidth(381);
    unusedArtifactsColumn.setResizable(false);

    unusedArtifactsViewer.setContentProvider(new UnusedArtifactsContentProvider());
    unusedArtifactsViewer.setLabelProvider(new TableLabelProvider(display));

    Composite unusedArtifactsButtonBar = new Composite(unusedArtifactsComposite, SWT.NONE);
    unusedArtifactsButtonBar.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, false));
    GridLayout unusedArtifactsButtonBarLayout = new GridLayout(2, false);
    unusedArtifactsButtonBarLayout.marginWidth = 0;
    unusedArtifactsButtonBarLayout.marginHeight = 0;
    unusedArtifactsButtonBar.setLayout(unusedArtifactsButtonBarLayout);

    deleteUnusedArtifactButton = new Button(unusedArtifactsButtonBar, SWT.NONE);
    deleteUnusedArtifactButton.setText("Delete");
    deleteUnusedArtifactButton.setEnabled(false);
    deleteUnusedArtifactButton.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        Artifact[] artifacts = getSelectedArtifacts(unusedArtifactsViewer);
        deleteUnusedArtifacts(artifacts);
      }
    });

    deleteAllUnusedArtifactsButton = new Button(unusedArtifactsButtonBar, SWT.NONE);
    deleteAllUnusedArtifactsButton.setText("Delete All");
    deleteAllUnusedArtifactsButton.setEnabled(false);
    deleteAllUnusedArtifactsButton.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        Artifact[] artifacts = getCurrentBundlePool().getUnusedArtifacts();
        deleteUnusedArtifacts(artifacts);
      }
    });

    new Label(unusedArtifactsComposite, SWT.NONE);

    Composite damagedArchivesComposite = new Composite(horizontalSashForm, SWT.NONE);
    GridLayout damagedArchivesLayout = new GridLayout(1, false);
    damagedArchivesLayout.marginWidth = 0;
    damagedArchivesLayout.marginHeight = 0;
    damagedArchivesComposite.setLayout(damagedArchivesLayout);

    damagedArchivesViewer = new TableViewer(damagedArchivesComposite, TABLE_STYLE | SWT.MULTI);
    Table damagedArchivesTable = damagedArchivesViewer.getTable();
    damagedArchivesTable.setHeaderVisible(true);
    damagedArchivesTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    new SelectAllAdapter(damagedArchivesViewer);

    TableColumn damagedArchivesColumn = new TableViewerColumn(damagedArchivesViewer, SWT.NONE).getColumn();
    damagedArchivesColumn.setText("Damaged Archive");
    damagedArchivesColumn.setWidth(382);
    damagedArchivesColumn.setResizable(false);

    damagedArchivesViewer.setContentProvider(new DamagedArchivesContentProvider());
    damagedArchivesViewer.setLabelProvider(new TableLabelProvider(display));

    Composite damagedArchivesButtonBar = new Composite(damagedArchivesComposite, SWT.NONE);
    damagedArchivesButtonBar.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, false));
    GridLayout damagedArchivesButtonBarLayout = new GridLayout(2, false);
    damagedArchivesButtonBarLayout.marginHeight = 0;
    damagedArchivesButtonBarLayout.marginWidth = 0;
    damagedArchivesButtonBar.setLayout(damagedArchivesButtonBarLayout);

    repairDamagedArchiveButton = new Button(damagedArchivesButtonBar, SWT.NONE);
    repairDamagedArchiveButton.setText("Repair");
    repairDamagedArchiveButton.setEnabled(false);
    repairDamagedArchiveButton.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        Artifact[] artifacts = getSelectedArtifacts(damagedArchivesViewer);
        repairDamagedArchives(artifacts);
      }
    });

    repairAllDamagedArchivesButton = new Button(damagedArchivesButtonBar, SWT.NONE);
    repairAllDamagedArchivesButton.setEnabled(false);
    repairAllDamagedArchivesButton.setText("Repair All");
    repairAllDamagedArchivesButton.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        Artifact[] artifacts = getCurrentBundlePool().getDamagedArchives();
        repairDamagedArchives(artifacts);
      }
    });

    new Label(damagedArchivesComposite, SWT.NONE);
    horizontalSashForm.setWeights(new int[] { 1, 1 });

    Composite profilesComposite = new Composite(verticalSashForm, SWT.NONE);
    GridLayout profilesLayout = new GridLayout(1, false);
    profilesLayout.marginWidth = 0;
    profilesLayout.marginHeight = 0;
    profilesComposite.setLayout(profilesLayout);

    profilesViewer = new TableViewer(profilesComposite, TABLE_STYLE | SWT.MULTI);
    Table profilesTable = profilesViewer.getTable();
    profilesTable.setHeaderVisible(true);
    profilesTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    new SelectAllAdapter(profilesViewer);

    TableColumn profileColumn = new TableViewerColumn(profilesViewer, SWT.NONE).getColumn();
    profileColumn.setText("Profile");
    profileColumn.setWidth(581);
    profileColumn.setResizable(false);

    TableColumn profileArtifactsColumn = new TableViewerColumn(profilesViewer, SWT.NONE).getColumn();
    profileArtifactsColumn.setText("Artifacts");
    profileArtifactsColumn.setAlignment(SWT.RIGHT);
    profileArtifactsColumn.setWidth(73);

    TableColumn profileDamagedArchivesColumn = new TableViewerColumn(profilesViewer, SWT.NONE).getColumn();
    profileDamagedArchivesColumn.setText("Damaged Archives");
    profileDamagedArchivesColumn.setAlignment(SWT.RIGHT);
    profileDamagedArchivesColumn.setWidth(125);

    profilesViewer.setContentProvider(new ProfilesContentProvider());
    profilesViewer.setLabelProvider(new TableLabelProvider(display));

    // Composite profilesButtonBar = new Composite(profilesComposite, SWT.NONE);
    // GridLayout profilesButtonBarLayout = new GridLayout(1, false);
    // profilesButtonBarLayout.marginWidth = 0;
    // profilesButtonBarLayout.marginHeight = 0;
    // profilesButtonBar.setLayout(profilesButtonBarLayout);
    // profilesButtonBar.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
    //
    // deleteProfileButton = new Button(profilesButtonBar, SWT.NONE);
    // deleteProfileButton.setText("Delete...");
    // deleteProfileButton.setEnabled(false);
    // deleteProfileButton.addSelectionListener(new SelectionAdapter()
    // {
    // @Override
    // public void widgetSelected(SelectionEvent e)
    // {
    // Profile[] profiles = getSelectedProfiles(profilesViewer);
    // deleteProfiles(profiles);
    // }
    // });

    verticalSashForm.setWeights(new int[] { 1, 1 });

    bundlePoolsViewer.addSelectionChangedListener(new SelectionChangedListener()
    {
      @Override
      protected void doSelectionChanged(SelectionChangedEvent event)
      {
        BundlePool bundlePool = getCurrentBundlePool();

        unusedArtifactsViewer.setInput(bundlePool);
        damagedArchivesViewer.setInput(bundlePool);
        profilesViewer.setInput(bundlePool);

        // bundlePoolMoveButton.setEnabled(true);
        // bundlePoolDeleteButton.setEnabled(true);

        updateUnusedArtifactButtons(bundlePool);
        updateDamagedArchiveButtons(bundlePool);
        // updateProfileButtons(bundlePool);
      }
    });

    unusedArtifactsViewer.addSelectionChangedListener(new SelectionChangedListener()
    {
      @Override
      protected void doSelectionChanged(SelectionChangedEvent event)
      {
        updateUnusedArtifactButtons(getCurrentBundlePool());
      }

      @Override
      protected void triggerOtherSelections(SelectionChangedEvent event)
      {
        if (!changingSelection.contains(damagedArchivesViewer))
        {
          damagedArchivesViewer.setSelection(unusedArtifactsViewer.getSelection());
        }
      }
    });

    damagedArchivesViewer.addSelectionChangedListener(new SelectionChangedListener()
    {
      @Override
      protected void doSelectionChanged(SelectionChangedEvent event)
      {
        updateDamagedArchiveButtons(getCurrentBundlePool());
      }

      @Override
      protected void triggerOtherSelections(SelectionChangedEvent event)
      {
        IStructuredSelection selection = (IStructuredSelection)damagedArchivesViewer.getSelection();
        if (!changingSelection.contains(unusedArtifactsViewer))
        {
          unusedArtifactsViewer.setSelection(selection);
        }

        if (!changingSelection.contains(profilesViewer))
        {
          Object[] artifacts = selection.toArray();
          List<Profile> profiles = new ArrayList<Profile>();
          for (Profile profile : getCurrentBundlePool().getProfiles())
          {
            for (int i = 0; i < artifacts.length; i++)
            {
              if (profile.getArtifacts().contains(artifacts[i]))
              {
                profiles.add(profile);
                break;
              }
            }
          }

          profilesViewer.setSelection(new StructuredSelection(profiles));
        }
      }
    });

    profilesViewer.addSelectionChangedListener(new SelectionChangedListener()
    {
      @Override
      protected void doSelectionChanged(SelectionChangedEvent event)
      {
        // BundlePool bundlePool = getCurrentBundlePool();
        // updateProfileButtons(bundlePool);
      }

      @Override
      protected void triggerOtherSelections(SelectionChangedEvent event)
      {
        if (!changingSelection.contains(damagedArchivesViewer))
        {
          Profile[] profiles = getSelectedProfiles(profilesViewer);
          Set<Artifact> artifacts = new HashSet<Artifact>();
          for (Profile profile : profiles)
          {
            artifacts.addAll(profile.getDamagedArchives());
          }

          damagedArchivesViewer.setSelection(new StructuredSelection(new ArrayList<Artifact>(artifacts)));
        }
      }
    });

    addDisposeListener(new DisposeListener()
    {
      public void widgetDisposed(DisposeEvent e)
      {
        if (agent != null)
        {
          agent.dispose();
        }
      }
    });

    final Shell shell = getShell();
    final Cursor oldCursor = shell.getCursor();
    shell.setCursor(new Cursor(display, SWT.CURSOR_WAIT));
    setEnabled(false);

    display.asyncExec(new Runnable()
    {
      public void run()
      {
        initAgent();
        shell.setCursor(oldCursor);
        setEnabled(true);

        bundlePoolsViewer.setInput(agent);

        display.asyncExec(new Runnable()
        {
          public void run()
          {
            Collection<BundlePool> bundlePools = agent.getBundlePools().values();
            if (!bundlePools.isEmpty())
            {
              bundlePoolsViewer.setSelection(new StructuredSelection(bundlePools.iterator().next()));
            }
          }
        });
      }
    });
  }

  @Override
  protected void checkSubclass()
  {
    // Disable the check that prevents subclassing of SWT components
  }

  private void initAgent()
  {
    final AgentListener agentListener = new AgentListener()
    {
      public void addedBundlePool(BundlePool bundlePool)
      {
      }

      public void addedBundlePoolArtifact(final Artifact artifact)
      {
        asyncExec(new Runnable()
        {
          public void run()
          {
            bundlePoolsViewer.update(artifact.getBundlePool(), null);
          }
        });
      }

      public void addedProfile(Profile profile)
      {
      }

      public void addedProfileArtifact(final Profile profile, Artifact artifact)
      {
        asyncExec(new Runnable()
        {
          public void run()
          {
            profilesViewer.update(profile, null);
          }
        });
      }

      public void changedUnusedArtifacts(final BundlePool bundlePool)
      {
        asyncExec(new Runnable()
        {
          public void run()
          {
            bundlePoolsViewer.update(bundlePool, null);
            unusedArtifactsViewer.refresh();
            updateUnusedArtifactButtons(bundlePool);
          }
        });
      }

      public void damageStateChanged(final Artifact artifact)
      {
        asyncExec(new Runnable()
        {
          public void run()
          {
            BundlePool bundlePool = artifact.getBundlePool();
            bundlePoolsViewer.update(bundlePool, null);

            unusedArtifactsViewer.refresh(true);
            damagedArchivesViewer.refresh(true);
            profilesViewer.refresh(true);

            updateUnusedArtifactButtons(bundlePool);
            updateDamagedArchiveButtons(bundlePool);
          }
        });
      }

      public void progressedDamagedArchiveAnalysis(final BundlePool bundlePool, int percent)
      {
        asyncExec(new Runnable()
        {
          public void run()
          {
            bundlePoolsViewer.update(bundlePool, null);
            updateDamagedArchiveButtons(bundlePool);
          }
        });
      }

      public void finishedDamagedArchiveAnalysis(final BundlePool bundlePool)
      {
        asyncExec(new Runnable()
        {
          public void run()
          {
            updateDamagedArchiveButtons(bundlePool);
          }
        });
      }
    };

    agent = new Agent(agentListener);
  }

  private void asyncExec(Runnable runnable)
  {
    if (!isDisposed())
    {
      Display display = getDisplay();
      UIUtil.asyncExec(display, runnable);
    }
  }

  private BundlePool getCurrentBundlePool()
  {
    return (BundlePool)((IStructuredSelection)bundlePoolsViewer.getSelection()).getFirstElement();
  }

  private Artifact[] getSelectedArtifacts(TableViewer viewer)
  {
    IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();

    @SuppressWarnings("unchecked")
    List<Artifact> artifacts = (List<Artifact>)(List<?>)selection.toList();
    return artifacts.toArray(new Artifact[artifacts.size()]);
  }

  private Profile[] getSelectedProfiles(TableViewer viewer)
  {
    IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();

    @SuppressWarnings("unchecked")
    List<Profile> profiles = (List<Profile>)(List<?>)selection.toList();
    return profiles.toArray(new Profile[profiles.size()]);
  }

  private void updateButton(TableViewer viewer, Button button, String text, boolean prompt)
  {
    IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
    int size = selection.size();
    if (size > 1)
    {
      text += " (" + size + ")";
    }

    if (prompt)
    {
      text += "...";
    }

    if (!button.getText().equals(text))
    {
      button.setText(text);
      button.getParent().getParent().layout(true);
    }

    button.setEnabled(size != 0);
  }

  private void updateUnusedArtifactButtons(BundlePool bundlePool)
  {
    if (bundlePool == getCurrentBundlePool())
    {
      updateButton(unusedArtifactsViewer, deleteUnusedArtifactButton, "Delete", false);
      deleteAllUnusedArtifactsButton.setEnabled(bundlePool.getUnusedArtifactsCount() != 0);
    }
  }

  private void updateDamagedArchiveButtons(BundlePool bundlePool)
  {
    if (bundlePool == getCurrentBundlePool())
    {
      updateButton(damagedArchivesViewer, repairDamagedArchiveButton, "Repair", false);
      repairAllDamagedArchivesButton.setEnabled(bundlePool.getDamagedArchivesPercent() == 100
          && bundlePool.getDamagedArchivesCount() != 0);
    }
  }

  // private void updateProfileButtons(BundlePool bundlePool)
  // {
  // if (bundlePool == getCurrentBundlePool())
  // {
  // updateButton(profilesViewer, deleteProfileButton, "Delete", true);
  // }
  // }
  //
  // private void moveBundlePool(BundlePool bundlePool)
  // {
  // MessageDialog.openInformation(getShell(), AbstractSetupDialog.SHELL_TEXT, "Not yet implemented.");
  // }
  //
  // private void deleteBundlePool(final BundlePool bundlePool)
  // {
  // MessageDialog.openInformation(getShell(), AbstractSetupDialog.SHELL_TEXT, "Not yet implemented.");
  // }

  private void deleteUnusedArtifacts(final Artifact[] artifacts)
  {
    try
    {
      UIUtil.runInProgressDialog(getShell(), new IRunnableWithProgress()
      {
        public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
        {
          SubMonitor progress = SubMonitor.convert(monitor, "Deleting artifacts", artifacts.length).detectCancelation();
          BundlePool bundlePool = artifacts[0].getBundlePool();

          for (Artifact artifact : artifacts)
          {
            if (artifact.deleteIfUnused(progress))
            {
              artifact.setDamaged(false);
              agent.listener.damageStateChanged(artifact);
              agent.listener.changedUnusedArtifacts(bundlePool);
            }
          }
        }
      });
    }
    catch (InvocationTargetException ex)
    {
      ErrorDialog.open(ex);
    }
    catch (InterruptedException ex)
    {
      throw new OperationCanceledException();
    }
  }

  private void repairDamagedArchives(final Artifact[] artifacts)
  {
    try
    {
      UIUtil.runInProgressDialog(getShell(), new IRunnableWithProgress()
      {
        public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
        {
          SubMonitor progress = SubMonitor.convert(monitor, "Repairing archives", artifacts.length).detectCancelation();

          IArtifactRepositoryManager repositoryManager = P2.getArtifactRepositoryManager();
          Set<URI> repositoryURIs = artifacts[0].getBundlePool().getRepositoryURIs();

          for (Artifact artifact : artifacts)
          {
            if (repairDamagedArchive(artifact, repositoryManager, repositoryURIs, progress.newChild()))
            {
              artifact.setDamaged(false);

              asyncExec(new Runnable()
              {
                public void run()
                {
                  damagedArchivesViewer.refresh();
                }
              });
            }
          }
        }
      });
    }
    catch (InvocationTargetException ex)
    {
      ErrorDialog.open(ex);
    }
    catch (InterruptedException ex)
    {
      throw new OperationCanceledException();
    }
  }

  private boolean repairDamagedArchive(Artifact artifact, IArtifactRepositoryManager repositoryManager,
      Set<URI> repositoryURIs, IProgressMonitor monitor)
  {
    SubMonitor progress = SubMonitor.convert(monitor, 2 * repositoryURIs.size()).detectCancelation();
    BundlePool bundlePool = artifact.getBundlePool();

    if (artifact.deleteIfUnused(progress))
    {
      agent.listener.changedUnusedArtifacts(bundlePool);
      return true;
    }

    for (URI uri : repositoryURIs)
    {
      if (repairDamagedArchive(artifact, uri, repositoryManager, progress))
      {
        return true;
      }
    }

    Set<URI> allURIs = new LinkedHashSet<URI>(bundlePool.getAgent().getRepositoryURIs());
    allURIs.removeAll(repositoryURIs);

    for (URI uri : allURIs)
    {
      if (repairDamagedArchive(artifact, uri, repositoryManager, progress))
      {
        return true;
      }
    }

    return false;
  }

  private boolean repairDamagedArchive(Artifact artifact, URI uri, IArtifactRepositoryManager repositoryManager,
      SubMonitor progress)
  {
    try
    {
      IArtifactKey key = artifact.getKey();
      File file = artifact.getFile();

      IArtifactRepository repository = repositoryManager.loadRepository(uri, progress.newChild());
      for (IArtifactDescriptor descriptor : repository.getArtifactDescriptors(key))
      {
        File tmp = new File(file.getAbsolutePath() + ".tmp");
        FileOutputStream destination = null;

        try
        {
          destination = new FileOutputStream(tmp);

          IStatus status = repository.getArtifact(descriptor, destination, progress.newChild());
          if (status.getSeverity() == IStatus.OK)
          {
            IOUtil.close(destination);
            IOUtil.copyFile(tmp, file);
            return true;
          }
        }
        finally
        {
          IOUtil.close(destination);
          if (!tmp.delete())
          {
            tmp.deleteOnExit();
          }
        }
      }
    }
    catch (OperationCanceledException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      Activator.log(ex);
    }

    return false;
  }

  // private void deleteProfiles(Profile[] profiles)
  // {
  // MessageDialog.openInformation(getShell(), AbstractSetupDialog.SHELL_TEXT, "Not yet implemented.");
  // }

  public static void openDialog(Shell shell)
  {
    Dialog dialog = new Dialog(shell);
    dialog.open();
  }

  /**
   * @author Eike Stepper
   */
  private static final class Dialog extends AbstractSetupDialog
  {
    Dialog(Shell parentShell)
    {
      super(parentShell, "Bundle Pool Management", 850, 750);
      setShellStyle(SWT.TITLE | SWT.MAX | SWT.RESIZE | SWT.BORDER | SWT.APPLICATION_MODAL);
    }

    @Override
    protected String getDefaultMessage()
    {
      return "Manage your bundle pools, delete unused artifacts and repair damaged archives";
    }

    @Override
    protected void createUI(Composite parent)
    {
      BundlePoolComposite bundlePoolComposite = new BundlePoolComposite(parent, SWT.NONE);
      bundlePoolComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent)
    {
      createButton(parent, IDialogConstants.OK_ID, IDialogConstants.CLOSE_LABEL, true);
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class SelectAllAdapter extends KeyAdapter
  {
    private final StructuredViewer viewer;

    public SelectAllAdapter(StructuredViewer viewer)
    {
      this.viewer = viewer;
      viewer.getControl().addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
      if ((e.stateMask & SWT.CONTROL) != 0 && e.keyCode == 'a')
      {
        IStructuredContentProvider contentProvider = (IStructuredContentProvider)viewer.getContentProvider();
        viewer.setSelection(new StructuredSelection(contentProvider.getElements(viewer.getInput())));
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private abstract class SelectionChangedListener implements ISelectionChangedListener
  {
    public final void selectionChanged(SelectionChangedEvent event)
    {
      doSelectionChanged(event);

      if (changingSelection.add(event.getSelectionProvider()))
      {
        try
        {
          triggerOtherSelections(event);
        }
        finally
        {
          changingSelection.remove(event.getSelectionProvider());
        }
      }
    }

    protected abstract void doSelectionChanged(SelectionChangedEvent event);

    protected void triggerOtherSelections(SelectionChangedEvent event)
    {
      // Subclasses may override
    }
  }

  /**
   * @author Eike Stepper
   */
  protected static abstract class ColumnResizer extends ControlAdapter implements IStructuredContentProvider
  {
    private Table table;

    private int lastWidth = -1;

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
      if (table == null)
      {
        table = ((TableViewer)viewer).getTable();
        table.addControlListener(this);
      }

      UIUtil.asyncExec(table.getDisplay(), new Runnable()
      {
        public void run()
        {
          controlResized(null);
        }
      });
    }

    @Override
    public void controlResized(ControlEvent e)
    {
      int tableWidth = table.getSize().x;
      if (tableWidth != lastWidth)
      {
        lastWidth = tableWidth;

        ScrollBar bar = table.getVerticalBar();
        if (bar != null && bar.isVisible())
        {
          tableWidth -= bar.getSize().x;
        }

        final TableColumn[] columns = table.getColumns();
        for (int i = 1; i < columns.length; i++)
        {
          tableWidth -= columns[i].getWidth();
        }

        columns[0].setWidth(tableWidth);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class BundlePoolsContentProvider extends ColumnResizer
  {
    private Agent agent;

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
      super.inputChanged(viewer, oldInput, newInput);
      agent = (Agent)newInput;
    }

    public Object[] getElements(Object inputElement)
    {
      List<BundlePool> bundlePools = new ArrayList<BundlePool>(agent.getBundlePools().values());
      Collections.sort(bundlePools);
      return bundlePools.toArray();
    }

    public void dispose()
    {
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class UnusedArtifactsContentProvider extends ColumnResizer
  {
    private BundlePool bundlePool;

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
      super.inputChanged(viewer, oldInput, newInput);
      bundlePool = (BundlePool)newInput;
    }

    public Object[] getElements(Object inputElement)
    {
      return bundlePool.getUnusedArtifacts();
    }

    public void dispose()
    {
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class DamagedArchivesContentProvider extends ColumnResizer
  {
    private BundlePool bundlePool;

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
      super.inputChanged(viewer, oldInput, newInput);
      bundlePool = (BundlePool)newInput;
    }

    public Object[] getElements(Object inputElement)
    {
      return bundlePool.getDamagedArchives();
    }

    public void dispose()
    {
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class ProfilesContentProvider extends ColumnResizer
  {
    private BundlePool bundlePool;

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
      super.inputChanged(viewer, oldInput, newInput);
      bundlePool = (BundlePool)newInput;
    }

    public Object[] getElements(Object inputElement)
    {
      return bundlePool.getProfiles();
    }

    public void dispose()
    {
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class TableLabelProvider extends LabelProvider implements ITableLabelProvider, IColorProvider
  {
    private final Color gray;

    public TableLabelProvider(Display display)
    {
      gray = display.getSystemColor(SWT.COLOR_DARK_GRAY);
    }

    public String getColumnText(Object element, int columnIndex)
    {
      if (element instanceof BundlePool)
      {
        BundlePool bundlePool = (BundlePool)element;
        switch (columnIndex)
        {
        case 0:
          return bundlePool.getLocation().getAbsolutePath();
        case 1:
          return Integer.toString(bundlePool.getArtifacts().size());
        case 2:
          return Integer.toString(bundlePool.getUnusedArtifactsCount());
        case 3:
          int percent = bundlePool.getDamagedArchivesPercent();
          return Integer.toString(bundlePool.getDamagedArchivesCount()) + (percent == 100 ? "" : " (" + percent + "%)");
        case 4:
          return Integer.toString(bundlePool.getProfilesCount());
        }
      }
      else if (element instanceof Artifact)
      {
        Artifact artifact = (Artifact)element;
        return artifact.getRelativePath();
      }
      else if (element instanceof Profile)
      {
        Profile profile = (Profile)element;
        switch (columnIndex)
        {
        case 0:
          return profile.getID();
        case 1:
          return Integer.toString(profile.getArtifacts().size());
        case 2:
          return Integer.toString(profile.getDamagedArchivesCount());
        }
      }

      return element.toString();
    }

    public Image getColumnImage(Object element, int columnIndex)
    {
      if (columnIndex == 0)
      {
        if (element instanceof BundlePool)
        {
          BundlePool bundlePool = (BundlePool)element;
          return ResourceManager.getPluginImage("org.eclipse.emf.cdo.releng.setup", "icons/obj16/bundlePool"
              + (bundlePool.getDamagedArchivesCount() != 0 ? "Damaged" : "") + ".gif");
        }

        if (element instanceof Artifact)
        {
          Artifact artifact = (Artifact)element;
          return ResourceManager.getPluginImage(
              "org.eclipse.emf.cdo.releng.setup",
              artifact.isFolder() ? "icons/obj16/artifactFolder.gif" : "icons/obj16/artifactArchive"
                  + (artifact.isDamaged() ? "Damaged" : "") + ".gif");
        }

        if (element instanceof Profile)
        {
          Profile profile = (Profile)element;
          return ResourceManager.getPluginImage("org.eclipse.emf.cdo.releng.setup",
              "icons/obj16/profile" + profile.getType() + (profile.isDamaged() ? "Damaged" : "") + ".gif");
        }
      }

      return null;
    }

    public Color getForeground(Object element)
    {
      if (element instanceof Artifact)
      {
        Artifact artifact = (Artifact)element;
        if (artifact.isUnused())
        {
          return gray;
        }
      }

      return null;
    }

    public Color getBackground(Object element)
    {
      return null;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class Agent
  {
    private final AgentListener listener;

    private final Map<File, BundlePool> bundlePools = new HashMap<File, BundlePool>();

    private final List<Job> analyzeProfileJobs = new ArrayList<Job>();

    private Set<URI> repositoryURIs;

    public Agent(AgentListener listener)
    {
      this.listener = listener;

      IProfileRegistry profileRegistry = P2.getProfileRegistry();
      for (IProfile p2Profile : profileRegistry.getProfiles())
      {
        String installFolder = p2Profile.getProperty(IProfile.PROP_INSTALL_FOLDER);
        String cache = p2Profile.getProperty(IProfile.PROP_CACHE);
        if (cache != null && !cache.equals(installFolder))
        {
          File location = new File(cache);

          BundlePool bundlePool = bundlePools.get(location);
          if (bundlePool == null)
          {
            bundlePool = new BundlePool(this, location);
            bundlePools.put(location, bundlePool);

            listener.addedBundlePool(bundlePool);
          }

          bundlePool.addProfile(p2Profile, installFolder);
        }
      }

      for (BundlePool bundlePool : bundlePools.values())
      {
        Job job = bundlePool.analyze();
        analyzeProfileJobs.add(job);
      }
    }

    public void dispose()
    {
      for (Job job : analyzeProfileJobs)
      {
        job.cancel();
      }

      analyzeProfileJobs.clear();
      bundlePools.clear();
    }

    public Map<File, BundlePool> getBundlePools()
    {
      return bundlePools;
    }

    public Set<URI> getRepositoryURIs()
    {
      if (repositoryURIs == null)
      {
        repositoryURIs = new LinkedHashSet<URI>();

        IArtifactRepositoryManager repositoryManager = P2.getArtifactRepositoryManager();
        addURIs(repositoryURIs, repositoryManager, IRepositoryManager.REPOSITORIES_ALL);
        addURIs(repositoryURIs, repositoryManager, IRepositoryManager.REPOSITORIES_DISABLED);
        addURIs(repositoryURIs, repositoryManager, IRepositoryManager.REPOSITORIES_LOCAL);
        addURIs(repositoryURIs, repositoryManager, IRepositoryManager.REPOSITORIES_NON_LOCAL);
        addURIs(repositoryURIs, repositoryManager, IRepositoryManager.REPOSITORIES_SYSTEM);
        addURIs(repositoryURIs, repositoryManager, IRepositoryManager.REPOSITORIES_NON_SYSTEM);

        for (BundlePool bundlePool : bundlePools.values())
        {
          // Don't use possibly damaged local bundle pools for damage repair
          repositoryURIs.remove(bundlePool.getLocation().toURI());
        }
      }

      return repositoryURIs;
    }

    private void addURIs(Set<URI> repos, IArtifactRepositoryManager repositoryManager, int flag)
    {
      for (URI uri : repositoryManager.getKnownRepositories(flag))
      {
        repos.add(uri);
      }
    }

    /**
     * @author Eike Stepper
     */
    public interface AgentListener
    {
      public void addedBundlePool(BundlePool bundlePool);

      public void addedBundlePoolArtifact(Artifact artifact);

      public void addedProfile(Profile profile);

      public void addedProfileArtifact(Profile profile, Artifact artifact);

      public void changedUnusedArtifacts(BundlePool bundlePool);

      public void progressedDamagedArchiveAnalysis(BundlePool bundlePool, int percent);

      public void finishedDamagedArchiveAnalysis(BundlePool bundlePool);

      public void damageStateChanged(Artifact artifact);
    }

    /**
     * @author Eike Stepper
     */
    public static final class BundlePool implements Comparable<BundlePool>
    {
      private final Agent agent;

      private final File location;

      private final Set<URI> repositoryURIs = new LinkedHashSet<URI>();

      private final List<Profile> profiles = new ArrayList<Profile>();

      private final Map<IArtifactKey, Artifact> artifacts = new HashMap<IArtifactKey, Artifact>();

      private final Set<Artifact> unusedArtifacts = new HashSet<Artifact>();

      private final Set<Artifact> damagedArchives = new HashSet<Artifact>();

      private int damagedArchivesPercent;

      public BundlePool(Agent agent, File location)
      {
        this.agent = agent;
        this.location = location;
      }

      public IFileArtifactRepository getP2BundlePool(IProgressMonitor monitor)
      {
        try
        {
          IArtifactRepositoryManager repositoryManager = P2.getArtifactRepositoryManager();
          return (IFileArtifactRepository)repositoryManager.loadRepository(location.toURI(), monitor);
        }
        catch (ProvisionException ex)
        {
          throw new IllegalStateException(ex);
        }
      }

      public Agent getAgent()
      {
        return agent;
      }

      public File getLocation()
      {
        return location;
      }

      public Set<URI> getRepositoryURIs()
      {
        return repositoryURIs;
      }

      public int getProfilesCount()
      {
        synchronized (profiles)
        {
          return profiles.size();
        }
      }

      public Profile[] getProfiles()
      {
        synchronized (profiles)
        {
          Collections.sort(profiles);
          return profiles.toArray(new Profile[profiles.size()]);
        }
      }

      public Profile addProfile(IProfile p2Profile, String installFolder)
      {
        Profile profile = new Profile(this, p2Profile, installFolder == null ? null : new File(installFolder));
        repositoryURIs.addAll(profile.getRepositoryURIs());

        synchronized (profiles)
        {
          profiles.add(profile);
        }

        agent.listener.addedProfile(profile);
        return profile;
      }

      public Map<IArtifactKey, Artifact> getArtifacts()
      {
        return artifacts;
      }

      public int getUnusedArtifactsCount()
      {
        synchronized (unusedArtifacts)
        {
          return unusedArtifacts.size();
        }
      }

      public Artifact[] getUnusedArtifacts()
      {
        Artifact[] array;
        synchronized (unusedArtifacts)
        {
          array = unusedArtifacts.toArray(new Artifact[unusedArtifacts.size()]);
        }

        Arrays.sort(array);
        return array;
      }

      public int getDamagedArchivesPercent()
      {
        return damagedArchivesPercent;
      }

      public int getDamagedArchivesCount()
      {
        synchronized (damagedArchives)
        {
          return damagedArchives.size();
        }
      }

      public Artifact[] getDamagedArchives()
      {
        Artifact[] array;
        synchronized (damagedArchives)
        {
          array = damagedArchives.toArray(new Artifact[damagedArchives.size()]);
        }

        Arrays.sort(array);
        return array;
      }

      public int compareTo(BundlePool o)
      {
        return location.getAbsolutePath().compareTo(o.getLocation().getAbsolutePath());
      }

      @Override
      public String toString()
      {
        return location.toString();
      }

      public Job analyze()
      {
        Job job = new Job("Analyzing bundle pool " + location)
        {
          @Override
          protected IStatus run(IProgressMonitor monitor)
          {
            analyze(monitor);
            return Status.OK_STATUS;
          }
        };

        job.schedule();
        return job;
      }

      private void analyze(IProgressMonitor monitor)
      {
        IFileArtifactRepository p2BundlePool = getP2BundlePool(monitor);
        for (IArtifactKey key : p2BundlePool.query(ArtifactKeyQuery.ALL_KEYS, monitor))
        {
          checkCancelation(monitor);

          File file = p2BundlePool.getArtifactFile(key);
          Artifact artifact = new Artifact(this, key, file);

          synchronized (artifacts)
          {
            artifacts.put(key, artifact);
          }

          agent.listener.addedBundlePoolArtifact(artifact);
        }

        analyzeUnusedArtifacts(monitor);
        analyzeDamagedArchives(monitor);
      }

      private void analyzeUnusedArtifacts(IProgressMonitor monitor)
      {
        synchronized (unusedArtifacts)
        {
          unusedArtifacts.addAll(artifacts.values());
        }

        for (Profile profile : getProfiles())
        {
          checkCancelation(monitor);

          profile.analyze(monitor);
          synchronized (unusedArtifacts)
          {
            unusedArtifacts.removeAll(profile.getArtifacts());
          }
        }

        agent.listener.changedUnusedArtifacts(this);
      }

      private void analyzeDamagedArchives(IProgressMonitor monitor)
      {
        int total = artifacts.size();
        int i = 0;

        for (Artifact artifact : artifacts.values())
        {
          checkCancelation(monitor);

          File file = artifact.getFile();
          if (file != null)
          {
            String path = file.getPath();
            if (path.endsWith(".jar") || path.endsWith(".zip"))
            {
              monitor.subTask("Validating " + file);
              if (!isValidZip(path))
              {
                artifact.setDamaged(true);
              }
            }
          }

          int percent = ++i * 100 / total;
          if (percent != damagedArchivesPercent)
          {
            damagedArchivesPercent = percent;
            agent.listener.progressedDamagedArchiveAnalysis(this, percent);
          }
        }

        agent.listener.finishedDamagedArchiveAnalysis(this);
      }

      private static boolean isValidZip(String path)
      {
        ZipInputStream in = null;
        ZipFile zip = null;

        try
        {
          zip = new ZipFile(path);
          in = new ZipInputStream(new FileInputStream(path));

          ZipEntry entry = in.getNextEntry();
          if (entry == null)
          {
            return false;
          }

          while (entry != null)
          {
            entry.getName();
            entry.getCompressedSize();
            entry.getCrc();

            zip.getInputStream(entry);

            entry = in.getNextEntry();
          }

          return true;
        }
        catch (Exception ex)
        {
          return false;
        }
        finally
        {
          IOUtil.close(zip);
          IOUtil.close(in);
        }
      }

      private static void checkCancelation(IProgressMonitor monitor)
      {
        if (monitor.isCanceled())
        {
          throw new OperationCanceledException();
        }
      }

      /**
       * @author Eike Stepper
       */
      public static final class Profile implements Comparable<Profile>
      {
        private final BundlePool bundlePool;

        private final IProfile p2Profile;

        private final File installFolder;

        private final String type;

        private final Set<URI> repositoryURIs = new LinkedHashSet<URI>();

        private final Set<Artifact> artifacts = new HashSet<Artifact>();

        public Profile(BundlePool bundlePool, IProfile p2Profile, File installFolder)
        {
          this.bundlePool = bundlePool;
          this.p2Profile = p2Profile;
          this.installFolder = installFolder;

          if (P2.isTargletProfile(p2Profile))
          {
            type = "Targlet";
          }
          else if (installFolder != null)
          {
            type = "Eclipse";
          }
          else
          {
            type = "Unknown";
          }

          String repoList = p2Profile.getProperty(UpdateUtil.PROP_REPO_LIST);
          if (repoList != null)
          {
            StringTokenizer tokenizer = new StringTokenizer(repoList, ",");
            while (tokenizer.hasMoreTokens())
            {
              String uri = tokenizer.nextToken();

              try
              {
                repositoryURIs.add(new URI(uri));
              }
              catch (URISyntaxException ex)
              {
                Activator.log(ex);
              }
            }
          }
        }

        public BundlePool getBundlePool()
        {
          return bundlePool;
        }

        public String getID()
        {
          return p2Profile.getProfileId();
        }

        public File getInstallFolder()
        {
          return installFolder;
        }

        public String getType()
        {
          return type;
        }

        public Set<URI> getRepositoryURIs()
        {
          return repositoryURIs;
        }

        public Set<Artifact> getArtifacts()
        {
          return artifacts;
        }

        public boolean isDamaged()
        {
          for (Artifact artifact : artifacts)
          {
            if (artifact.isDamaged())
            {
              return true;
            }
          }

          return false;
        }

        public int getDamagedArchivesCount()
        {
          int count = 0;
          for (Artifact artifact : artifacts)
          {
            if (artifact.isDamaged())
            {
              ++count;
            }
          }

          return count;
        }

        public List<Artifact> getDamagedArchives()
        {
          List<Artifact> list = new ArrayList<Artifact>();
          for (Artifact artifact : artifacts)
          {
            if (artifact.isDamaged())
            {
              list.add(artifact);
            }
          }

          return list;
        }

        public int compareTo(Profile o)
        {
          return getID().compareTo(o.getID());
        }

        public void analyze(IProgressMonitor monitor)
        {
          for (IInstallableUnit iu : p2Profile.query(QueryUtil.createIUAnyQuery(), monitor))
          {
            for (IArtifactKey key : iu.getArtifacts())
            {
              Artifact artifact = bundlePool.artifacts.get(key);
              if (artifact != null)
              {
                artifacts.add(artifact);
              }

              bundlePool.agent.listener.addedProfileArtifact(this, artifact);
            }
          }
        }

        @Override
        public String toString()
        {
          return getID();
        }
      }

      /**
       * @author Eike Stepper
       */
      public static final class Artifact implements Comparable<Artifact>
      {
        private final BundlePool bundlePool;

        private final IArtifactKey key;

        private final File file;

        private final String relativePath;

        private boolean damaged;

        public Artifact(BundlePool bundlePool, IArtifactKey key, File file)
        {
          this.bundlePool = bundlePool;
          this.key = key;
          this.file = file;

          int start = bundlePool.location.getAbsolutePath().length();
          relativePath = file.getAbsolutePath().substring(start + 1);
        }

        public boolean deleteIfUnused(IProgressMonitor monitor)
        {
          synchronized (bundlePool.unusedArtifacts)
          {
            if (bundlePool.unusedArtifacts.remove(this))
            {
              IFileArtifactRepository p2BundlePool = bundlePool.getP2BundlePool(monitor);
              p2BundlePool.removeDescriptor(key, monitor);
              return true;
            }
          }

          return false;
        }

        public boolean isUnused()
        {
          synchronized (bundlePool.unusedArtifacts)
          {
            return bundlePool.unusedArtifacts.contains(this);
          }
        }

        public boolean isDamaged()
        {
          return damaged;
        }

        public void setDamaged(boolean damaged)
        {
          this.damaged = damaged;

          synchronized (bundlePool.damagedArchives)
          {
            if (damaged)
            {
              bundlePool.damagedArchives.add(this);
            }
            else
            {
              bundlePool.damagedArchives.remove(this);
            }
          }

          bundlePool.agent.listener.damageStateChanged(this);
        }

        public Agent getAgent()
        {
          return bundlePool.getAgent();
        }

        public BundlePool getBundlePool()
        {
          return bundlePool;
        }

        public IArtifactKey getKey()
        {
          return key;
        }

        public File getFile()
        {
          return file;
        }

        public boolean isFolder()
        {
          return file.isDirectory();
        }

        public String getRelativePath()
        {
          return relativePath;
        }

        public int compareTo(Artifact o)
        {
          return relativePath.compareTo(o.relativePath);
        }

        @Override
        public int hashCode()
        {
          final int prime = 31;
          int result = 1;
          result = prime * result + (key == null ? 0 : key.hashCode());
          return result;
        }

        @Override
        public boolean equals(Object obj)
        {
          if (this == obj)
          {
            return true;
          }

          if (obj == null)
          {
            return false;
          }

          if (getClass() != obj.getClass())
          {
            return false;
          }

          Artifact other = (Artifact)obj;
          if (key == null)
          {
            if (other.key != null)
            {
              return false;
            }
          }
          else if (!key.equals(other.key))
          {
            return false;
          }

          return true;
        }

        @Override
        public String toString()
        {
          return key.toString();
        }
      }
    }
  }
}
