package com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions;

/**
 * DON'T USE THIS EXCEPTION in your plugin's code, it is only used internally by the error handler to trigger a rollback of the changes.
 */
public final class RollbackException extends RuntimeException {

}
