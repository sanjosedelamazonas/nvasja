package com.google.gwt.dev.shell;

import com.google.gwt.core.ext.TreeLogger;

import java.util.concurrent.FutureTask;

/**
 * Dummy class to fix compilation errors caused by references to the original
 * class that was removed in GWT 2.9.0. Does not, in fact, check for GWT version
 * updates.
 */
public final class CheckForUpdates {
    // NOP


    public static FutureTask checkForUpdatesInBackgroundThread(TreeLogger tl, long l) {
        return null;
    }
}

