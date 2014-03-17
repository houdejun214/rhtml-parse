package com.dj.rhtml.parse.script;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;

import org.mozilla.javascript.Script;

public class ScriptReference extends SoftReference<Script> {
	String path;
	byte[] digest;

	ScriptReference(String path, byte[] digest, Script script,
			ReferenceQueue<Script> queue) {
		super(script, queue);
		this.path = path;
		this.digest = digest;
	}
}
