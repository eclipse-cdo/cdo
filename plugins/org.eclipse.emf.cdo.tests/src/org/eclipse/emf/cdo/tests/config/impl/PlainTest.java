/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.tests.config.impl;

/**
 * Base type for tests that use the config-test suite infrastructure without requiring repository, session, or model
 * configuration. The suite runs each such test once under the synthetic {@link PlainScenario} scenario.
 *
 * @author Eike Stepper
 */
public abstract class PlainTest extends ConfigTest
{
}
