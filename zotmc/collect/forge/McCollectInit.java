/*
 * Copyright (c) 2014, Zothf, All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */

package zotmc.collect.forge;

import static java.util.Locale.ENGLISH;
import static org.apache.logging.log4j.Level.ERROR;
import static zotmc.collect.forge.McCollectInit.MODID;
import static zotmc.collect.forge.McCollectInit.NAME;

import java.io.File;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.script.ScriptException;

import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.oredict.RecipeSorter;
import zotmc.collect.delegate.Enumerable;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.google.common.io.CharStreams;
import com.google.common.io.Closer;
import com.google.common.io.Files;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = MODID, name = NAME, version = "0.1.0.1-1.7.2", dependencies = "before:*;")
public class McCollectInit {
	public static final String MODID = "mccollect", NAME = "McCollect";
	
	@Instance(MODID) public static McCollectInit instance;
	
	
	private Map<String, String> scripts = Maps.newLinkedHashMap();
	
	public boolean isScriptsLoaded() {
		return scripts == null;
	}
	
	@EventHandler public void preInit(FMLPreInitializationEvent event) {
		try {
			File dir = new File(event.getModConfigurationDirectory(), MODID);
			
			if (dir.exists() && !dir.isDirectory())
				dir.delete();
			dir.mkdirs();
			
			
			Joiner slash = Joiner.on('/');
			
			for (File f : dir.listFiles())
				try {
					String fn = f.getName();

					
					if (fn.toLowerCase(ENGLISH).endsWith(".js"))
						scripts.put(fn, Files.toString(f, Charsets.UTF_8));
					
					else if (fn.toLowerCase(ENGLISH).endsWith(".zip")) {
						Closer closer = Closer.create();
						
						try {
							ZipFile zf = new ZipFile(f);
							closer.register(zf);
							
							for (ZipEntry ze : Enumerable.forZipFile(zf))
								if (ze.getName().toLowerCase(ENGLISH).endsWith(".js")) {
									String zen = slash.join(fn, ze.getName());
									
									try {
										scripts.put(zen, CharStreams.toString(
												new InputStreamReader(zf.getInputStream(ze), Charsets.UTF_8)));
										
									} catch (Exception e) {
										FMLLog.log(ERROR, e,
												"[%s] An error occurred while reading a file: %s",
												NAME, zen);
									}
								}
							
						} catch (Exception e) {
							throw e;
						} finally {
							closer.close();
						}
					}
					
				} catch (Exception e) {
					FMLLog.log(ERROR, e,
							"[%s] An error occurred while reading a file: %s",
							NAME, f.getName());
				}
			
		} catch (Exception e) {
			FMLLog.log(ERROR, e,
					"[%s] An error occurred while trying to access the setting files!",
					NAME);
		}
		
	}
	
	@EventHandler public void onLoadComplete(FMLLoadCompleteEvent event) {
		Engine engine = Engine.prepareEngine();
		
		if (engine == null && scripts.size() > 0)
			throw new RuntimeException(
					"Neither one of the required JavaScript engines Rhino or Nashorn presents.");
		
		
		for (Entry<String, String> entry : scripts.entrySet())
			try {
				engine.getScriptEngine().eval(entry.getValue());
				
			} catch (ScriptException e) {
				FMLLog.severe("[%s] Catched an exception during the the execution of %s",
						NAME, entry.getKey());
				e.printStackTrace();
				
				FMLCommonHandler.instance().raiseException(e, "Error in scripts", true);
			}
		
		engine.reset();
		scripts = null;
		
		
		if (ForgeModContainer.shouldSortRecipies)
			RecipeSorter.sortCraftManager();
		
	}

}
