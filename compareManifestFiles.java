private static boolean compareManifestFiles(String filePath1, String filePath2) {
    try {
        BufferedReader reader1 = new BufferedReader(new FileReader(filePath1));
        BufferedReader reader2 = new BufferedReader(new FileReader(filePath2));
        String line1;
        String line2;

        while ((line1 = reader1.readLine()) != null && (line2 = reader2.readLine()) != null) {
            if (!line1.equals(line2)) {
                // Manifest files are different
                reader1.close();
                reader2.close();
                return false;
            }
        }

        // Make sure both files reached the end
        if (line1 != null || line2 != null) {
            reader1.close();
            reader2.close();
            return false;
        }

        reader1.close();
        reader2.close();
        return true; // Manifest files are identical
    } catch (IOException e) {
        e.printStackTrace();
        return false;
    }
}


while ((line = reader.readLine()) != null) {
    String key = getFilewithPathSource(line);
    System.out.println("Parent: " + line);
    System.out.println("Parts: " + key);
    List<String> movedToRepos = destinationReposFileMap.getOrDefault(key, new ArrayList<>());

    // Check if the file name is "MANIFEST.MF"
    if (key.endsWith("/MANIFEST.MF") && movedToRepos.size() > 0) {
        String manifestPath = movedToRepos.get(0); // Assuming it's the first destination
        boolean areManifestsEqual = compareManifestFiles(line, manifestPath);
        if (areManifestsEqual) {
            writeExcel(rowIndex++, line, String.join(".", movedToRepos), sheet, key);
        }
    } else {
        writeExcel(rowIndex++, line, String.join(".", movedToRepos), sheet, key);
    }
}

