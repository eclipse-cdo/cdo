package org.eclipse.emf.cdo.ui.widgets;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.compare.Comparison;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.structuremergeviewer.IDiffElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;

import java.lang.reflect.InvocationTargetException;

/**
 * A custom implementation of the editor input.
 * @since 4.2
 */
public class EMFCompareEditorInput extends CompareEditorInput
{

  private final Comparison comparison;

  private final AdapterFactory adapterFactory;

  /**
   * Constructor.
   *
   * @param configuration
   *            the compare configuration
   * @param adapterFactory
   *            an adapter factory (can be new as simple as ComposedAdapterFactory(
  			ComposedAdapterFactory.Descriptor.Registry.INSTANCE)).
   */
  public EMFCompareEditorInput(CompareConfiguration configuration, AdapterFactory adapterFactory)
  {
    super(configuration);
    comparison = (Comparison)configuration
        .getProperty(org.eclipse.emf.compare.ide.ui.internal.EMFCompareConstants.COMPARE_RESULT);
    this.adapterFactory = adapterFactory;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.compare.CompareEditorInput#prepareInput(org.eclipse.core.runtime.IProgressMonitor)
   */
  @Override
  protected Object prepareInput(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
  {
    return adapterFactory.adapt(comparison, IDiffElement.class);
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.compare.CompareEditorInput#createDiffViewer(org.eclipse.swt.widgets.Composite)
   */
  @Override
  public Viewer createDiffViewer(Composite parent)
  {
    return new org.eclipse.emf.compare.ide.ui.internal.structuremergeviewer.EMFCompareStructureMergeViewer(parent,
        getCompareConfiguration());
  }
}
