import java.io.*;
import java.util.jar.*;
import java.util.Enumeration;

public class ExtractJarContentsToTxt {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java ExtractJarContentsToTxt <source_path> <destination_path>");
            System.exit(1);
        }

        String sourcePath = args[0];
        String destinationPath = args[1];

        File sourceDir = new File(sourcePath);
        File destinationDir = new File(destinationPath);

        if (!sourceDir.isDirectory() || !destinationDir.isDirectory()) {
            System.err.println("Both source and destination paths should be directories.");
            System.exit(1);
        }

        File[] jarFiles = sourceDir.listFiles((dir, name) -> name.endsWith(".jar"));

        if (jarFiles == null || jarFiles.length == 0) {
            System.err.println("No JAR files found in the source directory.");
            System.exit(1);
        }

        try {
            for (File jarFile : jarFiles) {
                extractJarContents(jarFile, destinationDir);
            }

            System.out.println("Extraction complete.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void extractJarContents(File jarFile, File destinationDir) throws IOException {
        String jarFileName = jarFile.getName();
        String txtFileName = jarFileName.substring(0, jarFileName.lastIndexOf('.')) + ".txt";
        File txtFile = new File(destinationDir, txtFileName);

        try (JarFile jar = new JarFile(jarFile);
             BufferedWriter writer = new BufferedWriter(new FileWriter(txtFile))) {

            Enumeration<JarEntry> entries = jar.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (!entry.isDirectory()) {
                    try (InputStream is = jar.getInputStream(entry)) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            writer.write(line);
                            writer.newLine();
                        }
                    }
                }
            }
        }
    }
}
