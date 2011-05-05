/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */
package org.sikuli.ide.sikuli_test;

import javax.swing.ListModel;

import junit.framework.Test;

/**
 * The interface for accessing the Test run context. Test run views
 * should use this interface rather than accessing the TestRunner
 * directly.
 */
public interface TestRunContext {
	/**
	 * Handles the selection of a Test.
	 */
	public void handleTestSelected(Test test);
	/**
	 * Returns the failure model
	 */
	public ListModel getFailures();
}
