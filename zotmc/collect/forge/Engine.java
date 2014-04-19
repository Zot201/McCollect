package zotmc.collect.forge;

import static com.google.common.base.CaseFormat.LOWER_UNDERSCORE;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

enum Engine {
	NASHORN,
	RHINO;
	
	private ScriptEngineManager manager;
	
	
	public static Engine prepareEngine() {
		ScriptEngineManager manager = new ScriptEngineManager();
		
		for (Engine type : values())
			if (manager.getEngineByName(type.toString()) != null) {
				type.manager = manager;
				return type;
			}
		
		if (System.getProperty("java.version").startsWith("1.8."))
			try {
				manager = new ScriptEngineManager(new URLClassLoader(new URL[] {
						Paths.get(System.getProperty("java.home"),
								"lib", "ext", "nashorn.jar").toUri().toURL()
				}, Thread.currentThread().getContextClassLoader()));
				
				
				if (manager.getEngineByName(NASHORN.toString()) != null) {
					NASHORN.manager = manager;
					return NASHORN;
				}
				
			} catch (MalformedURLException ignored) { }
		
		
		return null;
	}
	
	
	public void reset() {
		manager = null;
	}
	
	public ScriptEngine getScriptEngine() throws ScriptException {
		ScriptEngine se = manager.getEngineByName(toString());
		
		se.eval("scriptEngine = '" + toString() + "'");
		
		switch (this) {
		case NASHORN:
			se.eval("load('nashorn:mozilla_compat.js');");
			
			applyImports(se);
			
			se.eval(  "	function o() {								\n"
					+ "	  return Java.to(							\n"
					+ "       arguments,							\n"
					+ "       'java.lang.Object[]');				\n"
					+ "	}											\n"
					+ "	function i() {								\n"
					+ "	  return Java.to(							\n"
					+ "       arguments,							\n"
					+ "       'net.minecraft.item.Item[]');			\n"
					+ "	}											\n"
					+ "	function b() {								\n"
					+ "	  return Java.to(							\n"
					+ "       arguments,							\n"
					+ "       'net.minecraft.block.Block[]');		\n"
					+ "	}											\n"
					+ "	function s() {								\n"
					+ "	  return Java.to(							\n"
					+ "       arguments,							\n"
					+ "       'java.lang.String[]');				\n"
					+ "	}											\n"
					+ "	function is() {								\n"
					+ "	  return Java.to(							\n"
					+ "       arguments,							\n"
					+ "       'net.minecraft.item.ItemStack[]');	\n"
					+ "	}											\n");
			break;
			
		case RHINO:
			applyImports(se);
			
			se.eval(  "	function o() {									\n"
					+ "	  return McCollect.adaptArgs(					\n"
					+ "	      java.lang.Object,							\n"
					+ "	      Array.prototype.slice.call(arguments));	\n"
					+ "	}												\n"
					+ "	function i() {									\n"
					+ "	  return McCollect.adaptArgs(					\n"
					+ "	      Packages.net.minecraft.item.Item,			\n"
					+ "	      Array.prototype.slice.call(arguments));	\n"
					+ "	}												\n"
					+ "	function b() {									\n"
					+ "	  return McCollect.adaptArgs(					\n"
					+ "	      Packages.net.minecraft.block.Block,		\n"
					+ "	      Array.prototype.slice.call(arguments));	\n"
					+ "	}												\n"
					+ "	function s() {									\n"
					+ "	  return McCollect.adaptArgs(					\n"
					+ "	      java.lang.String,							\n"
					+ "	      Array.prototype.slice.call(arguments));	\n"
					+ "	}												\n"
					+ "	function is() {									\n"
					+ "	  return McCollect.adaptArgs(					\n"
					+ "	      Packages.net.minecraft.item.ItemStack,	\n"
					+ "	      Array.prototype.slice.call(arguments));	\n"
					+ "	}												\n");
			break;
			
		}
		
		return se;
	}
	
	public void applyImports(ScriptEngine se) throws ScriptException {
		se.eval(  "	importPackage(Packages.zotmc.collect.recipe);			\n"
				+ "	importClass(Packages.zotmc.collect.forge.McCollect);	\n"
				+ "															\n"
				+ "	W				= RecipeElement.W;						\n"
				+ "	inMod			= Content.inMod;						\n"
				+ "	equalTo			= RecipeElement.equalTo;				\n"
				+ "	inProportion	= RecipeElement.inProportion;			\n"
				+ "	inShape			= RecipeElement.inShape;				\n"
				+ "	vague			= RecipeElement.vague;					\n"
				+ " STRICT_MATCHING	= BasicRecipeView.STRICT_MATCHING;		\n"
				+ " LOOSE_MATCHING	= BasicRecipeView.LOOSE_MATCHING;		\n");
	}
	
	@Override public String toString() {
		return UPPER_UNDERSCORE.to(LOWER_UNDERSCORE, name());
	}

}
