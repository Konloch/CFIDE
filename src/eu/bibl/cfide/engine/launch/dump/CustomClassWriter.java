package eu.bibl.cfide.engine.launch.dump;

import org.objectweb.asm.ClassWriter;

public class CustomClassWriter extends ClassWriter {
	
	protected ClassLoader cl;
	
	public CustomClassWriter(ClassLoader cl, int flags) {
		super(flags);
		this.cl = cl;
	}
	
	@Override
	protected String getCommonSuperClass(String paramString1, String paramString2) {
		Class<?> localClass1;
		Class<?> localClass2;
		try {
			localClass1 = cl.loadClass(paramString1.replace('/', '.'));
			localClass2 = cl.loadClass(paramString2.replace('/', '.'));
		} catch (Exception localException) {
			throw new RuntimeException(localException.toString());
		}
		if (localClass1.isAssignableFrom(localClass2)) {
			return paramString1;
		}
		if (localClass2.isAssignableFrom(localClass1)) {
			return paramString2;
		}
		if ((localClass1.isInterface()) || (localClass2.isInterface())) {
			return "java/lang/Object";
		}
		do {
			localClass1 = localClass1.getSuperclass();
		} while (!localClass1.isAssignableFrom(localClass2));
		return localClass1.getName().replace('.', '/');
	}
}
