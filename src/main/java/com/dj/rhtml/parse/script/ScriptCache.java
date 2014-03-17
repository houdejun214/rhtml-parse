package com.dj.rhtml.parse.script;

import java.lang.ref.ReferenceQueue;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.mozilla.javascript.Script;

public class ScriptCache extends LinkedHashMap<String, ScriptReference> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5621877048986886016L;
	ReferenceQueue<Script> queue;
	int capacity;

	ScriptCache(int capacity) {
		super(capacity + 1, 2f, true);
		this.capacity = capacity;
		queue = new ReferenceQueue<Script>();
	}

	@Override
	protected boolean removeEldestEntry(
			Map.Entry<String, ScriptReference> eldest) {
		return size() > capacity;
	}

	ScriptReference get(String path, byte[] digest) {
		ScriptReference ref;
		while ((ref = (ScriptReference) queue.poll()) != null) {
			remove(ref.path);
		}
		ref = get(path);
		if (ref != null && !Arrays.equals(digest, ref.digest)) {
			remove(ref.path);
			ref = null;
		}
		return ref;
	}

	void put(String path, byte[] digest, Script script) {
		put(path, new ScriptReference(path, digest, script, queue));
	}

}