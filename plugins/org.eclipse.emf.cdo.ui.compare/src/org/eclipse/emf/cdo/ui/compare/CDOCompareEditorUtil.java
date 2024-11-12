/*
 * Copyright (c) 2012, 2013, 2015, 2016, 2019-2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ui.compare;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.compare.CDOCompareUtil;
import org.eclipse.emf.cdo.compare.CDOComparisonScope;
import org.eclipse.emf.cdo.session.CDORepositoryInfo;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.spi.common.revision.CDOIDMapper;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.transaction.CDOCommitContext;
import org.eclipse.emf.cdo.transaction.CDODefaultTransactionHandler2;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.transaction.CDOTransactionOpener;
import org.eclipse.emf.cdo.ui.CDOItemProvider;
import org.eclipse.emf.cdo.ui.internal.compare.bundle.OM;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.util.ConcurrentAccessException;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewOpener;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.lifecycle.IDeactivateable;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.properties.IPropertiesContainer;
import org.eclipse.net4j.util.registry.HashMapRegistry;
import org.eclipse.net4j.util.registry.IRegistry;
import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.emf.common.CommonPlugin;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.Diff;
import org.eclipse.emf.compare.Match;
import org.eclipse.emf.compare.domain.ICompareEditingDomain;
import org.eclipse.emf.compare.domain.impl.EMFCompareEditingDomain;
import org.eclipse.emf.compare.ide.ui.internal.structuremergeviewer.EMFCompareStructureMergeViewer;
import org.eclipse.emf.compare.scope.IComparisonScope;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.edit.EMFEditPlugin;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.tree.TreeNode;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;
import org.eclipse.emf.spi.cdo.InternalCDOView;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareUI;
import org.eclipse.compare.CompareViewerPane;
import org.eclipse.compare.CompareViewerSwitchingPane;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Static methods to open an EMF Compare dialog.
 *
 * @author Eike Stepper
 * @since 4.2
 */
public final class CDOCompareEditorUtil
{
  /**
   * @since 4.6
   * @deprecated As of 4.7 use {@link #compareImage()}.
   */
  @Deprecated
  public static final Image COMPARE_IMAGE = null;

  /**
   * @since 4.4
   */
  public static final String PROP_COMPARISON_LABEL = "comparison.label";

  /**
   * @since 4.4
   */
  public static final String PROP_COMPARISON_IMAGE = "comparison.image";

  private static final ThreadLocal<String> COMPARISON_TITLE = new ThreadLocal<>();

  private static final ThreadLocal<Boolean> ACTIVATE_EDITOR = new ThreadLocal<>();

  private static final ThreadLocal<Consumer<Input>> INPUT_CONSUMER = new ThreadLocal<>();

  private static final ThreadLocal<Boolean> SUPPRESS_COMMIT = new ThreadLocal<>();

  private static final ThreadLocal<List<Runnable>> DISPOSE_RUNNABLES = new ThreadLocal<>();

  private static Image compareImage;

  private CDOCompareEditorUtil()
  {
  }

  /**
   * @since 4.7
   */
  public static Image compareImage()
  {
    if (compareImage == null)
    {
      compareImage = OM.getImage("icons/compare.gif");
    }

    return compareImage;
  }

  /**
   * @since 4.4
   */
  public static void closeTransactionAfterCommit(CDOTransaction transaction)
  {
    transaction.addTransactionHandler(new CDODefaultTransactionHandler2()
    {
      @Override
      public void rolledBackTransaction(CDOTransaction transaction)
      {
        closeTransaction(transaction);
      }

      @Override
      public void committedTransaction(CDOTransaction transaction, CDOCommitContext commitContext)
      {
        closeTransaction(transaction);
      }

      private void closeTransaction(CDOTransaction transaction)
      {
        UIUtil.getDisplay().asyncExec(new Runnable()
        {
          @Override
          public void run()
          {
            transaction.close();
          }
        });
      }
    });
  }

  /**
   * @since 4.4
   */
  public static void closeEditorWithTransaction(CDOTransaction transaction)
  {
    IEditorPart[] result = { null };

    IWorkbenchPage page = UIUtil.getActiveWorkbenchPage();
    IPartListener listener = new IPartListener()
    {
      @Override
      @SuppressWarnings("restriction")
      public void partOpened(IWorkbenchPart part)
      {
        if (part instanceof org.eclipse.compare.internal.CompareEditor)
        {
          result[0] = (IEditorPart)part;
        }
      }

      @Override
      public void partDeactivated(IWorkbenchPart part)
      {
        // Do nothing.
      }

      @Override
      public void partClosed(IWorkbenchPart part)
      {
        if (part == result[0])
        {
          transaction.close();
          page.removePartListener(this);
        }
      }

      @Override
      public void partBroughtToTop(IWorkbenchPart part)
      {
        // Do nothing.
      }

      @Override
      public void partActivated(IWorkbenchPart part)
      {
        // Do nothing.
      }
    };

    page.addPartListener(listener);

    transaction.addListener(new LifecycleEventAdapter()
    {
      @Override
      protected void onDeactivated(ILifecycle lifecycle)
      {
        if (result[0] != null)
        {
          UIUtil.getDisplay().asyncExec(new Runnable()
          {
            @Override
            public void run()
            {
              page.closeEditor(result[0], false);
            }
          });
        }
      }
    });
  }

  /**
   * @since 4.3
   */
  public static boolean openEditor(CDOViewOpener viewOpener, CDOBranchPoint leftPoint, CDOBranchPoint rightPoint, CDOView[] originView, boolean activate)
  {
    return openEditor(viewOpener, null, leftPoint, rightPoint, originView, activate);
  }

  /**
   * @since 4.3
   */
  public static boolean openEditor(CDOViewOpener viewOpener, CDOTransactionOpener transactionOpener, CDOBranchPoint leftPoint, CDOBranchPoint rightPoint,
      CDOView[] originView, boolean activate)
  {
    ACTIVATE_EDITOR.set(activate);

    try
    {
      return openDialog(viewOpener, transactionOpener, leftPoint, rightPoint, originView);
    }
    finally
    {
      ACTIVATE_EDITOR.remove();
    }
  }

  /**
   * @since 4.3
   */
  public static boolean openEditor(CDOCommitInfo leftCommitInfo, CDOBranchPoint rightPoint, boolean activate)
  {
    ACTIVATE_EDITOR.set(activate);

    try
    {
      return openDialog(leftCommitInfo, rightPoint);
    }
    finally
    {
      ACTIVATE_EDITOR.remove();
    }
  }

  /**
   * @since 4.3
   */
  public static boolean openEditor(CDOCommitInfo commitInfo, boolean activate)
  {
    ACTIVATE_EDITOR.set(activate);

    try
    {
      return openDialog(commitInfo);
    }
    finally
    {
      ACTIVATE_EDITOR.remove();
    }
  }

  /**
   * @since 4.3
   */
  public static boolean openEditor(CDOView leftView, CDOView rightView, CDOView[] originView, boolean activate)
  {
    ACTIVATE_EDITOR.set(activate);

    try
    {
      return openDialog(leftView, rightView, originView);
    }
    finally
    {
      ACTIVATE_EDITOR.remove();
    }
  }

  /**
   * @since 4.4
   */
  public static boolean openEditor(CDOView leftView, CDOView rightView, Set<CDOID> affectedIDs, boolean activate)
  {
    ACTIVATE_EDITOR.set(activate);

    try
    {
      return openDialog(leftView, rightView, affectedIDs);
    }
    finally
    {
      ACTIVATE_EDITOR.remove();
    }
  }

  /**
   * @since 4.3
   */
  public static boolean openDialog(CDOSession session, CDOBranchPoint leftPoint, CDOBranchPoint rightPoint)
  {
    return openDialog(session, leftPoint, rightPoint, null);
  }

  /**
   * @since 4.3
   */
  public static boolean openDialog(CDOViewOpener viewOpener, CDOBranchPoint leftPoint, CDOBranchPoint rightPoint, CDOView[] originView)
  {
    return openDialog(viewOpener, null, leftPoint, rightPoint, originView);
  }

  /**
   * @since 4.3
   */
  public static boolean openDialog(CDOViewOpener viewOpener, CDOTransactionOpener transactionOpener, CDOBranchPoint leftPoint, CDOBranchPoint rightPoint,
      CDOView[] originView)
  {
    Boolean activateEditor = ACTIVATE_EDITOR.get();
    CDOView[] leftAndRightView = { null, null };

    addDisposeRunnables(new Runnable()
    {
      @Override
      public void run()
      {
        LifecycleUtil.deactivate(leftAndRightView[0]);
        LifecycleUtil.deactivate(leftAndRightView[1]);
      }
    });

    try
    {
      ResourceSet leftResourceSet = new ResourceSetImpl();
      leftAndRightView[0] = viewOpener.openView(leftPoint, leftResourceSet);

      ResourceSet rightResourceSet = new ResourceSetImpl();
      if (transactionOpener != null)
      {
        rightPoint = rightPoint.getBranch().getHead();
        leftAndRightView[1] = transactionOpener.openTransaction(rightPoint, rightResourceSet);
      }
      else
      {
        leftAndRightView[1] = viewOpener.openView(rightPoint, rightResourceSet);
      }

      return openDialog(leftAndRightView[0], leftAndRightView[1], originView, viewOpener);
    }
    finally
    {
      if (activateEditor == null)
      {
        List<Runnable> list = removeDisposeRunnables();
        runDisposeRunnables(list);
      }
    }
  }

  public static boolean openDialog(CDOCommitInfo leftCommitInfo, CDOBranchPoint rightPoint)
  {
    CDORepositoryInfo repositoryInfo = (CDORepositoryInfo)leftCommitInfo.getCommitInfoManager().getRepository();
    CDOSession session = repositoryInfo.getSession();

    return openDialog(session, leftCommitInfo, rightPoint);
  }

  public static boolean openDialog(CDOCommitInfo commitInfo)
  {
    long previousTimeStamp = commitInfo.getPreviousTimeStamp();
    if (previousTimeStamp == CDOBranchPoint.UNSPECIFIED_DATE)
    {
      return false;
    }

    CDOBranchPoint previous = CDOBranchUtil.normalizeBranchPoint(commitInfo.getBranch(), previousTimeStamp);
    return openDialog(commitInfo, previous);
  }

  public static boolean openDialog(CDOView leftView, CDOView rightView, CDOView[] originView)
  {
    return openDialog(leftView, rightView, originView, CDOCompareUtil.DEFAULT_VIEW_OPENER);
  }

  /**
   * @since 4.3
   */
  public static boolean openDialog(CDOView leftView, CDOView rightView, CDOView[] originView, CDOViewOpener viewOpener)
  {
    Input input = createComparisonInput(leftView, rightView, originView, viewOpener);
    return openDialog(input, rightView);
  }

  /**
   * @since 4.4
   */
  public static boolean openDialog(CDOView leftView, CDOView rightView, Set<CDOID> affectedIDs)
  {
    Input input = createComparisonInput(leftView, rightView, affectedIDs);
    return openDialog(input, rightView);
  }

  /**
   * @since 4.4
   */
  public static boolean openDialog(Input input, CDOView rightView)
  {
    if (input == null)
    {
      UIUtil.getDisplay().syncExec(() -> {
        Shell shell = UIUtil.getShell();
        MessageDialog.openInformation(shell, "Compare", "There are no differences between the selected inputs.");
      });

      return false;
    }

    Boolean activateEditor = ACTIVATE_EDITOR.get();
    if (activateEditor != null)
    {
      List<Runnable> disposeRunnables = removeDisposeRunnables();
      input.setDisposeRunnables(disposeRunnables);

      UIUtil.getDisplay().asyncExec(() -> {
        IWorkbenchPage page = UIUtil.getActiveWorkbenchPage();
        page.addPartListener(new IPartListener2()
        {
          @Override
          public void partOpened(IWorkbenchPartReference partRef)
          {
            IWorkbenchPart part = partRef.getPart(false);
            if (part instanceof IEditorPart)
            {
              IEditorPart editor = (IEditorPart)part;
              IEditorInput editorInput = editor.getEditorInput();
              if (editorInput == input)
              {
                input.editorOpened(editor);
                page.removePartListener(this);
              }
            }
          }
        });

        CompareUI.openCompareEditor(input, activateEditor);
      });

      return true;
    }

    input.setModal(true);
    EList<Diff> differences = new BasicEList<>();

    UIUtil.getDisplay().syncExec(() -> {
      CompareUI.openCompareDialog(input);

      if (rightView instanceof InternalCDOTransaction)
      {
        Comparison comparison = input.getComparison();
        differences.addAll(comparison.getDifferences());
      }
    });

    if (!input.isOK())
    {
      // The Cancel button must have been pressed.
      return false;
    }

    if (differences.isEmpty())
    {
      return false;
    }

    try
    {
      input.commitChanges(new NullProgressMonitor());
      return true;
    }
    catch (Exception ex)
    {
      return false;
    }
  }

  /**
   * @since 4.3
   */
  public static Input createComparisonInput(CDOView leftView, CDOView rightView, CDOView[] originView, CDOViewOpener viewOpener)
  {
    Comparison comparison = CDOCompareUtil.compare(leftView, rightView, originView, viewOpener);
    return createComparisonInput(leftView, rightView, comparison);
  }

  /**
   * @since 4.4
   */
  public static Input createComparisonInput(CDOView leftView, CDOView rightView, Set<CDOID> affectedIDs)
  {
    CDOComparisonScope scope = new CDOComparisonScope.Minimal(leftView, rightView, null, affectedIDs);
    Comparison comparison = CDOCompareUtil.compare(scope);
    return createComparisonInput(leftView, rightView, comparison);
  }

  /**
   * @since 4.4
   */
  public static Input createComparisonInput(CDOView leftView, CDOView rightView, Comparison comparison)
  {
    if (comparison.getDifferences().isEmpty())
    {
      return null;
    }

    IComparisonScope scope = CDOCompareUtil.getScope(comparison);
    ICompareEditingDomain editingDomain = EMFCompareEditingDomain.create(scope.getLeft(), scope.getRight(), scope.getOrigin());

    ComposedAdapterFactory.Descriptor.Registry registry = EMFEditPlugin.getComposedAdapterFactoryDescriptorRegistry();
    ComposedAdapterFactory adapterFactory = new ComposedAdapterFactory(registry);

    CDOBranchPoint leftBranchPoint = CDOBranchUtil.copyBranchPoint(leftView);
    CDOBranchPoint rightBranchPoint = CDOBranchUtil.copyBranchPoint(rightView);

    CDOItemProvider itemProvider = new CDOItemProvider(null)
    {
      @Override
      public boolean useFullPath(Object object)
      {
        if (object instanceof CDOBranchPoint)
        {
          return true;
        }

        return super.useFullPath(object);
      }
    };

    IRegistry<String, Object> leftProperties = leftView.properties();
    Image leftImage = (Image)leftProperties.get(PROP_COMPARISON_IMAGE);
    if (leftImage == null)
    {
      leftImage = itemProvider.getImage(leftBranchPoint);
    }

    String leftLabel = (String)leftProperties.get(PROP_COMPARISON_LABEL);
    if (leftLabel == null)
    {
      leftLabel = itemProvider.getText(leftBranchPoint);
    }

    IRegistry<String, Object> rightProperties = rightView.properties();
    Image rightImage = (Image)rightProperties.get(PROP_COMPARISON_IMAGE);
    if (rightImage == null)
    {
      rightImage = itemProvider.getImage(rightBranchPoint);
    }

    String rightLabel = (String)rightProperties.get(PROP_COMPARISON_LABEL);
    if (rightLabel == null)
    {
      rightLabel = itemProvider.getText(rightBranchPoint);
    }

    itemProvider.dispose();

    boolean leftEditable = !leftView.isReadOnly();
    boolean rightEditable = !rightView.isReadOnly();

    String title = COMPARISON_TITLE.get();
    if (title == null)
    {
      String repositoryName = ((InternalCDOView)leftView).getRepositoryName();

      boolean merge = leftEditable || rightEditable;
      if (merge)
      {
        title = "Merge " + repositoryName + " from " + leftLabel + " into " + rightLabel;

        leftLabel = "From " + leftLabel;
        rightLabel = "Into " + rightLabel;
      }
      else
      {
        title = "Compare " + repositoryName + " between " + leftLabel + " and " + rightLabel;
      }
    }

    CompareConfiguration configuration = new CompareConfiguration();
    configuration.setLeftImage(leftImage);
    configuration.setLeftLabel(leftLabel);
    configuration.setLeftEditable(leftEditable);
    configuration.setRightImage(rightImage);
    configuration.setRightLabel(rightLabel);
    configuration.setRightEditable(rightEditable);

    Input input = new Input(leftView, rightView, configuration, comparison, editingDomain, adapterFactory, title);

    workaroundEMFCompareBug(leftView, leftLabel);
    workaroundEMFCompareBug(rightView, rightLabel);

    return input;
  }

  /**
   * See https://git.eclipse.org/r/#/c/55404/
   */
  private static void workaroundEMFCompareBug(CDOView view, String label)
  {
    try
    {
      Class<?> c = CommonPlugin.loadClass("org.eclipse.emf.compare.ide", "org.eclipse.emf.compare.ide.internal.utils.StoragePathAdapter");
      Constructor<?> constructor = c.getConstructor(String.class, boolean.class);
      Adapter adapter = (Adapter)constructor.newInstance(label, false);

      for (Resource resource : view.getResourceSet().getResources())
      {
        resource.eAdapters().add(adapter);
      }
    }
    catch (Throwable ex)
    {
      //$FALL-THROUGH$
    }
  }

  /**
   * @since 4.4
   */
  public static void runWithTitle(String title, Runnable runnable)
  {
    COMPARISON_TITLE.set(title);

    try
    {
      runnable.run();
    }
    finally
    {
      COMPARISON_TITLE.remove();
    }
  }

  /**
   * @since 4.6
   */
  public static void setInputConsumer(Consumer<Input> consumer)
  {
    if (consumer != null)
    {
      INPUT_CONSUMER.set(consumer);
    }
    else
    {
      INPUT_CONSUMER.remove();
    }
  }

  /**
   * @since 4.3
   */
  public static boolean isSuppressCommit()
  {
    return Boolean.TRUE.equals(SUPPRESS_COMMIT.get());
  }

  /**
   * @since 4.3
   */
  public static void setSuppressCommit(boolean suppressCommit)
  {
    if (suppressCommit)
    {
      SUPPRESS_COMMIT.set(true);
    }
    else
    {
      SUPPRESS_COMMIT.remove();
    }
  }

  /**
   * @since 4.3
   */
  public static void addDisposeRunnables(Runnable... disposeRunnables)
  {
    List<Runnable> list = DISPOSE_RUNNABLES.get();
    if (list == null)
    {
      list = new ArrayList<>();
      DISPOSE_RUNNABLES.set(list);
    }

    list.addAll(Arrays.asList(disposeRunnables));
  }

  private static List<Runnable> removeDisposeRunnables()
  {
    List<Runnable> list = DISPOSE_RUNNABLES.get();
    DISPOSE_RUNNABLES.remove();
    return list;
  }

  private static void runDisposeRunnables(List<Runnable> disposeRunnables)
  {
    if (disposeRunnables != null)
    {
      for (Runnable disposeRunnable : disposeRunnables)
      {
        try
        {
          disposeRunnable.run();
        }
        catch (Exception ex)
        {
          OM.LOG.error(ex);
        }
      }
    }
  }

  // private static boolean handleMerges(InternalCDOTransaction transaction, EList<Diff> differences)
  // {
  // Map<InternalCDOObject, InternalCDORevision> cleanRevisions = transaction.getCleanRevisions();
  // Map<CDOID, CDORevisionDelta> revisionDeltas = transaction.getLastSavepoint().getRevisionDeltas2();
  // InternalCDORevisionManager revisionManager = transaction.getSession().getRevisionManager();
  //
  // boolean unmergedConflicts = false;
  //
  // for (Diff diff : differences)
  // {
  // if (diff.getState() != DifferenceState.MERGED)
  // {
  // int xxxxx;
  // // Can merges with DISCARDS be committed???
  //
  // unmergedConflicts = true;
  // }
  // else
  // {
  // Match match = diff.getMatch();
  //
  // EObject left = match.getLeft();
  // EObject right = match.getRight();
  //
  // if (left != null && right != null)
  // {
  // InternalCDOObject leftObject = (InternalCDOObject)CDOUtil.getCDOObject(left);
  // InternalCDOObject rightObject = (InternalCDOObject)CDOUtil.getCDOObject(right);
  //
  // InternalCDORevision leftRevision = leftObject.cdoRevision();
  // cleanRevisions.put(rightObject, leftRevision);
  // int remoteVersion = leftRevision.getVersion();
  //
  // InternalCDORevision rightRevision = rightObject.cdoRevision();
  // if (rightRevision.getBranch() != leftRevision.getBranch() //
  // || rightRevision.getTimeStamp() != leftRevision.getTimeStamp() //
  // || rightRevision.getVersion() != leftRevision.getVersion())
  // {
  // rightRevision = rightRevision.copy();
  // rightRevision.setBranchPoint(leftRevision);
  // rightRevision.setVersion(remoteVersion);
  //
  // rightObject.cdoInternalSetRevision(rightRevision);
  // revisionManager.internRevision(rightRevision);
  // }
  //
  // InternalCDORevisionDelta revisionDelta = (InternalCDORevisionDelta)revisionDeltas.get(rightRevision.getID());
  // if (revisionDelta != null)
  // {
  // revisionDelta.setVersion(remoteVersion);
  // }
  //
  // transaction.removeConflict(rightObject);
  // rightObject.cdoInternalSetState(CDOState.DIRTY);
  // }
  // }
  // }
  //
  // return !unmergedConflicts;
  // }

  /**
   * @author Eike Stepper
   * @since 4.9
   */
  @FunctionalInterface
  public interface EditorConsumer
  {
    public void editorOpened(IEditorPart editor);
  }

  /**
   * @author Eike Stepper
   * @since 4.9
   */
  @FunctionalInterface
  public interface ContentsCreator
  {
    public Control createContents(Composite parent, Function<Composite, Control> defaultContentsCreator);
  }

  /**
   * @author Eike Stepper
   * @since 4.6
   */
  public static class InputHolder implements Consumer<Input>, EditorConsumer, ContentsCreator, IDeactivateable
  {
    private Input input;

    public InputHolder()
    {
    }

    public Input getInput()
    {
      return input;
    }

    @Override
    public void accept(Input input)
    {
      this.input = input;
      activate(input);
    }

    @Override
    public void editorOpened(IEditorPart editor)
    {
    }

    @Override
    public Control createContents(Composite parent, Function<Composite, Control> defaultContentsCreator)
    {
      return defaultContentsCreator.apply(parent);
    }

    /**
     * @since 4.9
     */
    public void activate(Input input)
    {
    }

    @Override
    public Exception deactivate()
    {
      return null;
    }
  }

  /**
   * @author Eike Stepper
   * @since 4.4
   */
  @SuppressWarnings("restriction")
  public static final class Input //
      extends org.eclipse.emf.compare.ide.ui.internal.editor.ComparisonEditorInput //
      implements IPropertiesContainer
  {
    private final IRegistry<String, Object> properties = new HashMapRegistry<>();

    private final CDOView sourceView;

    private final CDOView targetView;

    private final Comparison comparison;

    private final boolean suppressCommit;

    private final Consumer<Input> inputConsumer;

    private List<Runnable> disposeRunnables;

    private boolean modal;

    private boolean editionSelectionDialog;

    private Consumer<IProgressMonitor> saveHandler = this::defaultSave;

    private CDOCommitInfo commitInfo;

    private boolean ok;

    private IEditorPart editor;

    private CompareViewerPane structureInputPane;

    private Input(CDOView sourceView, CDOView targetView, CompareConfiguration configuration, Comparison comparison, ICompareEditingDomain editingDomain,
        AdapterFactory adapterFactory, String title)
    {
      super(createEMFCompareConfiguration(configuration, adapterFactory), comparison, editingDomain, adapterFactory);
      this.sourceView = sourceView;
      this.targetView = targetView;
      this.comparison = comparison;
      setTitle(title);

      suppressCommit = isSuppressCommit();
      SUPPRESS_COMMIT.remove();

      inputConsumer = INPUT_CONSUMER.get();
      INPUT_CONSUMER.remove();

      if (inputConsumer != null)
      {
        inputConsumer.accept(this);
      }
    }

    private static org.eclipse.emf.compare.ide.ui.internal.configuration.EMFCompareConfiguration createEMFCompareConfiguration(
        CompareConfiguration configuration, AdapterFactory adapterFactory)
    {
      org.eclipse.emf.compare.ide.ui.internal.configuration.EMFCompareConfiguration result = //
          new org.eclipse.emf.compare.ide.ui.internal.configuration.EMFCompareConfiguration(configuration);

      result.setAdapterFactory(adapterFactory);
      return result;
    }

    private void dispose()
    {
      if (inputConsumer instanceof IDeactivateable)
      {
        Throwable exception;

        try
        {
          exception = ((IDeactivateable)inputConsumer).deactivate();
        }
        catch (Throwable ex)
        {
          exception = ex;
        }

        if (exception != null)
        {
          OM.LOG.warn(exception);
        }
      }

      AdapterFactory adapterFactory = getAdapterFactory();
      if (adapterFactory instanceof ComposedAdapterFactory)
      {
        ComposedAdapterFactory composedAdapterFactory = (ComposedAdapterFactory)adapterFactory;
        composedAdapterFactory.dispose();
      }

      runDisposeRunnables(disposeRunnables);
      disposeRunnables = null;
    }

    public final Comparison getComparison()
    {
      return comparison;
    }

    @Override
    public Image getTitleImage()
    {
      return compareImage();
    }

    /**
     * @since 4.8
     */
    public boolean isModal()
    {
      return modal;
    }

    /**
     * @since 4.8
     */
    public void setModal(boolean modal)
    {
      this.modal = modal;
    }

    @Override
    public boolean isEditionSelectionDialog()
    {
      return editionSelectionDialog;
    }

    /**
     * @since 4.9
     */
    public void setEditionSelectionDialog(boolean editionSelectionDialog)
    {
      this.editionSelectionDialog = editionSelectionDialog;
    }

    @Override
    public Control createContents(Composite parent)
    {
      if (inputConsumer instanceof ContentsCreator)
      {
        ContentsCreator contentsCreator = (ContentsCreator)inputConsumer;
        return contentsCreator.createContents(parent, super::createContents);
      }

      return super.createContents(parent);
    }

    @Override
    protected CompareViewerPane createStructureInputPane(Composite parent)
    {
      structureInputPane = super.createStructureInputPane(parent);
      return structureInputPane;
    }

    /**
     * @since 4.9
     */
    public CompareViewerPane getStructureInputPane()
    {
      return structureInputPane;
    }

    /**
     * @since 4.9
     */
    public StructuredViewer getViewerWrapper()
    {
      if (structureInputPane instanceof CompareViewerSwitchingPane)
      {
        Viewer viewer = ((CompareViewerSwitchingPane)structureInputPane).getViewer();
        if (viewer instanceof StructuredViewer)
        {
          return (StructuredViewer)viewer;
        }
      }

      return null;
    }

    /**
     * @since 4.9
     */
    public TreeViewer getTreeViewer()
    {
      StructuredViewer viewerWrapper = getViewerWrapper();
      if (viewerWrapper instanceof EMFCompareStructureMergeViewer)
      {
        return ((EMFCompareStructureMergeViewer)viewerWrapper).getTreeViewer();
      }

      return null;
    }

    /**
     * @since 4.9
     */
    public boolean forEachDiffElement(Predicate<Object> consumer)
    {
      TreeViewer treeViewer = getTreeViewer();
      if (treeViewer != null)
      {
        ITreeContentProvider contentProvider = (ITreeContentProvider)treeViewer.getContentProvider();
        return forEachDiffElement(consumer, contentProvider, treeViewer.getInput());
      }

      return false;
    }

    private boolean forEachDiffElement(Predicate<Object> consumer, ITreeContentProvider contentProvider, Object obj)
    {
      if (consumer.test(obj))
      {
        return true;
      }

      Object[] children = contentProvider.getChildren(obj);
      for (Object child : children)
      {
        if (forEachDiffElement(consumer, contentProvider, child))
        {
          return true;
        }
      }

      return false;
    }

    /**
     * @since 4.9
     */
    public IEditorPart getEditor()
    {
      return editor;
    }

    private void editorOpened(IEditorPart editor)
    {
      this.editor = editor;
      if (inputConsumer instanceof EditorConsumer)
      {
        ((EditorConsumer)inputConsumer).editorOpened(editor);
      }
    }

    public void setDisposeRunnables(List<Runnable> disposeRunnables)
    {
      this.disposeRunnables = disposeRunnables;
    }

    /**
     * @since 4.9
     */
    public Consumer<IProgressMonitor> getSaveHandler()
    {
      return saveHandler;
    }

    /**
     * @since 4.9
     */
    public void setSaveHandler(Consumer<IProgressMonitor> saveHandler)
    {
      this.saveHandler = saveHandler;
    }

    @Override
    public void saveChanges(IProgressMonitor monitor) throws CoreException
    {
      if (saveHandler != null)
      {
        try
        {
          saveHandler.accept(monitor);
        }
        catch (Exception ex)
        {
          Exception unwrapped = WrappedException.unwrap(ex);
          OM.BUNDLE.coreException(unwrapped);
        }
      }
    }

    private void defaultSave(IProgressMonitor monitor)
    {
      flushViewers(monitor);

      if (!modal)
      {
        try
        {
          commitChanges(monitor);
        }
        catch (Exception ex)
        {
          throw WrappedException.wrap(ex);
        }
      }
    }

    private void commitChanges(IProgressMonitor monitor) throws ConcurrentAccessException, CommitException
    {
      if (targetView instanceof InternalCDOTransaction)
      {
        InternalCDOTransaction transaction = (InternalCDOTransaction)targetView;
        if (transaction.isDirty())
        {
          Collection<CDOObject> values = transaction.getNewObjects().values();
          if (!values.isEmpty())
          {
            Map<CDOID, CDOID> idMappings = CDOIDUtil.createMap();

            CDOObject[] rightObjects = values.toArray(new CDOObject[values.size()]);
            for (CDOObject rightObject : rightObjects)
            {
              Match match = comparison.getMatch(rightObject);
              if (match != null)
              {
                EObject left = match.getLeft();
                if (left != null)
                {
                  CDOObject leftObject = CDOUtil.getCDOObject(left);
                  if (leftObject != null)
                  {
                    CDOID id = leftObject.cdoID();
                    idMappings.put(rightObject.cdoID(), id);

                    org.eclipse.emf.internal.cdo.transaction.CDOTransactionImpl.resurrectObject(rightObject, id);
                  }
                }
              }
            }

            if (!idMappings.isEmpty())
            {
              CDOIDMapper idMapper = new CDOIDMapper(idMappings);

              for (CDOObject newObject : values)
              {
                InternalCDORevision revision = (InternalCDORevision)newObject.cdoRevision();
                revision.adjustReferences(idMapper);
              }

              for (CDOObject dirtyObject : transaction.getDirtyObjects().values())
              {
                InternalCDORevision revision = (InternalCDORevision)dirtyObject.cdoRevision();
                revision.adjustReferences(idMapper);
              }
            }
          }
        }

        if (!suppressCommit)
        {
          CDOBranchPoint mergeSource = sourceView.isHistorical() ? CDOBranchUtil.copyBranchPoint(sourceView)
              : sourceView.getBranch().getPoint(sourceView.getLastUpdateTime());

          transaction.setCommitMergeSource(mergeSource);
          commitInfo = transaction.commit(monitor);
          setDirty(false);
        }
      }
    }

    /**
     * @since 4.6
     */
    public CDOCommitInfo getCommitInfo()
    {
      return commitInfo;
    }

    /**
     * Returns <code>true</code> if the OK button was pressed, <code>false</code> otherwise.
     */
    public boolean isOK()
    {
      return ok;
    }

    @Override
    public boolean okPressed()
    {
      try
      {
        ok = true;
        return super.okPressed();
      }
      finally
      {
        dispose();
      }
    }

    @Override
    public void removePropertyChangeListener(IPropertyChangeListener listener)
    {
      try
      {
        super.removePropertyChangeListener(listener);
      }
      finally
      {
        dispose();
      }
    }

    @Override
    public IRegistry<String, Object> properties()
    {
      return properties;
    }

    /**
     * @since 4.9
     */
    public static TreeNode getTreeNode(Object diffElement)
    {
      if (diffElement instanceof Adapter)
      {
        Notifier target = ((Adapter)diffElement).getTarget();
        if (target instanceof TreeNode)
        {
          return (TreeNode)target;
        }
      }

      return null;
    }
  }

  /**
   * @author Eike Stepper
   * @since 4.4
   */
  public static final class TransactionOpenerAndEditorCloser implements CDOTransactionOpener
  {
    private final CDOTransactionOpener delegate;

    private final boolean closeTransactionAfterCommit;

    public TransactionOpenerAndEditorCloser(CDOTransactionOpener delegate, boolean closeTransactionAfterCommit)
    {
      this.delegate = delegate;
      this.closeTransactionAfterCommit = closeTransactionAfterCommit;
    }

    public boolean isCloseTransactionAfterCommit()
    {
      return closeTransactionAfterCommit;
    }

    @Override
    public CDOTransaction openTransaction(String durableLockingID, ResourceSet resourceSet)
    {
      return wrap(delegate.openTransaction(durableLockingID, resourceSet));
    }

    @Override
    public CDOTransaction openTransaction(CDOBranchPoint target, ResourceSet resourceSet)
    {
      return wrap(delegate.openTransaction(target, resourceSet));
    }

    private CDOTransaction wrap(CDOTransaction transaction)
    {
      if (closeTransactionAfterCommit)
      {
        closeTransactionAfterCommit(transaction);
      }

      CDOCompareEditorUtil.closeEditorWithTransaction(transaction);
      return transaction;
    }
  }
}
