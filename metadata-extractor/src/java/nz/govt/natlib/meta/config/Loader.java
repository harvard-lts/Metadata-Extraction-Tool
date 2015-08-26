/*
 *  Copyright 2006 The National Library of New Zealand
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

/*
 * Created on 21/05/2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package nz.govt.natlib.meta.config;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * @author aparker
 * 
 * To use this classloader use
 * -Djava.system.class.loader=nz.govt.natlib.meta.config.Loader on the command
 * line at initialisation time
 */
public class Loader extends ClassLoader {

	private static ArrayList jarList;

	private static Loader instance;

	public Class findClass(String className) throws ClassNotFoundException {
		String path = className.replace('.', '/') + ".class";
		byte[] classBytes = null;
		for (int i = 0; i < jarList.size(); i++) {
			classBytes = ((JarResources) jarList.get(i)).getResource(path);
			if (classBytes != null) {
				break;
			}
		}
		if (classBytes != null) {
			return defineClass(className, classBytes, 0, classBytes.length);
		}
		throw new ClassNotFoundException(className);
	}

	public static Class getClass(String name, byte[] b, int off, int len) {
		return instance.returnClass(name, b, off, len);
	}

	public Class returnClass(String name, byte[] b, int off, int len) {
		return defineClass(name, b, off, len);
	}

	public Loader(ClassLoader parent) {
		super(parent);
		instance = this;
	}

	public Loader() {
	}

	public InputStream getResourceAsStream(String name) {
		byte[] res = null;
		if (jarList != null) {
			for (int i = 0; i < jarList.size(); i++) {
				res = ((JarResources) jarList.get(i)).getResource(name);
				if (res != null) {
					return new ByteArrayInputStream(res);
				}
			}
		}
		return super.getResourceAsStream(name);
	}

	public static void loadJar(String jar) {
		if (jar.endsWith("jar") || jar.endsWith("JAR")) {
			JarResources res = new JarResources(jar);
			jarList.add(res);
		}
	}

	public static void setJarDir(String dir) {
		jarList = new ArrayList();
		File jarDir = new File(dir);
		if (jarDir.exists() && jarDir.isDirectory()) {
			String[] jars = jarDir.list();
			for (int i = 0; i < jars.length; i++) {
				loadJar(jarDir.getAbsolutePath()
						+ System.getProperty("file.separator") + jars[i]);
			}
		} else {
			// System.out.println("Jar directory not
			// found"+jarDir.getAbsolutePath());
		}
	}
}
