package org.eclipse.emf.cdo.doc.programmers.client;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.session.CDOSession;

/**
 * Branching and Versioning
 * <p>
 * This chapter describes how to work with branches and versions in CDO client applications. Branching and versioning
 * are essential for supporting parallel development, tracking changes, and maintaining historical data in model
 * repositories.
 * <p>
 * CDO provides powerful APIs for creating, navigating, and merging branches, as well as accessing and comparing
 * historical versions of model objects. Understanding these features enables advanced collaboration and data management
 * scenarios.
 */
public class Doc08_BranchingAndVersioning
{
  /**
   * Working with Branches
   * <p>
   * Branches allow multiple lines of development within a CDO repository. This section explains how to create new
   * branches, switch between branches, and manage branch hierarchies. Learn best practices for using branches to
   * support feature development, bug fixes, and release management.
   */
  public class WorkingWithBranches
  {
    /**
     * Example: Create a new branch in the repository
     * @snip
     * Creates a child branch from the main branch and prints its name.
     * @param session the CDOSession
     * @param branchName the name of the new branch
     */
    public void createBranch(CDOSession session, String branchName)
    {
      CDOBranch mainBranch = session.getBranchManager().getMainBranch();
      CDOBranch newBranch = mainBranch.createBranch(branchName);
      System.out.println("Created branch: " + newBranch.getName());
    }
  }

  /**
   * Accessing Historical Versions
   * <p>
   * CDO tracks all changes to model objects, enabling access to historical versions. This section covers how to query
   * and retrieve previous states of objects, analyze change history, and restore data from earlier revisions.
   */
  public class AccessingHistoricalVersions
  {
  }

  /**
   * Comparing Versions
   * <p>
   * Comparing versions is crucial for understanding changes, resolving conflicts, and merging branches. Learn how to
   * use CDO's APIs to compare object states across branches and revisions, and visualize differences in model data.
   */
  public class ComparingVersions
  {
  }

  /**
   * Merging Changes
   * <p>
   * Merging changes from different branches or versions is a common task in collaborative development. This section
   * explains how to merge model data, resolve conflicts, and ensure data integrity during merges.
   */
  public class MergingChanges
  {
  }
}
