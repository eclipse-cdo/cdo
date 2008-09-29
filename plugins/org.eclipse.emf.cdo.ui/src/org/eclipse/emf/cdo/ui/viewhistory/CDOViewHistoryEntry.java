package org.eclipse.emf.cdo.ui.viewhistory;

import org.eclipse.emf.cdo.CDOView;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public final class CDOViewHistoryEntry implements Comparable<CDOViewHistoryEntry>
{
  private CDOView view;

  private String resourcePath;

  public CDOViewHistoryEntry(CDOView view, String resourcePath)
  {
    if (view == null)
    {
      throw new IllegalArgumentException("view == null");
    }

    if (resourcePath == null)
    {
      throw new IllegalArgumentException("resourcePath == null");
    }

    this.view = view;
    this.resourcePath = resourcePath;
  }

  public CDOView getView()
  {
    return view;
  }

  public String getResourcePath()
  {
    return resourcePath;
  }

  public int compareTo(CDOViewHistoryEntry entry)
  {
    return resourcePath.compareTo(entry.resourcePath);
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj == this)
    {
      return true;
    }

    if (obj instanceof CDOViewHistoryEntry)
    {
      CDOViewHistoryEntry that = (CDOViewHistoryEntry)obj;
      return view == that.getView() && resourcePath.equals(that.resourcePath);
    }

    return false;
  }

  @Override
  public int hashCode()
  {
    return resourcePath.hashCode();
  }

  @Override
  public String toString()
  {
    return resourcePath;
  }
}
