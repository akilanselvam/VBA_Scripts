import java.io.*;
import java.util.jar.*;
import java.util.Enumeration;

public class JarContentsLister {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: JarContentsLister <sourceDirectory> <destinationDirectory>");
            return;
        }

        String sourceDirectoryPath = args[0];
        String destinationDirectoryPath = args[1];

        File sourceDirectory = new File(sourceDirectoryPath);
        File destinationDirectory = new File(destinationDirectoryPath);

        if (!sourceDirectory.exists() || !sourceDirectory.isDirectory()) {
            System.out.println("Source directory does not exist or is not a directory.");
            return;
        }

        if (!destinationDirectory.exists() || !destinationDirectory.isDirectory()) {
            System.out.println("Destination directory does not exist or is not a directory.");
            return;
        }

        File[] jarFiles = sourceDirectory.listFiles((dir, name) -> name.endsWith(".jar"));

        if (jarFiles == null || jarFiles.length == 0) {
            System.out.println("No JAR files found in the source directory.");
            return;
        }

        for (File jarFile : jarFiles) {
            try {
                JarFile jar = new JarFile(jarFile);
                Enumeration<JarEntry> entries = jar.entries();

                File outputFile = new File(destinationDirectory, jarFile.getName() + "_contents.txt");
                PrintWriter writer = new PrintWriter(outputFile);

                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    writer.println(entry.getName());
                }

                writer.close();
                jar.close();

                System.out.println("Contents of " + jarFile.getName() + " listed in " + outputFile.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
