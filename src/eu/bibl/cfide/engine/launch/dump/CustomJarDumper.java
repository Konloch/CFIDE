package eu.bibl.cfide.engine.launch.dump;

import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import eu.bibl.banalysis.storage.classes.ClassContainer;
import eu.bibl.bio.jfile.out.NonMetaJarDumper;

public class CustomJarDumper extends NonMetaJarDumper {
	
	protected ClassLoader cl;
	
	public CustomJarDumper(ClassContainer contents, ClassLoader cl) {
		super(contents);
		this.cl = cl;
	}
	
	@Override
	public int dumpClass(JarOutputStream out, String name, ClassNode cn) throws IOException {
		JarEntry entry = new JarEntry(cn.name + ".class");
		out.putNextEntry(entry);
		ClassWriter writer = new CustomClassWriter(cl, ClassWriter.COMPUTE_MAXS);
		cn.accept(writer);
		out.write(writer.toByteArray());
		return 1;
	}
}