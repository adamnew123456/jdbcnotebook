package org.adamnew123456.JDBCNotebook;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.googlecode.jsonrpc4j.ErrorResolver;

/**
 * Formats errors in a standard way, instead of the default method used by the
 * JSON-RPC library.
 */
public class StandardErrorResolver implements ErrorResolver {
	@Override
	public JsonError resolveError(Throwable exception, Method method, List<JsonNode> args) {
		HashMap<String, String> errorData = new HashMap<>();
		StringBuilder stacktraceBuffer = new StringBuilder();
		for (StackTraceElement frame : exception.getStackTrace()) {
			stacktraceBuffer.append("  at ");
			stacktraceBuffer.append(frame.getClassName());
			stacktraceBuffer.append(".");
			stacktraceBuffer.append(frame.getMethodName());
			stacktraceBuffer.append("(");
			stacktraceBuffer.append(frame.getFileName());
			stacktraceBuffer.append(":");
			stacktraceBuffer.append(frame.getLineNumber());
			stacktraceBuffer.append(")\n");
		}

		errorData.put("stacktrace", stacktraceBuffer.toString());
		Throwable realError = exception;
		if (realError.getCause() != null) {
			realError = exception.getCause();
		}

		return new JsonError(-32603, realError.getMessage(), (Object) errorData);
	}
}
